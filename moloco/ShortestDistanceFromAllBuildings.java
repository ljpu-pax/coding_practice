import java.util.*;

/**
 * LeetCode 317: Shortest Distance from All Buildings
 *
 * You are given an m x n grid where each cell is one of:
 * - 0 represents an empty land that you can pass through
 * - 1 represents a building that you cannot pass through
 * - 2 represents an obstacle that you cannot pass through
 *
 * You want to build a house on an empty land that reaches all buildings in the shortest total travel
 * distance. You can only move up, down, left, and right.
 *
 * Return the shortest travel distance for such a house. If it is not possible to build such a house
 * according to the above rules, return -1.
 *
 * The total travel distance is the sum of the distances between the houses of the friends and the meeting point.
 * The distance is calculated using Manhattan Distance, where distance(p1, p2) = |p2.x - p1.x| + |p2.y - p1.y|.
 *
 * Example 1:
 * Input: grid = [[1,0,2,0,1],[0,0,0,0,0],[0,0,1,0,0]]
 * Output: 7
 * Explanation: Given three buildings at (0,0), (0,4), and (2,2), and an obstacle at (0,2).
 * The point (1,2) is an ideal empty land to build a house, as the total travel distance of 3+3+1=7 is minimal.
 * So return 7.
 *
 * Example 2:
 * Input: grid = [[1,0]]
 * Output: 1
 *
 * Example 3:
 * Input: grid = [[1]]
 * Output: -1
 *
 * Constraints:
 * - m == grid.length
 * - n == grid[i].length
 * - 1 <= m, n <= 50
 * - grid[i][j] is either 0, 1, or 2.
 * - There will be at least one building in the grid.
 */
