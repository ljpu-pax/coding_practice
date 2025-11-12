import java.util.*;

/**
 * LeetCode 42: Trapping Rain Water
 *
 * Given n non-negative integers representing an elevation map where the width of each bar is 1,
 * compute how much water it can trap after raining.
 *
 * Example 1:
 * Input: height = [0,1,0,2,1,0,1,3,2,1,2,1]
 * Output: 6
 * Explanation: The above elevation map (black section) is represented by array [0,1,0,2,1,0,1,3,2,1,2,1].
 * In this case, 6 units of rain water (blue section) are being trapped.
 *
 * Example 2:
 * Input: height = [4,2,0,3,2,5]
 * Output: 9
 *
 * Constraints:
 * - n == height.length
 * - 1 <= n <= 2 * 10^4
 * - 0 <= height[i] <= 10^5
 */
class TrappingRainWater {
    /**
     * Approach 1: Dynamic Programming (Two Pass)
     *
     * Key Insight: Water trapped at index i = min(leftMax[i], rightMax[i]) - height[i]
     *
     * Algorithm:
     * 1. Precompute leftMax[i] = max height from left to i
     * 2. Precompute rightMax[i] = max height from right to i
     * 3. For each position, water = min(leftMax, rightMax) - height
     *
     * Time: O(n) - three passes
     * Space: O(n) - two arrays
     */
    public int trap(int[] height) {
        if (height == null || height.length == 0) {
            return 0;
        }

        int n = height.length;
        int[] leftMax = new int[n];
        int[] rightMax = new int[n];

        // Build leftMax array
        leftMax[0] = height[0];
        for (int i = 1; i < n; i++) {
            leftMax[i] = Math.max(leftMax[i - 1], height[i]);
        }

        // Build rightMax array
        rightMax[n - 1] = height[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            rightMax[i] = Math.max(rightMax[i + 1], height[i]);
        }

        // Calculate trapped water
        int water = 0;
        for (int i = 0; i < n; i++) {
            water += Math.min(leftMax[i], rightMax[i]) - height[i];
        }

        return water;
    }

    /**
     * Approach 2: Two Pointers (Optimal)
     *
     * Key Insight: We don't need to compute all leftMax and rightMax values upfront.
     * We can use two pointers and track maxLeft and maxRight as we go.
     *
     * Algorithm:
     * - Use two pointers: left and right
     * - Track maxLeft and maxRight
     * - Move pointer with smaller max height
     * - Water at current position depends only on the smaller max
     *
     * Time: O(n) - single pass
     * Space: O(1) - constant space
     */
    public int trapTwoPointers(int[] height) {
        if (height == null || height.length == 0) {
            return 0;
        }

        int left = 0, right = height.length - 1;
        int maxLeft = 0, maxRight = 0;
        int water = 0;

        while (left < right) {
            if (height[left] < height[right]) {
                // Process left side
                if (height[left] >= maxLeft) {
                    maxLeft = height[left];
                } else {
                    water += maxLeft - height[left];
                }
                left++;
            } else {
                // Process right side
                if (height[right] >= maxRight) {
                    maxRight = height[right];
                } else {
                    water += maxRight - height[right];
                }
                right--;
            }
        }

        return water;
    }

    /**
     * Approach 3: Stack (Monotonic Decreasing)
     *
     * Key Insight: Calculate water horizontally (layer by layer) instead of vertically.
     * Use stack to find previous higher bars.
     *
     * Algorithm:
     * - Maintain a stack of indices in decreasing order of heights
     * - When we find a taller bar, pop from stack and calculate trapped water
     * - Water forms between current bar and the bar at bottom of stack
     *
     * Time: O(n) - each element pushed/popped once
     * Space: O(n) - stack space
     */
    public int trapStack(int[] height) {
        if (height == null || height.length == 0) {
            return 0;
        }

        Stack<Integer> stack = new Stack<>();
        int water = 0;

        for (int i = 0; i < height.length; i++) {
            // While current height is greater than stack top
            while (!stack.isEmpty() && height[i] > height[stack.peek()]) {
                int bottom = stack.pop();

                if (stack.isEmpty()) {
                    break;
                }

                int left = stack.peek();
                int boundedHeight = Math.min(height[i], height[left]) - height[bottom];
                int distance = i - left - 1;
                water += boundedHeight * distance;
            }

            stack.push(i);
        }

        return water;
    }

