import java.util.*;

/**
 * LeetCode 149: Max Points on a Line (Hard)
 *
 * Given an array of points where points[i] = [xi, yi] represents a point on the X-Y plane,
 * return the maximum number of points that lie on the same straight line.
 *
 * Example 1:
 * Input: points = [[1,1],[2,2],[3,3]]
 * Output: 3
 * Explanation: All three points lie on the same line
 *
 * Example 2:
 * Input: points = [[1,1],[3,2],[5,3],[4,1],[2,3],[1,4]]
 * Output: 4
 * Explanation: Points [1,1], [2,3], [3,2] don't form a line, but [1,1],[2,3],[3,2],[1,4] might
 *
 * Constraints:
 * - 1 <= points.length <= 300
 * - points[i].length == 2
 * - -10^4 <= xi, yi <= 10^4
 * - All the points are unique
 */
public class MaxPointsOnLine {

    /**
     * Approach 1: Hash Map with Slope (using GCD to avoid floating point issues)
     *
     * Key Insight:
     * - Two points define a line
     * - Points on the same line have the same slope relative to a fixed point
     * - Use slope as key in hash map to count points
     *
     * Slope between (x1, y1) and (x2, y2):
     * - slope = (y2 - y1) / (x2 - x1)
     * - To avoid floating point precision issues, store slope as reduced fraction: dy/dx
     *
     * Time: O(n²) - for each point, check all other points
     * Space: O(n) - hash map for slopes
     */
    public int maxPoints(int[][] points) {
        if (points == null || points.length == 0) {
            return 0;
        }

        if (points.length <= 2) {
            return points.length;
        }

        int maxPoints = 0;

        // Try each point as the anchor point
        for (int i = 0; i < points.length; i++) {
            // Map: slope -> count of points with this slope from point i
            Map<String, Integer> slopeMap = new HashMap<>();
            int duplicate = 0;  // count of duplicate points
            int currentMax = 0;

            for (int j = i + 1; j < points.length; j++) {
                int dx = points[j][0] - points[i][0];
                int dy = points[j][1] - points[i][1];

                // Handle duplicate points
                if (dx == 0 && dy == 0) {
                    duplicate++;
                    continue;
                }

                // Reduce the slope to lowest terms using GCD
                int gcd = gcd(dx, dy);
                dx /= gcd;
                dy /= gcd;

                // Normalize the slope: ensure dx is always positive
                // This handles the case where (-2, -3) and (2, 3) are the same slope
                if (dx < 0) {
                    dx = -dx;
                    dy = -dy;
                } else if (dx == 0) {
                    // Vertical line: ensure dy is positive
                    dy = Math.abs(dy);
                }

                // Create slope key
                String slope = dy + "/" + dx;

                slopeMap.put(slope, slopeMap.getOrDefault(slope, 0) + 1);
                currentMax = Math.max(currentMax, slopeMap.get(slope));
            }

            // Add 1 for the anchor point itself, plus duplicates
            maxPoints = Math.max(maxPoints, currentMax + duplicate + 1);
        }

        return maxPoints;
    }

    /**
     * Calculate Greatest Common Divisor using Euclidean algorithm
     */
    private int gcd(int a, int b) {
        a = Math.abs(a);
        b = Math.abs(b);

        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }

