import java.util.*;

/**
 * LeetCode 432. All O`one Data Structure
 *
 * Problem:
 * Design a data structure to store strings' count with the ability to return
 * the strings with minimum and maximum counts.
 *
 * Implement the AllOne class:
 * - AllOne(): Initializes the object
 * - inc(String key): Increments the count of the string key by 1
 *   If key does not exist, insert it with count 1
 * - dec(String key): Decrements the count of the string key by 1
 *   If count becomes 0, remove the key. Assume key exists when called
 * - getMaxKey(): Returns one of the keys with the maximal count
 *   If no element exists, return ""
 * - getMinKey(): Returns one of the keys with the minimal count
 *   If no element exists, return ""
 *
 * ALL operations must be O(1) time complexity!
 *
 * Example:
 * AllOne allOne = new AllOne();
 * allOne.inc("hello");
 * allOne.inc("hello");
 * allOne.getMaxKey(); // return "hello"
 * allOne.getMinKey(); // return "hello"
 * allOne.inc("leet");
 * allOne.getMaxKey(); // return "hello"
 * allOne.getMinKey(); // return "leet"
 */

/**
 * Solution: HashMap + Doubly Linked List of Buckets
 *
 * Data Structures:
 * 1. HashMap<String, Integer>: key -> count
 * 2. HashMap<Integer, Bucket>: count -> bucket node
 * 3. Doubly Linked List of Buckets (sorted by count)
 *
 * Each Bucket contains:
 * - count: the count value
 * - keys: set of all keys with this count
 * - prev, next: pointers to adjacent buckets
 *
 * Key Insight:
 * - Counts change by 1 at a time (inc/dec)
 * - So we only need to move keys between adjacent buckets
 * - Maintain buckets in sorted order by count
 *
 * Time Complexity: ALL operations O(1)
 * Space Complexity: O(N) where N is number of unique keys
 */
class AllOne {

    // Bucket node in doubly linked list
    class Bucket {
        int count;
        Set<String> keys;
        Bucket prev, next;

        Bucket(int count) {
            this.count = count;
            this.keys = new HashSet<>();
        }
    }

    // Map: key -> count
    private Map<String, Integer> keyCount;

    // Map: count -> bucket node
    private Map<Integer, Bucket> countBucket;

    // Doubly linked list of buckets (dummy head and tail)
    private Bucket head, tail;

    // Total number of keys (for median calculation)
    private int totalKeys;

    public AllOne() {
        keyCount = new HashMap<>();
        countBucket = new HashMap<>();

        // Initialize dummy head and tail
        head = new Bucket(Integer.MIN_VALUE);
        tail = new Bucket(Integer.MAX_VALUE);
        head.next = tail;
        tail.prev = head;

        totalKeys = 0;
    }

    /**
     * Increment count of key by 1
     * Time: O(1)
     */
    public void inc(String key) {
        if (keyCount.containsKey(key)) {
            // Key exists, move from current bucket to next bucket
            changeKey(key, 1);
        } else {
            // New key, add to bucket with count 1
            keyCount.put(key, 1);

            if (!countBucket.containsKey(1)) {
                // Create new bucket for count 1
                Bucket newBucket = new Bucket(1);
                countBucket.put(1, newBucket);
                insertBucketAfter(newBucket, head);
            }

            countBucket.get(1).keys.add(key);
        }
    }

    /**
     * Decrement count of key by 1
     * Time: O(1)
     */
    public void dec(String key) {
        if (!keyCount.containsKey(key)) {
            return; // Key doesn't exist
        }

        int count = keyCount.get(key);

        if (count == 1) {
            // Remove key entirely
            keyCount.remove(key);
            removeKeyFromBucket(countBucket.get(1), key);
        } else {
            // Move from current bucket to previous bucket
            changeKey(key, -1);
        }
    }

    /**
     * Get one key with maximum count
     * Time: O(1)
     */
    public String getMaxKey() {
        if (tail.prev == head) {
            return ""; // No keys
        }

        // Return any key from the last bucket (before tail)
        return tail.prev.keys.iterator().next();
    }

    /**
     * Get one key with minimum count
     * Time: O(1)
     */
    public String getMinKey() {
        if (head.next == tail) {
            return ""; // No keys
        }

        // Return any key from the first bucket (after head)
        return head.next.keys.iterator().next();
    }

