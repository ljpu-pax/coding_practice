package doordash;
import java.util.*;

public class DashMartDistance {
    public static List<Integer> getClosestDashMart(char[][] city, List<int[]> locations) {
        int rows = city.length;
        int cols = city[0].length;

        // Create a distances array and initialize with -1
        int[][] distances = new int[rows][cols];
        for (int[] row : distances) {
            Arrays.fill(row, -1);
        }

        // Perform BFS from all DashMarts
        Queue<int[]> queue = new LinkedList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (city[i][j] == 'D') {
                    queue.offer(new int[]{i, j});
                    distances[i][j] = 0; // Distance to itself is 0
                }
            }
        }

        // Directions for moving in the grid
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        // BFS to compute the shortest distances to any DashMart
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0], y = current[1];

            for (int[] dir : directions) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                if (newX >= 0 && newX < rows && newY >= 0 && newY < cols &&
                    city[newX][newY] == ' ' && distances[newX][newY] == -1) {
                    distances[newX][newY] = distances[x][y] + 1;
                    queue.offer(new int[]{newX, newY});
                }
            }
        }

        // Compute distances for the given locations
        List<Integer> result = new ArrayList<>();
        for (int[] location : locations) {
            int distance = distances[location[0]][location[1]];
            result.add(distance);
        }

        return result;
    }

    public static void main(String[] args) {
        char[][] city = {
            {'X', ' ', ' ', 'D', ' ', ' ', 'X', ' ', 'X'},
            {'X', ' ', 'X', 'X', ' ', ' ', ' ', ' ', 'X'},
            {' ', ' ', ' ', 'D', 'X', 'X', ' ', 'X', ' '},
            {' ', ' ', ' ', 'D', ' ', 'X', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', 'X', ' ', ' ', 'X'},
            {' ', ' ', ' ', ' ', 'X', ' ', ' ', 'X', 'X'}
        };

        List<int[]> locations = Arrays.asList(
            new int[]{2, 2},
            new int[]{4, 0},
            new int[]{0, 4},
            new int[]{2, 6}
        );

        List<Integer> result = getClosestDashMart(city, locations);

        // Output the result
        System.out.println(result); // Expected: [1, 4, 1, 5]
    }
}

