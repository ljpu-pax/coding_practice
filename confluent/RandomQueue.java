import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Confluent onsite: Random Queue.
 *
 * A queue where dequeue() returns (and removes) a uniformly random element.
 * - enqueue O(1), dequeue O(1): swap the random index with the last element, remove last.
 *
 * Follow-ups:
 * 1) equals(other): two random queues are "equal" if they contain the same multiset of
 *    elements (order is meaningless). Compare size, then frequency maps. O(n).
 * 2) Multithreading: enqueue/dequeue mutate the list + size together -> guard with a lock
 *    (or synchronized). Dequeue on empty: block (Condition) or return null. equals() must
 *    lock BOTH queues in a consistent global order (e.g. by identityHashCode / id) to
 *    avoid deadlock; Random -> ThreadLocalRandom.
 * 3) Queue stored as variable run-length encoding [(value, count), ...]:
 *    equality = same multiset -> merge runs into value -> total count maps and compare.
 *    Runs can repeat a value and be split differently ([(a,2)] == [(a,1),(a,1)]).
 *    If order DID matter (plain RLE list equality): two-pointer walk consuming
 *    min(countA, countB) from the current runs, see rleSequenceEquals.
 */
public class RandomQueue<T> {

    // ---------------- Part 1 + 2: basic version, single thread, no locks ----------------
    static class Basic<T> {
        private final List<T> items = new ArrayList<>();
        private final Random rnd = new Random();

        void enqueue(T x) { items.add(x); }

        /** Remove and return a uniformly random element, or null if empty. O(1). */
        T dequeue() {
            if (items.isEmpty()) return null;
            int i = rnd.nextInt(items.size());
            int last = items.size() - 1;
            T res = items.get(i);
            items.set(i, items.get(last));   // move the last element into the hole
            items.remove(last);              // removing the last one is O(1)
            return res;
        }

        int size() { return items.size(); }

        /** Same elements with the same counts; order doesn't matter. O(n). */
        boolean sameElements(Basic<T> other) {
            if (items.size() != other.items.size()) return false;
            Map<T, Integer> cnt = new HashMap<>();
            for (T x : items) cnt.merge(x, 1, Integer::sum);        // count mine
            for (T x : other.items) cnt.merge(x, -1, Integer::sum); // subtract theirs
            for (int c : cnt.values()) if (c != 0) return false;
            return true;
        }
    }

    // ---------------- Part 3: thread-safe version ----------------
    private final List<T> items = new ArrayList<>();
    private final Random rnd;
    private final ReentrantLock lock = new ReentrantLock();
    private static long nextId = 0;
    private final long id;

    public RandomQueue() { this(new Random()); }

    public RandomQueue(Random rnd) {
        this.rnd = rnd;
        synchronized (RandomQueue.class) { id = nextId++; }
    }

    public void enqueue(T x) {
        lock.lock();
        try { items.add(x); } finally { lock.unlock(); }
    }

    public T dequeue() {
        lock.lock();
        try {
            if (items.isEmpty()) return null;
            int i = rnd.nextInt(items.size());
            int last = items.size() - 1;
            T res = items.get(i);
            items.set(i, items.get(last));
            items.remove(last);
            return res;
        } finally { lock.unlock(); }
    }

    public int size() {
        lock.lock();
        try { return items.size(); } finally { lock.unlock(); }
    }

    /** Multiset equality; locks both queues in id order to avoid deadlock. */
    public boolean sameElements(RandomQueue<T> other) {
        if (this == other) return true;
        RandomQueue<T> first = id < other.id ? this : other;
        RandomQueue<T> second = first == this ? other : this;
        first.lock.lock();
        second.lock.lock();
        try {
            if (items.size() != other.items.size()) return false;
            Map<T, Integer> cnt = new HashMap<>();
            for (T x : items) cnt.merge(x, 1, Integer::sum);
            for (T x : other.items) {
                Integer c = cnt.get(x);
                if (c == null) return false;
                if (c == 1) cnt.remove(x); else cnt.put(x, c - 1);
            }
            return cnt.isEmpty();
        } finally {
            second.lock.unlock();
            first.lock.unlock();
        }
    }

    // ---------------- RLE follow-up ----------------
    static class Run<T> {
        final T value; final long count;
        Run(T value, long count) { this.value = value; this.count = count; }
    }

