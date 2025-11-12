package nvda;

import java.util.*;

public class NumberOfIslands {
    
    // LeetCode 200: Number of Islands
    // DFS approach - O(m*n) time, O(m*n) space for recursion stack
    public int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0) return 0;
        
        int m = grid.length, n = grid[0].length;
        int count = 0;
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == '1') {
                    dfs(grid, i, j);
                    count++;
                }
            }
        }
        
        return count;
    }
    
    private void dfs(char[][] grid, int i, int j) {
        if (i < 0 || i >= grid.length || j < 0 || j >= grid[0].length || grid[i][j] != '1') {
            return;
        }
        
        grid[i][j] = '0'; // Mark as visited
        
        // Visit all 4 directions
        dfs(grid, i + 1, j);
        dfs(grid, i - 1, j);
        dfs(grid, i, j + 1);
        dfs(grid, i, j - 1);
    }

    // BFS approach - O(m*n) time, O(min(m,n)) space
    public int numIslandsBFS(char[][] grid) {
        if (grid == null || grid.length == 0) return 0;
        
        int m = grid.length, n = grid[0].length;
        int count = 0;
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == '1') {
                    Queue<int[]> queue = new LinkedList<>();
                    queue.offer(new int[]{i, j});
                    grid[i][j] = '0';
                    
                    while (!queue.isEmpty()) {
                        int[] current = queue.poll();
                        
                        for (int[] dir : directions) {
                            int x = current[0] + dir[0];
                            int y = current[1] + dir[1];
                            
                            if (x >= 0 && x < m && y >= 0 && y < n && grid[x][y] == '1') {
                                grid[x][y] = '0';
                                queue.offer(new int[]{x, y});
                            }
                        }
                    }
                    
                    count++;
                }
            }
        }
        
        return count;
    }

    // LeetCode 695: Max Area of Island
    // DFS approach - O(m*n) time, O(m*n) space
    public int maxAreaOfIsland(int[][] grid) {
        if (grid == null || grid.length == 0) return 0;
        
        int m = grid.length, n = grid[0].length;
        int maxArea = 0;
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    maxArea = Math.max(maxArea, dfsArea(grid, i, j));
                }
            }
        }
        
        return maxArea;
    }
    
    private int dfsArea(int[][] grid, int i, int j) {
        if (i < 0 || i >= grid.length || j < 0 || j >= grid[0].length || grid[i][j] != 1) {
            return 0;
        }
        
        grid[i][j] = 0; // Mark as visited
        int area = 1;
        
        area += dfsArea(grid, i + 1, j);
        area += dfsArea(grid, i - 1, j);
        area += dfsArea(grid, i, j + 1);
        area += dfsArea(grid, i, j - 1);
        
        return area;
    }

    // LeetCode 130: Surrounded Regions
    // DFS from borders - O(m*n) time, O(m*n) space
    public void solve(char[][] board) {
        if (board == null || board.length == 0) return;
        
        int m = board.length, n = board[0].length;
        
        // Mark 'O's connected to borders as 'T'
        for (int i = 0; i < m; i++) {
            if (board[i][0] == 'O') dfsMark(board, i, 0);
            if (board[i][n - 1] == 'O') dfsMark(board, i, n - 1);
        }
        
        for (int j = 0; j < n; j++) {
            if (board[0][j] == 'O') dfsMark(board, 0, j);
            if (board[m - 1][j] == 'O') dfsMark(board, m - 1, j);
        }
        
        // Convert remaining 'O's to 'X' and 'T's back to 'O'
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == 'O') {
                    board[i][j] = 'X';
                } else if (board[i][j] == 'T') {
                    board[i][j] = 'O';
                }
            }
        }
    }
    
    private void dfsMark(char[][] board, int i, int j) {
        if (i < 0 || i >= board.length || j < 0 || j >= board[0].length || board[i][j] != 'O') {
            return;
        }
        
        board[i][j] = 'T';
        dfsMark(board, i + 1, j);
        dfsMark(board, i - 1, j);
        dfsMark(board, i, j + 1);
        dfsMark(board, i, j - 1);
    }

    // LeetCode 994: Rotting Oranges
    // BFS approach - O(m*n) time, O(m*n) space
    public int orangesRotting(int[][] grid) {
        if (grid == null || grid.length == 0) return 0;
        
        int m = grid.length, n = grid[0].length;
        Queue<int[]> queue = new LinkedList<>();
        int fresh = 0;
        
        // Find all rotten oranges and count fresh ones
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 2) {
                    queue.offer(new int[]{i, j});
                } else if (grid[i][j] == 1) {
                    fresh++;
                }
            }
        }
        
        if (fresh == 0) return 0;
        
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        int minutes = 0;
        
        while (!queue.isEmpty()) {
            int size = queue.size();
            boolean hasRotten = false;
            
            for (int i = 0; i < size; i++) {
                int[] current = queue.poll();
                
                for (int[] dir : directions) {
                    int x = current[0] + dir[0];
                    int y = current[1] + dir[1];
                    
                    if (x >= 0 && x < m && y >= 0 && y < n && grid[x][y] == 1) {
                        grid[x][y] = 2;
                        queue.offer(new int[]{x, y});
                        fresh--;
                        hasRotten = true;
                    }
                }
            }
            
            if (hasRotten) minutes++;
        }
        
        return fresh == 0 ? minutes : -1;
    }

    // Helper method to print grid
    private static void printGrid(char[][] grid) {
        for (char[] row : grid) {
            System.out.println(Arrays.toString(row));
        }
    }

    private static void printIntGrid(int[][] grid) {
        for (int[] row : grid) {
            System.out.println(Arrays.toString(row));
        }
    }

    public static void main(String[] args) {
        NumberOfIslands solver = new NumberOfIslands();
        
        // Test Number of Islands
        System.out.println("=== Number of Islands (LeetCode 200) ===");
        char[][] grid1 = {
            {'1','1','1','1','0'},
            {'1','1','0','1','0'},
            {'1','1','0','0','0'},
            {'0','0','0','0','0'}
        };
        System.out.println("Grid:");
        printGrid(grid1);
        System.out.println("Number of islands (DFS): " + solver.numIslands(grid1));
        
        // Reset grid for BFS test
        char[][] grid1BFS = {
            {'1','1','1','1','0'},
            {'1','1','0','1','0'},
            {'1','1','0','0','0'},
            {'0','0','0','0','0'}
        };
        System.out.println("Number of islands (BFS): " + solver.numIslandsBFS(grid1BFS));
        System.out.println();
        
        // Test Max Area of Island
        System.out.println("=== Max Area of Island (LeetCode 695) ===");
        int[][] grid2 = {
            {0,0,1,0,0,0,0,1,0,0,0,0,0},
            {0,0,0,0,0,0,0,1,1,1,0,0,0},
            {0,1,1,0,1,0,0,0,0,0,0,0,0},
            {0,1,0,0,1,1,0,0,1,0,1,0,0},
            {0,1,0,0,1,1,0,0,1,1,1,0,0},
            {0,0,0,0,0,0,0,0,0,0,1,0,0},
            {0,0,0,0,0,0,0,1,1,1,0,0,0},
            {0,0,0,0,0,0,0,1,1,0,0,0,0}
        };
        System.out.println("Grid:");
        printIntGrid(grid2);
        System.out.println("Max area: " + solver.maxAreaOfIsland(grid2));
        System.out.println();
        
        // Test Surrounded Regions
        System.out.println("=== Surrounded Regions (LeetCode 130) ===");
        char[][] grid3 = {
            {'X','X','X','X'},
            {'X','O','O','X'},
            {'X','X','O','X'},
            {'X','O','X','X'}
        };
        System.out.println("Before:");
        printGrid(grid3);
        solver.solve(grid3);
        System.out.println("After:");
        printGrid(grid3);
        System.out.println();
        
        // Test Rotting Oranges
        System.out.println("=== Rotting Oranges (LeetCode 994) ===");
        int[][] grid4 = {
            {2,1,1},
            {1,1,0},
            {0,1,1}
        };
        System.out.println("Grid:");
        printIntGrid(grid4);
        System.out.println("Minutes to rot all oranges: " + solver.orangesRotting(grid4));
    }
}
