import java.util.*;

/**
 * LeetCode 200: Number of Islands
 *
 * Given an m x n 2D binary grid which represents a map of '1's (land) and '0's (water),
 * return the number of islands.
 *
 * An island is surrounded by water and is formed by connecting adjacent lands horizontally or vertically.
 * You may assume all four edges of the grid are all surrounded by water.
 *
 * Example 1:
 * Input: grid = [
 *   ["1","1","1","1","0"],
 *   ["1","1","0","1","0"],
 *   ["1","1","0","0","0"],
 *   ["0","0","0","0","0"]
 * ]
 * Output: 1
 *
 * Example 2:
 * Input: grid = [
 *   ["1","1","0","0","0"],
 *   ["1","1","0","0","0"],
 *   ["0","0","1","0","0"],
 *   ["0","0","0","1","1"]
 * ]
 * Output: 3
 */
class NumberOfIslands {
    /**
     * Approach 1: DFS (Recursive)
     *
     * Time: O(m * n) - visit each cell once
     * Space: O(m * n) - recursion stack in worst case
     */
    public int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }

        int m = grid.length;
        int n = grid[0].length;
        int count = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == '1') {
                    count++;
                    dfs(grid, i, j);
                }
            }
        }

        return count;
    }

    private void dfs(char[][] grid, int i, int j) {
        int m = grid.length;
        int n = grid[0].length;

        // Base case: out of bounds or water
        if (i < 0 || i >= m || j < 0 || j >= n || grid[i][j] == '0') {
            return;
        }

        // Mark as visited by setting to '0'
        grid[i][j] = '0';

        // Explore all 4 directions
        dfs(grid, i + 1, j); // down
        dfs(grid, i - 1, j); // up
        dfs(grid, i, j + 1); // right
        dfs(grid, i, j - 1); // left
    }

    /**
     * Approach 2: BFS
     *
     * Time: O(m * n)
     * Space: O(min(m, n)) - queue size in worst case
     */
    public int numIslandsBFS(char[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }

        int m = grid.length;
        int n = grid[0].length;
        int count = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == '1') {
                    count++;
                    bfs(grid, i, j);
                }
            }
        }

        return count;
    }

    private void bfs(char[][] grid, int startI, int startJ) {
        int m = grid.length;
        int n = grid[0].length;
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{startI, startJ});
        grid[startI][startJ] = '0';

        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int i = curr[0];
            int j = curr[1];

            for (int[] dir : directions) {
                int ni = i + dir[0];
                int nj = j + dir[1];

                if (ni >= 0 && ni < m && nj >= 0 && nj < n && grid[ni][nj] == '1') {
                    grid[ni][nj] = '0';
                    queue.offer(new int[]{ni, nj});
                }
            }
        }
    }

    /**
     * Approach 3: Union Find
     *
     * Time: O(m * n * α(m*n)) where α is inverse Ackermann function
     * Space: O(m * n)
     */
    public int numIslandsUnionFind(char[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }

        int m = grid.length;
        int n = grid[0].length;
        UnionFind uf = new UnionFind(grid);

        int[][] directions = {{1, 0}, {0, 1}};

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == '1') {
                    for (int[] dir : directions) {
                        int ni = i + dir[0];
                        int nj = j + dir[1];
                        if (ni < m && nj < n && grid[ni][nj] == '1') {
                            uf.union(i * n + j, ni * n + nj);
                        }
                    }
                }
            }
        }

        return uf.getCount();
    }

    static class UnionFind {
        int[] parent;
        int[] rank;
        int count;

        public UnionFind(char[][] grid) {
            int m = grid.length;
            int n = grid[0].length;
            parent = new int[m * n];
            rank = new int[m * n];
            count = 0;

            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    if (grid[i][j] == '1') {
                        parent[i * n + j] = i * n + j;
                        count++;
                    }
                }
            }
        }

        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX != rootY) {
                if (rank[rootX] > rank[rootY]) {
                    parent[rootY] = rootX;
                } else if (rank[rootX] < rank[rootY]) {
                    parent[rootX] = rootY;
                } else {
                    parent[rootY] = rootX;
                    rank[rootX]++;
                }
                count--;
            }
        }

        public int getCount() {
            return count;
        }
    }
}

/**
 * Follow-up: Number of Distinct Island Shapes
 *
 * Given a 2D grid, count the number of DISTINCT island shapes.
 * Two islands are considered the same shape if one can be translated (shifted) to match the other.
 *
 * Example:
 * Input: grid = [
 *   [1,1,0,0,0],
 *   [1,0,0,0,0],
 *   [0,0,0,1,1],
 *   [0,0,0,1,0]
 * ]
 * Output: 2
 * Explanation: The two L-shaped islands have the same shape.
 *
 * Approach: Use path signature to represent island shape
 * - During DFS, record the path taken (directions: U, D, L, R)
 * - Normalize the path by treating the starting point as origin (0, 0)
 * - Use a set to store unique shapes
 */
