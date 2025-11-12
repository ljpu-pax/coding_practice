package nvda;

import java.util.*;

/**
 * LeetCode 239: Sliding Window Maximum
 * 
 * Problem: You are given an array of integers nums, there is a sliding window of size k 
 * which is moving from the very left of the array to the very right. You can only see the k 
 * numbers in the window. Each time the sliding window moves right by one position.
 * Return the max sliding window.
 * 
 * Example 1:
 * Input: nums = [1,3,-1,-3,5,3,6,7], k = 3
 * Output: [3,3,5,5,6,7]
 * Explanation: 
 * Window position                Max
 * ---------------               -----
 * [1  3  -1] -3  5  3  6  7       3
 *  1 [3  -1  -3] 5  3  6  7       3
 *  1  3 [-1  -3  5] 3  6  7       5
 *  1  3  -1 [-3  5  3] 6  7       5
 *  1  3  -1  -3 [5  3  6] 7       6
 *  1  3  -1  -3  5 [3  6  7]      7
 * 
 * Example 2:
 * Input: nums = [1], k = 1
 * Output: [1]
 * 
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - -10^4 <= nums[i] <= 10^4
 * - 1 <= k <= nums.length
 */
public class SlidingWindowMaximum {
    
    /**
     * Approach 1: Deque (Optimal Solution)
     * 
     * Key Insight: Use a deque to maintain indices of elements in decreasing order of their values.
     * The front of deque always contains the index of maximum element in current window.
     * 
     * Algorithm:
     * 1. Use deque to store indices (not values)
     * 2. Remove indices that are out of current window from front
     * 3. Remove indices from back whose values are smaller than current element
     * 4. Add current index to back
     * 5. The front of deque gives us the maximum for current window
     * 
     * Time Complexity: O(n) - each element is added and removed at most once
     * Space Complexity: O(k) - deque stores at most k elements
     */
    public int[] maxSlidingWindow(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k <= 0) {
            return new int[0];
        }
        
        int n = nums.length;
        int[] result = new int[n - k + 1];
        Deque<Integer> deque = new LinkedList<>(); // stores indices
        
        for (int i = 0; i < n; i++) {
            // Remove indices that are out of current window
            while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
                deque.pollFirst();
            }
            
