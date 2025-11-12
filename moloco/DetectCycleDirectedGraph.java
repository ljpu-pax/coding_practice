import java.util.*;

/**
 * Detect Cycle in Directed Graph
 *
 * Given a directed graph, determine if it contains a cycle.
 *
 * Related LeetCode Problems:
 * - LeetCode 207: Course Schedule (detect cycle in directed graph)
 * - LeetCode 210: Course Schedule II (topological sort)
 *
 * Example 1:
 * Input: n = 4, edges = [[0,1], [1,2], [2,3], [3,1]]
 * Output: true
 * Explanation: There's a cycle: 1 -> 2 -> 3 -> 1
 *
 * Example 2:
 * Input: n = 4, edges = [[0,1], [1,2], [2,3]]
 * Output: false
 * Explanation: No cycle exists
 *
 * Example 3:
 * Input: n = 2, edges = [[0,1], [1,0]]
 * Output: true
 * Explanation: Cycle between 0 and 1
 */
class DetectCycleDirectedGraph {
    /**
     * Approach 1: DFS with Three Colors (White-Gray-Black)
     *
     * Use three states:
     * - WHITE (0): Unvisited
     * - GRAY (1): Visiting (in current path)
     * - BLACK (2): Visited (finished)
     *
     * If we encounter a GRAY node, there's a cycle.
     *
     * Time: O(V + E) where V = vertices, E = edges
     * Space: O(V) for recursion stack and state array
     */
    private static final int WHITE = 0; // Unvisited
    private static final int GRAY = 1;  // Visiting
    private static final int BLACK = 2; // Visited

    public boolean hasCycle(int n, int[][] edges) {
        // Build adjacency list
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }
        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]);
        }

        int[] color = new int[n];
        Arrays.fill(color, WHITE);

        // Check each node
        for (int i = 0; i < n; i++) {
            if (color[i] == WHITE) {
                if (dfs(i, graph, color)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean dfs(int node, List<List<Integer>> graph, int[] color) {
        color[node] = GRAY; // Mark as visiting

        for (int neighbor : graph.get(node)) {
            if (color[neighbor] == GRAY) {
                // Back edge found - cycle detected
                return true;
            }
            if (color[neighbor] == WHITE) {
                if (dfs(neighbor, graph, color)) {
                    return true;
                }
            }
        }

        color[node] = BLACK; // Mark as visited
        return false;
    }

    /**
     * Approach 2: DFS with Visited and RecStack
     *
     * Use two arrays:
     * - visited[]: has this node been visited
     * - recStack[]: is this node in current recursion stack (path)
     *
     * Time: O(V + E)
     * Space: O(V)
     */
    public boolean hasCycleRecStack(int n, int[][] edges) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }
        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]);
        }

        boolean[] visited = new boolean[n];
        boolean[] recStack = new boolean[n];

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                if (dfsRecStack(i, graph, visited, recStack)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean dfsRecStack(int node, List<List<Integer>> graph,
                                boolean[] visited, boolean[] recStack) {
        visited[node] = true;
        recStack[node] = true;

        for (int neighbor : graph.get(node)) {
            if (!visited[neighbor]) {
                if (dfsRecStack(neighbor, graph, visited, recStack)) {
                    return true;
                }
            } else if (recStack[neighbor]) {
                // Node is in current path - cycle detected
                return true;
            }
        }

        recStack[node] = false; // Remove from recursion stack
        return false;
    }

    /**
     * Approach 3: Topological Sort (Kahn's Algorithm)
     *
     * If we can't create a topological sort of all nodes, there's a cycle.
     *
     * Algorithm:
     * 1. Calculate in-degree of all nodes
     * 2. Add nodes with in-degree 0 to queue
     * 3. Process queue: remove node, decrease neighbors' in-degree
     * 4. If all nodes processed, no cycle; otherwise, cycle exists
     *
     * Time: O(V + E)
     * Space: O(V)
     */
    public boolean hasCycleTopologicalSort(int n, int[][] edges) {
        List<List<Integer>> graph = new ArrayList<>();
        int[] inDegree = new int[n];

        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]);
            inDegree[edge[1]]++;
        }

        // Add nodes with in-degree 0 to queue
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }

        int processedCount = 0;

        while (!queue.isEmpty()) {
            int node = queue.poll();
            processedCount++;

            for (int neighbor : graph.get(node)) {
                inDegree[neighbor]--;
                if (inDegree[neighbor] == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        // If not all nodes processed, there's a cycle
        return processedCount != n;
    }

    /**
     * Approach 4: Union Find (Less common for directed graphs)
     *
     * Note: Union-Find works naturally for undirected graphs.
     * For directed graphs, we need a modified approach.
     *
     * This approach only works if we're checking for cycles while building the graph.
     * For a fully constructed directed graph, DFS is more appropriate.
     *
     * Time: O(E * α(V)) where α is inverse Ackermann function
     * Space: O(V)
     */
    public boolean hasCycleUnionFind(int n, int[][] edges) {
        // For each strongly connected component, use Union-Find
        // This is more complex and less efficient than DFS for cycle detection
        // Included for completeness but DFS is preferred

        int[] parent = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }

        // This simplified version only detects self-loops and immediate back edges
        // Not suitable for general cycle detection in directed graphs
        for (int[] edge : edges) {
            int from = edge[0];
            int to = edge[1];

            // If there's already an edge from 'to' to 'from', we have a cycle
            // This is a simplified check and doesn't catch all cycles
            if (find(parent, to) == from) {
                return true;
            }
        }

        return false;
    }

    private int find(int[] parent, int x) {
        if (parent[x] != x) {
            parent[x] = find(parent, parent[x]);
        }
        return parent[x];
    }
}

