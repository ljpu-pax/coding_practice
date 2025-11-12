import java.util.*;

/**
 * LeetCode 34: Find First and Last Position of Element in Sorted Array
 *
 * Given an array of integers nums sorted in non-decreasing order, find the starting and ending
 * position of a given target value.
 *
 * If target is not found in the array, return [-1, -1].
 *
 * You must write an algorithm with O(log n) runtime complexity.
 *
 * Example 1:
 * Input: nums = [5,7,7,8,8,10], target = 8
 * Output: [3,4]
 *
 * Example 2:
 * Input: nums = [5,7,7,8,8,10], target = 6
 * Output: [-1,-1]
 *
 * Example 3:
 * Input: nums = [], target = 0
 * Output: [-1,-1]
 *
 * Constraints:
 * - 0 <= nums.length <= 10^5
 * - -10^9 <= nums[i] <= 10^9
 * - nums is a non-decreasing array.
 * - -10^9 <= target <= 10^9
 */
class FindFirstAndLastPosition {
    /**
     * Approach 1: Two Binary Searches (Optimal)
     *
     * Find first position (leftmost) and last position (rightmost) separately.
     *
     * Time: O(log n)
     * Space: O(1)
     */
    public int[] searchRange(int[] nums, int target) {
        int[] result = {-1, -1};

        if (nums == null || nums.length == 0) {
            return result;
        }

        // Find first position
        result[0] = findFirst(nums, target);

        // If not found, no need to find last
        if (result[0] == -1) {
            return result;
        }

        // Find last position
        result[1] = findLast(nums, target);

        return result;
    }

    private int findFirst(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                result = mid;
                right = mid - 1; // Continue searching left
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return result;
    }

    private int findLast(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                result = mid;
                left = mid + 1; // Continue searching right
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return result;
    }

    /**
     * Approach 2: Single Binary Search then Expand
     *
     * Find any occurrence of target, then expand left and right.
     *
     * Time: O(log n + k) where k is number of target elements
     * Space: O(1)
     *
     * Note: This is worse than approach 1 when k is large
     */
    public int[] searchRangeExpand(int[] nums, int target) {
        int[] result = {-1, -1};

        if (nums == null || nums.length == 0) {
            return result;
        }

        // Find any occurrence
        int index = binarySearch(nums, target);

        if (index == -1) {
            return result;
        }

        // Expand left
        int left = index;
        while (left > 0 && nums[left - 1] == target) {
            left--;
        }

        // Expand right
        int right = index;
        while (right < nums.length - 1 && nums[right + 1] == target) {
            right++;
        }

        result[0] = left;
        result[1] = right;

        return result;
    }

    private int binarySearch(int[] nums, int target) {
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -1;
    }

    /**
     * Approach 3: Using Lower Bound and Upper Bound
     *
     * Use standard lower_bound and upper_bound concept.
     * - lower_bound: first element >= target
     * - upper_bound: first element > target
     *
     * Time: O(log n)
     * Space: O(1)
     */
    public int[] searchRangeBounds(int[] nums, int target) {
        int[] result = {-1, -1};

        if (nums == null || nums.length == 0) {
            return result;
        }

        int lower = lowerBound(nums, target);

        // Target not found
        if (lower == nums.length || nums[lower] != target) {
            return result;
        }

        int upper = upperBound(nums, target);

        result[0] = lower;
        result[1] = upper - 1; // upper_bound gives index after last occurrence

        return result;
    }

    // First index where nums[i] >= target
    private int lowerBound(int[] nums, int target) {
        int left = 0, right = nums.length;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }

