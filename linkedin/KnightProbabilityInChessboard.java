import java.util.*;

/**
 * LeetCode 688. Knight Probability in Chessboard
 *
 * Problem:
 * On an n x n chessboard, a knight starts at position (row, column).
 * The knight makes exactly k moves. Each move, it chooses one of 8 possible
 * moves uniformly at random and moves there (even if it goes off the board).
 *
 * Return the probability that the knight remains on the board after k moves.
 *
 * Example 1:
 * Input: n = 3, k = 2, row = 0, column = 0
 * Output: 0.06250
 * Explanation: There are 8^2 = 64 possible sequences of moves.
 * 4 of them keep the knight on the board.
 *
 * Example 2:
 * Input: n = 1, k = 0, row = 0, column = 0
 * Output: 1.00000
 *
 * Constraints:
 * - 1 <= n <= 25
 * - 0 <= k <= 100
 * - 0 <= row, column < n
 *
 * Time Complexity: O(k * n^2)
 * Space Complexity: O(n^2)
 */

class KnightProbabilityInChessboard {

    // 8 possible knight moves
    private static final int[][] DIRS = {
        {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
        {1, -2}, {1, 2}, {2, -1}, {2, 1}
    };

    /**
     * Solution 1: DP with Memoization (Top-Down)
     * Time: O(k * n^2)
     * Space: O(k * n^2)
     *
     * State: dp[moves][r][c] = probability of staying on board
     *        after 'moves' moves starting from (r, c)
     */
    public double knightProbability(int n, int k, int row, int column) {
        Double[][][] memo = new Double[k + 1][n][n];
        return dfs(n, k, row, column, memo);
    }

    private double dfs(int n, int k, int r, int c, Double[][][] memo) {
        // Out of bounds
        if (r < 0 || r >= n || c < 0 || c >= n) {
            return 0.0;
        }

        // No more moves, still on board
        if (k == 0) {
            return 1.0;
        }

        // Already computed
        if (memo[k][r][c] != null) {
            return memo[k][r][c];
        }

        double prob = 0.0;

        // Try all 8 moves
        for (int[] dir : DIRS) {
            int nr = r + dir[0];
            int nc = c + dir[1];
            prob += dfs(n, k - 1, nr, nc, memo);
        }

        // Each move has 1/8 probability
        prob /= 8.0;

        memo[k][r][c] = prob;
        return prob;
    }

    /**
     * Solution 2: DP (Bottom-Up)
     * Time: O(k * n^2)
     * Space: O(n^2)
     *
     * Use two arrays to save space (current and previous state)
     */
    public double knightProbabilityBottomUp(int n, int k, int row, int column) {
        // dp[r][c] = probability of being at (r, c) after i moves
        double[][] dp = new double[n][n];
        dp[row][column] = 1.0;

        for (int move = 0; move < k; move++) {
            double[][] next = new double[n][n];

            for (int r = 0; r < n; r++) {
                for (int c = 0; c < n; c++) {
                    if (dp[r][c] > 0) {
                        // Try all 8 moves from (r, c)
                        for (int[] dir : DIRS) {
                            int nr = r + dir[0];
                            int nc = c + dir[1];

                            if (nr >= 0 && nr < n && nc >= 0 && nc < n) {
                                // Add probability of reaching (nr, nc) from (r, c)
                                next[nr][nc] += dp[r][c] / 8.0;
                            }
                        }
                    }
                }
            }

            dp = next;
        }

        // Sum all probabilities on the board
        double totalProb = 0.0;
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                totalProb += dp[r][c];
            }
        }

        return totalProb;
    }

    /**
     * Solution 3: DP (3D Array - Clearer but More Space)
     * Time: O(k * n^2)
     * Space: O(k * n^2)
     */
    public double knightProbability3D(int n, int k, int row, int column) {
        // dp[moves][r][c] = probability of being at (r, c) after 'moves' moves
        double[][][] dp = new double[k + 1][n][n];
        dp[0][row][column] = 1.0;

        for (int move = 0; move < k; move++) {
            for (int r = 0; r < n; r++) {
                for (int c = 0; c < n; c++) {
                    if (dp[move][r][c] > 0) {
                        for (int[] dir : DIRS) {
                            int nr = r + dir[0];
                            int nc = c + dir[1];

                            if (nr >= 0 && nr < n && nc >= 0 && nc < n) {
                                dp[move + 1][nr][nc] += dp[move][r][c] / 8.0;
                            }
                        }
                    }
                }
            }
        }

        // Sum all probabilities after k moves
        double totalProb = 0.0;
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                totalProb += dp[k][r][c];
            }
        }

        return totalProb;
    }
}

/**
 * Test Cases
 */
