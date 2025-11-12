import java.util.*;

/**
 * Applied Intuition Interview Question
 *
 * Problem: Game of Life with Very Large Grid (rows and cols are extremely large)
 *
 * Original Game of Life:
 * - Each cell is either alive (true) or dead (false)
 * - Rules for next generation:
 *   1. Any live cell with 2-3 live neighbors survives
 *   2. Any dead cell with exactly 3 live neighbors becomes alive
 *   3. All other cells die or stay dead
 *
 * Challenge: Grid is too large to fit in memory
 * Solution: Store each row in a separate file
 *
 * Key insight from interviewer:
 * - Store one row per file
 * - Only load 3 rows at a time (current, previous, next)
 * - Process row by row
 * - Use provided read/write file interface
 */

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
 */
class Minesweeper {

    /**
     * Approach 1: DFS (Recursive)
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
     * Approach 2: BFS (Iterative)
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
     * Approach 3: Optimized with directions array
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
 * LeetCode 289: Game of Life (In-memory version)
 */
class GameOfLife {

    /**
     * Standard in-memory solution for small grids
     */
    public void gameOfLife(int[][] board) {
        int m = board.length;
        int n = board[0].length;

        int[][] newBoard = new int[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                int liveNeighbors = countLiveNeighbors(board, i, j);

                if (board[i][j] == 1) {
                    // Live cell
                    if (liveNeighbors == 2 || liveNeighbors == 3) {
                        newBoard[i][j] = 1;
                    }
                } else {
                    // Dead cell
                    if (liveNeighbors == 3) {
                        newBoard[i][j] = 1;
                    }
                }
            }
        }

        // Copy back
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = newBoard[i][j];
            }
        }
    }

    private int countLiveNeighbors(int[][] board, int row, int col) {
        int count = 0;
        int m = board.length;
        int n = board[0].length;

        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i == row && j == col) continue;
                if (i >= 0 && i < m && j >= 0 && j < n) {
                    count += board[i][j];
                }
            }
        }

        return count;
    }

    /**
     * In-place solution (encoding state to save space)
     * Uses bit manipulation:
     * - bit 0: current state
     * - bit 1: next state
     */
    public void gameOfLifeInPlace(int[][] board) {
        int m = board.length;
        int n = board[0].length;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                int liveNeighbors = countLiveNeighborsInPlace(board, i, j);

                // Store next state in bit 1
                if (board[i][j] == 1 && (liveNeighbors == 2 || liveNeighbors == 3)) {
                    board[i][j] = 3; // 11 in binary
                }
                if (board[i][j] == 0 && liveNeighbors == 3) {
                    board[i][j] = 2; // 10 in binary
                }
            }
        }

        // Extract next state from bit 1
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] >>= 1;
            }
        }
    }

    private int countLiveNeighborsInPlace(int[][] board, int row, int col) {
        int count = 0;
        int m = board.length;
        int n = board[0].length;

        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i == row && j == col) continue;
                if (i >= 0 && i < m && j >= 0 && j < n) {
                    count += board[i][j] & 1; // Get current state from bit 0
                }
            }
        }

        return count;
    }
}

/**
 * File-based storage interface (provided by interviewer)
 */
interface FileSystem {
    /**
     * Read entire file contents
     */
    String read(String name);

    /**
     * Write entire file contents
     */
    void write(String name, String contents);

    /**
     * Read portion of file (seek-style)
     */
    String read(String name, int offset, int length);

    /**
     * Append to file
     */
    void append(String name, String contents);
}

/**
 * Mock file system implementation
 */
class MockFileSystem implements FileSystem {
    private Map<String, String> filesystem = new HashMap<>();

    @Override
    public String read(String name) {
        return filesystem.getOrDefault(name, "");
    }

    @Override
    public void write(String name, String contents) {
        filesystem.put(name, contents);
    }