/**
 * Extended: Find the cycle path
 */
class FindCyclePath {
    /**
     * Return the cycle path if exists, otherwise return empty list
     */
    public List<Integer> findCyclePath(int n, int[][] edges) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }
        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]);
        }

        int[] color = new int[n];
        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        for (int i = 0; i < n; i++) {
            if (color[i] == 0) {
                List<Integer> cycle = dfs(i, graph, color, parent);
                if (!cycle.isEmpty()) {
                    return cycle;
                }
            }
        }

        return new ArrayList<>();
    }

    private List<Integer> dfs(int node, List<List<Integer>> graph,
                              int[] color, int[] parent) {
        color[node] = 1; // Gray

        for (int neighbor : graph.get(node)) {
            if (color[neighbor] == 1) {
                // Cycle found, reconstruct path
                List<Integer> cycle = new ArrayList<>();
                cycle.add(neighbor);
                int curr = node;
                while (curr != neighbor) {
                    cycle.add(curr);
                    curr = parent[curr];
                }
                cycle.add(neighbor);
                Collections.reverse(cycle);
                return cycle;
            }

            if (color[neighbor] == 0) {
                parent[neighbor] = node;
                List<Integer> cycle = dfs(neighbor, graph, color, parent);
                if (!cycle.isEmpty()) {
                    return cycle;
                }
            }
        }

        color[node] = 2; // Black
        return new ArrayList<>();
    }
}

/**
 * Related: Detect cycle in undirected graph
 */
