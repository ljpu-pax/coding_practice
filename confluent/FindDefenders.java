import java.util.*;

/**
 * Confluent onsite (2024): Find Defenders Against Every Hostile Monster.
 * Source: FastPrep "confluent-hostile-monster-defenders".
 *
 * public String[] findDefenders(String[] names, int[] parent, boolean[] hostile)
 *   Monsters form a forest: parent[i] can directly defeat i (roots have -1).
 *   Defeat is transitive (ancestor defeats every descendant).
 *   Return every NON-hostile monster that can defeat EVERY hostile monster,
 *   in input order. n <= 2e5, at least one hostile monster.
 *
 *   ["dragon","griffin","orc","imp"], [-1,0,0,2], [F,F,T,T]  -> ["dragon"]
 *   ["atlas","guardian","rat"],       [-1,0,1],   [F,F,T]    -> ["atlas","guardian"]
 *   ["oak","wolf","elm","goblin"],    [-1,0,-1,2],[F,T,F,T]  -> []  (different trees)
 *
 * i.e. i is an answer  <=>  !hostile[i]  AND  all hostile monsters are strict
 * descendants of i  <=>  hostileInSubtree(i) == totalHostile (i itself is not hostile).
 *
 * Solution 1 (subtree counts), O(n):
 *   Build children lists, get a BFS order from the roots, then walk it in REVERSE
 *   (children before parents) adding each node's count into its parent.
 *   Iterative - recursion would overflow the stack on a 2e5-deep chain.
 *
 * Solution 2 (LCA view): answers are exactly the non-hostile nodes on the path from
 *   the root down to the LCA of all hostile monsters. Walk up from the first hostile
 *   node marking ancestors; intersect with each other hostile's ancestor chain.
 *   Same O(n) but more code - mention it, write solution 1.
 */
public class FindDefenders {

    public String[] findDefenders(String[] names, int[] parent, boolean[] hostile) {
        int n = names.length;
        List<List<Integer>> children = new ArrayList<>();
        for (int i = 0; i < n; i++) children.add(new ArrayList<>());
        int[] order = new int[n];
        int head = 0, tail = 0;
        for (int i = 0; i < n; i++) {
            if (parent[i] == -1) order[tail++] = i;
            else children.get(parent[i]).add(i);
        }
        while (head < tail) {
            int u = order[head++];
            for (int v : children.get(u)) order[tail++] = v;
        }

        int[] cnt = new int[n];
        int total = 0;
        for (int i = 0; i < n; i++) {
            if (hostile[i]) { cnt[i] = 1; total++; }
        }
        for (int i = n - 1; i >= 0; i--) {
            int u = order[i];
            if (parent[u] != -1) cnt[parent[u]] += cnt[u];
        }

        List<String> res = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (!hostile[i] && cnt[i] == total) res.add(names[i]);
        }
        return res.toArray(new String[0]);
    }

    public static void main(String[] args) {
        FindDefenders f = new FindDefenders();
        boolean T = true, F = false;
        System.out.println(Arrays.toString(f.findDefenders(
            new String[]{"dragon", "griffin", "orc", "imp"}, new int[]{-1, 0, 0, 2}, new boolean[]{F, F, T, T})));
        // [dragon]  (orc is hostile itself, so it doesn't count)
        System.out.println(Arrays.toString(f.findDefenders(
            new String[]{"atlas", "guardian", "rat"}, new int[]{-1, 0, 1}, new boolean[]{F, F, T})));
        // [atlas, guardian]
        System.out.println(Arrays.toString(f.findDefenders(
            new String[]{"oak", "wolf", "elm", "goblin"}, new int[]{-1, 0, -1, 2}, new boolean[]{F, T, F, T})));
        // []
        System.out.println(Arrays.toString(f.findDefenders(
            new String[]{"imp", "king", "knight"}, new int[]{1, -1, 1}, new boolean[]{T, F, F})));
        // [king]  (parent index can point forward)

        // deep chain: 200000 nodes, only the last is hostile -> every other node is an answer
        int n = 200000;
        String[] names = new String[n];
        int[] par = new int[n];
        boolean[] h = new boolean[n];
        for (int i = 0; i < n; i++) { names[i] = "m" + i; par[i] = i - 1; }
        h[n - 1] = true;
        System.out.println("deep chain answers: " + f.findDefenders(names, par, h).length); // 199999
    }
}
