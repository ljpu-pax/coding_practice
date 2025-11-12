import java.util.*;

/**
 * LeetCode 380: Insert Delete GetRandom O(1)
 *
 * Implement the RandomizedSet class:
 * - RandomizedSet() Initializes the RandomizedSet object.
 * - bool insert(int val) Inserts an item val into the set if not present. Returns true if the item
 *   was not present, false otherwise.
 * - bool remove(int val) Removes an item val from the set if present. Returns true if the item was
 *   present, false otherwise.
 * - int getRandom() Returns a random element from the current set of elements (it's guaranteed that
 *   at least one element exists when this method is called). Each element must have the same probability
 *   of being returned.
 *
 * You must implement the functions of the class such that each function works in average O(1) time complexity.
 *
 * Example 1:
 * Input:
 * ["RandomizedSet", "insert", "remove", "insert", "getRandom", "remove", "insert", "getRandom"]
 * [[], [1], [2], [2], [], [1], [2], []]
 * Output:
 * [null, true, false, true, 2, true, false, 2]
 *
 * Explanation:
 * RandomizedSet randomizedSet = new RandomizedSet();
 * randomizedSet.insert(1); // Inserts 1 to the set. Returns true as 1 was inserted successfully.
 * randomizedSet.remove(2); // Returns false as 2 does not exist in the set.
 * randomizedSet.insert(2); // Inserts 2 to the set, returns true. Set now contains [1,2].
 * randomizedSet.getRandom(); // getRandom() should return either 1 or 2 randomly.
 * randomizedSet.remove(1); // Removes 1 from the set, returns true. Set now contains [2].
 * randomizedSet.insert(2); // 2 was already in the set, so return false.
 * randomizedSet.getRandom(); // Since 2 is the only number in the set, getRandom() will always return 2.
 *
 * Constraints:
 * - -2^31 <= val <= 2^31 - 1
 * - At most 2 * 10^5 calls will be made to insert, remove, and getRandom.
 * - There will be at least one element in the data structure when getRandom is called.
 */

/**
 * Standard RandomizedSet (LeetCode 380)
 *
 * Use HashMap + ArrayList:
 * - HashMap: stores value -> index in ArrayList
 * - ArrayList: stores actual values
 *
 * Time: O(1) average for all operations
 * Space: O(n)
 */
class RandomizedSet {
    private List<Integer> list;
    private Map<Integer, Integer> map; // value -> index in list
    private Random random;

    public RandomizedSet() {
        list = new ArrayList<>();
        map = new HashMap<>();
        random = new Random();
    }

    public boolean insert(int val) {
        if (map.containsKey(val)) {
            return false;
        }

        map.put(val, list.size());
        list.add(val);
        return true;
    }

    public boolean remove(int val) {
        if (!map.containsKey(val)) {
            return false;
        }

        // Get index of element to remove
        int index = map.get(val);
        int lastElement = list.get(list.size() - 1);

        // Swap with last element
        list.set(index, lastElement);
        map.put(lastElement, index);

        // Remove last element
        list.remove(list.size() - 1);
        map.remove(val);

        return true;
    }

    public int getRandom() {
        return list.get(random.nextInt(list.size()));
    }
}

/**
 * Extended RandomizedSet with getLast() method
 *
 * Additional method:
 * - int getLast() Returns the most recently inserted element that still exists in the set.
 *
 * To support this, we need to track insertion order.
 */
class RandomizedSetWithGetLast {
    private List<Integer> list;
    private Map<Integer, Integer> map; // value -> index in list
    private Random random;
    private Deque<Integer> insertionOrder; // Track insertion order
    private Set<Integer> currentElements; // Quick lookup for current elements

    public RandomizedSetWithGetLast() {
        list = new ArrayList<>();
        map = new HashMap<>();
        random = new Random();
        insertionOrder = new ArrayDeque<>();
        currentElements = new HashSet<>();
    }

    public boolean insert(int val) {
        if (map.containsKey(val)) {
            return false;
        }

        map.put(val, list.size());
        list.add(val);
        insertionOrder.addLast(val);
        currentElements.add(val);
        return true;
    }

    public boolean remove(int val) {
        if (!map.containsKey(val)) {
            return false;
        }

        // Get index of element to remove
        int index = map.get(val);
        int lastElement = list.get(list.size() - 1);

        // Swap with last element
        list.set(index, lastElement);
        map.put(lastElement, index);

        // Remove last element
        list.remove(list.size() - 1);
        map.remove(val);
        currentElements.remove(val);

        return true;
    }

    public int getRandom() {
        return list.get(random.nextInt(list.size()));
    }

