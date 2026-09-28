import java.util.*;

/**
 * Cut Cake with Equal Area
 *
 * Problem:
 * Given a table with multiple square/rectangular cakes placed on it. All cakes are axis-aligned
 * (edges parallel to table edges). You can make one horizontal cut across the entire table.
 * Find where to cut so that the total area above the cut equals the total area below the cut.
 *
 * Input:
 * - Array of rectangles, each represented as [x1, y1, x2, y2]
 *   where (x1, y1) is bottom-left corner and (x2, y2) is top-right corner
 *
 * Output:
 * - The y-coordinate where the horizontal cut should be made
 * - Return -1 if no such cut exists
 *
 * Example 1:
 * Input: rectangles = [[0,0,2,2], [1,1,3,3]]
 * Output: 2.0
 * Explanation:
 * Rectangle 1: area = 4 (from y=0 to y=2)
 * Rectangle 2: area = 4 (from y=1 to y=3)
 * Total area = 8
 * Cut at y=2: below area = 4 (rect1) + 2 (part of rect2) = 6... need to find exact cut
 *
 * Example 2:
 * Input: rectangles = [[0,0,4,2], [0,2,4,4]]
 * Output: 3.0
 * Explanation:
 * Total area = 8 + 8 = 16
 * Cut at y=3: below = 8 + 4 = 12, above = 4
 * Need area = 8 on each side
 *
 * Approach:
 * 1. Use sweep line algorithm to process rectangles from bottom to top
 * 2. Track cumulative area as we sweep up
 * 3. Find the y-coordinate where cumulative area = total area / 2
 *
 * Similar to: LeetCode 850 (Rectangle Area II), LeetCode 218 (Skyline Problem)
 */
public class CutCakeEqualArea {

    /**
     * Approach 1: Sweep Line with Events
     *
     * Time Complexity: O(n log n) where n is number of rectangles
     * Space Complexity: O(n)
     */
    public double findCutPosition(int[][] rectangles) {
        if (rectangles == null || rectangles.length == 0) {
            return -1;
        }

        // Calculate total area
        long totalArea = 0;
        for (int[] rect : rectangles) {
            long width = rect[2] - rect[0];
            long height = rect[3] - rect[1];
            totalArea += width * height;
        }

        // If total area is odd, we can't split equally with integer coordinates
        if (totalArea % 2 != 0) {
            // Still continue - we'll find the closest position
        }

        long targetArea = totalArea / 2;

        // Create events for each rectangle's top and bottom edges
        List<Event> events = new ArrayList<>();

        for (int[] rect : rectangles) {
            int x1 = rect[0], y1 = rect[1], x2 = rect[2], y2 = rect[3];
            events.add(new Event(y1, x1, x2, true));  // bottom edge (start)
            events.add(new Event(y2, x1, x2, false)); // top edge (end)
        }

        // Sort events by y-coordinate
        Collections.sort(events, (a, b) -> {
            if (a.y != b.y) return Integer.compare(a.y, b.y);
            // Process endings before starts at same y
            return Boolean.compare(a.isStart, b.isStart);
        });

        // Sweep from bottom to top
        long cumulativeArea = 0;
        List<int[]> activeIntervals = new ArrayList<>(); // [x1, x2] of active horizontal segments
        int prevY = events.get(0).y;

        for (Event event : events) {
            int currentY = event.y;

            // Add area from previous y to current y
            if (currentY > prevY) {
                int totalWidth = getTotalWidth(activeIntervals);
                long areaAdded = (long) totalWidth * (currentY - prevY);
                cumulativeArea += areaAdded;

                // Check if we've reached or passed the target
                if (cumulativeArea >= targetArea) {
                    // Binary search or calculate exact position
                    long excessArea = cumulativeArea - targetArea;
                    long heightDiff = currentY - prevY;

                    if (totalWidth > 0) {
                        double heightBack = (double) excessArea / totalWidth;
                        return currentY - heightBack;
                    }
                }
            }

            // Update active intervals
            if (event.isStart) {
                addInterval(activeIntervals, event.x1, event.x2);
            } else {
                removeInterval(activeIntervals, event.x1, event.x2);
            }

            prevY = currentY;
        }

        return -1; // No valid cut found
    }

