import java.util.*;

/**
 * LeetCode 33: Search in Rotated Sorted Array
 *
 * There is an integer array nums sorted in ascending order (with distinct values).
 *
 * Prior to being passed to your function, nums is possibly rotated at an unknown pivot index k
 * (1 <= k < nums.length) such that the resulting array is [nums[k], nums[k+1], ..., nums[n-1],
 * nums[0], nums[1], ..., nums[k-1]] (0-indexed). For example, [0,1,2,4,5,6,7] might be rotated
 * at pivot index 3 and become [4,5,6,7,0,1,2].
 *
 * Given the array nums after the possible rotation and an integer target, return the index of
 * target if it is in nums, or -1 if it is not in nums.
 *
 * You must write an algorithm with O(log n) runtime complexity.
 *
 * Example 1:
 * Input: nums = [4,5,6,7,0,1,2], target = 0
 * Output: 4
 *
 * Example 2:
 * Input: nums = [4,5,6,7,0,1,2], target = 3
 * Output: -1
 *
 * Example 3:
 * Input: nums = [1], target = 0
 * Output: -1
 *
 * Constraints:
 * - 1 <= nums.length <= 5000
 * - -10^4 <= nums[i] <= 10^4
 * - All values of nums are unique.
 * - nums is an ascending array that is possibly rotated.
 * - -10^4 <= target <= 10^4
 */
class SearchRotatedSortedArray {
    /**
     * Approach 1: Modified Binary Search (One Pass)
     *
     * Key Insight: At least one half of the array is always sorted.
     * - If left half is sorted and target is in range, search left
     * - If right half is sorted and target is in range, search right
     * - Otherwise, search the other half
     *
     * Time: O(log n)
     * Space: O(1)
     */
    public int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                return mid;
            }

            // Determine which half is sorted
            if (nums[left] <= nums[mid]) {
                // Left half is sorted
                if (nums[left] <= target && target < nums[mid]) {
                    // Target is in sorted left half
                    right = mid - 1;
                } else {
                    // Target is in unsorted right half
                    left = mid + 1;
                }
            } else {
                // Right half is sorted
                if (nums[mid] < target && target <= nums[right]) {
                    // Target is in sorted right half
                    left = mid + 1;
                } else {
                    // Target is in unsorted left half
                    right = mid - 1;
                }
            }
        }

        return -1;
    }

    /**
     * Approach 2: Find Pivot, Then Binary Search
     *
     * Two steps:
     * 1. Find the rotation pivot
     * 2. Determine which side to search and use standard binary search
     *
     * Time: O(log n)
     * Space: O(1)
     */
    public int searchTwoPass(int[] nums, int target) {
        int n = nums.length;

        // Step 1: Find pivot (smallest element index)
        int pivot = findPivot(nums);

        // Step 2: Determine which side to search
        if (pivot == 0) {
            // Array not rotated
            return binarySearch(nums, 0, n - 1, target);
        }

        if (target >= nums[0]) {
            // Search in left side
            return binarySearch(nums, 0, pivot - 1, target);
        } else {
            // Search in right side
            return binarySearch(nums, pivot, n - 1, target);
        }
    }

    private int findPivot(int[] nums) {
        int left = 0, right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] > nums[right]) {
                // Pivot is in right half
                left = mid + 1;
            } else {
                // Pivot is in left half or at mid
                right = mid;
            }
        }

        return left;
    }

    private int binarySearch(int[] nums, int left, int right, int target) {
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
     * Approach 3: Recursive Binary Search
     *
     * Time: O(log n)
     * Space: O(log n) for recursion stack
     */
    public int searchRecursive(int[] nums, int target) {
        return searchHelper(nums, 0, nums.length - 1, target);
    }

    private int searchHelper(int[] nums, int left, int right, int target) {
        if (left > right) {
            return -1;
        }

        int mid = left + (right - left) / 2;

        if (nums[mid] == target) {
            return mid;
        }

        // Check which half is sorted
        if (nums[left] <= nums[mid]) {
            // Left half is sorted
            if (nums[left] <= target && target < nums[mid]) {
                return searchHelper(nums, left, mid - 1, target);
            } else {
                return searchHelper(nums, mid + 1, right, target);
            }
        } else {
            // Right half is sorted
            if (nums[mid] < target && target <= nums[right]) {
                return searchHelper(nums, mid + 1, right, target);
            } else {
                return searchHelper(nums, left, mid - 1, target);
            }
        }
    }

    /**
     * Approach 4: Find pivot and use modular arithmetic
     *
     * Convert rotated array indices to original array indices.
     *
     * Time: O(log n)
     * Space: O(1)
     */
    public int searchModular(int[] nums, int target) {
        int n = nums.length;
        int pivot = findPivot(nums);

        int left = 0, right = n - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int realMid = (mid + pivot) % n; // Convert to rotated index

            if (nums[realMid] == target) {
                return realMid;
            } else if (nums[realMid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -1;
    }
}

/**
 * Related Problems
 */
class SearchRotatedSortedArrayVariations {
    /**
     * LeetCode 81: Search in Rotated Sorted Array II (with duplicates)
     *
     * When duplicates exist, we can't always determine which half is sorted.
     * Worst case becomes O(n).
     */
    public boolean searchWithDuplicates(int[] nums, int target) {
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                return true;
            }

            // Handle duplicates
            if (nums[left] == nums[mid] && nums[mid] == nums[right]) {
                left++;
                right--;
            } else if (nums[left] <= nums[mid]) {
                // Left half is sorted
                if (nums[left] <= target && target < nums[mid]) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            } else {
                // Right half is sorted
                if (nums[mid] < target && target <= nums[right]) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
        }

        return false;
    }

    /**
     * LeetCode 153: Find Minimum in Rotated Sorted Array
     */
    public int findMin(int[] nums) {
        int left = 0, right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] > nums[right]) {
                // Minimum is in right half
                left = mid + 1;
            } else {
                // Minimum is in left half or at mid
                right = mid;
            }
        }

        return nums[left];
    }

    /**
     * LeetCode 154: Find Minimum in Rotated Sorted Array II (with duplicates)
     */
    public int findMinWithDuplicates(int[] nums) {
        int left = 0, right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] > nums[right]) {
                left = mid + 1;
            } else if (nums[mid] < nums[right]) {
                right = mid;
            } else {
                // Can't determine, reduce search space
                right--;
            }
        }

        return nums[left];
    }

    /**
     * Find the rotation count (number of rotations)
     * This is the index of the minimum element
     */
    public int findRotationCount(int[] nums) {
        int left = 0, right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] > nums[right]) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left; // Index of minimum element = rotation count
    }

    /**
     * Check if array is rotated sorted array
     */
    public boolean isRotatedSorted(int[] nums) {
        int n = nums.length;
        int rotations = 0;

        for (int i = 0; i < n - 1; i++) {
            if (nums[i] > nums[i + 1]) {
                rotations++;
            }
        }

        // Valid if 0 rotations (already sorted) or 1 rotation point
        // and last element < first element
        return rotations <= 1 && (rotations == 0 || nums[n - 1] < nums[0]);
    }
}

