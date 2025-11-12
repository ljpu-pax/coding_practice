package robinhood;

public class WaysToSplit {
    public int waysToSplit(int[] nums) {
        int n = nums.length;
        int MOD = 1_000_000_007;

        // Step 1: Build prefix sum
        int[] prefix = new int[n + 1]; // prefix[i] = sum(nums[0]..nums[i-1])
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + nums[i];
        }

        int totalWays = 0;

        for (int i = 1; i <= n - 2; i++) {
            int leftSum = prefix[i];

            // Binary search lower bound j1
            int low = i + 1, high = n - 1;
            int j1 = -1;
            while (low <= high) {
                int mid = (low + high) / 2;
                int midSum = prefix[mid] - leftSum;
                if (midSum >= leftSum) {
                    j1 = mid;
                    high = mid - 1;
                } else {
                    low = mid + 1;
                }
            }

            // Binary search upper bound j2
            low = i + 1; high = n - 1;
            int j2 = -1;
            while (low <= high) {
                int mid = (low + high) / 2;
                int midSum = prefix[mid] - leftSum;
                int rightSum = prefix[n] - prefix[mid];
                if (rightSum >= midSum) {
                    j2 = mid;
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }

            if (j1 != -1 && j2 != -1 && j1 <= j2) {
                totalWays = (totalWays + (j2 - j1 + 1)) % MOD;
            }
        }

        return totalWays;
    }

    public static void main(String[] args) {
        WaysToSplit s = new WaysToSplit();
        int[] nums = {1, 2, 2, 2, 5, 0};
        System.out.println(s.waysToSplit(nums)); // Output: 3
    }
}
