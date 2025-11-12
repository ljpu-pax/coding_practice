import java.util.*;

/**
 * LeetCode 463: Island Perimeter
 *
 * You are given row x col grid representing a map where grid[i][j] = 1 represents land and grid[i][j] = 0
 * represents water. Grid cells are connected horizontally/vertically (not diagonally).
 *
 * The grid is completely surrounded by water, and there is exactly one island (i.e., one or more connected
 * land cells). The island doesn't have "lakes", meaning the water inside isn't connected to the water around
 * the island. One cell is a square with side length 1. The grid is rectangular, width and height don't exceed 100.
 * Determine the perimeter of the island.
 *
 * Example 1:
 * Input: grid = [[0,1,0,0],[1,1,1,0],[0,1,0,0],[1,1,0,0]]
 * Output: 16
 *
 * Example 2:
 * Input: grid = [[1]]
 * Output: 4
 *
 * Example 3:
 * Input: grid = [[1,0]]
 * Output: 4
 */
class IslandPerimeter {
    /**
     * Approach 1: Count edges (Simple iteration)
     *
     * For each land cell, count how many of its 4 edges touch water or boundary.
     *
     * Time: O(m * n)
     * Space: O(1)
     */
    public int islandPerimeter(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        int perimeter = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    // Check all 4 directions
                    if (i == 0 || grid[i - 1][j] == 0) perimeter++; // top
                    if (i == m - 1 || grid[i + 1][j] == 0) perimeter++; // bottom
                    if (j == 0 || grid[i][j - 1] == 0) perimeter++; // left
                    if (j == n - 1 || grid[i][j + 1] == 0) perimeter++; // right
                }
            }
        }

        return perimeter;
    }

    /**
     * Approach 2: DFS
     *
     * Use DFS to traverse the island and count perimeter as we go.
     *
     * Time: O(m * n)
     * Space: O(m * n) for recursion stack
     */
    public int islandPerimeterDFS(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    return dfs(grid, i, j);
                }
            }
        }

        return 0;
    }

    private int dfs(int[][] grid, int i, int j) {
        int m = grid.length;
        int n = grid[0].length;

        // Out of bounds or water - contributes 1 to perimeter
        if (i < 0 || i >= m || j < 0 || j >= n || grid[i][j] == 0) {
            return 1;
        }

        // Already visited
        if (grid[i][j] == -1) {
            return 0;
        }

        // Mark as visited
        grid[i][j] = -1;

        // Count perimeter from all 4 directions
        int perimeter = 0;
        perimeter += dfs(grid, i + 1, j);
        perimeter += dfs(grid, i - 1, j);
        perimeter += dfs(grid, i, j + 1);
        perimeter += dfs(grid, i, j - 1);

        return perimeter;
    }

    /**
     * Approach 3: BFS
     *
     * Use BFS to traverse the island and count perimeter.
     *
     * Time: O(m * n)
     * Space: O(m * n)
     */
    public int islandPerimeterBFS(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        int perimeter = 0;

        // Find starting point
        Queue<int[]> queue = new LinkedList<>();
        boolean found = false;
        for (int i = 0; i < m && !found; i++) {
            for (int j = 0; j < n && !found; j++) {
                if (grid[i][j] == 1) {
                    queue.offer(new int[]{i, j});
                    grid[i][j] = -1; // mark as visited
                    found = true;
                }
            }
        }

        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int i = curr[0];
            int j = curr[1];

            for (int[] dir : directions) {
                int ni = i + dir[0];
                int nj = j + dir[1];

                // Out of bounds or water - add to perimeter
                if (ni < 0 || ni >= m || nj < 0 || nj >= n || grid[ni][nj] == 0) {
                    perimeter++;
                } else if (grid[ni][nj] == 1) {
                    // Unvisited land
                    grid[ni][nj] = -1;
                    queue.offer(new int[]{ni, nj});
                }
            }
        }

        return perimeter;
    }

    /**
     * Approach 4: Count land cells and shared edges
     *
     * Perimeter = 4 * land_cells - 2 * shared_edges
     *
     * Time: O(m * n)
     * Space: O(1)
     */
    public int islandPerimeterMath(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        int lands = 0;
        int neighbors = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    lands++;
                    // Count neighbors (only check right and down to avoid double counting)
                    if (i < m - 1 && grid[i + 1][j] == 1) neighbors++;
                    if (j < n - 1 && grid[i][j + 1] == 1) neighbors++;
                }
            }
        }

        return 4 * lands - 2 * neighbors;
    }
}

/**
 * Maximum Island Perimeter
 *
 * Given a grid, find the island with the maximum perimeter.
 * Multiple islands may exist in the grid.
 *
 * Example:
 * Input: grid = [
 *   [0,1,0,0,1],
 *   [1,1,1,0,1],
 *   [0,1,0,0,0],
 *   [0,0,0,1,1]
 * ]
 * Output: 12 (the left island has perimeter 12)
 */
