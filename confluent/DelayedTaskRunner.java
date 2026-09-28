import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Confluent onsite (2020-2025): Thread-Safe Delayed Task Runner (low-level design).
 * Source: FastPrep "thread-safe-delayed-task-runner" (full statement not public;
 * this follows the reported summary: priority queue by deadline, blocking consumers,
 * multiple workers). Confirm the API with the interviewer.
 *
 * API:
 *   long schedule(Runnable task, long delayMs)  -> task id; run at now + delayMs
 *   boolean cancel(long id)                     -> true if it had not started yet
 *   void shutdown()                             -> stop workers; pending tasks are dropped
 *
 * Design:
 * - PriorityQueue<Task> ordered by (runAt, seq) - seq keeps FIFO for equal deadlines.
 * - One ReentrantLock + one Condition "changed".
 * - Worker loop:
 *     lock; while running:
 *        queue empty          -> changed.await()
 *        head not due         -> changed.awaitNanos(head.runAt - now)
 *        head due             -> poll, unlock, RUN OUTSIDE THE LOCK, relock
 * - schedule(): push; if the new task became the head, signal so a sleeping worker
 *   re-computes its wait (otherwise it would oversleep until the old head's deadline).
 * - cancel(): lazy - mark cancelled, workers skip it. O(1); queue.remove() is O(n).
 * - Why not busy-wait / Thread.sleep: wastes CPU and can't be woken by an earlier task.
 * - Spurious wakeups: every wait is inside a loop that re-checks state.
 * - A throwing task must not kill the worker -> catch around run().
 *
 * Follow-ups: this is java.util.concurrent.DelayQueue / ScheduledThreadPoolExecutor.
 *   Leader-follower: only one worker does the timed wait, others wait untimed
 *   (fewer wakeups). Recurring tasks: re-enqueue with runAt += period after running.
 *   Use System.nanoTime (monotonic), not currentTimeMillis (wall clock can jump).
 */
public class DelayedTaskRunner {

    static class Task {
        final long id, runAtNanos, seq;
        final Runnable body;
        volatile boolean cancelled, started;
        Task(long id, long runAtNanos, long seq, Runnable body) {
            this.id = id; this.runAtNanos = runAtNanos; this.seq = seq; this.body = body;
        }
    }

    private final PriorityQueue<Task> queue = new PriorityQueue<>(
        Comparator.comparingLong((Task t) -> t.runAtNanos).thenComparingLong(t -> t.seq));
    private final Map<Long, Task> byId = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition changed = lock.newCondition();
    private final AtomicLong ids = new AtomicLong();
    private final List<Thread> workers = new ArrayList<>();
    private boolean running = true;

    public DelayedTaskRunner(int workerCount) {
        for (int i = 0; i < workerCount; i++) {
            Thread t = new Thread(this::workerLoop, "delayed-worker-" + i);
            workers.add(t);
            t.start();
        }
    }

    public long schedule(Runnable body, long delayMs) {
        long id = ids.incrementAndGet();
        lock.lock();
        try {
            if (!running) throw new IllegalStateException("runner is shut down");
            Task t = new Task(id, System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(delayMs), id, body);
            queue.add(t);
            byId.put(id, t);
            if (queue.peek() == t) changed.signal(); // new earliest deadline
            return id;
        } finally { lock.unlock(); }
    }

    public boolean cancel(long id) {
        lock.lock();
        try {
            Task t = byId.remove(id);
            if (t == null || t.started) return false;
            t.cancelled = true;
            return true;
        } finally { lock.unlock(); }
    }

    public void shutdown() throws InterruptedException {
        lock.lock();
        try {
            running = false;
            changed.signalAll();
        } finally { lock.unlock(); }
        for (Thread t : workers) t.join();
    }

    private void workerLoop() {
        lock.lock();
        try {
            while (running) {
                Task head = queue.peek();
                if (head == null) {
                    changed.await();
                    continue;
                }
                if (head.cancelled) { queue.poll(); continue; }
                long wait = head.runAtNanos - System.nanoTime();
                if (wait > 0) {
                    changed.awaitNanos(wait);
                    continue; // re-check: maybe an earlier task arrived / shutdown
                }
                queue.poll();
                byId.remove(head.id);
                head.started = true;
                // Wake another worker in case the next task is also due.
                if (!queue.isEmpty()) changed.signal();
                lock.unlock();
                try {
                    head.body.run();
                } catch (RuntimeException e) {
                    System.err.println("task " + head.id + " failed: " + e);
                } finally {
                    lock.lock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DelayedTaskRunner runner = new DelayedTaskRunner(2);
        List<String> log = Collections.synchronizedList(new ArrayList<>());

        runner.schedule(() -> log.add("C@300"), 300);
        runner.schedule(() -> log.add("A@100"), 100);
        long cancelMe = runner.schedule(() -> log.add("X@200 (should be cancelled)"), 200);
        runner.schedule(() -> log.add("B@150"), 150);
        runner.schedule(() -> { throw new RuntimeException("boom"); }, 50); // worker must survive
        runner.schedule(() -> log.add("D@0"), 0);

        System.out.println("cancel X: " + runner.cancel(cancelMe)); // true
        Thread.sleep(500);
        System.out.println("cancel after run: " + runner.cancel(1)); // false (C already ran)
        runner.shutdown();
        System.out.println(log); // [D@0, A@100, B@150, C@300]

        // Many tasks, many workers: all run exactly once
        DelayedTaskRunner r2 = new DelayedTaskRunner(4);
        AtomicLong count = new AtomicLong();
        for (int i = 0; i < 1000; i++) r2.schedule(count::incrementAndGet, i % 50);
        Thread.sleep(300);
        r2.shutdown();
        System.out.println("ran: " + count.get()); // 1000
    }
}
