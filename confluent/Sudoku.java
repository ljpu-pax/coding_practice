import java.util.*;

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

    // LC 36, one Set of encoded strings. Where the "(d)" sits tells row / col / box apart:
    //   "(5)4" = row 4 has 5, "7(5)" = col 7 has 5, "1(5)2" = box (1,2) has 5.
    // No box index math; a bit slower since it builds 3 strings per filled cell.
    public static boolean isValidSudokuOneSet(char[][] board) {
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == '.') continue;
                String b = "(" + board[i][j] + ")";
                if (!seen.add(b + i) || !seen.add(j + b) || !seen.add(i / 3 + b + j / 3)) return false;
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
        System.out.println("valid: " + isValidSudoku(board) + " / oneSet: " + isValidSudokuOneSet(board)); // true
        solveSudoku(board);
        for (char[] row : board) System.out.println(new String(row));
        System.out.println("solved still valid: " + isValidSudoku(board) + " / oneSet: " + isValidSudokuOneSet(board));

        // One duplicate of each kind; both versions must say false
        char[][][] bad = new char[3][][];
        for (int k = 0; k < 3; k++) {
            bad[k] = new char[9][];
            for (int i = 0; i < 9; i++) bad[k][i] = rowsStr[i].toCharArray();
        }
        bad[0][0][2] = '5';  // row 0 already has 5 at (0,0)
        bad[1][2][0] = '5';  // col 0 already has 5 at (0,0)
        bad[2][2][2] = '3';  // box 0 already has 3 at (0,1), different row and col
        String[] kind = {"row", "col", "box"};
        for (int k = 0; k < 3; k++)
            System.out.println("dup " + kind[k] + ": " + isValidSudoku(bad[k]) + " / oneSet: " + isValidSudokuOneSet(bad[k])); // false
    }
}
