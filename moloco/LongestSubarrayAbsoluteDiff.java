import java.util.*;

/**
 * LeetCode 1438: Longest Continuous Subarray With Absolute Diff Less Than or Equal to Limit
 *
 * Given an array of integers nums and an integer limit, return the size of the longest non-empty subarray
 * such that the absolute difference between any two elements of this subarray is less than or equal to limit.
 *
 * Example 1:
 * Input: nums = [8,2,4,7], limit = 4
 * Output: 2
 * Explanation: All subarrays are:
 * [8] with maximum absolute diff |8-8| = 0 <= 4.
 * [8,2] with maximum absolute diff |8-2| = 6 > 4.
 * [8,2,4] with maximum absolute diff |8-2| = 6 > 4.
 * [8,2,4,7] with maximum absolute diff |8-2| = 6 > 4.
 * [2] with maximum absolute diff |2-2| = 0 <= 4.
 * [2,4] with maximum absolute diff |2-4| = 2 <= 4.
 * [2,4,7] with maximum absolute diff |2-7| = 5 > 4.
 * [4] with maximum absolute diff |4-4| = 0 <= 4.
 * [4,7] with maximum absolute diff |4-7| = 3 <= 4.
 * [7] with maximum absolute diff |7-7| = 0 <= 4.
 * Therefore, the size of the longest subarray is 2.
 *
 * Example 2:
 * Input: nums = [10,1,2,4,7,2], limit = 5
 * Output: 4
 * Explanation: The subarray [2,4,7,2] is the longest since the maximum absolute diff is |2-7| = 5 <= 5.
 *
 * Example 3:
 * Input: nums = [4,2,2,2,4,4,2,2], limit = 0
 * Output: 3
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - 1 <= nums[i] <= 10^9
 * - 0 <= limit <= 10^9
 */
class LongestSubarrayAbsoluteDiff {
    /**
     * Approach 1: Sliding Window with Two Deques (Optimal)
     *
     * Key Insight: For a valid subarray, max(subarray) - min(subarray) <= limit
     *
     * We maintain two monotonic deques:
     * - maxDeque: decreasing order (front has max)
     * - minDeque: increasing order (front has min)
     *
     * Time: O(n) - each element added/removed from deques at most once
     * Space: O(n) - for the deques
     */
    public int longestSubarray(int[] nums, int limit) {
        Deque<Integer> maxDeque = new ArrayDeque<>(); // stores indices, decreasing values
        Deque<Integer> minDeque = new ArrayDeque<>(); // stores indices, increasing values

        int left = 0;
        int maxLen = 0;

        for (int right = 0; right < nums.length; right++) {
            // Maintain maxDeque in decreasing order
            while (!maxDeque.isEmpty() && nums[maxDeque.peekLast()] <= nums[right]) {
                maxDeque.pollLast();
            }
            maxDeque.offerLast(right);

            // Maintain minDeque in increasing order
            while (!minDeque.isEmpty() && nums[minDeque.peekLast()] >= nums[right]) {
                minDeque.pollLast();
            }
            minDeque.offerLast(right);

            // Shrink window if difference exceeds limit
            while (nums[maxDeque.peekFirst()] - nums[minDeque.peekFirst()] > limit) {
                left++;
                // Remove indices that are out of window
                if (maxDeque.peekFirst() < left) {
                    maxDeque.pollFirst();
                }
                if (minDeque.peekFirst() < left) {
                    minDeque.pollFirst();
                }
            }

            maxLen = Math.max(maxLen, right - left + 1);
        }

        return maxLen;
    }

    /**
     * Approach 2: Sliding Window with TreeMap
     *
     * TreeMap maintains sorted order and allows us to get min and max efficiently.
     *
     * Time: O(n log n) - TreeMap operations are O(log n)
     * Space: O(n) - for the TreeMap
     */
    public int longestSubarrayTreeMap(int[] nums, int limit) {
        TreeMap<Integer, Integer> map = new TreeMap<>(); // value -> count
        int left = 0;
        int maxLen = 0;

        for (int right = 0; right < nums.length; right++) {
            // Add current element
            map.put(nums[right], map.getOrDefault(nums[right], 0) + 1);

            // Shrink window if difference exceeds limit
            while (map.lastKey() - map.firstKey() > limit) {
                map.put(nums[left], map.get(nums[left]) - 1);
                if (map.get(nums[left]) == 0) {
                    map.remove(nums[left]);
                }
                left++;
            }

            maxLen = Math.max(maxLen, right - left + 1);
        }

        return maxLen;
    }

    /**
     * Approach 3: Sliding Window with Multiset (using TreeMap)
     * Similar to Approach 2 but more explicit about the multiset concept
     */
    public int longestSubarrayMultiset(int[] nums, int limit) {
        TreeMap<Integer, Integer> window = new TreeMap<>();
        int left = 0;
        int maxLen = 0;

        for (int right = 0; right < nums.length; right++) {
            // Add element to window
            window.merge(nums[right], 1, Integer::sum);

            // Check if window is valid
            while (!window.isEmpty() && window.lastKey() - window.firstKey() > limit) {
                // Remove leftmost element
                int count = window.get(nums[left]);
                if (count == 1) {
                    window.remove(nums[left]);
                } else {
                    window.put(nums[left], count - 1);
                }
                left++;
            }

            maxLen = Math.max(maxLen, right - left + 1);
        }

        return maxLen;
    }
}

/**
 * Test cases
 */
