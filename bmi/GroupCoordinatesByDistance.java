import java.util.*;

/**
 * Applied Intuition Interview Question
 *
 * Problem: Group coordinates by distance threshold
 *
 * Given:
 * - Array of coordinates (2D points)
 * - Integer K (distance threshold)
 *
 * Task: Group coordinates where points are in the same group if they are
 * connected through a chain of points within distance K
 *
 * Example:
 * coordinates = [(1,2), (1,4), (1,3), (4,5)], K = 1
 * - (1,2) and (1,3): distance = 1 ✓
 * - (1,3) and (1,4): distance = 1 ✓
 * - (1,2) and (1,4): distance = 2, but connected through (1,3)
 * - (4,5): no connection to others
 * Output: [[(1,2), (1,3), (1,4)], [(4,5)]]
 *
 * This is a graph connectivity problem:
 * - Points are nodes
 * - Edges exist if distance ≤ K
 * - Find all connected components
 */
class GroupCoordinatesByDistance {

    static class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    /**
     * Approach 1: Union-Find (Disjoint Set Union)
     *
     * Time: O(N² × α(N)) where α is inverse Ackermann function (nearly constant)
     * Space: O(N)
     *
     * Best approach for this problem!
     */
    public List<List<Point>> groupCoordinatesUnionFind(Point[] points, int k) {
        int n = points.length;
        UnionFind uf = new UnionFind(n);

        // Connect points within distance K
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (distance(points[i], points[j]) <= k) {
                    uf.union(i, j);
                }
            }
        }

        // Group points by their root parent
        Map<Integer, List<Point>> groups = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int root = uf.find(i);
            groups.putIfAbsent(root, new ArrayList<>());
            groups.get(root).add(points[i]);
        }

        return new ArrayList<>(groups.values());
    }

    /**
     * Union-Find data structure
     */
    static class UnionFind {
        int[] parent;
        int[] rank;

        UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                rank[i] = 1;
            }
        }

        int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]); // Path compression
            }
            return parent[x];
        }

        void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX != rootY) {
                // Union by rank
                if (rank[rootX] < rank[rootY]) {
                    parent[rootX] = rootY;
                } else if (rank[rootX] > rank[rootY]) {
                    parent[rootY] = rootX;
                } else {
                    parent[rootY] = rootX;
                    rank[rootX]++;
                }
            }
        }
    }

    /**
     * Approach 2: DFS (Depth-First Search)
     *
     * Time: O(N²)
     * Space: O(N²) for adjacency list + O(N) for recursion
     */
    public List<List<Point>> groupCoordinatesDFS(Point[] points, int k) {
        int n = points.length;
        List<List<Point>> result = new ArrayList<>();
        boolean[] visited = new boolean[n];

        // Build adjacency list
        List<Integer>[] graph = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            graph[i] = new ArrayList<>();
        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (distance(points[i], points[j]) <= k) {
                    graph[i].add(j);
                    graph[j].add(i);
                }
            }
        }

        // DFS to find connected components
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                List<Point> group = new ArrayList<>();
                dfs(i, points, graph, visited, group);
                result.add(group);
            }
        }

        return result;
    }

    private void dfs(int node, Point[] points, List<Integer>[] graph,
                     boolean[] visited, List<Point> group) {
        visited[node] = true;
        group.add(points[node]);

        for (int neighbor : graph[node]) {
            if (!visited[neighbor]) {
                dfs(neighbor, points, graph, visited, group);
            }
        }
    }

    /**
     * Approach 3: BFS (Breadth-First Search)
     *
     * Time: O(N²)
     * Space: O(N²)
     */
    public List<List<Point>> groupCoordinatesBFS(Point[] points, int k) {
        int n = points.length;
        List<List<Point>> result = new ArrayList<>();
        boolean[] visited = new boolean[n];

        // Build adjacency list
        List<Integer>[] graph = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            graph[i] = new ArrayList<>();
        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (distance(points[i], points[j]) <= k) {
                    graph[i].add(j);
                    graph[j].add(i);
                }
            }
        }

        // BFS to find connected components
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                List<Point> group = new ArrayList<>();
                Queue<Integer> queue = new LinkedList<>();
                queue.offer(i);
                visited[i] = true;

                while (!queue.isEmpty()) {
                    int node = queue.poll();
                    group.add(points[node]);

                    for (int neighbor : graph[node]) {
                        if (!visited[neighbor]) {
                            visited[neighbor] = true;
                            queue.offer(neighbor);
                        }
                    }
                }

                result.add(group);
            }
        }

        return result;
    }

    /**
     * Approach 4: Optimized with spatial indexing (for large datasets)
     *
     * Use grid/quadtree to avoid checking all pairs
     * Time: O(N × M) where M = average points per grid cell
     * Space: O(N)
     */
    public List<List<Point>> groupCoordinatesOptimized(Point[] points, int k) {
        int n = points.length;
        UnionFind uf = new UnionFind(n);

        // Grid-based spatial indexing
        Map<String, List<Integer>> grid = new HashMap<>();

        for (int i = 0; i < n; i++) {
            Point p = points[i];
            // Place point in grid cell
            int cellX = p.x / k;
            int cellY = p.y / k;

            // Check current cell and adjacent cells (3x3 grid)
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    String cellKey = (cellX + dx) + "," + (cellY + dy);
                    if (grid.containsKey(cellKey)) {
                        for (int j : grid.get(cellKey)) {
                            if (distance(points[i], points[j]) <= k) {
                                uf.union(i, j);
                            }
                        }
                    }
                }
            }

            // Add current point to grid
            String key = cellX + "," + cellY;
            grid.putIfAbsent(key, new ArrayList<>());
            grid.get(key).add(i);
        }

        // Group points by root
        Map<Integer, List<Point>> groups = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int root = uf.find(i);
            groups.putIfAbsent(root, new ArrayList<>());
            groups.get(root).add(points[i]);
        }

        return new ArrayList<>(groups.values());
    }

    /**
     * Calculate Manhattan distance between two points
     * Note: Can use Euclidean distance if needed
     */
    private double distance(Point p1, Point p2) {
        // Manhattan distance
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);

        // Euclidean distance (uncomment if needed)
        // int dx = p1.x - p2.x;
        // int dy = p1.y - p2.y;
        // return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Euclidean distance version
     */
    private double euclideanDistance(Point p1, Point p2) {
        int dx = p1.x - p2.x;
        int dy = p1.y - p2.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}