class NumberOfDistinctIslands {
    /**
     * Approach 1: Path Signature with Direction Encoding
     *
     * Time: O(m * n)
     * Space: O(m * n)
     */
    public int numDistinctIslands(int[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }

        int m = grid.length;
        int n = grid[0].length;
        Set<String> uniqueShapes = new HashSet<>();

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    StringBuilder path = new StringBuilder();
                    dfs(grid, i, j, i, j, path);
                    uniqueShapes.add(path.toString());
                }
            }
        }

        return uniqueShapes.size();
    }

    private void dfs(int[][] grid, int i, int j, int originI, int originJ, StringBuilder path) {
        int m = grid.length;
        int n = grid[0].length;

        if (i < 0 || i >= m || j < 0 || j >= n || grid[i][j] == 0) {
            return;
        }

        grid[i][j] = 0; // mark as visited

        // Record relative position to origin
        path.append((i - originI)).append(",").append((j - originJ)).append(";");

        // Explore all 4 directions
        dfs(grid, i + 1, j, originI, originJ, path);
        dfs(grid, i - 1, j, originI, originJ, path);
        dfs(grid, i, j + 1, originI, originJ, path);
        dfs(grid, i, j - 1, originI, originJ, path);
    }

    /**
     * Approach 2: Normalized Coordinate Set
     *
     * Collect all coordinates of an island, normalize to (0,0) as starting point,
     * and use the sorted coordinate set as the signature.
     *
     * Time: O(m * n * log(k)) where k is average island size
     * Space: O(m * n)
     */
    public int numDistinctIslandsCoordinates(int[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }

        int m = grid.length;
        int n = grid[0].length;
        Set<String> uniqueShapes = new HashSet<>();

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    List<int[]> cells = new ArrayList<>();
                    dfsCollect(grid, i, j, cells);

                    // Normalize coordinates
                    String signature = normalize(cells);
                    uniqueShapes.add(signature);
                }
            }
        }

        return uniqueShapes.size();
    }

    private void dfsCollect(int[][] grid, int i, int j, List<int[]> cells) {
        int m = grid.length;
        int n = grid[0].length;

        if (i < 0 || i >= m || j < 0 || j >= n || grid[i][j] == 0) {
            return;
        }

        grid[i][j] = 0;
        cells.add(new int[]{i, j});

        dfsCollect(grid, i + 1, j, cells);
        dfsCollect(grid, i - 1, j, cells);
        dfsCollect(grid, i, j + 1, cells);
        dfsCollect(grid, i, j - 1, cells);
    }

    private String normalize(List<int[]> cells) {
        if (cells.isEmpty()) return "";

        // Sort cells
        cells.sort((a, b) -> a[0] == b[0] ? a[1] - b[1] : a[0] - b[0]);

        // Normalize to origin (0, 0)
        int minI = cells.get(0)[0];
        int minJ = cells.get(0)[1];

        StringBuilder sb = new StringBuilder();
        for (int[] cell : cells) {
            sb.append(cell[0] - minI).append(",").append(cell[1] - minJ).append(";");
        }

        return sb.toString();
    }

    /**
     * Approach 3: Hash-based with backtracking marker
     *
     * Add backtracking markers to distinguish different shapes.
     * This ensures that different traversal orders produce different signatures.
     *
     * Time: O(m * n)
     * Space: O(m * n)
     */
    public int numDistinctIslandsWithBacktrack(int[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }

        int m = grid.length;
        int n = grid[0].length;
        Set<String> uniqueShapes = new HashSet<>();

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    StringBuilder path = new StringBuilder();
                    dfsWithDirection(grid, i, j, path, 'S'); // S for start
                    uniqueShapes.add(path.toString());
                }
            }
        }

        return uniqueShapes.size();
    }

    private void dfsWithDirection(int[][] grid, int i, int j, StringBuilder path, char direction) {
        int m = grid.length;
        int n = grid[0].length;

        if (i < 0 || i >= m || j < 0 || j >= n || grid[i][j] == 0) {
            return;
        }

        grid[i][j] = 0;
        path.append(direction);

        dfsWithDirection(grid, i + 1, j, path, 'D'); // Down
        dfsWithDirection(grid, i - 1, j, path, 'U'); // Up
        dfsWithDirection(grid, i, j + 1, path, 'R'); // Right
        dfsWithDirection(grid, i, j - 1, path, 'L'); // Left

        path.append('B'); // Backtrack marker
    }
}

/**
 * Test cases
 */
class NumberOfIslandsTest {
    public static void main(String[] args) {
        testNumberOfIslands();
        testDistinctIslands();
    }

