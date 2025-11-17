// LeetCode 272: Closest Binary Search Tree Value II
// https://leetcode.com/problems/closest-binary-search-tree-value-ii/
// Difficulty: Hard
// Premium Problem

// Given the root of a binary search tree, a target value, and an integer k,
// return the k values in the BST that are closest to the target.
// You may return the answer in any order.

// You are guaranteed to have only one unique set of k values in the BST
// that are closest to the target.

// Example 1:
// Input: root = [4,2,5,1,3], target = 3.714286, k = 2
// Output: [4,3]

// Example 2:
// Input: root = [1], target = 0.000000, k = 1
// Output: [1]

// Constraints:
// - The number of nodes in the tree is n.
// - 1 <= k <= n <= 10^4
// - 0 <= Node.val <= 10^9
// - -10^9 <= target <= 10^9

import java.util.*;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

class ClosestBSTValueII {
    // Approach 1: Inorder Traversal + Sliding Window
    // Time: O(n), Space: O(n)
    public List<Integer> closestKValues(TreeNode root, double target, int k) {
        List<Integer> inorder = new ArrayList<>();
        inorderTraversal(root, inorder);

        // Find the closest element to target
        int closestIdx = 0;
        double minDiff = Double.MAX_VALUE;

        for (int i = 0; i < inorder.size(); i++) {
            double diff = Math.abs(inorder.get(i) - target);
            if (diff < minDiff) {
                minDiff = diff;
                closestIdx = i;
            }
        }

        // Use two pointers to find k closest values
        List<Integer> result = new ArrayList<>();
        int left = closestIdx;
        int right = closestIdx;

        while (result.size() < k) {
            if (left < 0) {
                result.add(inorder.get(right++));
            } else if (right >= inorder.size()) {
                result.add(inorder.get(left--));
            } else {
                // Choose the closer one
                double leftDiff = Math.abs(inorder.get(left) - target);
                double rightDiff = Math.abs(inorder.get(right) - target);

                if (leftDiff <= rightDiff) {
                    result.add(inorder.get(left--));
                } else {
                    result.add(inorder.get(right++));
                }
            }
        }

        return result;
    }

    private void inorderTraversal(TreeNode root, List<Integer> list) {
        if (root == null) return;
        inorderTraversal(root.left, list);
        list.add(root.val);
        inorderTraversal(root.right, list);
    }

    // Approach 2: Max Heap (Priority Queue)
    // Time: O(n log k), Space: O(k)
    public List<Integer> closestKValuesHeap(TreeNode root, double target, int k) {
        // Max heap based on distance from target
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) ->
            Double.compare(Math.abs(b - target), Math.abs(a - target))
        );

        dfsHeap(root, target, k, maxHeap);

        return new ArrayList<>(maxHeap);
    }

    private void dfsHeap(TreeNode node, double target, int k, PriorityQueue<Integer> maxHeap) {
        if (node == null) return;

        maxHeap.offer(node.val);

        if (maxHeap.size() > k) {
            maxHeap.poll(); // Remove the farthest element
        }

        dfsHeap(node.left, target, k, maxHeap);
        dfsHeap(node.right, target, k, maxHeap);
    }

    // Approach 3: Two Stacks (Optimal for BST)
    // Time: O(k + log n), Space: O(log n)
    // This approach leverages BST properties for optimal performance
    public List<Integer> closestKValuesStacks(TreeNode root, double target, int k) {
        List<Integer> result = new ArrayList<>();

        // Stack for predecessors (values <= target)
        Stack<TreeNode> predecessors = new Stack<>();
        // Stack for successors (values > target)
        Stack<TreeNode> successors = new Stack<>();

        // Initialize the stacks
        initializeStacks(root, target, predecessors, successors);

        // Merge the two stacks to get k closest values
        while (k-- > 0) {
            if (predecessors.isEmpty()) {
                result.add(getNextSuccessor(successors));
            } else if (successors.isEmpty()) {
                result.add(getNextPredecessor(predecessors));
            } else {
                double predDiff = Math.abs(predecessors.peek().val - target);
                double succDiff = Math.abs(successors.peek().val - target);

                if (predDiff < succDiff) {
                    result.add(getNextPredecessor(predecessors));
                } else {
                    result.add(getNextSuccessor(successors));
                }
            }
        }

        return result;
    }

    private void initializeStacks(TreeNode root, double target,
                                   Stack<TreeNode> predecessors,
                                   Stack<TreeNode> successors) {
        TreeNode node = root;

        while (node != null) {
            if (node.val <= target) {
                predecessors.push(node);
                node = node.right;
            } else {
                successors.push(node);
                node = node.left;
            }
        }
    }

    private int getNextPredecessor(Stack<TreeNode> predecessors) {
        TreeNode node = predecessors.pop();
        int result = node.val;

        // Move to next predecessor (next smaller value in inorder)
        node = node.left;
        while (node != null) {
            predecessors.push(node);
            node = node.right;
        }

        return result;
    }

    private int getNextSuccessor(Stack<TreeNode> successors) {
        TreeNode node = successors.pop();
        int result = node.val;

        // Move to next successor (next larger value in inorder)
        node = node.right;
        while (node != null) {
            successors.push(node);
            node = node.left;
        }

        return result;
    }

    // Approach 4: Inorder with Early Termination (Space Optimized)
    // Time: O(n), Space: O(k) for result
    public List<Integer> closestKValuesOptimized(TreeNode root, double target, int k) {
        LinkedList<Integer> result = new LinkedList<>();
        inorderWithWindow(root, target, k, result);
        return result;
    }

    private void inorderWithWindow(TreeNode node, double target, int k, LinkedList<Integer> result) {
        if (node == null) return;

        inorderWithWindow(node.left, target, k, result);

        // Maintain a sliding window of size k
        if (result.size() < k) {
            result.add(node.val);
        } else {
            // Check if current value is closer than the oldest in window
            double oldDiff = Math.abs(result.getFirst() - target);
            double newDiff = Math.abs(node.val - target);

            if (newDiff < oldDiff) {
                result.removeFirst();
                result.add(node.val);
            } else {
                // Since BST is sorted, no need to check further
                return;
            }
        }

        inorderWithWindow(node.right, target, k, result);
    }

    public static void main(String[] args) {
        ClosestBSTValueII solution = new ClosestBSTValueII();

        // Test case 1: [4,2,5,1,3], target = 3.714286, k = 2
        TreeNode root1 = new TreeNode(4);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(5);
        root1.left.left = new TreeNode(1);
        root1.left.right = new TreeNode(3);

        System.out.println("Test 1 - Inorder + Sliding Window:");
        System.out.println(solution.closestKValues(root1, 3.714286, 2)); // [3, 4] or [4, 3]

        System.out.println("\nTest 1 - Max Heap:");
        System.out.println(solution.closestKValuesHeap(root1, 3.714286, 2));

        System.out.println("\nTest 1 - Two Stacks:");
        System.out.println(solution.closestKValuesStacks(root1, 3.714286, 2));

        // Test case 2: [1], target = 0.0, k = 1
        TreeNode root2 = new TreeNode(1);
        System.out.println("\nTest 2:");
        System.out.println(solution.closestKValues(root2, 0.0, 1)); // [1]

        // Test case 3: Larger tree
        TreeNode root3 = new TreeNode(5);
        root3.left = new TreeNode(3);
        root3.right = new TreeNode(7);
        root3.left.left = new TreeNode(2);
        root3.left.right = new TreeNode(4);
        root3.right.left = new TreeNode(6);
        root3.right.right = new TreeNode(8);

        System.out.println("\nTest 3 - Target 5.5, k=3:");
        System.out.println(solution.closestKValues(root3, 5.5, 3)); // [5, 6, 4]
    }
}

