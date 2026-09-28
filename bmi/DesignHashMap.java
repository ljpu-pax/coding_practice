import java.util.*;

/**
 * LeetCode 706: Design HashMap (Easy)
 *
 * Design a HashMap without using any built-in hash table libraries.
 *
 * Implement the MyHashMap class:
 * - MyHashMap() initializes the object with an empty map
 * - void put(int key, int value) inserts a (key, value) pair into the HashMap.
 *   If the key already exists, update the corresponding value
 * - int get(int key) returns the value to which the specified key is mapped,
 *   or -1 if this map contains no mapping for the key
 * - void remove(int key) removes the key and its corresponding value if the map
 *   contains the mapping for the key
 *
 * Example:
 * MyHashMap myHashMap = new MyHashMap();
 * myHashMap.put(1, 1);
 * myHashMap.put(2, 2);
 * myHashMap.get(1);    // returns 1
 * myHashMap.get(3);    // returns -1 (not found)
 * myHashMap.put(2, 1); // update existing value
 * myHashMap.get(2);    // returns 1
 * myHashMap.remove(2);
 * myHashMap.get(2);    // returns -1 (removed)
 *
 * Constraints:
 * - 0 <= key, value <= 10^6
 * - At most 10^4 calls will be made to put, get, and remove
 */

/**
 * Approach 1: Array with Separate Chaining (Linked List)
 *
 * Time: O(N/K) average, O(N) worst case where N = number of keys, K = buckets
 * Space: O(K + M) where M = number of unique keys
 */
class MyHashMap {
    private static final int SIZE = 10000;
    private List<Entry>[] buckets;

    static class Entry {
        int key;
        int value;

        Entry(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    public MyHashMap() {
        buckets = new ArrayList[SIZE];
        for (int i = 0; i < SIZE; i++) {
            buckets[i] = new ArrayList<>();
        }
    }

    private int hash(int key) {
        return key % SIZE;
    }

    public void put(int key, int value) {
        int index = hash(key);
        List<Entry> bucket = buckets[index];

        // Update if key exists
        for (Entry entry : bucket) {
            if (entry.key == key) {
                entry.value = value;
                return;
            }
        }

        // Add new entry
        bucket.add(new Entry(key, value));
    }

    public int get(int key) {
        int index = hash(key);
        List<Entry> bucket = buckets[index];

        for (Entry entry : bucket) {
            if (entry.key == key) {
                return entry.value;
            }
        }

        return -1;
    }

    public void remove(int key) {
        int index = hash(key);
        List<Entry> bucket = buckets[index];

        for (int i = 0; i < bucket.size(); i++) {
            if (bucket.get(i).key == key) {
                bucket.remove(i);
                return;
            }
        }
    }
}

/**
 * Approach 2: Using custom linked list nodes
 */
class MyHashMap2 {
    private static final int SIZE = 10000;
    private Node[] buckets;

    static class Node {
        int key, value;
        Node next;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    public MyHashMap2() {
        buckets = new Node[SIZE];
    }

    private int hash(int key) {
        return key % SIZE;
    }

    public void put(int key, int value) {
        int index = hash(key);

        if (buckets[index] == null) {
            buckets[index] = new Node(key, value);
            return;
        }

        Node curr = buckets[index];
        Node prev = null;

        while (curr != null) {
            if (curr.key == key) {
                curr.value = value;
                return;
            }
            prev = curr;
            curr = curr.next;
        }

        prev.next = new Node(key, value);
    }

    public int get(int key) {
        int index = hash(key);
        Node curr = buckets[index];

        while (curr != null) {
            if (curr.key == key) {
                return curr.value;
            }
            curr = curr.next;
        }

        return -1;
    }

