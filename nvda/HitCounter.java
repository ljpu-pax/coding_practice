package nvda;

import java.util.*;

/**
 * LeetCode 362: Design Hit Counter
 * 
 * Problem: Design a hit counter which counts the number of hits received in the past 5 minutes (i.e., the past 300 seconds).
 * 
 * Your system should accept a timestamp parameter (in seconds granularity), and you may assume that 
 * calls are being made to the system in chronological order (i.e., timestamp is monotonically increasing). 
 * Several hits may arrive roughly at the same time.
 * 
 * Implement the HitCounter class:
 * - HitCounter() Initializes the object of the hit counter.
 * - void hit(int timestamp) Records a hit that happened at timestamp (in seconds). Several hits may happen at the same timestamp.
 * - int getHits(int timestamp) Returns the number of hits in the past 5 minutes from timestamp (i.e., the past 300 seconds).
 * 
 * Example:
 * HitCounter hitCounter = new HitCounter();
 * hitCounter.hit(1);       // hit at timestamp 1.
 * hitCounter.hit(2);       // hit at timestamp 2.
 * hitCounter.hit(3);       // hit at timestamp 3.
 * hitCounter.getHits(4);   // get hits at timestamp 4, return 3.
 * hitCounter.hit(300);     // hit at timestamp 300.
 * hitCounter.getHits(300); // get hits at timestamp 300, return 4.
 * hitCounter.getHits(301); // get hits at timestamp 301, return 3.
 * 
 * Constraints:
 * - 1 <= timestamp <= 2 * 10^9
 * - All the calls are being made to the system in chronological order (i.e., timestamp is monotonically increasing).
 * - At most 300 calls will be made to hit and getHits.
 * 
 * Follow up: What if the number of hits per second could be huge? Does your design scale?
 * How would you design a hit counter to handle more traffic?
 */
public class HitCounter {
    
    /**
     * Approach 1: Queue (Simple and Intuitive)
     * 
     * Store all hit timestamps in a queue and remove old ones when queried.
     * 
     * Time Complexity: 
     * - hit(): O(1)
     * - getHits(): O(n) where n is number of hits in past 5 minutes
     * Space Complexity: O(n) where n is number of hits in past 5 minutes
     */
    static class HitCounterQueue {
        private Queue<Integer> hits;
        
        public HitCounterQueue() {
            hits = new LinkedList<>();
        }
        
        public void hit(int timestamp) {
            hits.offer(timestamp);
        }
        
        public int getHits(int timestamp) {
            // Remove hits older than 5 minutes (300 seconds)
            while (!hits.isEmpty() && timestamp - hits.peek() >= 300) {
                hits.poll();
            }
            return hits.size();
        }
    }
    
    /**
     * Approach 2: Circular Array (Optimized for High Traffic)
     * 
     * Use a circular array to store hit counts for each second in a 5-minute window.
     * This approach is more efficient when there are many hits per second.
     * 
     * Time Complexity: 
     * - hit(): O(1)
     * - getHits(): O(300) = O(1) constant time
     * Space Complexity: O(300) = O(1) constant space
     */
    static class HitCounterCircularArray {
        private int[] times;   // timestamps
        private int[] hits;    // hit counts
        private static final int WINDOW_SIZE = 300;
        
        public HitCounterCircularArray() {
            times = new int[WINDOW_SIZE];
            hits = new int[WINDOW_SIZE];
        }
        
        public void hit(int timestamp) {
            int index = timestamp % WINDOW_SIZE;
            
            // If this slot has a different timestamp, reset it
            if (times[index] != timestamp) {
                times[index] = timestamp;
                hits[index] = 1;
            } else {
                hits[index]++;
            }
        }
        
        public int getHits(int timestamp) {
            int totalHits = 0;
            
            for (int i = 0; i < WINDOW_SIZE; i++) {
                // Only count hits within the 5-minute window
                if (timestamp - times[i] < WINDOW_SIZE) {
                    totalHits += hits[i];
                }
            }
            
            return totalHits;
        }
    }
    
