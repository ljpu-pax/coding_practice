import java.util.*;

/**
 * LeetCode 778: Swim in Rising Water (Hard)
 *
 * You are given an n x n integer matrix grid where each value grid[i][j] represents the elevation at that point (i, j).
 *
 * The rain starts to fall. At time t, the depth of the water everywhere is t. You can swim from a square to another
 * 4-directionally adjacent square if and only if the elevation of both squares individually are at most t.
 * You can swim infinite distances in zero time. Of course, you must stay within the boundaries of the grid during your swim.
 *
 * Return the least time until you can reach the bottom right square (n - 1, n - 1) if you start at the top left square (0, 0).
 *
 * Example 1:
 * Input: grid = [[0,2],[1,3]]
 * Output: 3
 * Explanation:
 * At time 0, you are in grid location (0, 0).
 * You cannot go anywhere else because 4-directionally adjacent neighbors have a higher elevation than t = 0.
 * You cannot reach point (1, 1) until time 3.
 * When the depth of water is 3, we can swim anywhere inside the grid.
 *
 * Example 2:
 * Input: grid = [[0,1,2,3,4],[24,23,22,21,5],[12,13,14,15,16],[11,17,18,19,20],[10,9,8,7,6]]
 * Output: 16
 * Explanation: The final route is shown.
 * We need to wait until time 16 so that (0, 0) and (4, 4) are connected.
 *
 * Constraints:
 * - n == grid.length
 * - n == grid[i].length
 * - 1 <= n <= 50
 * - 0 <= grid[i][j] < n^2
 * - Each value grid[i][j] is unique.
 */
public class SwimInRisingWater {

    /**
     * Approach 1: Dijkstra's Algorithm (Modified)
     *
     * Key Insight:
     * - This is a shortest path problem where the "cost" is the maximum elevation along the path
     * - Use priority queue to always explore the path with minimum maximum elevation
     * - Similar to finding the path where the bottleneck (max elevation) is minimized
     *
     * Time Complexity: O(n^2 * log(n^2)) = O(n^2 * log(n))
     * Space Complexity: O(n^2)
     */
    public int swimInWater(int[][] grid) {
        int n = grid.length;

        // Priority queue: [row, col, max_elevation_so_far]
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[2] - b[2]);
        boolean[][] visited = new boolean[n][n];

        // Start from (0, 0)
        pq.offer(new int[]{0, 0, grid[0][0]});

        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int row = current[0];
            int col = current[1];
            int maxElevation = current[2];

            // If we reached the destination
            if (row == n - 1 && col == n - 1) {
                return maxElevation;
            }

            // Skip if already visited
            if (visited[row][col]) {
                continue;
            }
            visited[row][col] = true;

