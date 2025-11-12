import java.util.*;

/**
 * LeetCode 311. Sparse Matrix Multiplication
 *
 * Problem:
 * Given two sparse matrices mat1 (m x k) and mat2 (k x n), return mat1 x mat2
 *
 * A sparse matrix is a matrix with many zeros.
 *
 * Example:
 * mat1 = [[1,0,0],
 *         [-1,0,3]]
 *
 * mat2 = [[7,0,0],
 *         [0,0,0],
 *         [0,0,1]]
 *
 * Output: [[7,0,0],
 *          [-7,0,3]]
 *
 * Key Insight for Sparse Matrix:
 * - Skip zeros to optimize performance
 * - Only multiply when both elements are non-zero
 *
 * Time Complexity: O(m * k * n) worst case, but much better for sparse matrices
 * Space Complexity: O(m * n) for result matrix
 */

class SparseMatrixMultiplication {

    /**
     * Optimized Solution for Sparse Matrices
     *
     * Key Optimization:
     * - Check if mat1[i][k] == 0, if yes, skip entire inner loop
     * - Only compute when elements are non-zero
     *
     * Regular matrix multiplication:
     * result[i][j] = sum(mat1[i][k] * mat2[k][j]) for all k
     *
     * For sparse matrices, we reorganize:
     * - For each non-zero element mat1[i][k]
     * - Update all result[i][j] by mat1[i][k] * mat2[k][j]
     */
    public int[][] multiply(int[][] mat1, int[][] mat2) {
        int m = mat1.length;       // rows of mat1
        int k = mat1[0].length;    // cols of mat1 = rows of mat2
        int n = mat2[0].length;    // cols of mat2

        int[][] result = new int[m][n];

        // For each row in mat1
        for (int i = 0; i < m; i++) {
            // For each column in mat1 (row in mat2)
            for (int p = 0; p < k; p++) {
                // Skip if mat1[i][p] is zero (optimization for sparse matrix)
                if (mat1[i][p] == 0) {
                    continue;
                }

                // For each column in mat2
                for (int j = 0; j < n; j++) {
                    // Skip if mat2[p][j] is zero (further optimization)
                    if (mat2[p][j] == 0) {
                        continue;
                    }

                    // Accumulate result
                    result[i][j] += mat1[i][p] * mat2[p][j];
                }
            }
        }

        return result;
    }

    /**
     * Alternative: Using HashMap for Very Sparse Matrices
     *
     * If matrix is extremely sparse, we can store only non-zero elements
     * Using format: Map<row, Map<col, value>>
     */
    public int[][] multiplyWithHashMap(int[][] mat1, int[][] mat2) {
        int m = mat1.length;
        int n = mat2[0].length;

        // Convert to sparse representation
        Map<Integer, Map<Integer, Integer>> sparse1 = toSparse(mat1);
        Map<Integer, Map<Integer, Integer>> sparse2 = toSparse(mat2);

        int[][] result = new int[m][n];

        // For each row in mat1
        for (int i : sparse1.keySet()) {
            // For each non-zero element in mat1[i]
            for (int p : sparse1.get(i).keySet()) {
                int val1 = sparse1.get(i).get(p);

                // If mat2[p] has non-zero elements
                if (sparse2.containsKey(p)) {
                    // For each non-zero element in mat2[p]
                    for (int j : sparse2.get(p).keySet()) {
                        int val2 = sparse2.get(p).get(j);
                        result[i][j] += val1 * val2;
                    }
                }
            }
        }

        return result;
    }

