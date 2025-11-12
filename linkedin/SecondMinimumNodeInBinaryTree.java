import java.util.*;

/**
 * LeetCode 671. Second Minimum Node In A Binary Tree
 *
 * Problem:
 * Given a binary tree where each node has value = min(left.val, right.val)
 * Find the second minimum value in the tree
 *
 * Key Property:
 * - root.val = min(left.val, right.val)
 * - This means root always has the minimum value
 * - We need to find the second minimum value
 *
 * Example:
 *       2
 *      / \
 *     2   5
 *        / \
 *       5   7
 *
 * Output: 5
 * Explanation: The minimum is 2, second minimum is 5
 *
 * Time Complexity: O(log N) - optimized solution
 * Space Complexity: O(log N) - recursion stack
 */

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int x) { val = x; }
}

class SecondMinimumNodeInBinaryTree {

    /**
     * Optimized O(log N) Solution
     *
     * Key Insight:
     * - root.val is always the minimum (given property)
     * - Second minimum must be in a subtree where node.val > root.val
     * - We can prune branches where node.val == root.val (they only contain min value)
     *
     * Algorithm:
     * 1. The root has the minimum value (min)
     * 2. Search for the smallest value > min
     * 3. For each node:
     *    - If node.val > min, it's a candidate, but check its subtree for smaller values
     *    - If node.val == min, we must search both subtrees
     *    - If node.val > candidate, we can prune this branch
     */
    public int findSecondMinimumValue(TreeNode root) {
        if (root == null) {
            return -1;
        }

        int min = root.val;
        return findSecond(root, min);
    }

    private int findSecond(TreeNode node, int min) {
        // Base case: leaf or null
        if (node == null) {
            return -1;
        }

        // If current node value > min, it's a candidate
        // But we still need to check its subtrees for potentially smaller values
        if (node.val > min) {
            // This could be our answer, but check children for smaller values
            int leftMin = findSecond(node.left, min);
            int rightMin = findSecond(node.right, min);

            // Compare node.val with children results
            int candidate = node.val;

            if (leftMin != -1) {
                candidate = Math.min(candidate, leftMin);
            }
            if (rightMin != -1) {
                candidate = Math.min(candidate, rightMin);
            }

            return candidate;
        }

        // If node.val == min, search both subtrees
        int leftMin = findSecond(node.left, min);
        int rightMin = findSecond(node.right, min);

        // Combine results from both subtrees
        if (leftMin != -1 && rightMin != -1) {
            return Math.min(leftMin, rightMin);
        }

        // Return whichever is not -1
        return leftMin != -1 ? leftMin : rightMin;
    }

    /**
     * Alternative: More concise version
     */
    public int findSecondMinimumValueConcise(TreeNode root) {
        if (root == null) {
            return -1;
        }
        return helper(root, root.val);
    }

    private int helper(TreeNode node, int min) {
        if (node == null) {
            return -1;
        }

        // Found a value greater than min
        if (node.val > min) {
            return node.val;
        }

        // node.val == min, search both subtrees
        int left = helper(node.left, min);
        int right = helper(node.right, min);

        // If both found, return smaller
        if (left != -1 && right != -1) {
            return Math.min(left, right);
        }

        // Otherwise return the one that's not -1
        return Math.max(left, right);  // max because -1 is smallest
    }
}

/**
 * Why O(log N)?
 * =============
 *
 * Consider the structure:
 *       2
 *      / \
 *     2   5      <- We find 5 here, can stop searching right subtree
 *    / \
 *   2   2
 *  / \
 * 2   4          <- We find 4 here
 *
 * - Once we find a value > min, we can use it to prune other branches
 * - In best case (balanced tree with second min near root), we visit O(log N) nodes
 * - In worst case (all nodes except one are min), we might visit all nodes O(N)
 *
 * The O(log N) complexity assumes:
 * - Tree is relatively balanced
 * - Second minimum appears relatively high in tree
 *
 * More accurate complexity analysis:
 * - Best case: O(log N) - second min near root
 * - Average case: O(N) - might need to visit most nodes
 * - Worst case: O(N) - second min is at bottom or doesn't exist
 */

class SecondMinimumNodeInBinaryTreeTest {
    public static void main(String[] args) {
        SecondMinimumNodeInBinaryTree solution = new SecondMinimumNodeInBinaryTree();

        // Test case 1
        TreeNode root1 = new TreeNode(2);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(5);
        root1.right.left = new TreeNode(5);
        root1.right.right = new TreeNode(7);

        System.out.println("Test 1: " + solution.findSecondMinimumValue(root1)); // Expected: 5

        // Test case 2
        TreeNode root2 = new TreeNode(2);
        root2.left = new TreeNode(2);
        root2.right = new TreeNode(2);

        System.out.println("Test 2: " + solution.findSecondMinimumValue(root2)); // Expected: -1

        // Test case 3
        TreeNode root3 = new TreeNode(1);
        root3.left = new TreeNode(1);
        root3.right = new TreeNode(3);
        root3.left.left = new TreeNode(1);
        root3.left.right = new TreeNode(8);

        System.out.println("Test 3: " + solution.findSecondMinimumValue(root3)); // Expected: 3
    }
}

/**
 * Interview Tips:
 * ==============
 *
 * 1. Key Insight to Mention:
 *    - Root always has minimum value (given property)
 *    - We only need to find smallest value > root.val
 *
 * 2. Optimization Opportunity:
 *    - Can prune branches once we find a good candidate
 *    - No need to search subtrees where all values are min
 *
 * 3. Edge Cases:
 *    - All nodes have same value -> return -1
 *    - Only root exists -> return -1
 *    - Second min appears multiple times -> still return it once
 *
 * 4. Follow-up Questions:
 *    - What if we want kth minimum? -> Use similar approach with priority queue
 *    - What if tree doesn't have the min property? -> Need different approach (traverse all)
 */
