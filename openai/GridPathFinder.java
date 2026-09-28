package openai;

import java.util.*;

/**
 * Find Shortest Path Visiting All Target Points in Grid
 *
 * Problem:
 * Given m×n grid with:
 * - '.' = empty
 * - '#' = obstacle
 * - '*' = target (must visit all)
 *
 * Find shortest path that visits all '*' points.
 * - Can start anywhere (not '#')
 * - Can move up/down/left/right
 * - Cannot enter '#'
 * - Can revisit '.' multiple times
 *
 * Return: List of coordinates [(x1,y1), (x2,y2), ...] or empty if impossible
 *
 * Solution Approach:
 * This is a Traveling Salesman Problem (TSP) variant!
 * 1. Find all '*' positions
 * 2. Use BFS to find shortest path between each pair of '*'
 * 3. Try all permutations of visiting order (TSP with bitmask DP)
 * 4. Reconstruct path
 *
 * SIMPLIFIED for interview (40 min):
 * - Use BFS between star points
 * - Try all permutations (works for small number of stars)
 * - For larger grids, use bitmask DP (but harder to code in 40 min)
 *
 * Time: O(k! × m×n) where k = number of stars (brute force)
 *       O(k² × 2^k × m×n) with DP optimization
 * Space: O(m×n)
 */
public class GridPathFinder {

    static class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Point)) return false;
            Point p = (Point) o;
            return x == p.x && y == p.y;
        }

        @Override
        public int hashCode() {
            return x * 1000 + y;
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }
    }

    /**
     * Main method - find shortest path visiting all stars
     */
    public static List<Point> findShortestPath(char[][] grid) {
        if (grid == null || grid.length == 0) return new ArrayList<>();

        int m = grid.length;
        int n = grid[0].length;

        // Find all star positions
        List<Point> stars = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == '*') {
                    stars.add(new Point(i, j));
                }
            }
        }

        if (stars.isEmpty()) return new ArrayList<>();
        if (stars.size() == 1) return Arrays.asList(stars.get(0));

        // Precompute shortest paths between all pairs of stars
        Map<String, List<Point>> paths = new HashMap<>();
        for (int i = 0; i < stars.size(); i++) {
            for (int j = 0; j < stars.size(); j++) {
                if (i != j) {
                    List<Point> path = bfs(grid, stars.get(i), stars.get(j));
                    if (path.isEmpty()) return new ArrayList<>(); // Impossible
                    paths.put(i + "->" + j, path);
                }
            }
        }

        // Try all permutations to find shortest path
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < stars.size(); i++) order.add(i);

        List<Point> bestPath = null;
        int minLength = Integer.MAX_VALUE;

        // Generate all permutations
        List<List<Integer>> perms = permute(order);

        for (List<Integer> perm : perms) {
            List<Point> path = buildPath(perm, stars, paths);
            if (path.size() > 0 && path.size() < minLength) {
                minLength = path.size();
                bestPath = path;
            }
        }

        return bestPath != null ? bestPath : new ArrayList<>();
    }

    /**
     * BFS to find shortest path between two points
     */
    private static List<Point> bfs(char[][] grid, Point start, Point end) {
        int m = grid.length;
        int n = grid[0].length;

        Queue<Point> queue = new LinkedList<>();
        Map<Point, Point> parent = new HashMap<>();
        boolean[][] visited = new boolean[m][n];

        queue.offer(start);
        visited[start.x][start.y] = true;
        parent.put(start, null);

        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        while (!queue.isEmpty()) {
            Point curr = queue.poll();

            if (curr.equals(end)) {
                // Reconstruct path
                List<Point> path = new ArrayList<>();
                Point p = end;
                while (p != null) {
                    path.add(0, p);
                    p = parent.get(p);
                }
                return path;
            }

            for (int[] dir : dirs) {
                int nx = curr.x + dir[0];
                int ny = curr.y + dir[1];

                if (nx >= 0 && nx < m && ny >= 0 && ny < n &&
                    !visited[nx][ny] && grid[nx][ny] != '#') {

                    Point next = new Point(nx, ny);
                    visited[nx][ny] = true;
                    parent.put(next, curr);
                    queue.offer(next);
                }
            }
        }

        return new ArrayList<>(); // No path found
    }

    /**
     * Build complete path from visiting order
     */
    private static List<Point> buildPath(List<Integer> order, List<Point> stars,
                                          Map<String, List<Point>> paths) {
        List<Point> result = new ArrayList<>();

        for (int i = 0; i < order.size() - 1; i++) {
            int from = order.get(i);
            int to = order.get(i + 1);

            List<Point> segment = paths.get(from + "->" + to);
            if (segment.isEmpty()) return new ArrayList<>();

            // Add segment, skip first if not first segment (avoid duplicate)
            int start = (i == 0) ? 0 : 1;
            for (int j = start; j < segment.size(); j++) {
                result.add(segment.get(j));
            }
        }

        return result;
    }

    /**
     * Generate all permutations
     */
    private static List<List<Integer>> permute(List<Integer> nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(result, new ArrayList<>(), nums);
        return result;
    }

    private static void backtrack(List<List<Integer>> result, List<Integer> temp,
                                   List<Integer> nums) {
        if (temp.size() == nums.size()) {
            result.add(new ArrayList<>(temp));
            return;
        }

        for (int num : nums) {
            if (temp.contains(num)) continue;
            temp.add(num);
            backtrack(result, temp, nums);
            temp.remove(temp.size() - 1);
        }
    }

    /**
     * Tests
     */
    public static void main(String[] args) {
        System.out.println("=== Test 1: Simple 3x3 grid ===");
        char[][] grid1 = {
            {'.', '*', '.'},
            {'.', '#', '.'},
            {'*', '.', '*'}
        };
        printGrid(grid1);
        List<Point> path1 = findShortestPath(grid1);
        System.out.println("Path: " + path1);
        System.out.println("Length: " + path1.size());
        System.out.println();

        System.out.println("=== Test 2: 4x4 grid ===");
        char[][] grid2 = {
            {'*', '.', '.', '.'},
            {'.', '#', '#', '.'},
            {'.', '.', '.', '*'},
            {'.', '.', '*', '.'}
        };
        printGrid(grid2);
        List<Point> path2 = findShortestPath(grid2);
        System.out.println("Path: " + path2);
        System.out.println("Length: " + path2.size());
        System.out.println();

        System.out.println("=== Test 3: No path possible ===");
        char[][] grid3 = {
            {'*', '#', '*'},
            {'.', '#', '.'},
            {'.', '.', '.'}
        };
        printGrid(grid3);
        List<Point> path3 = findShortestPath(grid3);
        System.out.println("Path: " + path3);
        System.out.println("Result: " + (path3.isEmpty() ? "No path" : "Found"));
        System.out.println();

        System.out.println("=== Test 4: Single star ===");
        char[][] grid4 = {
            {'.', '.', '.'},
            {'.', '*', '.'},
            {'.', '.', '.'}
        };
        printGrid(grid4);
        List<Point> path4 = findShortestPath(grid4);
        System.out.println("Path: " + path4);
        System.out.println();

        System.out.println("=== Test 5: Two stars next to each other ===");
        char[][] grid5 = {
            {'.', '.', '.'},
            {'*', '*', '.'},
            {'.', '.', '.'}
        };
        printGrid(grid5);
        List<Point> path5 = findShortestPath(grid5);
        System.out.println("Path: " + path5);
        System.out.println("Length: " + path5.size());
    }

    private static void printGrid(char[][] grid) {
        for (char[] row : grid) {
            System.out.println(Arrays.toString(row));
        }
    }
}

