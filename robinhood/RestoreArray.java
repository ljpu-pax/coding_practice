package robinhood;

import java.util.*;

public class RestoreArray {
    public int[] restoreArray(int[][] adjacentPairs) {
        Map<Integer, List<Integer>> graph = new HashMap<>();

        // Step 1: Build graph
        for (int[] pair : adjacentPairs) {
            graph.computeIfAbsent(pair[0], k -> new ArrayList<>()).add(pair[1]);
            graph.computeIfAbsent(pair[1], k -> new ArrayList<>()).add(pair[0]);
        }

        int n = adjacentPairs.length + 1;
        int[] result = new int[n];

        // Step 2: Find start node (degree == 1)
        int start = 0;
        for (Map.Entry<Integer, List<Integer>> entry : graph.entrySet()) {
            if (entry.getValue().size() == 1) {
                start = entry.getKey();
                break;
            }
        }

        // Step 3: Traverse and build the array
        Set<Integer> visited = new HashSet<>();
        result[0] = start;
        visited.add(start);

        for (int i = 1; i < n; i++) {
            List<Integer> neighbors = graph.get(result[i - 1]);
            for (int neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    result[i] = neighbor;
                    visited.add(neighbor);
                    break;
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        RestoreArray s = new RestoreArray();
        int[][] input = {{2,1},{3,4},{3,2}};
        System.out.println(Arrays.toString(s.restoreArray(input)));
        // Output: [1, 2, 3, 4] or [4, 3, 2, 1] — both are valid
    }
}
