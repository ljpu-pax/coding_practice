import java.util.*;

/**
 * LeetCode 1381: Design a Stack With Increment Operation
 *
 * Design a stack that supports increment operations on its elements.
 *
 * Implement the CustomStack class:
 * - CustomStack(int maxSize) Initializes the object with maxSize which is the maximum number of elements
 *   in the stack.
 * - void push(int x) Adds x to the top of the stack if the stack has not reached the maxSize.
 * - int pop() Pops and returns the top of the stack or -1 if the stack is empty.
 * - void increment(int k, int val) Increments the bottom k elements of the stack by val. If there are less
 *   than k elements in the stack, increment all the elements in the stack.
 *
 * Example 1:
 * Input:
 * ["CustomStack","push","push","pop","push","push","push","increment","increment","pop","pop","pop","pop"]
 * [[3],[1],[2],[],[2],[3],[4],[5,100],[2,100],[],[],[],[]]
 * Output:
 * [null,null,null,2,null,null,null,null,null,103,202,201,-1]
 *
 * Explanation:
 * CustomStack stk = new CustomStack(3); // Stack is Empty []
 * stk.push(1);                          // stack becomes [1]
 * stk.push(2);                          // stack becomes [1, 2]
 * stk.pop();                            // return 2 --> Return top of the stack 2, stack becomes [1]
 * stk.push(2);                          // stack becomes [1, 2]
 * stk.push(3);                          // stack becomes [1, 2, 3]
 * stk.push(4);                          // stack still [1, 2, 3], Do not add another elements as size is 4
 * stk.increment(5, 100);                // stack becomes [101, 102, 103]
 * stk.increment(2, 100);                // stack becomes [201, 202, 103]
 * stk.pop();                            // return 103 --> Return top of the stack 103, stack becomes [201, 202]
 * stk.pop();                            // return 202 --> Return top of the stack 202, stack becomes [201]
 * stk.pop();                            // return 201 --> Return top of the stack 201, stack becomes []
 * stk.pop();                            // return -1 --> Stack is empty return -1.
 *
 * Constraints:
 * - 1 <= maxSize, x, k <= 1000
 * - 0 <= val <= 100
 * - At most 1000 calls will be made to each method of increment, push and pop each separately.
 */

/**
 * Approach 1: Array with Lazy Increment (Optimal)
 *
 * Key Insight: Instead of updating k elements immediately during increment(),
 * use a lazy propagation array to defer the increment until pop().
 *
 * - increment[i] stores the increment value to be added to stack[i]
 * - When popping, add increment[top] to the result and propagate it down
 *
 * Time Complexity:
 * - push(): O(1)
 * - pop(): O(1)
 * - increment(): O(1)
 *
 * Space: O(maxSize)
 */
class CustomStack {
    private int[] stack;
    private int[] increment; // lazy increment array
    private int top;
    private int maxSize;

    public CustomStack(int maxSize) {
        this.maxSize = maxSize;
        this.stack = new int[maxSize];
        this.increment = new int[maxSize];
        this.top = -1;
    }

    public void push(int x) {
        if (top < maxSize - 1) {
            top++;
            stack[top] = x;
        }
    }

    public int pop() {
        if (top == -1) {
            return -1;
        }

        // Get the result with accumulated increment
        int result = stack[top] + increment[top];

        // Propagate increment to the element below
        if (top > 0) {
            increment[top - 1] += increment[top];
        }

        // Clear the increment at current top
        increment[top] = 0;
        top--;

        return result;
    }

    public void increment(int k, int val) {
        // Only increment up to min(k-1, top)
        int limit = Math.min(k - 1, top);
        if (limit >= 0) {
            increment[limit] += val;
        }
    }
}

/**
 * Approach 2: Array with Direct Increment (Simple but slower)
 *
 * Directly update the bottom k elements during increment operation.
 *
 * Time Complexity:
 * - push(): O(1)
 * - pop(): O(1)
 * - increment(): O(k)
 *
 * Space: O(maxSize)
 */
class CustomStackDirect {
    private int[] stack;
    private int top;
    private int maxSize;

    public CustomStackDirect(int maxSize) {
        this.maxSize = maxSize;
        this.stack = new int[maxSize];
        this.top = -1;
    }

    public void push(int x) {
        if (top < maxSize - 1) {
            top++;
            stack[top] = x;
        }
    }