class ShortestDistanceFromAllBuildings {
    /**
     * Approach 1: BFS from Each Building
     *
     * For each building, do BFS to calculate distances to all reachable empty lands.
     * Track which empty lands can reach all buildings.
     *
     * Time: O(m * n * B) where B = number of buildings
     * Space: O(m * n)
     */
    public int shortestDistance(int[][] grid) {
        if (grid == null || grid.length == 0) {
            return -1;
        }

        int m = grid.length;
        int n = grid[0].length;

        // Count total buildings
        int buildingCount = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    buildingCount++;
                }
            }
        }

        // Distance sum for each empty land
        int[][] distanceSum = new int[m][n];
        // Number of buildings reached from each empty land
        int[][] reachCount = new int[m][n];

        // BFS from each building
        int buildingIndex = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    bfs(grid, i, j, distanceSum, reachCount, buildingIndex);
                    buildingIndex++;
                }
            }
        }

        // Find minimum distance
        int minDistance = Integer.MAX_VALUE;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 0 && reachCount[i][j] == buildingCount) {
                    minDistance = Math.min(minDistance, distanceSum[i][j]);
                }
            }
        }

        return minDistance == Integer.MAX_VALUE ? -1 : minDistance;
    }

    private void bfs(int[][] grid, int startI, int startJ,
                     int[][] distanceSum, int[][] reachCount, int buildingIndex) {
        int m = grid.length;
        int n = grid[0].length;

        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[m][n];

        queue.offer(new int[]{startI, startJ, 0}); // {row, col, distance}
        visited[startI][startJ] = true;

        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int i = curr[0];
            int j = curr[1];
            int dist = curr[2];

            for (int[] dir : directions) {
                int ni = i + dir[0];
                int nj = j + dir[1];

                // Check bounds and if not visited
                if (ni >= 0 && ni < m && nj >= 0 && nj < n &&
                    !visited[ni][nj] && grid[ni][nj] == 0) {

                    visited[ni][nj] = true;
                    distanceSum[ni][nj] += dist + 1;
                    reachCount[ni][nj]++;
                    queue.offer(new int[]{ni, nj, dist + 1});
                }
            }
        }
    }

    /**
     * Approach 2: Optimized BFS with Early Termination
     *
     * Optimize by marking visited cells to avoid revisiting.
     * Use a decreasing marker to track which cells can be visited.
     *
     * Time: O(m * n * B)
     * Space: O(m * n)
     */
    public int shortestDistanceOptimized(int[][] grid) {
        if (grid == null || grid.length == 0) {
            return -1;
        }

        int m = grid.length;
        int n = grid[0].length;
        int[][] distanceSum = new int[m][n];

        int emptyLandValue = 0; // Start with 0, decrease for each building
        int minDistance = Integer.MAX_VALUE;

        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        // BFS from each building
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    minDistance = Integer.MAX_VALUE;

                    Queue<int[]> queue = new LinkedList<>();
                    queue.offer(new int[]{i, j, 0});

                    while (!queue.isEmpty()) {
                        int[] curr = queue.poll();
                        int row = curr[0];
                        int col = curr[1];
                        int dist = curr[2];

                        for (int[] dir : directions) {
                            int nr = row + dir[0];
                            int nc = col + dir[1];

                            // Only visit cells with current emptyLandValue
                            if (nr >= 0 && nr < m && nc >= 0 && nc < n &&
                                grid[nr][nc] == emptyLandValue) {

                                grid[nr][nc]--; // Mark as visited for this building
                                distanceSum[nr][nc] += dist + 1;
                                minDistance = Math.min(minDistance, distanceSum[nr][nc]);
                                queue.offer(new int[]{nr, nc, dist + 1});
                            }
                        }
                    }

                    emptyLandValue--; // Decrease for next building
                }
            }
        }

        return minDistance == Integer.MAX_VALUE ? -1 : minDistance;
    }

    /**
     * Approach 3: BFS from Empty Lands (Less efficient)
     *
     * For each empty land, do BFS to check if it can reach all buildings.
     *
     * Time: O(m * n * m * n) - worse than approach 1
     * Space: O(m * n)
     */
    public int shortestDistanceFromLands(int[][] grid) {
        if (grid == null || grid.length == 0) {
            return -1;
        }

        int m = grid.length;
        int n = grid[0].length;

        // Count total buildings
        int totalBuildings = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    totalBuildings++;
                }
            }
        }

        int minDistance = Integer.MAX_VALUE;

        // Try each empty land
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 0) {
                    int distance = bfsFromLand(grid, i, j, totalBuildings);
                    if (distance != -1) {
                        minDistance = Math.min(minDistance, distance);
                    }
                }
            }
        }

        return minDistance == Integer.MAX_VALUE ? -1 : minDistance;
    }

    private int bfsFromLand(int[][] grid, int startI, int startJ, int totalBuildings) {
        int m = grid.length;
        int n = grid[0].length;

        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[m][n];

        queue.offer(new int[]{startI, startJ, 0});
        visited[startI][startJ] = true;

        int buildingsReached = 0;
        int totalDistance = 0;
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int i = curr[0];
            int j = curr[1];
            int dist = curr[2];

            // Check if we reached a building
            if (grid[i][j] == 1) {
                buildingsReached++;
                totalDistance += dist;
                continue; // Don't explore beyond buildings
            }

            for (int[] dir : directions) {
                int ni = i + dir[0];
                int nj = j + dir[1];

                if (ni >= 0 && ni < m && nj >= 0 && nj < n &&
                    !visited[ni][nj] && grid[ni][nj] != 2) {

                    visited[ni][nj] = true;
                    queue.offer(new int[]{ni, nj, dist + 1});
                }
            }
        }

        return buildingsReached == totalBuildings ? totalDistance : -1;
    }
}

/**
 * Variations and related problems
 */