/*
 * Key Insights:
 *
 * Approach Comparison:
 * ====================
 *
 * 1. Inorder Traversal + Sliding Window:
 *    - Time: O(n) - visit all nodes
 *    - Space: O(n) - store all values
 *    - Pros: Simple, easy to understand
 *    - Cons: Uses extra space for entire tree
 *
 * 2. Max Heap:
 *    - Time: O(n log k) - visit all nodes, maintain heap of size k
 *    - Space: O(k) - heap size
 *    - Pros: Space efficient, doesn't use BST property
 *    - Cons: Slightly slower due to heap operations
 *
 * 3. Two Stacks (Optimal):
 *    - Time: O(k + log n) - initialize stacks + get k values
 *    - Space: O(log n) - stack height
 *    - Pros: Best time complexity, uses BST property
 *    - Cons: More complex implementation
 *    - Best when k << n
 *
 * 4. Inorder with Early Termination:
 *    - Time: O(n) worst case, can be better with early stop
 *    - Space: O(k) for result
 *    - Pros: Space efficient for result
 *    - Cons: May still traverse entire tree
 *
 * Why Two Stacks is Optimal:
 * ==========================
 * - Leverages BST property: inorder traversal is sorted
 * - Predecessor stack: all values <= target (in descending order)
 * - Successor stack: all values > target (in ascending order)
 * - Merge k closest values from both stacks in O(k) time
 * - Only traverses O(log n) nodes to initialize + O(k) for result
 *
 * BST Properties Used:
 * ====================
 * - Inorder traversal gives sorted sequence
 * - Can efficiently find predecessor/successor
 * - Values to left are smaller, values to right are larger
 * - Height is O(log n) for balanced BST
 *
 * Interview Tips:
 * ===============
 * 1. Start with simple inorder + two pointers approach
 * 2. Mention heap approach as optimization
 * 3. For follow-up, discuss two-stack approach
 * 4. Highlight BST property utilization
 * 5. Discuss trade-offs: time vs space, simplicity vs optimality
 *
 * Edge Cases:
 * ===========
 * - k = n (return all nodes)
 * - k = 1 (similar to LeetCode 270)
 * - Target equals a node value
 * - Target < all values or > all values
 * - Single node tree
 *
 * Related Problems:
 * =================
 * - LeetCode 270: Closest Binary Search Tree Value (k = 1)
 * - LeetCode 658: Find K Closest Elements (sorted array version)
 * - LeetCode 230: Kth Smallest Element in a BST
 */
