package nvda;

import java.util.*;

public class CourseSchedule {

    // ✅ 解法一：DFS 检测环
    public static boolean canFinishDFS(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = new ArrayList<>();

        for (int i = 0; i < numCourses; i++) graph.add(new ArrayList<>());

        for (int[] edge : prerequisites) {
            int course = edge[0], prereq = edge[1];
            graph.get(prereq).add(course);  // b → a
        }

        boolean[] visited = new boolean[numCourses];
        boolean[] onPath = new boolean[numCourses];

        for (int i = 0; i < numCourses; i++) {
            if (!visited[i] && hasCycle(graph, i, visited, onPath)) {
                return false;
            }
        }

        return true;
    }

    private static boolean hasCycle(List<List<Integer>> graph, int node, boolean[] visited, boolean[] onPath) {
        visited[node] = true;
        onPath[node] = true;

        for (int neighbor : graph.get(node)) {
            if (!visited[neighbor]) {
                if (hasCycle(graph, neighbor, visited, onPath)) return true;
            } else if (onPath[neighbor]) {
                return true;
            }
        }

        onPath[node] = false;
        return false;
    }

    // ✅ 解法二：BFS 拓扑排序
    public static boolean canFinishBFS(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = new ArrayList<>();
        int[] indegree = new int[numCourses];

        for (int i = 0; i < numCourses; i++) graph.add(new ArrayList<>());

        for (int[] edge : prerequisites) {
            int course = edge[0], prereq = edge[1];
            graph.get(prereq).add(course);
            indegree[course]++;
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (indegree[i] == 0) queue.offer(i);
        }

        int count = 0;

        while (!queue.isEmpty()) {
            int curr = queue.poll();
            count++;
            for (int neighbor : graph.get(curr)) {
                indegree[neighbor]--;
                if (indegree[neighbor] == 0) queue.offer(neighbor);
            }
        }

        return count == numCourses;
    }

    // ✅ 测试用例入口
    public static void main(String[] args) {
        runTest(1, 2, new int[][]{{1, 0}}, true);
        runTest(2, 2, new int[][]{{1, 0}, {0, 1}}, false);
        runTest(3, 3, new int[][]{{1, 0}, {2, 1}}, true);
        runTest(4, 4, new int[][]{{1, 0}, {2, 1}, {3, 2}, {1, 3}}, false); // 有环
        runTest(5, 5, new int[][]{}, true); // 无依赖
    }

    private static void runTest(int id, int numCourses, int[][] prerequisites, boolean expected) {
        boolean dfsResult = canFinishDFS(numCourses, prerequisites);
        boolean bfsResult = canFinishBFS(numCourses, prerequisites);

        System.out.println("Test Case " + id + ":");

        System.out.println("  DFS  => " + (dfsResult == expected ? "✅ Passed" : "❌ Failed") +
                " (Expected: " + expected + ", Got: " + dfsResult + ")");
        System.out.println("  BFS  => " + (bfsResult == expected ? "✅ Passed" : "❌ Failed") +
                " (Expected: " + expected + ", Got: " + bfsResult + ")");
    }
}

