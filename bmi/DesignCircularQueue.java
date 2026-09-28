import java.util.*;

/**
 * LeetCode 622: Design Circular Queue (Medium)
 *
 * Design your implementation of the circular queue. The circular queue is a linear data structure
 * in which the operations are performed based on FIFO (First In First Out) principle and the last
 * position is connected back to the first position to make a circle.
 *
 * It is also called "Ring Buffer".
 *
 * Implement the MyCircularQueue class:
 * - MyCircularQueue(k) Initializes the object with the size of the queue to be k
 * - int Front() Gets the front item from the queue. If the queue is empty, return -1
 * - int Rear() Gets the last item from the queue. If the queue is empty, return -1
 * - boolean enQueue(int value) Inserts an element into the circular queue. Return true if successful
 * - boolean deQueue() Deletes an element from the circular queue. Return true if successful
 * - boolean isEmpty() Checks whether the circular queue is empty or not
 * - boolean isFull() Checks whether the circular queue is full or not
 *
 * Example:
 * MyCircularQueue circularQueue = new MyCircularQueue(3);
 * circularQueue.enQueue(1); // return True
 * circularQueue.enQueue(2); // return True
 * circularQueue.enQueue(3); // return True
 * circularQueue.enQueue(4); // return False (queue is full)
 * circularQueue.Rear();     // return 3
 * circularQueue.isFull();   // return True
 * circularQueue.deQueue();  // return True
 * circularQueue.enQueue(4); // return True
 * circularQueue.Rear();     // return 4
 *
 * Constraints:
 * - 1 <= k <= 1000
 * - 0 <= value <= 1000
 * - At most 3000 calls will be made to enQueue, deQueue, Front, Rear, isEmpty, and isFull
 */

/**
 * Approach 1: Array-based implementation
 *
 * Use array with two pointers (front and rear)
 * Time: O(1) for all operations
 * Space: O(k)
 */
class MyCircularQueue {
    private int[] data;
    private int front;
    private int rear;
    private int size;
    private int capacity;

    public MyCircularQueue(int k) {
        this.capacity = k;
        this.data = new int[k];
        this.front = 0;
        this.rear = -1;
        this.size = 0;
    }

    public boolean enQueue(int value) {
        if (isFull()) {
            return false;
        }

        rear = (rear + 1) % capacity;
        data[rear] = value;
        size++;
        return true;
    }

    public boolean deQueue() {
        if (isEmpty()) {
            return false;
        }

        front = (front + 1) % capacity;
        size--;
        return true;
    }

    public int Front() {
        if (isEmpty()) {
            return -1;
        }
        return data[front];
    }

    public int Rear() {
        if (isEmpty()) {
            return -1;
        }
        return data[rear];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }
}

/**
 * Approach 2: Array with count tracking (no size variable)
 */
class MyCircularQueue2 {
    private int[] data;
    private int front;
    private int rear;
    private int capacity;

    public MyCircularQueue2(int k) {
        this.capacity = k + 1; // One extra space to distinguish full vs empty
        this.data = new int[capacity];
        this.front = 0;
        this.rear = 0;
    }

    public boolean enQueue(int value) {
        if (isFull()) {
            return false;
        }

        data[rear] = value;
        rear = (rear + 1) % capacity;
        return true;
    }

    public boolean deQueue() {
        if (isEmpty()) {
            return false;
        }

        front = (front + 1) % capacity;
        return true;
    }

    public int Front() {
        if (isEmpty()) {
            return -1;
        }
        return data[front];
    }

    public int Rear() {
        if (isEmpty()) {
            return -1;
        }
        return data[(rear - 1 + capacity) % capacity];
    }

    public boolean isEmpty() {
        return front == rear;
    }

    public boolean isFull() {
        return (rear + 1) % capacity == front;
    }
}

/**
 * Approach 3: Linked List implementation
 */
class MyCircularQueue3 {
    static class Node {
        int value;
        Node next;

        Node(int value) {
            this.value = value;
        }
    }

    private Node head;
    private Node tail;
    private int size;
    private int capacity;

    public MyCircularQueue3(int k) {
        this.capacity = k;
        this.size = 0;
        this.head = null;
        this.tail = null;
    }

    public boolean enQueue(int value) {
        if (isFull()) {
            return false;
        }

        Node newNode = new Node(value);

        if (isEmpty()) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }

        size++;
        return true;
    }

    public boolean deQueue() {
        if (isEmpty()) {
            return false;
        }

        head = head.next;
        size--;

        if (isEmpty()) {
            tail = null;
        }

        return true;
    }

    public int Front() {
        if (isEmpty()) {
            return -1;
        }
        return head.value;
    }

    public int Rear() {
        if (isEmpty()) {
            return -1;
        }
        return tail.value;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }
}

/**
 * Related: Design Circular Deque (LeetCode 641)
 */
class MyCircularDeque {
    private int[] data;
    private int front;
    private int rear;
    private int size;
    private int capacity;

    public MyCircularDeque(int k) {
        this.capacity = k;
        this.data = new int[k];
        this.front = 0;
        this.rear = 0;
        this.size = 0;
    }

    public boolean insertFront(int value) {
        if (isFull()) {
            return false;
        }

        if (isEmpty()) {
            data[front] = value;
        } else {
            front = (front - 1 + capacity) % capacity;
            data[front] = value;
        }

        size++;
        return true;
    }

    public boolean insertLast(int value) {
        if (isFull()) {
            return false;
        }

        if (isEmpty()) {
            data[rear] = value;
        } else {
            rear = (rear + 1) % capacity;
            data[rear] = value;
        }

        size++;
        return true;
    }

