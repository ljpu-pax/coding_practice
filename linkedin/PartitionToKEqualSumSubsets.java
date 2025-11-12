import java.util.*;

/**
 * LeetCode 698. Partition to K Equal Sum Subsets
 *
 * Problem:
 * Given an integer array nums and an integer k, return true if it is possible
 * to divide this array into k non-empty subsets whose sums are all equal.
 *
 * Example 1:
 * Input: nums = [4,3,2,3,5,2,1], k = 4
 * Output: true
 * Explanation: It's possible to divide it into 4 subsets (5), (1,4), (2,3), (2,3)
 * with equal sums.
 *
 * Example 2:
 * Input: nums = [1,2,3,4], k = 3
 * Output: false
 *
 * Constraints:
 * - 1 <= k <= nums.length <= 16
 * - 1 <= nums[i] <= 10^4
 * - Frequency of each element is in the range [1, 4]
 *
 * Time Complexity: O(k * 2^n) with memoization
 * Space Complexity: O(2^n)
 */

class PartitionToKEqualSumSubsets {

    /**
     * Solution 1: Backtracking (Bucket Perspective)
     * Time: O(k^n) worst case, much better with pruning
     * Space: O(n) for recursion stack
     *
     * Key Insight:
     * - Each subset needs sum = total / k
     * - Use k buckets, try to fill each to target sum
     * - Backtrack if can't fill a bucket
     */
    public boolean canPartitionKSubsets(int[] nums, int k) {
        int total = 0;
        for (int num : nums) {
            total += num;
        }

        // Quick checks
        if (total % k != 0) {
            return false;
        }

        int target = total / k;

        // Optimization: sort in descending order (place larger items first)
        Arrays.sort(nums);
        reverse(nums);

        // Optimization: if any number > target, impossible
        if (nums[0] > target) {
            return false;
        }

        boolean[] used = new boolean[nums.length];
        return backtrack(nums, used, 0, k, 0, target);
    }

    /**
     * Backtracking function
     * @param nums array of numbers
     * @param used which numbers have been used
     * @param index current position in nums
     * @param k number of buckets remaining to fill
     * @param currentSum current sum of active bucket
     * @param target target sum for each bucket
     */
    private boolean backtrack(int[] nums, boolean[] used, int index,
                              int k, int currentSum, int target) {
        // All buckets filled successfully
        if (k == 1) {
            return true; // Last bucket will automatically be valid
        }

        // Current bucket is full, start next bucket
        if (currentSum == target) {
            return backtrack(nums, used, 0, k - 1, 0, target);
        }

        // Try adding each unused number to current bucket
        for (int i = index; i < nums.length; i++) {
            if (used[i]) {
                continue;
            }

            // Pruning: skip if exceeds target
            if (currentSum + nums[i] > target) {
                continue;
            }

            // Pruning: skip duplicates to avoid redundant work
            if (i > 0 && nums[i] == nums[i - 1] && !used[i - 1]) {
                continue;
            }

            // Try using nums[i]
            used[i] = true;
            if (backtrack(nums, used, i + 1, k, currentSum + nums[i], target)) {
                return true;
            }
            used[i] = false; // Backtrack

            // Pruning: if first element in bucket doesn't work, no point continuing
            if (currentSum == 0) {
                break;
            }
        }

        return false;
    }

    private void reverse(int[] nums) {
        int left = 0, right = nums.length - 1;
        while (left < right) {
            int temp = nums[left];
            nums[left] = nums[right];
            nums[right] = temp;
            left++;
            right--;
        }
    }

    /**
     * Solution 2: Backtracking with Bitmask Memoization (Optimal)
     * Time: O(n * 2^n)
     * Space: O(2^n)
     *
     * Key Insight:
     * - Use bitmask to represent which numbers are used
     * - Memoize states to avoid recomputation
     * - State: (mask, currentSum) -> can partition remaining numbers?
     */
    public boolean canPartitionKSubsetsMemo(int[] nums, int k) {
        int total = 0;
        for (int num : nums) {
            total += num;
        }

        if (total % k != 0) {
            return false;
        }

        int target = total / k;

        // Optimization: sort descending
        Arrays.sort(nums);
        reverse(nums);

        if (nums[0] > target) {
            return false;
        }

        // Memoization: memo[mask] = can partition numbers in mask?
        Boolean[] memo = new Boolean[1 << nums.length];

        return backtrackMemo(nums, 0, 0, target, memo);
    }

