package ambience;

import java.util.HashMap;

public class LRUCache {
    private class Node {
        int key, value;
        Node prev, next;
        Node(int k, int v) {
            key = k;
            value = v;
        }
    }

    private final int capacity;
    private final HashMap<Integer, Node> map;
    private final Node head, tail;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        map = new HashMap<>();
        head = new Node(0, 0); // dummy head
        tail = new Node(0, 0); // dummy tail
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        if (!map.containsKey(key)) return -1;
        Node node = map.get(key);
        moveToFront(node);
        return node.value;
    }

    public void put(int key, int value) {
        if (map.containsKey(key)) {
            Node node = map.get(key);
            node.value = value;
            moveToFront(node);
        } else {
            if (map.size() == capacity) {
                Node lru = tail.prev;
                remove(lru);
                map.remove(lru.key);
            }
            Node node = new Node(key, value);
            map.put(key, node);
            insertToFront(node);
        }
    }

    private void moveToFront(Node node) {
        remove(node);
        insertToFront(node);
    }

    private void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void insertToFront(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    // Test the implementation
    public static void main(String[] args) {
        System.out.println("=== Basic LRU Cache Tests ===");
        LRUCache cache = new LRUCache(2);

        cache.put(1, 1);  // cache = {1=1}
        cache.put(2, 2);  // cache = {1=1, 2=2}
        System.out.println(cache.get(1)); // return 1, cache = {2=2, 1=1}

        cache.put(3, 3);  // evicts key 2, cache = {1=1, 3=3}
        System.out.println(cache.get(2)); // return -1

        cache.put(4, 4);  // evicts key 1, cache = {3=3, 4=4}
        System.out.println(cache.get(1)); // return -1
        System.out.println(cache.get(3)); // return 3
        System.out.println(cache.get(4)); // return 4

        System.out.println("\n=== Edge Case Tests ===");
        // Test with capacity 1
        LRUCache singleCache = new LRUCache(1);
        singleCache.put(1, 10);
        System.out.println("Single capacity - get(1): " + singleCache.get(1)); // should return 10
        
        singleCache.put(2, 20); // should evict key 1
        System.out.println("Single capacity - get(1): " + singleCache.get(1)); // should return -1
        System.out.println("Single capacity - get(2): " + singleCache.get(2)); // should return 20

        System.out.println("\n=== Update Existing Key Tests ===");
        LRUCache updateCache = new LRUCache(3);
        updateCache.put(1, 100);
        updateCache.put(2, 200);
        updateCache.put(3, 300);
        
        // Update existing key - should move to front
        updateCache.put(1, 150);
        System.out.println("After update - get(1): " + updateCache.get(1)); // should return 150
        
        // Add new key - should evict least recently used (key 2)
        updateCache.put(4, 400);
        System.out.println("After adding key 4 - get(2): " + updateCache.get(2)); // should return -1
        System.out.println("After adding key 4 - get(1): " + updateCache.get(1)); // should return 150
        System.out.println("After adding key 4 - get(3): " + updateCache.get(3)); // should return 300
        System.out.println("After adding key 4 - get(4): " + updateCache.get(4)); // should return 400

        System.out.println("\n=== Access Pattern Tests ===");
        LRUCache patternCache = new LRUCache(4);
        patternCache.put(1, 1000);
        patternCache.put(2, 2000);
        patternCache.put(3, 3000);
        patternCache.put(4, 4000);
        
        // Access key 2 to make it most recently used
        System.out.println("Accessing key 2: " + patternCache.get(2)); // should return 2000
        
        // Add new key - should evict key 1 (least recently used)
        patternCache.put(5, 5000);
        System.out.println("After adding key 5 - get(1): " + patternCache.get(1)); // should return -1
        System.out.println("After adding key 5 - get(2): " + patternCache.get(2)); // should return 2000
        System.out.println("After adding key 5 - get(3): " + patternCache.get(3)); // should return 3000
        System.out.println("After adding key 5 - get(4): " + patternCache.get(4)); // should return 4000
        System.out.println("After adding key 5 - get(5): " + patternCache.get(5)); // should return 5000

        System.out.println("\n=== Zero Capacity Test ===");
        LRUCache zeroCache = new LRUCache(0);
        zeroCache.put(1, 100);
        zeroCache.put(2, 200);
        System.out.println("Zero capacity - get(1): " + zeroCache.get(1)); // should return -1
        System.out.println("Zero capacity - get(2): " + zeroCache.get(2)); // should return -1

        System.out.println("\n=== Large Capacity Test ===");
        LRUCache largeCache = new LRUCache(1000);
        for (int i = 1; i <= 1000; i++) {
            largeCache.put(i, i * 10);
        }
        System.out.println("Large capacity - get(1): " + largeCache.get(1)); // should return 10
        System.out.println("Large capacity - get(500): " + largeCache.get(500)); // should return 5000
        System.out.println("Large capacity - get(1000): " + largeCache.get(1000)); // should return 10000
        
        // Add one more - should evict key 1
        largeCache.put(1001, 10010);
        System.out.println("After adding key 1001 - get(1): " + largeCache.get(1)); // should return -1
        System.out.println("After adding key 1001 - get(1001): " + largeCache.get(1001)); // should return 10010

        System.out.println("\n=== All tests completed! ===");
    }
}

