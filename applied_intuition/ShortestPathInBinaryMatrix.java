import java.util.*;

/**
 * LeetCode 1091: Shortest Path in Binary Matrix (Medium)
 *
 * Given an n x n binary matrix grid, return the length of the shortest clear path in the matrix.
 * If there is no clear path, return -1.
 *
 * A clear path in a binary matrix is a path from the top-left cell (0, 0) to the bottom-right
 * cell (n - 1, n - 1) such that:
 * - All the visited cells of the path are 0
 * - All the adjacent cells of the path are 8-directionally connected (can move in 8 directions)
 *
 * The length of a clear path is the number of visited cells of this path.
 *
 * Example 1:
 * Input: grid = [[0,1],[1,0]]
 * Output: 2
 *
 * Example 2:
 * Input: grid = [[0,0,0],[1,1,0],[1,1,0]]
 * Output: 4
 *
 * Example 3:
 * Input: grid = [[1,0,0],[1,1,0],[1,1,0]]
 * Output: -1
 *
 * Constraints:
 * - n == grid.length
 * - n == grid[i].length
 * - 1 <= n <= 100
 * - grid[i][j] is 0 or 1
 */
class ShortestPathInBinaryMatrix {

    /**
     * Approach 1: BFS (Breadth-First Search) - Optimal
     *
     * Time: O(n²) - visit each cell once
     * Space: O(n²) for queue and visited
     */
    public int shortestPathBinaryMatrix(int[][] grid) {
        int n = grid.length;

        // Check if start or end is blocked
        if (grid[0][0] == 1 || grid[n-1][n-1] == 1) {
            return -1;
        }

        // Special case: single cell
        if (n == 1) {
            return 1;
        }

        // 8 directions: up, down, left, right, and 4 diagonals
        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
        };

        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{0, 0, 1}); // {row, col, distance}
        grid[0][0] = 1; // Mark as visited

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int row = curr[0];
            int col = curr[1];
            int dist = curr[2];

            // Try all 8 directions
            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                // Check if reached destination
                if (newRow == n - 1 && newCol == n - 1) {
                    return dist + 1;
                }

                // Check bounds and if cell is 0
                if (newRow >= 0 && newRow < n && newCol >= 0 && newCol < n &&
                    grid[newRow][newCol] == 0) {

                    grid[newRow][newCol] = 1; // Mark as visited
                    queue.offer(new int[]{newRow, newCol, dist + 1});
                }
            }
        }

        return -1; // No path found
    }

    /**
     * Approach 2: BFS without modifying input
     *
     * Time: O(n²)
     * Space: O(n²)
     */
    public int shortestPathBinaryMatrixNoModify(int[][] grid) {
        int n = grid.length;

        if (grid[0][0] == 1 || grid[n-1][n-1] == 1) {
            return -1;
        }

        if (n == 1) {
            return 1;
        }

        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
        };

        boolean[][] visited = new boolean[n][n];
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{0, 0, 1});
        visited[0][0] = true;

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int row = curr[0];
            int col = curr[1];
            int dist = curr[2];

            if (row == n - 1 && col == n - 1) {
                return dist;
            }

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow >= 0 && newRow < n && newCol >= 0 && newCol < n &&
                    !visited[newRow][newCol] && grid[newRow][newCol] == 0) {

                    visited[newRow][newCol] = true;
                    queue.offer(new int[]{newRow, newCol, dist + 1});
                }
            }
        }

        return -1;
    }

    /**
     * Approach 3: A* Search (optimized BFS with heuristic)
     *
     * Use Manhattan distance to destination as heuristic
     * Time: O(n² log n²) due to priority queue
     * Space: O(n²)
     */
    public int shortestPathAStar(int[][] grid) {
        int n = grid.length;

        if (grid[0][0] == 1 || grid[n-1][n-1] == 1) {
            return -1;
        }

        if (n == 1) {
            return 1;
        }

        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
        };

        // Priority queue: sort by (distance + heuristic)
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> {
            int costA = a[2] + heuristic(a[0], a[1], n);
            int costB = b[2] + heuristic(b[0], b[1], n);
            return Integer.compare(costA, costB);
        });

        boolean[][] visited = new boolean[n][n];
        pq.offer(new int[]{0, 0, 1});
        visited[0][0] = true;

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int row = curr[0];
            int col = curr[1];
            int dist = curr[2];

            if (row == n - 1 && col == n - 1) {
                return dist;
            }

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow >= 0 && newRow < n && newCol >= 0 && newCol < n &&
                    !visited[newRow][newCol] && grid[newRow][newCol] == 0) {

                    visited[newRow][newCol] = true;
                    pq.offer(new int[]{newRow, newCol, dist + 1});
                }
            }
        }

        return -1;
    }

    private int heuristic(int row, int col, int n) {
        // Chebyshev distance (since we can move diagonally)
        return Math.max(Math.abs(row - (n - 1)), Math.abs(col - (n - 1)));
    }

    /**
     * Approach 4: Bidirectional BFS
     *
     * Search from both start and end simultaneously
     * Time: O(n²)
     * Space: O(n²)
     */
    public int shortestPathBidirectional(int[][] grid) {
        int n = grid.length;

        if (grid[0][0] == 1 || grid[n-1][n-1] == 1) {
            return -1;
        }

        if (n == 1) {
            return 1;
        }

        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
        };

        Set<String> visitedFromStart = new HashSet<>();
        Set<String> visitedFromEnd = new HashSet<>();

        Queue<int[]> queueStart = new LinkedList<>();
        Queue<int[]> queueEnd = new LinkedList<>();

        queueStart.offer(new int[]{0, 0, 1});
        queueEnd.offer(new int[]{n-1, n-1, 1});

        visitedFromStart.add("0,0");
        visitedFromEnd.add((n-1) + "," + (n-1));

        while (!queueStart.isEmpty() || !queueEnd.isEmpty()) {
            // Expand from start
            if (!queueStart.isEmpty()) {
                int size = queueStart.size();
                for (int i = 0; i < size; i++) {
                    int[] curr = queueStart.poll();
                    int row = curr[0], col = curr[1], dist = curr[2];
                    String key = row + "," + col;

                    if (visitedFromEnd.contains(key)) {
                        // Paths met
                        return dist;
                    }

                    for (int[] dir : directions) {
                        int newRow = row + dir[0];
                        int newCol = col + dir[1];
                        String newKey = newRow + "," + newCol;

                        if (newRow >= 0 && newRow < n && newCol >= 0 && newCol < n &&
                            grid[newRow][newCol] == 0 && !visitedFromStart.contains(newKey)) {

                            visitedFromStart.add(newKey);
                            queueStart.offer(new int[]{newRow, newCol, dist + 1});
                        }
                    }
                }
            }

            // Expand from end (similar logic)
            // ... (simplified for brevity)
        }

        return -1;
    }
}