    /**
     * Approach 3: Deque with Pairs (Handles Multiple Hits per Timestamp)
     * 
     * Store pairs of (timestamp, count) to handle multiple hits at the same timestamp efficiently.
     * 
     * Time Complexity:
     * - hit(): O(1) amortized
     * - getHits(): O(k) where k is number of unique timestamps in past 5 minutes
     * Space Complexity: O(k) where k is number of unique timestamps in past 5 minutes
     */
    static class HitCounterDeque {
        private Deque<int[]> hits; // [timestamp, count]
        
        public HitCounterDeque() {
            hits = new LinkedList<>();
        }
        
        public void hit(int timestamp) {
            // If the last entry has the same timestamp, increment count
            if (!hits.isEmpty() && hits.peekLast()[0] == timestamp) {
                hits.peekLast()[1]++;
            } else {
                // Add new entry
                hits.offerLast(new int[]{timestamp, 1});
            }
        }
        
        public int getHits(int timestamp) {
            // Remove entries older than 5 minutes
            while (!hits.isEmpty() && timestamp - hits.peekFirst()[0] >= 300) {
                hits.pollFirst();
            }
            
            // Sum up all hits in the valid window
            int totalHits = 0;
            for (int[] entry : hits) {
                totalHits += entry[1];
            }
            
            return totalHits;
        }
    }
    
    /**
     * Approach 4: TreeMap (For Non-Chronological Order)
     * 
     * Although the problem states chronological order, this approach handles
     * any order of timestamps using a TreeMap.
     * 
     * Time Complexity:
     * - hit(): O(log n)
     * - getHits(): O(log n + k) where k is number of entries in range
     * Space Complexity: O(n) where n is number of unique timestamps
     */
    static class HitCounterTreeMap {
        private TreeMap<Integer, Integer> hitMap; // timestamp -> count
        
        public HitCounterTreeMap() {
            hitMap = new TreeMap<>();
        }
        
        public void hit(int timestamp) {
            hitMap.put(timestamp, hitMap.getOrDefault(timestamp, 0) + 1);
        }
        
        public int getHits(int timestamp) {
            // Remove entries older than 5 minutes
            int cutoff = timestamp - 299; // 300 seconds ago
            
            // Remove old entries
            Iterator<Map.Entry<Integer, Integer>> iterator = hitMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, Integer> entry = iterator.next();
                if (entry.getKey() < cutoff) {
                    iterator.remove();
                } else {
                    break; // TreeMap is sorted, so we can break early
                }
            }
            
            // Sum up remaining hits
            int totalHits = 0;
            for (int count : hitMap.values()) {
                totalHits += count;
            }
            
