import java.util.*;

/**
 * LeetCode 706: Design HashMap
 *
 * Design a HashMap without using any built-in hash table libraries.
 *
 * Implement the MyHashMap class:
 * - MyHashMap() initializes the object with an empty map.
 * - void put(int key, int value) inserts a (key, value) pair into the HashMap. If the key already exists
 *   in the map, update the corresponding value.
 * - int get(int key) returns the value to which the specified key is mapped, or -1 if this map contains
 *   no mapping for the key.
 * - void remove(int key) removes the key and its corresponding value if the map contains the mapping for the key.
 *
 * Example 1:
 * Input:
 * ["MyHashMap", "put", "put", "get", "get", "put", "get", "remove", "get"]
 * [[], [1, 1], [2, 2], [1], [3], [2, 1], [2], [2], [2]]
 * Output:
 * [null, null, null, 1, -1, null, 1, null, -1]
 *
 * Explanation:
 * MyHashMap myHashMap = new MyHashMap();
 * myHashMap.put(1, 1); // The map is now [[1,1]]
 * myHashMap.put(2, 2); // The map is now [[1,1], [2,2]]
 * myHashMap.get(1);    // return 1, The map is now [[1,1], [2,2]]
 * myHashMap.get(3);    // return -1 (i.e., not found), The map is now [[1,1], [2,2]]
 * myHashMap.put(2, 1); // The map is now [[1,1], [2,1]] (i.e., update the existing value)
 * myHashMap.get(2);    // return 1, The map is now [[1,1], [2,1]]
 * myHashMap.remove(2); // remove the mapping for 2, The map is now [[1,1]]
 * myHashMap.get(2);    // return -1 (i.e., not found), The map is now [[1,1]]
 *
 * Constraints:
 * - 0 <= key, value <= 10^6
 * - At most 10^4 calls will be made to put, get, and remove.
 */

/**
 * Approach 1: Array with Separate Chaining (LinkedList)
 *
 * Use an array of buckets where each bucket is a linked list.
 * Handle collisions using chaining.
 *
 * Time Complexity:
 * - put(): O(n/k) average, O(n) worst case where n = number of keys, k = number of buckets
 * - get(): O(n/k) average, O(n) worst case
 * - remove(): O(n/k) average, O(n) worst case
 *
 * Space: O(k + n) where k = number of buckets, n = number of keys
 */
class MyHashMap {
    private static final int SIZE = 1000;
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

        // Check if key already exists
        for (Entry entry : bucket) {
            if (entry.key == key) {
                entry.value = value;
                return;
            }
        }

        // Key doesn't exist, add new entry
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
 * Approach 2: Array with Separate Chaining (Custom LinkedList Node)
 *
 * Similar to approach 1 but using custom linked list nodes for better control.
 *
 * Time: O(n/k) average for all operations
 * Space: O(k + n)
 */
class MyHashMapLinkedList {
    private static final int SIZE = 1000;
    private Node[] buckets;