class DetectCycleUndirectedGraph {
    /**
     * DFS approach for undirected graph
     *
     * For undirected graphs, we need to track the parent to avoid
     * false cycle detection on the edge we just came from.
     */
    public boolean hasCycle(int n, int[][] edges) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        // Build undirected graph
        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]);
            graph.get(edge[1]).add(edge[0]);
        }

        boolean[] visited = new boolean[n];

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                if (dfs(i, -1, graph, visited)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean dfs(int node, int parent, List<List<Integer>> graph,
                       boolean[] visited) {
        visited[node] = true;

        for (int neighbor : graph.get(node)) {
            if (!visited[neighbor]) {
                if (dfs(neighbor, node, graph, visited)) {
                    return true;
                }
            } else if (neighbor != parent) {
                // Visited neighbor that's not parent - cycle found
                return true;
            }
        }

        return false;
    }

    /**
     * Union-Find approach for undirected graph
     *
     * This is the natural use case for Union-Find.
     */
    public boolean hasCycleUnionFind(int n, int[][] edges) {
        int[] parent = new int[n];
        int[] rank = new int[n];

        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }

        for (int[] edge : edges) {
            int x = find(parent, edge[0]);
            int y = find(parent, edge[1]);

            if (x == y) {
                // Both nodes in same component - adding edge creates cycle
                return true;
            }

            // Union
            union(parent, rank, x, y);
        }

        return false;
    }

    private int find(int[] parent, int x) {
        if (parent[x] != x) {
            parent[x] = find(parent, parent[x]);
        }
        return parent[x];
    }

    private void union(int[] parent, int[] rank, int x, int y) {
        if (rank[x] < rank[y]) {
            parent[x] = y;
        } else if (rank[x] > rank[y]) {
            parent[y] = x;
        } else {
            parent[y] = x;
            rank[x]++;
        }
    }
}

/**
 * Test cases
 */
class DetectCycleDirectedGraphTest {
    public static void main(String[] args) {
        testDirectedGraph();
        testFindCyclePath();
        testUndirectedGraph();
    }