    /**
     * Helper: Move key from current count to current count + delta
     * delta = +1 for inc, -1 for dec
     */
    private void changeKey(String key, int delta) {
        int count = keyCount.get(key);
        int newCount = count + delta;

        keyCount.put(key, newCount);

        // Get current and new buckets
        Bucket currentBucket = countBucket.get(count);
        Bucket newBucket = countBucket.get(newCount);

        // Create new bucket if it doesn't exist
        if (newBucket == null) {
            newBucket = new Bucket(newCount);
            countBucket.put(newCount, newBucket);

            // Insert new bucket in correct position
            if (delta == 1) {
                // Increment: insert after current bucket
                insertBucketAfter(newBucket, currentBucket);
            } else {
                // Decrement: insert before current bucket
                insertBucketAfter(newBucket, currentBucket.prev);
            }
        }

        // Move key from current bucket to new bucket
        newBucket.keys.add(key);
        removeKeyFromBucket(currentBucket, key);
    }

    /**
     * Helper: Remove key from bucket and clean up if empty
     */
    private void removeKeyFromBucket(Bucket bucket, String key) {
        bucket.keys.remove(key);

        if (bucket.keys.isEmpty()) {
            // Remove empty bucket from list and map
            removeBucket(bucket);
            countBucket.remove(bucket.count);
        }
    }

    /**
     * Helper: Insert bucket after prev
     */
    private void insertBucketAfter(Bucket bucket, Bucket prev) {
        bucket.prev = prev;
        bucket.next = prev.next;
        prev.next.prev = bucket;
        prev.next = bucket;
    }

    /**
     * Helper: Remove bucket from linked list
     */
    private void removeBucket(Bucket bucket) {
        bucket.prev.next = bucket.next;
        bucket.next.prev = bucket.prev;
    }
}

/**
 * Alternative Implementation: Using TreeMap (NOT O(1) but simpler)
 *
 * This is NOT the correct solution for this problem since it's O(log n),
 * but included for comparison and learning purposes.
 */
class AllOneTreeMap {
    private Map<String, Integer> keyCount;
    private TreeMap<Integer, Set<String>> countKeys;

    public AllOneTreeMap() {
        keyCount = new HashMap<>();
        countKeys = new TreeMap<>();
    }

    public void inc(String key) {
        int count = keyCount.getOrDefault(key, 0);
        keyCount.put(key, count + 1);

        // Remove from old count
        if (count > 0) {
            countKeys.get(count).remove(key);
            if (countKeys.get(count).isEmpty()) {
                countKeys.remove(count);
            }
        }

        // Add to new count
        countKeys.putIfAbsent(count + 1, new HashSet<>());
        countKeys.get(count + 1).add(key);
    }

    public void dec(String key) {
        int count = keyCount.get(key);

        // Remove from old count
        countKeys.get(count).remove(key);
        if (countKeys.get(count).isEmpty()) {
            countKeys.remove(count);
        }

        if (count == 1) {
            keyCount.remove(key);
        } else {
            keyCount.put(key, count - 1);
            countKeys.putIfAbsent(count - 1, new HashSet<>());
            countKeys.get(count - 1).add(key);
        }
    }

    public String getMaxKey() {
        if (countKeys.isEmpty()) return "";
        return countKeys.lastEntry().getValue().iterator().next();
    }

    public String getMinKey() {
        if (countKeys.isEmpty()) return "";
        return countKeys.firstEntry().getValue().iterator().next();
    }
}

/**
 * Test Cases
 */
