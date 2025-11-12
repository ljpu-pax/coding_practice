import java.util.*;

/**
 * LeetCode 296: Best Meeting Point (Hard)
 *
 * Given an m x n binary grid where each 1 marks the home of one friend,
 * return the minimal total travel distance.
 *
 * The total travel distance is the sum of the distances between the houses
 * of the friends and the meeting point.
 *
 * The distance is calculated using Manhattan Distance, where
 * distance(p1, p2) = |p2.x - p1.x| + |p2.y - p1.y|.
 *
 * Example 1:
 * Input: grid = [[1,0,0,0,1],
 *                [0,0,0,0,0],
 *                [0,0,1,0,0]]
 * Output: 6
 * Explanation: Given three friends at (0,0), (0,4), and (2,2).
 * The meeting point (0,2) minimizes the total distance to 2 + 2 + 2 = 6.
 *
 * Example 2:
 * Input: grid = [[1,1]]
 * Output: 1
 *
 * Constraints:
 * - m == grid.length
 * - n == grid[i].length
 * - 1 <= m, n <= 200
 * - grid[i][j] is either 0 or 1
 * - There will be at least two friends in the grid
 */
class BestMeetingPoint {

    /**
     * Approach 1: Brute Force - Try all empty positions
     *
     * Time: O((m*n)²) - try each position and calculate distance to all friends
     * Space: O(k) where k = number of friends
     */
    public int minTotalDistanceBruteForce(int[][] grid) {
        List<int[]> friends = new ArrayList<>();

        // Collect all friend positions
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1) {
                    friends.add(new int[]{i, j});
                }
            }
        }

        int minDist = Integer.MAX_VALUE;

        // Try each position as meeting point
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                int dist = calculateDistance(friends, i, j);
                minDist = Math.min(minDist, dist);
            }
        }

        return minDist;
    }

    private int calculateDistance(List<int[]> friends, int row, int col) {
        int total = 0;
        for (int[] friend : friends) {
            total += Math.abs(friend[0] - row) + Math.abs(friend[1] - col);
        }
        return total;
    }

    /**
     * Approach 2: Optimal Solution using Median
     *
     * Key insight: For Manhattan distance, the optimal meeting point is the
     * median of all x-coordinates and median of all y-coordinates.
     *
     * Why median?
     * - For 1D: median minimizes sum of absolute deviations
     * - For 2D Manhattan distance: can separate into x and y independently
     *
     * Time: O(m * n) for collecting points + O(k log k) for sorting
     * Space: O(k) where k = number of friends
     */
    public int minTotalDistance(int[][] grid) {
        List<Integer> rows = new ArrayList<>();
        List<Integer> cols = new ArrayList<>();

        // Collect all x and y coordinates
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1) {
                    rows.add(i);
                    cols.add(j);
                }
            }
        }

        // Find median coordinates
        int medianRow = findMedian(rows);
        int medianCol = findMedian(cols);

        // Calculate total distance
        return calculateDistance(rows, medianRow) + calculateDistance(cols, medianCol);
    }

    private int findMedian(List<Integer> list) {
        Collections.sort(list);
        return list.get(list.size() / 2);
    }

    private int calculateDistance(List<Integer> points, int median) {
        int distance = 0;
        for (int point : points) {
            distance += Math.abs(point - median);
        }
        return distance;
    }

    /**
     * Approach 3: Optimized - Collect in sorted order (no explicit sorting needed)
     *
     * Since we traverse row by row, rows are naturally sorted.
     * For columns, we can collect column by column to get sorted order.
     *
     * Time: O(m * n)
     * Space: O(k)
     */
    public int minTotalDistanceOptimized(int[][] grid) {
        List<Integer> rows = new ArrayList<>();
        List<Integer> cols = new ArrayList<>();

        // Collect rows (already in sorted order)
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1) {
                    rows.add(i);
                }
            }
        }

        // Collect cols in sorted order (iterate column by column)
        for (int j = 0; j < grid[0].length; j++) {
            for (int i = 0; i < grid.length; i++) {
                if (grid[i][j] == 1) {
                    cols.add(j);
                }
            }
        }

        return calculateDistance(rows, rows.get(rows.size() / 2)) +
               calculateDistance(cols, cols.get(cols.size() / 2));
    }

    /**
     * Approach 4: Without finding explicit median
     *
     * We don't actually need to find the median value.
     * We can calculate the sum of distances directly from sorted coordinates.
     */
    public int minTotalDistanceNoMedian(int[][] grid) {
        List<Integer> rows = new ArrayList<>();
        List<Integer> cols = new ArrayList<>();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1) {
                    rows.add(i);
                }
            }
        }

        for (int j = 0; j < grid[0].length; j++) {
            for (int i = 0; i < grid.length; i++) {
                if (grid[i][j] == 1) {
                    cols.add(j);
                }
            }
        }

        return minDistance1D(rows) + minDistance1D(cols);
    }

    private int minDistance1D(List<Integer> points) {
        int distance = 0;
        int i = 0;
        int j = points.size() - 1;

        while (i < j) {
            distance += points.get(j) - points.get(i);
            i++;
            j--;
        }

        return distance;
    }

    /**
     * Return the actual meeting point coordinates
     */
    public int[] findMeetingPoint(int[][] grid) {
        List<Integer> rows = new ArrayList<>();
        List<Integer> cols = new ArrayList<>();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1) {
                    rows.add(i);
                }
            }
        }

        for (int j = 0; j < grid[0].length; j++) {
            for (int i = 0; i < grid.length; i++) {
                if (grid[i][j] == 1) {
                    cols.add(j);
                }
            }
        }

        return new int[]{rows.get(rows.size() / 2), cols.get(cols.size() / 2)};
    }
}