    public int pop() {
        if (top == -1) {
            return -1;
        }
        return stack[top--];
    }

    public void increment(int k, int val) {
        int limit = Math.min(k, top + 1);
        for (int i = 0; i < limit; i++) {
            stack[i] += val;
        }
    }
}

/**
 * Approach 3: Using ArrayList (More flexible)
 *
 * Time Complexity:
 * - push(): O(1) amortized
 * - pop(): O(1)
 * - increment(): O(k)
 *
 * Space: O(maxSize)
 */
class CustomStackList {
    private List<Integer> stack;
    private int maxSize;

    public CustomStackList(int maxSize) {
        this.maxSize = maxSize;
        this.stack = new ArrayList<>();
    }

    public void push(int x) {
        if (stack.size() < maxSize) {
            stack.add(x);
        }
    }

    public int pop() {
        if (stack.isEmpty()) {
            return -1;
        }
        return stack.remove(stack.size() - 1);
    }

    public void increment(int k, int val) {
        int limit = Math.min(k, stack.size());
        for (int i = 0; i < limit; i++) {
            stack.set(i, stack.get(i) + val);
        }
    }
}

/**
 * Test cases
 */
class CustomStackTest {
    public static void main(String[] args) {
        System.out.println("=== Testing LeetCode 1381: Design Stack with Increment ===\n");

        testCustomStack();
        testCustomStackDirect();
        testCustomStackList();
    }

    private static void testCustomStack() {
        System.out.println("Testing Approach 1: Lazy Increment (Optimal)");
        CustomStack stk = new CustomStack(3);

        stk.push(1);                          // stack becomes [1]
        stk.push(2);                          // stack becomes [1, 2]
        int result1 = stk.pop();              // return 2, stack becomes [1]
        System.out.println("pop(): " + result1 + " (Expected: 2) - " + (result1 == 2 ? "PASS" : "FAIL"));

        stk.push(2);                          // stack becomes [1, 2]
        stk.push(3);                          // stack becomes [1, 2, 3]
        stk.push(4);                          // stack still [1, 2, 3] (maxSize reached)

        stk.increment(5, 100);                // stack becomes [101, 102, 103]
        stk.increment(2, 100);                // stack becomes [201, 202, 103]

        int result2 = stk.pop();              // return 103
        System.out.println("pop(): " + result2 + " (Expected: 103) - " + (result2 == 103 ? "PASS" : "FAIL"));

        int result3 = stk.pop();              // return 202
        System.out.println("pop(): " + result3 + " (Expected: 202) - " + (result3 == 202 ? "PASS" : "FAIL"));

        int result4 = stk.pop();              // return 201
        System.out.println("pop(): " + result4 + " (Expected: 201) - " + (result4 == 201 ? "PASS" : "FAIL"));

        int result5 = stk.pop();              // return -1 (empty)
        System.out.println("pop(): " + result5 + " (Expected: -1) - " + (result5 == -1 ? "PASS" : "FAIL"));

        System.out.println();
    }

    private static void testCustomStackDirect() {
        System.out.println("Testing Approach 2: Direct Increment");
        CustomStackDirect stk = new CustomStackDirect(3);

        stk.push(1);
        stk.push(2);
        int result1 = stk.pop();
        System.out.println("pop(): " + result1 + " (Expected: 2) - " + (result1 == 2 ? "PASS" : "FAIL"));

        stk.push(2);
        stk.push(3);
        stk.push(4);

        stk.increment(5, 100);
        stk.increment(2, 100);

        int result2 = stk.pop();
        System.out.println("pop(): " + result2 + " (Expected: 103) - " + (result2 == 103 ? "PASS" : "FAIL"));

        int result3 = stk.pop();
        System.out.println("pop(): " + result3 + " (Expected: 202) - " + (result3 == 202 ? "PASS" : "FAIL"));

        int result4 = stk.pop();
        System.out.println("pop(): " + result4 + " (Expected: 201) - " + (result4 == 201 ? "PASS" : "FAIL"));

        int result5 = stk.pop();
        System.out.println("pop(): " + result5 + " (Expected: -1) - " + (result5 == -1 ? "PASS" : "FAIL"));

        System.out.println();
    }