    /** Random-queue semantics: order irrelevant, compare total count per value. */
    static <T> boolean rleMultisetEquals(List<Run<T>> a, List<Run<T>> b) {
        Map<T, Long> cnt = new HashMap<>();
        for (Run<T> r : a) cnt.merge(r.value, r.count, Long::sum);
        for (Run<T> r : b) cnt.merge(r.value, -r.count, Long::sum);
        for (long v : cnt.values()) if (v != 0) return false;
        return true;
    }

    /** Ordered semantics: expanded sequences equal, without expanding. O(runs). */
    static <T> boolean rleSequenceEquals(List<Run<T>> a, List<Run<T>> b) {
        int i = 0, j = 0;
        long ra = 0, rb = 0; // remaining count in current run
        while (true) {
            while (ra == 0 && i < a.size()) ra = a.get(i++).count;
            while (rb == 0 && j < b.size()) rb = b.get(j++).count;
            if (ra == 0 || rb == 0) return ra == 0 && rb == 0;
            if (!Objects.equals(a.get(i - 1).value, b.get(j - 1).value)) return false;
            long take = Math.min(ra, rb);
            ra -= take;
            rb -= take;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Part 1 + 2 with the basic version
        Basic<Integer> bq = new Basic<>();
        for (int i = 1; i <= 5; i++) bq.enqueue(i);
        List<Integer> out = new ArrayList<>();
        Integer y;
        while ((y = bq.dequeue()) != null) out.add(y);
        List<Integer> sorted = new ArrayList<>(out);
        Collections.sort(sorted);
        System.out.println("basic dequeued: " + out + " all 1..5 once: " + sorted.equals(List.of(1, 2, 3, 4, 5))); // true
        Basic<String> ba = new Basic<>(), bb = new Basic<>();
        for (String s : new String[]{"a", "b", "a"}) ba.enqueue(s);
        for (String s : new String[]{"a", "a", "b"}) bb.enqueue(s);
        System.out.println("basic same: " + ba.sameElements(bb));   // true
        bb.dequeue(); bb.enqueue("c");
        System.out.println("basic same: " + ba.sameElements(bb));   // false

        // Uniformity check: each of 4 items should come out first about 25% of the time
        int[] firstCount = new int[4];
        for (int t = 0; t < 40000; t++) {
            Basic<Integer> u = new Basic<>();
            for (int i = 0; i < 4; i++) u.enqueue(i);
            firstCount[u.dequeue()]++;
        }
        System.out.println("first-out counts (~10000 each): " + Arrays.toString(firstCount));

        // Part 3 with the thread-safe version
        RandomQueue<Integer> q = new RandomQueue<>(new Random(42));
        for (int i = 1; i <= 5; i++) q.enqueue(i);
        StringBuilder sb = new StringBuilder();
        Integer x;
        while ((x = q.dequeue()) != null) sb.append(x).append(' ');
        System.out.println("dequeued: " + sb); // permutation of 1..5

        RandomQueue<String> a = new RandomQueue<>(), b = new RandomQueue<>();
        for (String s : new String[]{"a", "b", "a"}) a.enqueue(s);
        for (String s : new String[]{"a", "a", "b"}) b.enqueue(s);
        System.out.println(a.sameElements(b)); // true
        b.enqueue("c");
        System.out.println(a.sameElements(b)); // false

        // concurrency smoke test
        RandomQueue<Integer> cq = new RandomQueue<>();
        Thread[] ts = new Thread[4];
        for (int t = 0; t < 4; t++) {
            ts[t] = new Thread(() -> { for (int i = 0; i < 10000; i++) cq.enqueue(i); });
            ts[t].start();
        }
        for (Thread t : ts) t.join();
        System.out.println("size after concurrent enqueue: " + cq.size()); // 40000

        List<Run<Character>> r1 = List.of(new Run<>('a', 2), new Run<>('b', 1));
        List<Run<Character>> r2 = List.of(new Run<>('b', 1), new Run<>('a', 1), new Run<>('a', 1));
        List<Run<Character>> r3 = List.of(new Run<>('a', 1), new Run<>('a', 1), new Run<>('b', 1));
        System.out.println(rleMultisetEquals(r1, r2)); // true
        System.out.println(rleSequenceEquals(r1, r2)); // false
        System.out.println(rleSequenceEquals(r1, r3)); // true
    }
}
