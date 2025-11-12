package nvda;

public class MyHashMap {
    private static class Node {
        int key, value;
        Node next;
        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int SIZE = 10000;
    private Node[] buckets;

    public MyHashMap() {
        buckets = new Node[SIZE];
    }

    private int getIndex(int key) {
        return Integer.hashCode(key) % SIZE;
    }

    private Node find(Node head, int key) {
        Node prev = head;
        Node curr = head.next;
        while (curr != null && curr.key != key) {
            prev = curr;
            curr = curr.next;
        }
        return prev;
    }

    public void put(int key, int value) {
        int idx = getIndex(key);
        if (buckets[idx] == null) {
            buckets[idx] = new Node(-1, -1);  // dummy head
        }
        Node prev = find(buckets[idx], key);
        if (prev.next == null) {
            prev.next = new Node(key, value);
        } else {
            prev.next.value = value;  // update
        }
    }

    public int get(int key) {
        int idx = getIndex(key);
        if (buckets[idx] == null) return -1;
        Node prev = find(buckets[idx], key);
        if (prev.next == null) return -1;
        return prev.next.value;
    }

    public void remove(int key) {
        int idx = getIndex(key);
        if (buckets[idx] == null) return;
        Node prev = find(buckets[idx], key);
        if (prev.next == null) return;
        prev.next = prev.next.next;
    }

    // Main method for testing
    public static void main(String[] args) {
        MyHashMap map = new MyHashMap();

        map.put(10, 100);
        map.put(20, 200);
        map.put(10010, 300);  // likely to hash to same index as 10

        System.out.println("Get 10: " + map.get(10));       // should print 100
        System.out.println("Get 20: " + map.get(20));       // should print 200
        System.out.println("Get 10010: " + map.get(10010)); // should print 300

        map.remove(10);
        System.out.println("After removing 10, get 10: " + map.get(10)); // should print -1
        System.out.println("Get 10010 again: " + map.get(10010));        // should still print 300
    }
}

