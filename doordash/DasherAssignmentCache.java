package doordash;
import java.util.HashMap;
import java.util.Map;

public class DasherAssignmentCache {
    private class Node {
        int dasherId;
        int deliveryId;
        Node prev;
        Node next;

        public Node(int dasherId, int deliveryId) {
            this.dasherId = dasherId;
            this.deliveryId = deliveryId;
        }
    }

    private int capacity;
    private Map<Integer, Node> cache;
    private Node head;
    private Node tail;

    public DasherAssignmentCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>(capacity);
        this.head = new Node(-1, -1);
        this.tail = new Node(-1, -1);
        head.next = tail;
        tail.prev = head;
    }

    public int get(int dasherId) {
        Node node = cache.get(dasherId);
        if (node == null) return -1;
        moveTohead(node);
        return node.deliveryId;
    }

    public void put(int dasherId, int deliveryId) {
        Node node = cache.get(dasherId);

        if (node == null) {
            Node newNode = new Node(dasherId, deliveryId);
            cache.put(dasherId, newNode);
            addNode(newNode);

            if (cache.size() > capacity) {
                Node tail = popTail();
                cache.remove(tail.dasherId);
            }
        } else {
            node.deliveryId = deliveryId;
            moveTohead(node);
        }
    }

    public void addNode(Node node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    public void removeNode(Node node) {
        Node prev = node.prev;
        Node next = node.next;
        prev.next = next;
        next.prev = prev;
    }

    public void moveTohead(Node node) {
        removeNode(node);
        addNode(node);
    }

    public Node popTail() {
        Node res = tail.prev;
        removeNode(res);
        return res;
    }

    public static void main(String[] args) {
        DasherAssignmentCache cache = new DasherAssignmentCache(2);

        cache.put(1, 101); // Dasher 1 assigned to delivery 101
        cache.put(2, 102); // Dasher 2 assigned to delivery 102

        System.out.println(cache.get(1)); // Outputs: 101

        cache.put(3, 103); // Dasher 3 assigned to delivery 103, Dasher 2's assignment is evicted

        System.out.println(cache.get(2)); // Outputs: -1 (not found)
        System.out.println(cache.get(3)); // Outputs: 103
    }
}