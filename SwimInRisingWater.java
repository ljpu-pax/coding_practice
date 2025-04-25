import java.util.PriorityQueue;

public class SwimInRisingWater {
    private static final int[][] DIRECTIONS = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
    
    public int swimInWater(int[][] grid) {
        int n = grid.length;
        boolean[][] visited = new boolean[n][n];
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[2] - b[2]);
        
        // Add the starting point (0, 0) with its elevation as the priority
        pq.offer(new int[]{0, 0, grid[0][0]});
        visited[0][0] = true;
        int maxTime = 0;
        
        while (!pq.isEmpty()) {
            int[] cell = pq.poll();
            int row = cell[0], col = cell[1], time = cell[2];
            maxTime = Math.max(maxTime, time);
            
            // If we reach the bottom-right corner, return the maxTime
            if (row == n - 1 && col == n - 1) {
                return maxTime;
            }
            
            // Explore neighbors
            for (int[] dir : DIRECTIONS) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];
                
                if (newRow >= 0 && newRow < n && newCol >= 0 && newCol < n && !visited[newRow][newCol]) {
                    visited[newRow][newCol] = true;
                    pq.offer(new int[]{newRow, newCol, grid[newRow][newCol]});
                }
            }
        }
        
        return -1; // Should never reach here
    }
    
    public static void main(String[] args) {
        SwimInRisingWater solver = new SwimInRisingWater();
        int[][] grid1 = {
            {0, 2},
            {1, 3}
        };
        System.out.println(solver.swimInWater(grid1)); // Output: 3

        int[][] grid2 = {
            {0, 1, 2, 3},
            {4, 5, 6, 7},
            {8, 9, 10, 11},
            {12, 13, 14, 15}
        };
        System.out.println(solver.swimInWater(grid2)); // Expected: 15

        int[][] grid3 = {
            {0, 2, 1, 3},
            {3, 4, 8, 6},
            {5, 7, 9, 10},
            {11, 12, 13, 14}
        };
        System.out.println(solver.swimInWater(grid3)); // Expected: 11

        int[][] grid4 = {
            {5, 5},
            {5, 5}
        };
        System.out.println(solver.swimInWater(grid4)); // Expected: 5
    }
}