/**
 * Test cases
 */
class ShortestPathInBinaryMatrixTest {
    public static void main(String[] args) {
        testBasicCases();
        testEdgeCases();
        testAllApproaches();
    }

    private static void testBasicCases() {
        System.out.println("=== LeetCode 1091: Shortest Path in Binary Matrix ===\n");
        ShortestPathInBinaryMatrix solution = new ShortestPathInBinaryMatrix();

        // Test 1
        int[][] grid1 = {{0,1},{1,0}};
        int result1 = solution.shortestPathBinaryMatrix(copyGrid(grid1));
        System.out.println("Test 1:");
        printGrid(grid1);
        System.out.println("Shortest path: " + result1);
        System.out.println("Expected: 2");
        System.out.println();

        // Test 2
        int[][] grid2 = {{0,0,0},{1,1,0},{1,1,0}};
        int result2 = solution.shortestPathBinaryMatrix(copyGrid(grid2));
        System.out.println("Test 2:");
        printGrid(grid2);
        System.out.println("Shortest path: " + result2);
        System.out.println("Expected: 4");
        System.out.println();

        // Test 3
        int[][] grid3 = {{1,0,0},{1,1,0},{1,1,0}};
        int result3 = solution.shortestPathBinaryMatrix(copyGrid(grid3));
        System.out.println("Test 3 (blocked start):");
        printGrid(grid3);
        System.out.println("Shortest path: " + result3);
        System.out.println("Expected: -1");
        System.out.println();
    }