    @Override
    public String read(String name, int offset, int length) {
        String content = filesystem.getOrDefault(name, "");
        if (offset >= content.length()) return "";
        int end = Math.min(offset + length, content.length());
        return content.substring(offset, end);
    }

    @Override
    public void append(String name, String contents) {
        String existing = filesystem.getOrDefault(name, "");
        filesystem.put(name, existing + contents);
    }
}

/**
 * Solution for very large grids using file storage
 */
class GameOfLifeLargeGrid {
    private FileSystem fs;
    private int rows;
    private int cols;

    public GameOfLifeLargeGrid(FileSystem fs, int rows, int cols) {
        this.fs = fs;
        this.rows = rows;
        this.cols = cols;
    }

    /**
     * Initialize grid with probability p of being alive
     */
    public void initialize(double p) {
        Random random = new Random();

        for (int i = 0; i < rows; i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < cols; j++) {
                boolean alive = random.nextDouble() <= p;
                row.append(alive ? "1" : "0");
                if (j < cols - 1) row.append(",");
            }
            fs.write("row_" + i, row.toString());
        }
    }

    /**
     * Compute next generation - file-based approach
     *
     * Key insight: Only need 3 rows in memory at once
     * - Previous row (for cells above)
     * - Current row (being processed)
     * - Next row (for cells below)
     */
    public void nextGeneration() {
        // Process each row
        for (int i = 0; i < rows; i++) {
            // Load 3 rows: previous, current, next
            boolean[] prevRow = (i > 0) ? loadRow(i - 1) : null;
            boolean[] currRow = loadRow(i);
            boolean[] nextRow = (i < rows - 1) ? loadRow(i + 1) : null;

            // Compute next state for current row
            boolean[] newRow = new boolean[cols];
            for (int j = 0; j < cols; j++) {
                int liveNeighbors = countLiveNeighbors(prevRow, currRow, nextRow, j);

                if (currRow[j]) {
                    // Live cell
                    newRow[j] = (liveNeighbors == 2 || liveNeighbors == 3);
                } else {
                    // Dead cell
                    newRow[j] = (liveNeighbors == 3);
                }
            }

            // Write new row to temporary file
            saveRow("temp_row_" + i, newRow);
        }

        // Replace old rows with new rows
        for (int i = 0; i < rows; i++) {
            String content = fs.read("temp_row_" + i);
            fs.write("row_" + i, content);
        }
    }

    /**
     * Count live neighbors for cell at column j
     * Given three rows: previous, current, next
     */
    private int countLiveNeighbors(boolean[] prevRow, boolean[] currRow,
                                   boolean[] nextRow, int col) {
        int count = 0;

        // Check previous row
        if (prevRow != null) {
            if (col > 0 && prevRow[col - 1]) count++;
            if (prevRow[col]) count++;
            if (col < cols - 1 && prevRow[col + 1]) count++;
        }

        // Check current row (left and right)
        if (col > 0 && currRow[col - 1]) count++;
        if (col < cols - 1 && currRow[col + 1]) count++;

        // Check next row
        if (nextRow != null) {
            if (col > 0 && nextRow[col - 1]) count++;
            if (nextRow[col]) count++;
            if (col < cols - 1 && nextRow[col + 1]) count++;
        }

        return count;
    }

    /**
     * Load a row from file
     */
    private boolean[] loadRow(int rowIndex) {
        String content = fs.read("row_" + rowIndex);
        if (content.isEmpty()) {
            return new boolean[cols];
        }

        String[] parts = content.split(",");
        boolean[] row = new boolean[cols];
        for (int i = 0; i < parts.length && i < cols; i++) {
            row[i] = parts[i].equals("1");
        }
        return row;
    }

    /**
     * Save a row to file
     */
    private void saveRow(String filename, boolean[] row) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.length; i++) {
            sb.append(row[i] ? "1" : "0");
            if (i < row.length - 1) sb.append(",");
        }
        fs.write(filename, sb.toString());
    }

    /**
     * Get cell value at (row, col)
     */
    public boolean getCell(int row, int col) {
        boolean[] rowData = loadRow(row);
        return rowData[col];
    }

    /**
     * Print the grid (for testing small grids)
     */
    public void printGrid() {
        for (int i = 0; i < Math.min(rows, 20); i++) {
            boolean[] row = loadRow(i);
            for (int j = 0; j < Math.min(cols, 40); j++) {
                System.out.print(row[j] ? "■ " : "□ ");
            }
            System.out.println();
        }
        System.out.println();
    }
}