/**
 * Variations and extensions
 */
class GroupCoordinatesVariations {

    /**
     * Variation 1: Return number of groups instead of actual groups
     */
    public int countGroups(GroupCoordinatesByDistance.Point[] points, int k) {
        int n = points.length;
        GroupCoordinatesByDistance.UnionFind uf = new GroupCoordinatesByDistance.UnionFind(n);

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (distance(points[i], points[j]) <= k) {
                    uf.union(i, j);
                }
            }
        }

        // Count unique roots
        Set<Integer> uniqueRoots = new HashSet<>();
        for (int i = 0; i < n; i++) {
            uniqueRoots.add(uf.find(i));
        }

        return uniqueRoots.size();
    }

    /**
     * Variation 2: Find largest group
     */
    public List<GroupCoordinatesByDistance.Point> findLargestGroup(
            GroupCoordinatesByDistance.Point[] points, int k) {

        GroupCoordinatesByDistance solver = new GroupCoordinatesByDistance();
        List<List<GroupCoordinatesByDistance.Point>> groups =
            solver.groupCoordinatesUnionFind(points, k);

        List<GroupCoordinatesByDistance.Point> largest = new ArrayList<>();
        for (List<GroupCoordinatesByDistance.Point> group : groups) {
            if (group.size() > largest.size()) {
                largest = group;
            }
        }

        return largest;
    }

    /**
     * Variation 3: Group with different distance metric (Chebyshev distance)
     */
    public List<List<GroupCoordinatesByDistance.Point>> groupWithChebyshevDistance(
            GroupCoordinatesByDistance.Point[] points, int k) {

        int n = points.length;
        GroupCoordinatesByDistance.UnionFind uf = new GroupCoordinatesByDistance.UnionFind(n);

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (chebyshevDistance(points[i], points[j]) <= k) {
                    uf.union(i, j);
                }
            }
        }

        Map<Integer, List<GroupCoordinatesByDistance.Point>> groups = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int root = uf.find(i);
            groups.putIfAbsent(root, new ArrayList<>());
            groups.get(root).add(points[i]);
        }

        return new ArrayList<>(groups.values());
    }

    private double distance(GroupCoordinatesByDistance.Point p1,
                           GroupCoordinatesByDistance.Point p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }

    private int chebyshevDistance(GroupCoordinatesByDistance.Point p1,
                                  GroupCoordinatesByDistance.Point p2) {
        return Math.max(Math.abs(p1.x - p2.x), Math.abs(p1.y - p2.y));
    }
}

/**
 * Test cases
 */
class GroupCoordinatesByDistanceTest {
    public static void main(String[] args) {
        testBasicExample();
        testAllApproaches();
        testEdgeCases();
        testVariations();
    }

    private static void testBasicExample() {
        System.out.println("=== Testing Basic Example ===\n");
        GroupCoordinatesByDistance solver = new GroupCoordinatesByDistance();

        GroupCoordinatesByDistance.Point[] points = {
            new GroupCoordinatesByDistance.Point(1, 2),
            new GroupCoordinatesByDistance.Point(1, 4),
            new GroupCoordinatesByDistance.Point(1, 3),
            new GroupCoordinatesByDistance.Point(4, 5)
        };
        int k = 1;

        List<List<GroupCoordinatesByDistance.Point>> result =
            solver.groupCoordinatesUnionFind(points, k);

        System.out.println("Input: [(1,2), (1,4), (1,3), (4,5)], K = " + k);
        System.out.println("Output:");
        for (List<GroupCoordinatesByDistance.Point> group : result) {
            System.out.println("  " + group);
        }
        System.out.println("Expected: [(1,2), (1,3), (1,4)] and [(4,5)]");
        System.out.println();
    }

