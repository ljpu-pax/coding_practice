import java.util.*;

/**
 * Confluent onsite: Warehouse Loading / "comb SUM".
 *
 * Given N integer weights (positive = load, negative = unload), robot starts at 0.
 * Is there ANY ordering of the N operations such that the load equals exactly
 * `target` at some point during the process?
 *
 * Key insight: "some prefix of some ordering" == "some subset" (put the subset first).
 * So this is subset sum with negative numbers (empty subset -> target 0 is reachable at start).
 *
 * Part 1: return true/false. Part 2: return one valid subset.
 *
 * Approaches:
 * 1) Backtracking (what the candidate did): O(2^N) time, O(N) recursion depth.
 * 2) Interviewer's hint "remove the for loop": iterative DP over reachable sums.
 *    Offset DP: sums lie in [negSum, posSum]; boolean array of size (posSum - negSum + 1).
 *    O(N * range) time, O(range) space (1-D, iterate a copy per item).
 * 3) Reconstruction: keep parent pointers (sum -> item index that first reached it).
 */
public class WarehouseLoading {

    // ---------------- Part 1, simplest: pick or skip each item ----------------
    // No pruning on sum > target: a later negative weight can bring it back down.
    public static boolean canReach(int[] w, int target) {
        return canReach(w, 0, 0, target);
    }

    private static boolean canReach(int[] w, int i, int sum, int target) {
        if (sum == target) return true;          // the items picked so far reach target
        if (i == w.length) return false;         // no items left to try
        return canReach(w, i + 1, sum + w[i], target)   // pick w[i]
            || canReach(w, i + 1, sum, target);         // skip w[i]
    }

    // ---------------- Part 1, no recursion: set of reachable sums ----------------
    // Start with {0}; each item adds s + x for every sum s seen so far. O(n * #distinct sums).
    public static boolean canReachSet(int[] w, int target) {
        Set<Integer> sums = new HashSet<>();
        sums.add(0);
        for (int x : w) {
            Set<Integer> next = new HashSet<>(sums);  // skip x
            for (int s : sums) next.add(s + x);       // pick x
            sums = next;
        }
        return sums.contains(target);
    }

    // ---------------- 1) Backtracking: include / exclude ----------------
    public static boolean canReachBacktrack(int[] w, int target) {
        return dfs(w, 0, 0, target, new ArrayList<>()) != null;
    }

    public static List<Integer> findSubsetBacktrack(int[] w, int target) {
        return dfs(w, 0, 0, target, new ArrayList<>());
    }

    private static List<Integer> dfs(int[] w, int i, int sum, int target, List<Integer> path) {
        if (sum == target) return new ArrayList<>(path);
        if (i == w.length) return null;
        path.add(w[i]);
        List<Integer> r = dfs(w, i + 1, sum + w[i], target, path);
        path.remove(path.size() - 1);
        if (r != null) return r;
        return dfs(w, i + 1, sum, target, path);
    }

    // ---------------- 2) DP with offset (handles negatives) ----------------
    public static boolean canReachDp(int[] w, int target) {
        int neg = 0, pos = 0;
        for (int x : w) { if (x < 0) neg += x; else pos += x; }
        if (target < neg || target > pos) return false;
        int off = -neg, range = pos - neg + 1;
        boolean[] dp = new boolean[range];
        dp[off] = true; // empty subset -> sum 0
        for (int x : w) {
            boolean[] next = dp.clone();
            for (int s = 0; s < range; s++) {
                if (dp[s] && s + x >= 0 && s + x < range) next[s + x] = true;
            }
            dp = next;
        }
        return dp[target + off];
    }

    // ---------------- 3) Hash set of reachable sums + parent pointers ----------------
    /** Works for any value range; returns one subset (in original order) or null. */
    public static List<Integer> findSubsetDp(int[] w, int target) {
        // parent: sum -> {prevSum, itemIndex}
        Map<Integer, int[]> parent = new HashMap<>();
        parent.put(0, null);
        if (target == 0) return new ArrayList<>();
        for (int i = 0; i < w.length; i++) {
            List<Integer> sums = new ArrayList<>(parent.keySet()); // snapshot: use item once
            for (int s : sums) {
                int ns = s + w[i];
                if (!parent.containsKey(ns)) {
                    parent.put(ns, new int[]{s, i});
                    if (ns == target) return rebuild(parent, target, w);
                }
            }
        }
        return null;
    }

    private static List<Integer> rebuild(Map<Integer, int[]> parent, int target, int[] w) {
        LinkedList<Integer> res = new LinkedList<>();
        int cur = target;
        while (parent.get(cur) != null) {
            int[] p = parent.get(cur);
            res.addFirst(w[p[1]]);
            cur = p[0];
        }
        return res;
    }

    public static void main(String[] args) {
        int[][] ws = {{5, -3, 2, 7}, {4, 6}, {-2, -4, 10}, {1, 2, 3}, {}};
        int[] targets = {4, 5, -6, 0, 3};
        for (int k = 0; k < ws.length; k++) {
            int[] w = ws[k];
            int t = targets[k];
            System.out.printf("w=%s target=%d bt=%b dp=%b subsetBt=%s subsetDp=%s%n",
                Arrays.toString(w), t, canReachBacktrack(w, t), canReachDp(w, t),
                findSubsetBacktrack(w, t), findSubsetDp(w, t));
        }
        // expected: true/true [5,-3,2] ; false ; true [-2,-4] ; true [] ; false

        // Part 1 simple versions must agree with the DP on every case
        boolean same = true;
        for (int k = 0; k < ws.length; k++) {
            boolean dp = canReachDp(ws[k], targets[k]);
            same &= canReach(ws[k], targets[k]) == dp && canReachSet(ws[k], targets[k]) == dp;
        }
        Random rnd = new Random(7);
        for (int t = 0; t < 2000; t++) {
            int[] w = new int[rnd.nextInt(8)];
            for (int i = 0; i < w.length; i++) w[i] = rnd.nextInt(21) - 10;
            int target = rnd.nextInt(41) - 20;
            boolean dp = canReachDp(w, target);
            same &= canReach(w, target) == dp && canReachSet(w, target) == dp;
        }
        System.out.println("part 1 simple == dp: " + same); // true
    }
}