class LongestSubarrayAbsoluteDiffTest {
    public static void main(String[] args) {
        LongestSubarrayAbsoluteDiff solution = new LongestSubarrayAbsoluteDiff();

        System.out.println("=== Testing LeetCode 1438: Longest Subarray with Absolute Diff <= Limit ===\n");

        // Test 1: [8,2,4,7], limit = 4
        int[] nums1 = {8, 2, 4, 7};
        int limit1 = 4;
        int result1Deque = solution.longestSubarray(nums1, limit1);
        int result1TreeMap = solution.longestSubarrayTreeMap(nums1, limit1);
        int result1Multiset = solution.longestSubarrayMultiset(nums1, limit1);
        System.out.println("Test 1: nums = [8,2,4,7], limit = 4");
        System.out.println("  Deque: " + result1Deque + " (Expected: 2) - " + (result1Deque == 2 ? "PASS" : "FAIL"));
        System.out.println("  TreeMap: " + result1TreeMap + " (Expected: 2) - " + (result1TreeMap == 2 ? "PASS" : "FAIL"));
        System.out.println("  Multiset: " + result1Multiset + " (Expected: 2) - " + (result1Multiset == 2 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 2: [10,1,2,4,7,2], limit = 5
        int[] nums2 = {10, 1, 2, 4, 7, 2};
        int limit2 = 5;
        int result2Deque = solution.longestSubarray(nums2, limit2);
        int result2TreeMap = solution.longestSubarrayTreeMap(nums2, limit2);
        int result2Multiset = solution.longestSubarrayMultiset(nums2, limit2);
        System.out.println("Test 2: nums = [10,1,2,4,7,2], limit = 5");
        System.out.println("  Deque: " + result2Deque + " (Expected: 4) - " + (result2Deque == 4 ? "PASS" : "FAIL"));
        System.out.println("  TreeMap: " + result2TreeMap + " (Expected: 4) - " + (result2TreeMap == 4 ? "PASS" : "FAIL"));
        System.out.println("  Multiset: " + result2Multiset + " (Expected: 4) - " + (result2Multiset == 4 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 3: [4,2,2,2,4,4,2,2], limit = 0
        int[] nums3 = {4, 2, 2, 2, 4, 4, 2, 2};
        int limit3 = 0;
        int result3Deque = solution.longestSubarray(nums3, limit3);
        int result3TreeMap = solution.longestSubarrayTreeMap(nums3, limit3);
        int result3Multiset = solution.longestSubarrayMultiset(nums3, limit3);
        System.out.println("Test 3: nums = [4,2,2,2,4,4,2,2], limit = 0");
        System.out.println("  Deque: " + result3Deque + " (Expected: 3) - " + (result3Deque == 3 ? "PASS" : "FAIL"));
        System.out.println("  TreeMap: " + result3TreeMap + " (Expected: 3) - " + (result3TreeMap == 3 ? "PASS" : "FAIL"));
        System.out.println("  Multiset: " + result3Multiset + " (Expected: 3) - " + (result3Multiset == 3 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 4: Single element
        int[] nums4 = {5};
        int limit4 = 10;
        int result4Deque = solution.longestSubarray(nums4, limit4);
        int result4TreeMap = solution.longestSubarrayTreeMap(nums4, limit4);
        int result4Multiset = solution.longestSubarrayMultiset(nums4, limit4);
        System.out.println("Test 4: nums = [5], limit = 10");
        System.out.println("  Deque: " + result4Deque + " (Expected: 1) - " + (result4Deque == 1 ? "PASS" : "FAIL"));
        System.out.println("  TreeMap: " + result4TreeMap + " (Expected: 1) - " + (result4TreeMap == 1 ? "PASS" : "FAIL"));
        System.out.println("  Multiset: " + result4Multiset + " (Expected: 1) - " + (result4Multiset == 1 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 5: All same elements
        int[] nums5 = {3, 3, 3, 3, 3};
        int limit5 = 0;
        int result5Deque = solution.longestSubarray(nums5, limit5);
        int result5TreeMap = solution.longestSubarrayTreeMap(nums5, limit5);
        int result5Multiset = solution.longestSubarrayMultiset(nums5, limit5);
        System.out.println("Test 5: nums = [3,3,3,3,3], limit = 0");
        System.out.println("  Deque: " + result5Deque + " (Expected: 5) - " + (result5Deque == 5 ? "PASS" : "FAIL"));
        System.out.println("  TreeMap: " + result5TreeMap + " (Expected: 5) - " + (result5TreeMap == 5 ? "PASS" : "FAIL"));
        System.out.println("  Multiset: " + result5Multiset + " (Expected: 5) - " + (result5Multiset == 5 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 6: Large limit (entire array valid)
        int[] nums6 = {1, 5, 6, 7, 8, 10};
        int limit6 = 100;
        int result6Deque = solution.longestSubarray(nums6, limit6);
        int result6TreeMap = solution.longestSubarrayTreeMap(nums6, limit6);
        int result6Multiset = solution.longestSubarrayMultiset(nums6, limit6);
        System.out.println("Test 6: nums = [1,5,6,7,8,10], limit = 100");
        System.out.println("  Deque: " + result6Deque + " (Expected: 6) - " + (result6Deque == 6 ? "PASS" : "FAIL"));
        System.out.println("  TreeMap: " + result6TreeMap + " (Expected: 6) - " + (result6TreeMap == 6 ? "PASS" : "FAIL"));
        System.out.println("  Multiset: " + result6Multiset + " (Expected: 6) - " + (result6Multiset == 6 ? "PASS" : "FAIL"));
    }
}
