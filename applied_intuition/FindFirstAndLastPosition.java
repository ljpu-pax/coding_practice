import java.util.*;

/**
 * LeetCode 34: Find First and Last Position of Element in Sorted Array (Medium)
 *
 * Given an array of integers nums sorted in non-decreasing order, find the starting
 * and ending position of a given target value.
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
 * - nums is a non-decreasing array
 * - -10^9 <= target <= 10^9
 */
class FindFirstAndLastPosition {

    /**
     * Approach 1: Two Binary Searches
     *
     * Use binary search twice:
     * 1. Find leftmost (first) position
     * 2. Find rightmost (last) position
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

        // If not found, return early
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
     * Approach 2: Unified Binary Search Template
     *
     * Use a single binary search function with a flag for left/right bound
     */
    public int[] searchRangeUnified(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return new int[]{-1, -1};
        }

        int first = binarySearch(nums, target, true);
        if (first == -1) {
            return new int[]{-1, -1};
        }

        int last = binarySearch(nums, target, false);
        return new int[]{first, last};
    }

    private int binarySearch(int[] nums, int target, boolean findFirst) {
        int left = 0, right = nums.length - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                result = mid;
                if (findFirst) {
                    right = mid - 1; // Search left half
                } else {
                    left = mid + 1;  // Search right half
                }
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return result;
    }

    /**
     * Approach 3: Find first, then expand
     *
     * Find any occurrence with binary search, then expand left and right
     * Worst case: O(n) when all elements are target
     */
    public int[] searchRangeExpand(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return new int[]{-1, -1};
        }

        // Find any occurrence
        int pos = binarySearchAny(nums, target);
        if (pos == -1) {
            return new int[]{-1, -1};
        }

        // Expand left
        int left = pos;
        while (left > 0 && nums[left - 1] == target) {
            left--;
        }

        // Expand right
        int right = pos;
        while (right < nums.length - 1 && nums[right + 1] == target) {
            right++;
        }

        return new int[]{left, right};
    }

    private int binarySearchAny(int[] nums, int target) {
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
     * Approach 4: Using lower_bound and upper_bound concept
     *
     * lower_bound: first position where nums[i] >= target
     * upper_bound: first position where nums[i] > target
     */
    public int[] searchRangeBounds(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return new int[]{-1, -1};
        }

        int lower = lowerBound(nums, target);
        if (lower == nums.length || nums[lower] != target) {
            return new int[]{-1, -1};
        }

        int upper = upperBound(nums, target) - 1;
        return new int[]{lower, upper};
    }

    // First position where nums[i] >= target
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

    // First position where nums[i] > target
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
}

/**
 * Related variations
 */
class FindFirstAndLastPositionVariations {
    /**
     * Count occurrences of target
     */
    public int countOccurrences(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return 0;
        }

        int first = findFirst(nums, target);
        if (first == -1) {
            return 0;
        }

        int last = findLast(nums, target);
        return last - first + 1;
    }

    /**
     * Find range for all elements in range [lower, upper]
     */
    public int[][] findRanges(int[] nums, int[][] queries) {
        int[][] results = new int[queries.length][2];

        for (int i = 0; i < queries.length; i++) {
            results[i] = findRange(nums, queries[i][0], queries[i][1]);
        }

        return results;
    }

    private int[] findRange(int[] nums, int lower, int upper) {
        int first = lowerBound(nums, lower);
        int last = upperBound(nums, upper) - 1;

        if (first > last || first >= nums.length) {
            return new int[]{-1, -1};
        }

        return new int[]{first, last};
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
}

/**
 * Test cases
 */
class FindFirstAndLastPositionTest {
    public static void main(String[] args) {
        testBasicCases();
        testEdgeCases();
        testAllApproaches();
        testVariations();
    }

