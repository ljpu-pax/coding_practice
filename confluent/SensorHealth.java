import java.util.*;

/**
 * Confluent onsite: "Was server alive" / "Silent Sensor" detector.
 *
 * Given pings (key, timestamp). Two versions appear in reports:
 *
 * Version A (wasAlive):
 *   Window [start, start + 499] split into 5 slots of 100ms.
 *   wasAlive(key, start) is true if there are 3 CONSECUTIVE slots that each have >= 1 ping.
 *   (1,1),(1,101),(1,201) -> wasAlive(1,0) = true
 *   (2,1),(2,101),(2,301) -> wasAlive(2,0) = false
 *
 * Version B (SensorHealth):
 *   Window of 5 slots of 60s starting at T: [T, T+59], [T+60, T+119], ...
 *   UNSTABLE if 3 consecutive slots are INACTIVE (no ping), else STABLE.
 *
 * Solution: HashMap<key, sorted list of timestamps>. For each slot, binary search
 * for the first timestamp >= slotStart and check it is <= slotEnd.
 * Query: O(slots * log m). Brute force (scan all pings) is O(m) per query.
 *
 * Follow-ups:
 * - Pings arrive out of order / streaming -> TreeSet per key (ceiling()), or sort lazily.
 * - Generalize slot count / slot size / required run length (parameters below).
 */
public class SensorHealth {

    private final Map<String, List<Long>> pings = new HashMap<>();
    private final Set<String> dirty = new HashSet<>();
    private final int slotCount;
    private final long slotSize;

    public SensorHealth(int slotCount, long slotSize) {
        this.slotCount = slotCount;
        this.slotSize = slotSize;
    }

    public void ping(String key, long ts) {
        pings.computeIfAbsent(key, k -> new ArrayList<>()).add(ts);
        dirty.add(key); // sort lazily at query time
    }

    private List<Long> times(String key) {
        List<Long> list = pings.getOrDefault(key, Collections.emptyList());
        if (dirty.remove(key)) Collections.sort(list);
        return list;
    }

    /** active[i] = slot i has at least one ping. */
    boolean[] activeSlots(String key, long start) {
        List<Long> ts = times(key);
        boolean[] active = new boolean[slotCount];
        for (int i = 0; i < slotCount; i++) {
            long lo = start + i * slotSize, hi = lo + slotSize - 1;
            int idx = lowerBound(ts, lo);
            active[i] = idx < ts.size() && ts.get(idx) <= hi;
        }
        return active;
    }

    /** Version A: 3 consecutive active slots. */
    public boolean wasAlive(String key, long start) {
        return hasRun(activeSlots(key, start), true, 3);
    }

    /** Version B: 3 consecutive inactive slots -> UNSTABLE. */
    public String status(String key, long t) {
        return hasRun(activeSlots(key, t), false, 3) ? "UNSTABLE" : "STABLE";
    }

    private static boolean hasRun(boolean[] slots, boolean value, int k) {
        int run = 0;
        for (boolean s : slots) {
            run = (s == value) ? run + 1 : 0;
            if (run >= k) return true;
        }
        return false;
    }

    /** first index with ts[idx] >= target */
    private static int lowerBound(List<Long> ts, long target) {
        int lo = 0, hi = ts.size();
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (ts.get(mid) < target) lo = mid + 1;
            else hi = mid;
        }
        return lo;
    }

    public static void main(String[] args) {
        SensorHealth a = new SensorHealth(5, 100);
        a.ping("1", 1); a.ping("1", 101); a.ping("1", 201);
        a.ping("2", 1); a.ping("2", 101); a.ping("2", 301);
        System.out.println(a.wasAlive("1", 0)); // true
        System.out.println(a.wasAlive("2", 0)); // false
        System.out.println(a.wasAlive("3", 0)); // false (unknown key)

        SensorHealth b = new SensorHealth(5, 60);
        b.ping("Alpha", 130); b.ping("Alpha", 10); // out of order is fine
        b.ping("Beta", 5); b.ping("Beta", 250);
        System.out.println(b.status("Alpha", 0)); // slots: A I A I I -> STABLE
        System.out.println(b.status("Beta", 0));  // slots: A I I I A -> UNSTABLE
    }
}