class MaxIslandPerimeter {
    /**
     * Approach: DFS to find all islands and track max perimeter
     *
     * Time: O(m * n)
     * Space: O(m * n)
     */
    public int maxIslandPerimeter(int[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }

        int m = grid.length;
        int n = grid[0].length;
        int maxPerimeter = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    int perimeter = dfs(grid, i, j);
                    maxPerimeter = Math.max(maxPerimeter, perimeter);
                }
            }
        }

        return maxPerimeter;
    }

    private int dfs(int[][] grid, int i, int j) {
        int m = grid.length;
        int n = grid[0].length;

        // Out of bounds or water - contributes 1 to perimeter
        if (i < 0 || i >= m || j < 0 || j >= n || grid[i][j] == 0) {
            return 1;
        }

        // Already visited
        if (grid[i][j] == -1) {
            return 0;
        }

        // Mark as visited
        grid[i][j] = -1;

        // Count perimeter from all 4 directions
        int perimeter = 0;
        perimeter += dfs(grid, i + 1, j);
        perimeter += dfs(grid, i - 1, j);
        perimeter += dfs(grid, i, j + 1);
        perimeter += dfs(grid, i, j - 1);

        return perimeter;
    }

    /**
     * BFS approach for max island perimeter
     */
    public int maxIslandPerimeterBFS(int[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }

        int m = grid.length;
        int n = grid[0].length;
        int maxPerimeter = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    int perimeter = bfs(grid, i, j);
                    maxPerimeter = Math.max(maxPerimeter, perimeter);
                }
            }
        }

        return maxPerimeter;
    }

    private int bfs(int[][] grid, int startI, int startJ) {
        int m = grid.length;
        int n = grid[0].length;
        int perimeter = 0;

        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{startI, startJ});
        grid[startI][startJ] = -1;

        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int i = curr[0];
            int j = curr[1];

            for (int[] dir : directions) {
                int ni = i + dir[0];
                int nj = j + dir[1];

                if (ni < 0 || ni >= m || nj < 0 || nj >= n || grid[ni][nj] == 0) {
                    perimeter++;
                } else if (grid[ni][nj] == 1) {
                    grid[ni][nj] = -1;
                    queue.offer(new int[]{ni, nj});
                }
            }
        }

        return perimeter;
    }
}

/**
 * Follow-up: Making Largest Island Perimeter (Similar to LeetCode 827)
 *
 * You are allowed to change at most one 0 to be 1. What is the maximum perimeter
 * of an island you can achieve?
 *
 * Approach: Union Find with perimeter tracking
 * 1. For each island, calculate its perimeter and assign an ID
 * 2. For each water cell (0), check what happens if we flip it to land
 * 3. Calculate the new perimeter after merging with adjacent islands
 */
class MakingLargestIslandPerimeter {
    /**
     * Approach 1: Brute Force (for small grids)
     *
     * Try flipping each water cell and calculate resulting max perimeter.
     *
     * Time: O(m * n * m * n) - too slow for large grids
     * Space: O(m * n)
     */
    public int largestIslandPerimeter(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        int maxPerimeter = 0;

        // First, find current max perimeter
        int[][] gridCopy = copyGrid(grid);
        MaxIslandPerimeter solver = new MaxIslandPerimeter();
        maxPerimeter = solver.maxIslandPerimeter(gridCopy);

        // Try flipping each water cell
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 0) {
                    // Flip to land
                    int[][] testGrid = copyGrid(grid);
                    testGrid[i][j] = 1;

                    // Calculate new max perimeter
                    int newPerimeter = solver.maxIslandPerimeter(testGrid);
                    maxPerimeter = Math.max(maxPerimeter, newPerimeter);
                }
            }
        }

        return maxPerimeter;
    }

    /**
     * Approach 2: Optimized with Island Labeling and Perimeter Tracking
     *
     * 1. Label each island with unique ID and store its perimeter
     * 2. For each water cell, calculate new perimeter if we flip it
     *
     * Time: O(m * n)
     * Space: O(m * n)
     */
    public int largestIslandPerimeterOptimized(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;

        // Step 1: Label islands and calculate their perimeters
        Map<Integer, Integer> islandPerimeter = new HashMap<>();
        int islandId = 2; // Start from 2 (0 = water, 1 = unlabeled land)

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    int perimeter = dfsLabelAndCalculate(grid, i, j, islandId);
                    islandPerimeter.put(islandId, perimeter);
                    islandId++;
                }
            }
        }

        // Current max perimeter
        int maxPerimeter = 0;
        for (int p : islandPerimeter.values()) {
            maxPerimeter = Math.max(maxPerimeter, p);
        }

        // Step 2: Try flipping each water cell
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 0) {
                    Set<Integer> neighborIslands = new HashSet<>();
                    int exposedEdges = 4; // Flipping this cell adds these edges initially

                    for (int[] dir : directions) {
                        int ni = i + dir[0];
                        int nj = j + dir[1];

                        if (ni >= 0 && ni < m && nj >= 0 && nj < n && grid[ni][nj] > 1) {
                            neighborIslands.add(grid[ni][nj]);
                            exposedEdges--; // One less exposed edge
                        }
                    }

                    // Calculate new perimeter after flipping
                    int newPerimeter = exposedEdges;
                    for (int id : neighborIslands) {
                        // Each merged island loses 2 edges (shared border)
                        newPerimeter += islandPerimeter.get(id);
                    }
                    // Subtract shared edges between merged islands
                    newPerimeter -= 2 * (neighborIslands.size());

                    maxPerimeter = Math.max(maxPerimeter, newPerimeter);
                }
            }
        }

        return maxPerimeter;
    }

    private int dfsLabelAndCalculate(int[][] grid, int i, int j, int islandId) {
        int m = grid.length;
        int n = grid[0].length;

        if (i < 0 || i >= m || j < 0 || j >= n || grid[i][j] == 0) {
            return 1; // Water or boundary contributes to perimeter
        }

        if (grid[i][j] != 1) {
            return 0; // Already labeled or different island
        }

        grid[i][j] = islandId; // Label the cell

        int perimeter = 0;
        perimeter += dfsLabelAndCalculate(grid, i + 1, j, islandId);
        perimeter += dfsLabelAndCalculate(grid, i - 1, j, islandId);
        perimeter += dfsLabelAndCalculate(grid, i, j + 1, islandId);
        perimeter += dfsLabelAndCalculate(grid, i, j - 1, islandId);

        return perimeter;
    }

    private int[][] copyGrid(int[][] grid) {
        int[][] copy = new int[grid.length][];
        for (int i = 0; i < grid.length; i++) {
            copy[i] = grid[i].clone();
        }
        return copy;
    }
}

