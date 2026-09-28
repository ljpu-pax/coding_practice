/**
 * Confluent phone screen: "利口数独两题"
 *   LC 36 Valid Sudoku  - is a partially filled board valid?
 *   LC 37 Sudoku Solver - fill the board ('.' = empty), exactly one solution.
 *
 * Valid: one pass, bitmasks per row / col / box. O(81).
 * Solver: backtracking with bitmasks; pick the empty cell with the fewest
 * candidates first (MRV) to prune heavily.
 */
public class Sudoku {

    // ---------------- LC 36 ----------------
    public static boolean isValidSudoku(char[][] board) {
        int[] rows = new int[9], cols = new int[9], boxes = new int[9];
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                char ch = board[r][c];
                if (ch == '.') continue;
                int bit = 1 << (ch - '1');
                int b = (r / 3) * 3 + c / 3;
                if ((rows[r] & bit) != 0 || (cols[c] & bit) != 0 || (boxes[b] & bit) != 0) return false;
                rows[r] |= bit; cols[c] |= bit; boxes[b] |= bit;
            }
        }
        return true;
    }

    // ---------------- LC 37 ----------------
    private static int[] rows, cols, boxes;

    public static void solveSudoku(char[][] board) {
        rows = new int[9]; cols = new int[9]; boxes = new int[9];
        for (int r = 0; r < 9; r++)
            for (int c = 0; c < 9; c++)
                if (board[r][c] != '.') place(r, c, board[r][c] - '1', true);
        solve(board);
    }

    private static void place(int r, int c, int d, boolean on) {
        int bit = 1 << d, b = (r / 3) * 3 + c / 3;
        if (on) { rows[r] |= bit; cols[c] |= bit; boxes[b] |= bit; }
        else { rows[r] &= ~bit; cols[c] &= ~bit; boxes[b] &= ~bit; }
    }

    private static boolean solve(char[][] board) {
        // MRV: choose the empty cell with the fewest candidates
        int bestR = -1, bestC = -1, bestMask = 0, bestCount = 10;
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board[r][c] != '.') continue;
                int mask = ~(rows[r] | cols[c] | boxes[(r / 3) * 3 + c / 3]) & 0x1FF;
                int cnt = Integer.bitCount(mask);
                if (cnt < bestCount) { bestCount = cnt; bestR = r; bestC = c; bestMask = mask; }
            }
        }
        if (bestR == -1) return true;   // no empty cell -> solved
        if (bestCount == 0) return false;
        for (int m = bestMask; m != 0; m &= m - 1) {
            int d = Integer.numberOfTrailingZeros(m);
            board[bestR][bestC] = (char) ('1' + d);
            place(bestR, bestC, d, true);
            if (solve(board)) return true;
            place(bestR, bestC, d, false);
        }
        board[bestR][bestC] = '.';
        return false;
    }

    public static void main(String[] args) {
        String[] rowsStr = {
            "53..7....", "6..195...", ".98....6.",
            "8...6...3", "4..8.3..1", "7...2...6",
            ".6....28.", "...419..5", "....8..79"
        };
        char[][] board = new char[9][];
        for (int i = 0; i < 9; i++) board[i] = rowsStr[i].toCharArray();
        System.out.println("valid: " + isValidSudoku(board)); // true
        solveSudoku(board);
        for (char[] row : board) System.out.println(new String(row));
        System.out.println("solved still valid: " + isValidSudoku(board));

        board[0][1] = board[0][0]; // duplicate in row
        System.out.println("valid after corruption: " + isValidSudoku(board)); // false
    }
}