/**
 * Test cases
 */
class SearchRotatedSortedArrayTest {
    public static void main(String[] args) {
        testBasicSearch();
        testEdgeCases();
        testVariations();
    }

    private static void testBasicSearch() {
        System.out.println("=== Testing LeetCode 33: Search in Rotated Sorted Array ===\n");
        SearchRotatedSortedArray solution = new SearchRotatedSortedArray();

        // Test 1
        int[] nums1 = {4, 5, 6, 7, 0, 1, 2};
        int target1 = 0;
        int result1 = solution.search(nums1, target1);
        int result1TwoPass = solution.searchTwoPass(nums1, target1);
        int result1Rec = solution.searchRecursive(nums1, target1);
        System.out.println("Test 1: nums = [4,5,6,7,0,1,2], target = 0");
        System.out.println("  One Pass: " + result1 + " (Expected: 4) - " + (result1 == 4 ? "PASS" : "FAIL"));
        System.out.println("  Two Pass: " + result1TwoPass + " (Expected: 4) - " + (result1TwoPass == 4 ? "PASS" : "FAIL"));
        System.out.println("  Recursive: " + result1Rec + " (Expected: 4) - " + (result1Rec == 4 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 2: Target not found
        int target2 = 3;
        int result2 = solution.search(nums1, target2);
        System.out.println("Test 2: nums = [4,5,6,7,0,1,2], target = 3");
        System.out.println("  Result: " + result2 + " (Expected: -1) - " + (result2 == -1 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 3: Single element
        int[] nums3 = {1};
        int target3 = 0;
        int result3 = solution.search(nums3, target3);
        System.out.println("Test 3: nums = [1], target = 0");
        System.out.println("  Result: " + result3 + " (Expected: -1) - " + (result3 == -1 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 4: No rotation
        int[] nums4 = {1, 2, 3, 4, 5};
        int target4 = 3;
        int result4 = solution.search(nums4, target4);
        System.out.println("Test 4: nums = [1,2,3,4,5] (no rotation), target = 3");
        System.out.println("  Result: " + result4 + " (Expected: 2) - " + (result4 == 2 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 5: Rotated at last position
        int[] nums5 = {2, 3, 4, 5, 1};
        int target5 = 1;
        int result5 = solution.search(nums5, target5);
        System.out.println("Test 5: nums = [2,3,4,5,1], target = 1");
        System.out.println("  Result: " + result5 + " (Expected: 4) - " + (result5 == 4 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 6: Target at pivot
        int target6 = 4;
        int result6 = solution.search(nums1, target6);
        System.out.println("Test 6: nums = [4,5,6,7,0,1,2], target = 4");
        System.out.println("  Result: " + result6 + " (Expected: 0) - " + (result6 == 0 ? "PASS" : "FAIL"));
        System.out.println();
    }

    private static void testEdgeCases() {
        System.out.println("=== Testing Edge Cases ===\n");
        SearchRotatedSortedArray solution = new SearchRotatedSortedArray();

        // Test 1: Two elements
        int[] nums1 = {3, 1};
        System.out.println("Test 1: nums = [3,1]");
        System.out.println("  Search 1: " + solution.search(nums1, 1) + " (Expected: 1)");
        System.out.println("  Search 3: " + solution.search(nums1, 3) + " (Expected: 0)");
        System.out.println();

        // Test 2: Target is first element
        int[] nums2 = {4, 5, 6, 7, 0, 1, 2};
        int result2 = solution.search(nums2, 4);
        System.out.println("Test 2: nums = [4,5,6,7,0,1,2], target = 4 (first element)");
        System.out.println("  Result: " + result2 + " (Expected: 0) - " + (result2 == 0 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 3: Target is last element
        int result3 = solution.search(nums2, 2);
        System.out.println("Test 3: nums = [4,5,6,7,0,1,2], target = 2 (last element)");
        System.out.println("  Result: " + result3 + " (Expected: 6) - " + (result3 == 6 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 4: Large rotation
        int[] nums4 = {7, 8, 1, 2, 3, 4, 5, 6};
        int result4 = solution.search(nums4, 1);
        System.out.println("Test 4: nums = [7,8,1,2,3,4,5,6], target = 1");
        System.out.println("  Result: " + result4 + " (Expected: 2) - " + (result4 == 2 ? "PASS" : "FAIL"));
        System.out.println();
    }

    private static void testVariations() {
        System.out.println("=== Testing Related Problems ===\n");
        SearchRotatedSortedArrayVariations variations = new SearchRotatedSortedArrayVariations();

        // Test 1: Search with duplicates
        int[] nums1 = {2, 5, 6, 0, 0, 1, 2};
        boolean result1 = variations.searchWithDuplicates(nums1, 0);
        System.out.println("Test 1: Search with duplicates [2,5,6,0,0,1,2], target = 0");
        System.out.println("  Result: " + result1 + " (Expected: true) - " + (result1 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 2: Find minimum
        int[] nums2 = {4, 5, 6, 7, 0, 1, 2};
        int result2 = variations.findMin(nums2);
        System.out.println("Test 2: Find minimum in [4,5,6,7,0,1,2]");
        System.out.println("  Result: " + result2 + " (Expected: 0) - " + (result2 == 0 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 3: Find minimum with duplicates
        int[] nums3 = {2, 2, 2, 0, 1};
        int result3 = variations.findMinWithDuplicates(nums3);
        System.out.println("Test 3: Find minimum with duplicates [2,2,2,0,1]");
        System.out.println("  Result: " + result3 + " (Expected: 0) - " + (result3 == 0 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 4: Find rotation count
        int result4 = variations.findRotationCount(nums2);
        System.out.println("Test 4: Find rotation count [4,5,6,7,0,1,2]");
        System.out.println("  Result: " + result4 + " (Expected: 4) - " + (result4 == 4 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 5: Check if rotated sorted
        boolean result5a = variations.isRotatedSorted(nums2);
        int[] nums5b = {3, 1, 2};
        boolean result5b = variations.isRotatedSorted(nums5b);
        System.out.println("Test 5: Check if rotated sorted");
        System.out.println("  [4,5,6,7,0,1,2]: " + result5a + " (Expected: true) - " + (result5a ? "PASS" : "FAIL"));
        System.out.println("  [3,1,2]: " + result5b + " (Expected: false) - " + (!result5b ? "PASS" : "FAIL"));
        System.out.println();
    }
}
