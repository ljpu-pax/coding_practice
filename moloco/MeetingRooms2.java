import java.util.*;

/**
 * LeetCode 253: Meeting Rooms II
 *
 * Given an array of meeting time intervals consisting of start and end times [[s1,e1],[s2,e2],...]
 * (si < ei), find the minimum number of conference rooms required.
 *
 * Example 1:
 * Input: intervals = [[0,30],[5,10],[15,20]]
 * Output: 2
 * Explanation: We need two meeting rooms:
 * - Room 1: [0,30]
 * - Room 2: [5,10], [15,20]
 *
 * Example 2:
 * Input: intervals = [[7,10],[2,4]]
 * Output: 1
 * Explanation: Only one room is needed since meetings don't overlap.
 *
 * Example 3:
 * Input: intervals = [[1,5],[8,9],[8,9]]
 * Output: 2
 * Explanation: At time 8, two meetings start simultaneously.
 *
 * Constraints:
 * - 1 <= intervals.length <= 10^4
 * - 0 <= starti < endi <= 10^6
 */

/**
 * Definition for an interval.
 */
class Interval {
    int start;
    int end;

    Interval(int start, int end) {
        this.start = start;
        this.end = end;
    }
}

class MeetingRooms2 {
    /**
     * Approach 1: Min Heap (Priority Queue)
     *
     * Sort meetings by start time. For each meeting, check if earliest ending meeting
     * has finished. If yes, reuse that room; otherwise, allocate new room.
     *
     * Time: O(n log n) - sorting + heap operations
     * Space: O(n) - heap size
     */
    public int minMeetingRooms(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        // Sort by start time
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

        // Min heap to track end times of ongoing meetings
        PriorityQueue<Integer> heap = new PriorityQueue<>();

        for (int[] interval : intervals) {
            // If earliest meeting has ended, remove it (reuse room)
            if (!heap.isEmpty() && heap.peek() <= interval[0]) {
                heap.poll();
            }

            // Add current meeting's end time
            heap.offer(interval[1]);
        }

        // Heap size is the number of rooms needed
        return heap.size();
    }

    /**
     * Approach 2: Chronological Ordering (Event Sweep)
     *
     * Create separate arrays for start and end times, sort them.
     * Sweep through time: increment room count for starts, decrement for ends.
     *
     * Time: O(n log n)
     * Space: O(n)
     */
    public int minMeetingRoomsChronological(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        int n = intervals.length;
        int[] starts = new int[n];
        int[] ends = new int[n];

        for (int i = 0; i < n; i++) {
            starts[i] = intervals[i][0];
            ends[i] = intervals[i][1];
        }

        Arrays.sort(starts);
        Arrays.sort(ends);

        int rooms = 0;
        int maxRooms = 0;
        int startPtr = 0, endPtr = 0;

        while (startPtr < n) {
            // If a meeting starts before the earliest meeting ends, need a new room
            if (starts[startPtr] < ends[endPtr]) {
                rooms++;
                startPtr++;
            } else {
                // A meeting ended, room becomes free
                rooms--;
                endPtr++;
            }

            maxRooms = Math.max(maxRooms, rooms);
        }

        return maxRooms;
    }

    /**
     * Approach 3: TreeMap (Event Points)
     *
     * Use TreeMap to track events at each time point.
     * For each time, count net change in room usage.
     *
     * Time: O(n log n)
     * Space: O(n)
     */
    public int minMeetingRoomsTreeMap(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        TreeMap<Integer, Integer> events = new TreeMap<>();

        // Add events: +1 for start, -1 for end
        for (int[] interval : intervals) {
            events.put(interval[0], events.getOrDefault(interval[0], 0) + 1);
            events.put(interval[1], events.getOrDefault(interval[1], 0) - 1);
        }

        int currentRooms = 0;
        int maxRooms = 0;

        for (int delta : events.values()) {
            currentRooms += delta;
            maxRooms = Math.max(maxRooms, currentRooms);
        }

        return maxRooms;
    }

