// LeetCode 47: Permutations II
// https://leetcode.com/problems/permutations-ii/
// Difficulty: Medium

// Given a collection of numbers, nums, that might contain duplicates,
// return all possible unique permutations in any order.

// Example 1:
// Input: nums = [1,1,2]
// Output: [[1,1,2],[1,2,1],[2,1,1]]

// Example 2:
// Input: nums = [1,2,3]
// Output: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]

import java.util.*;

class PermutationsII {
    // Backtracking approach with sorting to handle duplicates
    // Time: O(n! * n), Space: O(n)
    public List<List<Integer>> permuteUnique(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();

        // Sort array to group duplicates together
        Arrays.sort(nums);

        boolean[] used = new boolean[nums.length];
        backtrack(nums, new ArrayList<>(), used, result);

        return result;
    }

    private void backtrack(int[] nums, List<Integer> current, boolean[] used, List<List<Integer>> result) {
        // Base case: if current permutation is complete
        if (current.size() == nums.length) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            // Skip if already used
            if (used[i]) {
                continue;
            }

            // Skip duplicates: if current element equals previous and previous is not used
            // This ensures we only use duplicates in order
            if (i > 0 && nums[i] == nums[i - 1] && !used[i - 1]) {
                continue;
            }

            // Choose
            used[i] = true;
            current.add(nums[i]);

            // Explore
            backtrack(nums, current, used, result);

            // Unchoose (backtrack)
            used[i] = false;
            current.remove(current.size() - 1);
        }
    }

    // Alternative approach using swap-based backtracking
    // Time: O(n! * n), Space: O(n)
    public List<List<Integer>> permuteUniqueSwap(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrackSwap(nums, 0, result);
        return result;
    }

    private void backtrackSwap(int[] nums, int start, List<List<Integer>> result) {
        if (start == nums.length) {
            List<Integer> perm = new ArrayList<>();
            for (int num : nums) {
                perm.add(num);
            }
            result.add(perm);
            return;
        }

        Set<Integer> seen = new HashSet<>();
        for (int i = start; i < nums.length; i++) {
            // Skip if we've already used this number at this position
            if (seen.contains(nums[i])) {
                continue;
            }
            seen.add(nums[i]);

            // Swap
            swap(nums, start, i);

            // Recurse
            backtrackSwap(nums, start + 1, result);

            // Swap back
            swap(nums, start, i);
        }
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    public static void main(String[] args) {
        PermutationsII solution = new PermutationsII();

        // Test case 1: with duplicates
        int[] nums1 = {1, 1, 2};
        List<List<Integer>> result1 = solution.permuteUnique(nums1);
        System.out.println("Test 1 - Sorted approach:");
        System.out.println(result1);
        // [[1,1,2], [1,2,1], [2,1,1]]

        // Test case 2: no duplicates
        int[] nums2 = {1, 2, 3};
        List<List<Integer>> result2 = solution.permuteUnique(nums2);
        System.out.println("\nTest 2:");
        System.out.println(result2);
        // [[1,2,3], [1,3,2], [2,1,3], [2,3,1], [3,1,2], [3,2,1]]

        // Test case 3: all duplicates
        int[] nums3 = {2, 2, 2};
        List<List<Integer>> result3 = solution.permuteUnique(nums3);
        System.out.println("\nTest 3:");
        System.out.println(result3);
        // [[2,2,2]]

        // Test swap-based approach
        System.out.println("\nTest 4 - Swap approach:");
        int[] nums4 = {1, 1, 2};
        List<List<Integer>> result4 = solution.permuteUniqueSwap(nums4);
        System.out.println(result4);

        // Test single element
        System.out.println("\nTest 5 - Single element:");
        int[] nums5 = {1};
        List<List<Integer>> result5 = solution.permuteUnique(nums5);
        System.out.println(result5);
        // [[1]]
    }
}

/*
 * Key Insights:
 *
 * 1. Sorting the array helps group duplicates together
 * 2. The key condition to avoid duplicates:
 *    if (i > 0 && nums[i] == nums[i-1] && !used[i-1]) continue;
 *    This ensures we only use duplicates in order
 * 3. The "used" array tracks which elements are in the current permutation
 *
 * Alternative approach (swap-based):
 * - Uses a HashSet to track which values have been used at each position
 * - Doesn't require sorting, but modifies the array during backtracking
 *
 * Time Complexity: O(n! * n)
 * - n! permutations, each taking O(n) to create
 * Space Complexity: O(n) for recursion stack and temporary storage
 *
 * Why the duplicate check works:
 * - After sorting [1,1,2], when we pick the first 1, we explore all permutations
 * - When we come back and try to pick the second 1 at the same level,
 *   the condition (nums[i] == nums[i-1] && !used[i-1]) is true
 * - This means we're trying to pick a duplicate before using the previous one,
 *   which would create duplicate permutations, so we skip it
 */