    /**
     * Approach 4: Two Pointers (Alternative Implementation)
     *
     * Slightly different style but same logic as approach 2.
     *
     * Time: O(n)
     * Space: O(1)
     */
    public int trapTwoPointersAlt(int[] height) {
        if (height == null || height.length == 0) {
            return 0;
        }

        int left = 0, right = height.length - 1;
        int leftMax = height[left], rightMax = height[right];
        int water = 0;

        while (left < right) {
            leftMax = Math.max(leftMax, height[left]);
            rightMax = Math.max(rightMax, height[right]);

            if (leftMax < rightMax) {
                water += leftMax - height[left];
                left++;
            } else {
                water += rightMax - height[right];
                right--;
            }
        }

        return water;
    }
}

/**
 * Test cases
 */
class TrappingRainWaterTest {
    public static void main(String[] args) {
        TrappingRainWater solution = new TrappingRainWater();

        System.out.println("=== Testing LeetCode 42: Trapping Rain Water ===\n");

        // Test 1: [0,1,0,2,1,0,1,3,2,1,2,1]
        int[] height1 = {0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1};
        int expected1 = 6;
        testCase(solution, height1, expected1, "Test 1");

        // Test 2: [4,2,0,3,2,5]
        int[] height2 = {4, 2, 0, 3, 2, 5};
        int expected2 = 9;
        testCase(solution, height2, expected2, "Test 2");

        // Test 3: Empty array (edge case)
        int[] height3 = {};
        int expected3 = 0;
        testCase(solution, height3, expected3, "Test 3");

        // Test 4: Single element
        int[] height4 = {5};
        int expected4 = 0;
        testCase(solution, height4, expected4, "Test 4");

        // Test 5: Two elements
        int[] height5 = {3, 2};
        int expected5 = 0;
        testCase(solution, height5, expected5, "Test 5");

        // Test 6: Ascending order
        int[] height6 = {1, 2, 3, 4, 5};
        int expected6 = 0;
        testCase(solution, height6, expected6, "Test 6");

        // Test 7: Descending order
        int[] height7 = {5, 4, 3, 2, 1};
        int expected7 = 0;
        testCase(solution, height7, expected7, "Test 7");

        // Test 8: Valley shape
        int[] height8 = {3, 0, 2, 0, 4};
        int expected8 = 7;
        testCase(solution, height8, expected8, "Test 8");

        // Test 9: Multiple peaks
        int[] height9 = {3, 0, 0, 2, 0, 4};
        int expected9 = 10;
        testCase(solution, height9, expected9, "Test 9");

        // Test 10: All same height
        int[] height10 = {2, 2, 2, 2, 2};
        int expected10 = 0;
        testCase(solution, height10, expected10, "Test 10");
    }

    private static void testCase(TrappingRainWater solution, int[] height, int expected, String testName) {
        System.out.println(testName + ": height = " + Arrays.toString(height));

        int resultDP = solution.trap(height);
        int resultTwoPointers = solution.trapTwoPointers(height);
        int resultStack = solution.trapStack(height);
        int resultTwoPointersAlt = solution.trapTwoPointersAlt(height);

        System.out.println("  DP: " + resultDP + " (Expected: " + expected + ") - " +
                          (resultDP == expected ? "PASS" : "FAIL"));
        System.out.println("  Two Pointers: " + resultTwoPointers + " (Expected: " + expected + ") - " +
                          (resultTwoPointers == expected ? "PASS" : "FAIL"));
        System.out.println("  Stack: " + resultStack + " (Expected: " + expected + ") - " +
                          (resultStack == expected ? "PASS" : "FAIL"));
        System.out.println("  Two Pointers Alt: " + resultTwoPointersAlt + " (Expected: " + expected + ") - " +
                          (resultTwoPointersAlt == expected ? "PASS" : "FAIL"));
        System.out.println();
    }
}
