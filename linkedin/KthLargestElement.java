// LeetCode 215: Kth Largest Element in an Array
// https://leetcode.com/problems/kth-largest-element-in-an-array/
// Difficulty: Medium

// Given an integer array nums and an integer k, return the kth largest element in the array.
// Note that it is the kth largest element in the sorted order, not the kth distinct element.

// Can you solve it without sorting?

// Example 1:
// Input: nums = [3,2,1,5,6,4], k = 2
// Output: 5

// Example 2:
// Input: nums = [3,2,3,1,2,4,5,5,6], k = 4
// Output: 4

import java.util.*;

class KthLargestElement {
    // Approach 1: Min-Heap (Priority Queue)
    // Time: O(n log k), Space: O(k)
    public int findKthLargest(int[] nums, int k) {
        // Min heap of size k
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        for (int num : nums) {
            minHeap.offer(num);

            // Keep heap size at k by removing smallest elements
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        // The root of min heap is the kth largest element
        return minHeap.peek();
    }

    // Approach 2: Max-Heap
    // Time: O(n + k log n), Space: O(n)
    public int findKthLargestMaxHeap(int[] nums, int k) {
        // Max heap
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);

        // Add all elements to max heap
        for (int num : nums) {
            maxHeap.offer(num);
        }

        // Remove k-1 largest elements
        for (int i = 0; i < k - 1; i++) {
            maxHeap.poll();
        }

        // The kth largest element is now at the top
        return maxHeap.peek();
    }

    // Approach 3: QuickSelect (Partition-based)
    // Average Time: O(n), Worst: O(n^2), Space: O(1)
    public int findKthLargestQuickSelect(int[] nums, int k) {
        // Convert to finding (n - k)th smallest element (0-indexed)
        return quickSelect(nums, 0, nums.length - 1, nums.length - k);
    }

    private int quickSelect(int[] nums, int left, int right, int kSmallest) {
        if (left == right) {
            return nums[left];
        }

        // Random pivot for better average performance
        Random random = new Random();
        int pivotIndex = left + random.nextInt(right - left + 1);

        pivotIndex = partition(nums, left, right, pivotIndex);

        if (kSmallest == pivotIndex) {
            return nums[kSmallest];
        } else if (kSmallest < pivotIndex) {
            return quickSelect(nums, left, pivotIndex - 1, kSmallest);
        } else {
            return quickSelect(nums, pivotIndex + 1, right, kSmallest);
        }
    }

    private int partition(int[] nums, int left, int right, int pivotIndex) {
        int pivotValue = nums[pivotIndex];

        // Move pivot to end
        swap(nums, pivotIndex, right);

        int storeIndex = left;

        // Move all smaller elements to the left
        for (int i = left; i < right; i++) {
            if (nums[i] < pivotValue) {
                swap(nums, storeIndex, i);
                storeIndex++;
            }
        }

        // Move pivot to its final place
        swap(nums, right, storeIndex);

        return storeIndex;
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    // Approach 4: Sorting (simple but not optimal)
    // Time: O(n log n), Space: O(1) or O(n) depending on sort implementation
    public int findKthLargestSort(int[] nums, int k) {
        Arrays.sort(nums);
        return nums[nums.length - k];
    }

    public static void main(String[] args) {
        KthLargestElement solution = new KthLargestElement();

        // Test case 1
        int[] nums1 = {3, 2, 1, 5, 6, 4};
        System.out.println("Test 1 - Min Heap: " + solution.findKthLargest(nums1, 2)); // 5

        // Test case 2
        int[] nums2 = {3, 2, 3, 1, 2, 4, 5, 5, 6};
        System.out.println("Test 2 - Min Heap: " + solution.findKthLargest(nums2, 4)); // 4

        // Test Max Heap approach
        int[] nums3 = {3, 2, 1, 5, 6, 4};
        System.out.println("Test 3 - Max Heap: " + solution.findKthLargestMaxHeap(nums3, 2)); // 5

        // Test QuickSelect approach
        int[] nums4 = {3, 2, 1, 5, 6, 4};
        System.out.println("Test 4 - QuickSelect: " + solution.findKthLargestQuickSelect(nums4, 2)); // 5

        // Test sorting approach
        int[] nums5 = {3, 2, 1, 5, 6, 4};
        System.out.println("Test 5 - Sorting: " + solution.findKthLargestSort(nums5, 2)); // 5

        // Edge case: k = 1 (largest element)
        int[] nums6 = {7, 3, 9, 1};
        System.out.println("Test 6 - k=1: " + solution.findKthLargest(nums6, 1)); // 9

        // Edge case: k = n (smallest element)
        int[] nums7 = {7, 3, 9, 1};
        System.out.println("Test 7 - k=n: " + solution.findKthLargest(nums7, 4)); // 1
    }
}

/*
 * Key Insights:
 *
 * Approach Comparison:
 *
 * 1. Min-Heap (Priority Queue): O(n log k) time, O(k) space
 *    - Best for small k relative to n
 *    - Maintains only k elements in memory
 *    - Stable performance
 *
 * 2. Max-Heap: O(n + k log n) time, O(n) space
 *    - Good when k is large
 *    - Uses more memory
 *
 * 3. QuickSelect: O(n) average, O(n^2) worst, O(1) space
 *    - Best average case performance
 *    - In-place algorithm
 *    - Similar to QuickSort partitioning
 *    - Can modify the input array
 *
 * 4. Sorting: O(n log n) time
 *    - Simple but not optimal
 *    - Good for small arrays or when array needs to be sorted anyway
 *
 * Interview Tips:
 * - Start with min-heap approach (most common solution)
 * - Mention QuickSelect as the optimal solution
 * - Discuss trade-offs between approaches
 * - Ask if modifying the input array is allowed
 */
