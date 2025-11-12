import java.util.*;

/**
 * LeetCode 716. Max Stack
 *
 * Problem:
 * Design a max stack data structure that supports:
 * - push(x): Push element x onto the stack
 * - pop(): Remove the element on top of the stack and return it
 * - top(): Get the element on the top of the stack
 * - peekMax(): Retrieve the maximum element in the stack
 * - popMax(): Retrieve the maximum element and remove it from the stack
 *
 * If there are multiple max elements, only remove the top-most one.
 *
 * Example:
 * MaxStack stk = new MaxStack();
 * stk.push(5);   // [5]
 * stk.push(1);   // [5, 1]
 * stk.push(5);   // [5, 1, 5]
 * stk.top();     // return 5
 * stk.popMax();  // return 5, stack becomes [5, 1]
 * stk.top();     // return 1
 * stk.peekMax(); // return 5
 * stk.pop();     // return 1, stack becomes [5]
 * stk.top();     // return 5
 *
 * Follow-up: Can you make all operations O(log n)?
 */

/**
 * Solution 1: Two Stacks (Simple Approach)
 *
 * Use two stacks:
 * - stack: stores all elements
 * - maxStack: stores maximum elements at each position
 *
 * Time Complexity:
 * - push: O(1)
 * - pop: O(1)
 * - top: O(1)
 * - peekMax: O(1)
 * - popMax: O(n) - need to temporarily pop elements
 *
 * Space Complexity: O(n)
 */
class MaxStack {
    private Stack<Integer> stack;
    private Stack<Integer> maxStack;

    public MaxStack() {
        stack = new Stack<>();
        maxStack = new Stack<>();
    }

    public void push(int x) {
        stack.push(x);
        if (maxStack.isEmpty()) {
            maxStack.push(x);
        } else {
            maxStack.push(Math.max(x, maxStack.peek()));
        }
    }

    public int pop() {
        maxStack.pop();
        return stack.pop();
    }

    public int top() {
        return stack.peek();
    }

    public int peekMax() {
        return maxStack.peek();
    }

    public int popMax() {
        int max = peekMax();
        Stack<Integer> buffer = new Stack<>();

        // Pop elements until we find the max
        while (top() != max) {
            buffer.push(pop());
        }

        // Remove the max
        pop();

        // Push back the buffered elements
        while (!buffer.isEmpty()) {
            push(buffer.pop());
        }

        return max;
    }
}

/**
 * Solution 2: TreeMap + DoublyLinkedList (Optimized O(log n))
 *
 * Data structures:
 * - DoublyLinkedList: maintains insertion order (stack order)
 * - TreeMap<value, List<Node>>: maps values to their nodes for fast max lookup
 *
 * Time Complexity:
 * - push: O(log n) - TreeMap insert
 * - pop: O(log n) - TreeMap remove
 * - top: O(1) - access tail of list
 * - peekMax: O(log n) - TreeMap lastKey
 * - popMax: O(log n) - TreeMap lastKey + remove
 *
 * Space Complexity: O(n)
 */
class MaxStackOptimized {

    // Node in doubly linked list
    class Node {
        int val;
        Node prev, next;

        Node(int val) {
            this.val = val;
        }
    }

    // Doubly linked list to maintain stack order
    private Node head, tail;

    // TreeMap: value -> list of nodes with that value
    private TreeMap<Integer, List<Node>> map;

    public MaxStackOptimized() {
        head = new Node(0);
        tail = new Node(0);
        head.next = tail;
        tail.prev = head;
        map = new TreeMap<>();
    }

    public void push(int x) {
        Node node = new Node(x);

        // Add to end of linked list (top of stack)
        node.prev = tail.prev;
        node.next = tail;
        tail.prev.next = node;
        tail.prev = node;

        // Add to TreeMap
        map.putIfAbsent(x, new ArrayList<>());
        map.get(x).add(node);
    }

    public int pop() {
        // Remove from end of linked list
        Node node = tail.prev;
        removeNode(node);

        // Remove from TreeMap
        List<Node> nodes = map.get(node.val);
        nodes.remove(nodes.size() - 1);
        if (nodes.isEmpty()) {
            map.remove(node.val);
        }

        return node.val;
    }

    public int top() {
        return tail.prev.val;
    }

    public int peekMax() {
        return map.lastKey();
    }

    public int popMax() {
        int max = peekMax();

        // Get the list of nodes with max value
        List<Node> nodes = map.get(max);

        // Remove the last (top-most) occurrence
        Node node = nodes.remove(nodes.size() - 1);

        // Clean up TreeMap if no more nodes with this value
        if (nodes.isEmpty()) {
            map.remove(max);
        }

        // Remove from linked list
        removeNode(node);

        return max;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
}

/**
 * Solution 3: Soft Delete with Counter (Alternative)
 *
 * Use a counter to mark deleted elements instead of physically removing them.
 * Clean up during pop operations.
 */
class MaxStackSoftDelete {
    private Stack<int[]> stack; // [value, id]
    private TreeMap<Integer, Stack<Integer>> valueToIds; // value -> stack of ids
    private Set<Integer> deleted; // set of deleted ids
    private int id;

    public MaxStackSoftDelete() {
        stack = new Stack<>();
        valueToIds = new TreeMap<>();
        deleted = new HashSet<>();
        id = 0;
    }

    public void push(int x) {
        stack.push(new int[]{x, id});
        valueToIds.putIfAbsent(x, new Stack<>());
        valueToIds.get(x).push(id);
        id++;
    }