    private boolean backtrackMemo(int[] nums, int mask, int currentSum,
                                   int target, Boolean[] memo) {
        // All numbers used
        if (mask == (1 << nums.length) - 1) {
            return true;
        }

        // Already computed
        if (memo[mask] != null) {
            return memo[mask];
        }

        // Reset currentSum when bucket is full
        if (currentSum >= target) {
            currentSum = 0;
        }

        // Try adding each unused number
        for (int i = 0; i < nums.length; i++) {
            // Skip if already used
            if ((mask & (1 << i)) != 0) {
                continue;
            }

            // Skip if exceeds target
            if (currentSum + nums[i] > target) {
                continue;
            }

            // Try using nums[i]
            if (backtrackMemo(nums, mask | (1 << i), currentSum + nums[i], target, memo)) {
                memo[mask] = true;
                return true;
            }
        }

        memo[mask] = false;
        return false;
    }

    /**
     * Solution 3: Dynamic Programming (Bitmask DP)
     * Time: O(n * 2^n)
     * Space: O(2^n)
     *
     * Bottom-up approach
     */
    public boolean canPartitionKSubsetsDP(int[] nums, int k) {
        int total = 0;
        for (int num : nums) {
            total += num;
        }

        if (total % k != 0) {
            return false;
        }

        int target = total / k;

        // dp[mask] = remaining sum needed to complete current bucket
        int[] dp = new int[1 << nums.length];
        Arrays.fill(dp, -1);
        dp[0] = 0; // No numbers used, no sum needed

        for (int mask = 0; mask < (1 << nums.length); mask++) {
            if (dp[mask] == -1) {
                continue; // Invalid state
            }

            for (int i = 0; i < nums.length; i++) {
                // Skip if already used
                if ((mask & (1 << i)) != 0) {
                    continue;
                }

                int newMask = mask | (1 << i);

                // Skip if already computed
                if (dp[newMask] != -1) {
                    continue;
                }

                // Add nums[i] to current bucket
                if (dp[mask] + nums[i] <= target) {
                    dp[newMask] = (dp[mask] + nums[i]) % target;
                }
            }
        }

        return dp[(1 << nums.length) - 1] == 0;
    }
}

/**
 * Follow-up: Return Actual Partitions
 *
 * Instead of just returning true/false, return the actual k subsets.
 */
class PartitionToKEqualSumSubsetsWithResult {

    public List<List<Integer>> partitionKSubsets(int[] nums, int k) {
        int total = 0;
        for (int num : nums) {
            total += num;
        }

        if (total % k != 0) {
            return new ArrayList<>(); // Empty means impossible
        }

        int target = total / k;

        // Sort descending
        Arrays.sort(nums);
        reverse(nums);

        if (nums[0] > target) {
            return new ArrayList<>();
        }

        // Track which bucket each number goes into
        List<List<Integer>> buckets = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            buckets.add(new ArrayList<>());
        }

        int[] bucketSums = new int[k];
        boolean[] used = new boolean[nums.length];

        if (backtrack(nums, used, buckets, bucketSums, 0, k, target)) {
            return buckets;
        }

        return new ArrayList<>();
    }

    private boolean backtrack(int[] nums, boolean[] used,
                              List<List<Integer>> buckets, int[] bucketSums,
                              int index, int k, int target) {
        // All numbers assigned
        if (index == nums.length) {
            // Verify all buckets have target sum
            for (int sum : bucketSums) {
                if (sum != target) {
                    return false;
                }
            }
            return true;
        }

        // Try assigning nums[index] to each bucket
        for (int i = 0; i < k; i++) {
            // Skip if adding would exceed target
            if (bucketSums[i] + nums[index] > target) {
                continue;
            }

            // Skip duplicate buckets (optimization)
            if (i > 0 && bucketSums[i] == bucketSums[i - 1]) {
                continue;
            }

            // Assign to bucket i
            buckets.get(i).add(nums[index]);
            bucketSums[i] += nums[index];
            used[index] = true;

            if (backtrack(nums, used, buckets, bucketSums, index + 1, k, target)) {
                return true;
            }

            // Backtrack
            buckets.get(i).remove(buckets.get(i).size() - 1);
            bucketSums[i] -= nums[index];
            used[index] = false;
        }

        return false;
    }

    private void reverse(int[] nums) {
        int left = 0, right = nums.length - 1;
        while (left < right) {
            int temp = nums[left];
            nums[left] = nums[right];
            nums[right] = temp;
            left++;
            right--;
        }
    }
}

/**
 * Alternative: Track Actual Subsets with Indices
 */
class PartitionWithIndices {

