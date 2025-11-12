import java.util.*;

/**
 * LeetCode 540: Single Element in a Sorted Array
 *
 * You are given a sorted array consisting of only integers where every element appears exactly
 * twice, except for one element which appears exactly once.
 *
 * Return the single element that appears only once.
 *
 * Your solution must run in O(log n) time and O(1) space.
 *
 * Example 1:
 * Input: nums = [1,1,2,3,3,4,4,8,8]
 * Output: 2
 *
 * Example 2:
 * Input: nums = [3,3,7,7,10,11,11]
 * Output: 10
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - 0 <= nums[i] <= 10^5
 * - nums is sorted in non-decreasing order.
 */
class SingleElementInSortedArray {
    /**
     * Approach 1: Binary Search on Even Indices (Optimal)
     *
     * Key Insight:
     * - Before the single element, pairs start at even indices: (0,1), (2,3), (4,5)...
     * - After the single element, pairs start at odd indices
     * - Check only even indices to determine which half contains the single element
     *
     * Time: O(log n)
     * Space: O(1)
     */
    public int singleNonDuplicate(int[] nums) {
        int left = 0, right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            // Make sure mid is even
            if (mid % 2 == 1) {
                mid--;
            }

            // Check if pair starts at mid
            if (nums[mid] == nums[mid + 1]) {
                // Pair is intact, single element is on the right
                left = mid + 2;
            } else {
                // Pair is broken, single element is on the left (or at mid)
                right = mid;
            }
        }

        return nums[left];
    }

    /**
     * Approach 2: Binary Search with XOR Index Check
     *
     * Use XOR property to handle both even and odd indices.
     * If mid is even and nums[mid] == nums[mid+1], or
     * if mid is odd and nums[mid] == nums[mid-1],
     * then single element is on the right.
     *
     * Time: O(log n)
     * Space: O(1)
     */
    public int singleNonDuplicateXOR(int[] nums) {
        int left = 0, right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            // Check if mid's pair is on the right or left
            boolean pairOnRight = (mid % 2 == 0 && mid + 1 < nums.length && nums[mid] == nums[mid + 1]) ||
                                 (mid % 2 == 1 && nums[mid] == nums[mid - 1]);

            if (pairOnRight) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return nums[left];
    }

    /**
     * Approach 3: Binary Search with Bit Manipulation
     *
     * Use XOR (^1) to flip between even and odd indices.
     * - If mid is even, mid^1 gives mid+1
     * - If mid is odd, mid^1 gives mid-1
     *
     * Time: O(log n)
     * Space: O(1)
     */
    public int singleNonDuplicateBitwise(int[] nums) {
        int left = 0, right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            // Check if mid and its pair index have same value
            if (nums[mid] == nums[mid ^ 1]) {
                // Pair is intact, search right
                left = mid + 1;
            } else {
                // Pair is broken, search left
                right = mid;
            }
        }

        return nums[left];
    }

    /**
     * Approach 4: XOR All Elements (Works but O(n) - not optimal)
     *
     * XOR all elements. Pairs cancel out, leaving only single element.
     *
     * Time: O(n)
     * Space: O(1)
     *
     * Note: This doesn't use the sorted property and is not optimal for this problem.
     */
    public int singleNonDuplicateXORAll(int[] nums) {
        int result = 0;
        for (int num : nums) {
            result ^= num;
        }
        return result;
    }

    /**
     * Approach 5: Compare Adjacent Elements (O(n) - not optimal)
     *
     * Linear scan to find element that doesn't match its neighbors.
     *
     * Time: O(n)
     * Space: O(1)
     */
    public int singleNonDuplicateLinear(int[] nums) {
        int n = nums.length;

        // Check first element
        if (n == 1 || nums[0] != nums[1]) {
            return nums[0];
        }

        // Check last element
        if (nums[n - 1] != nums[n - 2]) {
            return nums[n - 1];
        }

        // Check middle elements
        for (int i = 1; i < n - 1; i++) {
            if (nums[i] != nums[i - 1] && nums[i] != nums[i + 1]) {
                return nums[i];
            }
        }

        return -1; // Should never reach here
    }

    /**
     * Approach 6: Binary Search - Alternative Implementation
     *
     * Check the parity of elements before mid to determine search direction.
     *
     * Time: O(log n)
     * Space: O(1)
     */
    public int singleNonDuplicateAlternative(int[] nums) {
        int left = 0, right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            // Count elements to the left of mid (including mid)
            int elementsToLeft = mid - left + 1;

            // If nums[mid] != nums[mid-1] and nums[mid] != nums[mid+1]
            if ((mid == 0 || nums[mid] != nums[mid - 1]) &&
                (mid == nums.length - 1 || nums[mid] != nums[mid + 1])) {
                return nums[mid];
            }

            // Check if mid is part of a pair with mid-1
            if (mid > 0 && nums[mid] == nums[mid - 1]) {
                // Pair includes mid-1 and mid
                // If we have odd elements to the left (including pair), single is on left
                if (elementsToLeft % 2 == 1) {
                    right = mid - 2;
                } else {
                    left = mid + 1;
                }
            } else {
                // Pair includes mid and mid+1
                // If we have even elements to the left (before pair), single is on right
                if (elementsToLeft % 2 == 0) {
                    left = mid + 2;
                } else {
                    right = mid;
                }
            }
        }

        return nums[left];
    }
}