    /**
     * Returns the most recently inserted element that still exists in the set.
     * Time: O(n) worst case if many elements were removed
     * Space: O(1)
     */
    public int getLast() {
        // Find the most recent element that still exists
        while (!insertionOrder.isEmpty()) {
            int val = insertionOrder.peekLast();
            if (currentElements.contains(val)) {
                return val;
            }
            insertionOrder.removeLast(); // Clean up removed elements
        }

        throw new NoSuchElementException("Set is empty");
    }
}

/**
 * Optimized RandomizedSet with getLast() - O(1)
 *
 * Use a doubly linked list to maintain insertion order with O(1) access to last element.
 */
class RandomizedSetWithGetLastOptimized {
    private List<Integer> list;
    private Map<Integer, Integer> listIndexMap; // value -> index in list
    private Map<Integer, Node> nodeMap; // value -> node in linked list
    private Random random;
    private DoublyLinkedList insertionOrder;

    static class Node {
        int val;
        Node prev;
        Node next;

        Node(int val) {
            this.val = val;
        }
    }

    static class DoublyLinkedList {
        Node head;
        Node tail;

        DoublyLinkedList() {
            head = new Node(-1);
            tail = new Node(-1);
            head.next = tail;
            tail.prev = head;
        }

        void addLast(Node node) {
            node.prev = tail.prev;
            node.next = tail;
            tail.prev.next = node;
            tail.prev = node;
        }

        void remove(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        int getLast() {
            if (tail.prev == head) {
                throw new NoSuchElementException("List is empty");
            }
            return tail.prev.val;
        }

        boolean isEmpty() {
            return head.next == tail;
        }
    }

    public RandomizedSetWithGetLastOptimized() {
        list = new ArrayList<>();
        listIndexMap = new HashMap<>();
        nodeMap = new HashMap<>();
        random = new Random();
        insertionOrder = new DoublyLinkedList();
    }

    public boolean insert(int val) {
        if (listIndexMap.containsKey(val)) {
            return false;
        }

        // Add to list
        listIndexMap.put(val, list.size());
        list.add(val);

        // Add to insertion order
        Node node = new Node(val);
        insertionOrder.addLast(node);
        nodeMap.put(val, node);

        return true;
    }

    public boolean remove(int val) {
        if (!listIndexMap.containsKey(val)) {
            return false;
        }

        // Remove from list
        int index = listIndexMap.get(val);
        int lastElement = list.get(list.size() - 1);

        list.set(index, lastElement);
        listIndexMap.put(lastElement, index);

        list.remove(list.size() - 1);
        listIndexMap.remove(val);

        // Remove from insertion order
        Node node = nodeMap.get(val);
        insertionOrder.remove(node);
        nodeMap.remove(val);

        return true;
    }

    public int getRandom() {
        return list.get(random.nextInt(list.size()));
    }

    /**
     * Returns the most recently inserted element that still exists in the set.
     * Time: O(1)
     * Space: O(1)
     */
    public int getLast() {
        return insertionOrder.getLast();
    }
}

/**
 * LeetCode 381: Insert Delete GetRandom O(1) - Duplicates allowed
 *
 * Similar to 380 but allows duplicates.
 */
class RandomizedCollection {
    private List<Integer> list;
    private Map<Integer, Set<Integer>> map; // value -> set of indices
    private Random random;

    public RandomizedCollection() {
        list = new ArrayList<>();
        map = new HashMap<>();
        random = new Random();
    }

    public boolean insert(int val) {
        map.putIfAbsent(val, new HashSet<>());
        map.get(val).add(list.size());
        list.add(val);
        return map.get(val).size() == 1; // Return true if first occurrence
    }

    public boolean remove(int val) {
        if (!map.containsKey(val) || map.get(val).isEmpty()) {
            return false;
        }

        // Get any index of val
        int removeIndex = map.get(val).iterator().next();
        map.get(val).remove(removeIndex);

        int lastElement = list.get(list.size() - 1);
        list.set(removeIndex, lastElement);

        // Update indices
        map.get(lastElement).add(removeIndex);
        map.get(lastElement).remove(list.size() - 1);

        list.remove(list.size() - 1);

        // Clean up if no more occurrences
        if (map.get(val).isEmpty()) {
            map.remove(val);
        }

        return true;
    }

    public int getRandom() {
        return list.get(random.nextInt(list.size()));
    }
}

/**
 * Test cases
 */
class RandomizedSetTest {
    public static void main(String[] args) {
        testBasicRandomizedSet();
        testRandomizedSetWithGetLast();
        testRandomizedSetWithGetLastOptimized();
        testRandomizedCollection();
    }

