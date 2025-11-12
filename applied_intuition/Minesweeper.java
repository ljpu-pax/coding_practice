import java.util.*;

/**
 * LeetCode 529: Minesweeper
 *
 * Let's play the minesweeper game!
 *
 * You are given an m x n char matrix board representing the game board where:
 * - 'M' represents an unrevealed mine
 * - 'E' represents an unrevealed empty square
 * - 'B' represents a revealed blank square that has no adjacent mines
 * - digit ('1' to '8') represents how many mines are adjacent to this revealed square
 * - 'X' represents a revealed mine
 *
 * You are also given an integer array click where click = [clickr, clickc]
 * represents the next click position among all unrevealed squares ('M' or 'E').
 *
 * Return the board after revealing this position according to the following rules:
 * 1. If a mine 'M' is revealed, game over - change it to 'X'
 * 2. If an empty square 'E' with no adjacent mines is revealed, change it to 'B'
 *    and recursively reveal all adjacent unrevealed squares
 * 3. If an empty square 'E' with at least one adjacent mine is revealed,
 *    change it to a digit ('1' to '8') representing the number of adjacent mines
 * 4. Return the board when no more squares will be revealed
 *
 * Example 1:
 * Input: board = [["E","E","E","E","E"],
 *                 ["E","E","M","E","E"],
 *                 ["E","E","E","E","E"],
 *                 ["E","E","E","E","E"]]
 *        click = [3,0]
 * Output: [["B","1","E","1","B"],
 *          ["B","1","M","1","B"],
 *          ["B","1","1","1","B"],
 *          ["B","B","B","B","B"]]
 *
 * Example 2:
 * Input: board = [["B","1","E","1","B"],
 *                 ["B","1","M","1","B"],
 *                 ["B","1","1","1","B"],
 *                 ["B","B","B","B","B"]]
 *        click = [1,2]
 * Output: [["B","1","E","1","B"],
 *          ["B","1","X","1","B"],
 *          ["B","1","1","1","B"],
 *          ["B","B","B","B","B"]]
 *
 * Constraints:
 * - m == board.length
 * - n == board[i].length
 * - 1 <= m, n <= 50
 * - board[i][j] is either 'M', 'E', 'B', or a digit from '1' to '8'
 * - click.length == 2
 * - 0 <= clickr < m
 * - 0 <= clickc < n
 * - board[clickr][clickc] is either 'M' or 'E'
 */
class Minesweeper {

    /**
     * Approach 1: DFS (Recursive) - Most intuitive
     *
     * Time: O(m × n) in worst case (reveal entire board)
     * Space: O(m × n) for recursion stack
     */
    public char[][] updateBoard(char[][] board, int[] click) {
        int row = click[0];
        int col = click[1];

        // Rule 1: Hit a mine
        if (board[row][col] == 'M') {
            board[row][col] = 'X';
            return board;
        }

        // Rule 2 & 3: Reveal empty square
        dfs(board, row, col);
        return board;
    }