/**
 * Related variations
 */
class SingleElementVariations {
    /**
     * Find single element where all others appear three times
     * (LeetCode 137: Single Number II)
     */
    public int singleNumberThrice(int[] nums) {
        int ones = 0, twos = 0;

        for (int num : nums) {
            twos |= ones & num;
            ones ^= num;
            int threes = ones & twos;
            ones &= ~threes;
            twos &= ~threes;
        }

        return ones;
    }

    /**
     * Find single element in unsorted array where all others appear twice
     * (LeetCode 136: Single Number)
     */
    public int singleNumberUnsorted(int[] nums) {
        int result = 0;
        for (int num : nums) {
            result ^= num;
        }
        return result;
    }

    /**
     * Find two single elements where all others appear twice
     * (LeetCode 260: Single Number III)
     */
    public int[] singleNumberTwo(int[] nums) {
        // XOR all elements
        int xor = 0;
        for (int num : nums) {
            xor ^= num;
        }

        // Find rightmost set bit
        int rightmostBit = xor & (-xor);

        int num1 = 0, num2 = 0;
        for (int num : nums) {
            if ((num & rightmostBit) != 0) {
                num1 ^= num;
            } else {
                num2 ^= num;
            }
        }

        return new int[]{num1, num2};
    }

    /**
     * Find the element that appears once in a sorted array where others appear k times
     */
    public int singleElementKTimes(int[] nums, int k) {
        int result = 0;

        // Count bits at each position
        for (int bit = 0; bit < 32; bit++) {
            int count = 0;
            for (int num : nums) {
                if (((num >> bit) & 1) == 1) {
                    count++;
                }
            }

            // If count is not divisible by k, this bit is set in result
            if (count % k != 0) {
                result |= (1 << bit);
            }
        }

        return result;
    }
}

/**
 * Test cases
 */
class SingleElementInSortedArrayTest {
    public static void main(String[] args) {
        testBasicCases();
        testEdgeCases();
        testVariations();
    }

