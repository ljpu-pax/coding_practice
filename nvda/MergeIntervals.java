package nvda;

import java.util.*;

/**
 * LeetCode 56: Merge Intervals
 * 
 * Problem: Given an array of intervals where intervals[i] = [starti, endi], 
 * merge all overlapping intervals, and return an array of the non-overlapping 
 * intervals that cover all the intervals in the input.
 * 
 * Example 1:
 * Input: intervals = [[1,3],[2,6],[8,10],[15,18]]
 * Output: [[1,6],[8,10],[15,18]]
 * Explanation: Since intervals [1,3] and [2,6] overlap, merge them into [1,6].
 * 
 * Example 2:
 * Input: intervals = [[1,4],[4,5]]
 * Output: [[1,5]]
 * Explanation: Intervals [1,4] and [4,5] are considered overlapping.
 * 
 * Constraints:
 * - 1 <= intervals.length <= 10^4
 * - intervals[i].length == 2
 * - 0 <= starti <= endi <= 10^4
 */
public class MergeIntervals {
    
    /**
     * Approach 1: Sort and Merge (Most Optimal)
     * 
     * Algorithm:
     * 1. Sort intervals by start time
     * 2. Iterate through sorted intervals
     * 3. If current interval overlaps with the last merged interval, merge them
     * 4. Otherwise, add current interval to result
     * 
     * Time Complexity: O(n log n) due to sorting
     * Space Complexity: O(n) for the result list
     */
    public int[][] merge(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return intervals;
        }
        
        // Sort intervals by start time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        
        List<int[]> merged = new ArrayList<>();
        int[] currentInterval = intervals[0];
        merged.add(currentInterval);
        
        for (int i = 1; i < intervals.length; i++) {
            int[] nextInterval = intervals[i];
            
            // Check if current interval overlaps with the last merged interval
            if (currentInterval[1] >= nextInterval[0]) {
                // Merge intervals by extending the end time
                currentInterval[1] = Math.max(currentInterval[1], nextInterval[1]);
            } else {
                // No overlap, add the next interval to merged list
                currentInterval = nextInterval;
                merged.add(currentInterval);
            }
        }
        