    /**
     * Approach 4: Line Sweep with Events List
     *
     * Create events for start (+1) and end (-1), sort by time.
     * Ties: end events before start events (same time).
     *
     * Time: O(n log n)
     * Space: O(n)
     */
    public int minMeetingRoomsLineSweep(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        List<int[]> events = new ArrayList<>();

        for (int[] interval : intervals) {
            events.add(new int[]{interval[0], 1});  // start event
            events.add(new int[]{interval[1], -1}); // end event
        }

        // Sort by time, end events before start events at same time
        events.sort((a, b) -> a[0] == b[0] ? a[1] - b[1] : a[0] - b[0]);

        int currentRooms = 0;
        int maxRooms = 0;

        for (int[] event : events) {
            currentRooms += event[1];
            maxRooms = Math.max(maxRooms, currentRooms);
        }

        return maxRooms;
    }

    /**
     * Approach 5: Using Interval objects
     *
     * Same logic as approach 1 but with Interval class.
     */
    public int minMeetingRoomsWithIntervalClass(Interval[] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        Arrays.sort(intervals, (a, b) -> a.start - b.start);

        PriorityQueue<Integer> heap = new PriorityQueue<>();

        for (Interval interval : intervals) {
            if (!heap.isEmpty() && heap.peek() <= interval.start) {
                heap.poll();
            }
            heap.offer(interval.end);
        }

        return heap.size();
    }
}

/**
 * Related problems and variations
 */
class MeetingRoomsVariations {
    /**
     * LeetCode 252: Meeting Rooms I
     *
     * Determine if a person could attend all meetings.
     */
    public boolean canAttendMeetings(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return true;
        }

        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] < intervals[i - 1][1]) {
                return false; // Overlap found
            }
        }

        return true;
    }

    /**
     * Return the actual room assignments
     * Returns: List of lists where each inner list contains indices of meetings in that room
     */
    public List<List<Integer>> assignMeetingRooms(int[][] intervals) {
        List<List<Integer>> rooms = new ArrayList<>();
        if (intervals == null || intervals.length == 0) {
            return rooms;
        }

        // Create indexed intervals
        int[][] indexed = new int[intervals.length][3];
        for (int i = 0; i < intervals.length; i++) {
            indexed[i] = new int[]{intervals[i][0], intervals[i][1], i};
        }

        Arrays.sort(indexed, (a, b) -> a[0] - b[0]);

        // Min heap: {end_time, room_index}
        PriorityQueue<int[]> heap = new PriorityQueue<>((a, b) -> a[0] - b[0]);

        for (int[] meeting : indexed) {
            if (!heap.isEmpty() && heap.peek()[0] <= meeting[0]) {
                // Reuse room
                int[] room = heap.poll();
                int roomIndex = room[1];
                rooms.get(roomIndex).add(meeting[2]);
                heap.offer(new int[]{meeting[1], roomIndex});
            } else {
                // New room needed
                int newRoomIndex = rooms.size();
                List<Integer> newRoom = new ArrayList<>();
                newRoom.add(meeting[2]);
                rooms.add(newRoom);
                heap.offer(new int[]{meeting[1], newRoomIndex});
            }
        }

        return rooms;
    }

    /**
     * Find free time intervals given meeting schedules
     */
    public List<int[]> findFreeTime(int[][] intervals, int dayStart, int dayEnd) {
        List<int[]> freeTime = new ArrayList<>();
        if (intervals == null || intervals.length == 0) {
            freeTime.add(new int[]{dayStart, dayEnd});
            return freeTime;
        }

        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

        int currentTime = dayStart;

        for (int[] interval : intervals) {
            if (interval[0] > currentTime) {
                freeTime.add(new int[]{currentTime, interval[0]});
            }
            currentTime = Math.max(currentTime, interval[1]);
        }

        if (currentTime < dayEnd) {
            freeTime.add(new int[]{currentTime, dayEnd});
        }

        return freeTime;
    }

    /**
     * Find the time when maximum number of meetings are happening
     */
    public int[] findPeakMeetingTime(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new int[]{-1, 0};
        }

        TreeMap<Integer, Integer> events = new TreeMap<>();

        for (int[] interval : intervals) {
            events.put(interval[0], events.getOrDefault(interval[0], 0) + 1);
            events.put(interval[1], events.getOrDefault(interval[1], 0) - 1);
        }

        int maxMeetings = 0;
        int peakTime = -1;
        int currentMeetings = 0;

        for (Map.Entry<Integer, Integer> entry : events.entrySet()) {
            currentMeetings += entry.getValue();
            if (currentMeetings > maxMeetings) {
                maxMeetings = currentMeetings;
                peakTime = entry.getKey();
            }
        }

        return new int[]{peakTime, maxMeetings};
    }

    /**
     * Merge all overlapping intervals
     */
    public List<int[]> mergeIntervals(int[][] intervals) {
        List<int[]> merged = new ArrayList<>();
        if (intervals == null || intervals.length == 0) {
            return merged;
        }

        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

        int[] current = intervals[0];

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] <= current[1]) {
                // Merge
                current[1] = Math.max(current[1], intervals[i][1]);
            } else {
                // No overlap
                merged.add(current);
                current = intervals[i];
            }
        }

        merged.add(current);
        return merged;
    }
}