    public List<List<Integer>> partitionKSubsetsIndices(int[] nums, int k) {
        int total = 0;
        for (int num : nums) {
            total += num;
        }

        if (total % k != 0) {
            return new ArrayList<>();
        }

        int target = total / k;
        List<List<Integer>> result = new ArrayList<>();

        for (int i = 0; i < k; i++) {
            result.add(new ArrayList<>());
        }

        int[] bucketSums = new int[k];

        if (backtrackIndices(nums, 0, result, bucketSums, target)) {
            return result;
        }

        return new ArrayList<>();
    }

    private boolean backtrackIndices(int[] nums, int index,
                                     List<List<Integer>> result,
                                     int[] bucketSums, int target) {
        if (index == nums.length) {
            for (int sum : bucketSums) {
                if (sum != target) {
                    return false;
                }
            }
            return true;
        }

        for (int i = 0; i < result.size(); i++) {
            if (bucketSums[i] + nums[index] > target) {
                continue;
            }

            // Optimization: skip duplicate empty buckets
            if (i > 0 && bucketSums[i] == bucketSums[i - 1]) {
                continue;
            }

            result.get(i).add(index); // Store index
            bucketSums[i] += nums[index];

            if (backtrackIndices(nums, index + 1, result, bucketSums, target)) {
                return true;
            }

            result.get(i).remove(result.get(i).size() - 1);
            bucketSums[i] -= nums[index];
        }

        return false;
    }
}

/**
 * Test Cases
 */
class PartitionToKEqualSumSubsetsTest {
    public static void main(String[] args) {
        PartitionToKEqualSumSubsets solution = new PartitionToKEqualSumSubsets();

        // Test 1: Valid partition
        int[] nums1 = {4,3,2,3,5,2,1};
        int k1 = 4;
        System.out.println("Test 1: nums=" + Arrays.toString(nums1) + ", k=" + k1);
        System.out.println("Backtracking: " + solution.canPartitionKSubsets(nums1, k1));
        System.out.println("Memoization: " + solution.canPartitionKSubsetsMemo(nums1, k1));
        System.out.println("DP: " + solution.canPartitionKSubsetsDP(nums1, k1));
        System.out.println("Expected: true\n");

        // Test 2: Invalid partition
        int[] nums2 = {1,2,3,4};
        int k2 = 3;
        System.out.println("Test 2: nums=" + Arrays.toString(nums2) + ", k=" + k2);
        System.out.println("Backtracking: " + solution.canPartitionKSubsets(nums2, k2));
        System.out.println("Expected: false\n");

        // Test 3: Simple case
        int[] nums3 = {2,2,2,2,3,4,5};
        int k3 = 4;
        System.out.println("Test 3: nums=" + Arrays.toString(nums3) + ", k=" + k3);
        System.out.println("Memoization: " + solution.canPartitionKSubsetsMemo(nums3, k3));
        System.out.println("Expected: false\n");

        // Test 4: k = 1
        int[] nums4 = {1,2,3,4};
        int k4 = 1;
        System.out.println("Test 4: nums=" + Arrays.toString(nums4) + ", k=" + k4);
        System.out.println("Backtracking: " + solution.canPartitionKSubsets(nums4, k4));
        System.out.println("Expected: true\n");

        // Test 5: Equal elements
        int[] nums5 = {5,5,5,5,5,5,5,5};
        int k5 = 4;
        System.out.println("Test 5: nums=" + Arrays.toString(nums5) + ", k=" + k5);
        System.out.println("Backtracking: " + solution.canPartitionKSubsets(nums5, k5));
        System.out.println("Expected: true\n");

        // Test Follow-up: Return actual partitions
        System.out.println("=== Follow-up: Return Actual Partitions ===\n");
        PartitionToKEqualSumSubsetsWithResult solutionWithResult = new PartitionToKEqualSumSubsetsWithResult();

        int[] nums6 = {4,3,2,3,5,2,1};
        int k6 = 4;
        System.out.println("Test 6: nums=" + Arrays.toString(nums6) + ", k=" + k6);
        List<List<Integer>> partitions = solutionWithResult.partitionKSubsets(nums6, k6);
        if (!partitions.isEmpty()) {
            System.out.println("Partitions found:");
            for (int i = 0; i < partitions.size(); i++) {
                int sum = 0;
                for (int num : partitions.get(i)) {
                    sum += num;
                }
                System.out.println("  Subset " + (i + 1) + ": " + partitions.get(i) + " (sum=" + sum + ")");
            }
        } else {
            System.out.println("No valid partition found");
        }
        System.out.println("Expected: 4 subsets each with sum=5\n");

        // Test with indices
        PartitionWithIndices solutionWithIndices = new PartitionWithIndices();
        int[] nums7 = {4,3,2,3,5,2,1};
        int k7 = 4;
        System.out.println("Test 7 (with indices): nums=" + Arrays.toString(nums7) + ", k=" + k7);
        List<List<Integer>> indicesPartition = solutionWithIndices.partitionKSubsetsIndices(nums7, k7);
        if (!indicesPartition.isEmpty()) {
            System.out.println("Partitions found (by indices):");
            for (int i = 0; i < indicesPartition.size(); i++) {
                System.out.print("  Subset " + (i + 1) + " indices: " + indicesPartition.get(i) + " -> values: [");
                int sum = 0;
                for (int j = 0; j < indicesPartition.get(i).size(); j++) {
                    int idx = indicesPartition.get(i).get(j);
                    if (j > 0) System.out.print(", ");
                    System.out.print(nums7[idx]);
                    sum += nums7[idx];
                }
                System.out.println("] (sum=" + sum + ")");
            }
        }
        System.out.println();
    }
}

