import java.util.*;

public class MineFieldGenerator {
    public static boolean[][] generateMines(int rows, int cols, int numMines) {
        if (numMines > rows * cols) {
            throw new IllegalArgumentException("Number of Mines exceeds total cells");
        }

        boolean[][] board = new boolean[rows][cols];
        List<Integer> positions = new ArrayList<>();

        for (int i = 0; i < rows * cols; i++) {
            positions.add(i);
        }

        Collections.shuffle(positions);

        for (int i = 0; i < numMines; i++) {
            int pos = positions.get(i);
            int r = pos / cols;
            int c = pos % cols;

            board[r][c] = true;
        }

        return board;
    }

    public static void main(String[] args) {
        int rows = 5;
        int cols = 5;
        int numMines = 10;

        boolean[][] minefield = generateMines(rows, cols, numMines);

        // 打印生成的地雷布置
        for (boolean[] row : minefield) {
            for (boolean cell : row) {
                System.out.print(cell ? "M " : ". ");
            }
            System.out.println();
        }
    }
}
