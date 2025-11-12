package robinhood;

import java.util.*;

public class CourseSchedule2 {
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        // 1. 建图
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

        // 2. BFS拓扑排序
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (indegree[i] == 0) {
                queue.offer(i);
            }
        }

        int[] order = new int[numCourses];
        int idx = 0;

        while (!queue.isEmpty()) {
            int curr = queue.poll();
            order[idx++] = curr;

            for (int next : graph.get(curr)) {
                indegree[next]--;
                if (indegree[next] == 0) {
                    queue.offer(next);
                }
            }
        }

        // 3. 检查是否有环
        if (idx == numCourses) {
            return order;
        } else {
            return new int[0];
        }
    }

    public static void main(String[] args) {
        CourseSchedule2 solver = new CourseSchedule2();
        int[][] prereq = {{1,0},{2,0},{3,1},{3,2}};
        System.out.println(Arrays.toString(solver.findOrder(4, prereq)));
        // 可能输出 [0,1,2,3] 或 [0,2,1,3]
    }
}

