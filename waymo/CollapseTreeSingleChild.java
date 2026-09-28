/**
 * Problem: Collapse Binary Tree with Single Children
 *
 * Given a binary tree, if a node has only one child, delete that child
 * and add its value to the current node. Recursively apply this rule.
 *
 * Example 1:
 *       1
 *      /
 *     2
 *    /
 *   3
 *
 * Step 1: Node 2 has only one child (3), so collapse: 2 becomes 2+3=5, delete node 3
 *       1
 *      /
 *     5
 *
 * Step 2: Node 1 has only one child (5), so collapse: 1 becomes 1+5=6, delete node 5
 *       6
 *
 * Result: Single node with value 6
 *
 * Example 2:
 *       5
 *      / \
 *     3   8
 *    /
 *   2
 *
 * Step 1: Node 3 has only one child (2), collapse: 3 becomes 3+2=5, delete node 2
 *       5
 *      / \
 *     5   8
 *
 * Result: Node 5 has two children (5 and 8), so stop
 *
 * Example 3:
 *       10
 *      /  \
 *     5    15
 *    / \     \
 *   3   7    20
 *
 * Node 15 has only right child (20): 15 becomes 15+20=35
 * Result:
 *       10
 *      /  \
 *     5    35
 *    / \
 *   3   7
 */
public class CollapseTreeSingleChild {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    /**
     * Approach 1: Recursive post-order traversal
     * Process children first, then handle current node
     *
     * Time: O(n) - visit each node once
     * Space: O(h) - recursion stack, h = height
     */
    public TreeNode collapseTree(TreeNode root) {
        if (root == null) {
            return null;
        }

        // Post-order: process children first
        root.left = collapseTree(root.left);
        root.right = collapseTree(root.right);

        // Now handle current node
        return collapseNode(root);
    }

    /**
     * Collapse current node if it has only one child
     * Keep collapsing recursively until node has 0 or 2 children
     */
    private TreeNode collapseNode(TreeNode node) {
        if (node == null) {
            return null;
        }

        // Keep collapsing while node has exactly one child
        while (node != null && hasOnlyOneChild(node)) {
            if (node.left != null) {
                // Has only left child
                node.val += node.left.val;
                node.left = node.left.left != null ? node.left.left : node.left.right;
                // After merging, check if we need to collapse again
            } else {
                // Has only right child
                node.val += node.right.val;
                node.left = node.right.left;
                TreeNode oldRight = node.right.right;
                node.right = oldRight;
            }
        }

        return node;
    }

    private boolean hasOnlyOneChild(TreeNode node) {
        return (node.left != null && node.right == null) ||
               (node.left == null && node.right != null);
    }

    /**
     * Approach 2: Cleaner recursive approach
     * Recursively collapse the single child path
     */
    public TreeNode collapseTreeV2(TreeNode root) {
        if (root == null) {
            return null;
        }

        // Process children first
        root.left = collapseTreeV2(root.left);
        root.right = collapseTreeV2(root.right);

        // If node has only one child, collapse it
        if (root.left != null && root.right == null) {
            // Only has left child
            root.val += root.left.val;
            root.left = root.left.left;
            root.right = root.left != null ? null : root.right;

            // Recursively collapse in case the new structure still has one child
            return collapseTreeV2(root);

        } else if (root.left == null && root.right != null) {
            // Only has right child
            root.val += root.right.val;
            root.left = root.right.left;
            root.right = root.right.right;

            // Recursively collapse in case the new structure still has one child
            return collapseTreeV2(root);
        }

        // Has 0 or 2 children - no collapse needed
        return root;
    }

    /**
     * Approach 3: Most concise version
     */
    public TreeNode collapseTreeV3(TreeNode root) {
        if (root == null) {
            return null;
        }

        // Recursively process subtrees
        root.left = collapseTreeV3(root.left);
        root.right = collapseTreeV3(root.right);

        // Collapse if only one child exists
        while (root.left != null && root.right == null) {
            root.val += root.left.val;
            root.left = collapseTreeV3(root.left.left);
            root.right = collapseTreeV3(root.left != null ? root.left.right : null);
        }

        while (root.left == null && root.right != null) {
            root.val += root.right.val;
            root.left = collapseTreeV3(root.right.left);
            root.right = collapseTreeV3(root.right.right);
        }

        return root;
    }

    // ==================== Helper Methods ====================

    /**
     * Build tree from array (level-order, null for missing nodes)
     */
    public static TreeNode buildTree(Integer[] values) {
        if (values == null || values.length == 0 || values[0] == null) {
            return null;
        }

        TreeNode root = new TreeNode(values[0]);
        java.util.Queue<TreeNode> queue = new java.util.LinkedList<>();
        queue.offer(root);
        int i = 1;

        while (!queue.isEmpty() && i < values.length) {
            TreeNode node = queue.poll();

            if (i < values.length && values[i] != null) {
                node.left = new TreeNode(values[i]);
                queue.offer(node.left);
            }
            i++;

            if (i < values.length && values[i] != null) {
                node.right = new TreeNode(values[i]);
                queue.offer(node.right);
            }
            i++;
        }

        return root;
    }

