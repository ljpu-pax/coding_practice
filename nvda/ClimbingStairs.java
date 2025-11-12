package nvda;

import java.util.*;

public class ClimbingStairs {
    
    // LeetCode 70: Climbing Stairs
    // Fibonacci sequence - O(n) time, O(1) space
    public int climbStairs(int n) {
        if (n <= 2) return n;
        
        int prev2 = 1; // ways to reach step 1
        int prev1 = 2; // ways to reach step 2
        
        for (int i = 3; i <= n; i++) {
            int current = prev1 + prev2;
            prev2 = prev1;
            prev1 = current;
        }
        
        return prev1;
    }

    // LeetCode 746: Min Cost Climbing Stairs
    // DP with space optimization - O(n) time, O(1) space
    public int minCostClimbingStairs(int[] cost) {
        int prev2 = 0; // cost to reach step 0
        int prev1 = 0; // cost to reach step 1
        
        for (int i = 2; i <= cost.length; i++) {
            int current = Math.min(prev1 + cost[i - 1], prev2 + cost[i - 2]);
            prev2 = prev1;
            prev1 = current;
        }
        
        return prev1;
    }

    // LeetCode 198: House Robber
    // DP with space optimization - O(n) time, O(1) space
    public int rob(int[] nums) {
        if (nums.length == 1) return nums[0];
        
        int prev2 = nums[0]; // max money up to house 0
        int prev1 = Math.max(nums[0], nums[1]); // max money up to house 1
        
        for (int i = 2; i < nums.length; i++) {
            int current = Math.max(prev1, prev2 + nums[i]);
            prev2 = prev1;
            prev1 = current;
        }
        
        return prev1;
    }

    // LeetCode 213: House Robber II (Circular)
    // Two cases: rob first house or not - O(n) time, O(1) space
    public int robCircular(int[] nums) {
        if (nums.length == 1) return nums[0];
        if (nums.length == 2) return Math.max(nums[0], nums[1]);
        
        // Case 1: Rob first house, can't rob last
        int robFirst = robHelper(nums, 0, nums.length - 2);
        
        // Case 2: Don't rob first house, can rob last
        int notRobFirst = robHelper(nums, 1, nums.length - 1);
        
        return Math.max(robFirst, notRobFirst);
    }
    
    private int robHelper(int[] nums, int start, int end) {
        int prev2 = 0;
        int prev1 = 0;
        
        for (int i = start; i <= end; i++) {
            int current = Math.max(prev1, prev2 + nums[i]);
            prev2 = prev1;
            prev1 = current;
        }
        
        return prev1;
    }

    // LeetCode 64: Minimum Path Sum
    // DP with space optimization - O(m*n) time, O(n) space
    public int minPathSum(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[] dp = new int[n];
        
        // Initialize first row
        dp[0] = grid[0][0];
        for (int j = 1; j < n; j++) {
            dp[j] = dp[j - 1] + grid[0][j];
        }
        
        // Fill remaining rows
        for (int i = 1; i < m; i++) {
            dp[0] += grid[i][0];
            for (int j = 1; j < n; j++) {
                dp[j] = Math.min(dp[j], dp[j - 1]) + grid[i][j];
            }
        }
        
        return dp[n - 1];
    }

