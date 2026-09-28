/**
 * Problem: Find the Largest Rectangle from Points
 *
 * Given a list of points on a 2-D coordinate, find the biggest rectangle
 * that can be formed by these points (where all 4 corners are points from the list).
 *
 * The rectangle must be axis-aligned (sides parallel to x and y axes).
 *
 * Example 1:
 * Input: points = [[0,0], [0,1], [1,0], [1,1], [2,2]]
 * Output: 1 (rectangle formed by [0,0], [0,1], [1,0], [1,1])
 *
 * Example 2:
 * Input: points = [[0,0], [0,3], [3,0], [3,3], [1,1], [1,2], [2,1], [2,2]]
 * Output: 9 (rectangle formed by [0,0], [0,3], [3,0], [3,3])
 *
 * Example 3:
 * Input: points = [[1,1], [1,3], [3,1], [3,3], [2,2]]
 * Output: 4 (rectangle formed by [1,1], [1,3], [3,1], [3,3])
 */
public class LargestRectangleFromPoints {

    /**
     * Approach 1: Hash Set + Try all diagonal pairs
     *
     * Key Insight:
     * - A rectangle is defined by 4 points: (x1,y1), (x1,y2), (x2,y1), (x2,y2)
     * - We can try all pairs of points as diagonal corners
     * - Then check if the other two corners exist
     *
     * Time: O(n²) - try all pairs of points
     * Space: O(n) - hash set to store points
     */
    public int maxRectangleArea(int[][] points) {
        if (points == null || points.length < 4) {
            return 0;
        }

        // Store all points in a set for O(1) lookup
        java.util.Set<String> pointSet = new java.util.HashSet<>();
        for (int[] point : points) {
            pointSet.add(point[0] + "," + point[1]);
        }

        int maxArea = 0;

        // Try all pairs of points as potential diagonal corners
        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                int x1 = points[i][0];
                int y1 = points[i][1];
                int x2 = points[j][0];
                int y2 = points[j][1];

                // Skip if points are on same horizontal or vertical line
                if (x1 == x2 || y1 == y2) {
                    continue;
                }

                // Check if the other two corners exist
                String corner1 = x1 + "," + y2;
                String corner2 = x2 + "," + y1;

                if (pointSet.contains(corner1) && pointSet.contains(corner2)) {
                    int area = Math.abs(x2 - x1) * Math.abs(y2 - y1);
                    maxArea = Math.max(maxArea, area);
                }
            }
        }

        return maxArea;
    }

    /**
     * Approach 2: Using Point class with better organization
     */
    public int maxRectangleAreaV2(int[][] points) {
        if (points == null || points.length < 4) {
            return 0;
        }

        java.util.Set<Point> pointSet = new java.util.HashSet<>();
        for (int[] p : points) {
            pointSet.add(new Point(p[0], p[1]));
        }

        int maxArea = 0;

        // Convert to array for easier iteration
        Point[] pointArray = pointSet.toArray(new Point[0]);

        for (int i = 0; i < pointArray.length; i++) {
            for (int j = i + 1; j < pointArray.length; j++) {
                Point p1 = pointArray[i];
                Point p2 = pointArray[j];

                // These two points must be diagonal corners
                if (p1.x == p2.x || p1.y == p2.y) {
                    continue;
                }

                // Check if other two corners exist
                Point p3 = new Point(p1.x, p2.y);
                Point p4 = new Point(p2.x, p1.y);

                if (pointSet.contains(p3) && pointSet.contains(p4)) {
                    int area = Math.abs(p2.x - p1.x) * Math.abs(p2.y - p1.y);
                    maxArea = Math.max(maxArea, area);
                }
            }
        }

        return maxArea;
    }

    /**
     * Approach 3: Group points by X coordinate, then check Y pairs
     * More efficient when points are clustered
     *
     * Time: O(n² * m) where m = average points per x-coordinate
     * Space: O(n)
     */
    public int maxRectangleAreaV3(int[][] points) {
        if (points == null || points.length < 4) {
            return 0;
        }

        // Group points by x-coordinate
        java.util.Map<Integer, java.util.Set<Integer>> xToYs = new java.util.TreeMap<>();
        for (int[] p : points) {
            xToYs.putIfAbsent(p[0], new java.util.HashSet<>());
            xToYs.get(p[0]).add(p[1]);
        }

        int maxArea = 0;
        java.util.List<Integer> xCoords = new java.util.ArrayList<>(xToYs.keySet());

        // Try all pairs of x-coordinates
        for (int i = 0; i < xCoords.size(); i++) {
            for (int j = i + 1; j < xCoords.size(); j++) {
                int x1 = xCoords.get(i);
                int x2 = xCoords.get(j);

                java.util.Set<Integer> ys1 = xToYs.get(x1);
                java.util.Set<Integer> ys2 = xToYs.get(x2);

                // Find common y-coordinates
                java.util.List<Integer> commonYs = new java.util.ArrayList<>();
                for (int y : ys1) {
                    if (ys2.contains(y)) {
                        commonYs.add(y);
                    }
                }

                // If we have at least 2 common y-coordinates, we can form rectangles
                if (commonYs.size() >= 2) {
                    java.util.Collections.sort(commonYs);

                    // Try all pairs of y-coordinates
                    for (int yi = 0; yi < commonYs.size(); yi++) {
                        for (int yj = yi + 1; yj < commonYs.size(); yj++) {
                            int width = Math.abs(x2 - x1);
                            int height = Math.abs(commonYs.get(yj) - commonYs.get(yi));
                            maxArea = Math.max(maxArea, width * height);
                        }
                    }
                }
            }
        }

        return maxArea;
    }

    /**
     * Return the rectangle coordinates (bottom-left and top-right corners)
     */
    public int[][] findLargestRectangle(int[][] points) {
        if (points == null || points.length < 4) {
            return null;
        }

        java.util.Set<String> pointSet = new java.util.HashSet<>();
        for (int[] point : points) {
            pointSet.add(point[0] + "," + point[1]);
        }

        int maxArea = 0;
        int[] bestP1 = null;
        int[] bestP2 = null;

        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                int x1 = points[i][0];
                int y1 = points[i][1];
                int x2 = points[j][0];
                int y2 = points[j][1];

                if (x1 == x2 || y1 == y2) {
                    continue;
                }

                String corner1 = x1 + "," + y2;
                String corner2 = x2 + "," + y1;

                if (pointSet.contains(corner1) && pointSet.contains(corner2)) {
                    int area = Math.abs(x2 - x1) * Math.abs(y2 - y1);
                    if (area > maxArea) {
                        maxArea = area;
                        bestP1 = new int[]{Math.min(x1, x2), Math.min(y1, y2)};
                        bestP2 = new int[]{Math.max(x1, x2), Math.max(y1, y2)};
                    }
                }
            }
        }

        if (bestP1 == null) {
            return null;
        }

        return new int[][]{bestP1, bestP2};
    }

    // Helper class
    static class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Point)) return false;
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }
    }

    // ==================== Visualization ====================

    public static void visualizePoints(int[][] points, int[][] rectangle) {
        if (points == null || points.length == 0) {
            System.out.println("No points to visualize");
            return;
        }

        // Find bounds
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

        for (int[] p : points) {
            minX = Math.min(minX, p[0]);
            maxX = Math.max(maxX, p[0]);
            minY = Math.min(minY, p[1]);
            maxY = Math.max(maxY, p[1]);
        }

        // Create grid
        java.util.Set<String> pointSet = new java.util.HashSet<>();
        for (int[] p : points) {
            pointSet.add(p[0] + "," + p[1]);
        }

        java.util.Set<String> rectSet = new java.util.HashSet<>();
        if (rectangle != null && rectangle.length == 2) {
            int x1 = rectangle[0][0], y1 = rectangle[0][1];
            int x2 = rectangle[1][0], y2 = rectangle[1][1];
            rectSet.add(x1 + "," + y1);
            rectSet.add(x1 + "," + y2);
            rectSet.add(x2 + "," + y1);
            rectSet.add(x2 + "," + y2);
        }

        // Print grid (y-axis goes top to bottom for display)
        System.out.println("\nVisualization (* = point, # = rectangle corner):");
        for (int y = maxY; y >= minY; y--) {
            System.out.printf("%2d |", y);
            for (int x = minX; x <= maxX; x++) {
                String key = x + "," + y;
                if (rectSet.contains(key)) {
                    System.out.print(" #");
                } else if (pointSet.contains(key)) {
                    System.out.print(" *");
                } else {
                    System.out.print(" .");
                }
            }
            System.out.println();
        }

        System.out.print("   +");
        for (int x = minX; x <= maxX; x++) {
            System.out.print("--");
        }
        System.out.println();

        System.out.print("    ");
        for (int x = minX; x <= maxX; x++) {
            System.out.printf("%2d", x);
        }
        System.out.println();
    }

    // ==================== Test Cases ====================

    public static void main(String[] args) {
        LargestRectangleFromPoints solution = new LargestRectangleFromPoints();

        System.out.println("=== Find Largest Rectangle from Points ===\n");

        // Test 1: Simple square
        System.out.println("Test 1: Simple square");
        int[][] points1 = {{0,0}, {0,1}, {1,0}, {1,1}, {2,2}};
        int area1 = solution.maxRectangleArea(points1);
        int[][] rect1 = solution.findLargestRectangle(points1);
        System.out.println("Points: " + java.util.Arrays.deepToString(points1));
        System.out.println("Max area: " + area1);
        System.out.println("Rectangle: " + java.util.Arrays.deepToString(rect1));
        visualizePoints(points1, rect1);
        System.out.println();

        // Test 2: Larger rectangle
        System.out.println("Test 2: 3x3 rectangle");
        int[][] points2 = {{0,0}, {0,3}, {3,0}, {3,3}, {1,1}, {1,2}, {2,1}, {2,2}};
        int area2 = solution.maxRectangleArea(points2);
        int[][] rect2 = solution.findLargestRectangle(points2);
        System.out.println("Points: " + java.util.Arrays.deepToString(points2));
        System.out.println("Max area: " + area2);
        System.out.println("Rectangle: " + java.util.Arrays.deepToString(rect2));
        visualizePoints(points2, rect2);
        System.out.println();

        // Test 3: Multiple rectangles, find largest
        System.out.println("Test 3: Multiple rectangles");
        int[][] points3 = {{1,1}, {1,3}, {3,1}, {3,3}, {2,2}, {0,0}, {0,5}, {5,0}, {5,5}};
        int area3 = solution.maxRectangleArea(points3);
        int[][] rect3 = solution.findLargestRectangle(points3);
        System.out.println("Points: " + java.util.Arrays.deepToString(points3));
        System.out.println("Max area: " + area3);
        System.out.println("Rectangle: " + java.util.Arrays.deepToString(rect3));
        visualizePoints(points3, rect3);
        System.out.println();

        // Test 4: No rectangle possible
        System.out.println("Test 4: No rectangle possible");
        int[][] points4 = {{0,0}, {1,1}, {2,2}};
        int area4 = solution.maxRectangleArea(points4);
        System.out.println("Points: " + java.util.Arrays.deepToString(points4));
        System.out.println("Max area: " + area4);
        System.out.println("Expected: 0\n");

        // Test 5: Compare all approaches
        System.out.println("Test 5: Compare all approaches");
        int[][] points5 = {{0,0}, {0,2}, {2,0}, {2,2}, {1,1}, {1,3}, {3,1}, {3,3}};
        int area5a = solution.maxRectangleArea(points5);
        int area5b = solution.maxRectangleAreaV2(points5);
        int area5c = solution.maxRectangleAreaV3(points5);
        System.out.println("Approach 1 (HashSet): " + area5a);
        System.out.println("Approach 2 (Point class): " + area5b);
        System.out.println("Approach 3 (Group by X): " + area5c);
        System.out.println("All should be equal: " + (area5a == area5b && area5b == area5c));
        System.out.println();

        // Test 6: Negative coordinates
        System.out.println("Test 6: Negative coordinates");
        int[][] points6 = {{-2,-2}, {-2,2}, {2,-2}, {2,2}, {0,0}};
        int area6 = solution.maxRectangleArea(points6);
        int[][] rect6 = solution.findLargestRectangle(points6);
        System.out.println("Points: " + java.util.Arrays.deepToString(points6));
        System.out.println("Max area: " + area6);
        System.out.println("Rectangle: " + java.util.Arrays.deepToString(rect6));
        visualizePoints(points6, rect6);
        System.out.println();

        // Test 7: Many overlapping rectangles
        System.out.println("Test 7: Grid with many rectangles");
        int[][] points7 = {
            {0,0}, {0,1}, {0,2},
            {1,0}, {1,1}, {1,2},
            {2,0}, {2,1}, {2,2}
        };
        int area7 = solution.maxRectangleArea(points7);
        int[][] rect7 = solution.findLargestRectangle(points7);
        System.out.println("Max area: " + area7);
        System.out.println("Rectangle: " + java.util.Arrays.deepToString(rect7));
        visualizePoints(points7, rect7);
        System.out.println();
    }
}