            return totalHits;
        }
    }
    
    /**
     * Approach 5: Sliding Window with List (Memory Efficient)
     * 
     * Uses a list to store timestamps and binary search for efficient cleanup.
     * 
     * Time Complexity:
     * - hit(): O(1)
     * - getHits(): O(log n + k) where k is number of old entries to remove
     * Space Complexity: O(n) where n is number of hits in past 5 minutes
     */
    static class HitCounterSlidingWindow {
        private List<Integer> hits;
        
        public HitCounterSlidingWindow() {
            hits = new ArrayList<>();
        }
        
        public void hit(int timestamp) {
            hits.add(timestamp);
        }
        
        public int getHits(int timestamp) {
            int cutoff = timestamp - 299; // 300 seconds ago
            
            // Find the first valid timestamp using binary search
            int left = 0, right = hits.size() - 1;
            int firstValid = hits.size();
            
            while (left <= right) {
                int mid = left + (right - left) / 2;
                if (hits.get(mid) >= cutoff) {
                    firstValid = mid;
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            }
            
            // Remove old entries
            if (firstValid > 0) {
                hits.subList(0, firstValid).clear();
            }
            
            return hits.size();
        }
    }
    
    // Test cases
    public static void main(String[] args) {
        System.out.println("Testing Hit Counter - Multiple Approaches\n");
        
        // Test all approaches
        System.out.println("=== Testing Queue Approach ===");
        testHitCounterQueue();
        
        System.out.println("\n=== Testing Circular Array Approach ===");
        testHitCounterCircularArray();
        
        System.out.println("\n=== Testing Deque Approach ===");
        testHitCounterDeque();
        
        System.out.println("\n=== Testing TreeMap Approach ===");
        testHitCounterTreeMap();
        
        System.out.println("\n=== Testing Sliding Window Approach ===");
        testHitCounterSlidingWindow();
        
        // Performance comparison
        System.out.println("\n=== Performance Comparison ===");
        performanceTest();
        
        // Edge cases
        System.out.println("\n=== Edge Cases ===");
        testEdgeCases();
    }
    
    private static void testHitCounterQueue() {
        HitCounterQueue counter = new HitCounterQueue();
        runBasicTest(counter::hit, counter::getHits);
    }
    
    private static void testHitCounterCircularArray() {
        HitCounterCircularArray counter = new HitCounterCircularArray();
        runBasicTest(counter::hit, counter::getHits);
    }
    
    private static void testHitCounterDeque() {
        HitCounterDeque counter = new HitCounterDeque();
        runBasicTest(counter::hit, counter::getHits);
    }
    
    private static void testHitCounterTreeMap() {
        HitCounterTreeMap counter = new HitCounterTreeMap();
        runBasicTest(counter::hit, counter::getHits);
    }
    
    private static void testHitCounterSlidingWindow() {
        HitCounterSlidingWindow counter = new HitCounterSlidingWindow();
        runBasicTest(counter::hit, counter::getHits);
    }
    
    private static void runBasicTest(HitFunction hitFunc, GetHitsFunction getHitsFunc) {
        hitFunc.hit(1);
        hitFunc.hit(2);
        hitFunc.hit(3);
        System.out.println("getHits(4): " + getHitsFunc.getHits(4)); // Expected: 3
        
        hitFunc.hit(300);
        System.out.println("getHits(300): " + getHitsFunc.getHits(300)); // Expected: 4
        System.out.println("getHits(301): " + getHitsFunc.getHits(301)); // Expected: 3
        
        // Additional tests
        hitFunc.hit(301);
        hitFunc.hit(301);
        System.out.println("getHits(302): " + getHitsFunc.getHits(302)); // Expected: 5
        System.out.println("getHits(600): " + getHitsFunc.getHits(600)); // Expected: 2
    }
    
    @FunctionalInterface
    interface HitFunction {
        void hit(int timestamp);
    }
    
    @FunctionalInterface
    interface GetHitsFunction {
        int getHits(int timestamp);
    }
    
    private static void performanceTest() {
        int numOperations = 1000;
        
        // Test Queue approach
        HitCounterQueue counter1 = new HitCounterQueue();
        long start = System.nanoTime();
        for (int i = 1; i <= numOperations; i++) {
            counter1.hit(i);
            if (i % 100 == 0) counter1.getHits(i);
        }
        long end = System.nanoTime();
        System.out.println("Queue approach: " + (end - start) / 1000000.0 + " ms");
        
        // Test Circular Array approach
        HitCounterCircularArray counter2 = new HitCounterCircularArray();
        start = System.nanoTime();
        for (int i = 1; i <= numOperations; i++) {
            counter2.hit(i);
            if (i % 100 == 0) counter2.getHits(i);
        }
        end = System.nanoTime();
        System.out.println("Circular Array approach: " + (end - start) / 1000000.0 + " ms");
        
        // Test Deque approach
        HitCounterDeque counter3 = new HitCounterDeque();
        start = System.nanoTime();
        for (int i = 1; i <= numOperations; i++) {
            counter3.hit(i);
            if (i % 100 == 0) counter3.getHits(i);
        }
        end = System.nanoTime();
        System.out.println("Deque approach: " + (end - start) / 1000000.0 + " ms");
    }
    
    private static void testEdgeCases() {
        System.out.println("Testing edge cases:");
        
        HitCounterQueue counter = new HitCounterQueue();
        
        // Test with no hits
        System.out.println("getHits(100) with no hits: " + counter.getHits(100)); // Expected: 0
        
        // Test with hits at boundary
        counter.hit(1);
        System.out.println("getHits(300): " + counter.getHits(300)); // Expected: 1
        System.out.println("getHits(301): " + counter.getHits(301)); // Expected: 0
        
        // Test with many hits at same timestamp
        for (int i = 0; i < 10; i++) {
            counter.hit(500);
        }
        System.out.println("getHits(500) after 10 hits at timestamp 500: " + counter.getHits(500)); // Expected: 10
        System.out.println("getHits(800): " + counter.getHits(800)); // Expected: 10
        System.out.println("getHits(801): " + counter.getHits(801)); // Expected: 0
    }
}
