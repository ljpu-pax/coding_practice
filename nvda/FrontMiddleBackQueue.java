package nvda;

import java.util.*;

public class FrontMiddleBackQueue {
    private Deque<Integer> left;
    private Deque<Integer> right;

    public FrontMiddleBackQueue() {
        left = new ArrayDeque<>();
        right = new ArrayDeque<>();
    }

    private void balance() {
        // Ensure: left.size() == right.size() or left.size() == right.size() + 1
        while (left.size() > right.size() + 1) {
            right.addFirst(left.removeLast());
        }
        while (left.size() < right.size()) {
            left.addLast(right.removeFirst());
        }
    }

    public void pushFront(int val) {
        left.addFirst(val);
        balance();
    }

    public void pushMiddle(int val) {
        if (left.size() > right.size()) {
            right.addFirst(left.removeLast());
        }
        left.addLast(val);
        balance();
    }

    public void pushBack(int val) {
        right.addLast(val);
        balance();
    }

    public int popFront() {
        if (isEmpty()) return -1;
        int val = left.removeFirst();
        balance();
        return val;
    }

    public int popMiddle() {
        if (isEmpty()) return -1;
        int val = left.removeLast();
        balance();
        return val;
    }

    public int popBack() {
        if (isEmpty()) return -1;
        int val;
        if (!right.isEmpty()) {
            val = right.removeLast();
        } else {
            val = left.removeLast();
        }
        balance();
        return val;
    }

    private boolean isEmpty() {
        return left.isEmpty() && right.isEmpty();
    }

    public static void main(String[] args) {
        FrontMiddleBackQueue q = new FrontMiddleBackQueue();
        q.pushFront(1);               // [1]
        q.pushBack(2);                // [1,2]
        q.pushMiddle(3);              // [1,3,2]
        q.pushMiddle(4);              // [1,4,3,2]
        System.out.println(q.popFront());   // 1
        System.out.println(q.popMiddle());  // 4
        System.out.println(q.popMiddle());  // 3
        System.out.println(q.popBack());    // 2
        System.out.println(q.popFront());   // -1
    }
}