    private void dfs(char[][] board, int row, int col) {
        // Bounds check
        if (row < 0 || row >= board.length || col < 0 || col >= board[0].length) {
            return;
        }

        // Only process unrevealed empty squares
        if (board[row][col] != 'E') {
            return;
        }

        // Count adjacent mines
        int mineCount = countAdjacentMines(board, row, col);

        if (mineCount > 0) {
            // Rule 3: Has adjacent mines, show count
            board[row][col] = (char) ('0' + mineCount);
        } else {
            // Rule 2: No adjacent mines, reveal as blank
            board[row][col] = 'B';

            // Recursively reveal all 8 adjacent squares
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0) continue;
                    dfs(board, row + i, col + j);
                }
            }
        }
    }

    private int countAdjacentMines(char[][] board, int row, int col) {
        int count = 0;
        int m = board.length;
        int n = board[0].length;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                int newRow = row + i;
                int newCol = col + j;

                if (newRow >= 0 && newRow < m && newCol >= 0 && newCol < n) {
                    if (board[newRow][newCol] == 'M' || board[newRow][newCol] == 'X') {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    /**
     * Approach 2: BFS (Iterative) - Avoids stack overflow for large boards
     *
     * Time: O(m × n)
     * Space: O(m × n) for queue
     */
    public char[][] updateBoardBFS(char[][] board, int[] click) {
        int row = click[0];
        int col = click[1];

        // Hit a mine
        if (board[row][col] == 'M') {
            board[row][col] = 'X';
            return board;
        }

        // BFS
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{row, col});
        board[row][col] = 'B'; // Mark as visited

        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
        };

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int r = curr[0];
            int c = curr[1];

            int mineCount = countAdjacentMines(board, r, c);

            if (mineCount > 0) {
                board[r][c] = (char) ('0' + mineCount);
            } else {
                board[r][c] = 'B';

                // Add all 8 adjacent cells to queue
                for (int[] dir : directions) {
                    int nr = r + dir[0];
                    int nc = c + dir[1];

                    if (nr >= 0 && nr < board.length && nc >= 0 && nc < board[0].length) {
                        if (board[nr][nc] == 'E') {
                            queue.offer(new int[]{nr, nc});
                            board[nr][nc] = 'B'; // Mark as visited to avoid duplicates
                        }
                    }
                }
            }
        }

        return board;
    }

    /**
     * Approach 3: Optimized DFS with directions array
     */
    private static final int[][] DIRECTIONS = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1},           {0, 1},
        {1, -1},  {1, 0},  {1, 1}
    };

    public char[][] updateBoardOptimized(char[][] board, int[] click) {
        int row = click[0];
        int col = click[1];

        if (board[row][col] == 'M') {
            board[row][col] = 'X';
            return board;
        }

        reveal(board, row, col);
        return board;
    }

    private void reveal(char[][] board, int row, int col) {
        if (row < 0 || row >= board.length || col < 0 || col >= board[0].length) {
            return;
        }

        if (board[row][col] != 'E') {
            return;
        }

        int mineCount = 0;
        for (int[] dir : DIRECTIONS) {
            int nr = row + dir[0];
            int nc = col + dir[1];

            if (nr >= 0 && nr < board.length && nc >= 0 && nc < board[0].length) {
                if (board[nr][nc] == 'M' || board[nr][nc] == 'X') {
                    mineCount++;
                }
            }
        }

        if (mineCount > 0) {
            board[row][col] = (char) ('0' + mineCount);
        } else {
            board[row][col] = 'B';

            for (int[] dir : DIRECTIONS) {
                reveal(board, row + dir[0], col + dir[1]);
            }
        }
    }
}

/**
 * Extended Minesweeper with additional features
 */
class MinesweeperExtended {

    /**
     * Generate a random minesweeper board
     */
    public char[][] generateBoard(int rows, int cols, int numMines) {
        char[][] board = new char[rows][cols];

        // Initialize all cells as empty
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = 'E';
            }
        }

        // Place mines randomly
        Random rand = new Random();
        int placedMines = 0;

        while (placedMines < numMines) {
            int row = rand.nextInt(rows);
            int col = rand.nextInt(cols);

            if (board[row][col] != 'M') {
                board[row][col] = 'M';
                placedMines++;
            }
        }

        return board;
    }

    /**
     * Check if game is won (all non-mine cells revealed)
     */
    public boolean isGameWon(char[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                // If there's an unrevealed non-mine cell, game not won
                if (board[i][j] == 'E') {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Count total mines on board
     */
    public int countTotalMines(char[][] board) {
        int count = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == 'M' || board[i][j] == 'X') {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Reveal all mines (for game over or debug)
     */
    public void revealAllMines(char[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == 'M') {
                    board[i][j] = 'X';
                }
            }
        }
    }

    /**
     * Get all safe cells (cells that are not mines)
     */
    public List<int[]> getSafeCells(char[][] board) {
        List<int[]> safeCells = new ArrayList<>();

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == 'E' || board[i][j] == 'B' ||
                    Character.isDigit(board[i][j])) {
                    safeCells.add(new int[]{i, j});
                }
            }
        }

        return safeCells;
    }
}

/**
 * Test cases
 */
class MinesweeperTest {
    public static void main(String[] args) {
        testBasicCases();
        testHitMine();
        testRevealLargeArea();
        testAllApproaches();
        testExtendedFeatures();
    }

    private static void testBasicCases() {
        System.out.println("=== LeetCode 529: Minesweeper ===\n");
        Minesweeper game = new Minesweeper();

        // Test 1: Click on empty cell, reveal large area
        char[][] board1 = {
            {'E', 'E', 'E', 'E', 'E'},
            {'E', 'E', 'M', 'E', 'E'},
            {'E', 'E', 'E', 'E', 'E'},
            {'E', 'E', 'E', 'E', 'E'}
        };

        System.out.println("Test 1: Click on corner (3,0)");
        System.out.println("Before:");
        printBoard(board1);

        char[][] result1 = game.updateBoard(board1, new int[]{3, 0});
        System.out.println("After:");
        printBoard(result1);

        // Test 2: Click on cell adjacent to mine
        char[][] board2 = {
            {'E', 'E', 'E'},
            {'E', 'M', 'E'},
            {'E', 'E', 'E'}
        };

        System.out.println("Test 2: Click on cell adjacent to mine (0,0)");
        System.out.println("Before:");
        printBoard(board2);

        char[][] result2 = game.updateBoard(board2, new int[]{0, 0});
        System.out.println("After:");
        printBoard(result2);
    }

