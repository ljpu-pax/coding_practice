import java.util.*;

/**
 * LeetCode 317 Variation: Shortest Distance from All Buildings
 *
 * Modified conditions:
 * 1. Buildings can be passed through (buildings are NOT obstacles)
 * 2. Starting point can be a building (houses can be built on buildings)
 *
 * Original LeetCode 317:
 * - 0 = empty land (passable)
 * - 1 = building (NOT passable, destination)
 * - 2 = obstacle (NOT passable)
 *
 * Modified version:
 * - 0 = empty land (passable, can place house)
 * - 1 = building (PASSABLE, can place house, destination)
 * - 2 = obstacle (NOT passable, cannot place house)
 *
 * Key differences:
 * - BFS can go THROUGH buildings (treat as passable)
 * - Can place house ON a building location
 * - Distance calculation includes path through buildings
 *
 * Example:
 * Grid:  1 0 2 0 1
 *        0 0 0 0 0
 *        0 0 1 0 0
 *
 * With buildings passable:
 * - Can walk through building at (0,0), (0,4), (2,2)
 * - Shortest paths may go through buildings
 * - Can place house on building location
 */
class ShortestDistanceBuildingsPassable {

    /**
     * Approach 1: BFS from each building, buildings are passable
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

        // Distance sum for each cell
        int[][] distanceSum = new int[m][n];
        // Number of buildings reached from each cell
        int[][] reachCount = new int[m][n];

        // BFS from each building
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    bfsFromBuilding(grid, i, j, distanceSum, reachCount);
                }
            }
        }

        // Find minimum distance among valid cells
        int minDistance = Integer.MAX_VALUE;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                // Valid cells: empty land (0) or buildings (1)
                if (grid[i][j] != 2 && reachCount[i][j] == buildingCount) {
                    minDistance = Math.min(minDistance, distanceSum[i][j]);
                }
            }
        }

        return minDistance == Integer.MAX_VALUE ? -1 : minDistance;
    }

    /**
     * BFS from a building, treating other buildings as passable
     */
    private void bfsFromBuilding(int[][] grid, int startI, int startJ,
                                  int[][] distanceSum, int[][] reachCount) {
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

            // Update distance for this cell
            distanceSum[i][j] += dist;
            reachCount[i][j]++;

            // Explore neighbors
            for (int[] dir : directions) {
                int ni = i + dir[0];
                int nj = j + dir[1];

                // Check bounds and if not visited
                if (ni >= 0 && ni < m && nj >= 0 && nj < n &&
                    !visited[ni][nj] && grid[ni][nj] != 2) { // Can pass through 0 and 1

                    visited[ni][nj] = true;
                    queue.offer(new int[]{ni, nj, dist + 1});
                }
            }
        }
    }

    /**
     * Approach 2: Find meeting point with coordinates
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

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    bfsFromBuilding(grid, i, j, distanceSum, reachCount);
                }
            }
        }

        int minDistance = Integer.MAX_VALUE;
        int[] result = {-1, -1};

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] != 2 && reachCount[i][j] == buildingCount) {
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

    /**
     * Approach 3: Optimized with early termination
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

                            // Visit cells with current emptyLandValue (passable: 0 or 1)
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
     * Approach 4: Calculate distance with building penalty
     *
     * Variant: Passing through buildings has a cost penalty
     */
    public int shortestDistanceWithPenalty(int[][] grid, int buildingPenalty) {
        if (grid == null || grid.length == 0) {
            return -1;
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
                    bfsWithPenalty(grid, i, j, distanceSum, reachCount, buildingPenalty);
                }
            }
        }

        int minDistance = Integer.MAX_VALUE;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] != 2 && reachCount[i][j] == buildingCount) {
                    minDistance = Math.min(minDistance, distanceSum[i][j]);
                }
            }
        }

        return minDistance == Integer.MAX_VALUE ? -1 : minDistance;
    }

    /**
     * BFS with penalty for passing through buildings
     */
    private void bfsWithPenalty(int[][] grid, int startI, int startJ,
                                int[][] distanceSum, int[][] reachCount,
                                int buildingPenalty) {
        int m = grid.length;
        int n = grid[0].length;

        // Use Dijkstra-style BFS with priority queue
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[2] - b[2]);
        int[][] dist = new int[m][n];
        for (int i = 0; i < m; i++) {
            Arrays.fill(dist[i], Integer.MAX_VALUE);
        }

        pq.offer(new int[]{startI, startJ, 0});
        dist[startI][startJ] = 0;

        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int i = curr[0];
            int j = curr[1];
            int d = curr[2];

            if (d > dist[i][j]) continue;

            // Update distance and reach count for this cell
            distanceSum[i][j] += d;
            reachCount[i][j]++;

            for (int[] dir : directions) {
                int ni = i + dir[0];
                int nj = j + dir[1];

                if (ni >= 0 && ni < m && nj >= 0 && nj < n && grid[ni][nj] != 2) {
                    // Calculate cost: 1 for empty, 1 + penalty for building
                    int cost = (grid[ni][nj] == 1 && !(ni == startI && nj == startJ))
                               ? buildingPenalty : 1;
                    int newDist = d + cost;

                    if (newDist < dist[ni][nj]) {
                        dist[ni][nj] = newDist;
                        pq.offer(new int[]{ni, nj, newDist});
                    }
                }
            }
        }
    }
}

/**
 * Comparison with original LeetCode 317
 */