/**
 * INTERVIEW STRATEGY (40 minutes):
 * =================================
 *
 * Step 1 (5 min): Understand the problem
 * ---------------------------------------
 * - This is TSP on a grid!
 * - Need to visit all stars in shortest path
 * - Ask: How many stars typically? (if ≤10, brute force OK)
 *
 * Step 2 (10 min): BFS between star pairs
 * ----------------------------------------
 * - Write standard BFS with parent tracking
 * - Return path from start to end
 * - Handle obstacles ('#')
 *
 * Step 3 (15 min): Try all visiting orders
 * -----------------------------------------
 * - Generate all permutations of star order
 * - For each permutation, build complete path
 * - Track shortest path
 *
 * Step 4 (10 min): Test & debug
 * ------------------------------
 * - Test simple cases
 * - Test edge cases (no path, single star)
 *
 * SIMPLIFICATIONS for 40 min:
 * ===========================
 * ✅ Brute force permutations (works for ≤10 stars)
 * ✅ Standard BFS (everyone knows this)
 * ✅ Simple Point class with equals/hashCode
 * ✅ No fancy optimizations (save time!)
 *
 * OPTIMAL SOLUTION (if more time):
 * =================================
 * Use bitmask DP:
 * dp[mask][i] = shortest distance visiting stars in 'mask', ending at star i
 *
 * Transition:
 * dp[mask | (1<<j)][j] = min(dp[mask | (1<<j)][j],
 *                            dp[mask][i] + dist[i][j])
 *
 * But this is complex to code in 40 min!
 *
 * FOLLOW-UP QUESTIONS:
 * ====================
 * Q: "What if there are many stars?"
 * A: Use bitmask DP or approximation algorithms (nearest neighbor)
 *
 * Q: "What if we need to return to start?"
 * A: Classic TSP - add distance from last star back to first
 *
 * Q: "Can we optimize?"
 * A: Yes - use A* instead of BFS, or prune impossible permutations early
 *
 * TIME COMPLEXITY:
 * ================
 * k = number of stars
 * m, n = grid dimensions
 *
 * Current: O(k! × k² × mn) = O(k! × mn) practically
 * With DP: O(k² × 2^k × mn)
 *
 * For k=3: 3! = 6 permutations ✅
 * For k=5: 5! = 120 permutations ✅
 * For k=10: 10! = 3.6M permutations (slow but works)
 */
