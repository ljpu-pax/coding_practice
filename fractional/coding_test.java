package fractional;


// drawing program
// print a rectangle to the screen
// prompt user with line segment: user enter to where to draw line, add to internal page
// print the graph
// then user enter the line segment

// we need a grid with the board, output the board
// start of the line (x, y) end of line (x1, y1)
public class coding_test {
    char[][] grid;

    public void initialize(int row, int col) {
        if (row <= 0 || col <= 0) {
            System.out.println("the input is not valid");
            return;
        }
        grid = new char[row][col];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                grid[i][j] = ' ';
            }
        }
        
        
        // initialize the boarder 
        for (int i = 0; i < col; i++) {
            grid[0][i] = '-';
            grid[row - 1][i] = '-';
        }

        for (int m = 1; m < row - 1; m++) {
            grid[m][0] = '|';
            grid[m][col - 1] = '|';
        }
    }

    public void drawLine(int start_x, int start_y, int end_x, int end_y) {
        if (grid == null) {
            System.out.println("grid not initialized");
            return;
        }

        // 防御性检查
        if (start_x < 0 || start_y < 0 || end_x >= grid.length || end_y >= grid[0].length) {
            System.out.println("the input is not valid");
            return;
        }


        // case 1: horizontal line
        if (start_y == end_y) {
            for (int x = Math.min(start_x, end_x); x <= Math.max(start_x, end_x); x++) {
                grid[start_y][x] = '.';
            }
            return;
        }

        // case 2: vertical line
        if (start_x == end_x) {
            for (int y = Math.min(start_y, end_y); y <= Math.max(start_y, end_y); y++) {
                grid[y][start_x] = '.';
            }
            return;
        }

        // case 3: diagonal line (45 degrees)
        int dx = end_x > start_x ? 1 : -1;
        int dy = end_y > start_y ? 1 : -1;
        int x = start_x;
        int y = start_y;

        while (true) {
            grid[y][x] = '.';
            if (x == end_x && y == end_y) break;
            x += dx;
            y += dy;
        }
    }


    public void printGrid() {
        int row = grid.length;
        int col = grid[0].length;

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                System.out.print(grid[i][j]);
            }
            System.out.println();
        }
    }
    public static void main(String[] args) {
        coding_test t = new coding_test();
        t.initialize(10, 10);

        //System.out.print(t.grid);

        // t.printGrid();

        t.drawLine(2, 5, 3, 8);
        t.printGrid();


        // t.drawLine(3, 5, 5, 5);
        // t.printGrid();

    }
}
