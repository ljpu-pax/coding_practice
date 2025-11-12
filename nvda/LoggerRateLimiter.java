package nvda;

import java.util.*;

/**
 * LeetCode 359: Logger Rate Limiter
 * 
 * Problem: Design a logger system that receives a stream of messages along with their timestamps. 
 * Each unique message should only be printed at most every 10 seconds (i.e. a message printed 
 * at timestamp t will prevent other identical messages from being printed until timestamp t + 10).
 * 
 * All messages will come in chronological order. Several messages may arrive at the same timestamp.
 * 
 * Implement the Logger class:
 * - Logger() Initializes the logger object.
 * - boolean shouldPrintMessage(int timestamp, String message) Returns true if the message should 
 *   be printed in the given timestamp, otherwise returns false.
 * 
 * Example:
 * Logger logger = new Logger();
 * logger.shouldPrintMessage(1, "foo");  // return true, next allowed timestamp for "foo" is 1 + 10 = 11
 * logger.shouldPrintMessage(2, "bar");  // return true, next allowed timestamp for "bar" is 2 + 10 = 12
 * logger.shouldPrintMessage(3, "foo");  // 3 < 11, return false
 * logger.shouldPrintMessage(8, "bar");  // 8 < 12, return false
 * logger.shouldPrintMessage(10, "foo"); // 10 < 11, return false
 * logger.shouldPrintMessage(11, "foo"); // 11 >= 11, return true, next allowed timestamp for "foo" is 11 + 10 = 21
 * 
 * Constraints:
 * - 0 <= timestamp <= 10^9
 * - Every timestamp will be passed in non-decreasing order (chronological order).
 * - 1 <= message.length <= 30
 * - At most 10^4 calls will be made to shouldPrintMessage.
 */
public class LoggerRateLimiter {
    
    /**
     * Approach 1: HashMap (Simple and Efficient)
     * 
     * Store the next allowed timestamp for each message.
     * 
     * Time Complexity: O(1) per operation
     * Space Complexity: O(M) where M is the number of unique messages
     */
    static class Logger {
        private Map<String, Integer> messageTimestamps;
        
        public Logger() {
            messageTimestamps = new HashMap<>();
        }
        
        public boolean shouldPrintMessage(int timestamp, String message) {
            // Check if message exists and if current timestamp is before next allowed time
            if (messageTimestamps.containsKey(message) && timestamp < messageTimestamps.get(message)) {
                return false;
            }
            
            // Update next allowed timestamp for this message
            messageTimestamps.put(message, timestamp + 10);
            return true;
        }
    }
    
    /**
     * Approach 2: HashMap with Cleanup (Memory Optimized)
     * 
     * Same as approach 1 but periodically cleans up old entries to save memory.
     * This is useful when we have many unique messages over a long period.
     * 
     * Time Complexity: O(1) amortized per operation
     * Space Complexity: O(M) where M is the number of unique messages in recent time window
     */
    static class LoggerWithCleanup {
        private Map<String, Integer> messageTimestamps;
        private int lastCleanupTime;
        private static final int CLEANUP_INTERVAL = 100; // Clean up every 100 timestamps
        
        public LoggerWithCleanup() {
            messageTimestamps = new HashMap<>();
            lastCleanupTime = 0;
        }
        
        public boolean shouldPrintMessage(int timestamp, String message) {
            // Periodic cleanup to remove old entries
            if (timestamp - lastCleanupTime >= CLEANUP_INTERVAL) {
                cleanup(timestamp);
                lastCleanupTime = timestamp;
            }
            
            if (messageTimestamps.containsKey(message) && timestamp < messageTimestamps.get(message)) {
                return false;
            }
            
            messageTimestamps.put(message, timestamp + 10);
            return true;
        }
        
