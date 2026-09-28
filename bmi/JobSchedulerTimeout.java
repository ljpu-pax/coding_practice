import java.util.*;

/**
 * Applied Intuition Interview Question
 *
 * Problem: Job Scheduler - Find timed out events
 *
 * Input:
 * - timeout: time limit before an event is considered timed out
 * - events: list of events with timestamp and type
 *
 * Event types:
 * - START: event begins
 * - PING: event updates its status (heartbeat)
 * - END: event completes
 *
 * Output: List of event IDs that have timed out
 *
 * An event times out if:
 * - Current time - last update time > timeout
 * - Last update can be START or PING (END removes the event)
 *
 * Example:
 * timeout = 5
 * events = [
 *   (0, "job1", START),
 *   (2, "job2", START),
 *   (3, "job1", PING),
 *   (6, "job2", PING),
 *   (10, "job3", START),
 *   (11, "job1", END)
 * ]
 * At time 10: job1 timed out (last ping at 3, 10-3=7 > 5)
 * At time 11: job1 ended (removed from tracking)
 * At time 12: job2 timed out (last ping at 6, 12-6=6 > 5)
 */

class JobSchedulerTimeout {

    enum EventType {
        START, PING, END
    }

    static class Event {
        int timestamp;
        String jobId;
        EventType type;

        Event(int timestamp, String jobId, EventType type) {
            this.timestamp = timestamp;
            this.jobId = jobId;
            this.type = type;
        }

        @Override
        public String toString() {
            return String.format("(%d, %s, %s)", timestamp, jobId, type);
        }
    }

    /**
     * Approach 1: Process events sequentially and track last update time
     *
     * Time: O(N) where N = number of events
     * Space: O(M) where M = number of active jobs
     */
    public List<String> findTimedOutEvents(int timeout, List<Event> events) {
        Set<String> timedOutJobs = new HashSet<>();
        Map<String, Integer> jobLastUpdate = new HashMap<>(); // jobId -> last update timestamp

        for (Event event : events) {
            String jobId = event.jobId;
            int currentTime = event.timestamp;

            // Check if any active jobs have timed out at current time
            List<String> newTimeouts = checkTimeouts(jobLastUpdate, currentTime, timeout, timedOutJobs);
            timedOutJobs.addAll(newTimeouts);

            // Process current event
            switch (event.type) {
                case START:
                    jobLastUpdate.put(jobId, currentTime);
                    break;

                case PING:
                    if (jobLastUpdate.containsKey(jobId)) {
                        jobLastUpdate.put(jobId, currentTime);
                    }
                    break;

                case END:
                    jobLastUpdate.remove(jobId);
                    // Note: we keep it in timedOutJobs if it already timed out
                    break;
            }
        }

        return new ArrayList<>(timedOutJobs);
    }

    /**
     * Check which jobs have timed out at the current time
     */
    private List<String> checkTimeouts(Map<String, Integer> jobLastUpdate,
                                       int currentTime, int timeout,
                                       Set<String> alreadyTimedOut) {
        List<String> newTimeouts = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : jobLastUpdate.entrySet()) {
            String jobId = entry.getKey();
            int lastUpdate = entry.getValue();

            if (!alreadyTimedOut.contains(jobId) &&
                currentTime - lastUpdate > timeout) {
                newTimeouts.add(jobId);
            }
        }

