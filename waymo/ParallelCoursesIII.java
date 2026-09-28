import java.util.*;

/**
 * LeetCode 2050: Parallel Courses III (Hard)
 *
 * You are given an integer n, which indicates that there are n courses labeled from 1 to n.
 * You are also given a 2D integer array relations where relations[j] = [prevCoursej, nextCoursej]
 * denotes that course prevCoursej has to be completed before course nextCoursej (prerequisite relationship).
 *
 * Furthermore, you are given a 0-indexed integer array time where time[i] denotes how many months
 * it takes to complete the (i+1)th course.
 *
 * You must find the minimum number of months needed to complete all the courses following these rules:
 * - You may start taking a course at any time if the prerequisites are met.
 * - Any number of courses can be taken at the same time.
 *
 * Return the minimum number of months needed to complete all the courses.
 *
 * Note: The test cases are generated such that it is possible to complete every course
 * (i.e., the graph is a directed acyclic graph).
 *
 * Example 1:
 * Input: n = 3, relations = [[1,3],[2,3]], time = [3,2,5]
 * Output: 8
 * Explanation: The figure above represents the given graph and the time required to complete each course.
 * We start course 1 and course 2 simultaneously at month 0.
 * Course 1 takes 3 months and course 2 takes 2 months to complete respectively.
 * Thus, the earliest time we can start course 3 is at month 3, and the total time required is 3 + 5 = 8 months.
 *
 * Example 2:
 * Input: n = 5, relations = [[1,5],[2,5],[3,5],[3,4],[4,5]], time = [1,2,3,4,5]
 * Output: 12
 * Explanation: The figure above represents the given graph and the time required to complete each course.
 * You can start courses 1, 2, and 3 at month 0.
 * You can complete them at months 1, 2, and 3 respectively.
 * Course 4 should be taken after course 3 is completed, i.e., after 3 months. It is completed after 3 + 4 = 7 months.
 * Course 5 should be taken after courses 1, 2, 3, and 4 have been completed, i.e., after max(1,2,3,7) = 7.
 * Thus, the minimum time needed to complete all the courses is 7 + 5 = 12 months.
 *
 * Constraints:
 * - 1 <= n <= 5 * 10^4
 * - 0 <= relations.length <= min(n * (n - 1) / 2, 5 * 10^4)
 * - relations[j].length == 2
 * - 1 <= prevCoursej, nextCoursej <= n
 * - prevCoursej != nextCoursej
 * - All the pairs [prevCoursej, nextCoursej] are unique.
 * - time.length == n
 * - 1 <= time[i] <= 10^4
 * - The given graph is a directed acyclic graph.
 */
public class ParallelCoursesIII {

    /**
     * Approach 1: Topological Sort with BFS (Kahn's Algorithm) + DP
     *
     * Key Insight:
     * - This is a scheduling problem with dependencies (DAG)
     * - Use topological sort to process courses in dependency order
     * - Track the earliest completion time for each course
     * - dp[i] = earliest time to complete course i
     * - dp[i] = max(dp[prerequisites]) + time[i]
     *
     * Time Complexity: O(V + E) where V = number of courses, E = number of relations
     * Space Complexity: O(V + E) for graph and auxiliary data structures
     */
    public int minimumTime(int n, int[][] relations, int[] time) {
        // Build adjacency list and indegree array
        List<List<Integer>> graph = new ArrayList<>();
        int[] indegree = new int[n + 1];

        for (int i = 0; i <= n; i++) {
            graph.add(new ArrayList<>());
        }

        for (int[] relation : relations) {
            int prev = relation[0];
            int next = relation[1];
            graph.get(prev).add(next);
            indegree[next]++;
        }

        // dp[i] = earliest completion time for course i
        int[] dp = new int[n + 1];
        Queue<Integer> queue = new LinkedList<>();

        // Start with courses that have no prerequisites
        for (int i = 1; i <= n; i++) {
            if (indegree[i] == 0) {
                queue.offer(i);
                dp[i] = time[i - 1];  // time array is 0-indexed
            }
        }

        // Process courses in topological order
        while (!queue.isEmpty()) {
            int course = queue.poll();

            for (int nextCourse : graph.get(course)) {
                // Update earliest completion time for next course
                dp[nextCourse] = Math.max(dp[nextCourse], dp[course] + time[nextCourse - 1]);

                indegree[nextCourse]--;
                if (indegree[nextCourse] == 0) {
                    queue.offer(nextCourse);
                }
            }
        }

        // Return the maximum completion time among all courses
        int maxTime = 0;
        for (int i = 1; i <= n; i++) {
            maxTime = Math.max(maxTime, dp[i]);
        }

        return maxTime;
    }