    public boolean deleteFront() {
        if (isEmpty()) {
            return false;
        }

        if (size == 1) {
            size = 0;
        } else {
            front = (front + 1) % capacity;
            size--;
        }

        return true;
    }

    public boolean deleteLast() {
        if (isEmpty()) {
            return false;
        }

        if (size == 1) {
            size = 0;
        } else {
            rear = (rear - 1 + capacity) % capacity;
            size--;
        }

        return true;
    }

    public int getFront() {
        if (isEmpty()) {
            return -1;
        }
        return data[front];
    }

    public int getRear() {
        if (isEmpty()) {
            return -1;
        }
        return data[rear];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }
}

/**
 * Test cases
 */
class DesignCircularQueueTest {
    public static void main(String[] args) {
        testBasicOperations();
        testEdgeCases();
        testAllImplementations();
        testCircularDeque();
    }

    private static void testBasicOperations() {
        System.out.println("=== LeetCode 622: Design Circular Queue ===\n");

        MyCircularQueue queue = new MyCircularQueue(3);

        System.out.println("enQueue(1): " + queue.enQueue(1)); // true
        System.out.println("enQueue(2): " + queue.enQueue(2)); // true
        System.out.println("enQueue(3): " + queue.enQueue(3)); // true
        System.out.println("enQueue(4): " + queue.enQueue(4)); // false (full)
        System.out.println("Rear(): " + queue.Rear());         // 3
        System.out.println("isFull(): " + queue.isFull());     // true
        System.out.println("deQueue(): " + queue.deQueue());   // true
        System.out.println("enQueue(4): " + queue.enQueue(4)); // true
        System.out.println("Rear(): " + queue.Rear());         // 4
        System.out.println("Front(): " + queue.Front());       // 2
        System.out.println();
    }

    private static void testEdgeCases() {
        System.out.println("=== Edge Cases ===\n");

        // Test 1: Single element
        MyCircularQueue queue1 = new MyCircularQueue(1);
        System.out.println("Test 1: Single element queue");
        System.out.println("enQueue(1): " + queue1.enQueue(1));
        System.out.println("enQueue(2): " + queue1.enQueue(2)); // false
        System.out.println("Front(): " + queue1.Front());
        System.out.println("Rear(): " + queue1.Rear());
        System.out.println();

        // Test 2: Empty operations
        MyCircularQueue queue2 = new MyCircularQueue(3);
        System.out.println("Test 2: Empty queue operations");
        System.out.println("isEmpty(): " + queue2.isEmpty());  // true
        System.out.println("Front(): " + queue2.Front());      // -1
        System.out.println("Rear(): " + queue2.Rear());        // -1
        System.out.println("deQueue(): " + queue2.deQueue());  // false
        System.out.println();
    }

    private static void testAllImplementations() {
        System.out.println("=== Testing All Implementations ===\n");

        MyCircularQueue q1 = new MyCircularQueue(3);
        MyCircularQueue2 q2 = new MyCircularQueue2(3);
        MyCircularQueue3 q3 = new MyCircularQueue3(3);

        // Same operations on all
        q1.enQueue(1); q2.enQueue(1); q3.enQueue(1);
        q1.enQueue(2); q2.enQueue(2); q3.enQueue(2);

        System.out.println("Array (with size): Front=" + q1.Front() + ", Rear=" + q1.Rear());
        System.out.println("Array (no size):   Front=" + q2.Front() + ", Rear=" + q2.Rear());
        System.out.println("Linked List:       Front=" + q3.Front() + ", Rear=" + q3.Rear());
        System.out.println();
    }

    private static void testCircularDeque() {
        System.out.println("=== LeetCode 641: Circular Deque ===\n");

        MyCircularDeque deque = new MyCircularDeque(3);

        System.out.println("insertLast(1): " + deque.insertLast(1));   // true
        System.out.println("insertLast(2): " + deque.insertLast(2));   // true
        System.out.println("insertFront(3): " + deque.insertFront(3)); // true
        System.out.println("insertFront(4): " + deque.insertFront(4)); // false (full)
        System.out.println("getRear(): " + deque.getRear());           // 2
        System.out.println("getFront(): " + deque.getFront());         // 3
        System.out.println("deleteLast(): " + deque.deleteLast());     // true
        System.out.println("getRear(): " + deque.getRear());           // 1
        System.out.println();
    }
}

/**
 * Key Insights
 */
class CircularQueueInsights {
    /*
     * Why Circular Queue?
     * ===================
     * - Efficient use of space (reuse freed space at front)
     * - No need to shift elements
     * - O(1) enqueue and dequeue operations
     *
     * Implementation Choices:
     * =======================
     * 1. Array with size counter:
     *    - Simple logic
     *    - Extra variable for size
     *    - Easy to check empty/full
     *
     * 2. Array with extra space:
     *    - No size variable needed
     *    - Use one extra space to distinguish full vs empty
     *    - front == rear: empty
     *    - (rear + 1) % capacity == front: full
     *
     * 3. Linked List:
     *    - More memory per element (node overhead)
     *    - Dynamic size (though limited by capacity)
     *    - No modulo operations
     *
     * Common Pitfalls:
     * ================
     * - Rear calculation: (rear - 1 + capacity) % capacity
     * - Empty vs Full: Need size or extra space to distinguish
     * - Modulo operations: Always add capacity before subtracting
     *
     * Applications:
     * =============
     * - CPU scheduling (round-robin)
     * - Buffer for data streams
     * - Producer-consumer problems
     * - Traffic flow systems
     */
}