/**
 * Related variations
 */
class BestMeetingPointVariations {
    /**
     * Variation 1: With obstacles (some cells cannot be meeting points)
     */
    public int minTotalDistanceWithObstacles(int[][] grid) {
        // grid[i][j] = 0: empty, 1: friend, 2: obstacle
        List<int[]> friends = new ArrayList<>();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1) {
                    friends.add(new int[]{i, j});
                }
            }
        }

        int minDist = Integer.MAX_VALUE;

        // Try only empty cells
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 0 || grid[i][j] == 1) {
                    int dist = 0;
                    for (int[] friend : friends) {
                        dist += Math.abs(friend[0] - i) + Math.abs(friend[1] - j);
                    }
                    minDist = Math.min(minDist, dist);
                }
            }
        }

        return minDist;
    }

    /**
     * Variation 2: Euclidean distance instead of Manhattan
     */
    public double minTotalDistanceEuclidean(int[][] grid) {
        List<int[]> friends = new ArrayList<>();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1) {
                    friends.add(new int[]{i, j});
                }
            }
        }

        // For Euclidean distance, optimal point is the centroid
        double avgRow = 0, avgCol = 0;
        for (int[] friend : friends) {
            avgRow += friend[0];
            avgCol += friend[1];
        }
        avgRow /= friends.size();
        avgCol /= friends.size();

        // Try points near centroid
        double minDist = Double.MAX_VALUE;
        int centerRow = (int) Math.round(avgRow);
        int centerCol = (int) Math.round(avgCol);

        for (int i = Math.max(0, centerRow - 2); i <= Math.min(grid.length - 1, centerRow + 2); i++) {
            for (int j = Math.max(0, centerCol - 2); j <= Math.min(grid[0].length - 1, centerCol + 2); j++) {
                double dist = 0;
                for (int[] friend : friends) {
                    dist += Math.sqrt(Math.pow(friend[0] - i, 2) + Math.pow(friend[1] - j, 2));
                }
                minDist = Math.min(minDist, dist);
            }
        }

        return minDist;
    }
}

/**
 * Test cases
 */
class BestMeetingPointTest {
    public static void main(String[] args) {
        testBasicCases();
        testAllApproaches();
        testMeetingPointCoordinates();
        testVariations();
    }