class AllOneTest {
    public static void main(String[] args) {
        System.out.println("=== Testing AllOne Data Structure ===\n");

        AllOne allOne = new AllOne();

        System.out.println("inc(\"hello\")");
        allOne.inc("hello");

        System.out.println("inc(\"hello\")");
        allOne.inc("hello");

        System.out.println("getMaxKey(): " + allOne.getMaxKey() + " (expected: hello)");
        System.out.println("getMinKey(): " + allOne.getMinKey() + " (expected: hello)");

        System.out.println("\ninc(\"leet\")");
        allOne.inc("leet");

        System.out.println("getMaxKey(): " + allOne.getMaxKey() + " (expected: hello)");
        System.out.println("getMinKey(): " + allOne.getMinKey() + " (expected: leet)");

        System.out.println("\ninc(\"leet\")");
        allOne.inc("leet");

        System.out.println("getMaxKey(): " + allOne.getMaxKey() + " (expected: hello or leet)");
        System.out.println("getMinKey(): " + allOne.getMinKey() + " (expected: hello or leet)");

        System.out.println("\ninc(\"leet\")");
        allOne.inc("leet");

        System.out.println("getMaxKey(): " + allOne.getMaxKey() + " (expected: leet)");
        System.out.println("getMinKey(): " + allOne.getMinKey() + " (expected: hello)");

        System.out.println("\ndec(\"leet\")");
        allOne.dec("leet");

        System.out.println("getMaxKey(): " + allOne.getMaxKey() + " (expected: hello or leet)");

        System.out.println("\ninc(\"code\")");
        allOne.inc("code");

        System.out.println("getMinKey(): " + allOne.getMinKey() + " (expected: code)");

        // Test edge cases
        System.out.println("\n=== Edge Cases ===\n");
        AllOne allOne2 = new AllOne();
        System.out.println("Empty: getMaxKey(): \"" + allOne2.getMaxKey() + "\" (expected: \"\")");
        System.out.println("Empty: getMinKey(): \"" + allOne2.getMinKey() + "\" (expected: \"\")");
    }
}

/**
 * Complexity Analysis:
 * ===================
 *
 * Optimal Solution (HashMap + Doubly Linked List):
 * ------------------------------------------------
 * inc():       O(1)
 * dec():       O(1)
 * getMaxKey(): O(1)
 * getMinKey(): O(1)
 * Space:       O(N) where N = number of unique keys
 *
 * TreeMap Solution (NOT correct for this problem):
 * -----------------------------------------------
 * inc():       O(log K) where K = number of distinct counts
 * dec():       O(log K)
 * getMaxKey(): O(log K)
 * getMinKey(): O(log K)
 * Space:       O(N)
 *
 *
 * Design Decisions:
 * ================
 *
 * 1. Why Doubly Linked List of Buckets?
 *    - Counts change by ±1, so adjacent buckets are always nearby
 *    - Insert/remove buckets in O(1) with prev/next pointers
 *    - Head and tail are always min and max
 *
 * 2. Why HashSet in Each Bucket?
 *    - O(1) add/remove keys
 *    - O(1) check if bucket is empty
 *    - O(1) get any key (iterator.next())
 *
 * 3. Why Two HashMaps?
 *    - keyCount: Quick lookup of current count for any key
 *    - countBucket: Quick access to bucket for any count
 *
 * 4. Dummy Head and Tail:
 *    - Simplifies edge cases (no null checks)
 *    - getMin/getMax become trivial (head.next, tail.prev)
 *
 *
 * Interview Tips:
 * ==============
 *
 * 1. Clarify Requirements:
 *    - ALL operations must be O(1)
 *    - This rules out TreeMap (O(log n))
 *
 * 2. Key Insights to Mention:
 *    - "Counts only change by 1, so we only move between adjacent buckets"
 *    - "We need to track both min and max efficiently"
 *    - "Doubly linked list gives us O(1) access to min/max"
 *
 * 3. Start with High-Level Design:
 *    - "I'll use a doubly linked list of buckets, sorted by count"
 *    - "Each bucket contains all keys with that count"
 *    - "Two hashmaps for O(1) lookups"
 *
 * 4. Walk Through an Example:
 *    inc("a") -> [1: {a}]
 *    inc("a") -> [2: {a}]
 *    inc("b") -> [1: {b}] <-> [2: {a}]
 *    getMin() -> "b" (from first bucket)
 *    getMax() -> "a" (from last bucket)
 *
 * 5. Edge Cases:
 *    - Empty data structure
 *    - Single key
 *    - All keys have same count
 *    - Decrement to 0 (remove key)
 *
 * 6. Common Mistakes:
 *    - Forgetting to remove empty buckets
 *    - Not using dummy head/tail (null pointer issues)
 *    - Not maintaining sorted order of buckets
 *
 * 7. Follow-up Questions:
 *    - Thread safety? → Add synchronization
 *    - Persistent storage? → Add database layer
 *    - Get all keys with max count? → Return bucket.keys (already O(1))
 */
