import java.util.*;

/**
 * LeetCode 261. Graph Valid Tree
 *
 * Problem:
 * Given n nodes (labeled 0 to n-1) and a list of undirected edges,
 * determine if these edges make up a valid tree.
 *
 * Tree Properties:
 * 1. Exactly n-1 edges (necessary condition)
 * 2. All nodes are connected (1 component)
 * 3. No cycles
 *
 * Note: If any two properties are satisfied, the third is automatically true.
 *
 * Example 1:
 * n = 5, edges = [[0,1], [0,2], [0,3], [1,4]]
 * Output: true
 *
 * Example 2:
 * n = 5, edges = [[0,1], [1,2], [2,3], [1,3], [1,4]]
 * Output: false (has cycle: 1-2-3-1)
 *
 * Time Complexity: O(V + E) for DFS/BFS, O(E * α(V)) for Union-Find
 * Space Complexity: O(V + E) for DFS/BFS, O(V) for Union-Find
 */

class GraphValidTree {

    /**
     * Solution 1: DFS with Cycle Detection
     * Time: O(V + E)
     * Space: O(V + E)
     *
     * Check for:
     * 1. n-1 edges
     * 2. No cycles (using parent tracking)
     * 3. All nodes visited (connected)
     */
    public boolean validTreeDFS(int n, int[][] edges) {
        // Tree must have exactly n-1 edges
        if (edges.length != n - 1) {
            return false;
        }

        // Build adjacency list
        List<List<Integer>> graph = buildGraph(n, edges);
        boolean[] visited = new boolean[n];

        // Check for cycles starting from node 0
        if (hasCycle(0, -1, graph, visited)) {
            return false;
        }

        // Check if all nodes are visited (connected)
        for (boolean v : visited) {
            if (!v) {
                return false;
            }
        }

        return true;
    }

    /**
     * DFS with parent tracking to detect cycles
     */
    private boolean hasCycle(int node, int parent, List<List<Integer>> graph, boolean[] visited) {
        visited[node] = true;

        for (int neighbor : graph.get(node)) {
            if (!visited[neighbor]) {
                // Continue DFS
                if (hasCycle(neighbor, node, graph, visited)) {
                    return true;
                }
            } else if (neighbor != parent) {
                // Visited neighbor that's not parent means cycle
                return true;
            }
        }

        return false;
    }

    /**
     * Solution 2: Union-Find (Optimal!)
     * Time: O(E * α(V)) ≈ O(E)
     * Space: O(V)
     *
     * Key insight:
     * - If union returns false, there's a cycle
     * - After all unions, check if count == 1
     */
    public boolean validTreeUnionFind(int n, int[][] edges) {
        // Tree must have exactly n-1 edges
        if (edges.length != n - 1) {
            return false;
        }

        UnionFind uf = new UnionFind(n);

        for (int[] edge : edges) {
            // If already connected, adding this edge creates a cycle
            if (!uf.union(edge[0], edge[1])) {
                return false;
            }
        }

        // All nodes should be in one component
        return uf.getCount() == 1;
    }

    /**
     * Solution 3: Simple Edge Count + Connectivity (Cleanest!)
     * Time: O(V + E)
     * Space: O(V + E)
     *
     * Key insight:
     * If edges.length == n-1 AND graph is connected, it's a tree!
     * (No need to explicitly check for cycles)
     */
    public boolean validTreeSimple(int n, int[][] edges) {
        // Tree must have exactly n-1 edges
        if (edges.length != n - 1) {
            return false;
        }

        // Build graph
        List<List<Integer>> graph = buildGraph(n, edges);
        boolean[] visited = new boolean[n];

        // DFS from node 0
        dfs(0, graph, visited);

        // Check if all nodes visited (connected)
        for (boolean v : visited) {
            if (!v) {
                return false;
            }
        }

        return true;
    }

    private void dfs(int node, List<List<Integer>> graph, boolean[] visited) {
        visited[node] = true;

        for (int neighbor : graph.get(node)) {
            if (!visited[neighbor]) {
                dfs(neighbor, graph, visited);
            }
        }
    }

    /**
     * Solution 4: BFS
     * Time: O(V + E)
     * Space: O(V + E)
     */
    public boolean validTreeBFS(int n, int[][] edges) {
        if (edges.length != n - 1) {
            return false;
        }

        List<List<Integer>> graph = buildGraph(n, edges);
        boolean[] visited = new boolean[n];

        // BFS from node 0
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(0);
        visited[0] = true;
        int visitedCount = 1;

        while (!queue.isEmpty()) {
            int node = queue.poll();

            for (int neighbor : graph.get(node)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.offer(neighbor);
                    visitedCount++;
                }
            }
        }

        // Check if all nodes visited
        return visitedCount == n;
    }

    /**
     * Helper: Build adjacency list
     */
    private List<List<Integer>> buildGraph(int n, int[][] edges) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]);
            graph.get(edge[1]).add(edge[0]);
        }

        return graph;
    }
}

/**
 * Union-Find Data Structure
 */
class UnionFind {
    private int[] parent;
    private int[] rank;
    private int count;

    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        count = n;

        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 1;
        }
    }

    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);

        if (rootX == rootY) {
            return false; // Cycle detected
        }

        if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }

        count--;
        return true;
    }

    public int getCount() {
        return count;
    }
}