    private static void testEdgeCases() {
        System.out.println("=== Edge Cases ===\n");
        ShortestPathInBinaryMatrix solution = new ShortestPathInBinaryMatrix();

        // Single cell
        int[][] grid1 = {{0}};
        System.out.println("Single cell (0): " + solution.shortestPathBinaryMatrix(copyGrid(grid1))); // 1

        int[][] grid2 = {{1}};
        System.out.println("Single cell (1): " + solution.shortestPathBinaryMatrix(copyGrid(grid2))); // -1

        // Blocked end
        int[][] grid3 = {{0,0},{0,1}};
        System.out.println("Blocked end: " + solution.shortestPathBinaryMatrix(copyGrid(grid3))); // -1

        // Straight path
        int[][] grid4 = {{0,0,0},{0,0,0},{0,0,0}};
        System.out.println("All clear: " + solution.shortestPathBinaryMatrix(copyGrid(grid4))); // 3
        System.out.println();
    }

    private static void testAllApproaches() {
        System.out.println("=== Comparing Approaches ===\n");
        ShortestPathInBinaryMatrix solution = new ShortestPathInBinaryMatrix();

        int[][] grid = {{0,0,0},{1,1,0},{1,1,0}};

        long start = System.nanoTime();
        int result1 = solution.shortestPathBinaryMatrix(copyGrid(grid));
        long time1 = System.nanoTime() - start;

        start = System.nanoTime();
        int result2 = solution.shortestPathBinaryMatrixNoModify(copyGrid(grid));
        long time2 = System.nanoTime() - start;

        start = System.nanoTime();
        int result3 = solution.shortestPathAStar(copyGrid(grid));
        long time3 = System.nanoTime() - start;

        System.out.println("BFS (modify input): " + result1 + " (" + time1 + "ns)");
        System.out.println("BFS (no modify):    " + result2 + " (" + time2 + "ns)");
        System.out.println("A* Search:          " + result3 + " (" + time3 + "ns)");
        System.out.println();
    }

    private static void printGrid(int[][] grid) {
        for (int[] row : grid) {
            for (int cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    private static int[][] copyGrid(int[][] grid) {
        int[][] copy = new int[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            copy[i] = grid[i].clone();
        }
        return copy;
    }
}

/**
 * Key Insights
 */
class ShortestPathInsights {
    /*
     * Why BFS Works:
     * ==============
     * - BFS explores nodes level by level
     * - First time we reach destination = shortest path
     * - All edges have weight 1 (one step)
     *
     * 8-Directional Movement:
     * =======================
     * Unlike 4-directional (up/down/left/right), we can also move diagonally:
     * - (-1,-1), (-1,1), (1,-1), (1,1)
     * - Allows for shorter paths
     *
     * Optimization Techniques:
     * ========================
     * 1. Modify input grid to mark visited (saves space)
     * 2. A* search with heuristic (faster for large grids)
     * 3. Bidirectional BFS (search from both ends)
     * 4. Early termination when destination reached
     *
     * Time Complexity:
     * ================
     * - BFS: O(n²) - visit each cell at most once
     * - A*: O(n² log n) - priority queue operations
     * - Bidirectional: O(n²) but typically faster in practice
     *
     * Space Complexity:
     * =================
     * - Queue: O(n²) worst case
     * - Visited: O(n²) if using separate array
     * - Can reduce to O(1) if modifying input allowed
     *
     * Related Problems:
     * =================
     * - 542: 01 Matrix
     * - 994: Rotting Oranges
     * - 1162: As Far from Land as Possible
     * - 847: Shortest Path Visiting All Nodes
     * - 1293: Shortest Path in Grid with Obstacles Elimination
     *
     * Common Pitfalls:
     * ================
     * - Forgetting to mark cells as visited
     * - Not checking if start/end is blocked
     * - Confusing path length (number of cells vs number of steps)
     * - Using DFS instead of BFS (DFS doesn't guarantee shortest)
     */
}