            // Explore neighbors
            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow >= 0 && newRow < n && newCol >= 0 && newCol < n &&
                    !visited[newRow][newCol]) {
                    // The time needed is the max of current max and the new cell's elevation
                    int newMaxElevation = Math.max(maxElevation, grid[newRow][newCol]);
                    pq.offer(new int[]{newRow, newCol, newMaxElevation});
                }
            }
        }

        return -1; // Should never reach here
    }

    /**
     * Approach 2: Binary Search + BFS/DFS
     *
     * Key Insight:
     * - Binary search on the answer (time t)
     * - For each candidate time t, check if we can reach destination using BFS/DFS
     * - If we can reach with time t, try smaller t; otherwise try larger t
     *
     * Time Complexity: O(n^2 * log(n^2)) = O(n^2 * log(n))
     * Space Complexity: O(n^2)
     */
    public int swimInWaterBinarySearch(int[][] grid) {
        int n = grid.length;
        int left = grid[0][0];
        int right = n * n - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (canReach(grid, mid)) {
                right = mid;  // Try smaller time
            } else {
                left = mid + 1;  // Need more time
            }
        }

        return left;
    }

    /**
     * Check if we can reach from (0,0) to (n-1, n-1) with maximum elevation <= maxTime
     */
    private boolean canReach(int[][] grid, int maxTime) {
        int n = grid.length;

        // Can't even start
        if (grid[0][0] > maxTime) {
            return false;
        }

        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[n][n];

        queue.offer(new int[]{0, 0});
        visited[0][0] = true;

        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int row = current[0];
            int col = current[1];

            // Reached destination
            if (row == n - 1 && col == n - 1) {
                return true;
            }

            // Explore neighbors
            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow >= 0 && newRow < n && newCol >= 0 && newCol < n &&
                    !visited[newRow][newCol] && grid[newRow][newCol] <= maxTime) {
                    visited[newRow][newCol] = true;
                    queue.offer(new int[]{newRow, newCol});
                }
            }
        }

        return false;
    }

    /**
     * Approach 3: Union-Find
     *
     * Key Insight:
     * - Sort all cells by elevation
     * - Add cells one by one in increasing order of elevation
     * - Use Union-Find to connect adjacent cells
     * - Stop when (0,0) and (n-1,n-1) are connected
     *
     * Time Complexity: O(n^2 * log(n^2)) = O(n^2 * log(n))
     * Space Complexity: O(n^2)
     */
    public int swimInWaterUnionFind(int[][] grid) {
        int n = grid.length;

        // Create list of cells with their elevations
        List<int[]> cells = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                cells.add(new int[]{i, j, grid[i][j]});
            }
        }

        // Sort by elevation
        Collections.sort(cells, (a, b) -> a[2] - b[2]);

        UnionFind uf = new UnionFind(n * n);
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        boolean[][] added = new boolean[n][n];

        for (int[] cell : cells) {
            int row = cell[0];
            int col = cell[1];
            int elevation = cell[2];

            added[row][col] = true;
            int currentId = row * n + col;

            // Connect with adjacent cells that are already added
            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow >= 0 && newRow < n && newCol >= 0 && newCol < n &&
                    added[newRow][newCol]) {
                    int neighborId = newRow * n + newCol;
                    uf.union(currentId, neighborId);
                }
            }

            // Check if start and end are connected
            if (uf.find(0) == uf.find(n * n - 1)) {
                return elevation;
            }
        }

        return -1;
    }

    /**
     * Union-Find (Disjoint Set Union) data structure
     */
    static class UnionFind {
        private int[] parent;
        private int[] rank;

        public UnionFind(int size) {
            parent = new int[size];
            rank = new int[size];
            for (int i = 0; i < size; i++) {
                parent[i] = i;
                rank[i] = 1;
            }
        }

        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);  // Path compression
            }
            return parent[x];
        }

        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX != rootY) {
                // Union by rank
                if (rank[rootX] > rank[rootY]) {
                    parent[rootY] = rootX;
                } else if (rank[rootX] < rank[rootY]) {
                    parent[rootX] = rootY;
                } else {
                    parent[rootY] = rootX;
                    rank[rootX]++;
                }
            }
        }
    }

    // ==================== Test Cases ====================

    public static void main(String[] args) {
        SwimInRisingWater solution = new SwimInRisingWater();

        System.out.println("=== LeetCode 778: Swim in Rising Water ===\n");

        // Test 1: Simple 2x2 grid
        System.out.println("Test 1: Simple 2x2 grid");
        int[][] grid1 = {{0, 2}, {1, 3}};
        System.out.println("Grid:");
        printGrid(grid1);
        System.out.println("Result (Dijkstra): " + solution.swimInWater(grid1));
        System.out.println("Result (Binary Search): " + solution.swimInWaterBinarySearch(grid1));
        System.out.println("Result (Union-Find): " + solution.swimInWaterUnionFind(grid1));
        System.out.println("Expected: 3\n");

        // Test 2: 5x5 grid
        System.out.println("Test 2: 5x5 grid");
        int[][] grid2 = {
            {0, 1, 2, 3, 4},
            {24, 23, 22, 21, 5},
            {12, 13, 14, 15, 16},
            {11, 17, 18, 19, 20},
            {10, 9, 8, 7, 6}
        };
        System.out.println("Grid:");
        printGrid(grid2);
        System.out.println("Result (Dijkstra): " + solution.swimInWater(grid2));
        System.out.println("Result (Binary Search): " + solution.swimInWaterBinarySearch(grid2));
        System.out.println("Result (Union-Find): " + solution.swimInWaterUnionFind(grid2));
        System.out.println("Expected: 16\n");

        // Test 3: 3x3 grid
        System.out.println("Test 3: 3x3 grid");
        int[][] grid3 = {
            {0, 3, 6},
            {1, 4, 7},
            {2, 5, 8}
        };
        System.out.println("Grid:");
        printGrid(grid3);
        System.out.println("Result (Dijkstra): " + solution.swimInWater(grid3));
        System.out.println("Result (Binary Search): " + solution.swimInWaterBinarySearch(grid3));
        System.out.println("Result (Union-Find): " + solution.swimInWaterUnionFind(grid3));
        System.out.println("Expected: 8\n");

        // Test 4: Single cell
        System.out.println("Test 4: Single cell");
        int[][] grid4 = {{0}};
        System.out.println("Grid:");
        printGrid(grid4);
        System.out.println("Result: " + solution.swimInWater(grid4));
        System.out.println("Expected: 0\n");

        // Test 5: 3x3 with high barrier
        System.out.println("Test 5: 3x3 with high barrier");
        int[][] grid5 = {
            {0, 1, 2},
            {8, 7, 6},
            {3, 4, 5}
        };
        System.out.println("Grid:");
        printGrid(grid5);
        System.out.println("Result (Dijkstra): " + solution.swimInWater(grid5));
        System.out.println("Result (Binary Search): " + solution.swimInWaterBinarySearch(grid5));
        System.out.println("Result (Union-Find): " + solution.swimInWaterUnionFind(grid5));
        System.out.println("Expected: 6\n");
    }

    private static void printGrid(int[][] grid) {
        for (int[] row : grid) {
            System.out.println(Arrays.toString(row));
        }
    }
}