        return merged.toArray(new int[merged.size()][]);
    }
    
    /**
     * Approach 2: Using LinkedList for better insertion performance
     * 
     * This approach is similar but uses LinkedList which can be more efficient
     * for certain operations, though the overall complexity remains the same.
     * 
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     */
    public int[][] mergeWithLinkedList(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return intervals;
        }
        
        // Sort intervals by start time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        
        LinkedList<int[]> merged = new LinkedList<>();
        
        for (int[] interval : intervals) {
            // If merged is empty or no overlap with the last interval
            if (merged.isEmpty() || merged.getLast()[1] < interval[0]) {
                merged.add(interval);
            } else {
                // Overlap exists, merge by updating the end time
                merged.getLast()[1] = Math.max(merged.getLast()[1], interval[1]);
            }
        }
        
        return merged.toArray(new int[merged.size()][]);
    }
    
    /**
     * Approach 3: In-place modification (Space optimized)
     * 
     * This approach modifies the input array in-place to save space,
     * though it's generally not recommended to modify input.
     * 
     * Time Complexity: O(n log n)
     * Space Complexity: O(1) if we don't count the space used by sorting
     */
    public int[][] mergeInPlace(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return intervals;
        }
        
        // Sort intervals by start time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        
        int writeIndex = 0;
        
        for (int i = 1; i < intervals.length; i++) {
            // If current interval overlaps with the interval at writeIndex
            if (intervals[writeIndex][1] >= intervals[i][0]) {
                // Merge by extending the end time
                intervals[writeIndex][1] = Math.max(intervals[writeIndex][1], intervals[i][1]);
            } else {
                // No overlap, move to next position
                writeIndex++;
                intervals[writeIndex] = intervals[i];
            }
        }
        
        // Return only the merged intervals
        return Arrays.copyOf(intervals, writeIndex + 1);
    }
    
    /**
     * Helper method to print intervals for debugging
     */
    private static void printIntervals(int[][] intervals) {
        System.out.print("[");
        for (int i = 0; i < intervals.length; i++) {
            System.out.print("[" + intervals[i][0] + "," + intervals[i][1] + "]");
            if (i < intervals.length - 1) {
                System.out.print(",");
            }
        }
        System.out.println("]");
    }
    
    /**
     * Helper method to check if two interval arrays are equal
     */
    private static boolean areIntervalsEqual(int[][] a, int[][] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (a[i][0] != b[i][0] || a[i][1] != b[i][1]) {
                return false;
            }
        }
        return true;
    }
    
    // Test cases
    public static void main(String[] args) {
        MergeIntervals solution = new MergeIntervals();
        
        // Test Case 1: Basic overlapping intervals
        int[][] intervals1 = {{1,3},{2,6},{8,10},{15,18}};
        int[][] expected1 = {{1,6},{8,10},{15,18}};
        int[][] result1 = solution.merge(intervals1);
        System.out.println("Test 1 - Input: [[1,3],[2,6],[8,10],[15,18]]");
        System.out.print("Expected: ");
        printIntervals(expected1);
        System.out.print("Got: ");
        printIntervals(result1);
        System.out.println("Pass: " + areIntervalsEqual(result1, expected1));
        System.out.println();
        
        // Test Case 2: Adjacent intervals (touching)
        int[][] intervals2 = {{1,4},{4,5}};
        int[][] expected2 = {{1,5}};
        int[][] result2 = solution.merge(intervals2);
        System.out.println("Test 2 - Input: [[1,4],[4,5]]");
        System.out.print("Expected: ");
        printIntervals(expected2);
        System.out.print("Got: ");
        printIntervals(result2);
        System.out.println("Pass: " + areIntervalsEqual(result2, expected2));
        System.out.println();
        
        // Test Case 3: Single interval
        int[][] intervals3 = {{1,4}};
        int[][] expected3 = {{1,4}};
        int[][] result3 = solution.merge(intervals3);
        System.out.println("Test 3 - Input: [[1,4]]");
        System.out.print("Expected: ");
        printIntervals(expected3);
        System.out.print("Got: ");
        printIntervals(result3);
        System.out.println("Pass: " + areIntervalsEqual(result3, expected3));
        System.out.println();
        
        // Test Case 4: No overlapping intervals
        int[][] intervals4 = {{1,2},{3,4},{5,6}};
        int[][] expected4 = {{1,2},{3,4},{5,6}};
        int[][] result4 = solution.merge(intervals4);
        System.out.println("Test 4 - Input: [[1,2],[3,4],[5,6]]");
        System.out.print("Expected: ");
        printIntervals(expected4);
        System.out.print("Got: ");
        printIntervals(result4);
        System.out.println("Pass: " + areIntervalsEqual(result4, expected4));
        System.out.println();
        
        // Test Case 5: All intervals merge into one
        int[][] intervals5 = {{1,4},{2,3}};
        int[][] expected5 = {{1,4}};
        int[][] result5 = solution.merge(intervals5);
        System.out.println("Test 5 - Input: [[1,4],[2,3]]");
        System.out.print("Expected: ");
        printIntervals(expected5);
        System.out.print("Got: ");
        printIntervals(result5);
        System.out.println("Pass: " + areIntervalsEqual(result5, expected5));
        System.out.println();
        
        // Test Case 6: Unsorted input
        int[][] intervals6 = {{2,3},{4,5},{6,7},{8,9},{1,10}};
        int[][] expected6 = {{1,10}};
        int[][] result6 = solution.merge(intervals6);
        System.out.println("Test 6 - Input: [[2,3],[4,5],[6,7],[8,9],[1,10]]");
        System.out.print("Expected: ");
        printIntervals(expected6);
        System.out.print("Got: ");
        printIntervals(result6);
        System.out.println("Pass: " + areIntervalsEqual(result6, expected6));
        System.out.println();
        
        // Test different approaches with the same input
        System.out.println("Testing different approaches:");
        int[][] testInput = {{1,3},{2,6},{8,10},{15,18}};
        
        int[][] result_approach1 = solution.merge(Arrays.copyOf(testInput, testInput.length));
        int[][] result_approach2 = solution.mergeWithLinkedList(Arrays.copyOf(testInput, testInput.length));
        int[][] result_approach3 = solution.mergeInPlace(Arrays.copyOf(testInput, testInput.length));
        
        System.out.println("All approaches give same result: " + 
            (areIntervalsEqual(result_approach1, result_approach2) && 
            areIntervalsEqual(result_approach2, result_approach3)));
    }
}