    public void remove(int key) {
        int index = hash(key);
        Node curr = buckets[index];

        if (curr == null) {
            return;
        }

        // Remove head
        if (curr.key == key) {
            buckets[index] = curr.next;
            return;
        }

        // Remove from middle/end
        while (curr.next != null) {
            if (curr.next.key == key) {
                curr.next = curr.next.next;
                return;
            }
            curr = curr.next;
        }
    }
}

/**
 * Approach 3: Open Addressing with Linear Probing
 */
class MyHashMap3 {
    private static final int SIZE = 20000;
    private static final int EMPTY = -1;
    private int[] keys;
    private int[] values;

    public MyHashMap3() {
        keys = new int[SIZE];
        values = new int[SIZE];
        Arrays.fill(keys, EMPTY);
    }

    private int hash(int key) {
        return key % SIZE;
    }

    public void put(int key, int value) {
        int index = hash(key);

        while (keys[index] != EMPTY && keys[index] != key) {
            index = (index + 1) % SIZE; // Linear probing
        }

        keys[index] = key;
        values[index] = value;
    }

    public int get(int key) {
        int index = hash(key);
        int steps = 0;

        while (keys[index] != EMPTY && steps < SIZE) {
            if (keys[index] == key) {
                return values[index];
            }
            index = (index + 1) % SIZE;
            steps++;
        }

        return -1;
    }

    public void remove(int key) {
        int index = hash(key);
        int steps = 0;

        while (keys[index] != EMPTY && steps < SIZE) {
            if (keys[index] == key) {
                keys[index] = EMPTY;
                return;
            }
            index = (index + 1) % SIZE;
            steps++;
        }
    }
}

/**
 * Approach 4: With dynamic resizing (advanced)
 */
class MyHashMapDynamic {
    private static final int INITIAL_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    private List<Entry>[] buckets;
    private int size;
    private int capacity;

    static class Entry {
        int key, value;

        Entry(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    public MyHashMapDynamic() {
        this.capacity = INITIAL_CAPACITY;
        this.size = 0;
        this.buckets = new ArrayList[capacity];
        for (int i = 0; i < capacity; i++) {
            buckets[i] = new ArrayList<>();
        }
    }

    private int hash(int key) {
        return key % capacity;
    }

    public void put(int key, int value) {
        int index = hash(key);

        // Update existing key
        for (Entry entry : buckets[index]) {
            if (entry.key == key) {
                entry.value = value;
                return;
            }
        }

        // Add new entry
        buckets[index].add(new Entry(key, value));
        size++;

        // Resize if needed
        if ((double) size / capacity > LOAD_FACTOR) {
            resize();
        }
    }

    public int get(int key) {
        int index = hash(key);

        for (Entry entry : buckets[index]) {
            if (entry.key == key) {
                return entry.value;
            }
        }

        return -1;
    }

    public void remove(int key) {
        int index = hash(key);

        for (int i = 0; i < buckets[index].size(); i++) {
            if (buckets[index].get(i).key == key) {
                buckets[index].remove(i);
                size--;
                return;
            }
        }
    }

    private void resize() {
        int newCapacity = capacity * 2;
        List<Entry>[] newBuckets = new ArrayList[newCapacity];

        for (int i = 0; i < newCapacity; i++) {
            newBuckets[i] = new ArrayList<>();
        }

        // Rehash all entries
        for (List<Entry> bucket : buckets) {
            for (Entry entry : bucket) {
                int newIndex = entry.key % newCapacity;
                newBuckets[newIndex].add(entry);
            }
        }

        buckets = newBuckets;
        capacity = newCapacity;
    }
}

/**
 * Test cases
 */
class DesignHashMapTest {
    public static void main(String[] args) {
        testBasicOperations();
        testAllImplementations();
        testEdgeCases();
        testPerformance();
    }

    private static void testBasicOperations() {
        System.out.println("=== LeetCode 706: Design HashMap ===\n");

        MyHashMap map = new MyHashMap();

        map.put(1, 1);
        map.put(2, 2);
        System.out.println("put(1,1), put(2,2)");
        System.out.println("get(1): " + map.get(1));  // 1
        System.out.println("get(3): " + map.get(3));  // -1

        map.put(2, 1);
        System.out.println("put(2,1) - update");
        System.out.println("get(2): " + map.get(2));  // 1

        map.remove(2);
        System.out.println("remove(2)");
        System.out.println("get(2): " + map.get(2));  // -1
        System.out.println();
    }