    /**
     * Approach 2: Discretization + Binary Search
     *
     * Collect all unique y-coordinates, then binary search for the cut position
     */
    public double findCutPositionBinarySearch(int[][] rectangles) {
        if (rectangles == null || rectangles.length == 0) {
            return -1;
        }

        // Calculate total area
        long totalArea = 0;
        for (int[] rect : rectangles) {
            totalArea += (long) (rect[2] - rect[0]) * (rect[3] - rect[1]);
        }

        long targetArea = totalArea / 2;

        // Collect all unique y-coordinates
        Set<Integer> ySet = new TreeSet<>();
        for (int[] rect : rectangles) {
            ySet.add(rect[1]); // bottom
            ySet.add(rect[3]); // top
        }

        List<Integer> yCoords = new ArrayList<>(ySet);

        // Binary search on y-coordinates
        double left = yCoords.get(0);
        double right = yCoords.get(yCoords.size() - 1);
        double epsilon = 1e-6;

        while (right - left > epsilon) {
            double mid = (left + right) / 2;
            long areaBelow = calculateAreaBelow(rectangles, mid);

            if (areaBelow < targetArea) {
                left = mid;
            } else {
                right = mid;
            }
        }

        return (left + right) / 2;
    }

    /**
     * Calculate total area of all rectangles below the given y-coordinate
     */
    private long calculateAreaBelow(int[][] rectangles, double y) {
        long area = 0;

        for (int[] rect : rectangles) {
            int x1 = rect[0], y1 = rect[1], x2 = rect[2], y2 = rect[3];

            if (y2 <= y) {
                // Entire rectangle is below the cut
                area += (long) (x2 - x1) * (y2 - y1);
            } else if (y1 < y) {
                // Rectangle is partially below the cut
                double height = y - y1;
                area += (long) ((x2 - x1) * height);
            }
            // If y1 >= y, rectangle is entirely above, contribute 0
        }

        return area;
    }

    /**
     * Get total width covered by active intervals (merge overlapping intervals)
     */
    private int getTotalWidth(List<int[]> intervals) {
        if (intervals.isEmpty()) return 0;

        // Sort and merge intervals
        List<int[]> sorted = new ArrayList<>(intervals);
        sorted.sort((a, b) -> Integer.compare(a[0], b[0]));

        int totalWidth = 0;
        int currentStart = sorted.get(0)[0];
        int currentEnd = sorted.get(0)[1];

        for (int i = 1; i < sorted.size(); i++) {
            int[] interval = sorted.get(i);
            if (interval[0] <= currentEnd) {
                // Overlapping, merge
                currentEnd = Math.max(currentEnd, interval[1]);
            } else {
                // No overlap, add previous width and start new interval
                totalWidth += currentEnd - currentStart;
                currentStart = interval[0];
                currentEnd = interval[1];
            }
        }

        totalWidth += currentEnd - currentStart;
        return totalWidth;
    }

    /**
     * Add interval to active intervals list
     */
    private void addInterval(List<int[]> intervals, int x1, int x2) {
        intervals.add(new int[]{x1, x2});
    }

    /**
     * Remove interval from active intervals list
     */
    private void removeInterval(List<int[]> intervals, int x1, int x2) {
        for (int i = 0; i < intervals.size(); i++) {
            if (intervals.get(i)[0] == x1 && intervals.get(i)[1] == x2) {
                intervals.remove(i);
                break;
            }
        }
    }

    /**
     * Event class for sweep line algorithm
     */
    static class Event {
        int y;      // y-coordinate of the edge
        int x1, x2; // horizontal span [x1, x2]
        boolean isStart; // true = bottom edge, false = top edge

        Event(int y, int x1, int x2, boolean isStart) {
            this.y = y;
            this.x1 = x1;
            this.x2 = x2;
            this.isStart = isStart;
        }
    }