/**
 * Optimized version using chunk-based processing
 */
class GameOfLifeChunked {
    private FileSystem fs;
    private int rows;
    private int cols;
    private int chunkSize; // Process multiple rows at once

    public GameOfLifeChunked(FileSystem fs, int rows, int cols, int chunkSize) {
        this.fs = fs;
        this.rows = rows;
        this.cols = cols;
        this.chunkSize = chunkSize;
    }

    /**
     * Process grid in chunks for better I/O efficiency
     */
    public void nextGeneration() {
        for (int chunkStart = 0; chunkStart < rows; chunkStart += chunkSize) {
            int chunkEnd = Math.min(chunkStart + chunkSize, rows);
            processChunk(chunkStart, chunkEnd);
        }

        // Replace old files with new files
        for (int i = 0; i < rows; i++) {
            String content = fs.read("temp_row_" + i);
            fs.write("row_" + i, content);
        }
    }

    private void processChunk(int start, int end) {
        // Load chunk + boundary rows
        boolean[][] chunk = new boolean[end - start + 2][cols];

        // Load previous boundary
        if (start > 0) {
            chunk[0] = loadRow(start - 1);
        }

        // Load chunk rows
        for (int i = start; i < end; i++) {
            chunk[i - start + 1] = loadRow(i);
        }

        // Load next boundary
        if (end < rows) {
            chunk[end - start + 1] = loadRow(end);
        }

        // Process chunk
        for (int i = start; i < end; i++) {
            boolean[] newRow = new boolean[cols];
            int localRow = i - start + 1;

            for (int j = 0; j < cols; j++) {
                int count = countNeighborsInChunk(chunk, localRow, j);

                if (chunk[localRow][j]) {
                    newRow[j] = (count == 2 || count == 3);
                } else {
                    newRow[j] = (count == 3);
                }
            }

            saveRow("temp_row_" + i, newRow);
        }
    }

    private int countNeighborsInChunk(boolean[][] chunk, int row, int col) {
        int count = 0;

        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i == row && j == col) continue;
                if (i >= 0 && i < chunk.length && j >= 0 && j < cols) {
                    if (chunk[i][j]) count++;
                }
            }
        }

        return count;
    }

    private boolean[] loadRow(int rowIndex) {
        String content = fs.read("row_" + rowIndex);
        if (content.isEmpty()) {
            return new boolean[cols];
        }

        String[] parts = content.split(",");
        boolean[] row = new boolean[cols];
        for (int i = 0; i < parts.length && i < cols; i++) {
            row[i] = parts[i].equals("1");
        }
        return row;
    }

    private void saveRow(String filename, boolean[] row) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.length; i++) {
            sb.append(row[i] ? "1" : "0");
            if (i < row.length - 1) sb.append(",");
        }
        fs.write(filename, sb.toString());
    }
}

/**
 * Test cases
 */
class GameOfLifeLargeGridTest {
    public static void main(String[] args) {
        testStandardGameOfLife();
        testLargeGridFileBasedBasic();
        testLargeGridPatterns();
        testChunkedProcessing();
    }

    private static void testStandardGameOfLife() {
        System.out.println("=== LeetCode 289: Game of Life (Standard) ===\n");
        GameOfLife game = new GameOfLife();

        int[][] board = {
            {0, 1, 0},
            {0, 0, 1},
            {1, 1, 1},
            {0, 0, 0}
        };

        System.out.println("Initial state:");
        printBoard(board);

        game.gameOfLife(board);

        System.out.println("Next generation:");
        printBoard(board);
    }

