// ✅ Question 1: Return All Dependencies in Topological Order
// 📄 Problem Statement:
// You are given a list of Task objects. Each task has a unique string id and a list of other task IDs it depends on (i.e., its dependencies):

// java
// Copy
// Edit
// class Task {
//     String id;
//     List<String> dependencies;
// }
// You're also given a list of target task IDs (List<String> targetTasks).

// ✨ Your task:
// Return all dependencies (direct and indirect) of the given target tasks:

// The result must be topologically sorted: if task A depends on B, then B must appear before A.

// No duplicate tasks should be returned.

// Do not include the target tasks themselves in the output.

// ✅ Question 2: Check Ancestor Relationship
// 📄 Problem Statement:
// Given the same list of Task objects as above, and two task IDs: ancestorId and descendantId,

// ✨ Your task:
// Return whether ancestorId is an ancestor of descendantId—that is, whether descendantId (directly or indirectly) depends on ancestorId.

package vanta;

import java.util.*;

public class TaskDependencyDemo {

    static class Task {
        String id;
        List<String> dependencies;

        public Task(String id, List<String> dependencies) {
            this.id = id;
            this.dependencies = dependencies;
        }
    }

    static class TaskDependencyResolver {

        // ✅ Question 1: Get all dependencies (in topological order, no duplicates)
        public List<String> getAllDependencies(List<Task> allTasks, List<String> targetTaskIds) {
            Map<String, Task> taskMap = new HashMap<>();
            for (Task task : allTasks) {
                taskMap.put(task.id, task);
            }

            Set<String> visited = new HashSet<>();
            List<String> result = new ArrayList<>();

            for (String targetId : targetTaskIds) {
                dfs(targetId, taskMap, visited, result);
            }

            // Remove targets themselves if present
            result.removeAll(targetTaskIds);
            return result;
        }

        private void dfs(String taskId, Map<String, Task> taskMap, Set<String> visited, List<String> result) {
            if (!taskMap.containsKey(taskId) || visited.contains(taskId)) return;
            visited.add(taskId);

            for (String dep : taskMap.get(taskId).dependencies) {
                dfs(dep, taskMap, visited, result);
            }

            result.add(taskId);
        }

        // ✅ Question 2: Find all ancestors of a given task (reverse graph)
        public List<String> findAllAncestors(List<Task> allTasks, String targetId) {
            Map<String, List<String>> reverseGraph = new HashMap<>();

            for (Task task : allTasks) {
                for (String dep : task.dependencies) {
                    reverseGraph.computeIfAbsent(dep, k -> new ArrayList<>()).add(task.id);
                }
            }

            Set<String> ancestors = new HashSet<>();
            Deque<String> stack = new ArrayDeque<>();
            stack.push(targetId);

            while (!stack.isEmpty()) {
                String current = stack.pop();
                for (String parent : reverseGraph.getOrDefault(current, Collections.emptyList())) {
                    if (ancestors.add(parent)) {
                        stack.push(parent);
                    }
                }
            }

            return new ArrayList<>(ancestors);
        }
    }

    public static void main(String[] args) {
        List<Task> tasks = List.of(
            new Task("A", List.of()),
            new Task("B", List.of("A")),
            new Task("C", List.of("A")),
            new Task("D", List.of("B", "C")),
            new Task("E", List.of("C"))
        );

        TaskDependencyResolver resolver = new TaskDependencyResolver();

        System.out.println("== Dependencies (topo order, no dupes) ==");
        System.out.println("Target: D => " + resolver.getAllDependencies(tasks, List.of("D"))); // [A, B, C]
        System.out.println("Target: E => " + resolver.getAllDependencies(tasks, List.of("E"))); // [A, C]
        System.out.println("Target: [D, E] => " + resolver.getAllDependencies(tasks, List.of("D", "E"))); // [A, B, C]

        System.out.println("\n== Ancestors (reverse graph) ==");
        System.out.println("Ancestors of A: " + resolver.findAllAncestors(tasks, "A")); // [B, C, D, E]
        System.out.println("Ancestors of D: " + resolver.findAllAncestors(tasks, "D")); // [B, C, A]
        System.out.println("Ancestors of C: " + resolver.findAllAncestors(tasks, "C")); // [D, E]
    }
}