        return newTimeouts;
    }

    /**
     * Approach 2: Return timeout info with timestamp when timeout was detected
     */
    static class TimeoutInfo {
        String jobId;
        int timeoutDetectedAt;
        int lastUpdateTime;

        TimeoutInfo(String jobId, int timeoutDetectedAt, int lastUpdateTime) {
            this.jobId = jobId;
            this.timeoutDetectedAt = timeoutDetectedAt;
            this.lastUpdateTime = lastUpdateTime;
        }

        @Override
        public String toString() {
            return String.format("%s timed out at %d (last update: %d)",
                               jobId, timeoutDetectedAt, lastUpdateTime);
        }
    }

    public List<TimeoutInfo> findTimedOutEventsWithInfo(int timeout, List<Event> events) {
        List<TimeoutInfo> timedOutJobs = new ArrayList<>();
        Set<String> alreadyTimedOut = new HashSet<>();
        Map<String, Integer> jobLastUpdate = new HashMap<>();

        for (Event event : events) {
            String jobId = event.jobId;
            int currentTime = event.timestamp;

            // Check for timeouts at current time
            for (Map.Entry<String, Integer> entry : jobLastUpdate.entrySet()) {
                String activeJobId = entry.getKey();
                int lastUpdate = entry.getValue();

                if (!alreadyTimedOut.contains(activeJobId) &&
                    currentTime - lastUpdate > timeout) {
                    timedOutJobs.add(new TimeoutInfo(activeJobId, currentTime, lastUpdate));
                    alreadyTimedOut.add(activeJobId);
                }
            }

            // Process current event
            switch (event.type) {
                case START:
                    jobLastUpdate.put(jobId, currentTime);
                    break;

                case PING:
                    if (jobLastUpdate.containsKey(jobId)) {
                        jobLastUpdate.put(jobId, currentTime);
                    }
                    break;

                case END:
                    jobLastUpdate.remove(jobId);
                    break;
            }
        }

        return timedOutJobs;
    }

    /**
     * Approach 3: Query-based - check timeout status at specific time
     *
     * Useful when you want to query: "Which jobs are timed out at time T?"
     */
    public List<String> getTimedOutJobsAtTime(int timeout, List<Event> events, int queryTime) {
        Map<String, Integer> jobLastUpdate = new HashMap<>();

        // Process all events up to queryTime
        for (Event event : events) {
            if (event.timestamp > queryTime) {
                break;
            }

            switch (event.type) {
                case START:
                    jobLastUpdate.put(event.jobId, event.timestamp);
                    break;

                case PING:
                    if (jobLastUpdate.containsKey(event.jobId)) {
                        jobLastUpdate.put(event.jobId, event.timestamp);
                    }
                    break;

                case END:
                    jobLastUpdate.remove(event.jobId);
                    break;
            }
        }

        // Find timed out jobs at queryTime
        List<String> timedOutJobs = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : jobLastUpdate.entrySet()) {
            if (queryTime - entry.getValue() > timeout) {
                timedOutJobs.add(entry.getKey());
            }
        }

        return timedOutJobs;
    }

    /**
     * Approach 4: Real-time monitoring with priority queue
     *
     * Use min-heap to efficiently find next job that will timeout
     * Useful for real-time systems that need to trigger alerts
     */
    static class JobStatus implements Comparable<JobStatus> {
        String jobId;
        int lastUpdate;

        JobStatus(String jobId, int lastUpdate) {
            this.jobId = jobId;
            this.lastUpdate = lastUpdate;
        }

        @Override
        public int compareTo(JobStatus other) {
            return Integer.compare(this.lastUpdate, other.lastUpdate);
        }
    }

    public List<TimeoutInfo> findTimedOutEventsWithPriorityQueue(int timeout, List<Event> events) {
        List<TimeoutInfo> timedOutJobs = new ArrayList<>();
        Set<String> alreadyTimedOut = new HashSet<>();
        Map<String, Integer> jobLastUpdate = new HashMap<>();
        PriorityQueue<JobStatus> pq = new PriorityQueue<>();

        for (Event event : events) {
            int currentTime = event.timestamp;

            // Check timeouts using priority queue
            while (!pq.isEmpty() && currentTime - pq.peek().lastUpdate > timeout) {
                JobStatus expired = pq.poll();
                if (!alreadyTimedOut.contains(expired.jobId) &&
                    jobLastUpdate.containsKey(expired.jobId) &&
                    jobLastUpdate.get(expired.jobId) == expired.lastUpdate) {

                    timedOutJobs.add(new TimeoutInfo(expired.jobId, currentTime, expired.lastUpdate));
                    alreadyTimedOut.add(expired.jobId);
                }
            }

            // Process event
            switch (event.type) {
                case START:
                    jobLastUpdate.put(event.jobId, currentTime);
                    pq.offer(new JobStatus(event.jobId, currentTime));
                    break;

                case PING:
                    if (jobLastUpdate.containsKey(event.jobId)) {
                        jobLastUpdate.put(event.jobId, currentTime);
                        pq.offer(new JobStatus(event.jobId, currentTime));
                    }
                    break;

                case END:
                    jobLastUpdate.remove(event.jobId);
                    break;
            }
        }

        return timedOutJobs;
    }
}

