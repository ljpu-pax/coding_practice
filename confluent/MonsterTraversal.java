import java.util.*;

/**
 * Confluent onsite: Constrained Monster Traversal (2025).
 * Source: PracHub "Solve constrained monster traversal".
 *
 * Directed graph with n rooms 0..n-1; room i has a monster with health hp[i] >= 0.
 * Start in room s with energy E. ENTERING a room (including s) costs hp[i].
 * Energy must stay >= 0 after every room entry.
 *
 * Part A: is there a survivable path s -> t? Return (reachable, path).
 *   (4, [[0,1],[1,2],[0,2],[2,3]], hp=[0,2,1,1], s=0, t=3, E=4) -> true
 *       e.g. [0,1,2,3] (cost 4) or [0,2,3] (cost 2); any survivable path is valid.
 *   (3, [[0,1],[1,2]], hp=[0,5,0], s=0, t=2, E=4) -> (false, [])
 *
 *   Since hp >= 0, energy only goes down, so "survivable" == "cheapest path cost <= E".
 *   -> Dijkstra on node weights: dist[v] = min over u of dist[u] + hp[v], dist[s] = hp[s].
 *      O((n + m) log n), fine for n = 2e5, m = 5e5.
 *   Reference solution uses DFS with pruning (keep the best remaining energy seen per
 *   room; revisit only with strictly more energy). Also correct; Dijkstra has the
 *   clean worst-case bound. Use an ITERATIVE traversal for n = 2e5 (no stack overflow).
 *
 * Part B: rooms may hold potions [room, gain]; first entry adds gain.
 *   Net change on entering room i = gain[i] - hp[i] (applied together).
 *   Find the path s -> t that visits the MOST distinct rooms while energy stays >= 0.
 *   Return the room count (including s and t), or -1 if t can't be reached.
 *
 *   This is a longest-simple-path problem -> NP-hard in general, so the expected
 *   answer is backtracking DFS over simple paths (visited set, undo on return),
 *   pruning when energy < 0. Say this out loud; mention that on a DAG you can do
 *   DP in topological order with state (room, energy) if E is small.
 *   Simple paths only: revisiting a room never gives a potion again and costs hp again.
 */
public class MonsterTraversal {

    static List<List<Integer>> buildGraph(int n, int[][] edges) {
        List<List<Integer>> g = new ArrayList<>();
        for (int i = 0; i < n; i++) g.add(new ArrayList<>());
        for (int[] e : edges) g.get(e[0]).add(e[1]);
        return g;
    }

    // ---------------- Part A: Dijkstra on node costs ----------------
    /** Returns the survivable path s -> t, or an empty list if none. */
    public static List<Integer> survivablePath(int n, int[][] edges, int[] hp, int s, int t, long E) {
        List<List<Integer>> g = buildGraph(n, edges);
        long[] dist = new long[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Long.MAX_VALUE);
        Arrays.fill(parent, -1);
        if (hp[s] > E) return new ArrayList<>();
        dist[s] = hp[s];
        PriorityQueue<long[]> pq = new PriorityQueue<>(Comparator.comparingLong(a -> a[0]));
        pq.add(new long[]{dist[s], s});
        while (!pq.isEmpty()) {
            long[] top = pq.poll();
            int u = (int) top[1];
            if (top[0] > dist[u]) continue;
            if (u == t) break;
            for (int v : g.get(u)) {
                long nd = dist[u] + hp[v];
                if (nd <= E && nd < dist[v]) { // prune anything that would kill us
                    dist[v] = nd;
                    parent[v] = u;
                    pq.add(new long[]{nd, v});
                }
            }
        }
        LinkedList<Integer> path = new LinkedList<>();
        if (dist[t] == Long.MAX_VALUE) return path;
        for (int v = t; v != -1; v = parent[v]) path.addFirst(v);
        return path;
    }

    public static boolean canReach(int n, int[][] edges, int[] hp, int s, int t, long E) {
        return !survivablePath(n, edges, hp, s, t, E).isEmpty();
    }

    // ---------------- Part B: backtracking for max rooms ----------------
    public static int maxRooms(int n, int[][] edges, int[] hp, int[][] potions, int s, int t, long E) {
        List<List<Integer>> g = buildGraph(n, edges);
        long[] net = new long[n];
        for (int i = 0; i < n; i++) net[i] = -hp[i];
        for (int[] p : potions) net[p[0]] += p[1];

        long energy = E + net[s];
        if (energy < 0) return -1;
        boolean[] visited = new boolean[n];
        visited[s] = true;
        int[] best = {-1};
        dfs(g, net, s, t, energy, 1, visited, best);
        return best[0];
    }

    private static void dfs(List<List<Integer>> g, long[] net, int u, int t, long energy,
                            int count, boolean[] visited, int[] best) {
        if (u == t) {
            best[0] = Math.max(best[0], count);
            return; // path must END at t; entering t again later is not allowed (simple path)
        }
        for (int v : g.get(u)) {
            if (visited[v]) continue;
            long ne = energy + net[v];
            if (ne < 0) continue; // dies entering v
            visited[v] = true;
            dfs(g, net, v, t, ne, count + 1, visited, best);
            visited[v] = false;   // backtrack
        }
    }

    public static void main(String[] args) {
        int[][] edges = {{0, 1}, {1, 2}, {0, 2}, {2, 3}};

        // Part A
        System.out.println(survivablePath(4, edges, new int[]{0, 2, 1, 1}, 0, 3, 4));   // [0, 2, 3]
        System.out.println(survivablePath(3, new int[][]{{0, 1}, {1, 2}}, new int[]{0, 5, 0}, 0, 2, 4)); // []
        System.out.println(canReach(2, new int[][]{{0, 1}}, new int[]{3, 1}, 0, 1, 2)); // false: start room costs 3
        System.out.println(canReach(1, new int[][]{}, new int[]{0}, 0, 0, 0));          // true: s == t

        // Part B
        System.out.println(maxRooms(4, edges, new int[]{0, 2, 1, 1}, new int[][]{}, 0, 3, 4));          // 4
        System.out.println(maxRooms(4, edges, new int[]{0, 5, 1, 1}, new int[][]{{1, 5}}, 0, 3, 4));    // 4
        System.out.println(maxRooms(4, edges, new int[]{0, 5, 1, 1}, new int[][]{}, 0, 3, 4));          // 3 (skip room 1)
        System.out.println(maxRooms(3, new int[][]{{0, 1}}, new int[]{0, 0, 0}, new int[][]{}, 0, 2, 9)); // -1
    }
}