    private static void testAllApproaches() {
        System.out.println("=== Testing All Approaches ===\n");
        GroupCoordinatesByDistance solver = new GroupCoordinatesByDistance();

        GroupCoordinatesByDistance.Point[] points = {
            new GroupCoordinatesByDistance.Point(0, 0),
            new GroupCoordinatesByDistance.Point(1, 1),
            new GroupCoordinatesByDistance.Point(5, 5),
            new GroupCoordinatesByDistance.Point(6, 6),
            new GroupCoordinatesByDistance.Point(10, 10)
        };
        int k = 2;

        System.out.println("Input points: " + Arrays.toString(points));
        System.out.println("K = " + k + "\n");

        List<List<GroupCoordinatesByDistance.Point>> result1 =
            solver.groupCoordinatesUnionFind(points, k);
        System.out.println("Union-Find: " + result1);

        List<List<GroupCoordinatesByDistance.Point>> result2 =
            solver.groupCoordinatesDFS(points, k);
        System.out.println("DFS: " + result2);

        List<List<GroupCoordinatesByDistance.Point>> result3 =
            solver.groupCoordinatesBFS(points, k);
        System.out.println("BFS: " + result3);

        List<List<GroupCoordinatesByDistance.Point>> result4 =
            solver.groupCoordinatesOptimized(points, k);
        System.out.println("Optimized (Grid): " + result4);
        System.out.println();
    }

    private static void testEdgeCases() {
        System.out.println("=== Testing Edge Cases ===\n");
        GroupCoordinatesByDistance solver = new GroupCoordinatesByDistance();

        // Test 1: All points in one group
        GroupCoordinatesByDistance.Point[] points1 = {
            new GroupCoordinatesByDistance.Point(0, 0),
            new GroupCoordinatesByDistance.Point(1, 0),
            new GroupCoordinatesByDistance.Point(2, 0),
            new GroupCoordinatesByDistance.Point(3, 0)
        };
        List<List<GroupCoordinatesByDistance.Point>> result1 =
            solver.groupCoordinatesUnionFind(points1, 1);
        System.out.println("Test 1: All connected");
        System.out.println("  Groups: " + result1.size() + " (expected: 1)");
        System.out.println();

        // Test 2: All points separate
        GroupCoordinatesByDistance.Point[] points2 = {
            new GroupCoordinatesByDistance.Point(0, 0),
            new GroupCoordinatesByDistance.Point(10, 10),
            new GroupCoordinatesByDistance.Point(20, 20),
            new GroupCoordinatesByDistance.Point(30, 30)
        };
        List<List<GroupCoordinatesByDistance.Point>> result2 =
            solver.groupCoordinatesUnionFind(points2, 1);
        System.out.println("Test 2: All separate");
        System.out.println("  Groups: " + result2.size() + " (expected: 4)");
        System.out.println();

        // Test 3: Single point
        GroupCoordinatesByDistance.Point[] points3 = {
            new GroupCoordinatesByDistance.Point(5, 5)
        };
        List<List<GroupCoordinatesByDistance.Point>> result3 =
            solver.groupCoordinatesUnionFind(points3, 1);
        System.out.println("Test 3: Single point");
        System.out.println("  Groups: " + result3);
        System.out.println();
    }

    private static void testVariations() {
        System.out.println("=== Testing Variations ===\n");
        GroupCoordinatesVariations variations = new GroupCoordinatesVariations();

        GroupCoordinatesByDistance.Point[] points = {
            new GroupCoordinatesByDistance.Point(1, 2),
            new GroupCoordinatesByDistance.Point(1, 4),
            new GroupCoordinatesByDistance.Point(1, 3),
            new GroupCoordinatesByDistance.Point(4, 5)
        };
        int k = 1;

        int count = variations.countGroups(points, k);
        System.out.println("Number of groups: " + count);

        List<GroupCoordinatesByDistance.Point> largest = variations.findLargestGroup(points, k);
        System.out.println("Largest group: " + largest);
        System.out.println();
    }
}

/**
 * Complexity Analysis and Comparison
 */
class ComplexityComparison {
    /*
     * Time Complexity Comparison:
     *
     * 1. Union-Find: O(N² × α(N))
     *    - Building edges: O(N²) comparisons
     *    - Union operations: O(α(N)) per operation (nearly constant)
     *    - Best for most cases
     *
     * 2. DFS/BFS: O(N² + N + E) = O(N²)
     *    - Building graph: O(N²)
     *    - Traversal: O(N + E) where E ≤ N²
     *    - Similar performance to Union-Find
     *
     * 3. Optimized (Grid): O(N × M)
     *    - M = average points per grid cell
     *    - Best for uniformly distributed points
     *    - Can be much faster than O(N²) when K is small
     *
     * Space Complexity:
     *
     * 1. Union-Find: O(N)
     * 2. DFS/BFS: O(N²) for adjacency list
     * 3. Optimized: O(N) for grid + union-find
     *
     * Recommendation:
     * - Small N (< 1000): Union-Find or DFS/BFS
     * - Large N, small K: Grid-based optimization
     * - Need simple implementation: DFS
     * - Best performance: Union-Find
     */
}