        private void cleanup(int currentTimestamp) {
            Iterator<Map.Entry<String, Integer>> iterator = messageTimestamps.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Integer> entry = iterator.next();
                // Remove entries that are no longer relevant (next allowed time has passed)
                if (entry.getValue() <= currentTimestamp) {
                    iterator.remove();
                }
            }
        }
    }
    
    /**
     * Approach 3: Deque + Set (Alternative Implementation)
     * 
     * Uses a deque to maintain chronological order and a set for O(1) lookups.
     * This approach explicitly manages the 10-second window.
     * 
     * Time Complexity: O(1) amortized per operation
     * Space Complexity: O(M) where M is the number of unique messages in 10-second window
     */
    static class LoggerWithDeque {
        private Deque<Pair> messageQueue;
        private Set<String> messageSet;
        
        private static class Pair {
            int timestamp;
            String message;
            
            Pair(int timestamp, String message) {
                this.timestamp = timestamp;
                this.message = message;
            }
        }
        
        public LoggerWithDeque() {
            messageQueue = new LinkedList<>();
            messageSet = new HashSet<>();
        }
        
        public boolean shouldPrintMessage(int timestamp, String message) {
            // Remove old messages that are outside the 10-second window
            while (!messageQueue.isEmpty() && messageQueue.peekFirst().timestamp <= timestamp - 10) {
                Pair oldPair = messageQueue.pollFirst();
                messageSet.remove(oldPair.message);
            }
            
            // Check if message is already in the current window
            if (messageSet.contains(message)) {
                return false;
            }
            
            // Add new message
            messageQueue.offerLast(new Pair(timestamp, message));
            messageSet.add(message);
            return true;
        }
    }
    
    /**
     * Approach 4: Circular Buffer (Fixed Size Implementation)
     * 
     * Uses a circular buffer for scenarios where we want to limit memory usage
     * to a fixed size regardless of the number of unique messages.
     * 
     * Time Complexity: O(k) per operation where k is buffer size
     * Space Complexity: O(k) where k is buffer size
     */
    static class LoggerWithCircularBuffer {
        private static class Entry {
            int timestamp;
            String message;
            
            Entry(int timestamp, String message) {
                this.timestamp = timestamp;
                this.message = message;
            }
        }
        
        private Entry[] buffer;
        private int size;
        private int head;
        private static final int BUFFER_SIZE = 1000;
        
        public LoggerWithCircularBuffer() {
            buffer = new Entry[BUFFER_SIZE];
            size = 0;
            head = 0;
        }
        
        public boolean shouldPrintMessage(int timestamp, String message) {
            // Check if message exists in recent entries
            for (int i = 0; i < size; i++) {
                int index = (head - 1 - i + BUFFER_SIZE) % BUFFER_SIZE;
                Entry entry = buffer[index];
                
                // If we find the message and it's within 10 seconds, reject
                if (entry.message.equals(message) && timestamp < entry.timestamp + 10) {
                    return false;
                }
                
                // If entry is too old, we can stop checking
                if (timestamp >= entry.timestamp + 10) {
                    break;
                }
            }
            
            // Add new entry
            buffer[head] = new Entry(timestamp, message);
            head = (head + 1) % BUFFER_SIZE;
            if (size < BUFFER_SIZE) {
                size++;
            }
            
            return true;
        }
    }
    
    // Test cases
    public static void main(String[] args) {
        System.out.println("Testing Logger Rate Limiter - Multiple Approaches\n");
        
        // Test Approach 1: Basic HashMap
        System.out.println("=== Testing Basic HashMap Approach ===");
        testLogger(new Logger());
        
        // Test Approach 2: HashMap with Cleanup
        System.out.println("\n=== Testing HashMap with Cleanup Approach ===");
        testLoggerWithCleanup(new LoggerWithCleanup());
        
        // Test Approach 3: Deque + Set
        System.out.println("\n=== Testing Deque + Set Approach ===");
        testLoggerWithDeque(new LoggerWithDeque());
        
        // Test Approach 4: Circular Buffer
        System.out.println("\n=== Testing Circular Buffer Approach ===");
        testLoggerWithCircularBuffer(new LoggerWithCircularBuffer());
        
        // Performance comparison
        System.out.println("\n=== Performance Comparison ===");
        performanceTest();
    }
    
    private static void testLogger(Logger logger) {
        System.out.println("shouldPrintMessage(1, \"foo\"): " + logger.shouldPrintMessage(1, "foo")); // true
        System.out.println("shouldPrintMessage(2, \"bar\"): " + logger.shouldPrintMessage(2, "bar")); // true
        System.out.println("shouldPrintMessage(3, \"foo\"): " + logger.shouldPrintMessage(3, "foo")); // false
        System.out.println("shouldPrintMessage(8, \"bar\"): " + logger.shouldPrintMessage(8, "bar")); // false
        System.out.println("shouldPrintMessage(10, \"foo\"): " + logger.shouldPrintMessage(10, "foo")); // false
        System.out.println("shouldPrintMessage(11, \"foo\"): " + logger.shouldPrintMessage(11, "foo")); // true
        
        // Additional test cases
        System.out.println("shouldPrintMessage(12, \"baz\"): " + logger.shouldPrintMessage(12, "baz")); // true
        System.out.println("shouldPrintMessage(13, \"baz\"): " + logger.shouldPrintMessage(13, "baz")); // false
        System.out.println("shouldPrintMessage(22, \"baz\"): " + logger.shouldPrintMessage(22, "baz")); // true
    }
    
    private static void testLoggerWithCleanup(LoggerWithCleanup logger) {
        System.out.println("shouldPrintMessage(1, \"foo\"): " + logger.shouldPrintMessage(1, "foo")); // true
        System.out.println("shouldPrintMessage(2, \"bar\"): " + logger.shouldPrintMessage(2, "bar")); // true
        System.out.println("shouldPrintMessage(3, \"foo\"): " + logger.shouldPrintMessage(3, "foo")); // false
        System.out.println("shouldPrintMessage(8, \"bar\"): " + logger.shouldPrintMessage(8, "bar")); // false
        System.out.println("shouldPrintMessage(10, \"foo\"): " + logger.shouldPrintMessage(10, "foo")); // false
        System.out.println("shouldPrintMessage(11, \"foo\"): " + logger.shouldPrintMessage(11, "foo")); // true
    }
    
    private static void testLoggerWithDeque(LoggerWithDeque logger) {
        System.out.println("shouldPrintMessage(1, \"foo\"): " + logger.shouldPrintMessage(1, "foo")); // true
        System.out.println("shouldPrintMessage(2, \"bar\"): " + logger.shouldPrintMessage(2, "bar")); // true
        System.out.println("shouldPrintMessage(3, \"foo\"): " + logger.shouldPrintMessage(3, "foo")); // false
        System.out.println("shouldPrintMessage(8, \"bar\"): " + logger.shouldPrintMessage(8, "bar")); // false
        System.out.println("shouldPrintMessage(10, \"foo\"): " + logger.shouldPrintMessage(10, "foo")); // false
        System.out.println("shouldPrintMessage(11, \"foo\"): " + logger.shouldPrintMessage(11, "foo")); // true
    }
    
    private static void testLoggerWithCircularBuffer(LoggerWithCircularBuffer logger) {
        System.out.println("shouldPrintMessage(1, \"foo\"): " + logger.shouldPrintMessage(1, "foo")); // true
        System.out.println("shouldPrintMessage(2, \"bar\"): " + logger.shouldPrintMessage(2, "bar")); // true
        System.out.println("shouldPrintMessage(3, \"foo\"): " + logger.shouldPrintMessage(3, "foo")); // false
        System.out.println("shouldPrintMessage(8, \"bar\"): " + logger.shouldPrintMessage(8, "bar")); // false
        System.out.println("shouldPrintMessage(10, \"foo\"): " + logger.shouldPrintMessage(10, "foo")); // false
        System.out.println("shouldPrintMessage(11, \"foo\"): " + logger.shouldPrintMessage(11, "foo")); // true
    }
    
    private static void performanceTest() {
        int numOperations = 10000;
        
        // Test HashMap approach
        Logger logger1 = new Logger();
        long start = System.nanoTime();
        for (int i = 0; i < numOperations; i++) {
            logger1.shouldPrintMessage(i, "message" + (i % 100));
        }
        long end = System.nanoTime();
        System.out.println("HashMap approach: " + (end - start) / 1000000.0 + " ms");
        
        // Test Deque approach
        LoggerWithDeque logger2 = new LoggerWithDeque();
        start = System.nanoTime();
        for (int i = 0; i < numOperations; i++) {
            logger2.shouldPrintMessage(i, "message" + (i % 100));
        }
        end = System.nanoTime();
        System.out.println("Deque + Set approach: " + (end - start) / 1000000.0 + " ms");
        
        // Test Circular Buffer approach
        LoggerWithCircularBuffer logger3 = new LoggerWithCircularBuffer();
        start = System.nanoTime();
        for (int i = 0; i < numOperations; i++) {
            logger3.shouldPrintMessage(i, "message" + (i % 100));
        }
        end = System.nanoTime();
        System.out.println("Circular Buffer approach: " + (end - start) / 1000000.0 + " ms");
    }
}