    private static void testCustomStackList() {
        System.out.println("Testing Approach 3: ArrayList Implementation");
        CustomStackList stk = new CustomStackList(3);

        stk.push(1);
        stk.push(2);
        int result1 = stk.pop();
        System.out.println("pop(): " + result1 + " (Expected: 2) - " + (result1 == 2 ? "PASS" : "FAIL"));

        stk.push(2);
        stk.push(3);
        stk.push(4);

        stk.increment(5, 100);
        stk.increment(2, 100);

        int result2 = stk.pop();
        System.out.println("pop(): " + result2 + " (Expected: 103) - " + (result2 == 103 ? "PASS" : "FAIL"));

        int result3 = stk.pop();
        System.out.println("pop(): " + result3 + " (Expected: 202) - " + (result3 == 202 ? "PASS" : "FAIL"));

        int result4 = stk.pop();
        System.out.println("pop(): " + result4 + " (Expected: 201) - " + (result4 == 201 ? "PASS" : "FAIL"));

        int result5 = stk.pop();
        System.out.println("pop(): " + result5 + " (Expected: -1) - " + (result5 == -1 ? "PASS" : "FAIL"));

        System.out.println();
    }
}

/**
 * Test for the bug case
 */
class CustomStackBugTest {
    public static void main(String[] args) {
        System.out.println("=== Testing Bug Case ===\n");
        CustomStack stk = new CustomStack(30);

        System.out.println("pop(): " + stk.pop() + " (Expected: -1)");
        stk.increment(3, 40);
        System.out.println("increment(3, 40) on empty stack");

        stk.push(30);
        System.out.println("push(30)");

        stk.increment(4, 63);
        System.out.println("increment(4, 63)");

        stk.increment(2, 79);
        System.out.println("increment(2, 79)");

        stk.increment(5, 57);
        System.out.println("increment(5, 57)");

        int result = stk.pop();
        System.out.println("pop(): " + result + " (Expected: 229) - " + (result == 229 ? "PASS" : "FAIL"));

        stk.increment(5, 32);
        System.out.println("increment(5, 32) on empty stack");
        System.out.println();
    }
}

/**
 * Additional test cases for edge cases
 */
class CustomStackEdgeCaseTest {
    public static void main(String[] args) {
        System.out.println("=== Testing Edge Cases ===\n");

        // Test 1: Size 1 stack
        System.out.println("Test 1: Size 1 stack");
        CustomStack stk1 = new CustomStack(1);
        stk1.push(10);
        stk1.push(20); // Should not be added
        stk1.increment(1, 5);
        int result1 = stk1.pop();
        System.out.println("pop(): " + result1 + " (Expected: 15) - " + (result1 == 15 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 2: Multiple increments on same elements
        System.out.println("Test 2: Multiple increments");
        CustomStack stk2 = new CustomStack(3);
        stk2.push(1);
        stk2.push(2);
        stk2.push(3);
        stk2.increment(3, 10);
        stk2.increment(2, 20);
        stk2.increment(1, 30);
        int result2a = stk2.pop(); // 3 + 10 = 13
        int result2b = stk2.pop(); // 2 + 10 + 20 = 32
        int result2c = stk2.pop(); // 1 + 10 + 20 + 30 = 61
        System.out.println("pop(): " + result2a + " (Expected: 13) - " + (result2a == 13 ? "PASS" : "FAIL"));
        System.out.println("pop(): " + result2b + " (Expected: 32) - " + (result2b == 32 ? "PASS" : "FAIL"));
        System.out.println("pop(): " + result2c + " (Expected: 61) - " + (result2c == 61 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 3: Increment empty stack
        System.out.println("Test 3: Increment empty stack");
        CustomStack stk3 = new CustomStack(3);
        stk3.increment(5, 100); // Should do nothing
        int result3 = stk3.pop();
        System.out.println("pop(): " + result3 + " (Expected: -1) - " + (result3 == -1 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 4: Increment with k > stack size
        System.out.println("Test 4: k > stack size");
        CustomStack stk4 = new CustomStack(5);
        stk4.push(1);
        stk4.push(2);
        stk4.increment(10, 50); // Should increment all 2 elements
        int result4a = stk4.pop();
        int result4b = stk4.pop();
        System.out.println("pop(): " + result4a + " (Expected: 52) - " + (result4a == 52 ? "PASS" : "FAIL"));
        System.out.println("pop(): " + result4b + " (Expected: 51) - " + (result4b == 51 ? "PASS" : "FAIL"));
        System.out.println();
    }
}