    // First index where nums[i] > target
    private int upperBound(int[] nums, int target) {
        int left = 0, right = nums.length;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] <= target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }

    /**
     * Approach 4: Recursive Binary Search
     *
     * Time: O(log n)
     * Space: O(log n) for recursion stack
     */
    public int[] searchRangeRecursive(int[] nums, int target) {
        int[] result = {-1, -1};

        if (nums == null || nums.length == 0) {
            return result;
        }

        result[0] = findFirstRecursive(nums, target, 0, nums.length - 1);

        if (result[0] != -1) {
            result[1] = findLastRecursive(nums, target, 0, nums.length - 1);
        }

        return result;
    }

    private int findFirstRecursive(int[] nums, int target, int left, int right) {
        if (left > right) {
            return -1;
        }

        int mid = left + (right - left) / 2;

        if (nums[mid] == target) {
            // Check if this is the first occurrence
            if (mid == 0 || nums[mid - 1] != target) {
                return mid;
            }
            return findFirstRecursive(nums, target, left, mid - 1);
        } else if (nums[mid] < target) {
            return findFirstRecursive(nums, target, mid + 1, right);
        } else {
            return findFirstRecursive(nums, target, left, mid - 1);
        }
    }

    private int findLastRecursive(int[] nums, int target, int left, int right) {
        if (left > right) {
            return -1;
        }

        int mid = left + (right - left) / 2;

        if (nums[mid] == target) {
            // Check if this is the last occurrence
            if (mid == nums.length - 1 || nums[mid + 1] != target) {
                return mid;
            }
            return findLastRecursive(nums, target, mid + 1, right);
        } else if (nums[mid] < target) {
            return findLastRecursive(nums, target, mid + 1, right);
        } else {
            return findLastRecursive(nums, target, left, mid - 1);
        }
    }

    /**
     * Approach 5: Single pass with modified binary search
     *
     * Find both positions in a single binary search traversal.
     *
     * Time: O(log n)
     * Space: O(1)
     */
    public int[] searchRangeSinglePass(int[] nums, int target) {
        int[] result = {-1, -1};

        if (nums == null || nums.length == 0) {
            return result;
        }

        findRange(nums, target, 0, nums.length - 1, result);
        return result;
    }

    private void findRange(int[] nums, int target, int left, int right, int[] result) {
        if (left > right) {
            return;
        }

        if (nums[left] == target && nums[right] == target) {
            result[0] = left;
            result[1] = right;
            return;
        }

        int mid = left + (right - left) / 2;

        if (nums[mid] < target) {
            findRange(nums, target, mid + 1, right, result);
        } else if (nums[mid] > target) {
            findRange(nums, target, left, mid - 1, result);
        } else {
            // nums[mid] == target
            findRange(nums, target, left, mid, result);
            findRange(nums, target, mid + 1, right, result);
        }
    }
}

/**
 * Related variations
 */
class FindFirstAndLastPositionVariations {
    /**
     * Count occurrences of target
     */
    public int countOccurrences(int[] nums, int target) {
        int first = findFirst(nums, target);
        if (first == -1) {
            return 0;
        }
        int last = findLast(nums, target);
        return last - first + 1;
    }

    private int findFirst(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                result = mid;
                right = mid - 1;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return result;
    }

    private int findLast(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                result = mid;
                left = mid + 1;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return result;
    }

    /**
     * Find range for all values in a list of targets
     */
    public Map<Integer, int[]> searchMultipleRanges(int[] nums, int[] targets) {
        Map<Integer, int[]> result = new HashMap<>();

        for (int target : targets) {
            int first = findFirst(nums, target);
            if (first == -1) {
                result.put(target, new int[]{-1, -1});
            } else {
                int last = findLast(nums, target);
                result.put(target, new int[]{first, last});
            }
        }

        return result;
    }

    /**
     * Find the element that appears more than n/2 times (if exists)
     * Use binary search range finding
     */
    public int findMajorityElement(int[] nums) {
        if (nums == null || nums.length == 0) {
            return -1;
        }

        // Check middle element (optimization for majority element)
        int candidate = nums[nums.length / 2];
        int first = findFirst(nums, candidate);
        int last = findLast(nums, candidate);

        if (last - first + 1 > nums.length / 2) {
            return candidate;
        }

        return -1;
    }

    /**
     * Find range in 2D matrix (sorted row-wise and column-wise)
     */
    public int countOccurrences2D(int[][] matrix, int target) {
        int count = 0;
        for (int[] row : matrix) {
            int first = findFirst(row, target);
            if (first != -1) {
                int last = findLast(row, target);
                count += (last - first + 1);
            }
        }
        return count;
    }
}