    private static void testBasicCases() {
        System.out.println("=== Testing LeetCode 540: Single Element in Sorted Array ===\n");
        SingleElementInSortedArray solution = new SingleElementInSortedArray();

        // Test 1
        int[] nums1 = {1, 1, 2, 3, 3, 4, 4, 8, 8};
        int result1a = solution.singleNonDuplicate(nums1);
        int result1b = solution.singleNonDuplicateXOR(nums1);
        int result1c = solution.singleNonDuplicateBitwise(nums1);
        System.out.println("Test 1: [1,1,2,3,3,4,4,8,8]");
        System.out.println("  Even Indices: " + result1a + " (Expected: 2) - " + (result1a == 2 ? "PASS" : "FAIL"));
        System.out.println("  XOR Check: " + result1b + " (Expected: 2) - " + (result1b == 2 ? "PASS" : "FAIL"));
        System.out.println("  Bitwise: " + result1c + " (Expected: 2) - " + (result1c == 2 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 2
        int[] nums2 = {3, 3, 7, 7, 10, 11, 11};
        int result2a = solution.singleNonDuplicate(nums2);
        int result2b = solution.singleNonDuplicateXOR(nums2);
        int result2c = solution.singleNonDuplicateBitwise(nums2);
        System.out.println("Test 2: [3,3,7,7,10,11,11]");
        System.out.println("  Even Indices: " + result2a + " (Expected: 10) - " + (result2a == 10 ? "PASS" : "FAIL"));
        System.out.println("  XOR Check: " + result2b + " (Expected: 10) - " + (result2b == 10 ? "PASS" : "FAIL"));
        System.out.println("  Bitwise: " + result2c + " (Expected: 10) - " + (result2c == 10 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 3: Single element at beginning
        int[] nums3 = {1, 2, 2, 3, 3, 4, 4};
        int result3 = solution.singleNonDuplicate(nums3);
        System.out.println("Test 3: [1,2,2,3,3,4,4] - single at beginning");
        System.out.println("  Result: " + result3 + " (Expected: 1) - " + (result3 == 1 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 4: Single element at end
        int[] nums4 = {1, 1, 2, 2, 3, 3, 4};
        int result4 = solution.singleNonDuplicate(nums4);
        System.out.println("Test 4: [1,1,2,2,3,3,4] - single at end");
        System.out.println("  Result: " + result4 + " (Expected: 4) - " + (result4 == 4 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 5: Single element only
        int[] nums5 = {1};
        int result5 = solution.singleNonDuplicate(nums5);
        System.out.println("Test 5: [1] - single element only");
        System.out.println("  Result: " + result5 + " (Expected: 1) - " + (result5 == 1 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 6: Linear approaches (for comparison)
        int result6a = solution.singleNonDuplicateXORAll(nums1);
        int result6b = solution.singleNonDuplicateLinear(nums1);
        System.out.println("Test 6: Linear approaches [1,1,2,3,3,4,4,8,8]");
        System.out.println("  XOR All: " + result6a + " (Expected: 2) - " + (result6a == 2 ? "PASS" : "FAIL"));
        System.out.println("  Linear Scan: " + result6b + " (Expected: 2) - " + (result6b == 2 ? "PASS" : "FAIL"));
        System.out.println();
    }

    private static void testEdgeCases() {
        System.out.println("=== Testing Edge Cases ===\n");
        SingleElementInSortedArray solution = new SingleElementInSortedArray();

        // Test 1: Three elements
        int[] nums1 = {1, 1, 2};
        int result1 = solution.singleNonDuplicate(nums1);
        System.out.println("Test 1: [1,1,2]");
        System.out.println("  Result: " + result1 + " (Expected: 2) - " + (result1 == 2 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 2: Three elements, single at beginning
        int[] nums2 = {1, 2, 2};
        int result2 = solution.singleNonDuplicate(nums2);
        System.out.println("Test 2: [1,2,2]");
        System.out.println("  Result: " + result2 + " (Expected: 1) - " + (result2 == 1 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 3: Single in middle
        int[] nums3 = {1, 1, 2, 2, 3, 4, 4, 5, 5};
        int result3 = solution.singleNonDuplicate(nums3);
        System.out.println("Test 3: [1,1,2,2,3,4,4,5,5]");
        System.out.println("  Result: " + result3 + " (Expected: 3) - " + (result3 == 3 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 4: Large numbers
        int[] nums4 = {100000, 100000, 200000, 300000, 300000};
        int result4 = solution.singleNonDuplicate(nums4);
        System.out.println("Test 4: Large numbers [100000,100000,200000,300000,300000]");
        System.out.println("  Result: " + result4 + " (Expected: 200000) - " + (result4 == 200000 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 5: All zeros except one
        int[] nums5 = {0, 0, 0, 0, 1};
        int result5 = solution.singleNonDuplicate(nums5);
        System.out.println("Test 5: [0,0,0,0,1]");
        System.out.println("  Result: " + result5 + " (Expected: 1) - " + (result5 == 1 ? "PASS" : "FAIL"));
        System.out.println();
    }

    private static void testVariations() {
        System.out.println("=== Testing Variations ===\n");
        SingleElementVariations variations = new SingleElementVariations();

        // Test 1: Single number (all others appear three times)
        int[] nums1 = {2, 2, 3, 2, 3, 3, 5};
        int result1 = variations.singleNumberThrice(nums1);
        System.out.println("Test 1: Single number (others appear 3x) [2,2,3,2,3,3,5]");
        System.out.println("  Result: " + result1 + " (Expected: 5) - " + (result1 == 5 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 2: Single number unsorted
        int[] nums2 = {4, 1, 2, 1, 2};
        int result2 = variations.singleNumberUnsorted(nums2);
        System.out.println("Test 2: Single number unsorted [4,1,2,1,2]");
        System.out.println("  Result: " + result2 + " (Expected: 4) - " + (result2 == 4 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 3: Two single numbers
        int[] nums3 = {1, 2, 1, 3, 2, 5};
        int[] result3 = variations.singleNumberTwo(nums3);
        Arrays.sort(result3);
        System.out.println("Test 3: Two single numbers [1,2,1,3,2,5]");
        System.out.println("  Result: " + Arrays.toString(result3) + " (Expected: [3,5]) - " +
                          (Arrays.equals(result3, new int[]{3, 5}) ? "PASS" : "FAIL"));
        System.out.println();

        // Test 4: Single element (others appear k times)
        int[] nums4 = {1, 1, 1, 2, 2, 2, 3};
        int result4 = variations.singleElementKTimes(nums4, 3);
        System.out.println("Test 4: Single element (others appear 3x) [1,1,1,2,2,2,3]");
        System.out.println("  Result: " + result4 + " (Expected: 3) - " + (result4 == 3 ? "PASS" : "FAIL"));
        System.out.println();
    }
}