/**
 * Test cases
 */
class MeetingRooms2Test {
    public static void main(String[] args) {
        testBasicCases();
        testEdgeCases();
        testVariations();
    }

    private static void testBasicCases() {
        System.out.println("=== Testing LeetCode 253: Meeting Rooms II ===\n");
        MeetingRooms2 solution = new MeetingRooms2();

        // Test 1
        int[][] intervals1 = {{0, 30}, {5, 10}, {15, 20}};
        int result1a = solution.minMeetingRooms(intervals1);
        int result1b = solution.minMeetingRoomsChronological(intervals1);
        int result1c = solution.minMeetingRoomsTreeMap(intervals1);
        int result1d = solution.minMeetingRoomsLineSweep(intervals1);
        System.out.println("Test 1: [[0,30],[5,10],[15,20]]");
        System.out.println("  Min Heap: " + result1a + " (Expected: 2) - " + (result1a == 2 ? "PASS" : "FAIL"));
        System.out.println("  Chronological: " + result1b + " (Expected: 2) - " + (result1b == 2 ? "PASS" : "FAIL"));
        System.out.println("  TreeMap: " + result1c + " (Expected: 2) - " + (result1c == 2 ? "PASS" : "FAIL"));
        System.out.println("  Line Sweep: " + result1d + " (Expected: 2) - " + (result1d == 2 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 2
        int[][] intervals2 = {{7, 10}, {2, 4}};
        int result2 = solution.minMeetingRooms(intervals2);
        System.out.println("Test 2: [[7,10],[2,4]]");
        System.out.println("  Result: " + result2 + " (Expected: 1) - " + (result2 == 1 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 3: Simultaneous meetings
        int[][] intervals3 = {{1, 5}, {8, 9}, {8, 9}};
        int result3 = solution.minMeetingRooms(intervals3);
        System.out.println("Test 3: [[1,5],[8,9],[8,9]]");
        System.out.println("  Result: " + result3 + " (Expected: 2) - " + (result3 == 2 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 4: All overlapping
        int[][] intervals4 = {{1, 10}, {2, 11}, {3, 12}, {4, 13}};
        int result4 = solution.minMeetingRooms(intervals4);
        System.out.println("Test 4: All overlapping [[1,10],[2,11],[3,12],[4,13]]");
        System.out.println("  Result: " + result4 + " (Expected: 4) - " + (result4 == 4 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 5: No overlaps
        int[][] intervals5 = {{1, 2}, {3, 4}, {5, 6}, {7, 8}};
        int result5 = solution.minMeetingRooms(intervals5);
        System.out.println("Test 5: No overlaps [[1,2],[3,4],[5,6],[7,8]]");
        System.out.println("  Result: " + result5 + " (Expected: 1) - " + (result5 == 1 ? "PASS" : "FAIL"));
        System.out.println();
    }

    private static void testEdgeCases() {
        System.out.println("=== Testing Edge Cases ===\n");
        MeetingRooms2 solution = new MeetingRooms2();

        // Test 1: Single meeting
        int[][] intervals1 = {{1, 5}};
        int result1 = solution.minMeetingRooms(intervals1);
        System.out.println("Test 1: Single meeting [[1,5]]");
        System.out.println("  Result: " + result1 + " (Expected: 1) - " + (result1 == 1 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 2: Back-to-back meetings (no overlap)
        int[][] intervals2 = {{1, 5}, {5, 10}, {10, 15}};
        int result2 = solution.minMeetingRooms(intervals2);
        System.out.println("Test 2: Back-to-back [[1,5],[5,10],[10,15]]");
        System.out.println("  Result: " + result2 + " (Expected: 1) - " + (result2 == 1 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 3: Same start time
        int[][] intervals3 = {{5, 10}, {5, 15}, {5, 20}};
        int result3 = solution.minMeetingRooms(intervals3);
        System.out.println("Test 3: Same start time [[5,10],[5,15],[5,20]]");
        System.out.println("  Result: " + result3 + " (Expected: 3) - " + (result3 == 3 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 4: Meeting ends when another starts
        int[][] intervals4 = {{1, 5}, {5, 10}};
        int result4 = solution.minMeetingRooms(intervals4);
        System.out.println("Test 4: End = Next start [[1,5],[5,10]]");
        System.out.println("  Result: " + result4 + " (Expected: 1) - " + (result4 == 1 ? "PASS" : "FAIL"));
        System.out.println();
    }

    private static void testVariations() {
        System.out.println("=== Testing Variations ===\n");
        MeetingRoomsVariations variations = new MeetingRoomsVariations();

        // Test 1: Can attend all meetings
        int[][] intervals1 = {{7, 10}, {2, 4}};
        boolean canAttend1 = variations.canAttendMeetings(intervals1);
        System.out.println("Test 1: Can attend all meetings [[7,10],[2,4]]");
        System.out.println("  Result: " + canAttend1 + " (Expected: true) - " + (canAttend1 ? "PASS" : "FAIL"));

        int[][] intervals2 = {{0, 30}, {5, 10}};
        boolean canAttend2 = variations.canAttendMeetings(intervals2);
        System.out.println("  [[0,30],[5,10]]: " + canAttend2 + " (Expected: false) - " + (!canAttend2 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 2: Assign meeting rooms
        int[][] intervals3 = {{0, 30}, {5, 10}, {15, 20}};
        List<List<Integer>> assignment = variations.assignMeetingRooms(intervals3);
        System.out.println("Test 2: Assign meeting rooms [[0,30],[5,10],[15,20]]");
        System.out.println("  Number of rooms: " + assignment.size() + " (Expected: 2)");
        for (int i = 0; i < assignment.size(); i++) {
            System.out.println("  Room " + (i + 1) + ": meetings " + assignment.get(i));
        }
        System.out.println();

        // Test 3: Find free time
        int[][] intervals4 = {{9, 10}, {14, 15}};
        List<int[]> freeTime = variations.findFreeTime(intervals4, 8, 17);
        System.out.println("Test 3: Find free time between 8-17 with meetings [[9,10],[14,15]]");
        for (int[] slot : freeTime) {
            System.out.println("  Free: [" + slot[0] + "," + slot[1] + "]");
        }
        System.out.println();

        // Test 4: Find peak meeting time
        int[][] intervals5 = {{0, 30}, {5, 10}, {15, 20}};
        int[] peak = variations.findPeakMeetingTime(intervals5);
        System.out.println("Test 4: Find peak meeting time [[0,30],[5,10],[15,20]]");
        System.out.println("  Peak at time " + peak[0] + " with " + peak[1] + " meetings");
        System.out.println();

        // Test 5: Merge intervals
        int[][] intervals6 = {{1, 3}, {2, 6}, {8, 10}, {15, 18}};
        List<int[]> merged = variations.mergeIntervals(intervals6);
        System.out.println("Test 5: Merge intervals [[1,3],[2,6],[8,10],[15,18]]");
        for (int[] interval : merged) {
            System.out.println("  [" + interval[0] + "," + interval[1] + "]");
        }
        System.out.println();
    }
}