    private static void testNumberOfIslands() {
        System.out.println("=== Testing LeetCode 200: Number of Islands ===\n");
        NumberOfIslands solution = new NumberOfIslands();

        // Test 1
        char[][] grid1 = {
            {'1', '1', '1', '1', '0'},
            {'1', '1', '0', '1', '0'},
            {'1', '1', '0', '0', '0'},
            {'0', '0', '0', '0', '0'}
        };
        testIslandCase(solution, grid1, 1, "Test 1");

        // Test 2
        char[][] grid2 = {
            {'1', '1', '0', '0', '0'},
            {'1', '1', '0', '0', '0'},
            {'0', '0', '1', '0', '0'},
            {'0', '0', '0', '1', '1'}
        };
        testIslandCase(solution, grid2, 3, "Test 2");

        // Test 3: Single cell
        char[][] grid3 = {{'1'}};
        testIslandCase(solution, grid3, 1, "Test 3");

        // Test 4: All water
        char[][] grid4 = {
            {'0', '0'},
            {'0', '0'}
        };
        testIslandCase(solution, grid4, 0, "Test 4");

        // Test 5: All land
        char[][] grid5 = {
            {'1', '1'},
            {'1', '1'}
        };
        testIslandCase(solution, grid5, 1, "Test 5");
    }

    private static void testIslandCase(NumberOfIslands solution, char[][] grid, int expected, String testName) {
        System.out.println(testName + ":");

        char[][] gridCopy1 = copyGrid(grid);
        char[][] gridCopy2 = copyGrid(grid);
        char[][] gridCopy3 = copyGrid(grid);

        int resultDFS = solution.numIslands(gridCopy1);
        int resultBFS = solution.numIslandsBFS(gridCopy2);
        int resultUF = solution.numIslandsUnionFind(gridCopy3);

        System.out.println("  DFS: " + resultDFS + " (Expected: " + expected + ") - " +
                          (resultDFS == expected ? "PASS" : "FAIL"));
        System.out.println("  BFS: " + resultBFS + " (Expected: " + expected + ") - " +
                          (resultBFS == expected ? "PASS" : "FAIL"));
        System.out.println("  Union Find: " + resultUF + " (Expected: " + expected + ") - " +
                          (resultUF == expected ? "PASS" : "FAIL"));
        System.out.println();
    }

    private static void testDistinctIslands() {
        System.out.println("=== Testing Follow-up: Number of Distinct Island Shapes ===\n");
        NumberOfDistinctIslands solution = new NumberOfDistinctIslands();

        // Test 1: Two L-shaped islands (same shape)
        int[][] grid1 = {
            {1, 1, 0, 0, 0},
            {1, 0, 0, 0, 0},
            {0, 0, 0, 1, 1},
            {0, 0, 0, 1, 0}
        };
        testDistinctCase(solution, grid1, 2, "Test 1 - Two L-shapes");

        // Test 2: Different shapes
        int[][] grid2 = {
            {1, 1, 0, 1, 1},
            {1, 0, 0, 0, 0},
            {0, 0, 1, 0, 0},
            {1, 1, 0, 1, 1}
        };
        testDistinctCase(solution, grid2, 3, "Test 2 - Different shapes");

        // Test 3: All same shapes
        int[][] grid3 = {
            {1, 0, 1},
            {0, 0, 0},
            {1, 0, 1}
        };
        testDistinctCase(solution, grid3, 1, "Test 3 - All single cells");

        // Test 4: Complex shapes
        int[][] grid4 = {
            {1, 1, 1, 0, 0},
            {0, 1, 0, 0, 1},
            {0, 0, 0, 1, 1}
        };
        testDistinctCase(solution, grid4, 2, "Test 4 - Complex shapes");
    }

    private static void testDistinctCase(NumberOfDistinctIslands solution, int[][] grid, int expected, String testName) {
        System.out.println(testName + ":");

        int[][] gridCopy1 = copyIntGrid(grid);
        int[][] gridCopy2 = copyIntGrid(grid);
        int[][] gridCopy3 = copyIntGrid(grid);

        int result1 = solution.numDistinctIslands(gridCopy1);
        int result2 = solution.numDistinctIslandsCoordinates(gridCopy2);
        int result3 = solution.numDistinctIslandsWithBacktrack(gridCopy3);

        System.out.println("  Path Signature: " + result1 + " (Expected: " + expected + ") - " +
                          (result1 == expected ? "PASS" : "FAIL"));
        System.out.println("  Normalized Coords: " + result2 + " (Expected: " + expected + ") - " +
                          (result2 == expected ? "PASS" : "FAIL"));
        System.out.println("  With Backtrack: " + result3 + " (Expected: " + expected + ") - " +
                          (result3 == expected ? "PASS" : "FAIL"));
        System.out.println();
    }

    private static char[][] copyGrid(char[][] grid) {
        char[][] copy = new char[grid.length][];
        for (int i = 0; i < grid.length; i++) {
            copy[i] = grid[i].clone();
        }
        return copy;
    }

    private static int[][] copyIntGrid(int[][] grid) {
        int[][] copy = new int[grid.length][];
        for (int i = 0; i < grid.length; i++) {
            copy[i] = grid[i].clone();
        }
        return copy;
    }
}