    /**
     * Approach 2: DFS with Memoization (Top-down DP)
     *
     * Calculate the earliest completion time for each course recursively:
     * - For a course with no prerequisites: time[course]
     * - For a course with prerequisites: max(completion time of all prerequisites) + time[course]
     *
     * Time Complexity: O(V + E)
     * Space Complexity: O(V + E)
     */
    public int minimumTimeDFS(int n, int[][] relations, int[] time) {
        // Build adjacency list (reverse direction: store prerequisites for each course)
        List<List<Integer>> prerequisites = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            prerequisites.add(new ArrayList<>());
        }

        for (int[] relation : relations) {
            int prev = relation[0];
            int next = relation[1];
            prerequisites.get(next).add(prev);
        }

        // Memoization: memo[i] = earliest completion time for course i
        int[] memo = new int[n + 1];

        int maxTime = 0;
        for (int i = 1; i <= n; i++) {
            maxTime = Math.max(maxTime, dfs(i, prerequisites, time, memo));
        }

        return maxTime;
    }

    private int dfs(int course, List<List<Integer>> prerequisites, int[] time, int[] memo) {
        if (memo[course] != 0) {
            return memo[course];
        }

        int maxPrereqTime = 0;

        // Find the maximum completion time among all prerequisites
        for (int prereq : prerequisites.get(course)) {
            maxPrereqTime = Math.max(maxPrereqTime, dfs(prereq, prerequisites, time, memo));
        }

        // Earliest time to complete this course = max prereq time + this course's time
        memo[course] = maxPrereqTime + time[course - 1];

        return memo[course];
    }

    /**
     * Extension: Check if all courses can be completed within a given deadline
     */
    public boolean canFinishByDeadline(int n, int[][] relations, int[] time, int deadline) {
        int minTime = minimumTime(n, relations, time);
        return minTime <= deadline;
    }

    /**
     * Extension: Return the course schedule (order of completion)
     */
    public List<Integer> getCourseSchedule(int n, int[][] relations, int[] time) {
        List<List<Integer>> graph = new ArrayList<>();
        int[] indegree = new int[n + 1];

        for (int i = 0; i <= n; i++) {
            graph.add(new ArrayList<>());
        }

        for (int[] relation : relations) {
            int prev = relation[0];
            int next = relation[1];
            graph.get(prev).add(next);
            indegree[next]++;
        }

        // Priority queue: process courses by earliest completion time
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);
        int[] completionTime = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            if (indegree[i] == 0) {
                completionTime[i] = time[i - 1];
                pq.offer(new int[]{i, completionTime[i]});
            }
        }

        List<Integer> schedule = new ArrayList<>();

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int course = current[0];
            schedule.add(course);

            for (int nextCourse : graph.get(course)) {
                completionTime[nextCourse] = Math.max(completionTime[nextCourse],
                                                       completionTime[course] + time[nextCourse - 1]);

                indegree[nextCourse]--;
                if (indegree[nextCourse] == 0) {
                    pq.offer(new int[]{nextCourse, completionTime[nextCourse]});
                }
            }
        }

        return schedule;
    }

    // ==================== Test Cases ====================

    public static void main(String[] args) {
        ParallelCoursesIII solution = new ParallelCoursesIII();

        System.out.println("=== LeetCode 2050: Parallel Courses III ===\n");

        // Test 1
        System.out.println("Test 1: Basic example");
        int n1 = 3;
        int[][] relations1 = {{1, 3}, {2, 3}};
        int[] time1 = {3, 2, 5};
        int result1 = solution.minimumTime(n1, relations1, time1);
        System.out.println("n = " + n1);
        System.out.println("relations = " + Arrays.deepToString(relations1));
        System.out.println("time = " + Arrays.toString(time1));
        System.out.println("Result (BFS): " + result1);
        System.out.println("Result (DFS): " + solution.minimumTimeDFS(n1, relations1, time1));
        System.out.println("Expected: 8");
        System.out.println("Schedule: " + solution.getCourseSchedule(n1, relations1, time1));
        System.out.println();

        // Test 2
        System.out.println("Test 2: Complex dependencies");
        int n2 = 5;
        int[][] relations2 = {{1, 5}, {2, 5}, {3, 5}, {3, 4}, {4, 5}};
        int[] time2 = {1, 2, 3, 4, 5};
        int result2 = solution.minimumTime(n2, relations2, time2);
        System.out.println("n = " + n2);
        System.out.println("relations = " + Arrays.deepToString(relations2));
        System.out.println("time = " + Arrays.toString(time2));
        System.out.println("Result (BFS): " + result2);
        System.out.println("Result (DFS): " + solution.minimumTimeDFS(n2, relations2, time2));
        System.out.println("Expected: 12");
        System.out.println("Schedule: " + solution.getCourseSchedule(n2, relations2, time2));
        System.out.println();

        // Test 3: No dependencies
        System.out.println("Test 3: No dependencies (all parallel)");
        int n3 = 4;
        int[][] relations3 = {};
        int[] time3 = {5, 3, 8, 2};
        int result3 = solution.minimumTime(n3, relations3, time3);
        System.out.println("n = " + n3);
        System.out.println("relations = " + Arrays.deepToString(relations3));
        System.out.println("time = " + Arrays.toString(time3));
        System.out.println("Result: " + result3);
        System.out.println("Expected: 8 (max of all times)");
        System.out.println();

        // Test 4: Linear chain
        System.out.println("Test 4: Linear chain (sequential)");
        int n4 = 4;
        int[][] relations4 = {{1, 2}, {2, 3}, {3, 4}};
        int[] time4 = {1, 2, 3, 4};
        int result4 = solution.minimumTime(n4, relations4, time4);
        System.out.println("n = " + n4);
        System.out.println("relations = " + Arrays.deepToString(relations4));
        System.out.println("time = " + Arrays.toString(time4));
        System.out.println("Result: " + result4);
        System.out.println("Expected: 10 (1+2+3+4)");
        System.out.println();

        // Test 5: Deadline check
        System.out.println("Test 5: Deadline check");
        System.out.println("Can complete Test 1 by deadline 10? " +
                          solution.canFinishByDeadline(n1, relations1, time1, 10));
        System.out.println("Can complete Test 1 by deadline 7? " +
                          solution.canFinishByDeadline(n1, relations1, time1, 7));
        System.out.println();

        // Test 6: Diamond dependency
        System.out.println("Test 6: Diamond dependency");
        int n6 = 4;
        int[][] relations6 = {{1, 2}, {1, 3}, {2, 4}, {3, 4}};
        int[] time6 = {1, 2, 3, 4};
        int result6 = solution.minimumTime(n6, relations6, time6);
        System.out.println("n = " + n6);
        System.out.println("relations = " + Arrays.deepToString(relations6));
        System.out.println("time = " + Arrays.toString(time6));
        System.out.println("Result: " + result6);
        System.out.println("Expected: 8 (1 -> max(1+2, 1+3) -> 4+4)");
        System.out.println("Schedule: " + solution.getCourseSchedule(n6, relations6, time6));
    }
}