    public int pop() {
        // Skip deleted elements
        while (!stack.isEmpty() && deleted.contains(stack.peek()[1])) {
            stack.pop();
        }

        int[] top = stack.pop();
        int val = top[0];
        int topId = top[1];

        // Remove from valueToIds
        Stack<Integer> ids = valueToIds.get(val);
        ids.pop();
        if (ids.isEmpty()) {
            valueToIds.remove(val);
        }

        return val;
    }

    public int top() {
        // Skip deleted elements
        while (!stack.isEmpty() && deleted.contains(stack.peek()[1])) {
            stack.pop();
        }
        return stack.peek()[0];
    }

    public int peekMax() {
        return valueToIds.lastKey();
    }

    public int popMax() {
        int max = peekMax();
        Stack<Integer> ids = valueToIds.get(max);

        // Mark the top-most occurrence as deleted
        int maxId = ids.pop();
        deleted.add(maxId);

        if (ids.isEmpty()) {
            valueToIds.remove(max);
        }

        return max;
    }
}

/**
 * Test Cases
 */
class MaxStackTest {
    public static void main(String[] args) {
        System.out.println("=== Testing Simple MaxStack ===\n");
        testSimpleMaxStack();

        System.out.println("\n=== Testing Optimized MaxStack ===\n");
        testOptimizedMaxStack();

        System.out.println("\n=== Testing Soft Delete MaxStack ===\n");
        testSoftDeleteMaxStack();
    }

    private static void testSimpleMaxStack() {
        MaxStack stk = new MaxStack();

        stk.push(5);
        System.out.println("push(5)");

        stk.push(1);
        System.out.println("push(1)");

        stk.push(5);
        System.out.println("push(5)");

        System.out.println("top(): " + stk.top() + " (expected: 5)");
        System.out.println("popMax(): " + stk.popMax() + " (expected: 5)");
        System.out.println("top(): " + stk.top() + " (expected: 1)");
        System.out.println("peekMax(): " + stk.peekMax() + " (expected: 5)");
        System.out.println("pop(): " + stk.pop() + " (expected: 1)");
        System.out.println("top(): " + stk.top() + " (expected: 5)");
    }

    private static void testOptimizedMaxStack() {
        MaxStackOptimized stk = new MaxStackOptimized();

        stk.push(5);
        System.out.println("push(5)");

        stk.push(1);
        System.out.println("push(1)");

        stk.push(5);
        System.out.println("push(5)");

        System.out.println("top(): " + stk.top() + " (expected: 5)");
        System.out.println("popMax(): " + stk.popMax() + " (expected: 5)");
        System.out.println("top(): " + stk.top() + " (expected: 1)");
        System.out.println("peekMax(): " + stk.peekMax() + " (expected: 5)");
        System.out.println("pop(): " + stk.pop() + " (expected: 1)");
        System.out.println("top(): " + stk.top() + " (expected: 5)");
    }

    private static void testSoftDeleteMaxStack() {
        MaxStackSoftDelete stk = new MaxStackSoftDelete();

        stk.push(5);
        System.out.println("push(5)");

        stk.push(1);
        System.out.println("push(1)");

        stk.push(5);
        System.out.println("push(5)");

        System.out.println("top(): " + stk.top() + " (expected: 5)");
        System.out.println("popMax(): " + stk.popMax() + " (expected: 5)");
        System.out.println("top(): " + stk.top() + " (expected: 1)");
        System.out.println("peekMax(): " + stk.peekMax() + " (expected: 5)");
        System.out.println("pop(): " + stk.pop() + " (expected: 1)");
        System.out.println("top(): " + stk.top() + " (expected: 5)");
    }
}

/**
 * Complexity Summary:
 * ==================
 *
 * Solution 1 (Two Stacks):
 * -------------------------
 * push:     O(1)
 * pop:      O(1)
 * top:      O(1)
 * peekMax:  O(1)
 * popMax:   O(n) ⚠️
 *
 * Space: O(n)
 * Best for: Simple cases, few popMax operations
 *
 *
 * Solution 2 (TreeMap + DoublyLinkedList): ⭐ BEST
 * ----------------------------------------------
 * push:     O(log n)
 * pop:      O(log n)
 * top:      O(1)
 * peekMax:  O(log n)
 * popMax:   O(log n)
 *
 * Space: O(n)
 * Best for: Balanced operations, production code
 *
 *
 * Solution 3 (Soft Delete):
 * -------------------------
 * push:     O(log n)
 * pop:      O(n) amortized - cleanup deleted
 * top:      O(n) worst case - skip deleted
 * peekMax:  O(log n)
 * popMax:   O(log n)
 *
 * Space: O(n) - stores deleted IDs
 * Best for: Many popMax, few pop/top
 *
 *
 * Interview Tips:
 * ==============
 *
 * 1. Start with Two Stacks:
 *    - Easiest to implement
 *    - Show you understand the basics
 *    - Mention O(n) popMax limitation
 *
 * 2. Optimize to O(log n):
 *    - Use TreeMap for max tracking
 *    - Use DoublyLinkedList for O(1) removal
 *    - Explain trade-offs
 *
 * 3. Key Insights:
 *    - popMax removes TOP-MOST max (not all max elements)
 *    - Need to maintain both value order and insertion order
 *    - TreeMap.lastKey() gives max in O(log n)
 *
 * 4. Edge Cases:
 *    - Single element
 *    - All elements are the same
 *    - Multiple max elements
 *    - popMax then pop
 *
 * 5. Follow-up Questions:
 *    - Thread-safety? → Add synchronized
 *    - Distributed system? → Separate data structures
 *    - What if we need popMin too? → Add TreeMap.firstKey()
 */
