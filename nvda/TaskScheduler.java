package nvda;

import java.util.*;

/**
 * LeetCode 621: Task Scheduler
 * 
 * Problem: Given a char array representing tasks CPU need to do. It contains capital letters A to Z 
 * where different letters represent different tasks. Tasks could be done without original order. 
 * Each task could be done in one interval. For each interval, CPU could finish one task or just be idle.
 * However, there is a non-negative cooling time n that means between two same tasks, there must be 
 * at least n intervals that CPU are doing different tasks or just be idle.
 * 
 * Return the least number of intervals the CPU will take to finish all the given tasks.
 * 
 * Example 1:
 * Input: tasks = ["A","A","A","B","B","B"], n = 2
 * Output: 8
 * Explanation: A -> B -> idle -> A -> B -> idle -> A -> B.
 * 
 * Example 2:
 * Input: tasks = ["A","A","A","B","B","B"], n = 0
 * Output: 6
 * Explanation: On this case any permutation of size 6 would work since n = 0.
 * ["A","A","A","B","B","B"]
 * ["A","B","A","B","A","B"]
 * ["B","B","B","A","A","A"]
 * ...
 * And it is only the number of tasks.
 * 
 * Example 3:
 * Input: tasks = ["A","A","A","A","A","A","B","C","D","E","F","G"], n = 2
 * Output: 16
 * Explanation: One possible solution is
 * A -> B -> C -> A -> D -> E -> A -> F -> G -> A -> idle -> idle -> A -> idle -> idle -> A
 */
public class TaskScheduler {
    
    /**
     * Approach 1: Mathematical Solution (Most Optimal)
     * 
     * Key Insight: The task with the highest frequency determines the minimum time needed.
     * We need to place the most frequent tasks first, then fill in the gaps.
     * 
     * Algorithm:
     * 1. Count frequency of each task
     * 2. Find the maximum frequency
     * 3. Count how many tasks have this maximum frequency
     * 4. Calculate minimum intervals needed
     * 
     * Time Complexity: O(n) where n is the number of tasks
     * Space Complexity: O(1) since we have at most 26 different tasks
     */
    public int leastInterval(char[] tasks, int n) {
        // Count frequency of each task
        int[] frequencies = new int[26];
        for (char task : tasks) {
            frequencies[task - 'A']++;
        }
        
        // Find the maximum frequency
        int maxFreq = 0;
        for (int freq : frequencies) {
            maxFreq = Math.max(maxFreq, freq);
        }
        
        // Count how many tasks have the maximum frequency
        int maxFreqCount = 0;
        for (int freq : frequencies) {
            if (freq == maxFreq) {
                maxFreqCount++;
            }
        }
        
        // Calculate minimum intervals needed
        // (maxFreq - 1) * (n + 1) gives us the slots for the most frequent task
        // + maxFreqCount accounts for all tasks with maximum frequency
        int minIntervals = (maxFreq - 1) * (n + 1) + maxFreqCount;
        
        // The result is the maximum of calculated intervals and total tasks
        // This handles cases where we have enough variety of tasks to fill all slots
        return Math.max(minIntervals, tasks.length);
    }
    
    /**
     * Approach 2: Priority Queue + Simulation
     * 
     * This approach simulates the actual scheduling process using a priority queue
     * to always pick the task with the highest remaining count.
     * 
     * Time Complexity: O(n * log(26)) = O(n) since we have at most 26 different tasks
     * Space Complexity: O(26) = O(1)
     */
    public int leastIntervalSimulation(char[] tasks, int n) {
        // Count frequency of each task
        Map<Character, Integer> taskCount = new HashMap<>();
        for (char task : tasks) {
            taskCount.put(task, taskCount.getOrDefault(task, 0) + 1);
        }
        
        // Max heap to store task frequencies
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        maxHeap.addAll(taskCount.values());
        
        int intervals = 0;
        
        while (!maxHeap.isEmpty()) {
            List<Integer> temp = new ArrayList<>();
            
            // Try to schedule n+1 tasks (including cooldown period)
            for (int i = 0; i <= n; i++) {
                if (!maxHeap.isEmpty()) {
                    int count = maxHeap.poll();
                    if (count > 1) {
                        temp.add(count - 1);
                    }
                }
            }
            
            // Add back the remaining counts
            maxHeap.addAll(temp);
            
            // If heap is empty, we only count the actual tasks scheduled
            // Otherwise, we count the full cycle (n+1)
            intervals += maxHeap.isEmpty() ? temp.size() + (maxHeap.isEmpty() ? 0 : 1) : n + 1;
        }
        
        return intervals;
    }
    
    /**
     * Approach 3: Greedy with Array (Alternative implementation)
     * 
     * This approach uses sorting and greedy selection to schedule tasks.
     * 
     * Time Complexity: O(n * 26) = O(n)
     * Space Complexity: O(1)
     */
    public int leastIntervalGreedy(char[] tasks, int n) {
        int[] frequencies = new int[26];
        for (char task : tasks) {
            frequencies[task - 'A']++;
        }
        
        int intervals = 0;
        
        while (true) {
            // Sort frequencies in descending order
            Arrays.sort(frequencies);
            
            // Check if all tasks are completed
            if (frequencies[25] == 0) break;
            
            // Schedule tasks for this cycle
            int cycleLength = Math.min(n + 1, 26);
            for (int i = 0; i < cycleLength; i++) {
                if (frequencies[25 - i] > 0) {
                    frequencies[25 - i]--;
                    intervals++;
                } else if (frequencies[25] > 0) {
                    // If we run out of different tasks but still have the most frequent task
                    intervals++;
                    break;
                }
            }
        }
        
        return intervals;
    }
    
    // Test cases
    public static void main(String[] args) {
        TaskScheduler scheduler = new TaskScheduler();
        
        // Test Case 1
        char[] tasks1 = {'A','A','A','B','B','B'};
        int n1 = 2;
        System.out.println("Test 1 - Expected: 8, Got: " + scheduler.leastInterval(tasks1, n1));
        
        // Test Case 2
        char[] tasks2 = {'A','A','A','B','B','B'};
        int n2 = 0;
        System.out.println("Test 2 - Expected: 6, Got: " + scheduler.leastInterval(tasks2, n2));
        
        // Test Case 3
        char[] tasks3 = {'A','A','A','A','A','A','B','C','D','E','F','G'};
        int n3 = 2;
        System.out.println("Test 3 - Expected: 16, Got: " + scheduler.leastInterval(tasks3, n3));
        
        // Test Case 4: Edge case with single task
        char[] tasks4 = {'A'};
        int n4 = 2;
        System.out.println("Test 4 - Expected: 1, Got: " + scheduler.leastInterval(tasks4, n4));
        
        // Test Case 5: Edge case with no cooldown
        char[] tasks5 = {'A','B','C','D','E'};
        int n5 = 0;
        System.out.println("Test 5 - Expected: 5, Got: " + scheduler.leastInterval(tasks5, n5));
        
        // Test Case 6: All same tasks
        char[] tasks6 = {'A','A','A','A'};
        int n6 = 3;
        System.out.println("Test 6 - Expected: 13, Got: " + scheduler.leastInterval(tasks6, n6));
        
        // Verify with simulation approach
        System.out.println("\nVerification with simulation approach:");
        System.out.println("Test 1 Simulation: " + scheduler.leastIntervalSimulation(tasks1, n1));
        System.out.println("Test 3 Simulation: " + scheduler.leastIntervalSimulation(tasks3, n3));
    }
}
