package robinhood;

import java.util.*;

public class LoadFactorDFS {
    public static Map<String, Integer> computeVisits(List<String> edges, String start) {
        Map<String, List<String>> graph = new HashMap<>();
        Map<String, Integer> indegree = new HashMap<>();

        // Step 1: Parse input
        for (String e : edges) {
            String[] parts = e.split("=");
            String node = parts[0].trim();
            List<String> deps = new ArrayList<>();
            if (parts.length > 1 && !parts[1].isEmpty()) {
                deps = Arrays.asList(parts[1].split(","));
            }
            graph.putIfAbsent(node, new ArrayList<>());
            indegree.putIfAbsent(node, 0);
            for (String dep : deps) {
                dep = dep.trim();
                graph.putIfAbsent(dep, new ArrayList<>());
                indegree.putIfAbsent(dep, 0);
                graph.get(node).add(dep);
                indegree.put(dep, indegree.get(dep) + 1);
            }
        }

        // Step 2: Topological order
        Queue<String> q = new LinkedList<>();
        for (String node : indegree.keySet()) {
            if (indegree.get(node) == 0) q.add(node);
        }
        List<String> topoOrder = new ArrayList<>();
        while (!q.isEmpty()) {
            String u = q.poll();
            topoOrder.add(u);
            for (String v : graph.getOrDefault(u, Collections.emptyList())) {
                indegree.put(v, indegree.get(v) - 1);
                if (indegree.get(v) == 0) q.add(v);
            }
        }

        // Step 3: Count visits
        Map<String, Integer> count = new HashMap<>();
        for (String node : graph.keySet()) {
            count.put(node, 0);
        }
        count.put(start, 1);

        // Traverse topo order in reverse (start dependencies first)
        Collections.reverse(topoOrder);
        for (String u : topoOrder) {
            for (String v : graph.getOrDefault(u, Collections.emptyList())) {
                count.put(v, count.get(v) + count.get(u));
            }
        }

        return count;
    }

    public static void main(String[] args) {
        List<String> edges = Arrays.asList(
            "a=",
            "b=a",
            "c=b,x",
            "d=b,c",
            "e=b,c,d"
        );
        String start = "e";
        Map<String, Integer> result = computeVisits(edges, start);
        System.out.println(result);
        // 期望输出: {e=1, a=4, c=2, d=1, b=4}
    }
}