/**
 * Complexity Analysis:
 * ===================
 *
 * Solution 1 (Backtracking):
 * - Time: O(k^n) worst case, but much better with pruning
 *   - Each number can go into k buckets
 *   - Pruning significantly reduces search space
 * - Space: O(n) for recursion stack + used array
 * - Pros: Intuitive, good for small inputs
 * - Cons: Can be slow without optimizations
 *
 * Solution 2 (Memoization with Bitmask): ⭐ OPTIMAL
 * - Time: O(n * 2^n)
 *   - 2^n possible states (bitmasks)
 *   - Each state tries n numbers
 * - Space: O(2^n) for memoization
 * - Pros: Much faster, avoids recomputation
 * - Cons: More complex, space for large n
 *
 * Solution 3 (Bitmask DP):
 * - Time: O(n * 2^n)
 * - Space: O(2^n)
 * - Pros: Bottom-up, no recursion
 * - Cons: Less intuitive
 *
 *
 * Key Optimizations:
 * =================
 *
 * 1. Sort Descending:
 *    - Place larger numbers first
 *    - Fail faster if impossible
 *    - Reduces branching factor
 *
 * 2. Early Termination:
 *    - If total % k != 0, impossible
 *    - If max(nums) > target, impossible
 *
 * 3. Pruning Duplicates:
 *    - Skip duplicate numbers in same position
 *    - Avoids redundant branches
 *
 * 4. First Element Pruning:
 *    - If first element in bucket doesn't work, break
 *    - No point trying other elements in empty bucket
 *
 * 5. Memoization:
 *    - Cache results for each bitmask state
 *    - Avoid recomputing same subproblems
 *
 *
 * Interview Tips:
 * ==============
 *
 * 1. Problem Analysis:
 *    "This is a partition problem, classic backtracking"
 *    "Total must be divisible by k"
 *    "Each subset needs sum = total / k"
 *
 * 2. Two Perspectives:
 *    a) Bucket Perspective: Fill k buckets to target
 *    b) Number Perspective: Assign each number to a bucket
 *    Both work, bucket is usually clearer
 *
 * 3. Start Simple:
 *    - Implement basic backtracking first
 *    - Add optimizations incrementally
 *    - Mention memoization as improvement
 *
 * 4. Walk Through Example:
 *    nums = [4,3,2,3,5,2,1], k = 4, target = 5
 *    Bucket 1: [5]
 *    Bucket 2: [4,1]
 *    Bucket 3: [3,2]
 *    Bucket 4: [3,2]
 *
 * 5. Edge Cases:
 *    - k = 1 (entire array is one subset)
 *    - k = nums.length (each number is its own subset)
 *    - All numbers equal
 *    - total % k != 0
 *    - max(nums) > target
 *
 * 6. Common Mistakes:
 *    - Not checking total % k first
 *    - Forgetting to backtrack (restore state)
 *    - Not handling k = 1 case
 *    - Not sorting for optimization
 *
 * 7. Follow-up Questions:
 *    - Find actual partition (not just boolean)? → Track buckets
 *    - Minimize k? → Try k from 1 to n
 *    - Different targets for each subset? → Different problem
 *    - Continuous values? → Different approach needed
 *
 * 8. Related Problems:
 *    - LeetCode 416: Partition Equal Subset Sum (k=2)
 *    - LeetCode 473: Matchsticks to Square (k=4, specific)
 *    - LeetCode 1755: Closest Subsequence Sum
 *
 * 9. When to Use Each Solution:
 *    - Backtracking: n <= 10, simple implementation
 *    - Memoization: n <= 16, optimal performance
 *    - DP: Teaching/understanding purposes
 */
