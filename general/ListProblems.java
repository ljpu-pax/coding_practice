public class ListProblems {
    // Definition for singly-linked list.
    public static class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    public ListNode removeNthFromEnd(ListNode head, int n) {
        int len = 0;
        ListNode result = new ListNode(0);
        result.next = head;
        
        ListNode temp = head;
        while (temp != null) {
            len++;
            temp = temp.next;
        }
        
        int index = len - n;
        temp = result;
        
        while (index > 0) {
            temp = temp.next;
            index--;
        }
        
        temp.next = temp.next.next;
        
        return result.next;
    }

    // Helper method to print and check the ListNode
    private static void assertList(ListNode result, int[] expected) {
        ListNode current = result;
        for (int i = 0; i < expected.length; i++) {
            if (current == null || current.val != expected[i]) {
                System.out.println("Test failed! Expected value " + expected[i] + " but got " + (current == null ? "null" : current.val));
                return;
            }
            current = current.next;
        }
        if (current != null) {
            System.out.println("Test failed! List is longer than expected.");
        }
        System.out.println("Test passed!");
    }

    // Test case 1: Remove middle node
    public static void testRemoveMiddleNode() {
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        head.next.next.next.next = new ListNode(5);
        
        asdsa solution = new asdsa();
        ListNode result = solution.removeNthFromEnd(head, 2); // Remove 2nd from end -> should remove 4
        
        // Expected list: 1 -> 2 -> 3 -> 5
        assertList(result, new int[] {1, 2, 3, 5});
    }

    // Test case 2: Remove the first node (n = length)
    public static void testRemoveFirstNode() {
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        
        asdsa solution = new asdsa();
        ListNode result = solution.removeNthFromEnd(head, 3); // Remove 3rd from end -> should remove 1
        
        // Expected list: 2 -> 3
        assertList(result, new int[] {2, 3});
    }

    // Test case 3: Remove the last node (n = 1)
    public static void testRemoveLastNode() {
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        
        asdsa solution = new asdsa();
        ListNode result = solution.removeNthFromEnd(head, 1); // Remove 1st from end -> should remove 3
        
        // Expected list: 1 -> 2
        assertList(result, new int[] {1, 2});
    }

    // Test case 4: Remove from a list with only one node (n = 1)
    public static void testRemoveOnlyNode() {
        ListNode head = new ListNode(1);
        
        asdsa solution = new asdsa();
        ListNode result = solution.removeNthFromEnd(head, 1); // Remove the 1st from end -> list becomes empty
        
        // Expected list: null
        if (result == null) {
            System.out.println("Test passed!");
        } else {
            System.out.println("Test failed! List should be empty.");
        }
    }

    // Test case 5: Remove the middle node from an even-sized list
    public static void testRemoveMiddleNodeEvenSize() {
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        
        asdsa solution = new asdsa();
        ListNode result = solution.removeNthFromEnd(head, 2); // Remove 2nd from end -> should remove 3
        
        // Expected list: 1 -> 2 -> 4
        assertList(result, new int[] {1, 2, 4});
    }

    // Test case 6: Remove node when n is larger than the list size (invalid input)
    public static void testRemoveInvalidNode() {
        ListNode head = new ListNode(1);
        
        asdsa solution = new asdsa();
        ListNode result = solution.removeNthFromEnd(head, 2); // Trying to remove 2nd from end, but list size is 1
        
        // Expected list: 1 (no changes should be made)
        assertList(result, new int[] {1});
    }

    // Test case 7: Remove from a long list
    public static void testRemoveNodeFromLongList() {
        ListNode head = new ListNode(1);
        ListNode current = head;
        for (int i = 2; i <= 100; i++) {
            current.next = new ListNode(i);
            current = current.next;
        }
        
        asdsa solution = new asdsa();
        ListNode result = solution.removeNthFromEnd(head, 50); // Remove 50th from end -> should remove 51
        
        // Expected list: 1 -> 2 -> 3 ... 50 -> 52 -> 53 ...
        assertList(result, new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 
            21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 
            44, 45, 46, 47, 48, 49, 50, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 
            69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 
            93, 94, 95, 96, 97, 98, 99, 100});
    }

    // Main method to run tests
    public static void main(String[] args) {
        testRemoveMiddleNode();
        testRemoveFirstNode();
        testRemoveLastNode();
        testRemoveOnlyNode();
        testRemoveMiddleNodeEvenSize();
        testRemoveInvalidNode();
        testRemoveNodeFromLongList();
    }
}
