// LeetCode 256: Paint House
// https://leetcode.com/problems/paint-house/
// Difficulty: Medium
// Premium Problem

// There is a row of n houses, where each house can be painted one of three colors:
// red, blue, or green. The cost of painting each house with a certain color is different.
// You have to paint all the houses such that no two adjacent houses have the same color.

// The cost of painting each house with a certain color is represented by an n x 3 cost matrix costs.
// - costs[0][0] is the cost of painting house 0 with the color red
// - costs[1][2] is the cost of painting house 1 with the color green
// - and so on...

// Return the minimum cost to paint all houses.

// Example 1:
// Input: costs = [[17,2,17],[16,16,5],[14,3,19]]
// Output: 10
// Explanation: Paint house 0 into blue, paint house 1 into green, paint house 2 into blue.
// Minimum cost: 2 + 5 + 3 = 10.

// Example 2:
// Input: costs = [[7,6,2]]
// Output: 2

import java.util.*;

class PaintHouse {
    // Dynamic Programming approach - O(n) time, O(1) space
    // Modify input array in-place
    public int minCost(int[][] costs) {
        if (costs == null || costs.length == 0) {
            return 0;
        }

        int n = costs.length;

        // For each house starting from the second one
        for (int i = 1; i < n; i++) {
            // Cost of painting current house red = cost[i][0] + min(previous blue, previous green)
            costs[i][0] += Math.min(costs[i - 1][1], costs[i - 1][2]);

            // Cost of painting current house blue = cost[i][1] + min(previous red, previous green)
            costs[i][1] += Math.min(costs[i - 1][0], costs[i - 1][2]);

            // Cost of painting current house green = cost[i][2] + min(previous red, previous blue)
            costs[i][2] += Math.min(costs[i - 1][0], costs[i - 1][1]);
        }

        // Return the minimum cost among the three colors for the last house
        return Math.min(costs[n - 1][0], Math.min(costs[n - 1][1], costs[n - 1][2]));
    }

    // Dynamic Programming without modifying input - O(n) time, O(n) space
    public int minCostNoModify(int[][] costs) {
        if (costs == null || costs.length == 0) {
            return 0;
        }

        int n = costs.length;
        int[][] dp = new int[n][3];

        // Base case: first house
        dp[0][0] = costs[0][0];
        dp[0][1] = costs[0][1];
        dp[0][2] = costs[0][2];

        // Fill dp table
        for (int i = 1; i < n; i++) {
            dp[i][0] = costs[i][0] + Math.min(dp[i - 1][1], dp[i - 1][2]);
            dp[i][1] = costs[i][1] + Math.min(dp[i - 1][0], dp[i - 1][2]);
            dp[i][2] = costs[i][2] + Math.min(dp[i - 1][0], dp[i - 1][1]);
        }

        return Math.min(dp[n - 1][0], Math.min(dp[n - 1][1], dp[n - 1][2]));
    }

    // Space optimized DP - O(n) time, O(1) space
    // Use only two variables to track previous costs
    public int minCostOptimized(int[][] costs) {
        if (costs == null || costs.length == 0) {
            return 0;
        }

        int n = costs.length;
        int red = costs[0][0];
        int blue = costs[0][1];
        int green = costs[0][2];

        for (int i = 1; i < n; i++) {
            int prevRed = red;
            int prevBlue = blue;
            int prevGreen = green;

            red = costs[i][0] + Math.min(prevBlue, prevGreen);
            blue = costs[i][1] + Math.min(prevRed, prevGreen);
            green = costs[i][2] + Math.min(prevRed, prevBlue);
        }

        return Math.min(red, Math.min(blue, green));
    }

    public static void main(String[] args) {
        PaintHouse solution = new PaintHouse();

        // Test case 1
        int[][] costs1 = {{17, 2, 17}, {16, 16, 5}, {14, 3, 19}};
        System.out.println("Test 1: " + solution.minCostNoModify(costs1)); // 10

        // Test case 2
        int[][] costs2 = {{7, 6, 2}};
        System.out.println("Test 2: " + solution.minCostNoModify(costs2)); // 2

        // Test case 3
        int[][] costs3 = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        System.out.println("Test 3: " + solution.minCostNoModify(costs3)); // 12 (1+5+6 or 2+4+9)

        // Test optimized version
        System.out.println("\nOptimized version:");
        int[][] costs4 = {{17, 2, 17}, {16, 16, 5}, {14, 3, 19}};
        System.out.println("Test 4: " + solution.minCostOptimized(costs4)); // 10

        // Test in-place version (note: this modifies the input)
        System.out.println("\nIn-place version:");
        int[][] costs5 = {{17, 2, 17}, {16, 16, 5}, {14, 3, 19}};
        System.out.println("Test 5: " + solution.minCost(costs5)); // 10
    }
}

/*
 * Key Insights:
 *
 * 1. This is a classic dynamic programming problem
 * 2. State: dp[i][j] = minimum cost to paint houses 0 to i with house i painted color j
 * 3. Transition: dp[i][j] = costs[i][j] + min(dp[i-1][k]) where k != j
 * 4. Since we only need the previous row, we can optimize space to O(1)
 *
 * Time Complexity: O(n) where n is the number of houses
 * Space Complexity: O(1) for optimized version, O(n) for standard DP
 *
 * The key constraint is that no two adjacent houses can have the same color,
 * which means when painting house i with color j, we must choose the minimum
 * cost from the previous house with a different color.
 */