    private static void testBasicCases() {
        System.out.println("=== LeetCode 296: Best Meeting Point ===\n");
        BestMeetingPoint solution = new BestMeetingPoint();

        // Test 1
        int[][] grid1 = {
            {1, 0, 0, 0, 1},
            {0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0}
        };
        int result1 = solution.minTotalDistance(grid1);
        System.out.println("Test 1:");
        printGrid(grid1);
        System.out.println("Min total distance: " + result1);
        System.out.println("Expected: 6");
        System.out.println();

        // Test 2
        int[][] grid2 = {{1, 1}};
        int result2 = solution.minTotalDistance(grid2);
        System.out.println("Test 2: Two adjacent friends");
        System.out.println("Min total distance: " + result2);
        System.out.println("Expected: 1");
        System.out.println();
    }

    private static void testAllApproaches() {
        System.out.println("=== Comparing All Approaches ===\n");
        BestMeetingPoint solution = new BestMeetingPoint();

        int[][] grid = {
            {1, 0, 0, 0, 1},
            {0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0}
        };

        long start = System.nanoTime();
        int result1 = solution.minTotalDistance(grid);
        long time1 = System.nanoTime() - start;

        start = System.nanoTime();
        int result2 = solution.minTotalDistanceOptimized(grid);
        long time2 = System.nanoTime() - start;

        start = System.nanoTime();
        int result3 = solution.minTotalDistanceNoMedian(grid);
        long time3 = System.nanoTime() - start;

        System.out.println("Median with sort: " + result1 + " (time: " + time1 + "ns)");
        System.out.println("Optimized (no sort): " + result2 + " (time: " + time2 + "ns)");
        System.out.println("Two pointers: " + result3 + " (time: " + time3 + "ns)");
        System.out.println();
    }

    private static void testMeetingPointCoordinates() {
        System.out.println("=== Finding Meeting Point Coordinates ===\n");
        BestMeetingPoint solution = new BestMeetingPoint();

        int[][] grid = {
            {1, 0, 0, 0, 1},
            {0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0}
        };

        int[] point = solution.findMeetingPoint(grid);
        System.out.println("Grid:");
        printGrid(grid);
        System.out.println("Meeting point: [" + point[0] + ", " + point[1] + "]");
        System.out.println();
    }

    private static void testVariations() {
        System.out.println("=== Testing Variations ===\n");
        BestMeetingPointVariations variations = new BestMeetingPointVariations();

        // With obstacles
        int[][] gridWithObstacles = {
            {1, 0, 0, 0, 1},
            {0, 2, 2, 2, 0},
            {0, 0, 1, 0, 0}
        };

        System.out.println("With obstacles (2 = obstacle):");
        printGrid(gridWithObstacles);
        int result = variations.minTotalDistanceWithObstacles(gridWithObstacles);
        System.out.println("Min distance: " + result);
        System.out.println();
    }

    private static void printGrid(int[][] grid) {
        for (int[] row : grid) {
            for (int cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }
}

/**
 * Key Insights
 */
class BestMeetingPointInsights {
    /*
     * Why Median Works:
     * =================
     * For Manhattan distance, we can separate x and y independently:
     * distance = |x1 - x0| + |y1 - y0|
     *
     * For 1D case: minimize sum of |xi - x|
     * - If x < median: moving right increases distances from left, decreases from right
     * - If x > median: moving left increases distances from right, decreases from left
     * - At median: balanced, optimal
     *
     * For 2D Manhattan:
     * - Apply median independently to x and y coordinates
     * - Meeting point: (median of xs, median of ys)
     *
     * Time Complexity:
     * ================
     * - Brute Force: O((mn)²)
     * - With Sorting: O(mn + k log k) where k = number of friends
     * - Optimized: O(mn) - collect in sorted order
     *
     * Why NOT Centroid (Average)?
     * ============================
     * - Centroid minimizes sum of SQUARED distances (Euclidean)
     * - Median minimizes sum of ABSOLUTE distances (Manhattan)
     * - Manhattan distance is L1 norm, Euclidean is L2 norm
     *
     * Comparison with K Closest Points:
     * ==================================
     * - K Closest: find K nearest points to origin
     * - Best Meeting Point: find point minimizing total distance
     * - Different objectives, different algorithms
     */
}
