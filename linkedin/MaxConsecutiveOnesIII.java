import java.util.*;

/**
 * LeetCode 1004. Max Consecutive Ones III
 *
 * Problem:
 * Given a binary array nums and an integer k, return the maximum number of
 * consecutive 1's in the array if you can flip at most k 0's.
 *
 * Example 1:
 * Input: nums = [1,1,1,0,0,0,1,1,1,1,0], k = 2
 * Output: 6
 * Explanation: Flip nums[5] and nums[10], get [1,1,1,0,0,1,1,1,1,1,1]
 * Max consecutive 1's is 6
 *
 * Example 2:
 * Input: nums = [0,0,1,1,0,0,1,1,1,0,1,1,0,0,0,1,1,1,1], k = 3
 * Output: 10
 * Explanation: Flip nums[4], nums[5] and nums[9]
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - nums[i] is either 0 or 1
 * - 0 <= k <= nums.length
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */

class MaxConsecutiveOnesIII {

    /**
     * Solution 1: Sliding Window (Optimal)
     * Time: O(n)
     * Space: O(1)
     *
     * Key Insight:
     * - Use sliding window [left, right]
     * - Expand right, count zeros in window
     * - If zeros > k, shrink from left
     * - Track maximum window size
     */
    public int longestOnes(int[] nums, int k) {
        int left = 0;
        int maxLen = 0;
        int zerosCount = 0;

        for (int right = 0; right < nums.length; right++) {
            // Expand window: add nums[right]
            if (nums[right] == 0) {
                zerosCount++;
            }

            // Shrink window if too many zeros
            while (zerosCount > k) {
                if (nums[left] == 0) {
                    zerosCount--;
                }
                left++;
            }

            // Update max length
            maxLen = Math.max(maxLen, right - left + 1);
        }

        return maxLen;
    }

    /**
     * Solution 2: Optimized Sliding Window (No Shrinking)
     * Time: O(n)
     * Space: O(1)
     *
     * Key Insight:
     * - We only care about the MAXIMUM window size
     * - Don't shrink window, just slide it forward
     * - Window size can only increase or stay same
     */
    public int longestOnesOptimized(int[] nums, int k) {
        int left = 0;
        int zerosCount = 0;

        for (int right = 0; right < nums.length; right++) {
            if (nums[right] == 0) {
                zerosCount++;
            }

            // If too many zeros, slide window (don't shrink)
            if (zerosCount > k) {
                if (nums[left] == 0) {
                    zerosCount--;
                }
                left++;
            }
        }

        // Window size = right - left
        return nums.length - left;
    }

    /**
     * Solution 3: Sliding Window with Queue (Track Zero Positions)
     * Time: O(n)
     * Space: O(k)
     *
     * Track positions of zeros we've flipped
     */
    public int longestOnesWithQueue(int[] nums, int k) {
        Queue<Integer> zeroPositions = new LinkedList<>();
        int left = 0;
        int maxLen = 0;

        for (int right = 0; right < nums.length; right++) {
            if (nums[right] == 0) {
                zeroPositions.offer(right);

                // If more than k zeros, remove leftmost zero
                if (zeroPositions.size() > k) {
                    left = zeroPositions.poll() + 1;
                }
            }

            maxLen = Math.max(maxLen, right - left + 1);
        }

        return maxLen;
    }
}

/**
 * Test Cases
 */
