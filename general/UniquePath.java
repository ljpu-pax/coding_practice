public class UniquePath {
    public int uniquePathsWithObstacles(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        boolean[][] visited = new boolean[m][n];
        return dfs(grid, 0, 0, visited);
    }

    private int dfs(int[][] grid, int x, int y, boolean[][] visited) {
        int m = grid.length, n = grid[0].length;

        if (x < 0 || y < 0 || x >= m || y >= n || grid[x][y] == 1 || visited[x][y]) {
            return 0;
        }

        if (x == m - 1 && y == n - 1) return 1;

        visited[x][y] = true;

        int totalPaths =
            dfs(grid, x + 1, y, visited) +
            dfs(grid, x - 1, y, visited) +
            dfs(grid, x, y + 1, visited) +
            dfs(grid, x, y - 1, visited);

        visited[x][y] = false; // backtrack

        return totalPaths;
    }

    public static void main(String[] args) {
        UniquePath sol = new UniquePath();

        int[][] grid1 = {
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 0}
        };
        System.out.println("Test 1: Expected = Multiple, Got = " + sol.uniquePathsWithObstacles(grid1)); // Expect >1

        int[][] grid2 = {
            {0, 1, 0},
            {0, 0, 0},
            {0, 1, 0}
        };
        System.out.println("Test 2: Expected = 1, Got = " + sol.uniquePathsWithObstacles(grid2));

        int[][] grid3 = {
            {0, 1},
            {1, 0}
        };
        System.out.println("Test 3: Expected = 0, Got = " + sol.uniquePathsWithObstacles(grid3));

        int[][] grid4 = {
            {0, 0},
            {0, 0}
        };
        System.out.println("Test 4: Expected = 2, Got = " + sol.uniquePathsWithObstacles(grid4)); // Two routes

        int[][] grid5 = {
            {0, 0, 1},
            {1, 0, 1},
            {0, 0, 0}
        };
        System.out.println("Test 5: Expected = 1, Got = " + sol.uniquePathsWithObstacles(grid5));

        int[][] grid6 = {
            {1, 0},
            {0, 0}
        };
        System.out.println("Test 6: Expected = 0, Got = " + sol.uniquePathsWithObstacles(grid6)); // Blocked at start

        int[][] grid7 = {
            {0, 0},
            {0, 1}
        };
        System.out.println("Test 7: Expected = 0, Got = " + sol.uniquePathsWithObstacles(grid7)); // Blocked at end
    }
}