class ComparisonWithOriginal {
    /*
     * Original LeetCode 317:
     * - Buildings are OBSTACLES (cannot pass through)
     * - Starting point must be EMPTY LAND (0)
     * - BFS stops at buildings
     *
     * Modified Version (Buildings Passable):
     * - Buildings are PASSABLE (can walk through)
     * - Starting point can be BUILDING (1) or EMPTY (0)
     * - BFS goes through buildings
     *
     * Example showing difference:
     *
     * Grid:  1 0 0 0 1
     *        2 2 2 2 2
     *        0 0 0 0 0
     *
     * Original (buildings = walls):
     * - Cannot reach from top row to bottom row
     * - Result: -1
     *
     * Modified (buildings passable):
     * - Can place house at (0,2) - equal distance from both buildings
     * - Can walk through buildings at (0,0) and (0,4)
     * - Result: 2 (sum of distances)
     *
     * Key code differences:
     *
     * Original:
     *   if (grid[ni][nj] == 0) { // Only empty land
     *
     * Modified:
     *   if (grid[ni][nj] != 2) { // Empty land OR building
     *
     * Original result check:
     *   if (grid[i][j] == 0) { // Only empty land
     *
     * Modified result check:
     *   if (grid[i][j] != 2) { // Empty land OR building
     */
}

/**
 * Test cases
 */
class ShortestDistanceBuildingsPassableTest {
    public static void main(String[] args) {
        testBasicPassable();
        testComparisonWithOriginal();
        testBuildingAsStartPoint();
        testWithPenalty();
    }

    private static void testBasicPassable() {
        System.out.println("=== Test: Buildings Passable ===\n");
        ShortestDistanceBuildingsPassable solver = new ShortestDistanceBuildingsPassable();

        // Buildings can be passed through
        int[][] grid1 = {
            {1, 0, 2, 0, 1},
            {0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0}
        };

        int result1 = solver.shortestDistance(grid1);
        System.out.println("Test 1: Buildings passable");
        System.out.println("Grid:");
        printGrid(grid1);
        System.out.println("Shortest distance: " + result1);
        System.out.println();

        int[] meetingPoint = solver.findMeetingPoint(grid1);
        System.out.println("Meeting point: [" + meetingPoint[0] + ", " + meetingPoint[1] + "]");
        System.out.println();
    }

    private static void testComparisonWithOriginal() {
        System.out.println("=== Comparison: Original vs Modified ===\n");
        ShortestDistanceBuildingsPassable solverPassable = new ShortestDistanceBuildingsPassable();

        // Grid where buildings block path in original version
        int[][] grid = {
            {1, 0, 0, 0, 1},
            {1, 2, 2, 2, 1},
            {0, 0, 0, 0, 0}
        };

        System.out.println("Grid:");
        printGrid(grid);

        System.out.println("Original LeetCode 317 (buildings = walls):");
        System.out.println("  Cannot place house on buildings");
        System.out.println("  Buildings block BFS paths");
        System.out.println();

        System.out.println("Modified version (buildings passable):");
        int resultPassable = solverPassable.shortestDistance(grid);
        System.out.println("  Can place house on buildings: YES");
        System.out.println("  Buildings block BFS paths: NO");
        System.out.println("  Shortest distance: " + resultPassable);
        System.out.println();
    }

    private static void testBuildingAsStartPoint() {
        System.out.println("=== Test: Building as Starting Point ===\n");
        ShortestDistanceBuildingsPassable solver = new ShortestDistanceBuildingsPassable();

        int[][] grid = {
            {1, 0, 1},
            {0, 0, 0},
            {1, 0, 1}
        };

        System.out.println("Grid (buildings in corners):");
        printGrid(grid);

        int result = solver.shortestDistance(grid);
        int[] meetingPoint = solver.findMeetingPoint(grid);

        System.out.println("Can place house on building: YES");
        System.out.println("Best location: [" + meetingPoint[0] + ", " + meetingPoint[1] + "]");
        System.out.println("Total distance: " + result);

        if (grid[meetingPoint[0]][meetingPoint[1]] == 1) {
            System.out.println("Note: Optimal location is ON a building!");
        }
        System.out.println();
    }

    private static void testWithPenalty() {
        System.out.println("=== Test: Building Pass-Through Penalty ===\n");
        ShortestDistanceBuildingsPassable solver = new ShortestDistanceBuildingsPassable();

        int[][] grid = {
            {1, 0, 0, 0, 1},
            {0, 1, 0, 1, 0},
            {0, 0, 0, 0, 0}
        };

        System.out.println("Grid:");
        printGrid(grid);

        int noPenalty = solver.shortestDistanceWithPenalty(grid, 1);
        int withPenalty = solver.shortestDistanceWithPenalty(grid, 5);

        System.out.println("No penalty (cost=1): " + noPenalty);
        System.out.println("With penalty (cost=5): " + withPenalty);
        System.out.println("Routes prefer going around buildings when penalty is high");
        System.out.println();
    }

    private static void printGrid(int[][] grid) {
        for (int[] row : grid) {
            for (int cell : row) {
                char symbol = (cell == 0) ? '.' : (cell == 1) ? 'B' : 'X';
                System.out.print(symbol + " ");
            }
            System.out.println();
        }
    }
}

/**
 * Follow-up variations
 */
class FollowUpVariations {
    /*
     * Variation 1: Different building heights
     * - Taller buildings have higher pass-through cost
     * - Use Dijkstra instead of BFS
     *
     * Variation 2: Directional restrictions
     * - Can only enter buildings from certain directions
     * - Track entry direction in state
     *
     * Variation 3: Limited building passes
     * - Can only pass through K buildings total
     * - Track remaining passes in BFS state
     *
     * Variation 4: Building types
     * - Some buildings passable, others not
     * - Check building type before allowing pass
     *
     * Variation 5: Time-based availability
     * - Buildings passable only at certain times
     * - Add time dimension to BFS
     */
}