/**
 * Test Cases
 */
class GraphValidTreeTest {
    public static void main(String[] args) {
        GraphValidTree solution = new GraphValidTree();

        // Test 1: Valid tree
        int n1 = 5;
        int[][] edges1 = {{0,1}, {0,2}, {0,3}, {1,4}};
        System.out.println("Test 1: n=" + n1 + ", edges=" + Arrays.deepToString(edges1));
        System.out.println("DFS: " + solution.validTreeDFS(n1, edges1));
        System.out.println("Union-Find: " + solution.validTreeUnionFind(n1, edges1));
        System.out.println("Simple: " + solution.validTreeSimple(n1, edges1));
        System.out.println("BFS: " + solution.validTreeBFS(n1, edges1));
        System.out.println("Expected: true\n");

        // Test 2: Has cycle
        int n2 = 5;
        int[][] edges2 = {{0,1}, {1,2}, {2,3}, {1,3}, {1,4}};
        System.out.println("Test 2 (cycle): n=" + n2 + ", edges=" + Arrays.deepToString(edges2));
        System.out.println("Union-Find: " + solution.validTreeUnionFind(n2, edges2));
        System.out.println("Expected: false (too many edges: 5 instead of 4)\n");

        // Test 3: Disconnected
        int n3 = 5;
        int[][] edges3 = {{0,1}, {2,3}};
        System.out.println("Test 3 (disconnected): n=" + n3 + ", edges=" + Arrays.deepToString(edges3));
        System.out.println("Union-Find: " + solution.validTreeUnionFind(n3, edges3));
        System.out.println("Expected: false (too few edges: 2 instead of 4)\n");

        // Test 4: Single node
        int n4 = 1;
        int[][] edges4 = {};
        System.out.println("Test 4 (single node): n=" + n4 + ", edges=" + Arrays.deepToString(edges4));
        System.out.println("Union-Find: " + solution.validTreeUnionFind(n4, edges4));
        System.out.println("Expected: true\n");

        // Test 5: Two nodes connected
        int n5 = 2;
        int[][] edges5 = {{0,1}};
        System.out.println("Test 5: n=" + n5 + ", edges=" + Arrays.deepToString(edges5));
        System.out.println("Union-Find: " + solution.validTreeUnionFind(n5, edges5));
        System.out.println("Expected: true\n");

        // Test 6: Two nodes, two edges (cycle)
        int n6 = 2;
        int[][] edges6 = {{0,1}, {1,0}};
        System.out.println("Test 6 (duplicate edge): n=" + n6 + ", edges=" + Arrays.deepToString(edges6));
        System.out.println("Union-Find: " + solution.validTreeUnionFind(n6, edges6));
        System.out.println("Expected: false (too many edges: 2 instead of 1)\n");
    }
}

/**
 * Complexity Analysis:
 * ===================
 *
 * Solution 1 (DFS with Cycle Detection):
 * - Time: O(V + E)
 * - Space: O(V + E)
 * - Pros: Explicitly checks for cycles
 * - Cons: More complex logic
 *
 * Solution 2 (Union-Find): ⭐ BEST
 * - Time: O(E * α(V)) ≈ O(E)
 * - Space: O(V)
 * - Pros: Clean, efficient, natural cycle detection
 * - Cons: Requires understanding Union-Find
 *
 * Solution 3 (Simple Edge Count + DFS): ⭐ CLEANEST
 * - Time: O(V + E)
 * - Space: O(V + E)
 * - Pros: Simplest logic, easy to understand
 * - Cons: Relies on mathematical property
 *
 * Solution 4 (BFS):
 * - Time: O(V + E)
 * - Space: O(V + E)
 * - Pros: Iterative, easy to understand
 * - Cons: Same complexity as DFS
 *
 *
 * Interview Tips:
 * ==============
 *
 * 1. State Tree Properties First:
 *    "A tree with n nodes must have:"
 *    - Exactly n-1 edges
 *    - Be connected (1 component)
 *    - Have no cycles
 *
 * 2. Quick Win:
 *    "Check edges.length == n-1 first"
 *    "If not, immediately return false"
 *
 * 3. Recommended Approach:
 *    Start with: "If edges == n-1 and connected, it's a tree"
 *    Then: Implement simple DFS connectivity check
 *    Optimize: Mention Union-Find for follow-up
 *
 * 4. Why n-1 Edges is Sufficient:
 *    "With n-1 edges and connectivity, cycles are impossible"
 *    "Because a cycle requires at least n edges in n nodes"
 *
 * 5. Edge Cases:
 *    - Single node (n=1, edges=[]) → true
 *    - Two nodes, one edge → true
 *    - Two nodes, two edges → false (duplicate/cycle)
 *    - Disconnected graph → false
 *    - Too many edges → false (cycle guaranteed)
 *
 * 6. Follow-up Questions:
 *    - Directed graph? → Check for single root, DAG
 *    - Weighted edges? → Same algorithm, weights don't matter
 *    - Dynamic edge addition? → Use Union-Find
 *    - Find the cycle? → DFS with path tracking
 *
 * 7. Common Mistakes:
 *    - Forgetting to check edge count first
 *    - Not handling parent in cycle detection
 *    - Assuming all nodes are visited without checking
 */