/**
 * Test cases
 */
class FindFirstAndLastPositionTest {
    public static void main(String[] args) {
        testBasicCases();
        testEdgeCases();
        testVariations();
    }

    private static void testBasicCases() {
        System.out.println("=== Testing LeetCode 34: Find First and Last Position ===\n");
        FindFirstAndLastPosition solution = new FindFirstAndLastPosition();

        // Test 1
        int[] nums1 = {5, 7, 7, 8, 8, 10};
        int target1 = 8;
        int[] result1 = solution.searchRange(nums1, target1);
        int[] result1Expand = solution.searchRangeExpand(nums1, target1);
        int[] result1Bounds = solution.searchRangeBounds(nums1, target1);
        System.out.println("Test 1: nums = [5,7,7,8,8,10], target = 8");
        System.out.println("  Two Binary Searches: " + Arrays.toString(result1) + " (Expected: [3,4]) - " +
                          (Arrays.equals(result1, new int[]{3, 4}) ? "PASS" : "FAIL"));
        System.out.println("  Expand: " + Arrays.toString(result1Expand) + " (Expected: [3,4]) - " +
                          (Arrays.equals(result1Expand, new int[]{3, 4}) ? "PASS" : "FAIL"));
        System.out.println("  Bounds: " + Arrays.toString(result1Bounds) + " (Expected: [3,4]) - " +
                          (Arrays.equals(result1Bounds, new int[]{3, 4}) ? "PASS" : "FAIL"));
        System.out.println();

        // Test 2: Target not found
        int target2 = 6;
        int[] result2 = solution.searchRange(nums1, target2);
        System.out.println("Test 2: nums = [5,7,7,8,8,10], target = 6");
        System.out.println("  Result: " + Arrays.toString(result2) + " (Expected: [-1,-1]) - " +
                          (Arrays.equals(result2, new int[]{-1, -1}) ? "PASS" : "FAIL"));
        System.out.println();

        // Test 3: Empty array
        int[] nums3 = {};
        int target3 = 0;
        int[] result3 = solution.searchRange(nums3, target3);
        System.out.println("Test 3: nums = [], target = 0");
        System.out.println("  Result: " + Arrays.toString(result3) + " (Expected: [-1,-1]) - " +
                          (Arrays.equals(result3, new int[]{-1, -1}) ? "PASS" : "FAIL"));
        System.out.println();

        // Test 4: Single element (found)
        int[] nums4 = {1};
        int target4 = 1;
        int[] result4 = solution.searchRange(nums4, target4);
        System.out.println("Test 4: nums = [1], target = 1");
        System.out.println("  Result: " + Arrays.toString(result4) + " (Expected: [0,0]) - " +
                          (Arrays.equals(result4, new int[]{0, 0}) ? "PASS" : "FAIL"));
        System.out.println();

        // Test 5: All same elements
        int[] nums5 = {2, 2, 2, 2, 2};
        int target5 = 2;
        int[] result5 = solution.searchRange(nums5, target5);
        System.out.println("Test 5: nums = [2,2,2,2,2], target = 2");
        System.out.println("  Result: " + Arrays.toString(result5) + " (Expected: [0,4]) - " +
                          (Arrays.equals(result5, new int[]{0, 4}) ? "PASS" : "FAIL"));
        System.out.println();

        // Test 6: Recursive approach
        int[] result6 = solution.searchRangeRecursive(nums1, target1);
        System.out.println("Test 6: Recursive approach with [5,7,7,8,8,10], target = 8");
        System.out.println("  Result: " + Arrays.toString(result6) + " (Expected: [3,4]) - " +
                          (Arrays.equals(result6, new int[]{3, 4}) ? "PASS" : "FAIL"));
        System.out.println();
    }