/**
 * Test cases
 */
class JobSchedulerTimeoutTest {
    public static void main(String[] args) {
        testBasicScenarios();
        testComplexScenarios();
        testQueryAtTime();
    }

    private static void testBasicScenarios() {
        System.out.println("=== Testing Basic Scenarios ===\n");
        JobSchedulerTimeout scheduler = new JobSchedulerTimeout();

        // Test 1: Simple timeout case
        int timeout1 = 5;
        List<JobSchedulerTimeout.Event> events1 = Arrays.asList(
            new JobSchedulerTimeout.Event(0, "job1", JobSchedulerTimeout.EventType.START),
            new JobSchedulerTimeout.Event(3, "job1", JobSchedulerTimeout.EventType.PING),
            new JobSchedulerTimeout.Event(10, "job2", JobSchedulerTimeout.EventType.START)
        );

        List<String> result1 = scheduler.findTimedOutEvents(timeout1, events1);
        System.out.println("Test 1: Basic timeout");
        System.out.println("  Timeout: " + timeout1);
        System.out.println("  Events: " + events1);
        System.out.println("  Timed out jobs: " + result1);
        System.out.println("  Expected: [job1] - last ping at 3, checked at 10, 10-3=7 > 5");
        System.out.println();

        // Test 2: Job ends before timeout
        int timeout2 = 10;
        List<JobSchedulerTimeout.Event> events2 = Arrays.asList(
            new JobSchedulerTimeout.Event(0, "job1", JobSchedulerTimeout.EventType.START),
            new JobSchedulerTimeout.Event(5, "job1", JobSchedulerTimeout.EventType.END),
            new JobSchedulerTimeout.Event(20, "job2", JobSchedulerTimeout.EventType.START)
        );

        List<String> result2 = scheduler.findTimedOutEvents(timeout2, events2);
        System.out.println("Test 2: Job ends before timeout");
        System.out.println("  Timeout: " + timeout2);
        System.out.println("  Timed out jobs: " + result2);
        System.out.println("  Expected: [] - job1 ended at 5, no timeout");
        System.out.println();

        // Test 3: Multiple pings keep job alive
        int timeout3 = 5;
        List<JobSchedulerTimeout.Event> events3 = Arrays.asList(
            new JobSchedulerTimeout.Event(0, "job1", JobSchedulerTimeout.EventType.START),
            new JobSchedulerTimeout.Event(4, "job1", JobSchedulerTimeout.EventType.PING),
            new JobSchedulerTimeout.Event(8, "job1", JobSchedulerTimeout.EventType.PING),
            new JobSchedulerTimeout.Event(12, "job1", JobSchedulerTimeout.EventType.PING),
            new JobSchedulerTimeout.Event(20, "job2", JobSchedulerTimeout.EventType.START)
        );

        List<String> result3 = scheduler.findTimedOutEvents(timeout3, events3);
        System.out.println("Test 3: Pings keep job alive");
        System.out.println("  Timeout: " + timeout3);
        System.out.println("  Timed out jobs: " + result3);
        System.out.println("  Expected: [job1] - last ping at 12, checked at 20, 20-12=8 > 5");
        System.out.println();
    }