/**
 * Test cases
 */
class IslandPerimeterTest {
    public static void main(String[] args) {
        testBasicPerimeter();
        testMaxPerimeter();
        testLargestIslandPerimeter();
    }

    private static void testBasicPerimeter() {
        System.out.println("=== Testing LeetCode 463: Island Perimeter ===\n");
        IslandPerimeter solution = new IslandPerimeter();

        // Test 1
        int[][] grid1 = {
            {0, 1, 0, 0},
            {1, 1, 1, 0},
            {0, 1, 0, 0},
            {1, 1, 0, 0}
        };
        testPerimeterCase(solution, grid1, 16, "Test 1");

        // Test 2
        int[][] grid2 = {{1}};
        testPerimeterCase(solution, grid2, 4, "Test 2");

        // Test 3
        int[][] grid3 = {{1, 0}};
        testPerimeterCase(solution, grid3, 4, "Test 3");
    }

    private static void testPerimeterCase(IslandPerimeter solution, int[][] grid, int expected, String testName) {
        System.out.println(testName + ":");

        int[][] gridCopy1 = copyGrid(grid);
        int[][] gridCopy2 = copyGrid(grid);
        int[][] gridCopy3 = copyGrid(grid);
        int[][] gridCopy4 = copyGrid(grid);

        int result1 = solution.islandPerimeter(gridCopy1);
        int result2 = solution.islandPerimeterDFS(gridCopy2);
        int result3 = solution.islandPerimeterBFS(gridCopy3);
        int result4 = solution.islandPerimeterMath(gridCopy4);

        System.out.println("  Simple: " + result1 + " (Expected: " + expected + ") - " +
                          (result1 == expected ? "PASS" : "FAIL"));
        System.out.println("  DFS: " + result2 + " (Expected: " + expected + ") - " +
                          (result2 == expected ? "PASS" : "FAIL"));
        System.out.println("  BFS: " + result3 + " (Expected: " + expected + ") - " +
                          (result3 == expected ? "PASS" : "FAIL"));
        System.out.println("  Math: " + result4 + " (Expected: " + expected + ") - " +
                          (result4 == expected ? "PASS" : "FAIL"));
        System.out.println();
    }

    private static void testMaxPerimeter() {
        System.out.println("=== Testing Maximum Island Perimeter ===\n");
        MaxIslandPerimeter solution = new MaxIslandPerimeter();

        // Test 1: Multiple islands
        int[][] grid1 = {
            {0, 1, 0, 0, 1},
            {1, 1, 1, 0, 1},
            {0, 1, 0, 0, 0},
            {0, 0, 0, 1, 1}
        };

        int[][] gridCopy1 = copyGrid(grid1);
        int[][] gridCopy2 = copyGrid(grid1);

        int result1 = solution.maxIslandPerimeter(gridCopy1);
        int result2 = solution.maxIslandPerimeterBFS(gridCopy2);

        System.out.println("Test 1: Multiple islands");
        System.out.println("  DFS: " + result1);
        System.out.println("  BFS: " + result2);
        System.out.println();
    }

    private static void testLargestIslandPerimeter() {
        System.out.println("=== Testing Follow-up: Making Largest Island Perimeter ===\n");
        MakingLargestIslandPerimeter solution = new MakingLargestIslandPerimeter();

        int[][] grid1 = {
            {1, 0},
            {0, 1}
        };

        int result1 = solution.largestIslandPerimeter(copyGrid(grid1));
        System.out.println("Test 1: Small grid");
        System.out.println("  Result: " + result1);
        System.out.println();
    }

    private static int[][] copyGrid(int[][] grid) {
        int[][] copy = new int[grid.length][];
        for (int i = 0; i < grid.length; i++) {
            copy[i] = grid[i].clone();
        }
        return copy;
    }
}