class ShortestDistanceVariations {
    /**
     * Find the actual meeting point coordinates, not just distance
     */
    public int[] findMeetingPoint(int[][] grid) {
        if (grid == null || grid.length == 0) {
            return new int[]{-1, -1};
        }

        int m = grid.length;
        int n = grid[0].length;

        int buildingCount = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    buildingCount++;
                }
            }
        }

        int[][] distanceSum = new int[m][n];
        int[][] reachCount = new int[m][n];

        int buildingIndex = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    bfs(grid, i, j, distanceSum, reachCount);
                    buildingIndex++;
                }
            }
        }

        int minDistance = Integer.MAX_VALUE;
        int[] result = {-1, -1};

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 0 && reachCount[i][j] == buildingCount) {
                    if (distanceSum[i][j] < minDistance) {
                        minDistance = distanceSum[i][j];
                        result[0] = i;
                        result[1] = j;
                    }
                }
            }
        }

        return result;
    }

    private void bfs(int[][] grid, int startI, int startJ,
                     int[][] distanceSum, int[][] reachCount) {
        int m = grid.length;
        int n = grid[0].length;

        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[m][n];

        queue.offer(new int[]{startI, startJ, 0});
        visited[startI][startJ] = true;

        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int i = curr[0];
            int j = curr[1];
            int dist = curr[2];

            for (int[] dir : directions) {
                int ni = i + dir[0];
                int nj = j + dir[1];

                if (ni >= 0 && ni < m && nj >= 0 && nj < n &&
                    !visited[ni][nj] && grid[ni][nj] == 0) {

                    visited[ni][nj] = true;
                    distanceSum[ni][nj] += dist + 1;
                    reachCount[ni][nj]++;
                    queue.offer(new int[]{ni, nj, dist + 1});
                }
            }
        }
    }

    /**
     * Get all valid meeting points with their distances
     */
    public List<int[]> getAllValidPoints(int[][] grid) {
        List<int[]> result = new ArrayList<>();

        if (grid == null || grid.length == 0) {
            return result;
        }

        int m = grid.length;
        int n = grid[0].length;

        int buildingCount = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    buildingCount++;
                }
            }
        }

        int[][] distanceSum = new int[m][n];
        int[][] reachCount = new int[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    bfs(grid, i, j, distanceSum, reachCount);
                }
            }
        }

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 0 && reachCount[i][j] == buildingCount) {
                    result.add(new int[]{i, j, distanceSum[i][j]});
                }
            }
        }

        // Sort by distance
        result.sort((a, b) -> a[2] - b[2]);
        return result;
    }
}

/**
 * Test cases
 */
class ShortestDistanceFromAllBuildingsTest {
    public static void main(String[] args) {
        testBasicCases();
        testEdgeCases();
        testVariations();
    }

