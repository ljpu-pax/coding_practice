import java.util.*;

/**
 * LeetCode 56: Merge Intervals (Medium)
 *
 * Given an array of intervals where intervals[i] = [starti, endi], merge all overlapping intervals,
 * and return an array of the non-overlapping intervals that cover all the intervals in the input.
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
class MergeIntervals {

    /**
     * Approach 1: Sort and Merge
     *
     * Time: O(N log N) for sorting
     * Space: O(N) for result
     */
    public int[][] merge(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new int[0][0];
        }

        // Sort by start time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        List<int[]> merged = new ArrayList<>();
        int[] current = intervals[0];
        merged.add(current);

        for (int i = 1; i < intervals.length; i++) {
            int[] interval = intervals[i];

            // If overlapping, merge by extending end time
            if (interval[0] <= current[1]) {
                current[1] = Math.max(current[1], interval[1]);
            } else {
                // No overlap, add new interval
                current = interval;
                merged.add(current);
            }
        }

        return merged.toArray(new int[merged.size()][]);
    }

    /**
     * Approach 2: Using stack
     *
     * Time: O(N log N)
     * Space: O(N)
     */
    public int[][] mergeWithStack(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new int[0][0];
        }

        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        Stack<int[]> stack = new Stack<>();
        stack.push(intervals[0]);

        for (int i = 1; i < intervals.length; i++) {
            int[] top = stack.peek();
            int[] curr = intervals[i];

            if (curr[0] <= top[1]) {
                // Merge
                top[1] = Math.max(top[1], curr[1]);
            } else {
                stack.push(curr);
            }
        }

        return stack.toArray(new int[stack.size()][]);
    }

    /**
     * Approach 3: In-place merge (modifies input)
     *
     * Time: O(N log N)
     * Space: O(1) excluding sort space
     */
    public int[][] mergeInPlace(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new int[0][0];
        }

        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        int writeIdx = 0;

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] <= intervals[writeIdx][1]) {
                // Merge
                intervals[writeIdx][1] = Math.max(intervals[writeIdx][1], intervals[i][1]);
            } else {
                // Move to next position
                writeIdx++;
                intervals[writeIdx] = intervals[i];
            }
        }

        return Arrays.copyOfRange(intervals, 0, writeIdx + 1);
    }
}

/**
 * Related problem: Insert Interval (LeetCode 57)
 */
class InsertInterval {
    /**
     * Insert newInterval and merge if necessary
     */
    public int[][] insert(int[][] intervals, int[] newInterval) {
        List<int[]> result = new ArrayList<>();
        int i = 0;
        int n = intervals.length;

        // Add all intervals before newInterval
        while (i < n && intervals[i][1] < newInterval[0]) {
            result.add(intervals[i]);
            i++;
        }

        // Merge overlapping intervals
        while (i < n && intervals[i][0] <= newInterval[1]) {
            newInterval[0] = Math.min(newInterval[0], intervals[i][0]);
            newInterval[1] = Math.max(newInterval[1], intervals[i][1]);
            i++;
        }
        result.add(newInterval);

        // Add remaining intervals
        while (i < n) {
            result.add(intervals[i]);
            i++;
        }

        return result.toArray(new int[result.size()][]);
    }
}

/**
 * Related: Non-overlapping Intervals (LeetCode 435)
 */
class NonOverlappingIntervals {
    /**
     * Minimum number of intervals to remove to make rest non-overlapping
     */
    public int eraseOverlapIntervals(int[][] intervals) {
        if (intervals.length == 0) return 0;

        // Sort by end time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1]));

        int count = 0;
        int end = intervals[0][1];

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] < end) {
                // Overlap, need to remove
                count++;
            } else {
                // No overlap, update end
                end = intervals[i][1];
            }
        }

        return count;
    }
}

/**
 * Related: Meeting Rooms II (LeetCode 253)
 */
