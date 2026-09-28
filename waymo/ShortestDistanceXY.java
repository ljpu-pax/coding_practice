import java.util.*;

/**
 * Find Shortest Distance Between 'X' and 'Y' in 2D Grid
 *
 * Given a 2D grid containing 'X', 'Y', and obstacles, find the shortest distance
 * between any 'X' and any 'Y'.
 *
 * Grid values:
 * - 'X': Source points
 * - 'Y': Target points
 * - 'O': Obstacle (cannot pass through)
 * - '.': Empty cell (can pass through)
 *
 * Example 1:
 * Input: grid = [
 *   ['X', '.', '.', 'Y'],
 *   ['.', 'O', '.', '.'],
 *   ['.', '.', '.', 'Y']
 * ]
 * Output: 3 (from X at (0,0) to Y at (0,3))
 *
 * Example 2:
 * Input: grid = [
 *   ['X', 'O', 'Y'],
 *   ['.', 'O', '.'],
 *   ['.', '.', '.']
 * ]
 * Output: 5 (must go around the obstacle)
 */
public class ShortestDistanceXY {

    /**
     * Approach 1: Multi-source BFS from all 'X' positions
     * Time Complexity: O(m * n) where m = rows, n = cols
     * Space Complexity: O(m * n) for the queue and visited array
     */
    public int shortestDistance(char[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return -1;
        }

        int m = grid.length;
        int n = grid[0].length;

        // Find all 'X' positions and add to queue
        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 'X') {
                    queue.offer(new int[]{i, j, 0}); // row, col, distance
                    visited[i][j] = true;
                }
            }
        }

        // BFS to find nearest 'Y'
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int row = current[0];
            int col = current[1];
            int dist = current[2];

            // Check if we found a 'Y'
            if (grid[row][col] == 'Y') {
                return dist;
            }

            // Explore neighbors
            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow >= 0 && newRow < m && newCol >= 0 && newCol < n &&
                    !visited[newRow][newCol] && grid[newRow][newCol] != 'O') {
                    visited[newRow][newCol] = true;
                    queue.offer(new int[]{newRow, newCol, dist + 1});
                }
            }
        }

        return -1; // No path found
    }

    /**
     * Approach 2: Bidirectional BFS (more efficient for large grids)
     * Start BFS from both 'X' and 'Y' positions simultaneously
     * Time Complexity: O(m * n)
     * Space Complexity: O(m * n)
     */
    public int shortestDistanceBidirectional(char[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return -1;
        }

        int m = grid.length;
        int n = grid[0].length;

        // Two queues for bidirectional search
        Queue<int[]> queueX = new LinkedList<>();
        Queue<int[]> queueY = new LinkedList<>();
        int[][] distFromX = new int[m][n];
        int[][] distFromY = new int[m][n];

        // Initialize with -1 (not visited)
        for (int i = 0; i < m; i++) {
            Arrays.fill(distFromX[i], -1);
            Arrays.fill(distFromY[i], -1);
        }

        // Add all 'X' and 'Y' positions
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 'X') {
                    queueX.offer(new int[]{i, j});
                    distFromX[i][j] = 0;
                } else if (grid[i][j] == 'Y') {
                    queueY.offer(new int[]{i, j});
                    distFromY[i][j] = 0;
                }
            }
        }

        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        // BFS from all X positions
        while (!queueX.isEmpty()) {
            int[] current = queueX.poll();
            int row = current[0];
            int col = current[1];

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow >= 0 && newRow < m && newCol >= 0 && newCol < n &&
                    distFromX[newRow][newCol] == -1 && grid[newRow][newCol] != 'O') {
                    distFromX[newRow][newCol] = distFromX[row][col] + 1;
                    queueX.offer(new int[]{newRow, newCol});
                }
            }
        }

        // BFS from all Y positions
        while (!queueY.isEmpty()) {
            int[] current = queueY.poll();
            int row = current[0];
            int col = current[1];

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow >= 0 && newRow < m && newCol >= 0 && newCol < n &&
                    distFromY[newRow][newCol] == -1 && grid[newRow][newCol] != 'O') {
                    distFromY[newRow][newCol] = distFromY[row][col] + 1;
                    queueY.offer(new int[]{newRow, newCol});
                }
            }
        }

        // Find minimum sum of distances
        int minDistance = Integer.MAX_VALUE;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (distFromX[i][j] != -1 && distFromY[i][j] != -1) {
                    minDistance = Math.min(minDistance, distFromX[i][j] + distFromY[i][j]);
                }
            }
        }

        return minDistance == Integer.MAX_VALUE ? -1 : minDistance;
    }

    /**
     * Approach 3: Find closest pair directly
     * Calculate distance between each X and Y pair, return minimum
     * Only efficient when number of X's and Y's is small
     */
    public int shortestDistanceBruteForce(char[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return -1;
        }

        List<int[]> xPositions = new ArrayList<>();
        List<int[]> yPositions = new ArrayList<>();

        // Collect all X and Y positions
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 'X') {
                    xPositions.add(new int[]{i, j});
                } else if (grid[i][j] == 'Y') {
                    yPositions.add(new int[]{i, j});
                }
            }
        }

        int minDistance = Integer.MAX_VALUE;

        // For each X, find shortest path to any Y using BFS
        for (int[] xPos : xPositions) {
            int dist = bfs(grid, xPos[0], xPos[1]);
            if (dist != -1) {
                minDistance = Math.min(minDistance, dist);
            }
        }

        return minDistance == Integer.MAX_VALUE ? -1 : minDistance;
    }

    private int bfs(char[][] grid, int startRow, int startCol) {
        int m = grid.length;
        int n = grid[0].length;
        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[m][n];

        queue.offer(new int[]{startRow, startCol, 0});
        visited[startRow][startCol] = true;

        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int row = current[0];
            int col = current[1];
            int dist = current[2];

            if (grid[row][col] == 'Y') {
                return dist;
            }

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow >= 0 && newRow < m && newCol >= 0 && newCol < n &&
                    !visited[newRow][newCol] && grid[newRow][newCol] != 'O') {
                    visited[newRow][newCol] = true;
                    queue.offer(new int[]{newRow, newCol, dist + 1});
                }
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        ShortestDistanceXY solution = new ShortestDistanceXY();

        // Test case 1: Simple path
        char[][] grid1 = {
            {'X', '.', '.', 'Y'},
            {'.', 'O', '.', '.'},
            {'.', '.', '.', 'Y'}
        };
        System.out.println("Test 1: " + solution.shortestDistance(grid1));
        // Expected: 3

        // Test case 2: Path around obstacle
        char[][] grid2 = {
            {'X', 'O', 'Y'},
            {'.', 'O', '.'},
            {'.', '.', '.'}
        };
        System.out.println("Test 2: " + solution.shortestDistance(grid2));
        // Expected: 5

        // Test case 3: Multiple X and Y
        char[][] grid3 = {
            {'X', '.', '.', '.'},
            {'.', 'O', 'O', '.'},
            {'.', '.', '.', 'Y'},
            {'X', '.', '.', 'Y'}
        };
        System.out.println("Test 3: " + solution.shortestDistance(grid3));
        // Expected: 2 (from X at (3,0) to Y at (3,3))

        // Test case 4: No path (completely blocked)
        char[][] grid4 = {
            {'X', 'O', 'Y'},
            {'.', 'O', '.'},
            {'.', 'O', '.'}
        };
        System.out.println("Test 4: " + solution.shortestDistance(grid4));
        // Expected: -1

        // Test case 5: Direct neighbors
        char[][] grid5 = {
            {'X', 'Y'},
            {'.', '.'}
        };
        System.out.println("Test 5: " + solution.shortestDistance(grid5));
        // Expected: 1

        System.out.println("\nTesting bidirectional approach:");
        System.out.println("Test 1: " + solution.shortestDistanceBidirectional(grid1));
        System.out.println("Test 3: " + solution.shortestDistanceBidirectional(grid3));
    }
}