        return a;
    }

    /**
     * Approach 2: Using Pair class for cleaner slope representation
     */
    public int maxPointsV2(int[][] points) {
        if (points == null || points.length <= 2) {
            return points == null ? 0 : points.length;
        }

        int maxPoints = 0;

        for (int i = 0; i < points.length; i++) {
            Map<Slope, Integer> slopeMap = new HashMap<>();
            int currentMax = 0;

            for (int j = i + 1; j < points.length; j++) {
                Slope slope = new Slope(points[i], points[j]);
                slopeMap.put(slope, slopeMap.getOrDefault(slope, 0) + 1);
                currentMax = Math.max(currentMax, slopeMap.get(slope));
            }

            maxPoints = Math.max(maxPoints, currentMax + 1);
        }

        return maxPoints;
    }

    /**
     * Helper class to represent slope as a reduced fraction
     */
    static class Slope {
        int dy;  // numerator
        int dx;  // denominator

        public Slope(int[] p1, int[] p2) {
            int deltaX = p2[0] - p1[0];
            int deltaY = p2[1] - p1[1];

            if (deltaX == 0) {
                // Vertical line
                dy = 1;
                dx = 0;
            } else if (deltaY == 0) {
                // Horizontal line
                dy = 0;
                dx = 1;
            } else {
                // Reduce to lowest terms
                int gcd = gcd(Math.abs(deltaX), Math.abs(deltaY));
                dy = deltaY / gcd;
                dx = deltaX / gcd;

                // Normalize: make dx positive
                if (dx < 0) {
                    dx = -dx;
                    dy = -dy;
                }
            }
        }

        private int gcd(int a, int b) {
            while (b != 0) {
                int temp = b;
                b = a % b;
                a = temp;
            }
            return a;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Slope)) return false;
            Slope slope = (Slope) o;
            return dy == slope.dy && dx == slope.dx;
        }

        @Override
        public int hashCode() {
            return Objects.hash(dy, dx);
        }

        @Override
        public String toString() {
            return dy + "/" + dx;
        }
    }

    /**
     * Approach 3: Brute force (for comparison) - check all possible lines
     * Time: O(n³)
     */
    public int maxPointsBruteForce(int[][] points) {
        if (points.length <= 2) return points.length;

        int maxPoints = 0;

        // Try every pair of points to define a line
        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                int count = 2;  // At least points i and j

                // Check how many other points are on this line
                for (int k = 0; k < points.length; k++) {
                    if (k == i || k == j) continue;

                    // Check if point k is on the line defined by i and j
                    if (isCollinear(points[i], points[j], points[k])) {
                        count++;
                    }
                }

                maxPoints = Math.max(maxPoints, count);
            }
        }

        return maxPoints;
    }

    /**
     * Check if three points are collinear using cross product
     * Points p1, p2, p3 are collinear if:
     * (p2.y - p1.y) * (p3.x - p2.x) == (p3.y - p2.y) * (p2.x - p1.x)
     */
    private boolean isCollinear(int[] p1, int[] p2, int[] p3) {
        // Use cross product to avoid division
        // (y2 - y1) / (x2 - x1) == (y3 - y1) / (x3 - x1)
        // Cross multiply: (y2 - y1) * (x3 - x1) == (y3 - y1) * (x2 - x1)
        long dy1 = p2[1] - p1[1];
        long dx1 = p2[0] - p1[0];
        long dy2 = p3[1] - p1[1];
        long dx2 = p3[0] - p1[0];

        return dy1 * dx2 == dy2 * dx1;
    }

    // ==================== Test Cases ====================

    public static void main(String[] args) {
        MaxPointsOnLine solution = new MaxPointsOnLine();

        System.out.println("=== LeetCode 149: Max Points on a Line ===\n");

        // Test 1: All points on same line
        System.out.println("Test 1: All points on same line");
        int[][] points1 = {{1,1}, {2,2}, {3,3}};
        int result1 = solution.maxPoints(points1);
        System.out.println("Points: " + java.util.Arrays.deepToString(points1));
        System.out.println("Result: " + result1);
        System.out.println("Expected: 3\n");

        // Test 2: Multiple lines
        System.out.println("Test 2: Multiple lines");
        int[][] points2 = {{1,1}, {3,2}, {5,3}, {4,1}, {2,3}, {1,4}};
        int result2 = solution.maxPoints(points2);
        System.out.println("Points: " + java.util.Arrays.deepToString(points2));
        System.out.println("Result: " + result2);
        System.out.println("Expected: 4\n");

        // Test 3: Vertical line
        System.out.println("Test 3: Vertical line");
        int[][] points3 = {{1,1}, {1,2}, {1,3}, {2,1}};
        int result3 = solution.maxPoints(points3);
        System.out.println("Points: " + java.util.Arrays.deepToString(points3));
        System.out.println("Result: " + result3);
        System.out.println("Expected: 3\n");

        // Test 4: Horizontal line
        System.out.println("Test 4: Horizontal line");
        int[][] points4 = {{1,1}, {2,1}, {3,1}, {1,2}};
        int result4 = solution.maxPoints(points4);
        System.out.println("Points: " + java.util.Arrays.deepToString(points4));
        System.out.println("Result: " + result4);
        System.out.println("Expected: 3\n");

        // Test 5: Two points
        System.out.println("Test 5: Two points");
        int[][] points5 = {{1,1}, {2,2}};
        int result5 = solution.maxPoints(points5);
        System.out.println("Points: " + java.util.Arrays.deepToString(points5));
        System.out.println("Result: " + result5);
        System.out.println("Expected: 2\n");

        // Test 6: Single point
        System.out.println("Test 6: Single point");
        int[][] points6 = {{1,1}};
        int result6 = solution.maxPoints(points6);
        System.out.println("Points: " + java.util.Arrays.deepToString(points6));
        System.out.println("Result: " + result6);
        System.out.println("Expected: 1\n");

        // Test 7: Negative coordinates and same slope
        System.out.println("Test 7: Negative coordinates");
        int[][] points7 = {{0,0}, {1,1}, {-1,-1}, {2,2}};
        int result7 = solution.maxPoints(points7);
        System.out.println("Points: " + java.util.Arrays.deepToString(points7));
        System.out.println("Result: " + result7);
        System.out.println("Expected: 4\n");

        // Test 8: Compare all approaches
        System.out.println("Test 8: Compare all approaches");
        int[][] points8 = {{1,1}, {2,2}, {3,3}, {4,4}, {1,2}, {2,3}};
        int resultHash = solution.maxPoints(points8);
        int resultV2 = solution.maxPointsV2(points8);
        int resultBrute = solution.maxPointsBruteForce(points8);
        System.out.println("Hash Map approach: " + resultHash);
        System.out.println("V2 (Slope class): " + resultV2);
        System.out.println("Brute Force: " + resultBrute);
        System.out.println("All should be equal\n");

        // Test 9: Points forming different slopes
        System.out.println("Test 9: Different slopes");
        int[][] points9 = {{0,0}, {1,1}, {0,1}, {1,0}, {2,2}};
        int result9 = solution.maxPoints(points9);
        System.out.println("Points: " + java.util.Arrays.deepToString(points9));
        System.out.println("Result: " + result9);
        System.out.println("Expected: 3 (diagonal line: 0,0 -> 1,1 -> 2,2)\n");
    }
}