class MeetingRoomsII {
    /**
     * Minimum number of conference rooms required
     */
    public int minMeetingRooms(int[][] intervals) {
        if (intervals.length == 0) return 0;

        // Separate start and end times
        int[] starts = new int[intervals.length];
        int[] ends = new int[intervals.length];

        for (int i = 0; i < intervals.length; i++) {
            starts[i] = intervals[i][0];
            ends[i] = intervals[i][1];
        }

        Arrays.sort(starts);
        Arrays.sort(ends);

        int rooms = 0;
        int endIdx = 0;

        for (int start : starts) {
            if (start < ends[endIdx]) {
                // Need a new room
                rooms++;
            } else {
                // Reuse a room
                endIdx++;
            }
        }

        return rooms;
    }

    /**
     * Alternative: Using priority queue
     */
    public int minMeetingRoomsPQ(int[][] intervals) {
        if (intervals.length == 0) return 0;

        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        PriorityQueue<Integer> pq = new PriorityQueue<>(); // End times
        pq.offer(intervals[0][1]);

        for (int i = 1; i < intervals.length; i++) {
            // If earliest ending meeting finishes before this one starts, reuse room
            if (intervals[i][0] >= pq.peek()) {
                pq.poll();
            }
            pq.offer(intervals[i][1]);
        }

        return pq.size();
    }
}

/**
 * Test cases
 */
class MergeIntervalsTest {
    public static void main(String[] args) {
        testBasicMerge();
        testInsertInterval();
        testNonOverlapping();
        testMeetingRooms();
    }

    private static void testBasicMerge() {
        System.out.println("=== LeetCode 56: Merge Intervals ===\n");
        MergeIntervals solution = new MergeIntervals();

        // Test 1
        int[][] intervals1 = {{1,3},{2,6},{8,10},{15,18}};
        int[][] result1 = solution.merge(intervals1);
        System.out.println("Test 1: " + Arrays.deepToString(intervals1));
        System.out.println("Result: " + Arrays.deepToString(result1));
        System.out.println("Expected: [[1,6],[8,10],[15,18]]");
        System.out.println();

        // Test 2
        int[][] intervals2 = {{1,4},{4,5}};
        int[][] result2 = solution.merge(intervals2);
        System.out.println("Test 2: " + Arrays.deepToString(intervals2));
        System.out.println("Result: " + Arrays.deepToString(result2));
        System.out.println("Expected: [[1,5]]");
        System.out.println();

        // Test 3: All overlap
        int[][] intervals3 = {{1,4},{2,5},{3,6}};
        int[][] result3 = solution.merge(intervals3);
        System.out.println("Test 3 (all overlap): " + Arrays.deepToString(intervals3));
        System.out.println("Result: " + Arrays.deepToString(result3));
        System.out.println();
    }

    private static void testInsertInterval() {
        System.out.println("=== LeetCode 57: Insert Interval ===\n");
        InsertInterval solution = new InsertInterval();

        int[][] intervals = {{1,3},{6,9}};
        int[] newInterval = {2,5};
        int[][] result = solution.insert(intervals, newInterval);
        System.out.println("Intervals: " + Arrays.deepToString(intervals));
        System.out.println("Insert: " + Arrays.toString(newInterval));
        System.out.println("Result: " + Arrays.deepToString(result));
        System.out.println("Expected: [[1,5],[6,9]]");
        System.out.println();
    }

    private static void testNonOverlapping() {
        System.out.println("=== LeetCode 435: Non-overlapping Intervals ===\n");
        NonOverlappingIntervals solution = new NonOverlappingIntervals();

        int[][] intervals = {{1,2},{2,3},{3,4},{1,3}};
        int result = solution.eraseOverlapIntervals(intervals);
        System.out.println("Intervals: " + Arrays.deepToString(intervals));
        System.out.println("Min removals: " + result);
        System.out.println("Expected: 1 (remove [1,3])");
        System.out.println();
    }

    private static void testMeetingRooms() {
        System.out.println("=== LeetCode 253: Meeting Rooms II ===\n");
        MeetingRoomsII solution = new MeetingRoomsII();

        int[][] intervals = {{0,30},{5,10},{15,20}};
        int result1 = solution.minMeetingRooms(intervals);
        int result2 = solution.minMeetingRoomsPQ(intervals);
        System.out.println("Meetings: " + Arrays.deepToString(intervals));
        System.out.println("Min rooms (array): " + result1);
        System.out.println("Min rooms (PQ): " + result2);
        System.out.println("Expected: 2");
        System.out.println();
    }
}