    private static void testBasicCases() {
        System.out.println("=== Testing LeetCode 317: Shortest Distance from All Buildings ===\n");
        ShortestDistanceFromAllBuildings solution = new ShortestDistanceFromAllBuildings();

        // Test 1
        int[][] grid1 = {
            {1, 0, 2, 0, 1},
            {0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0}
        };
        int result1 = solution.shortestDistance(copyGrid(grid1));
        int result1Opt = solution.shortestDistanceOptimized(copyGrid(grid1));
        int result1Land = solution.shortestDistanceFromLands(copyGrid(grid1));
        System.out.println("Test 1: 3 buildings with obstacle");
        System.out.println("  BFS from buildings: " + result1 + " (Expected: 7) - " +
                          (result1 == 7 ? "PASS" : "FAIL"));
        System.out.println("  Optimized: " + result1Opt + " (Expected: 7) - " +
                          (result1Opt == 7 ? "PASS" : "FAIL"));
        System.out.println("  BFS from lands: " + result1Land + " (Expected: 7) - " +
                          (result1Land == 7 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 2
        int[][] grid2 = {{1, 0}};
        int result2 = solution.shortestDistance(copyGrid(grid2));
        System.out.println("Test 2: Single building");
        System.out.println("  Result: " + result2 + " (Expected: 1) - " +
                          (result2 == 1 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 3
        int[][] grid3 = {{1}};
        int result3 = solution.shortestDistance(copyGrid(grid3));
        System.out.println("Test 3: Only building, no empty land");
        System.out.println("  Result: " + result3 + " (Expected: -1) - " +
                          (result3 == -1 ? "PASS" : "FAIL"));
        System.out.println();
    }

    private static void testEdgeCases() {
        System.out.println("=== Testing Edge Cases ===\n");
        ShortestDistanceFromAllBuildings solution = new ShortestDistanceFromAllBuildings();

        // Test 1: Unreachable building
        int[][] grid1 = {
            {1, 2, 0},
            {2, 2, 2},
            {0, 0, 1}
        };
        int result1 = solution.shortestDistance(copyGrid(grid1));
        System.out.println("Test 1: Unreachable building (separated by obstacles)");
        System.out.println("  Result: " + result1 + " (Expected: -1) - " +
                          (result1 == -1 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 2: All empty land
        int[][] grid2 = {
            {0, 0, 0},
            {0, 1, 0},
            {0, 0, 0}
        };
        int result2 = solution.shortestDistance(copyGrid(grid2));
        System.out.println("Test 2: Building in center, all sides accessible");
        System.out.println("  Result: " + result2 + " (Expected: 1)");
        System.out.println();

        // Test 3: Two buildings in corners
        int[][] grid3 = {
            {1, 0, 0, 0, 1},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0}
        };
        int result3 = solution.shortestDistance(copyGrid(grid3));
        System.out.println("Test 3: Two buildings in opposite corners");
        System.out.println("  Result: " + result3 + " (Expected: 4)");
        System.out.println();

        // Test 4: Multiple optimal points
        int[][] grid4 = {
            {1, 0, 1},
            {0, 0, 0},
            {1, 0, 1}
        };
        int result4 = solution.shortestDistance(copyGrid(grid4));
        System.out.println("Test 4: Four buildings in corners (center is optimal)");
        System.out.println("  Result: " + result4);
        System.out.println();
    }

    private static void testVariations() {
        System.out.println("=== Testing Variations ===\n");
        ShortestDistanceVariations variations = new ShortestDistanceVariations();

        // Test 1: Find meeting point
        int[][] grid1 = {
            {1, 0, 2, 0, 1},
            {0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0}
        };
        int[] meetingPoint = variations.findMeetingPoint(copyGrid(grid1));
        System.out.println("Test 1: Find meeting point coordinates");
        System.out.println("  Result: [" + meetingPoint[0] + ", " + meetingPoint[1] + "]" +
                          " (Expected: [1, 2])");
        System.out.println();

        // Test 2: Get all valid points
        int[][] grid2 = {
            {1, 0, 0, 0, 1},
            {0, 0, 0, 0, 0}
        };
        List<int[]> validPoints = variations.getAllValidPoints(copyGrid(grid2));
        System.out.println("Test 2: Get all valid meeting points with distances");
        System.out.println("  Number of valid points: " + validPoints.size());
        for (int[] point : validPoints) {
            System.out.println("    [" + point[0] + ", " + point[1] + "] distance = " + point[2]);
        }
        System.out.println();
    }

    private static int[][] copyGrid(int[][] grid) {
        int[][] copy = new int[grid.length][];
        for (int i = 0; i < grid.length; i++) {
            copy[i] = grid[i].clone();
        }
        return copy;
    }
}

/**
 * Follow-up Questions
 */
class ShortestDistanceFollowUps {
    /**
     * Follow-up 1: Minimize the MAXIMUM distance from meeting point to any building
     * (instead of minimizing the SUM of distances)
     *
     * This is also known as the minimax problem or finding the "center" that minimizes
     * the worst-case distance.
     */
    public int shortestMaxDistance(int[][] grid) {
        if (grid == null || grid.length == 0) {
            return -1;
        }

        int m = grid.length;
        int n = grid[0].length;

        // Count total buildings
        int buildingCount = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    buildingCount++;
                }
            }
        }

        // Track MAX distance from each empty land to any building
        int[][] maxDistance = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                maxDistance[i][j] = Integer.MIN_VALUE;
            }
        }

        // Number of buildings reached from each empty land
        int[][] reachCount = new int[m][n];

        // BFS from each building
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    bfsMaxDistance(grid, i, j, maxDistance, reachCount);
                }
            }
        }

        // Find minimum of the maximum distances
        int minMaxDistance = Integer.MAX_VALUE;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 0 && reachCount[i][j] == buildingCount) {
                    minMaxDistance = Math.min(minMaxDistance, maxDistance[i][j]);
                }
            }
        }

        return minMaxDistance == Integer.MAX_VALUE ? -1 : minMaxDistance;
    }

    private void bfsMaxDistance(int[][] grid, int startI, int startJ,
                                int[][] maxDistance, int[][] reachCount) {
        int m = grid.length;
        int n = grid[0].length;

        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[m][n];

        queue.offer(new int[]{startI, startJ, 0}); // {row, col, distance}
        visited[startI][startJ] = true;

        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int i = curr[0];
            int j = curr[1];
            int dist = curr[2];

            for (int[] dir : directions) {
                int ni = i + dir[0];
                int nj = j + dir[1];

                // Check bounds and if not visited
                if (ni >= 0 && ni < m && nj >= 0 && nj < n &&
                    !visited[ni][nj] && grid[ni][nj] == 0) {

                    visited[ni][nj] = true;
                    // KEY CHANGE: Track MAX distance instead of SUM
                    maxDistance[ni][nj] = Math.max(maxDistance[ni][nj], dist + 1);
                    reachCount[ni][nj]++;
                    queue.offer(new int[]{ni, nj, dist + 1});
                }
            }
        }
    }

    /**
     * Follow-up 2: Use an arbitrary distance function instead of grid-based BFS
     *
     * Instead of assuming Manhattan distance on a grid, use a custom distance function
     * that can represent any metric (Euclidean, weighted edges, etc.)
     */

    // Point class for representing coordinates
    static class Point {
        int x, y;
        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    // Distance function interface
    interface DistanceFunction {
        int getDistance(Point p1, Point p2);
    }

    /**
     * Find shortest sum distance using arbitrary distance function
     */
    public int shortestDistanceWithFunction(int[][] grid, DistanceFunction distFunc) {
        if (grid == null || grid.length == 0) {
            return -1;
        }

        int m = grid.length;
        int n = grid[0].length;

        // Collect all building positions
        List<Point> buildings = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    buildings.add(new Point(i, j));
                }
            }
        }

        if (buildings.isEmpty()) {
            return -1;
        }

        // Try each empty land
        int minDistance = Integer.MAX_VALUE;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 0) {
                    Point candidate = new Point(i, j);
                    int totalDistance = 0;

                    // KEY CHANGE: Use custom distance function instead of BFS
                    for (Point building : buildings) {
                        totalDistance += distFunc.getDistance(building, candidate);
                    }

                    minDistance = Math.min(minDistance, totalDistance);
                }
            }
        }

        return minDistance == Integer.MAX_VALUE ? -1 : minDistance;
    }

    /**
     * Example distance functions
     */

    // Manhattan distance (same as grid BFS)
    static class ManhattanDistance implements DistanceFunction {
        @Override
        public int getDistance(Point p1, Point p2) {
            return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
        }
    }

    // Euclidean distance (straight line)
    static class EuclideanDistance implements DistanceFunction {
        @Override
        public int getDistance(Point p1, Point p2) {
            int dx = p1.x - p2.x;
            int dy = p1.y - p2.y;
            return (int) Math.sqrt(dx * dx + dy * dy);
        }
    }

    // Chebyshev distance (chess king moves)
    static class ChebyshevDistance implements DistanceFunction {
        @Override
        public int getDistance(Point p1, Point p2) {
            return Math.max(Math.abs(p1.x - p2.x), Math.abs(p1.y - p2.y));
        }
    }

    // Custom weighted distance (e.g., different costs for horizontal vs vertical)
    static class WeightedDistance implements DistanceFunction {
        private int horizontalCost;
        private int verticalCost;

        WeightedDistance(int horizontalCost, int verticalCost) {
            this.horizontalCost = horizontalCost;
            this.verticalCost = verticalCost;
        }

        @Override
        public int getDistance(Point p1, Point p2) {
            int dx = Math.abs(p1.x - p2.x);
            int dy = Math.abs(p1.y - p2.y);
            return dx * horizontalCost + dy * verticalCost;
        }
    }
}

