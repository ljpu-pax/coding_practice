import java.util.*;

/**
 * Count Overlapping Intervals
 *
 * Given a list of intervals and a target interval, count how many intervals
 * in the list overlap with the target interval.
 *
 * Two intervals [a, b] and [c, d] overlap if they have at least one point in common.
 * i.e., max(a, c) <= min(b, d)
 *
 * Example 1:
 * Input: intervals = [[1,3], [2,4], [3,100]], target = [2,5]
 * Output: 3
 * Explanation: All three intervals overlap with [2,5]:
 * - [1,3] and [2,5] overlap at [2,3]
 * - [2,4] and [2,5] overlap at [2,4]
 * - [3,100] and [2,5] overlap at [3,5]
 *
 * Example 2:
 * Input: intervals = [[1,2], [3,4], [5,6]], target = [7,8]
 * Output: 0
 *
 * Example 3:
 * Input: intervals = [[1,5], [2,3], [4,6]], target = [3,4]
 * Output: 3
 */
class IntervalOverlapCount {
    /**
     * Approach 1: Brute Force - Check each interval
     *
     * For each interval in the list, check if it overlaps with the target.
     * Two intervals [a,b] and [c,d] overlap if: max(a,c) <= min(b,d)
     *
     * Time: O(n) where n is the number of intervals
     * Space: O(1)
     */
    public int countOverlaps(int[][] intervals, int[] target) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        int count = 0;
        int targetStart = target[0];
        int targetEnd = target[1];

        for (int[] interval : intervals) {
            int start = interval[0];
            int end = interval[1];

            // Check if intervals overlap
            if (hasOverlap(start, end, targetStart, targetEnd)) {
                count++;
            }
        }

        return count;
    }

    private boolean hasOverlap(int start1, int end1, int start2, int end2) {
        // Two intervals overlap if: max(start1, start2) <= min(end1, end2)
        return Math.max(start1, start2) <= Math.min(end1, end2);
    }

    /**
     * Approach 2: Alternative overlap check
     *
     * Two intervals DON'T overlap if:
     * - One ends before the other starts
     * So they DO overlap if neither of these conditions is true.
     *
     * Time: O(n)
     * Space: O(1)
     */
    public int countOverlapsAlt(int[][] intervals, int[] target) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        int count = 0;
        int targetStart = target[0];
        int targetEnd = target[1];

        for (int[] interval : intervals) {
            int start = interval[0];
            int end = interval[1];

            // Intervals overlap if they don't NOT overlap
            // They don't overlap if: end < targetStart OR start > targetEnd
            if (!(end < targetStart || start > targetEnd)) {
                count++;
            }
        }

        return count;
    }

    /**
     * Approach 3: Using Interval class for better readability
     *
     * Time: O(n)
     * Space: O(1) - not counting input
     */
    static class Interval {
        int start;
        int end;

        Interval(int start, int end) {
            this.start = start;
            this.end = end;
        }

        boolean overlaps(Interval other) {
            return Math.max(this.start, other.start) <= Math.min(this.end, other.end);
        }

        @Override
        public String toString() {
            return "[" + start + "," + end + "]";
        }
    }

    public int countOverlapsWithClass(List<Interval> intervals, Interval target) {
        if (intervals == null || intervals.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (Interval interval : intervals) {
            if (interval.overlaps(target)) {
                count++;
            }
        }

        return count;
    }

    /**
     * Approach 4: Binary Search (if intervals are sorted)
     *
     * If the intervals list is sorted by start time, we can optimize using binary search
     * to find the range of potentially overlapping intervals.
     *
     * Time: O(log n + k) where k is the number of overlapping intervals
     * Space: O(1)
     */
    public int countOverlapsSorted(int[][] intervals, int[] target) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        // Assume intervals are sorted by start time
        int count = 0;
        int targetStart = target[0];
        int targetEnd = target[1];

        // Binary search to find first interval that could overlap
        int left = 0, right = intervals.length - 1;
        int firstPossible = intervals.length;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (intervals[mid][0] <= targetEnd) {
                firstPossible = mid;
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        // Check all intervals starting from firstPossible
        for (int i = firstPossible; i < intervals.length; i++) {
            // If interval starts after target ends, no more overlaps possible
            if (intervals[i][0] > targetEnd) {
                break;
            }

            if (hasOverlap(intervals[i][0], intervals[i][1], targetStart, targetEnd)) {
                count++;
            }
        }

        return count;
    }
}

/**
 * Extended version: Return the overlapping intervals themselves
 */
class IntervalOverlapFinder {
    static class Interval {
        int start;
        int end;

        Interval(int start, int end) {
            this.start = start;
            this.end = end;
        }

        boolean overlaps(Interval other) {
            return Math.max(this.start, other.start) <= Math.min(this.end, other.end);
        }

