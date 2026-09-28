import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 54: Spiral Matrix
 *
 * Given an m x n matrix, return all elements of the matrix in spiral order.
 *
 * Example 1:
 * Input: matrix = [[1,2,3],[4,5,6],[7,8,9]]
 * Output: [1,2,3,6,9,8,7,4,5]
 *
 * Example 2:
 * Input: matrix = [[1,2,3,4],[5,6,7,8],[9,10,11,12]]
 * Output: [1,2,3,4,8,12,11,10,9,5,6,7]
 *
 * Constraints:
 * - m == matrix.length
 * - n == matrix[i].length
 * - 1 <= m, n <= 10
 * - -100 <= matrix[i][j] <= 100
 */
public class SpiralMatrix {

    /**
     * Layer-by-layer approach
     * Time Complexity: O(m * n) where m is number of rows and n is number of columns
     * Space Complexity: O(1) excluding the output list
     */
    public List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        if (matrix == null || matrix.length == 0) {
            return result;
        }

        int top = 0;
        int bottom = matrix.length - 1;
        int left = 0;
        int right = matrix[0].length - 1;

        while (top <= bottom && left <= right) {
            // Traverse right along the top row
            for (int col = left; col <= right; col++) {
                result.add(matrix[top][col]);
            }
            top++;

            // Traverse down along the right column
            for (int row = top; row <= bottom; row++) {
                result.add(matrix[row][right]);
            }
            right--;

            // Traverse left along the bottom row (if there's a row remaining)
            if (top <= bottom) {
                for (int col = right; col >= left; col--) {
                    result.add(matrix[bottom][col]);
                }
                bottom--;
            }

            // Traverse up along the left column (if there's a column remaining)
            if (left <= right) {
                for (int row = bottom; row >= top; row--) {
                    result.add(matrix[row][left]);
                }
                left++;
            }
        }

        return result;
    }

    /**
     * Direction-based approach using state machine
     */
    public List<Integer> spiralOrderDirectional(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        if (matrix == null || matrix.length == 0) {
            return result;
        }

        int m = matrix.length;
        int n = matrix[0].length;
        boolean[][] visited = new boolean[m][n];

        // Direction vectors: right, down, left, up
        int[] dr = {0, 1, 0, -1};
        int[] dc = {1, 0, -1, 0};

        int r = 0, c = 0, dir = 0;

        for (int i = 0; i < m * n; i++) {
            result.add(matrix[r][c]);
            visited[r][c] = true;

            // Calculate next position
            int nextR = r + dr[dir];
            int nextC = c + dc[dir];

            // Check if we need to change direction
            if (nextR < 0 || nextR >= m || nextC < 0 || nextC >= n || visited[nextR][nextC]) {
                // Change direction clockwise
                dir = (dir + 1) % 4;
                nextR = r + dr[dir];
                nextC = c + dc[dir];
            }

            r = nextR;
            c = nextC;
        }

        return result;
    }

    public static void main(String[] args) {
        SpiralMatrix solution = new SpiralMatrix();

        // Test case 1
        int[][] matrix1 = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        System.out.println("Test 1: " + solution.spiralOrder(matrix1));
        // Expected: [1, 2, 3, 6, 9, 8, 7, 4, 5]

        // Test case 2
        int[][] matrix2 = {{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}};
        System.out.println("Test 2: " + solution.spiralOrder(matrix2));
        // Expected: [1, 2, 3, 4, 8, 12, 11, 10, 9, 5, 6, 7]

        // Test case 3: Single row
        int[][] matrix3 = {{1, 2, 3, 4}};
        System.out.println("Test 3: " + solution.spiralOrder(matrix3));
        // Expected: [1, 2, 3, 4]

        // Test case 4: Single column
        int[][] matrix4 = {{1}, {2}, {3}, {4}};
        System.out.println("Test 4: " + solution.spiralOrder(matrix4));
        // Expected: [1, 2, 3, 4]

        // Test case 5: Single element
        int[][] matrix5 = {{1}};
        System.out.println("Test 5: " + solution.spiralOrder(matrix5));
        // Expected: [1]
    }
}