    private Map<Integer, Map<Integer, Integer>> toSparse(int[][] mat) {
        Map<Integer, Map<Integer, Integer>> sparse = new HashMap<>();

        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                if (mat[i][j] != 0) {
                    sparse.putIfAbsent(i, new HashMap<>());
                    sparse.get(i).put(j, mat[i][j]);
                }
            }
        }

        return sparse;
    }

    /**
     * Alternative: Using List of Triples
     *
     * Store each non-zero element as (row, col, value)
     */
    static class Element {
        int row, col, val;
        Element(int r, int c, int v) {
            row = r;
            col = c;
            val = v;
        }
    }

    public int[][] multiplyWithList(int[][] mat1, int[][] mat2) {
        int m = mat1.length;
        int n = mat2[0].length;

        // Extract non-zero elements
        List<Element> list1 = toList(mat1);
        List<Element> list2 = toList(mat2);

        // Index mat2 by row for faster lookup
        Map<Integer, List<Element>> mat2ByRow = new HashMap<>();
        for (Element e : list2) {
            mat2ByRow.putIfAbsent(e.row, new ArrayList<>());
            mat2ByRow.get(e.row).add(e);
        }

        int[][] result = new int[m][n];

        // For each non-zero element in mat1
        for (Element e1 : list1) {
            int i = e1.row;
            int p = e1.col;
            int val1 = e1.val;

            // Find corresponding row in mat2
            if (mat2ByRow.containsKey(p)) {
                for (Element e2 : mat2ByRow.get(p)) {
                    int j = e2.col;
                    int val2 = e2.val;
                    result[i][j] += val1 * val2;
                }
            }
        }

        return result;
    }

    private List<Element> toList(int[][] mat) {
        List<Element> list = new ArrayList<>();
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                if (mat[i][j] != 0) {
                    list.add(new Element(i, j, mat[i][j]));
                }
            }
        }
        return list;
    }
}

/**
 * Test Cases
 */
class SparseMatrixMultiplicationTest {
    public static void main(String[] args) {
        SparseMatrixMultiplication solution = new SparseMatrixMultiplication();

        // Test case 1
        int[][] mat1 = {
            {1, 0, 0},
            {-1, 0, 3}
        };

        int[][] mat2 = {
            {7, 0, 0},
            {0, 0, 0},
            {0, 0, 1}
        };

        int[][] result1 = solution.multiply(mat1, mat2);
        System.out.println("Test 1 (Basic method):");
        printMatrix(result1);
        // Expected: [[7,0,0], [-7,0,3]]

        int[][] result2 = solution.multiplyWithHashMap(mat1, mat2);
        System.out.println("\nTest 1 (HashMap method):");
        printMatrix(result2);

        int[][] result3 = solution.multiplyWithList(mat1, mat2);
        System.out.println("\nTest 1 (List method):");
        printMatrix(result3);

        // Test case 2
        int[][] mat3 = {{1, -5}};
        int[][] mat4 = {{12}, {-1}};
        int[][] result4 = solution.multiply(mat3, mat4);
        System.out.println("\nTest 2:");
        printMatrix(result4);
        // Expected: [[17]]
    }

    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            System.out.print("[");
            for (int i = 0; i < row.length; i++) {
                System.out.print(row[i]);
                if (i < row.length - 1) System.out.print(", ");
            }
            System.out.println("]");
        }
    }
}

/**
 * Complexity Analysis:
 * ===================
 *
 * Method 1 (Basic Optimization):
 * - Time: O(m * k * n) worst case, but O(m * nnz1 * n) for sparse matrices
 *   where nnz1 = number of non-zero elements in mat1
 * - Space: O(m * n) for result
 * - Best for moderately sparse matrices
 *
 * Method 2 (HashMap):
 * - Time: O(nnz1 * nnz2) where nnz = non-zero elements
 * - Space: O(nnz1 + nnz2 + m * n)
 * - Best for extremely sparse matrices
 *
 * Method 3 (List):
 * - Time: O(nnz1 * nnz2 / k) on average
 * - Space: O(nnz1 + nnz2 + m * n)
 * - Best for extremely sparse matrices with good distribution
 *
 * Interview Tips:
 * ==============
 *
 * 1. Start with basic optimization (check mat1[i][p] != 0)
 * 2. Mention that for very sparse matrices, HashMap/List approaches are better
 * 3. Discuss trade-offs between preprocessing time and multiplication time
 * 4. Ask about sparsity level to choose appropriate method
 *
 * Key Points to Mention:
 * - Regular matrix multiplication formula
 * - Why checking mat1[i][p] first is important (skip entire inner loop)
 * - Space-time tradeoff in different approaches
 */
