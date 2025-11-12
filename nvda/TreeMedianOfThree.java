package nvda;

import java.util.*;

public class TreeMedianOfThree {

    static class Edge {
        int to;
        long w;
        Edge(int to, long w) { this.to = to; this.w = w; }
    }

    // Runs a single-source DFS accumulating weighted distances in a tree
    private static void dfs(int u, int parent, long dist, List<List<Edge>> g, long[] out) {
        out[u] = dist;
        for (Edge e : g.get(u)) {
            if (e.to == parent) continue;
            dfs(e.to, u, dist + e.w, g, out);
        }
    }

    // ---------------- LCA (binary lifting) for unweighted structure ----------------
    static class LCA {
        int n, LOG;
        int[][] up;
        int[] depth;
        List<List<Integer>> tree;

        LCA(int n, List<int[]> edges, int root) {
            this.n = n;
            this.LOG = 1;
            while ((1 << LOG) <= n) LOG++;
            up = new int[LOG][n];
            depth = new int[n];
            tree = new ArrayList<>();
            for (int i = 0; i < n; i++) tree.add(new ArrayList<>());
            for (int[] e : edges) {
                int u = e[0], v = e[1];
                tree.get(u).add(v);
                tree.get(v).add(u);
            }
            Arrays.fill(up[0], -1);
            build(root);
        }

        private void build(int root) {
            Deque<Integer> dq = new ArrayDeque<>();
            dq.add(root);
            up[0][root] = root;
            depth[root] = 0;
            boolean[] vis = new boolean[n];
            vis[root] = true;
            while (!dq.isEmpty()) {
                int u = dq.poll();
                for (int v : tree.get(u)) {
                    if (vis[v]) continue;
                    vis[v] = true;
                    up[0][v] = u;
                    depth[v] = depth[u] + 1;
                    dq.add(v);
                }
            }
            for (int k = 1; k < LOG; k++) {
                for (int v = 0; v < n; v++) {
                    up[k][v] = up[k-1][ up[k-1][v] ];
                }
            }
        }

        int lca(int a, int b) {
            if (depth[a] < depth[b]) { int t = a; a = b; b = t; }
            int diff = depth[a] - depth[b];
            for (int k = 0; k < LOG; k++) if (((diff >> k) & 1) == 1) a = up[k][a];
            if (a == b) return a;
            for (int k = LOG - 1; k >= 0; k--) {
                if (up[k][a] != up[k][b]) { a = up[k][a]; b = up[k][b]; }
            }
            return up[0][a];
        }
    }

    // The minimizing node for three nodes in a tree equals the deepest among
    // lca(a,b), lca(b,c), lca(c,a) in terms of depth in the rooted tree.
    public static int bestNodeByLCA(int n, List<int[]> edges, int a, int b, int c, int root) {
        LCA lca = new LCA(n, edges, root);
        int x = lca.lca(a, b);
        int y = lca.lca(b, c);
        int z = lca.lca(c, a);
        int ans = x;
        if (lca.depth[y] > lca.depth[ans]) ans = y;
        if (lca.depth[z] > lca.depth[ans]) ans = z;
        return ans;
    }

    // Given tree with n nodes labeled [0..n-1]
    // edges: int[]{u, v, w}
    // targets: a, b, c
    public static int bestNodeMinSum(int n, List<int[]> edges, int a, int b, int c) {
        List<List<Edge>> g = new ArrayList<>();
        for (int i = 0; i < n; i++) g.add(new ArrayList<>());
        for (int[] e : edges) {
            int u = e[0], v = e[1];
            long w = e[2];
            g.get(u).add(new Edge(v, w));
            g.get(v).add(new Edge(u, w));
        }

        long[] da = new long[n], db = new long[n], dc = new long[n];
        dfs(a, -1, 0, g, da);
        dfs(b, -1, 0, g, db);
        dfs(c, -1, 0, g, dc);

        long best = Long.MAX_VALUE;
        int bestNode = -1;
        for (int i = 0; i < n; i++) {
            long sum = da[i] + db[i] + dc[i];
            if (sum < best) { best = sum; bestNode = i; }
        }
        return bestNode;
    }

    // Simple demo
    public static void main(String[] args) {
        // Tree:
        // 0-1(3), 1-2(2), 1-3(4), 3-4(1)
        int n = 5;
        List<int[]> edges = Arrays.asList(
            new int[]{0,1,3},
            new int[]{1,2,2},
            new int[]{1,3,4},
            new int[]{3,4,1}
        );
        int a = 0, b = 2, c = 4;
        int ans = bestNodeMinSum(n, edges, a, b, c);
        System.out.println("Best node (3-DFS): " + ans);

        // LCA uses unweighted structure; supply the edges without weights to build the tree.
        List<int[]> unweightedEdges = Arrays.asList(
            new int[]{0,1,0},
            new int[]{1,2,0},
            new int[]{1,3,0},
            new int[]{3,4,0}
        );
        int ans2 = bestNodeByLCA(n, unweightedEdges, a, b, c, 0);
        System.out.println("Best node (LCA): " + ans2);
    }
}