    // LeetCode 62: Unique Paths
    // DP with space optimization - O(m*n) time, O(n) space
    public int uniquePaths(int m, int n) {
        int[] dp = new int[n];
        Arrays.fill(dp, 1);
        
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[j] += dp[j - 1];
            }
        }
        
        return dp[n - 1];
    }

    // LeetCode 63: Unique Paths II (with obstacles)
    // DP with space optimization - O(m*n) time, O(n) space
    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        int m = obstacleGrid.length, n = obstacleGrid[0].length;
        int[] dp = new int[n];
        
        // Initialize first row
        dp[0] = obstacleGrid[0][0] == 1 ? 0 : 1;
        for (int j = 1; j < n; j++) {
            dp[j] = (obstacleGrid[0][j] == 1) ? 0 : dp[j - 1];
        }
        
        // Fill remaining rows
        for (int i = 1; i < m; i++) {
            dp[0] = (obstacleGrid[i][0] == 1) ? 0 : dp[0];
            for (int j = 1; j < n; j++) {
                dp[j] = (obstacleGrid[i][j] == 1) ? 0 : dp[j] + dp[j - 1];
            }
        }
        
        return dp[n - 1];
    }

    // LeetCode 322: Coin Change
    // DP - O(amount * coins.length) time, O(amount) space
    public int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;
        
        for (int i = 1; i <= amount; i++) {
            for (int coin : coins) {
                if (coin <= i) {
                    dp[i] = Math.min(dp[i], dp[i - coin] + 1);
                }
            }
        }
        
        return dp[amount] > amount ? -1 : dp[amount];
    }

    public static void main(String[] args) {
        ClimbingStairs solver = new ClimbingStairs();
        
        // Test Climbing Stairs
        System.out.println("=== Climbing Stairs (LeetCode 70) ===");
        for (int n = 1; n <= 5; n++) {
            System.out.println("n = " + n + " -> " + solver.climbStairs(n) + " ways");
        }
        System.out.println();
        
        // Test Min Cost Climbing Stairs
        System.out.println("=== Min Cost Climbing Stairs (LeetCode 746) ===");
        int[] cost1 = {10, 15, 20};
        int[] cost2 = {1, 100, 1, 1, 1, 100, 1, 1, 100, 1};
        System.out.println("Cost: " + Arrays.toString(cost1) + " -> " + solver.minCostClimbingStairs(cost1));
        System.out.println("Cost: " + Arrays.toString(cost2) + " -> " + solver.minCostClimbingStairs(cost2));
        System.out.println();
        
        // Test House Robber
        System.out.println("=== House Robber (LeetCode 198) ===");
        int[] houses1 = {1, 2, 3, 1};
        int[] houses2 = {2, 7, 9, 3, 1};
        System.out.println("Houses: " + Arrays.toString(houses1) + " -> " + solver.rob(houses1));
        System.out.println("Houses: " + Arrays.toString(houses2) + " -> " + solver.rob(houses2));
        System.out.println();
        
        // Test House Robber II
        System.out.println("=== House Robber II (LeetCode 213) ===");
        int[] houses3 = {2, 3, 2};
        int[] houses4 = {1, 2, 3, 1};
        System.out.println("Circular houses: " + Arrays.toString(houses3) + " -> " + solver.robCircular(houses3));
        System.out.println("Circular houses: " + Arrays.toString(houses4) + " -> " + solver.robCircular(houses4));
        System.out.println();
        
        // Test Minimum Path Sum
        System.out.println("=== Minimum Path Sum (LeetCode 64) ===");
        int[][] grid = {{1, 3, 1}, {1, 5, 1}, {4, 2, 1}};
        System.out.println("Grid: " + Arrays.deepToString(grid));
        System.out.println("Min path sum: " + solver.minPathSum(grid));
        System.out.println();
        
        // Test Unique Paths
        System.out.println("=== Unique Paths (LeetCode 62) ===");
        System.out.println("m=3, n=7 -> " + solver.uniquePaths(3, 7));
        System.out.println("m=3, n=2 -> " + solver.uniquePaths(3, 2));
        System.out.println();
        
        // Test Unique Paths II
        System.out.println("=== Unique Paths II (LeetCode 63) ===");
        int[][] obstacleGrid = {{0, 0, 0}, {0, 1, 0}, {0, 0, 0}};
        System.out.println("Obstacle grid: " + Arrays.deepToString(obstacleGrid));
        System.out.println("Unique paths: " + solver.uniquePathsWithObstacles(obstacleGrid));
        System.out.println();
        
        // Test Coin Change
        System.out.println("=== Coin Change (LeetCode 322) ===");
        int[] coins = {1, 3, 4};
        int amount = 6;
        System.out.println("Coins: " + Arrays.toString(coins) + ", Amount: " + amount);
        System.out.println("Min coins: " + solver.coinChange(coins, amount));
    }
}