    static class Node {
        int key;
        int value;
        Node next;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    public MyHashMapLinkedList() {
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
        while (true) {
            if (curr.key == key) {
                curr.value = value;
                return;
            }
            if (curr.next == null) break;
            curr = curr.next;
        }

        curr.next = new Node(key, value);
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

        if (curr == null) return;

        // Check if head needs to be removed
        if (curr.key == key) {
            buckets[index] = curr.next;
            return;
        }

        // Check remaining nodes
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
 * Approach 3: Direct Array (Works only if key range is small)
 *
 * If we know the key range is limited (e.g., 0 to 10^6), we can use direct indexing.
 *
 * Time: O(1) for all operations
 * Space: O(max_key) - can be very large!
 */
class MyHashMapArray {
    private static final int SIZE = 1000001;
    private int[] data;

    public MyHashMapArray() {
        data = new int[SIZE];
        Arrays.fill(data, -1);
    }

    public void put(int key, int value) {
        data[key] = value;
    }

    public int get(int key) {
        return data[key];
    }

    public void remove(int key) {
        data[key] = -1;
    }
}

/**
 * Approach 4: Binary Search Tree in Each Bucket
 *
 * Use BST instead of linked list for better worst-case performance.
 *
 * Time: O(log(n/k)) average for balanced BST
 * Space: O(k + n)
 */
class MyHashMapBST {
    private static final int SIZE = 1000;
    private TreeNode[] buckets;

    static class TreeNode {
        int key;
        int value;
        TreeNode left;
        TreeNode right;

        TreeNode(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    public MyHashMapBST() {
        buckets = new TreeNode[SIZE];
    }

    private int hash(int key) {
        return key % SIZE;
    }

    public void put(int key, int value) {
        int index = hash(key);
        buckets[index] = insertOrUpdate(buckets[index], key, value);
    }

    private TreeNode insertOrUpdate(TreeNode root, int key, int value) {
        if (root == null) {
            return new TreeNode(key, value);
        }

        if (key == root.key) {
            root.value = value;
        } else if (key < root.key) {
            root.left = insertOrUpdate(root.left, key, value);
        } else {
            root.right = insertOrUpdate(root.right, key, value);
        }

        return root;
    }

    public int get(int key) {
        int index = hash(key);
        TreeNode node = search(buckets[index], key);
        return node == null ? -1 : node.value;
    }

    private TreeNode search(TreeNode root, int key) {
        if (root == null || root.key == key) {
            return root;
        }

        if (key < root.key) {
            return search(root.left, key);
        } else {
            return search(root.right, key);
        }
    }

    public void remove(int key) {
        int index = hash(key);
        buckets[index] = delete(buckets[index], key);
    }

    private TreeNode delete(TreeNode root, int key) {
        if (root == null) return null;

        if (key < root.key) {
            root.left = delete(root.left, key);
        } else if (key > root.key) {
            root.right = delete(root.right, key);
        } else {
            // Node to be deleted found
            if (root.left == null) return root.right;
            if (root.right == null) return root.left;

            // Node has two children
            TreeNode minNode = findMin(root.right);
            root.key = minNode.key;
            root.value = minNode.value;
            root.right = delete(root.right, minNode.key);
        }

        return root;
    }

    private TreeNode findMin(TreeNode root) {
        while (root.left != null) {
            root = root.left;
        }
        return root;
    }
}

/**
 * Test cases
 */
class MyHashMapTest {
    public static void main(String[] args) {
        testMyHashMap();
        testMyHashMapLinkedList();
        testMyHashMapArray();
        testMyHashMapBST();
        testEdgeCases();
    }

    private static void testMyHashMap() {
        System.out.println("=== Testing Approach 1: ArrayList Chaining ===");
        MyHashMap map = new MyHashMap();

        map.put(1, 1);
        map.put(2, 2);
        System.out.println("get(1): " + map.get(1) + " (Expected: 1) - " + (map.get(1) == 1 ? "PASS" : "FAIL"));
        System.out.println("get(3): " + map.get(3) + " (Expected: -1) - " + (map.get(3) == -1 ? "PASS" : "FAIL"));

        map.put(2, 1);
        System.out.println("get(2): " + map.get(2) + " (Expected: 1) - " + (map.get(2) == 1 ? "PASS" : "FAIL"));

        map.remove(2);
        System.out.println("get(2): " + map.get(2) + " (Expected: -1) - " + (map.get(2) == -1 ? "PASS" : "FAIL"));

        System.out.println();
    }

    private static void testMyHashMapLinkedList() {
        System.out.println("=== Testing Approach 2: Custom LinkedList ===");
        MyHashMapLinkedList map = new MyHashMapLinkedList();

        map.put(1, 1);
        map.put(2, 2);
        System.out.println("get(1): " + map.get(1) + " (Expected: 1) - " + (map.get(1) == 1 ? "PASS" : "FAIL"));
        System.out.println("get(3): " + map.get(3) + " (Expected: -1) - " + (map.get(3) == -1 ? "PASS" : "FAIL"));

        map.put(2, 1);
        System.out.println("get(2): " + map.get(2) + " (Expected: 1) - " + (map.get(2) == 1 ? "PASS" : "FAIL"));

        map.remove(2);
        System.out.println("get(2): " + map.get(2) + " (Expected: -1) - " + (map.get(2) == -1 ? "PASS" : "FAIL"));

        System.out.println();
    }

    private static void testMyHashMapArray() {
        System.out.println("=== Testing Approach 3: Direct Array ===");
        MyHashMapArray map = new MyHashMapArray();

        map.put(1, 1);
        map.put(2, 2);
        System.out.println("get(1): " + map.get(1) + " (Expected: 1) - " + (map.get(1) == 1 ? "PASS" : "FAIL"));
        System.out.println("get(3): " + map.get(3) + " (Expected: -1) - " + (map.get(3) == -1 ? "PASS" : "FAIL"));

        map.put(2, 1);
        System.out.println("get(2): " + map.get(2) + " (Expected: 1) - " + (map.get(2) == 1 ? "PASS" : "FAIL"));

        map.remove(2);
        System.out.println("get(2): " + map.get(2) + " (Expected: -1) - " + (map.get(2) == -1 ? "PASS" : "FAIL"));

        System.out.println();
    }

    private static void testMyHashMapBST() {
        System.out.println("=== Testing Approach 4: BST in Buckets ===");
        MyHashMapBST map = new MyHashMapBST();

        map.put(1, 1);
        map.put(2, 2);
        System.out.println("get(1): " + map.get(1) + " (Expected: 1) - " + (map.get(1) == 1 ? "PASS" : "FAIL"));
        System.out.println("get(3): " + map.get(3) + " (Expected: -1) - " + (map.get(3) == -1 ? "PASS" : "FAIL"));

        map.put(2, 1);
        System.out.println("get(2): " + map.get(2) + " (Expected: 1) - " + (map.get(2) == 1 ? "PASS" : "FAIL"));

        map.remove(2);
        System.out.println("get(2): " + map.get(2) + " (Expected: -1) - " + (map.get(2) == -1 ? "PASS" : "FAIL"));

        System.out.println();
    }

    private static void testEdgeCases() {
        System.out.println("=== Testing Edge Cases ===");
        MyHashMap map = new MyHashMap();

        // Test 1: Update existing key
        map.put(1, 100);
        map.put(1, 200);
        System.out.println("Test 1 - Update: " + map.get(1) + " (Expected: 200) - " + (map.get(1) == 200 ? "PASS" : "FAIL"));

        // Test 2: Remove non-existent key
        map.remove(999);
        System.out.println("Test 2 - Remove non-existent: PASS (no crash)");

        // Test 3: Hash collision (keys with same hash)
        map.put(1, 10);
        map.put(1001, 20); // 1 % 1000 = 1, 1001 % 1000 = 1
        System.out.println("Test 3 - Collision get(1): " + map.get(1) + " (Expected: 10) - " + (map.get(1) == 10 ? "PASS" : "FAIL"));
        System.out.println("Test 3 - Collision get(1001): " + map.get(1001) + " (Expected: 20) - " + (map.get(1001) == 20 ? "PASS" : "FAIL"));

        // Test 4: Remove with collision
        map.remove(1);
        System.out.println("Test 4 - Remove with collision get(1): " + map.get(1) + " (Expected: -1) - " + (map.get(1) == -1 ? "PASS" : "FAIL"));
        System.out.println("Test 4 - Remove with collision get(1001): " + map.get(1001) + " (Expected: 20) - " + (map.get(1001) == 20 ? "PASS" : "FAIL"));

        // Test 5: Large keys
        map.put(1000000, 999);
        System.out.println("Test 5 - Large key: " + map.get(1000000) + " (Expected: 999) - " + (map.get(1000000) == 999 ? "PASS" : "FAIL"));

        // Test 6: Zero key and value
        map.put(0, 0);
        System.out.println("Test 6 - Zero key/value: " + map.get(0) + " (Expected: 0) - " + (map.get(0) == 0 ? "PASS" : "FAIL"));

        System.out.println();
    }
}
