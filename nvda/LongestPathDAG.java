// 一个 多源有向图（可能有多个入度为 0 的点），每条边都有一个 延迟值，要求找出图中 最长的路径（也就是最大总延迟），并且输出这条路径的节点序列。

package nvda;

import java.util.*;

public class LongestPathDAG {
    static class Edge {
        int to, weight;
        Edge(int t, int w) {
            to = t;
            weight = w;
        }
    }

    public static List<Integer> longestPath(int n, List<int[]> edges) {
        List<List<Edge>> graph = new ArrayList<>();
        int[] inDegree = new int[n];
        for (int i = 0; i < n; i++) graph.add(new ArrayList<>());

        for (int[] e : edges) {
            int from = e[0], to = e[1], weight = e[2];
            graph.get(from).add(new Edge(to, weight));
            inDegree[to]++;
        }

        // Topo sort
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++)
            if (inDegree[i] == 0)
                queue.offer(i);

        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MIN_VALUE);
        int[] prev = new int[n];
        Arrays.fill(prev, -1);

        // 多源初始化
        for (int i = 0; i < n; i++)
            if (inDegree[i] == 0)
                dist[i] = 0;

        while (!queue.isEmpty()) {
            int node = queue.poll();
            for (Edge edge : graph.get(node)) {
                int next = edge.to, w = edge.weight;
                if (dist[node] + w > dist[next]) {
                    dist[next] = dist[node] + w;
                    prev[next] = node;
                }
                if (--inDegree[next] == 0)
                    queue.offer(next);
            }
        }

        // 找最长路径终点
        int maxLen = -1, end = -1;
        for (int i = 0; i < n; i++) {
            if (dist[i] > maxLen) {
                maxLen = dist[i];
                end = i;
            }
        }

        // 回溯路径
        LinkedList<Integer> path = new LinkedList<>();
        while (end != -1) {
            path.addFirst(end);
            end = prev[end];
        }

        System.out.println("Longest Delay: " + maxLen);
        return path;
    }

    public static void main(String[] args) {
        List<int[]> edges = List.of(
            new int[]{0, 1, 2},
            new int[]{1, 2, 3},
            new int[]{0, 3, 4},
            new int[]{3, 2, 2}
        );
        System.out.println(longestPath(4, edges)); // Example output: [0, 3, 2] with delay 6
    }
}

