package delphina;

public class ConnectFour{
    char[][] grid;
    private char[][] init(int rows, int cols) {
        grid = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = '-';
            }
        }

        return grid;
    }

    private void printBoard(char[][] board) {
        int len = board.length;

        for (int i = 0; i < len; i++) {
            System.out.println(board[i]);
        }
    }
    // player 1 is X, player 2 is O
    private void placePiece(int col, int player) {
        int rows = grid.length;

        for (int i = rows - 1; i >= 0; i--) {
            if (grid[i][col] == '-') {
                if (player == 1) {
                    grid[i][col] = 'X';
                    System.out.println("player 1 wins? :" + checkVertical(1, col));
                } else {
                    grid[i][col] = 'O';
                    System.out.println("player 2 wins? :" + checkVertical(2, col));
                }
                break;
            }
        }
    }

    // private boolean checkWin(int player) {
    //     // checkHorizon();
    //     // checkVertical();
    //     // checkDiagnol();
    // }

    private boolean checkHorizon(int player, int row) {
        char winChar = player == 1 ? 'X' : 'O';
        int count = 0;
        
        // O O X X X X O O
        char[] horizon = grid[row];
        for (int i = 0; i < horizon.length; i++) {
            if (grid[row][i] == winChar) {
                count++;
                if (count >= 4) return true;
            } else {
                count = 0;
            }
        }

        return count >= 4;
    }

    private boolean checkVertical(int player, int col) {
        char winChar = player == 1 ? 'X' : 'O';
        int count = 0;

        // X O
        // X O
        // X O
        // X O

        for (int i = grid.length - 1; i >= 0; i--) {
            if (grid[i][col] == winChar) {
                count++;
                System.out.println(count);
                if (count >= 4) return true;
            } else {
                count = 0;
            }
        }

        return count >= 4;
    }

    private boolean checkDiagnol(int player, int row, int col) {
        /*
           X   X
         *   X
         * X    X
         */ 
        char winChar = player == 1 ? 'X' : 'O';
        int count_1 = 0;
        int count_2 = 0;

        int i = row;
        int j = col;
        while (i >= 0 && i < grid.length && j >= 0 && j < grid[0].length) {
            
        } 

        
    }


    public static void main(String[] args) {
        ConnectFour cf = new ConnectFour();
        char[][] board = cf.init(6, 7);
        cf.printBoard(board);
        System.out.println("================");
        System.out.println("================");
        cf.placePiece(0, 1);
        cf.placePiece(0, 1);
        cf.placePiece(0, 1);
        cf.placePiece(0, 1);
        cf.printBoard(board);
    }
}