        // Get the intersection of two intervals
        Interval getIntersection(Interval other) {
            if (!overlaps(other)) return null;
            return new Interval(
                Math.max(this.start, other.start),
                Math.min(this.end, other.end)
            );
        }

        @Override
        public String toString() {
            return "[" + start + "," + end + "]";
        }
    }

    /**
     * Find all intervals that overlap with target
     */
    public List<Interval> findOverlappingIntervals(List<Interval> intervals, Interval target) {
        List<Interval> result = new ArrayList<>();

        for (Interval interval : intervals) {
            if (interval.overlaps(target)) {
                result.add(interval);
            }
        }

        return result;
    }

    /**
     * Find all intervals that overlap with target and return their intersections
     */
    public List<Interval> findIntersections(List<Interval> intervals, Interval target) {
        List<Interval> result = new ArrayList<>();

        for (Interval interval : intervals) {
            Interval intersection = interval.getIntersection(target);
            if (intersection != null) {
                result.add(intersection);
            }
        }

        return result;
    }
}

/**
 * Follow-up variations
 */
class IntervalOverlapVariations {
    /**
     * Variation 1: Count intervals with exact overlap length >= k
     */
    public int countOverlapsWithMinLength(int[][] intervals, int[] target, int minLength) {
        int count = 0;
        int targetStart = target[0];
        int targetEnd = target[1];

        for (int[] interval : intervals) {
            int start = interval[0];
            int end = interval[1];

            int overlapStart = Math.max(start, targetStart);
            int overlapEnd = Math.min(end, targetEnd);

            if (overlapStart <= overlapEnd) {
                int overlapLength = overlapEnd - overlapStart + 1;
                if (overlapLength >= minLength) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Variation 2: Count intervals that are completely contained in target
     */
    public int countContainedIntervals(int[][] intervals, int[] target) {
        int count = 0;
        int targetStart = target[0];
        int targetEnd = target[1];

        for (int[] interval : intervals) {
            int start = interval[0];
            int end = interval[1];

            // Check if interval is completely contained in target
            if (start >= targetStart && end <= targetEnd) {
                count++;
            }
        }

        return count;
    }

    /**
     * Variation 3: Count intervals that completely contain the target
     */
    public int countContainingIntervals(int[][] intervals, int[] target) {
        int count = 0;
        int targetStart = target[0];
        int targetEnd = target[1];

        for (int[] interval : intervals) {
            int start = interval[0];
            int end = interval[1];

            // Check if interval completely contains target
            if (start <= targetStart && end >= targetEnd) {
                count++;
            }
        }

        return count;
    }

    /**
     * Variation 4: Find maximum overlap at any point
     */
    public int maxOverlapAtAnyPoint(int[][] intervals) {
        List<int[]> events = new ArrayList<>();

        for (int[] interval : intervals) {
            events.add(new int[]{interval[0], 1}); // start event
            events.add(new int[]{interval[1] + 1, -1}); // end event (exclusive)
        }

        // Sort events by time, ties broken by type (end before start)
        events.sort((a, b) -> a[0] == b[0] ? a[1] - b[1] : a[0] - b[0]);

        int maxOverlap = 0;
        int currentOverlap = 0;

        for (int[] event : events) {
            currentOverlap += event[1];
            maxOverlap = Math.max(maxOverlap, currentOverlap);
        }

        return maxOverlap;
    }
}

/**
 * Test cases
 */
class IntervalOverlapCountTest {
    public static void main(String[] args) {
        testBasicOverlapCount();
        testOverlapFinder();
        testVariations();
    }

    private static void testBasicOverlapCount() {
        System.out.println("=== Testing Basic Interval Overlap Count ===\n");
        IntervalOverlapCount solution = new IntervalOverlapCount();

        // Test 1: Example from problem
        int[][] intervals1 = {{1, 3}, {2, 4}, {3, 100}};
        int[] target1 = {2, 5};
        int result1 = solution.countOverlaps(intervals1, target1);
        int result1Alt = solution.countOverlapsAlt(intervals1, target1);
        System.out.println("Test 1: intervals = [[1,3],[2,4],[3,100]], target = [2,5]");
        System.out.println("  Approach 1: " + result1 + " (Expected: 3) - " + (result1 == 3 ? "PASS" : "FAIL"));
        System.out.println("  Approach 2: " + result1Alt + " (Expected: 3) - " + (result1Alt == 3 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 2: No overlaps
        int[][] intervals2 = {{1, 2}, {3, 4}, {5, 6}};
        int[] target2 = {7, 8};
        int result2 = solution.countOverlaps(intervals2, target2);
        System.out.println("Test 2: intervals = [[1,2],[3,4],[5,6]], target = [7,8]");
        System.out.println("  Result: " + result2 + " (Expected: 0) - " + (result2 == 0 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 3: All overlap
        int[][] intervals3 = {{1, 5}, {2, 3}, {4, 6}};
        int[] target3 = {3, 4};
        int result3 = solution.countOverlaps(intervals3, target3);
        System.out.println("Test 3: intervals = [[1,5],[2,3],[4,6]], target = [3,4]");
        System.out.println("  Result: " + result3 + " (Expected: 3) - " + (result3 == 3 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 4: Edge touch (should overlap)
        int[][] intervals4 = {{1, 3}, {5, 7}};
        int[] target4 = {3, 5};
        int result4 = solution.countOverlaps(intervals4, target4);
        System.out.println("Test 4: intervals = [[1,3],[5,7]], target = [3,5]");
        System.out.println("  Result: " + result4 + " (Expected: 2) - " + (result4 == 2 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 5: Sorted intervals with binary search
        int[][] intervals5 = {{1, 3}, {2, 5}, {6, 8}, {7, 10}};
        int[] target5 = {4, 7};
        int result5Sorted = solution.countOverlapsSorted(intervals5, target5);
        System.out.println("Test 5: intervals = [[1,3],[2,5],[6,8],[7,10]], target = [4,7]");
        System.out.println("  Binary Search: " + result5Sorted + " (Expected: 3) - " + (result5Sorted == 3 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 6: Using Interval class
        List<IntervalOverlapCount.Interval> intervals6 = new ArrayList<>();
        intervals6.add(new IntervalOverlapCount.Interval(1, 3));
        intervals6.add(new IntervalOverlapCount.Interval(2, 4));
        intervals6.add(new IntervalOverlapCount.Interval(3, 100));
        IntervalOverlapCount.Interval target6 = new IntervalOverlapCount.Interval(2, 5);
        int result6 = solution.countOverlapsWithClass(intervals6, target6);
        System.out.println("Test 6: Using Interval class");
        System.out.println("  Result: " + result6 + " (Expected: 3) - " + (result6 == 3 ? "PASS" : "FAIL"));
        System.out.println();
    }

    private static void testOverlapFinder() {
        System.out.println("=== Testing Overlap Finder (returns intervals) ===\n");
        IntervalOverlapFinder finder = new IntervalOverlapFinder();

        List<IntervalOverlapFinder.Interval> intervals = new ArrayList<>();
        intervals.add(new IntervalOverlapFinder.Interval(1, 3));
        intervals.add(new IntervalOverlapFinder.Interval(2, 4));
        intervals.add(new IntervalOverlapFinder.Interval(3, 100));

        IntervalOverlapFinder.Interval target = new IntervalOverlapFinder.Interval(2, 5);

        List<IntervalOverlapFinder.Interval> overlapping = finder.findOverlappingIntervals(intervals, target);
        System.out.println("Overlapping intervals with [2,5]:");
        for (IntervalOverlapFinder.Interval interval : overlapping) {
            System.out.println("  " + interval);
        }

        List<IntervalOverlapFinder.Interval> intersections = finder.findIntersections(intervals, target);
        System.out.println("\nIntersections with [2,5]:");
        for (IntervalOverlapFinder.Interval interval : intersections) {
            System.out.println("  " + interval);
        }
        System.out.println();
    }

    private static void testVariations() {
        System.out.println("=== Testing Variations ===\n");
        IntervalOverlapVariations variations = new IntervalOverlapVariations();

        int[][] intervals = {{1, 3}, {2, 6}, {4, 8}, {10, 12}};
        int[] target = {3, 7};

        // Test 1: Minimum overlap length
        int result1 = variations.countOverlapsWithMinLength(intervals, target, 2);
        System.out.println("Test 1: Count overlaps with min length 2");
        System.out.println("  Result: " + result1 + " (Expected: 2) - [2,6] and [4,8]");
        System.out.println();

        // Test 2: Contained intervals
        int[][] intervals2 = {{1, 10}, {3, 5}, {4, 6}, {2, 8}};
        int[] target2 = {2, 8};
        int result2 = variations.countContainedIntervals(intervals2, target2);
        System.out.println("Test 2: Count intervals contained in [2,8]");
        System.out.println("  Result: " + result2 + " (Expected: 3) - [3,5], [4,6], [2,8]");
        System.out.println();

        // Test 3: Containing intervals
        int result3 = variations.countContainingIntervals(intervals2, target2);
        System.out.println("Test 3: Count intervals containing [2,8]");
        System.out.println("  Result: " + result3 + " (Expected: 2) - [1,10], [2,8]");
        System.out.println();

        // Test 4: Max overlap at any point
        int[][] intervals4 = {{1, 5}, {2, 6}, {3, 7}, {8, 10}};
        int result4 = variations.maxOverlapAtAnyPoint(intervals4);
        System.out.println("Test 4: Max overlap at any point");
        System.out.println("  Result: " + result4 + " (Expected: 3) - at point 3-5");
        System.out.println();
    }
}