    private static void testAllImplementations() {
        System.out.println("=== Testing All Implementations ===\n");

        MyHashMap map1 = new MyHashMap();
        MyHashMap2 map2 = new MyHashMap2();
        MyHashMap3 map3 = new MyHashMap3();
        MyHashMapDynamic map4 = new MyHashMapDynamic();

        // Same operations on all
        for (int i = 1; i <= 5; i++) {
            map1.put(i, i * 10);
            map2.put(i, i * 10);
            map3.put(i, i * 10);
            map4.put(i, i * 10);
        }

        System.out.println("After inserting 1->10, 2->20, ..., 5->50:");
        System.out.println("Array + List:       get(3) = " + map1.get(3));
        System.out.println("Custom LinkedList:  get(3) = " + map2.get(3));
        System.out.println("Linear Probing:     get(3) = " + map3.get(3));
        System.out.println("Dynamic Resize:     get(3) = " + map4.get(3));
        System.out.println();
    }

    private static void testEdgeCases() {
        System.out.println("=== Edge Cases ===\n");

        MyHashMap map = new MyHashMap();

        // Get non-existent key
        System.out.println("Get non-existent: " + map.get(999)); // -1

        // Remove non-existent key
        map.remove(999); // Should not crash

        // Update same key multiple times
        map.put(1, 1);
        map.put(1, 2);
        map.put(1, 3);
        System.out.println("Multiple updates: " + map.get(1)); // 3

        // Collision handling
        map.put(1, 100);
        map.put(10001, 200); // Same bucket as 1 if SIZE=10000
        System.out.println("After collision: get(1) = " + map.get(1));
        System.out.println("After collision: get(10001) = " + map.get(10001));
        System.out.println();
    }

    private static void testPerformance() {
        System.out.println("=== Performance Test ===\n");

        MyHashMap map = new MyHashMap();

        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            map.put(i, i);
        }
        long putTime = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            map.get(i);
        }
        long getTime = System.nanoTime() - start;

        System.out.println("1000 puts: " + putTime / 1000 + " μs");
        System.out.println("1000 gets: " + getTime / 1000 + " μs");
        System.out.println();
    }
}

/**
 * Key Insights
 */
class HashMapInsights {
    /*
     * Hash Function Requirements:
     * ============================
     * 1. Deterministic: same input -> same output
     * 2. Uniform distribution: spread keys evenly
     * 3. Fast to compute
     *
     * Collision Resolution:
     * =====================
     * 1. Separate Chaining:
     *    - Each bucket is a linked list/array
     *    - Simple to implement
     *    - No clustering
     *    - Extra memory for pointers
     *
     * 2. Open Addressing:
     *    - All entries stored in array
     *    - Linear probing: check next slot
     *    - Quadratic probing: check i² slots away
     *    - Double hashing: use second hash function
     *    - Better cache locality
     *    - Clustering issues
     *
     * Load Factor:
     * ============
     * - Load factor = size / capacity
     * - Higher load factor: more collisions, less space
     * - Lower load factor: fewer collisions, more space
     * - Typical: resize at 0.75 load factor
     *
     * Time Complexity:
     * ================
     * Average case:
     * - put: O(1)
     * - get: O(1)
     * - remove: O(1)
     *
     * Worst case (all keys hash to same bucket):
     * - put: O(N)
     * - get: O(N)
     * - remove: O(N)
     *
     * Space Complexity: O(K + M)
     * - K = number of buckets
     * - M = number of unique keys
     *
     * Related Problems:
     * =================
     * - 705: Design HashSet
     * - 146: LRU Cache
     * - 460: LFU Cache
     * - 355: Design Twitter
     */
}
