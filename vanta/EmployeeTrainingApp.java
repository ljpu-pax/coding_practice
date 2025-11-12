// 📄 Problem Statement:
// You are given a list of Employee objects, where each employee has:

// joinedDay: the day they joined the company

// trainedDay: the day they completed their training

// overdueDay: the deadline by which they must complete the training

// Assume:

// joinedDay < overdueDay

// joinedDay <= trainedDay

// The order between trainedDay and overdueDay is arbitrary

// currentDay can fall before, within, or after these days

// ✨ Your task:
// Implement a function in Java:

// java
// Copy
// Edit
// Map<Employee, String> getTrainStatus(int currentDay, List<Employee> employees)
// Where the training status for each employee should be one of:

// "done": if training is completed on or before currentDay

// "overdue": if currentDay > overdueDay and training has not yet been completed

// "unknown": otherwise (i.e., currentDay < trainedDay && currentDay <= overdueDay)

package vanta;

import java.util.*;

// Employee class with groupId
class Employee {
    int startDay;
    int trainedDay;
    int checkDay;
    int trainingWindow;
    int groupId;

    public Employee(int startDay, int trainedDay, int checkDay, int trainingWindow, int groupId) {
        this.startDay = startDay;
        this.trainedDay = trainedDay;
        this.checkDay = checkDay;
        this.trainingWindow = trainingWindow;
        this.groupId = groupId;
    }
}

// Result per employee
class TrainingStatus {
    String status;
    int overdueDays;

    public TrainingStatus(String status, int overdueDays) {
        this.status = status;
        this.overdueDays = overdueDays;
    }

    @Override
    public String toString() {
        return status + " (" + overdueDays + ")";
    }
}

// Result per group
class GroupStats {
    int numEmployees;
    int totalOverdueDays;

    public GroupStats(int numEmployees, int totalOverdueDays) {
        this.numEmployees = numEmployees;
        this.totalOverdueDays = totalOverdueDays;
    }

    @Override
    public String toString() {
        return "Employees=" + numEmployees + ", OverdueDays=" + totalOverdueDays;
    }
}

// Main class
public class EmployeeTrainingApp {

    // Part 1: Individual employee training status
    public static TrainingStatus getStatus(Employee emp) {
        int deadline = emp.startDay + emp.trainingWindow;

        if (emp.checkDay < emp.startDay) {
            return new TrainingStatus("not_required", 0);
        } else if (emp.trainedDay <= deadline) {
            return new TrainingStatus("completed", 0);
        } else if (emp.checkDay >= emp.trainedDay) {
            return new TrainingStatus("overdue", emp.trainedDay - deadline);
        } else {
            return new TrainingStatus("pending", 0);
        }
    }

    // Part 2: Group-wise aggregation (including children)
    public static Map<Integer, GroupStats> computeGroupStats(
        List<Employee> employees,
        Map<Integer, List<Integer>> groupTree // parent → children
    ) {
        // Step 1: groupId → list of Employees
        Map<Integer, List<Employee>> groupToEmployees = new HashMap<>();
        for (Employee e : employees) {
            groupToEmployees.computeIfAbsent(e.groupId, k -> new ArrayList<>()).add(e);
        }

        // Step 2: DFS aggregation
        Map<Integer, GroupStats> result = new HashMap<>();
        Set<Integer> allGroups = new HashSet<>(groupToEmployees.keySet());
        allGroups.addAll(groupTree.keySet());

        for (Integer groupId : allGroups) {
            dfs(groupId, groupTree, groupToEmployees, result, new HashSet<>());
        }

        return result;
    }

    private static GroupStats dfs(
        int groupId,
        Map<Integer, List<Integer>> groupTree,
        Map<Integer, List<Employee>> groupToEmployees,
        Map<Integer, GroupStats> memo,
        Set<Integer> visited
    ) {
        if (memo.containsKey(groupId)) return memo.get(groupId);
        if (visited.contains(groupId)) return new GroupStats(0, 0); // prevent cycles
        visited.add(groupId);

        int count = 0, overdueSum = 0;
        for (Employee e : groupToEmployees.getOrDefault(groupId, Collections.emptyList())) {
            TrainingStatus status = getStatus(e);
            count += 1;
            overdueSum += status.overdueDays;
        }

        for (int child : groupTree.getOrDefault(groupId, Collections.emptyList())) {
            GroupStats childStats = dfs(child, groupTree, groupToEmployees, memo, visited);
            count += childStats.numEmployees;
            overdueSum += childStats.totalOverdueDays;
        }

        GroupStats stats = new GroupStats(count, overdueSum);
        memo.put(groupId, stats);
        return stats;
    }

    // ✅ Demo / Test
    public static void main(String[] args) {
        List<Employee> emps = List.of(
            new Employee(1, 3, 5, 2, 1),   // overdue (deadline = 3)
            new Employee(1, 2, 3, 5, 1),   // completed (deadline = 6)
            new Employee(10, 10, 2, 5, 2), // not_required (checkDay < startDay)
            new Employee(1, 7, 6, 5, 3)    // pending (trainedDay > checkDay but before deadline)
        );

        Map<Integer, List<Integer>> tree = Map.of(
            1, List.of(2, 3)
        );

        Map<Integer, GroupStats> stats = computeGroupStats(emps, tree);
        stats.forEach((g, s) -> System.out.println("Group " + g + ": " + s));
    }
}