/**
 * Test cases for follow-up questions
 */
class ShortestDistanceFollowUpsTest {
    public static void main(String[] args) {
        testMinimizeMaxDistance();
        testArbitraryDistanceFunction();
    }

    private static void testMinimizeMaxDistance() {
        System.out.println("=== Follow-up 1: Minimize MAX distance (minimax) ===\n");
        ShortestDistanceFollowUps solution = new ShortestDistanceFollowUps();

        // Test 1: Basic case
        int[][] grid1 = {
            {1, 0, 2, 0, 1},
            {0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0}
        };
        int result1 = solution.shortestMaxDistance(copyGrid(grid1));
        System.out.println("Test 1: 3 buildings with obstacle");
        System.out.println("  Minimize MAX distance: " + result1);
        System.out.println("  (Compare to sum distance: 7)");
        System.out.println();

        // Test 2: Two buildings in corners
        int[][] grid2 = {
            {1, 0, 0, 0, 1},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0}
        };
        int result2 = solution.shortestMaxDistance(copyGrid(grid2));
        System.out.println("Test 2: Two buildings in opposite corners");
        System.out.println("  Minimize MAX distance: " + result2);
        System.out.println("  (Center point [0,2] or [1,2] has max distance 2 to either building)");
        System.out.println();

        // Test 3: Four buildings in corners
        int[][] grid3 = {
            {1, 0, 1},
            {0, 0, 0},
            {1, 0, 1}
        };
        int result3 = solution.shortestMaxDistance(copyGrid(grid3));
        System.out.println("Test 3: Four buildings in corners");
        System.out.println("  Minimize MAX distance: " + result3);
        System.out.println("  (Center point [1,1] has max distance 2 to any corner)");
        System.out.println();
    }

    private static void testArbitraryDistanceFunction() {
        System.out.println("=== Follow-up 2: Arbitrary distance function ===\n");
        ShortestDistanceFollowUps solution = new ShortestDistanceFollowUps();

        int[][] grid = {
            {1, 0, 0, 0, 1},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0}
        };

        // Test with Manhattan distance
        int manhattanResult = solution.shortestDistanceWithFunction(
            copyGrid(grid),
            new ShortestDistanceFollowUps.ManhattanDistance()
        );
        System.out.println("Test 1: Manhattan distance (L1 norm)");
        System.out.println("  Result: " + manhattanResult);
        System.out.println();

        // Test with Euclidean distance
        int euclideanResult = solution.shortestDistanceWithFunction(
            copyGrid(grid),
            new ShortestDistanceFollowUps.EuclideanDistance()
        );
        System.out.println("Test 2: Euclidean distance (L2 norm)");
        System.out.println("  Result: " + euclideanResult);
        System.out.println();

        // Test with Chebyshev distance
        int chebyshevResult = solution.shortestDistanceWithFunction(
            copyGrid(grid),
            new ShortestDistanceFollowUps.ChebyshevDistance()
        );
        System.out.println("Test 3: Chebyshev distance (L-infinity norm)");
        System.out.println("  Result: " + chebyshevResult);
        System.out.println();

        // Test with weighted distance
        int weightedResult = solution.shortestDistanceWithFunction(
            copyGrid(grid),
            new ShortestDistanceFollowUps.WeightedDistance(2, 1) // horizontal costs 2x vertical
        );
        System.out.println("Test 4: Weighted distance (horizontal=2, vertical=1)");
        System.out.println("  Result: " + weightedResult);
        System.out.println();
    }

    private static int[][] copyGrid(int[][] grid) {
        int[][] copy = new int[grid.length][];
        for (int i = 0; i < grid.length; i++) {
            copy[i] = grid[i].clone();
        }
        return copy;
    }
}