class KnightProbabilityInChessboardTest {
    public static void main(String[] args) {
        KnightProbabilityInChessboard solution = new KnightProbabilityInChessboard();

        // Test 1
        int n1 = 3, k1 = 2, row1 = 0, col1 = 0;
        System.out.println("Test 1: n=" + n1 + ", k=" + k1 + ", start=(" + row1 + "," + col1 + ")");
        System.out.printf("Top-Down: %.5f\n", solution.knightProbability(n1, k1, row1, col1));
        System.out.printf("Bottom-Up: %.5f\n", solution.knightProbabilityBottomUp(n1, k1, row1, col1));
        System.out.printf("3D Array: %.5f\n", solution.knightProbability3D(n1, k1, row1, col1));
        System.out.println("Expected: 0.06250\n");

        // Test 2
        int n2 = 1, k2 = 0, row2 = 0, col2 = 0;
        System.out.println("Test 2: n=" + n2 + ", k=" + k2 + ", start=(" + row2 + "," + col2 + ")");
        System.out.printf("Top-Down: %.5f\n", solution.knightProbability(n2, k2, row2, col2));
        System.out.println("Expected: 1.00000\n");

        // Test 3: Knight in center
        int n3 = 8, k3 = 1, row3 = 4, col3 = 4;
        System.out.println("Test 3: n=" + n3 + ", k=" + k3 + ", start=(" + row3 + "," + col3 + ")");
        System.out.printf("Bottom-Up: %.5f\n", solution.knightProbabilityBottomUp(n3, k3, row3, col3));
        System.out.println("Expected: 1.00000 (all 8 moves stay on board)\n");

        // Test 4: Corner position
        int n4 = 3, k4 = 1, row4 = 0, col4 = 0;
        System.out.println("Test 4: n=" + n4 + ", k=" + k4 + ", start=(" + row4 + "," + col4 + ")");
        System.out.printf("Bottom-Up: %.5f\n", solution.knightProbabilityBottomUp(n4, k4, row4, col4));
        System.out.println("Expected: 0.25000 (2 out of 8 moves stay on board)\n");
    }
}

/**
 * Complexity Analysis:
 * ===================
 *
 * Solution 1 (Top-Down DP with Memoization):
 * - Time: O(k * n^2 * 8) = O(k * n^2)
 *   - k moves, n^2 positions, 8 directions each
 * - Space: O(k * n^2) for memo table + O(k) recursion stack
 * - Pros: Intuitive, only computes reachable states
 * - Cons: Recursion overhead
 *
 * Solution 2 (Bottom-Up DP with 2D Arrays): ⭐ BEST
 * - Time: O(k * n^2)
 * - Space: O(n^2) - only stores current and next state
 * - Pros: Space optimized, no recursion
 * - Cons: Computes all states even if not reachable
 *
 * Solution 3 (Bottom-Up DP with 3D Array):
 * - Time: O(k * n^2)
 * - Space: O(k * n^2)
 * - Pros: Clearer logic, can query any intermediate state
 * - Cons: More space
 *
 *
 * Key Insights:
 * ============
 *
 * 1. Probability Propagation:
 *    - Each position has a probability
 *    - Each move distributes probability to 8 neighbors (1/8 each)
 *    - Out-of-bounds moves lose that probability
 *
 * 2. Two Perspectives:
 *    a) Top-Down: "What's probability of staying on board from (r,c) with k moves?"
 *    b) Bottom-Up: "What's probability of being at (r,c) after k moves?"
 *
 * 3. State Transition:
 *    dp[move+1][nr][nc] += dp[move][r][c] / 8.0
 *    (Probability at next position accumulates from all valid previous positions)
 *
 *
 * Interview Tips:
 * ==============
 *
 * 1. Clarify the Problem:
 *    - Knight moves randomly (uniformly)
 *    - Must make exactly k moves
 *    - Can go off board (contributes to failure probability)
 *
 * 2. Start with Recursion:
 *    "From position (r,c) with k moves left:"
 *    - If out of bounds: return 0
 *    - If k=0 and on board: return 1
 *    - Otherwise: average of 8 recursive calls
 *
 * 3. Identify Overlapping Subproblems:
 *    "Same position can be reached via different paths"
 *    "Need memoization to avoid recomputation"
 *
 * 4. Bottom-Up Approach:
 *    "Start with probability 1 at initial position"
 *    "For each move, distribute probability to 8 neighbors"
 *    "Sum remaining probabilities after k moves"
 *
 * 5. Edge Cases:
 *    - k = 0 (no moves, probability = 1)
 *    - n = 1 (always fall off unless k = 0)
 *    - Knight starts at corner vs center
 *    - Large k (many moves)
 *
 * 6. Common Mistakes:
 *    - Forgetting to divide by 8 (each move has 1/8 probability)
 *    - Not handling out-of-bounds properly
 *    - Confusing "staying on board" vs "being at position"
 *
 * 7. Follow-up Questions:
 *    - What if knight can choose moves (not random)? → Different problem (pathfinding)
 *    - What if some cells are blocked? → Add check in transition
 *    - What if we want expected number of moves before falling off? → Modified DP
 *    - Different piece (bishop, rook)? → Change DIRS array
 *
 * 8. Optimization Notes:
 *    - Space: Use 2 arrays instead of 3D (rolling array technique)
 *    - Pruning: Skip cells with probability ≈ 0
 *    - For very large k: probability decays, can use threshold
 */
