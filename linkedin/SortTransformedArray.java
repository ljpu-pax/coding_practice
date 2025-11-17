// LeetCode 360: Sort Transformed Array
// https://leetcode.com/problems/sort-transformed-array/
// Difficulty: Medium
// Premium Problem

// Given a sorted integer array nums and three integers a, b and c,
// apply a quadratic function of the form f(x) = ax^2 + bx + c to each element nums[i]
// in the array, and return the array in a sorted order.

// Example 1:
// Input: nums = [-4,-2,2,4], a = 1, b = 3, c = 5
// Output: [3,9,15,33]
// Explanation:
// f(-4) = 1*16 - 12 + 5 = 9
// f(-2) = 1*4 - 6 + 5 = 3
// f(2) = 1*4 + 6 + 5 = 15
// f(4) = 1*16 + 12 + 5 = 33

// Example 2:
// Input: nums = [-4,-2,2,4], a = -1, b = 3, c = 5
// Output: [-23,-5,1,7]

// Follow up: Could you solve it in O(n) time?

import java.util.*;

class SortTransformedArray {
    // Two-pointer approach - O(n) time, O(n) space
    // Key insight: For quadratic functions, the extreme values are at the ends
    // - If a >= 0, parabola opens upward -> max values at ends
    // - If a < 0, parabola opens downward -> min values at ends
    public int[] sortTransformedArray(int[] nums, int a, int b, int c) {
        int n = nums.length;
        int[] result = new int[n];

        int left = 0;
        int right = n - 1;

        // Fill result array from end to start if a >= 0 (larger values at ends)
        // Fill from start to end if a < 0 (smaller values at ends)
        int index = a >= 0 ? n - 1 : 0;

        while (left <= right) {
            int leftVal = transform(nums[left], a, b, c);
            int rightVal = transform(nums[right], a, b, c);

            if (a >= 0) {
                // Parabola opens upward - pick larger value
                if (leftVal >= rightVal) {
                    result[index--] = leftVal;
                    left++;
                } else {
                    result[index--] = rightVal;
                    right--;
                }
            } else {
                // Parabola opens downward - pick smaller value
                if (leftVal <= rightVal) {
                    result[index++] = leftVal;
                    left++;
                } else {
                    result[index++] = rightVal;
                    right--;
                }
            }
        }

        return result;
    }

    // Helper function to compute f(x) = ax^2 + bx + c
    private int transform(int x, int a, int b, int c) {
        return a * x * x + b * x + c;
    }

    // Brute force approach - O(n log n) time
    public int[] sortTransformedArrayBruteForce(int[] nums, int a, int b, int c) {
        int n = nums.length;
        int[] result = new int[n];

        for (int i = 0; i < n; i++) {
            result[i] = transform(nums[i], a, b, c);
        }

        Arrays.sort(result);
        return result;
    }

    public static void main(String[] args) {
        SortTransformedArray solution = new SortTransformedArray();

        // Test case 1: a > 0 (parabola opens upward)
        int[] nums1 = {-4, -2, 2, 4};
        int[] result1 = solution.sortTransformedArray(nums1, 1, 3, 5);
        System.out.println("Test 1: " + Arrays.toString(result1)); // [3, 9, 15, 33]

        // Test case 2: a < 0 (parabola opens downward)
        int[] nums2 = {-4, -2, 2, 4};
        int[] result2 = solution.sortTransformedArray(nums2, -1, 3, 5);
        System.out.println("Test 2: " + Arrays.toString(result2)); // [-23, -5, 1, 7]

        // Test case 3: a = 0 (linear function)
        int[] nums3 = {-4, -2, 2, 4};
        int[] result3 = solution.sortTransformedArray(nums3, 0, 1, 5);
        System.out.println("Test 3: " + Arrays.toString(result3)); // [1, 3, 7, 9]

        // Test case 4: Single element
        int[] nums4 = {1};
        int[] result4 = solution.sortTransformedArray(nums4, 1, 2, 3);
        System.out.println("Test 4: " + Arrays.toString(result4)); // [6]

        // Verify with brute force
        System.out.println("\nBrute Force Verification:");
        int[] result5 = solution.sortTransformedArrayBruteForce(nums1, 1, 3, 5);
        System.out.println("Brute Force: " + Arrays.toString(result5)); // [3, 9, 15, 33]
    }
}
