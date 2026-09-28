/*
 * Scale.ai interview: Task Processor
 *
 * Part 1:
 * Given tasks with task_id and deadline, process smaller deadline first.
 * If deadlines tie, keep the same order as the input.
 *
 * Part 2:
 * Each task may have subtasks. A parent task becomes runnable only after all
 * its subtasks have been processed. Process subtasks in increasing subtask_id.
 * Among runnable parent tasks, process smaller deadline first, then smaller task id.
 *
 * Part 3:
 * Discuss production concerns for a task processor: correctness, concurrency,
 * retries, observability, scaling, and operations.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;

public class TaskProcessor {

    static class BasicTask {
        int taskId;
        int deadline;
        int inputIndex;

        BasicTask(int taskId, int deadline, int inputIndex) {
            this.taskId = taskId;
            this.deadline = deadline;
            this.inputIndex = inputIndex;
        }
    }

    static class SubtaskEdge {
        int subtaskId;
        int parentTaskId;

        SubtaskEdge(int subtaskId, int parentTaskId) {
            this.subtaskId = subtaskId;
            this.parentTaskId = parentTaskId;
        }
    }

    /*
     * Part 1 solution.
     *
     * Time: O(n log n)
     * Space: O(n)
     */
    public static List<Integer> processByDeadlineStable(List<BasicTask> tasks) {
        PriorityQueue<BasicTask> pq = new PriorityQueue<>(
                Comparator.comparingInt((BasicTask task) -> task.deadline)
                        .thenComparingInt(task -> task.inputIndex)
        );

        pq.addAll(tasks);

        List<Integer> order = new ArrayList<>();
        while (!pq.isEmpty()) {
            order.add(pq.poll().taskId);
        }
        return order;
    }

    /*
     * Part 2 solution.
     *
     * Subtasks have no dependencies among themselves, so processing them in
     * increasing id unlocks parent tasks as soon as each parent's count reaches 0.
     *
     * Time: O((n + m) log n + m log m)
     * Space: O(n + m)
     */
    public static SubtaskResult processWithSubtasks(int[] deadlines, List<SubtaskEdge> edges) {
        int n = deadlines.length - 1; // deadlines are 1-indexed.
        int[] remainingSubtasks = new int[n + 1];
        Map<Integer, Integer> subtaskToParent = new HashMap<>();

        for (SubtaskEdge edge : edges) {
            subtaskToParent.put(edge.subtaskId, edge.parentTaskId);
            remainingSubtasks[edge.parentTaskId]++;
        }

        List<Integer> subtaskOrder = new ArrayList<>(subtaskToParent.keySet());
        Collections.sort(subtaskOrder);

        PriorityQueue<Integer> runnableTasks = new PriorityQueue<>(
                Comparator.comparingInt((Integer taskId) -> deadlines[taskId])
                        .thenComparingInt(taskId -> taskId)
        );

        for (int taskId = 1; taskId <= n; taskId++) {
            if (remainingSubtasks[taskId] == 0) {
                runnableTasks.offer(taskId);
            }
        }

        for (int subtaskId : subtaskOrder) {
            int parentTaskId = subtaskToParent.get(subtaskId);
            remainingSubtasks[parentTaskId]--;
            if (remainingSubtasks[parentTaskId] == 0) {
                runnableTasks.offer(parentTaskId);
            }
        }

        List<Integer> taskOrder = new ArrayList<>();
        while (!runnableTasks.isEmpty()) {
            taskOrder.add(runnableTasks.poll());
        }

        return new SubtaskResult(subtaskOrder, taskOrder);
    }

    static class SubtaskResult {
        List<Integer> subtaskOrder;
        List<Integer> taskOrder;

        SubtaskResult(List<Integer> subtaskOrder, List<Integer> taskOrder) {
            this.subtaskOrder = subtaskOrder;
            this.taskOrder = taskOrder;
        }
    }

    /*
     * Part 3 production answer outline.
     *
     * - Reliability/correctness: use task ids as idempotency keys, deduplicate
     *   enqueue requests, persist a state machine such as PENDING -> RUNNING ->
     *   SUCCEEDED/FAILED, use retry limits, exponential backoff, timeouts, and a
     *   dead-letter queue or quarantine table for poison tasks.
     * - Consistency/concurrency: multiple workers must atomically claim tasks.
     *   Common choices are database row locks with leases, compare-and-swap
     *   updates, or a queue with visibility timeouts. Leases need heartbeats and
     *   expiration so crashed workers do not hold tasks forever.
     * - Performance/scalability: track queue depth and latency, apply
     *   backpressure when workers or downstream services are saturated, shard by
     *   tenant/priority/deadline bucket if one global heap is too hot, and use
     *   rate limiting to protect dependencies.
     * - Observability: emit structured logs by task_id, metrics for queue depth,
     *   wait time, processing time, success/failure rate, retry count, DLQ size,
     *   and traces across worker/downstream calls. Alert on SLO burn and stuck
     *   queues.
     * - Ops/security: support canary deploys, rollbacks, schema migrations,
     *   access control, audit logs, encryption for sensitive payloads, and data
     *   retention policies.
     */
    public static List<String> productionConsiderations() {
        List<String> notes = new ArrayList<>();
        notes.add("Reliability: idempotency keys, dedupe, persisted task states, retries, timeouts, DLQ/quarantine.");
        notes.add("Concurrency: atomic claim, leases/visibility timeout, heartbeats, prevent double processing.");
        notes.add("Scalability: backpressure, rate limiting, horizontal workers, sharding, durable storage.");
        notes.add("Observability: structured logs, queue depth, wait time, retry count, success rate, tracing, alerting.");
        notes.add("Ops/security: canary, rollback, access control, auditing, encryption, retention.");
        return notes;
    }

    private static String joinInts(List<Integer> values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(values.get(i));
        }
        return sb.toString();
    }

    /*
     * Small runner:
     * - Input starting with "1" runs Part 1:
     *   1
     *   n
     *   task_id deadline
     *
     * - Input starting with "2" runs Part 2:
     *   2
     *   n m
     *   d1 d2 ... dn
     *   subtask_id parent_task_id
     *
     * If no input is provided, main prints both sample cases.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        if (!scanner.hasNextInt()) {
            runSamples();
            return;
        }

        int part = scanner.nextInt();
        if (part == 1) {
            int n = scanner.nextInt();
            List<BasicTask> tasks = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                tasks.add(new BasicTask(scanner.nextInt(), scanner.nextInt(), i));
            }
            System.out.println(joinInts(processByDeadlineStable(tasks)));
        } else if (part == 2) {
            int n = scanner.nextInt();
            int m = scanner.nextInt();
            int[] deadlines = new int[n + 1];
            for (int taskId = 1; taskId <= n; taskId++) {
                deadlines[taskId] = scanner.nextInt();
            }

            List<SubtaskEdge> edges = new ArrayList<>();
            for (int i = 0; i < m; i++) {
                edges.add(new SubtaskEdge(scanner.nextInt(), scanner.nextInt()));
            }

            SubtaskResult result = processWithSubtasks(deadlines, edges);
            System.out.println(joinInts(result.subtaskOrder));
            System.out.println(joinInts(result.taskOrder));
        } else {
            for (String note : productionConsiderations()) {
                System.out.println("- " + note);
            }
        }
    }

    private static void runSamples() {
        List<BasicTask> tasks = new ArrayList<>();
        tasks.add(new BasicTask(1, 10, 0));
        tasks.add(new BasicTask(2, 5, 1));
        tasks.add(new BasicTask(3, 7, 2));
        tasks.add(new BasicTask(4, 5, 3));
        tasks.add(new BasicTask(5, 12, 4));
        System.out.println("Part 1 sample:");
        System.out.println(joinInts(processByDeadlineStable(tasks)));

        int[] deadlines = {0, 10, 5, 7};
        List<SubtaskEdge> edges = new ArrayList<>();
        edges.add(new SubtaskEdge(1, 1));
        edges.add(new SubtaskEdge(2, 1));
        edges.add(new SubtaskEdge(3, 2));
        edges.add(new SubtaskEdge(4, 3));
        SubtaskResult result = processWithSubtasks(deadlines, edges);
        System.out.println("Part 2 sample:");
        System.out.println(joinInts(result.subtaskOrder));
        System.out.println(joinInts(result.taskOrder));
    }
}
