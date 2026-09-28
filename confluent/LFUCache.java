import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Confluent senior onsite (2024): LFU Cache with thread-safety follow-up (LC 460).
 * Source: FastPrep "confluent-least-frequently-used-cache".
 *
 * public int[] processLfuCache(int capacity, String[] operations)
 *   "put key value" / "get key"; returns results of all gets (-1 if absent).
 *   New key -> freq 1. A successful get, or a put that updates an existing key,
 *   increments freq and makes the key most-recently-used within its freq tier.
 *   Over capacity -> evict smallest freq; ties -> least recently used.
 *
 *   cap 2: put 1 1, put 2 2, get 1, put 3 3, get 2, get 3, put 4 4, get 1, get 3, get 4
 *          -> [1, -1, 3, -1, 3, 4]
 *
 * O(1) per op:
 *   keyToVal, keyToFreq : HashMap
 *   freqToKeys          : HashMap<freq, LinkedHashSet<key>>  (insertion order = recency)
 *   minFreq             : smallest freq present; reset to 1 on every NEW insert, and
 *                         bumped only when a touch empties the minFreq bucket.
 *
 * Thread-safety follow-up:
 * - get() mutates (freq, recency) -> it's a write; a ReadWriteLock gives no benefit.
 *   Simplest correct answer: one lock around every public method (done below).
 * - Scaling: shard into N independent LFU caches by key hash (each with capacity/N,
 *   its own lock) -> eviction becomes approximately-LFU globally; say so.
 * - Or: record accesses in a lock-free buffer and apply freq updates in batches
 *   (Caffeine's approach, W-TinyLFU) so reads rarely take the lock.
 * - Never call user code / IO while holding the lock.
 */
public class LFUCache {

    private final int capacity;
    private final Map<Integer, Integer> keyToVal = new HashMap<>();
    private final Map<Integer, Integer> keyToFreq = new HashMap<>();
    private final Map<Integer, LinkedHashSet<Integer>> freqToKeys = new HashMap<>();
    private int minFreq = 0;
    private final ReentrantLock lock = new ReentrantLock();

    public LFUCache(int capacity) { this.capacity = capacity; }

    public int get(int key) {
        lock.lock();
        try {
            if (!keyToVal.containsKey(key)) return -1;
            touch(key);
            return keyToVal.get(key);
        } finally { lock.unlock(); }
    }

    public void put(int key, int value) {
        lock.lock();
        try {
            if (capacity <= 0) return;
            if (keyToVal.containsKey(key)) {
                keyToVal.put(key, value);
                touch(key);
                return;
            }
            if (keyToVal.size() >= capacity) evict();
            keyToVal.put(key, value);
            keyToFreq.put(key, 1);
            freqToKeys.computeIfAbsent(1, f -> new LinkedHashSet<>()).add(key);
            minFreq = 1;
        } finally { lock.unlock(); }
    }

    /** freq += 1 and move key to the MRU end of the new bucket. */
    private void touch(int key) {
        int f = keyToFreq.get(key);
        LinkedHashSet<Integer> bucket = freqToKeys.get(f);
        bucket.remove(key);
        if (bucket.isEmpty()) {
            freqToKeys.remove(f);
            if (minFreq == f) minFreq = f + 1;
        }
        keyToFreq.put(key, f + 1);
        freqToKeys.computeIfAbsent(f + 1, x -> new LinkedHashSet<>()).add(key);
    }

    private void evict() {
        LinkedHashSet<Integer> bucket = freqToKeys.get(minFreq);
        int victim = bucket.iterator().next(); // oldest = least recently used
        bucket.remove(victim);
        if (bucket.isEmpty()) freqToKeys.remove(minFreq);
        keyToVal.remove(victim);
        keyToFreq.remove(victim);
    }

    public static int[] processLfuCache(int capacity, String[] operations) {
        LFUCache c = new LFUCache(capacity);
        List<Integer> out = new ArrayList<>();
        for (String op : operations) {
            String[] p = op.trim().split("\\s+");
            if (p[0].equals("get")) out.add(c.get(Integer.parseInt(p[1])));
            else c.put(Integer.parseInt(p[1]), Integer.parseInt(p[2]));
        }
        return out.stream().mapToInt(Integer::intValue).toArray();
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(Arrays.toString(processLfuCache(2, new String[]{
            "put 1 1", "put 2 2", "get 1", "put 3 3", "get 2", "get 3", "put 4 4", "get 1", "get 3", "get 4"})));
        // [1, -1, 3, -1, 3, 4]
        System.out.println(Arrays.toString(processLfuCache(1, new String[]{
            "put 7 5", "put 7 8", "get 7", "put 9 4", "get 7", "get 9"})));
        // [8, -1, 4]
        System.out.println(Arrays.toString(processLfuCache(2, new String[]{
            "put 1 10", "put 2 20", "put 1 11", "put 3 30", "get 1", "get 2", "get 3"})));
        // [11, -1, 30]

        // concurrency smoke test: size never exceeds capacity, no exceptions
        LFUCache c = new LFUCache(100);
        Thread[] ts = new Thread[8];
        for (int t = 0; t < ts.length; t++) {
            final int seed = t;
            ts[t] = new Thread(() -> {
                Random r = new Random(seed);
                for (int i = 0; i < 50000; i++) {
                    int k = r.nextInt(500);
                    if (r.nextBoolean()) c.put(k, i); else c.get(k);
                }
            });
            ts[t].start();
        }
        for (Thread t : ts) t.join();
        System.out.println("size after concurrent ops: " + c.keyToVal.size() + " (<= 100)");
    }
}