    private static void testHitMine() {
        System.out.println("=== Test: Hit Mine ===\n");
        Minesweeper game = new Minesweeper();

        char[][] board = {
            {'E', 'E', 'E'},
            {'E', 'M', 'E'},
            {'E', 'E', 'E'}
        };

        System.out.println("Before clicking on mine (1,1):");
        printBoard(board);

        char[][] result = game.updateBoard(board, new int[]{1, 1});
        System.out.println("After (Game Over):");
        printBoard(result);
    }

    private static void testRevealLargeArea() {
        System.out.println("=== Test: Reveal Large Area ===\n");
        Minesweeper game = new Minesweeper();

        char[][] board = {
            {'E', 'E', 'E', 'E', 'E'},
            {'E', 'E', 'E', 'E', 'E'},
            {'E', 'E', 'E', 'E', 'E'},
            {'E', 'M', 'E', 'E', 'E'},
            {'E', 'E', 'E', 'E', 'E'}
        };

        System.out.println("Before clicking on (0,0):");
        printBoard(board);

        char[][] result = game.updateBoard(board, new int[]{0, 0});
        System.out.println("After:");
        printBoard(result);
    }

    private static void testAllApproaches() {
        System.out.println("=== Test: All Approaches ===\n");
        Minesweeper game = new Minesweeper();

        char[][] board = {
            {'E', 'E', 'E', 'E'},
            {'E', 'M', 'E', 'E'},
            {'E', 'E', 'E', 'E'},
            {'E', 'E', 'E', 'E'}
        };

        char[][] board1 = copyBoard(board);
        char[][] board2 = copyBoard(board);
        char[][] board3 = copyBoard(board);

        char[][] result1 = game.updateBoard(board1, new int[]{3, 0});
        char[][] result2 = game.updateBoardBFS(board2, new int[]{3, 0});
        char[][] result3 = game.updateBoardOptimized(board3, new int[]{3, 0});

        System.out.println("DFS Recursive:");
        printBoard(result1);

        System.out.println("BFS Iterative:");
        printBoard(result2);

        System.out.println("DFS Optimized:");
        printBoard(result3);
    }

    private static void testExtendedFeatures() {
        System.out.println("=== Test: Extended Features ===\n");
        MinesweeperExtended extended = new MinesweeperExtended();

        // Generate random board
        char[][] board = extended.generateBoard(5, 5, 3);
        System.out.println("Generated board with 3 mines:");
        printBoard(board);

        // Count mines
        int mineCount = extended.countTotalMines(board);
        System.out.println("Total mines: " + mineCount);

        // Get safe cells
        List<int[]> safeCells = extended.getSafeCells(board);
        System.out.println("Safe cells: " + safeCells.size());

        // Reveal all mines
        extended.revealAllMines(board);
        System.out.println("\nAll mines revealed:");
        printBoard(board);
    }

    private static void printBoard(char[][] board) {
        for (char[] row : board) {
            for (char cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static char[][] copyBoard(char[][] board) {
        char[][] copy = new char[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            copy[i] = board[i].clone();
        }
        return copy;
    }
}

/**
 * Key Insights and Complexity Analysis
 */
class MinesweeperAnalysis {
    /*
     * Algorithm Comparison:
     *
     * 1. DFS (Recursive):
     *    - Pros: Simple, intuitive, easy to understand
     *    - Cons: Stack overflow risk for very large boards
     *    - Best for: Small to medium boards
     *
     * 2. BFS (Iterative):
     *    - Pros: No stack overflow, processes level by level
     *    - Cons: More complex, needs explicit queue
     *    - Best for: Large boards or when stack depth is concern
     *
     * 3. Optimized DFS:
     *    - Pros: Clean code with directions array
     *    - Cons: Same stack overflow risk as DFS
     *    - Best for: Production code (readability)
     *
     * Time Complexity: O(m × n)
     * - Worst case: reveal entire board
     * - Each cell visited at most once
     *
     * Space Complexity:
     * - DFS: O(m × n) for recursion stack
     * - BFS: O(m × n) for queue
     * - Both worst case is full board
     *
     * Optimization Techniques:
     * 1. Mark cells as visited during processing (avoid reprocessing)
     * 2. Use directions array for cleaner neighbor iteration
     * 3. Early termination when clicking on mine
     * 4. Count mines in single pass
     *
     * Edge Cases to Consider:
     * - Click on mine (immediate game over)
     * - Click on already revealed cell (do nothing)
     * - Board with no mines (reveal all)
     * - Board completely surrounded by mines
     * - Single cell board
     * - Click on boundary cells
     */
}
