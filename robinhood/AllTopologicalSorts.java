package robinhood;

import java.util.*;

public class AllTopologicalSorts {
    public List<List<Integer>> allCourseOrders(int numCourses, int[][] prerequisites) {
        // 建图
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }
        int[] indegree = new int[numCourses];

        for (int[] pre : prerequisites) {
            int course = pre[0];
            int prereq = pre[1];
            graph.get(prereq).add(course);
            indegree[course]++;
        }

        List<List<Integer>> result = new ArrayList<>();
        boolean[] visited = new boolean[numCourses];
        backtrack(graph, indegree, visited, new ArrayList<>(), result, numCourses);
        return result;
    }

    private void backtrack(List<List<Integer>> graph, int[] indegree, boolean[] visited,
                           List<Integer> path, List<List<Integer>> result, int total) {
        if (path.size() == total) {
            result.add(new ArrayList<>(path));
            return;
        }

        boolean hasChoice = false;
        for (int i = 0; i < total; i++) {
            if (!visited[i] && indegree[i] == 0) {
                hasChoice = true;
                visited[i] = true;
                path.add(i);
                for (int next : graph.get(i)) {
                    indegree[next]--;
                }

                backtrack(graph, indegree, visited, path, result, total);

                // 回溯
                for (int next : graph.get(i)) {
                    indegree[next]++;
                }
                visited[i] = false;
                path.remove(path.size() - 1);
            }
        }

        // 如果没有 indegree=0 的课程且还没学完，说明有环，直接返回
        if (!hasChoice) return;
    }

    public static void main(String[] args) {
        AllTopologicalSorts solver = new AllTopologicalSorts();
        int[][] prereq = {{1,0},{2,0},{3,1},{3,2}};
        List<List<Integer>> allOrders = solver.allCourseOrders(4, prereq);
        for (List<Integer> order : allOrders) {
            System.out.println(order);
        }
    }
}