    private static void testDirectedGraph() {
        System.out.println("=== Testing Detect Cycle in Directed Graph ===\n");
        DetectCycleDirectedGraph solution = new DetectCycleDirectedGraph();

        // Test 1: Simple cycle
        int[][] edges1 = {{0, 1}, {1, 2}, {2, 3}, {3, 1}};
        boolean result1a = solution.hasCycle(4, edges1);
        boolean result1b = solution.hasCycleRecStack(4, edges1);
        boolean result1c = solution.hasCycleTopologicalSort(4, edges1);
        System.out.println("Test 1: Simple cycle [0->1->2->3->1]");
        System.out.println("  Three Colors: " + result1a + " (Expected: true) - " + (result1a ? "PASS" : "FAIL"));
        System.out.println("  RecStack: " + result1b + " (Expected: true) - " + (result1b ? "PASS" : "FAIL"));
        System.out.println("  Topological: " + result1c + " (Expected: true) - " + (result1c ? "PASS" : "FAIL"));
        System.out.println();

        // Test 2: No cycle
        int[][] edges2 = {{0, 1}, {1, 2}, {2, 3}};
        boolean result2a = solution.hasCycle(4, edges2);
        boolean result2b = solution.hasCycleRecStack(4, edges2);
        boolean result2c = solution.hasCycleTopologicalSort(4, edges2);
        System.out.println("Test 2: No cycle [0->1->2->3]");
        System.out.println("  Three Colors: " + result2a + " (Expected: false) - " + (!result2a ? "PASS" : "FAIL"));
        System.out.println("  RecStack: " + result2b + " (Expected: false) - " + (!result2b ? "PASS" : "FAIL"));
        System.out.println("  Topological: " + result2c + " (Expected: false) - " + (!result2c ? "PASS" : "FAIL"));
        System.out.println();

        // Test 3: Self loop
        int[][] edges3 = {{0, 0}};
        boolean result3 = solution.hasCycle(1, edges3);
        System.out.println("Test 3: Self loop [0->0]");
        System.out.println("  Result: " + result3 + " (Expected: true) - " + (result3 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 4: Two separate cycles
        int[][] edges4 = {{0, 1}, {1, 0}, {2, 3}, {3, 2}};
        boolean result4 = solution.hasCycle(4, edges4);
        System.out.println("Test 4: Two separate cycles [0->1->0] and [2->3->2]");
        System.out.println("  Result: " + result4 + " (Expected: true) - " + (result4 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 5: DAG (Directed Acyclic Graph)
        int[][] edges5 = {{0, 1}, {0, 2}, {1, 3}, {2, 3}};
        boolean result5 = solution.hasCycle(4, edges5);
        System.out.println("Test 5: DAG [0->1->3, 0->2->3]");
        System.out.println("  Result: " + result5 + " (Expected: false) - " + (!result5 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 6: Complex cycle
        int[][] edges6 = {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 2}, {1, 5}};
        boolean result6 = solution.hasCycle(6, edges6);
        System.out.println("Test 6: Complex cycle [2->3->4->2]");
        System.out.println("  Result: " + result6 + " (Expected: true) - " + (result6 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 7: Empty graph
        int[][] edges7 = {};
        boolean result7 = solution.hasCycle(3, edges7);
        System.out.println("Test 7: Empty graph (no edges)");
        System.out.println("  Result: " + result7 + " (Expected: false) - " + (!result7 ? "PASS" : "FAIL"));
        System.out.println();
    }

    private static void testFindCyclePath() {
        System.out.println("=== Testing Find Cycle Path ===\n");
        FindCyclePath finder = new FindCyclePath();

        // Test 1: Find cycle path
        int[][] edges1 = {{0, 1}, {1, 2}, {2, 3}, {3, 1}};
        List<Integer> cycle1 = finder.findCyclePath(4, edges1);
        System.out.println("Test 1: Find cycle in [0->1->2->3->1]");
        System.out.println("  Cycle path: " + cycle1 + " (Should be [1,2,3,1])");
        System.out.println();

        // Test 2: No cycle
        int[][] edges2 = {{0, 1}, {1, 2}, {2, 3}};
        List<Integer> cycle2 = finder.findCyclePath(4, edges2);
        System.out.println("Test 2: No cycle [0->1->2->3]");
        System.out.println("  Cycle path: " + cycle2 + " (Should be empty)");
        System.out.println();

        // Test 3: Multiple cycles (finds first one)
        int[][] edges3 = {{0, 1}, {1, 0}, {2, 3}, {3, 2}};
        List<Integer> cycle3 = finder.findCyclePath(4, edges3);
        System.out.println("Test 3: Multiple cycles");
        System.out.println("  First cycle found: " + cycle3);
        System.out.println();
    }

    private static void testUndirectedGraph() {
        System.out.println("=== Testing Detect Cycle in Undirected Graph ===\n");
        DetectCycleUndirectedGraph solution = new DetectCycleUndirectedGraph();

        // Test 1: Triangle (cycle)
        int[][] edges1 = {{0, 1}, {1, 2}, {2, 0}};
        boolean result1a = solution.hasCycle(3, edges1);
        boolean result1b = solution.hasCycleUnionFind(3, edges1);
        System.out.println("Test 1: Triangle [0-1-2-0]");
        System.out.println("  DFS: " + result1a + " (Expected: true) - " + (result1a ? "PASS" : "FAIL"));
        System.out.println("  Union-Find: " + result1b + " (Expected: true) - " + (result1b ? "PASS" : "FAIL"));
        System.out.println();

        // Test 2: Tree (no cycle)
        int[][] edges2 = {{0, 1}, {1, 2}, {1, 3}};
        boolean result2a = solution.hasCycle(4, edges2);
        boolean result2b = solution.hasCycleUnionFind(4, edges2);
        System.out.println("Test 2: Tree [0-1-2, 1-3]");
        System.out.println("  DFS: " + result2a + " (Expected: false) - " + (!result2a ? "PASS" : "FAIL"));
        System.out.println("  Union-Find: " + result2b + " (Expected: false) - " + (!result2b ? "PASS" : "FAIL"));
        System.out.println();

        // Test 3: Single edge (no cycle)
        int[][] edges3 = {{0, 1}};
        boolean result3 = solution.hasCycle(2, edges3);
        System.out.println("Test 3: Single edge [0-1]");
        System.out.println("  Result: " + result3 + " (Expected: false) - " + (!result3 ? "PASS" : "FAIL"));
        System.out.println();
    }
}