    /**
     * Approach 3: Simple approach with all y-coordinates
     * Check area at each distinct y-coordinate
     */
    public double findCutPositionSimple(int[][] rectangles) {
        if (rectangles == null || rectangles.length == 0) {
            return -1;
        }

        long totalArea = 0;
        Set<Integer> ySet = new TreeSet<>();

        for (int[] rect : rectangles) {
            totalArea += (long) (rect[2] - rect[0]) * (rect[3] - rect[1]);
            ySet.add(rect[1]);
            ySet.add(rect[3]);
        }

        long targetArea = totalArea / 2;
        List<Integer> yCoords = new ArrayList<>(ySet);

        // Check each y-coordinate
        for (int y : yCoords) {
            long areaBelow = calculateAreaBelow(rectangles, y);
            if (areaBelow == targetArea) {
                return y;
            }
        }

        // If no exact match, use binary search for precise position
        return findCutPositionBinarySearch(rectangles);
    }

    // ==================== Test Cases ====================

    public static void main(String[] args) {
        CutCakeEqualArea solution = new CutCakeEqualArea();

        System.out.println("=== Cut Cake with Equal Area ===\n");

        // Test 1: Two non-overlapping rectangles
        System.out.println("Test 1: Two stacked rectangles");
        int[][] rects1 = {{0, 0, 4, 2}, {0, 2, 4, 4}};
        double result1 = solution.findCutPositionBinarySearch(rects1);
        System.out.println("Rectangles: " + Arrays.deepToString(rects1));
        System.out.println("Total area: 16, target: 8 each side");
        System.out.println("Cut position: " + result1);
        System.out.println("Expected: 3.0");
        System.out.println();

        // Test 2: Overlapping rectangles
        System.out.println("Test 2: Overlapping rectangles");
        int[][] rects2 = {{0, 0, 2, 2}, {1, 1, 3, 3}};
        double result2 = solution.findCutPositionBinarySearch(rects2);
        System.out.println("Rectangles: " + Arrays.deepToString(rects2));
        System.out.println("Cut position: " + result2);
        System.out.println();

        // Test 3: Three rectangles
        System.out.println("Test 3: Three rectangles");
        int[][] rects3 = {{0, 0, 2, 1}, {0, 1, 2, 2}, {0, 2, 2, 3}};
        double result3 = solution.findCutPositionBinarySearch(rects3);
        System.out.println("Rectangles: " + Arrays.deepToString(rects3));
        System.out.println("Total area: 6, target: 3 each side");
        System.out.println("Cut position: " + result3);
        System.out.println("Expected: 1.5");
        System.out.println();

        // Test 4: Single large rectangle
        System.out.println("Test 4: Single rectangle");
        int[][] rects4 = {{0, 0, 4, 4}};
        double result4 = solution.findCutPositionBinarySearch(rects4);
        System.out.println("Rectangles: " + Arrays.deepToString(rects4));
        System.out.println("Total area: 16, target: 8 each side");
        System.out.println("Cut position: " + result4);
        System.out.println("Expected: 2.0");
        System.out.println();

        // Test 5: Multiple rectangles at different heights
        System.out.println("Test 5: Complex layout");
        int[][] rects5 = {{0, 0, 2, 2}, {2, 0, 4, 2}, {0, 2, 4, 3}};
        double result5 = solution.findCutPositionBinarySearch(rects5);
        System.out.println("Rectangles: " + Arrays.deepToString(rects5));
        System.out.println("Total area: 8 + 4 = 12, target: 6 each side");
        System.out.println("Cut position: " + result5);
        System.out.println();

        // Verify solution
        System.out.println("=== Verification ===");
        System.out.println("Test 1 verification:");
        long below1 = solution.calculateAreaBelow(rects1, result1);
        long total1 = 16;
        System.out.println("Area below: " + below1 + ", Area above: " + (total1 - below1));

        System.out.println("\nTest 4 verification:");
        long below4 = solution.calculateAreaBelow(rects4, result4);
        long total4 = 16;
        System.out.println("Area below: " + below4 + ", Area above: " + (total4 - below4));
    }
}
