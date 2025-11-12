package nvda;

import java.util.*;

public class TwoSum {
    
    // LeetCode 1: Two Sum
    // Hash Map approach - O(n) time, O(n) space
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
            }
            map.put(nums[i], i);
        }
        
        return new int[]{}; // No solution found
    }

    // LeetCode 167: Two Sum II - Input array is sorted
    // Two pointers approach - O(n) time, O(1) space
    public int[] twoSumSorted(int[] numbers, int target) {
        int left = 0, right = numbers.length - 1;
        
        while (left < right) {
            int sum = numbers[left] + numbers[right];
            if (sum == target) {
                return new int[]{left + 1, right + 1}; // 1-indexed
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
        
        return new int[]{}; // No solution found
    }

    // LeetCode 15: 3Sum
    // Sort + Two pointers - O(n²) time, O(1) space (excluding result)
    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        
        for (int i = 0; i < nums.length - 2; i++) {
            // Skip duplicates
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            
            int left = i + 1, right = nums.length - 1;
            int target = -nums[i];
            
            while (left < right) {
                int sum = nums[left] + nums[right];
                if (sum == target) {
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));
                    
                    // Skip duplicates
                    while (left < right && nums[left] == nums[left + 1]) left++;
                    while (left < right && nums[right] == nums[right - 1]) right--;
                    
                    left++;
                    right--;
                } else if (sum < target) {
                    left++;
                } else {
                    right--;
                }
            }
        }
        
        return result;
    }

    // LeetCode 18: 4Sum
    // Sort + Two pointers for inner loop - O(n³) time, O(1) space
    public List<List<Integer>> fourSum(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        
        for (int i = 0; i < nums.length - 3; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            
            for (int j = i + 1; j < nums.length - 2; j++) {
                if (j > i + 1 && nums[j] == nums[j - 1]) continue;
                
                int left = j + 1, right = nums.length - 1;
                long targetSum = (long)target - nums[i] - nums[j];
                
                while (left < right) {
                    long sum = (long)nums[left] + nums[right];
                    if (sum == targetSum) {
                        result.add(Arrays.asList(nums[i], nums[j], nums[left], nums[right]));
                        
                        while (left < right && nums[left] == nums[left + 1]) left++;
                        while (left < right && nums[right] == nums[right - 1]) right--;
                        
                        left++;
                        right--;
                    } else if (sum < targetSum) {
                        left++;
                    } else {
                        right--;
                    }
                }
            }
        }
        
        return result;
    }


    public static void main(String[] args) {
        TwoSum solver = new TwoSum();
        
        // Test Two Sum
        System.out.println("=== Two Sum (LeetCode 1) ===");
        int[] nums1 = {2, 7, 11, 15};
        int target1 = 9;
        int[] result1 = solver.twoSum(nums1, target1);
        System.out.println("Input: " + Arrays.toString(nums1) + ", Target: " + target1);
        System.out.println("Output: " + Arrays.toString(result1));
        System.out.println();
        
        // Test Two Sum II
        System.out.println("=== Two Sum II (LeetCode 167) ===");
        int[] nums2 = {2, 7, 11, 15};
        int target2 = 9;
        int[] result2 = solver.twoSumSorted(nums2, target2);
        System.out.println("Input: " + Arrays.toString(nums2) + ", Target: " + target2);
        System.out.println("Output: " + Arrays.toString(result2));
        System.out.println();
        
        // Test 3Sum
        System.out.println("=== 3Sum (LeetCode 15) ===");
        int[] nums3 = {-1, 0, 1, 2, -1, -4};
        List<List<Integer>> result3 = solver.threeSum(nums3);
        System.out.println("Input: " + Arrays.toString(nums3));
        System.out.println("Output: " + result3);
        System.out.println();
        
        // Test 4Sum
        System.out.println("=== 4Sum (LeetCode 18) ===");
        int[] nums4 = {1, 0, -1, 0, -2, 2};
        int target4 = 0;
        List<List<Integer>> result4 = solver.fourSum(nums4, target4);
        System.out.println("Input: " + Arrays.toString(nums4) + ", Target: " + target4);
        System.out.println("Output: " + result4);
    }
}
