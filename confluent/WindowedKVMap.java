import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Confluent onsite (2021-2024): Windowed Key-Value Map.
 * Source: FastPrep "confluent-windowed-key-value-map".
 *
 * processWindowedMap(String[] operations, int windowSeconds) -> String[]
 *   "put t key value"  insert/replace; active during [t, t + windowSeconds)
 *   "get t key"        -> value, or "NOT_FOUND" if absent/expired
 *   "delete t key"     remove if active (no output)
 *   "average t"        -> average of active values as a reduced fraction
 *                         ("15", "-3/2"), or "EMPTY" if none active
 * Before applying a command at time t, entries with expiry <= t are inactive.
 * Replacing a key removes its old value and expiry. Timestamps are nondecreasing.
 *
 * Example (window 5): put 0 a 10, put 1 b 20, average 2 -> "15",
 *                     get 5 a -> "NOT_FOUND", average 5 -> "20"
 *
 * Design:
 * - HashMap<key, Entry(value, expiry)>
 * - Min-heap of entries by expiry for eviction. Replaced / deleted entries stay in the
 *   heap as stale and are skipped (compare object identity with the map) -> lazy deletion.
 * - Running sum + count of active values -> average in O(1).
 * Each entry is pushed and popped once: O(log n) amortized per operation.
 *
 * Follow-ups:
 * - Frequent average queries: that's why we keep sum/count instead of scanning.
 * - Concurrency: every op (even get) may evict -> mutates state, so a single lock
 *   (or synchronized) around each op. A ReadWriteLock doesn't help because reads write.
 *   For higher throughput: shard by key hash with per-shard sum/count, and
 *   average = combine (sum, count) of all shards under their locks.
 * - Timestamps not monotonic: can't evict by "now"; check expiry on read instead.
 */
public class WindowedKVMap {

    static class Entry {
        final String key;
        final long value, expiry;
        Entry(String key, long value, long expiry) { this.key = key; this.value = value; this.expiry = expiry; }
    }

    private final long window;
    private final Map<String, Entry> map = new HashMap<>();
    private final PriorityQueue<Entry> heap = new PriorityQueue<>(Comparator.comparingLong(e -> e.expiry));
    private long sum = 0;
    private final ReentrantLock lock = new ReentrantLock();

    public WindowedKVMap(long windowSeconds) { this.window = windowSeconds; }

    private void evict(long now) {
        while (!heap.isEmpty() && heap.peek().expiry <= now) {
            Entry e = heap.poll();
            if (map.get(e.key) == e) { // still the live entry (not replaced / deleted)
                map.remove(e.key);
                sum -= e.value;
            }
        }
    }

    private void removeKey(String key) {
        Entry old = map.remove(key);
        if (old != null) sum -= old.value; // heap copy becomes stale
    }

    public void put(long t, String key, long value) {
        lock.lock();
        try {
            evict(t);
            removeKey(key);
            Entry e = new Entry(key, value, t + window);
            map.put(key, e);
            heap.add(e);
            sum += value;
        } finally { lock.unlock(); }
    }

    public String get(long t, String key) {
        lock.lock();
        try {
            evict(t);
            Entry e = map.get(key);
            return e == null ? "NOT_FOUND" : String.valueOf(e.value);
        } finally { lock.unlock(); }
    }

    public void delete(long t, String key) {
        lock.lock();
        try {
            evict(t);
            removeKey(key);
        } finally { lock.unlock(); }
    }

    public String average(long t) {
        lock.lock();
        try {
            evict(t);
            if (map.isEmpty()) return "EMPTY";
            long num = sum, den = map.size();
            long g = gcd(Math.abs(num), den);
            num /= g;
            den /= g;
            return den == 1 ? String.valueOf(num) : num + "/" + den;
        } finally { lock.unlock(); }
    }

    private static long gcd(long a, long b) { return b == 0 ? a : gcd(b, a % b); }

    public static String[] processWindowedMap(String[] operations, int windowSeconds) {
        WindowedKVMap m = new WindowedKVMap(windowSeconds);
        List<String> out = new ArrayList<>();
        for (String op : operations) {
            String[] p = op.trim().split("\\s+");
            long t = Long.parseLong(p[1]);
            switch (p[0]) {
                case "put": m.put(t, p[2], Long.parseLong(p[3])); break;
                case "get": out.add(m.get(t, p[2])); break;
                case "delete": m.delete(t, p[2]); break;
                case "average": out.add(m.average(t)); break;
                default: throw new IllegalArgumentException(op);
            }
        }
        return out.toArray(new String[0]);
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(processWindowedMap(new String[]{
            "put 0 a 10", "put 1 b 20", "average 2", "get 5 a", "average 5"}, 5)));
        // [15, NOT_FOUND, 20]

        System.out.println(Arrays.toString(processWindowedMap(new String[]{
            "put 0 a 10", "put 3 a 7", "get 6 a", "delete 6 a", "average 6", "get 7 a"}, 5)));
        // replace resets expiry to 8 -> [7, EMPTY, NOT_FOUND]

        System.out.println(Arrays.toString(processWindowedMap(new String[]{
            "put 0 x -5", "put 0 y 2", "average 0", "put 1 z 6", "average 1"}, 10)));
        // (-5+2)/2 = -3/2 ; (-5+2+6)/3 = 1 -> [-3/2, 1]
    }
}
