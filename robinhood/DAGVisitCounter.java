package robinhood;

import java.util.*;

public class DAGVisitCounter {

    public static Map<String, Integer> countVisits(List<String> edges, String start) {
        // Step 1: Parse edges into graph
        Map<String, List<String>> graph = new HashMap<>();
        for (String edge : edges) {
            String[] parts = edge.split("=");
            String node = parts[0];
            List<String> deps = new ArrayList<>();
            if (parts.length > 1 && !parts[1].isEmpty()) {
                deps = Arrays.asList(parts[1].split(","));
            }
            graph.put(node, deps);
        }

        // Step 2: DFS with visit counting
        Map<String, Integer> visitCount = new HashMap<>();

        dfs(start, graph, visitCount);

        return visitCount;
    }

    private static void dfs(String node, Map<String, List<String>> graph, Map<String, Integer> countMap) {
        countMap.put(node, countMap.getOrDefault(node, 0) + 1);

        for (String dep : graph.getOrDefault(node, Collections.emptyList())) {
            dfs(dep, graph, countMap);
        }
    }

    // Example usage
    public static void main(String[] args) {
        List<String> input = Arrays.asList(
            "a=",
            "b=a",
            "c=b,x",
            "d=b,c",
            "e=b,c,d"
        );
        String startNode = "e";

        Map<String, Integer> result = countVisits(input, startNode);
        for (Map.Entry<String, Integer> entry : result.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}