    /**
     * Print tree structure
     */
    public static void printTree(TreeNode root, String prefix, boolean isLeft) {
        if (root == null) {
            return;
        }

        System.out.println(prefix + (isLeft ? "├── " : "└── ") + root.val);

        if (root.left != null || root.right != null) {
            if (root.left != null) {
                printTree(root.left, prefix + (isLeft ? "│   " : "    "), true);
            } else {
                System.out.println(prefix + (isLeft ? "│   " : "    ") + "├── null");
            }

            if (root.right != null) {
                printTree(root.right, prefix + (isLeft ? "│   " : "    "), false);
            } else {
                System.out.println(prefix + (isLeft ? "│   " : "    ") + "└── null");
            }
        }
    }

    public static void printTree(TreeNode root) {
        if (root == null) {
            System.out.println("Empty tree");
            return;
        }
        System.out.println("Tree structure:");
        System.out.println(root.val);
        if (root.left != null || root.right != null) {
            printTree(root.left, "", true);
            printTree(root.right, "", false);
        }
    }

    /**
     * Count total sum of all node values
     */
    public static int sumTree(TreeNode root) {
        if (root == null) return 0;
        return root.val + sumTree(root.left) + sumTree(root.right);
    }

    // ==================== Test Cases ====================

    public static void main(String[] args) {
        CollapseTreeSingleChild solution = new CollapseTreeSingleChild();

        System.out.println("=== Test 1: Linear chain (from problem description) ===");
        TreeNode tree1 = new TreeNode(1);
        tree1.left = new TreeNode(2);
        tree1.left.left = new TreeNode(3);

        System.out.println("Before collapse:");
        printTree(tree1);
        int sum1Before = sumTree(tree1);

        TreeNode result1 = solution.collapseTree(tree1);
        System.out.println("\nAfter collapse:");
        printTree(result1);
        int sum1After = sumTree(result1);

        System.out.println("Sum before: " + sum1Before + ", Sum after: " + sum1After);
        System.out.println("Expected: Single node with value 6");
        System.out.println();

        System.out.println("=== Test 2: Has one single-child path ===");
        TreeNode tree2 = new TreeNode(5);
        tree2.left = new TreeNode(3);
        tree2.right = new TreeNode(8);
        tree2.left.left = new TreeNode(2);

        System.out.println("Before collapse:");
        printTree(tree2);
        int sum2Before = sumTree(tree2);

        TreeNode result2 = solution.collapseTree(tree2);
        System.out.println("\nAfter collapse:");
        printTree(result2);
        int sum2After = sumTree(result2);

        System.out.println("Sum before: " + sum2Before + ", Sum after: " + sum2After);
        System.out.println();

        System.out.println("=== Test 3: Right-only single child ===");
        TreeNode tree3 = new TreeNode(10);
        tree3.left = new TreeNode(5);
        tree3.right = new TreeNode(15);
        tree3.left.left = new TreeNode(3);
        tree3.left.right = new TreeNode(7);
        tree3.right.right = new TreeNode(20);

        System.out.println("Before collapse:");
        printTree(tree3);
        int sum3Before = sumTree(tree3);

        TreeNode result3 = solution.collapseTree(tree3);
        System.out.println("\nAfter collapse:");
        printTree(result3);
        int sum3After = sumTree(result3);

        System.out.println("Sum before: " + sum3Before + ", Sum after: " + sum3After);
        System.out.println();

        System.out.println("=== Test 4: Perfect binary tree (no collapse) ===");
        TreeNode tree4 = new TreeNode(10);
        tree4.left = new TreeNode(5);
        tree4.right = new TreeNode(15);
        tree4.left.left = new TreeNode(3);
        tree4.left.right = new TreeNode(7);
        tree4.right.left = new TreeNode(12);
        tree4.right.right = new TreeNode(20);

        System.out.println("Before collapse:");
        printTree(tree4);

        TreeNode result4 = solution.collapseTree(tree4);
        System.out.println("\nAfter collapse:");
        printTree(result4);
        System.out.println("Expected: No change (all nodes have 0 or 2 children)");
        System.out.println();

        System.out.println("=== Test 5: Single node ===");
        TreeNode tree5 = new TreeNode(42);

        System.out.println("Before collapse:");
        printTree(tree5);

        TreeNode result5 = solution.collapseTree(tree5);
        System.out.println("\nAfter collapse:");
        printTree(result5);
        System.out.println();

        System.out.println("=== Test 6: Complex case with multiple single-child nodes ===");
        TreeNode tree6 = new TreeNode(1);
        tree6.left = new TreeNode(2);
        tree6.right = new TreeNode(3);
        tree6.left.left = new TreeNode(4);
        tree6.left.left.left = new TreeNode(5);
        tree6.right.right = new TreeNode(6);
        tree6.right.right.right = new TreeNode(7);

        System.out.println("Before collapse:");
        printTree(tree6);
        int sum6Before = sumTree(tree6);

        TreeNode result6 = solution.collapseTreeV2(tree6);
        System.out.println("\nAfter collapse:");
        printTree(result6);
        int sum6After = sumTree(result6);

        System.out.println("Sum before: " + sum6Before + ", Sum after: " + sum6After);
        System.out.println();
    }
}
