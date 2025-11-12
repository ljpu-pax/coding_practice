package robinhood;

import java.util.Arrays;

public class MineSweeper {
    private static final int[][] DIRS = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1},           {0, 1},
        {1, -1},  {1, 0},  {1, 1}
    };

    public char[][] updateBoard(char[][] board, int[] click) {
        int r = click[0], c = click[1];

        if (board[r][c] == 'M') {
            board[r][c] = 'X'; // Clicked on a mine
        } else {
            dfs(board, r, c);
        }

        return board;
    }

    private void dfs(char[][] board, int row, int col) {
        if (!inBounds(board, row, col) || board[row][col] != 'E') return;

        int count = countMinesAround(board, row, col);
        if (count > 0) {
            board[row][col] = (char)(count + '0');
        } else {
            board[row][col] = 'B';
            for (int[] dir : DIRS) {
                dfs(board, row + dir[0], col + dir[1]);
            }
        }
    }

    private int countMinesAround(char[][] board, int row, int col) {
        int count = 0;
        for (int[] dir : DIRS) {
            int nr = row + dir[0], nc = col + dir[1];
            if (inBounds(board, nr, nc) && board[nr][nc] == 'M') {
                count++;
            }
        }
        return count;
    }

    private boolean inBounds(char[][] board, int row, int col) {
        return row >= 0 && col >= 0 && row < board.length && col < board[0].length;
    }

    // 🧪 Main method for testing
    public static void main(String[] args) {
        MineSweeper solution = new MineSweeper();

        char[][] board = {
            {'E', 'E', 'E', 'E', 'E'},
            {'E', 'E', 'M', 'E', 'E'},
            {'E', 'E', 'E', 'E', 'E'},
            {'E', 'E', 'E', 'E', 'E'}
        };
        int[] click = {3, 0};

        char[][] updatedBoard = solution.updateBoard(board, click);

        System.out.println("Updated Board:");
        for (char[] row : updatedBoard) {
            System.out.println(Arrays.toString(row));
        }
    }
}

