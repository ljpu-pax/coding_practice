package nvda;

import java.util.*;

public class MaximumSubarray {
    
    // LeetCode 53: Maximum Subarray (Kadane's Algorithm)
    // O(n) time, O(1) space
    public int maxSubArray(int[] nums) {
        int maxSoFar = nums[0];
        int maxEndingHere = nums[0];
        
        for (int i = 1; i < nums.length; i++) {
            maxEndingHere = Math.max(nums[i], maxEndingHere + nums[i]);
            maxSoFar = Math.max(maxSoFar, maxEndingHere);
        }
        
        return maxSoFar;
    }

    // Extended version that also returns the subarray indices
    public int[] maxSubArrayWithIndices(int[] nums) {
        int maxSoFar = nums[0];
        int maxEndingHere = nums[0];
        int start = 0, end = 0, tempStart = 0;
        
        for (int i = 1; i < nums.length; i++) {
            if (maxEndingHere < 0) {
                maxEndingHere = nums[i];
                tempStart = i;
            } else {
                maxEndingHere += nums[i];
            }
            
            if (maxEndingHere > maxSoFar) {
                maxSoFar = maxEndingHere;
                start = tempStart;
                end = i;
            }
        }
        
        return new int[]{maxSoFar, start, end};
    }

    // LeetCode 152: Maximum Product Subarray
    // Handle negative numbers and zeros - O(n) time, O(1) space
    public int maxProduct(int[] nums) {
        int maxSoFar = nums[0];
        int maxEndingHere = nums[0];
        int minEndingHere = nums[0];
        
        for (int i = 1; i < nums.length; i++) {
            int temp = maxEndingHere;
            maxEndingHere = Math.max(nums[i], Math.max(maxEndingHere * nums[i], minEndingHere * nums[i]));
            minEndingHere = Math.min(nums[i], Math.min(temp * nums[i], minEndingHere * nums[i]));
            maxSoFar = Math.max(maxSoFar, maxEndingHere);
        }
        
        return maxSoFar;
    }

    // LeetCode 918: Maximum Sum Circular Subarray
    // Handle circular array - O(n) time, O(1) space
    public int maxSubarraySumCircular(int[] nums) {
        int total = 0;
        int maxSum = nums[0];
        int minSum = nums[0];
        int currentMax = 0;
        int currentMin = 0;
        
        for (int num : nums) {
            total += num;
            currentMax = Math.max(num, currentMax + num);
            currentMin = Math.min(num, currentMin + num);
            maxSum = Math.max(maxSum, currentMax);
            minSum = Math.min(minSum, currentMin);
        }
        
        return maxSum > 0 ? Math.max(maxSum, total - minSum) : maxSum;
    }

    // LeetCode 209: Minimum Size Subarray Sum
    // Sliding window - O(n) time, O(1) space
    public int minSubArrayLen(int target, int[] nums) {
        int left = 0;
        int sum = 0;
        int minLen = Integer.MAX_VALUE;
        
        for (int right = 0; right < nums.length; right++) {
            sum += nums[right];
            
            while (sum >= target) {
                minLen = Math.min(minLen, right - left + 1);
                sum -= nums[left];
                left++;
            }
        }
        
        return minLen == Integer.MAX_VALUE ? 0 : minLen;
    }

    // Helper method to print subarray
    public static void printSubarray(int[] nums, int start, int end) {
        System.out.print("[");
        for (int i = start; i <= end; i++) {
            System.out.print(nums[i]);
            if (i < end) System.out.print(", ");
        }
        System.out.print("]");
    }

    public static void main(String[] args) {
        MaximumSubarray solver = new MaximumSubarray();
        
        // Test Maximum Subarray
        System.out.println("=== Maximum Subarray (LeetCode 53) ===");
        int[] nums1 = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        int result1 = solver.maxSubArray(nums1);
        System.out.println("Input: " + Arrays.toString(nums1));
        System.out.println("Maximum sum: " + result1);
        
        int[] indices = solver.maxSubArrayWithIndices(nums1);
        System.out.print("Subarray: ");
        printSubarray(nums1, indices[1], indices[2]);
        System.out.println();
        System.out.println();
        
        // Test Maximum Product Subarray
        System.out.println("=== Maximum Product Subarray (LeetCode 152) ===");
        int[] nums2 = {2, 3, -2, 4};
        int result2 = solver.maxProduct(nums2);
        System.out.println("Input: " + Arrays.toString(nums2));
        System.out.println("Maximum product: " + result2);
        System.out.println();
        
        // Test Maximum Sum Circular Subarray
        System.out.println("=== Maximum Sum Circular Subarray (LeetCode 918) ===");
        int[] nums3 = {5, -3, 5};
        int result3 = solver.maxSubarraySumCircular(nums3);
        System.out.println("Input: " + Arrays.toString(nums3));
        System.out.println("Maximum circular sum: " + result3);
        System.out.println();
        
        // Test Minimum Size Subarray Sum
        System.out.println("=== Minimum Size Subarray Sum (LeetCode 209) ===");
        int[] nums4 = {2, 3, 1, 2, 4, 3};
        int target4 = 7;
        int result4 = solver.minSubArrayLen(target4, nums4);
        System.out.println("Input: " + Arrays.toString(nums4) + ", Target: " + target4);
        System.out.println("Minimum length: " + result4);
    }
}
