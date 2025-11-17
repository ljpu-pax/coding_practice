// LeetCode 322: Coin Change
// https://leetcode.com/problems/coin-change/
// Difficulty: Medium

// You are given an integer array coins representing coins of different denominations
// and an integer amount representing a total amount of money.

// Return the fewest number of coins that you need to make up that amount.
// If that amount of money cannot be made up by any combination of the coins, return -1.

// You may assume that you have an infinite number of each kind of coin.

// Example 1:
// Input: coins = [1,2,5], amount = 11
// Output: 3
// Explanation: 11 = 5 + 5 + 1

// Example 2:
// Input: coins = [2], amount = 3
// Output: -1

// Example 3:
// Input: coins = [1], amount = 0
// Output: 0

import java.util.*;

class CoinChange {
    // Dynamic Programming - Bottom Up approach
    // Time: O(amount * n), Space: O(amount)
    public int coinChange(int[] coins, int amount) {
        // dp[i] = minimum number of coins needed to make amount i
        int[] dp = new int[amount + 1];

        // Initialize with a value larger than any possible answer
        Arrays.fill(dp, amount + 1);

        // Base case: 0 coins needed to make amount 0
        dp[0] = 0;

        // For each amount from 1 to target amount
        for (int i = 1; i <= amount; i++) {
            // Try each coin
            for (int coin : coins) {
                if (coin <= i) {
                    // Update dp[i] if using this coin gives fewer coins
                    dp[i] = Math.min(dp[i], dp[i - coin] + 1);
                }
            }
        }

        // If dp[amount] is still the initial value, it's impossible
        return dp[amount] > amount ? -1 : dp[amount];
    }

    // Alternative: DFS with memoization (Top-Down DP)
    // Time: O(amount * n), Space: O(amount)
    public int coinChangeDFS(int[] coins, int amount) {
        if (amount == 0) return 0;

        // memo[i] = minimum coins needed for amount i, -2 means not computed yet
        int[] memo = new int[amount + 1];
        Arrays.fill(memo, -2);
        memo[0] = 0;

        return dfs(coins, amount, memo);
    }

    private int dfs(int[] coins, int amount, int[] memo) {
        if (amount < 0) return -1;
        if (memo[amount] != -2) return memo[amount];

        int minCoins = Integer.MAX_VALUE;

        for (int coin : coins) {
            int result = dfs(coins, amount - coin, memo);
            if (result >= 0) {
                minCoins = Math.min(minCoins, result + 1);
            }
        }

        memo[amount] = (minCoins == Integer.MAX_VALUE) ? -1 : minCoins;
        return memo[amount];
    }

    // BFS approach - finds shortest path
    // Time: O(amount * n), Space: O(amount)
    public int coinChangeBFS(int[] coins, int amount) {
        if (amount == 0) return 0;

        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        queue.offer(0);
        visited.add(0);

        int steps = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            steps++;

            for (int i = 0; i < size; i++) {
                int curr = queue.poll();

                for (int coin : coins) {
                    int next = curr + coin;

                    if (next == amount) {
                        return steps;
                    }

                    if (next < amount && !visited.contains(next)) {
                        queue.offer(next);
                        visited.add(next);
                    }
                }
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        CoinChange solution = new CoinChange();

        // Test cases
        System.out.println(solution.coinChange(new int[]{1, 2, 5}, 11));  // 3
        System.out.println(solution.coinChange(new int[]{2}, 3));         // -1
        System.out.println(solution.coinChange(new int[]{1}, 0));         // 0
        System.out.println(solution.coinChange(new int[]{1, 2, 5}, 100)); // 20

        // Test DFS approach
        System.out.println("\nDFS approach:");
        System.out.println(solution.coinChangeDFS(new int[]{1, 2, 5}, 11)); // 3

        // Test BFS approach
        System.out.println("\nBFS approach:");
        System.out.println(solution.coinChangeBFS(new int[]{1, 2, 5}, 11)); // 3
    }
}