    private static void testLargeGridFileBasedBasic() {
        System.out.println("=== File-Based Game of Life (Small Test) ===\n");

        MockFileSystem fs = new MockFileSystem();
        GameOfLifeLargeGrid game = new GameOfLifeLargeGrid(fs, 5, 5);

        // Manually set up a glider pattern
        fs.write("row_0", "0,0,0,0,0");
        fs.write("row_1", "0,0,1,0,0");
        fs.write("row_2", "0,0,0,1,0");
        fs.write("row_3", "0,1,1,1,0");
        fs.write("row_4", "0,0,0,0,0");

        System.out.println("Generation 0:");
        game.printGrid();

        game.nextGeneration();
        System.out.println("Generation 1:");
        game.printGrid();

        game.nextGeneration();
        System.out.println("Generation 2:");
        game.printGrid();
    }

    private static void testLargeGridPatterns() {
        System.out.println("=== Large Grid with Random Initialization ===\n");

        MockFileSystem fs = new MockFileSystem();
        GameOfLifeLargeGrid game = new GameOfLifeLargeGrid(fs, 10, 10);

        game.initialize(0.3); // 30% probability of being alive

        System.out.println("Initial random state:");
        game.printGrid();

        for (int gen = 1; gen <= 3; gen++) {
            game.nextGeneration();
            System.out.println("Generation " + gen + ":");
            game.printGrid();
        }
    }

    private static void testChunkedProcessing() {
        System.out.println("=== Chunked Processing for Very Large Grids ===\n");

        MockFileSystem fs = new MockFileSystem();
        GameOfLifeChunked game = new GameOfLifeChunked(fs, 100, 100, 10);

        // Initialize with random pattern
        Random rand = new Random(42);
        for (int i = 0; i < 100; i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < 100; j++) {
                row.append(rand.nextDouble() < 0.3 ? "1" : "0");
                if (j < 99) row.append(",");
            }
            fs.write("row_" + i, row.toString());
        }

        System.out.println("Processing 100x100 grid with chunk size 10");
        System.out.println("Running 5 generations...");

        for (int i = 0; i < 5; i++) {
            game.nextGeneration();
            System.out.println("Generation " + (i + 1) + " complete");
        }
    }

    private static void printBoard(int[][] board) {
        for (int[] row : board) {
            for (int cell : row) {
                System.out.print(cell == 1 ? "■ " : "□ ");
            }
            System.out.println();
        }
        System.out.println();
    }
}

/**
 * Follow-up Discussion Points
 */
class FollowUpDiscussion {
    /*
     * Q1: Why store one row per file?
     * A1:
     *   - Game of Life only needs neighbors (3 rows at most)
     *   - Memory usage: O(3 × cols) instead of O(rows × cols)
     *   - Can process grids with millions of rows
     *
     * Q2: What if columns are also too large?
     * A2:
     *   - Store grid in blocks (chunks of rows × cols)
     *   - Process block by block
     *   - Need to handle boundary cells between blocks
     *
     * Q3: How to optimize file I/O?
     * A3:
     *   - Process multiple rows in chunks (amortize I/O cost)
     *   - Use buffering
     *   - Compress row data
     *   - Use binary format instead of CSV
     *
     * Q4: What about infinite grids?
     * A4:
     *   - Store only live cells in sparse format
     *   - Use HashMap: (row, col) -> true
     *   - Only process regions with live cells
     *
     * Q5: How to parallelize?
     * A5:
     *   - Process different chunks in parallel
     *   - Need synchronization at boundaries
     *   - MapReduce style: map chunks, reduce boundaries
     *
     * Q6: What if we need to support queries?
     * A6:
     *   - Index files by row number
     *   - Use B-tree for efficient row lookup
     *   - Cache frequently accessed rows
     */
}