    private static void testComplexScenarios() {
        System.out.println("=== Testing Complex Scenarios ===\n");
        JobSchedulerTimeout scheduler = new JobSchedulerTimeout();

        // Test 1: Multiple jobs with different behaviors
        int timeout = 5;
        List<JobSchedulerTimeout.Event> events = Arrays.asList(
            new JobSchedulerTimeout.Event(0, "job1", JobSchedulerTimeout.EventType.START),
            new JobSchedulerTimeout.Event(2, "job2", JobSchedulerTimeout.EventType.START),
            new JobSchedulerTimeout.Event(3, "job1", JobSchedulerTimeout.EventType.PING),
            new JobSchedulerTimeout.Event(4, "job3", JobSchedulerTimeout.EventType.START),
            new JobSchedulerTimeout.Event(6, "job2", JobSchedulerTimeout.EventType.PING),
            new JobSchedulerTimeout.Event(10, "job4", JobSchedulerTimeout.EventType.START),
            new JobSchedulerTimeout.Event(11, "job1", JobSchedulerTimeout.EventType.END),
            new JobSchedulerTimeout.Event(15, "job3", JobSchedulerTimeout.EventType.PING),
            new JobSchedulerTimeout.Event(20, "job5", JobSchedulerTimeout.EventType.START)
        );

        List<JobSchedulerTimeout.TimeoutInfo> result = scheduler.findTimedOutEventsWithInfo(timeout, events);
        System.out.println("Test 1: Multiple jobs tracking");
        System.out.println("  Timeout: " + timeout);
        System.out.println("  Timed out jobs:");
        for (JobSchedulerTimeout.TimeoutInfo info : result) {
            System.out.println("    " + info);
        }
        System.out.println();

        // Test 2: Same job timing out after being restarted
        List<JobSchedulerTimeout.Event> events2 = Arrays.asList(
            new JobSchedulerTimeout.Event(0, "job1", JobSchedulerTimeout.EventType.START),
            new JobSchedulerTimeout.Event(10, "job1", JobSchedulerTimeout.EventType.END),
            new JobSchedulerTimeout.Event(15, "job1", JobSchedulerTimeout.EventType.START),
            new JobSchedulerTimeout.Event(25, "job2", JobSchedulerTimeout.EventType.START)
        );

        List<String> result2 = scheduler.findTimedOutEvents(timeout, events2);
        System.out.println("Test 2: Job restart scenario");
        System.out.println("  Timed out jobs: " + result2);
        System.out.println("  Expected: [job1] - restarted at 15, checked at 25, 25-15=10 > 5");
        System.out.println();
    }

    private static void testQueryAtTime() {
        System.out.println("=== Testing Query At Specific Time ===\n");
        JobSchedulerTimeout scheduler = new JobSchedulerTimeout();

        int timeout = 5;
        List<JobSchedulerTimeout.Event> events = Arrays.asList(
            new JobSchedulerTimeout.Event(0, "job1", JobSchedulerTimeout.EventType.START),
            new JobSchedulerTimeout.Event(2, "job2", JobSchedulerTimeout.EventType.START),
            new JobSchedulerTimeout.Event(3, "job1", JobSchedulerTimeout.EventType.PING),
            new JobSchedulerTimeout.Event(6, "job2", JobSchedulerTimeout.EventType.PING),
            new JobSchedulerTimeout.Event(10, "job3", JobSchedulerTimeout.EventType.START)
        );

        // Query at different times
        int[] queryTimes = {5, 10, 15};
        for (int queryTime : queryTimes) {
            List<String> timedOut = scheduler.getTimedOutJobsAtTime(timeout, events, queryTime);
            System.out.println("At time " + queryTime + ": Timed out jobs = " + timedOut);
        }
        System.out.println();
    }
}

/**
 * Follow-up Questions
 */
class JobSchedulerFollowUps {
    /*
     * Q1: What if we need to handle millions of events in real-time?
     * A1:
     *   - Use priority queue to efficiently find next timeout
     *   - Implement sliding window for active jobs
     *   - Use time-series database for event storage
     *   - Partition events by job ID for parallel processing
     *
     * Q2: How to handle events arriving out of order?
     * A2:
     *   - Buffer events and sort by timestamp
     *   - Use watermark strategy (process events within time window)
     *   - Late events can be handled with grace period
     *   - Maintain event ordering per job ID
     *
     * Q3: What if timeout threshold is different per job?
     * A3:
     *   - Store timeout value per job ID
     *   - Priority queue ordered by (lastUpdate + timeout)
     *   - Check job-specific timeout in comparison
     *
     * Q4: How to implement timeout alerts in production?
     * A4:
     *   - Use scheduled background job to check timeouts
     *   - Implement callback/webhook when timeout detected
     *   - Use message queue for async timeout notifications
     *   - Store timeout events in separate table for audit
     *
     * Q5: How to optimize memory usage for long-running system?
     * A5:
     *   - Periodically clean up completed jobs
     *   - Use LRU cache with size limit
     *   - Archive old events to persistent storage
     *   - Implement checkpointing for job states
     */
}