    private static void testEdgeCases() {
        System.out.println("=== Testing Edge Cases ===\n");
        FindFirstAndLastPosition solution = new FindFirstAndLastPosition();

        // Test 1: Target at beginning
        int[] nums1 = {1, 1, 1, 2, 3, 4};
        int target1 = 1;
        int[] result1 = solution.searchRange(nums1, target1);
        System.out.println("Test 1: Target at beginning [1,1,1,2,3,4], target = 1");
        System.out.println("  Result: " + Arrays.toString(result1) + " (Expected: [0,2]) - " +
                          (Arrays.equals(result1, new int[]{0, 2}) ? "PASS" : "FAIL"));
        System.out.println();

        // Test 2: Target at end
        int[] nums2 = {1, 2, 3, 4, 4, 4};
        int target2 = 4;
        int[] result2 = solution.searchRange(nums2, target2);
        System.out.println("Test 2: Target at end [1,2,3,4,4,4], target = 4");
        System.out.println("  Result: " + Arrays.toString(result2) + " (Expected: [3,5]) - " +
                          (Arrays.equals(result2, new int[]{3, 5}) ? "PASS" : "FAIL"));
        System.out.println();

        // Test 3: Single occurrence
        int[] nums3 = {1, 2, 3, 4, 5};
        int target3 = 3;
        int[] result3 = solution.searchRange(nums3, target3);
        System.out.println("Test 3: Single occurrence [1,2,3,4,5], target = 3");
        System.out.println("  Result: " + Arrays.toString(result3) + " (Expected: [2,2]) - " +
                          (Arrays.equals(result3, new int[]{2, 2}) ? "PASS" : "FAIL"));
        System.out.println();

        // Test 4: Two elements, both target
        int[] nums4 = {5, 5};
        int target4 = 5;
        int[] result4 = solution.searchRange(nums4, target4);
        System.out.println("Test 4: Two elements [5,5], target = 5");
        System.out.println("  Result: " + Arrays.toString(result4) + " (Expected: [0,1]) - " +
                          (Arrays.equals(result4, new int[]{0, 1}) ? "PASS" : "FAIL"));
        System.out.println();

        // Test 5: Large array with target in middle
        int[] nums5 = new int[1000];
        Arrays.fill(nums5, 0, 400, 1);
        Arrays.fill(nums5, 400, 600, 5);
        Arrays.fill(nums5, 600, 1000, 10);
        int target5 = 5;
        int[] result5 = solution.searchRange(nums5, target5);
        System.out.println("Test 5: Large array with target in middle, target = 5");
        System.out.println("  Result: " + Arrays.toString(result5) + " (Expected: [400,599]) - " +
                          (Arrays.equals(result5, new int[]{400, 599}) ? "PASS" : "FAIL"));
        System.out.println();
    }

    private static void testVariations() {
        System.out.println("=== Testing Variations ===\n");
        FindFirstAndLastPositionVariations variations = new FindFirstAndLastPositionVariations();

        // Test 1: Count occurrences
        int[] nums1 = {5, 7, 7, 8, 8, 10};
        int count1 = variations.countOccurrences(nums1, 8);
        System.out.println("Test 1: Count occurrences of 8 in [5,7,7,8,8,10]");
        System.out.println("  Result: " + count1 + " (Expected: 2) - " + (count1 == 2 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 2: Multiple targets
        int[] targets = {5, 7, 8, 10};
        Map<Integer, int[]> result2 = variations.searchMultipleRanges(nums1, targets);
        System.out.println("Test 2: Search multiple targets");
        for (int target : targets) {
            System.out.println("  Target " + target + ": " + Arrays.toString(result2.get(target)));
        }
        System.out.println();

        // Test 3: Find majority element
        int[] nums3 = {2, 2, 2, 3, 3};
        int majority = variations.findMajorityElement(nums3);
        System.out.println("Test 3: Find majority element in [2,2,2,3,3]");
        System.out.println("  Result: " + majority + " (Expected: 2) - " + (majority == 2 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 4: Count in 2D matrix
        int[][] matrix = {
            {1, 2, 2, 3},
            {2, 2, 3, 4},
            {2, 3, 4, 5}
        };
        int count4 = variations.countOccurrences2D(matrix, 2);
        System.out.println("Test 4: Count 2 in 2D matrix");
        System.out.println("  Result: " + count4 + " (Expected: 5) - " + (count4 == 5 ? "PASS" : "FAIL"));
        System.out.println();
    }
}
