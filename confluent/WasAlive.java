import java.util.*;

/**
 * Confluent onsite (newer 地理 question): was the server alive?
 *
 * Given a list of (key, timestamp): key was seen at that time.
 * Implement bool wasAlive(int key, int start):
 *   Split [start, start + 499] into 5 consecutive 100ms slots:
 *     [start, start+99], [start+100, start+199], ..., [start+400, start+499]
 *   Return true if there are 3 CONSECUTIVE slots that each contain >= 1 record of key.
 *
 *   (1, 1), (1, 101), (1, 201) -> wasAlive(1, 0) = true   (slots 0,1,2 active)
 *   (2, 1), (2, 101), (2, 301) -> wasAlive(2, 0) = false  (slots 0,1,3 active)
 *
 * Part 1 (brute force): group timestamps by key; for a query, scan all of that key's
 *   timestamps and mark which slot each falls in. O(m) per query, m = records of key.
 * Part 2 (optimization): keep each key's timestamps sorted; for each of the 5 slots,
 *   binary search the first timestamp >= slotStart and check it is <= slotEnd.
 *   O(5 log m) per query. Sort once at build time (or TreeSet.ceiling() if streaming).
 *
 * Generalize: SLOTS / SLOT_SIZE / NEED as parameters; the run check is a simple counter.
 */
public class WasAlive {

    static final int SLOTS = 5, SLOT_SIZE = 100, NEED = 3;

    private final Map<Integer, int[]> sorted = new HashMap<>();
    private final Map<Integer, List<Integer>> raw = new HashMap<>();

    public WasAlive(int[][] records) {
        for (int[] r : records) raw.computeIfAbsent(r[0], k -> new ArrayList<>()).add(r[1]);
        for (Map.Entry<Integer, List<Integer>> e : raw.entrySet()) {
            int[] ts = e.getValue().stream().mapToInt(Integer::intValue).toArray();
            Arrays.sort(ts);
            sorted.put(e.getKey(), ts);
        }
    }

    // ---------------- Part 1: brute force ----------------
    public boolean wasAliveBrute(int key, int start) {
        boolean[] active = new boolean[SLOTS];
        for (int t : raw.getOrDefault(key, Collections.emptyList())) {
            if (t >= start && t <= start + SLOTS * SLOT_SIZE - 1) {
                active[(t - start) / SLOT_SIZE] = true;
            }
        }
        return hasConsecutive(active);
    }

    // ---------------- Part 2: binary search ----------------
    public boolean wasAlive(int key, int start) {
        int[] ts = sorted.get(key);
        if (ts == null) return false;
        boolean[] active = new boolean[SLOTS];
        for (int i = 0; i < SLOTS; i++) {
            int lo = start + i * SLOT_SIZE, hi = lo + SLOT_SIZE - 1;
            int idx = lowerBound(ts, lo);
            active[i] = idx < ts.length && ts[idx] <= hi;
        }
        return hasConsecutive(active);
    }

    private static boolean hasConsecutive(boolean[] active) {
        int run = 0;
        for (boolean a : active) {
            run = a ? run + 1 : 0;
            if (run >= NEED) return true;
        }
        return false;
    }

    /** first index i with ts[i] >= target */
    private static int lowerBound(int[] ts, int target) {
        int lo = 0, hi = ts.length;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (ts[mid] < target) lo = mid + 1;
            else hi = mid;
        }
        return lo;
    }

    public static void main(String[] args) {
        int[][] records = {
            {1, 1}, {1, 101}, {1, 201},
            {2, 1}, {2, 101}, {2, 301},
            {3, 450}, {3, 250}, {3, 399}, {3, 700},   // unsorted input; slots 2,3,4 from start 0
            {4, 99}, {4, 100}, {4, 199}, {4, 200},     // slot boundaries
        };
        WasAlive w = new WasAlive(records);
        int[][] queries = {
            {1, 0, 1}, {2, 0, 0}, {3, 0, 1}, {3, 1000, 0},
            {4, 0, 1},   // 99 in slot0, 100/199 in slot1, 200 in slot2 -> true
            {4, 1, 0},   // start 1: 99 slot0, 100,199 slot0/1, 200 slot1 -> slots 0,1 only
            {9, 0, 0},   // unknown key
            {1, -99, 1}, // slots [-99,0],[1,100],[101,200],[201,300] -> 1,2,3 active
        };
        for (int[] q : queries) {
            boolean fast = w.wasAlive(q[0], q[1]), brute = w.wasAliveBrute(q[0], q[1]);
            System.out.printf("wasAlive(%d, %d) = %b  brute=%b  expected=%b %s%n",
                q[0], q[1], fast, brute, q[2] == 1, (fast == brute && fast == (q[2] == 1)) ? "OK" : "FAIL");
        }
    }
}