    private static void testBasicCases() {
        System.out.println("=== LeetCode 34: Find First and Last Position ===\n");
        FindFirstAndLastPosition solution = new FindFirstAndLastPosition();

        // Test 1
        int[] nums1 = {5, 7, 7, 8, 8, 10};
        int target1 = 8;
        int[] result1 = solution.searchRange(nums1, target1);
        System.out.println("Test 1: " + Arrays.toString(nums1) + ", target=" + target1);
        System.out.println("Result: " + Arrays.toString(result1));
        System.out.println("Expected: [3, 4]");
        System.out.println();

        // Test 2
        int[] nums2 = {5, 7, 7, 8, 8, 10};
        int target2 = 6;
        int[] result2 = solution.searchRange(nums2, target2);
        System.out.println("Test 2: " + Arrays.toString(nums2) + ", target=" + target2);
        System.out.println("Result: " + Arrays.toString(result2));
        System.out.println("Expected: [-1, -1]");
        System.out.println();

        // Test 3
        int[] nums3 = {};
        int target3 = 0;
        int[] result3 = solution.searchRange(nums3, target3);
        System.out.println("Test 3: Empty array, target=" + target3);
        System.out.println("Result: " + Arrays.toString(result3));
        System.out.println("Expected: [-1, -1]");
        System.out.println();
    }

    private static void testEdgeCases() {
        System.out.println("=== Edge Cases ===\n");
        FindFirstAndLastPosition solution = new FindFirstAndLastPosition();

        // Single element found
        int[] nums1 = {1};
        System.out.println("Single element (found): " + Arrays.toString(solution.searchRange(nums1, 1)));

        // Single element not found
        System.out.println("Single element (not found): " + Arrays.toString(solution.searchRange(nums1, 2)));

        // All same elements
        int[] nums2 = {1, 1, 1, 1, 1};
        System.out.println("All same: " + Arrays.toString(solution.searchRange(nums2, 1)));

        // Target at start
        int[] nums3 = {1, 1, 2, 3, 4};
        System.out.println("Target at start: " + Arrays.toString(solution.searchRange(nums3, 1)));

        // Target at end
        int[] nums4 = {1, 2, 3, 4, 4};
        System.out.println("Target at end: " + Arrays.toString(solution.searchRange(nums4, 4)));
        System.out.println();
    }

    private static void testAllApproaches() {
        System.out.println("=== Comparing All Approaches ===\n");
        FindFirstAndLastPosition solution = new FindFirstAndLastPosition();

        int[] nums = {5, 7, 7, 8, 8, 10};
        int target = 8;

        int[] result1 = solution.searchRange(nums, target);
        int[] result2 = solution.searchRangeUnified(nums, target);
        int[] result3 = solution.searchRangeExpand(nums, target);
        int[] result4 = solution.searchRangeBounds(nums, target);

        System.out.println("Two binary searches: " + Arrays.toString(result1));
        System.out.println("Unified template:    " + Arrays.toString(result2));
        System.out.println("Find and expand:     " + Arrays.toString(result3));
        System.out.println("Lower/upper bound:   " + Arrays.toString(result4));
        System.out.println();
    }

    private static void testVariations() {
        System.out.println("=== Variations ===\n");
        FindFirstAndLastPositionVariations variations = new FindFirstAndLastPositionVariations();

        int[] nums = {1, 2, 2, 2, 3, 3, 4, 5, 5, 5, 5, 6};

        // Count occurrences
        System.out.println("Count of 5: " + variations.countOccurrences(nums, 5)); // 4
        System.out.println("Count of 2: " + variations.countOccurrences(nums, 2)); // 3
        System.out.println("Count of 7: " + variations.countOccurrences(nums, 7)); // 0
        System.out.println();
    }
}

/**
 * Key Insights
 */
class BinarySearchInsights {
    /*
     * Binary Search Templates:
     * ========================
     *
     * 1. Find First (Leftmost):
     *    - When found, search left half: right = mid - 1
     *    - Keep track of result
     *
     * 2. Find Last (Rightmost):
     *    - When found, search right half: left = mid + 1
     *    - Keep track of result
     *
     * 3. Lower Bound (first >= target):
     *    - Use [left, right) range
     *    - if nums[mid] < target: left = mid + 1
     *    - else: right = mid
     *
     * 4. Upper Bound (first > target):
     *    - Use [left, right) range
     *    - if nums[mid] <= target: left = mid + 1
     *    - else: right = mid
     *
     * Common Pitfalls:
     * ================
     * - Off-by-one errors in boundary conditions
     * - Not storing result before updating pointers
     * - Integer overflow in mid calculation: use left + (right - left) / 2
     * - Different loop conditions: <= vs <
     *
     * Related Problems:
     * =================
     * - 278: First Bad Version
     * - 35: Search Insert Position
     * - 153: Find Minimum in Rotated Sorted Array
     * - 162: Find Peak Element
     * - 441: Arranging Coins
     */
}