class MaxConsecutiveOnesIIITest {
    public static void main(String[] args) {
        MaxConsecutiveOnesIII solution = new MaxConsecutiveOnesIII();

        // Test 1
        int[] nums1 = {1,1,1,0,0,0,1,1,1,1,0};
        int k1 = 2;
        System.out.println("Test 1: nums=" + Arrays.toString(nums1) + ", k=" + k1);
        System.out.println("Solution 1: " + solution.longestOnes(nums1, k1));
        System.out.println("Solution 2: " + solution.longestOnesOptimized(nums1, k1));
        System.out.println("Solution 3: " + solution.longestOnesWithQueue(nums1, k1));
        System.out.println("Expected: 6\n");

        // Test 2
        int[] nums2 = {0,0,1,1,0,0,1,1,1,0,1,1,0,0,0,1,1,1,1};
        int k2 = 3;
        System.out.println("Test 2: nums=" + Arrays.toString(nums2) + ", k=" + k2);
        System.out.println("Solution 1: " + solution.longestOnes(nums2, k2));
        System.out.println("Expected: 10\n");

        // Test 3: k = 0 (no flips allowed)
        int[] nums3 = {0,0,1,1,1,0,0};
        int k3 = 0;
        System.out.println("Test 3: nums=" + Arrays.toString(nums3) + ", k=" + k3);
        System.out.println("Solution 1: " + solution.longestOnes(nums3, k3));
        System.out.println("Expected: 3\n");

        // Test 4: All zeros
        int[] nums4 = {0,0,0,0};
        int k4 = 2;
        System.out.println("Test 4: nums=" + Arrays.toString(nums4) + ", k=" + k4);
        System.out.println("Solution 1: " + solution.longestOnes(nums4, k4));
        System.out.println("Expected: 2\n");

        // Test 5: All ones
        int[] nums5 = {1,1,1,1};
        int k5 = 0;
        System.out.println("Test 5: nums=" + Arrays.toString(nums5) + ", k=" + k5);
        System.out.println("Solution 1: " + solution.longestOnes(nums5, k5));
        System.out.println("Expected: 4\n");

        // Test 6: k >= array length
        int[] nums6 = {0,0,1,0,1};
        int k6 = 10;
        System.out.println("Test 6: nums=" + Arrays.toString(nums6) + ", k=" + k6);
        System.out.println("Solution 1: " + solution.longestOnes(nums6, k6));
        System.out.println("Expected: 5\n");
    }
}

/**
 * Complexity Analysis:
 * ===================
 *
 * Solution 1 (Standard Sliding Window): ⭐ RECOMMENDED
 * - Time: O(n) - each element visited at most twice (by left and right)
 * - Space: O(1)
 * - Pros: Clear logic, easy to understand
 * - Cons: None
 *
 * Solution 2 (Optimized Sliding Window): ⭐ MOST EFFICIENT
 * - Time: O(n) - each element visited exactly once
 * - Space: O(1)
 * - Pros: Slight optimization, window never shrinks
 * - Cons: Less intuitive
 *
 * Solution 3 (Queue):
 * - Time: O(n)
 * - Space: O(k) - queue stores at most k+1 positions
 * - Pros: Explicitly tracks flipped positions
 * - Cons: Extra space
 *
 *
 * Pattern Recognition:
 * ===================
 *
 * This is a classic "Sliding Window" problem with constraints:
 * - Fixed constraint: at most k flips (zeros in window)
 * - Goal: maximize window size
 *
 * Similar Problems:
 * - LeetCode 424: Longest Repeating Character Replacement
 * - LeetCode 487: Max Consecutive Ones II (k=1)
 * - LeetCode 1208: Get Equal Substrings Within Budget
 *
 *
 * Interview Tips:
 * ==============
 *
 * 1. Problem Transformation:
 *    "This is asking: find longest subarray with at most k zeros"
 *
 * 2. Sliding Window Template:
 *    left = 0
 *    for right in range(n):
 *        # Expand window
 *        add nums[right]
 *
 *        # Shrink if invalid
 *        while (not valid):
 *            remove nums[left]
 *            left++
 *
 *        # Update result
 *        maxLen = max(maxLen, right - left + 1)
 *
 * 3. Key Insights to Mention:
 *    - "Flipping k zeros = allowing k zeros in window"
 *    - "Two pointers: expand right, shrink left when invalid"
 *    - "Time is O(n) because each element touched at most twice"
 *
 * 4. Edge Cases:
 *    - k = 0 (no flips allowed)
 *    - All zeros
 *    - All ones
 *    - k >= array length (can flip everything)
 *    - Single element
 *
 * 5. Follow-up Questions:
 *    - What if we want to minimize flips instead? → Same approach
 *    - What if k changes dynamically? → Rebuild solution
 *    - What positions did we flip? → Use queue-based approach
 *    - 2D version? → Harder, need different approach
 *
 * 6. Common Mistakes:
 *    - Forgetting to update maxLen inside loop
 *    - Not handling k=0 case
 *    - Off-by-one errors in window size calculation
 *
 * 7. Optimization Note:
 *    Solution 2 is clever: window size never decreases
 *    - If current window is invalid, just slide it forward
 *    - We only care about the maximum size reached
 *    - Saves the inner while loop iteration
 */
