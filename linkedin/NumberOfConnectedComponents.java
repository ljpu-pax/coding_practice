import java.util.*;

/**
 * LeetCode 323. Number of Connected Components in an Undirected Graph
 *
 * Problem:
 * Given n nodes (labeled 0 to n-1) and a list of undirected edges,
 * count the number of connected components.
 *
 * Example 1:
 * n = 5, edges = [[0,1], [1,2], [3,4]]
 * Output: 2
 * Explanation: 0-1-2 is one component, 3-4 is another
 *
 * Example 2:
 * n = 5, edges = [[0,1], [1,2], [2,3], [3,4]]
 * Output: 1
 * Explanation: All nodes are connected
 *
 * Time Complexity: O(V + E) for DFS/BFS, O(E * α(V)) for Union-Find
 * Space Complexity: O(V + E) for DFS/BFS, O(V) for Union-Find
 */

class NumberOfConnectedComponents {

    /**
     * Solution 1: DFS
     * Time: O(V + E)
     * Space: O(V + E) for adjacency list + recursion stack
     */
    public int countComponentsDFS(int n, int[][] edges) {
        // Build adjacency list
        List<List<Integer>> graph = buildGraph(n, edges);

        boolean[] visited = new boolean[n];
        int components = 0;

        // Visit each unvisited node
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfs(i, graph, visited);
                components++;
            }
        }

        return components;
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
     * Solution 2: BFS
     * Time: O(V + E)
     * Space: O(V + E)
     */
    public int countComponentsBFS(int n, int[][] edges) {
        List<List<Integer>> graph = buildGraph(n, edges);
        boolean[] visited = new boolean[n];
        int components = 0;

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                bfs(i, graph, visited);
                components++;
            }
        }

        return components;
    }

    private void bfs(int start, List<List<Integer>> graph, boolean[] visited) {
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(start);
        visited[start] = true;

        while (!queue.isEmpty()) {
            int node = queue.poll();

            for (int neighbor : graph.get(node)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.offer(neighbor);
                }
            }
        }
    }

    /**
     * Solution 3: Union-Find (Best!)
     * Time: O(E * α(V)) ≈ O(E) where α is inverse Ackermann function
     * Space: O(V)
     *
     * Best for: Dynamic connectivity, multiple queries
     */
    public int countComponentsUnionFind(int n, int[][] edges) {
        UnionFind uf = new UnionFind(n);

        // Union connected nodes
        for (int[] edge : edges) {
            uf.union(edge[0], edge[1]);
        }

        return uf.getCount();
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
 * Union-Find (Disjoint Set Union) Data Structure
 *
 * Supports:
 * - find(x): Find root of x with path compression
 * - union(x, y): Union two sets by rank
 * - getCount(): Get number of disjoint sets
 */
class UnionFind {
    private int[] parent;
    private int[] rank;
    private int count; // Number of components

    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        count = n;

        // Initially, each node is its own parent
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 1;
        }
    }

    /**
     * Find with path compression
     * Time: O(α(n)) amortized
     */
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // Path compression
        }
        return parent[x];
    }

    /**
     * Union by rank
     * Returns true if union successful, false if already connected
     * Time: O(α(n)) amortized
     */
    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);

        if (rootX == rootY) {
            return false; // Already in same component
        }

        // Union by rank: attach smaller tree under larger tree
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

    public boolean isConnected(int x, int y) {
        return find(x) == find(y);
    }
}

/**
 * Test Cases
 */
class NumberOfConnectedComponentsTest {
    public static void main(String[] args) {
        NumberOfConnectedComponents solution = new NumberOfConnectedComponents();

        // Test 1: 2 components
        int n1 = 5;
        int[][] edges1 = {{0,1}, {1,2}, {3,4}};
        System.out.println("Test 1: n=" + n1 + ", edges=" + Arrays.deepToString(edges1));
        System.out.println("DFS: " + solution.countComponentsDFS(n1, edges1));
        System.out.println("BFS: " + solution.countComponentsBFS(n1, edges1));
        System.out.println("Union-Find: " + solution.countComponentsUnionFind(n1, edges1));
        System.out.println("Expected: 2\n");

        // Test 2: 1 component
        int n2 = 5;
        int[][] edges2 = {{0,1}, {1,2}, {2,3}, {3,4}};
        System.out.println("Test 2: n=" + n2 + ", edges=" + Arrays.deepToString(edges2));
        System.out.println("Union-Find: " + solution.countComponentsUnionFind(n2, edges2));
        System.out.println("Expected: 1\n");

        // Test 3: No edges (all isolated nodes)
        int n3 = 5;
        int[][] edges3 = {};
        System.out.println("Test 3: n=" + n3 + ", edges=" + Arrays.deepToString(edges3));
        System.out.println("Union-Find: " + solution.countComponentsUnionFind(n3, edges3));
        System.out.println("Expected: 5\n");

        // Test 4: Single node
        int n4 = 1;
        int[][] edges4 = {};
        System.out.println("Test 4: n=" + n4 + ", edges=" + Arrays.deepToString(edges4));
        System.out.println("Union-Find: " + solution.countComponentsUnionFind(n4, edges4));
        System.out.println("Expected: 1\n");
    }
}

/**
 * Complexity Analysis:
 * ===================
 *
 * DFS:
 * - Time: O(V + E)
 *   - Build graph: O(E)
 *   - Visit all nodes: O(V)
 *   - Visit all edges: O(E)
 * - Space: O(V + E)
 *   - Adjacency list: O(V + E)
 *   - Visited array: O(V)
 *   - Recursion stack: O(V) worst case
 *
 * BFS:
 * - Time: O(V + E)
 * - Space: O(V + E)
 *   - Same as DFS, but queue instead of recursion stack
 *
 * Union-Find: ⭐ BEST
 * - Time: O(E * α(V)) ≈ O(E)
 *   - α(V) is inverse Ackermann, practically constant
 * - Space: O(V)
 *   - Only parent and rank arrays
 * - Best for: Multiple queries, dynamic graphs
 *
 *
 * Interview Tips:
 * ==============
 *
 * 1. Approach Selection:
 *    - Start with DFS (easiest to explain)
 *    - Mention Union-Find as optimization
 *    - BFS is equivalent to DFS for this problem
 *
 * 2. Key Insights:
 *    - Each unvisited node starts a new component
 *    - Union-Find naturally tracks component count
 *    - Path compression makes Union-Find nearly O(1)
 *
 * 3. Edge Cases:
 *    - Empty graph (n nodes, 0 edges) → n components
 *    - Single node → 1 component
 *    - Fully connected → 1 component
 *    - Self-loops (if allowed)
 *
 * 4. Follow-up Questions:
 *    - Dynamic edges (add/remove)? → Union-Find
 *    - Need to list nodes in each component? → DFS/BFS
 *    - Directed graph? → Use Kosaraju's or Tarjan's algorithm
 *
 * 5. Code Organization:
 *    - Separate graph building from traversal
 *    - Union-Find as reusable class
 *    - Clear helper methods
 */