    private static void testBasicRandomizedSet() {
        System.out.println("=== Testing LeetCode 380: RandomizedSet ===\n");
        RandomizedSet set = new RandomizedSet();

        System.out.println("insert(1): " + set.insert(1) + " (Expected: true)");
        System.out.println("remove(2): " + set.remove(2) + " (Expected: false)");
        System.out.println("insert(2): " + set.insert(2) + " (Expected: true)");
        System.out.println("getRandom(): " + set.getRandom() + " (Should be 1 or 2)");
        System.out.println("remove(1): " + set.remove(1) + " (Expected: true)");
        System.out.println("insert(2): " + set.insert(2) + " (Expected: false)");
        System.out.println("getRandom(): " + set.getRandom() + " (Should be 2)");
        System.out.println();

        // Test getRandom distribution
        RandomizedSet set2 = new RandomizedSet();
        set2.insert(1);
        set2.insert(2);
        set2.insert(3);

        Map<Integer, Integer> frequency = new HashMap<>();
        for (int i = 0; i < 10000; i++) {
            int val = set2.getRandom();
            frequency.put(val, frequency.getOrDefault(val, 0) + 1);
        }

        System.out.println("getRandom() distribution over 10000 calls:");
        for (Map.Entry<Integer, Integer> entry : frequency.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue() +
                              " (~" + String.format("%.1f", entry.getValue() / 100.0) + "%)");
        }
        System.out.println();
    }

    private static void testRandomizedSetWithGetLast() {
        System.out.println("=== Testing RandomizedSet with getLast() ===\n");
        RandomizedSetWithGetLast set = new RandomizedSetWithGetLast();

        System.out.println("insert(1): " + set.insert(1));
        System.out.println("insert(2): " + set.insert(2));
        System.out.println("insert(3): " + set.insert(3));
        System.out.println("getLast(): " + set.getLast() + " (Expected: 3)");

        System.out.println("remove(3): " + set.remove(3));
        System.out.println("getLast(): " + set.getLast() + " (Expected: 2)");

        System.out.println("insert(4): " + set.insert(4));
        System.out.println("getLast(): " + set.getLast() + " (Expected: 4)");

        System.out.println("remove(2): " + set.remove(2));
        System.out.println("getLast(): " + set.getLast() + " (Expected: 4)");

        System.out.println("remove(4): " + set.remove(4));
        System.out.println("getLast(): " + set.getLast() + " (Expected: 1)");
        System.out.println();
    }

    private static void testRandomizedSetWithGetLastOptimized() {
        System.out.println("=== Testing Optimized RandomizedSet with getLast() O(1) ===\n");
        RandomizedSetWithGetLastOptimized set = new RandomizedSetWithGetLastOptimized();

        System.out.println("insert(10): " + set.insert(10));
        System.out.println("insert(20): " + set.insert(20));
        System.out.println("insert(30): " + set.insert(30));
        System.out.println("getLast(): " + set.getLast() + " (Expected: 30)");

        System.out.println("getRandom(): " + set.getRandom());

        System.out.println("remove(30): " + set.remove(30));
        System.out.println("getLast(): " + set.getLast() + " (Expected: 20)");

        System.out.println("insert(40): " + set.insert(40));
        System.out.println("getLast(): " + set.getLast() + " (Expected: 40)");

        System.out.println("remove(20): " + set.remove(20));
        System.out.println("getLast(): " + set.getLast() + " (Expected: 40)");

        System.out.println("remove(10): " + set.remove(10));
        System.out.println("getLast(): " + set.getLast() + " (Expected: 40)");

        System.out.println("remove(40): " + set.remove(40));
        System.out.println("Set is now empty");
        System.out.println();
    }

    private static void testRandomizedCollection() {
        System.out.println("=== Testing LeetCode 381: RandomizedCollection (Duplicates) ===\n");
        RandomizedCollection collection = new RandomizedCollection();

        System.out.println("insert(1): " + collection.insert(1) + " (Expected: true)");
        System.out.println("insert(1): " + collection.insert(1) + " (Expected: false - duplicate)");
        System.out.println("insert(2): " + collection.insert(2) + " (Expected: true)");
        System.out.println("getRandom(): " + collection.getRandom() + " (1 or 2)");
        System.out.println("remove(1): " + collection.remove(1) + " (Expected: true)");
        System.out.println("getRandom(): " + collection.getRandom() + " (1 or 2)");
        System.out.println("remove(1): " + collection.remove(1) + " (Expected: true)");
        System.out.println("getRandom(): " + collection.getRandom() + " (Should be 2)");

        // Test with multiple duplicates
        RandomizedCollection collection2 = new RandomizedCollection();
        collection2.insert(5);
        collection2.insert(5);
        collection2.insert(5);
        collection2.insert(10);

        Map<Integer, Integer> frequency = new HashMap<>();
        for (int i = 0; i < 10000; i++) {
            int val = collection2.getRandom();
            frequency.put(val, frequency.getOrDefault(val, 0) + 1);
        }

        System.out.println("\ngetRandom() distribution with duplicates (3x5, 1x10):");
        for (Map.Entry<Integer, Integer> entry : frequency.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue() +
                              " (~" + String.format("%.1f", entry.getValue() / 100.0) + "%)");
        }
        System.out.println();
    }
}
