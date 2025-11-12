import java.util.*;

public class WordPathFinder {
    public List<int[]> findPath(char[][] grid, String word) {
        int rows = grid.length;
        int cols = grid[0].length;
        List<int[]> path = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == word.charAt(0)) {
                    if (dfs(grid, word, i, j, 0, path)) {
                        return path;
                    }
                }
            }
        }
        return new ArrayList<>(); // Return empty list if path not found
    }

    private boolean dfs(char[][] grid, String word, int i, int j, int index, List<int[]> path) {
        int rows = grid.length;
        int cols = grid[0].length;

        if (i >= rows || j >= cols || grid[i][j] != word.charAt(index)) {
            return false;
        }

        path.add(new int[]{i, j});

        if (index == word.length() - 1) {
            return true;
        }

        // Move Right
        if (dfs(grid, word, i, j + 1, index + 1, path)) {
            return true;
        }

        // Move Down
        if (dfs(grid, word, i + 1, j, index + 1, path)) {
            return true;
        }

        // Backtrack
        path.remove(path.size() - 1);
        return false;
    }

    // For testing purposes
    public static void main(String[] args) {
        WordPathFinder finder = new WordPathFinder();
        char[][] grid = {
            {'A', 'B', 'C'},
            {'D', 'E', 'F'},
            {'G', 'H', 'I'}
        };
        String word = "ABE";
        List<int[]> path = finder.findPath(grid, word);
        if (!path.isEmpty()) {
            System.out.println("Path found:");
            for (int[] coord : path) {
                System.out.println(Arrays.toString(coord));
            }
        } else {
            System.out.println("No path found.");
        }
    }
}