            // Remove indices from back whose values are smaller than current element
            // This maintains decreasing order in deque
            while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
                deque.pollLast();
            }
            
            // Add current index
            deque.offerLast(i);
            
            // Add maximum to result once we have processed first window
            if (i >= k - 1) {
                result[i - k + 1] = nums[deque.peekFirst()];
            }
        }
        
        return result;
    }
    
    /**
     * Approach 2: Brute Force (For comparison)
     * 
     * For each window, find the maximum element by scanning all k elements.
     * 
     * Time Complexity: O(n * k)
     * Space Complexity: O(1) excluding result array
     */
    public int[] maxSlidingWindowBruteForce(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k <= 0) {
            return new int[0];
        }
        
        int n = nums.length;
        int[] result = new int[n - k + 1];
        
        for (int i = 0; i <= n - k; i++) {
            int max = nums[i];
            for (int j = i + 1; j < i + k; j++) {
                max = Math.max(max, nums[j]);
            }
            result[i] = max;
        }
        
        return result;
    }
    
    /**
     * Approach 3: Using Priority Queue (Max Heap)
     * 
     * Use a max heap to keep track of elements in current window.
     * Need to handle removal of elements that are no longer in window.
     * 
     * Time Complexity: O(n * log k)
     * Space Complexity: O(k)
     */
    public int[] maxSlidingWindowPriorityQueue(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k <= 0) {
            return new int[0];
        }
        
        int n = nums.length;
        int[] result = new int[n - k + 1];
        
        // Max heap storing [value, index] pairs
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>((a, b) -> {
            if (a[0] != b[0]) return b[0] - a[0]; // Compare by value (descending)
            return b[1] - a[1]; // If values equal, compare by index (descending)
        });
        
        // Add first k elements
        for (int i = 0; i < k; i++) {
            maxHeap.offer(new int[]{nums[i], i});
        }
        result[0] = maxHeap.peek()[0];
        
        // Process remaining elements
        for (int i = k; i < n; i++) {
            maxHeap.offer(new int[]{nums[i], i});
            
            // Remove elements that are out of current window
            while (!maxHeap.isEmpty() && maxHeap.peek()[1] <= i - k) {
                maxHeap.poll();
            }
            
            result[i - k + 1] = maxHeap.peek()[0];
        }
        
        return result;
    }
    
    /**
     * Approach 4: Dynamic Programming with Sparse Table (Advanced)
     * 
     * Preprocess the array to answer range maximum queries in O(1) time.
     * This approach is overkill for this problem but demonstrates the concept.
     * 
     * Time Complexity: O(n log n) preprocessing + O(n) query = O(n log n)
     * Space Complexity: O(n log n)
     */
    public int[] maxSlidingWindowSparseTable(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k <= 0) {
            return new int[0];
        }
        
        int n = nums.length;
        int[] result = new int[n - k + 1];
        
        // Build sparse table
        int logN = (int) Math.floor(Math.log(n) / Math.log(2)) + 1;
        int[][] st = new int[n][logN];
        
        // Initialize for intervals of length 1
        for (int i = 0; i < n; i++) {
            st[i][0] = nums[i];
        }
        
        // Build sparse table
        for (int j = 1; j < logN; j++) {
            for (int i = 0; i + (1 << j) <= n; i++) {
                st[i][j] = Math.max(st[i][j-1], st[i + (1 << (j-1))][j-1]);
            }
        }
        
        // Answer queries
        int logK = (int) Math.floor(Math.log(k) / Math.log(2));
        for (int i = 0; i <= n - k; i++) {
            int j = i + k - 1;
            result[i] = Math.max(st[i][logK], st[j - (1 << logK) + 1][logK]);
        }
        
        return result;
    }
    
    /**
     * Helper method to print array
     */
    private static void printArray(int[] arr) {
        System.out.print("[");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
            if (i < arr.length - 1) System.out.print(",");
        }
        System.out.println("]");
    }
    
    /**
     * Helper method to compare two arrays
     */
    private static boolean arraysEqual(int[] a, int[] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) return false;
        }
        return true;
    }
    
    // Test cases
    public static void main(String[] args) {
        SlidingWindowMaximum solution = new SlidingWindowMaximum();
        
        // Test Case 1: Basic example
        int[] nums1 = {1,3,-1,-3,5,3,6,7};
        int k1 = 3;
        int[] expected1 = {3,3,5,5,6,7};
        int[] result1 = solution.maxSlidingWindow(nums1, k1);
        System.out.println("Test 1 - Input: [1,3,-1,-3,5,3,6,7], k=3");
        System.out.print("Expected: ");
        printArray(expected1);
        System.out.print("Got: ");
        printArray(result1);
        System.out.println("Pass: " + arraysEqual(result1, expected1));
        System.out.println();
        
        // Test Case 2: Single element
        int[] nums2 = {1};
        int k2 = 1;
        int[] expected2 = {1};
        int[] result2 = solution.maxSlidingWindow(nums2, k2);
        System.out.println("Test 2 - Input: [1], k=1");
        System.out.print("Expected: ");
        printArray(expected2);
        System.out.print("Got: ");
        printArray(result2);
        System.out.println("Pass: " + arraysEqual(result2, expected2));
        System.out.println();
        
        // Test Case 3: Window size equals array length
        int[] nums3 = {1,3,2,5,4};
        int k3 = 5;
        int[] expected3 = {5};
        int[] result3 = solution.maxSlidingWindow(nums3, k3);
        System.out.println("Test 3 - Input: [1,3,2,5,4], k=5");
        System.out.print("Expected: ");
        printArray(expected3);
        System.out.print("Got: ");
        printArray(result3);
        System.out.println("Pass: " + arraysEqual(result3, expected3));
        System.out.println();
        
        // Test Case 4: Decreasing array
        int[] nums4 = {7,6,5,4,3,2,1};
        int k4 = 3;
        int[] expected4 = {7,6,5,4,3};
        int[] result4 = solution.maxSlidingWindow(nums4, k4);
        System.out.println("Test 4 - Input: [7,6,5,4,3,2,1], k=3");
        System.out.print("Expected: ");
        printArray(expected4);
        System.out.print("Got: ");
        printArray(result4);
        System.out.println("Pass: " + arraysEqual(result4, expected4));
        System.out.println();
        
        // Test Case 5: Increasing array
        int[] nums5 = {1,2,3,4,5,6,7};
        int k5 = 3;
        int[] expected5 = {3,4,5,6,7};
        int[] result5 = solution.maxSlidingWindow(nums5, k5);
        System.out.println("Test 5 - Input: [1,2,3,4,5,6,7], k=3");
        System.out.print("Expected: ");
        printArray(expected5);
        System.out.print("Got: ");
        printArray(result5);
        System.out.println("Pass: " + arraysEqual(result5, expected5));
        System.out.println();
        
        // Test Case 6: Negative numbers
        int[] nums6 = {-1,-3,-2,-5,-4};
        int k6 = 2;
        int[] expected6 = {-1,-2,-2,-4};
        int[] result6 = solution.maxSlidingWindow(nums6, k6);
        System.out.println("Test 6 - Input: [-1,-3,-2,-5,-4], k=2");
        System.out.print("Expected: ");
        printArray(expected6);
        System.out.print("Got: ");
        printArray(result6);
        System.out.println("Pass: " + arraysEqual(result6, expected6));
        System.out.println();
        
        // Performance comparison
        System.out.println("Performance Comparison:");
        int[] largeArray = new int[1000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = (int) (Math.random() * 1000);
        }
        
        long start, end;
        
        // Deque approach
        start = System.nanoTime();
        int[] dequeResult = solution.maxSlidingWindow(largeArray.clone(), 100);
        end = System.nanoTime();
        System.out.println("Deque approach: " + (end - start) / 1000000.0 + " ms");
        
        // Priority Queue approach
        start = System.nanoTime();
        int[] pqResult = solution.maxSlidingWindowPriorityQueue(largeArray.clone(), 100);
        end = System.nanoTime();
        System.out.println("Priority Queue approach: " + (end - start) / 1000000.0 + " ms");
        
        // Verify both approaches give same result
        System.out.println("Both approaches give same result: " + arraysEqual(dequeResult, pqResult));
    }
}
