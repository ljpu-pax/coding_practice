import java.util.*;

/**
 * Definition for a binary tree node.
 */
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int x) { val = x; }
}

/**
 * LeetCode 235: Lowest Common Ancestor of a Binary Search Tree
 *
 * Given a binary search tree (BST), find the lowest common ancestor (LCA) of two given nodes in the BST.
 *
 * According to the definition of LCA on Wikipedia: "The lowest common ancestor is defined between two nodes
 * p and q as the lowest node in T that has both p and q as descendants (where we allow a node to be a
 * descendant of itself)."
 *
 * Example 1:
 * Input: root = [6,2,8,0,4,7,9,null,null,3,5], p = 2, q = 8
 * Output: 6
 * Explanation: The LCA of nodes 2 and 8 is 6.
 *
 * Example 2:
 * Input: root = [6,2,8,0,4,7,9,null,null,3,5], p = 2, q = 4
 * Output: 2
 * Explanation: The LCA of nodes 2 and 4 is 2, since a node can be a descendant of itself.
 */
class LCA_BST_235 {
    // Approach 1: Recursive - Leveraging BST property
    // Time: O(h) where h is height, Space: O(h) for recursion stack
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        // If both p and q are smaller than root, LCA must be in left subtree
        if (p.val < root.val && q.val < root.val) {
            return lowestCommonAncestor(root.left, p, q);
        }
        // If both p and q are greater than root, LCA must be in right subtree
        else if (p.val > root.val && q.val > root.val) {
            return lowestCommonAncestor(root.right, p, q);
        }
        // Otherwise, we have found the split point, which is the LCA
        else {
            return root;
        }
    }

    // Approach 2: Iterative - More space efficient
    // Time: O(h), Space: O(1)
    public TreeNode lowestCommonAncestorIterative(TreeNode root, TreeNode p, TreeNode q) {
        TreeNode curr = root;

        while (curr != null) {
            // Both nodes are in left subtree
            if (p.val < curr.val && q.val < curr.val) {
                curr = curr.left;
            }
            // Both nodes are in right subtree
            else if (p.val > curr.val && q.val > curr.val) {
                curr = curr.right;
            }
            // We've found the split point
            else {
                return curr;
            }
        }

        return null;
    }
}

/**
 * LeetCode 236: Lowest Common Ancestor of a Binary Tree
 *
 * Given a binary tree, find the lowest common ancestor (LCA) of two given nodes in the tree.
 *
 * According to the definition of LCA on Wikipedia: "The lowest common ancestor is defined between two nodes
 * p and q as the lowest node in T that has both p and q as descendants (where we allow a node to be a
 * descendant of itself)."
 *
 * Example 1:
 * Input: root = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 1
 * Output: 3
 * Explanation: The LCA of nodes 5 and 1 is 3.
 *
 * Example 2:
 * Input: root = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 4
 * Output: 5
 * Explanation: The LCA of nodes 5 and 4 is 5, since a node can be a descendant of itself.
 */
class LCA_BinaryTree_236 {
    // Approach 1: Recursive DFS
    // Time: O(n), Space: O(h) for recursion stack
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        // Base case: if root is null or root is one of p or q
        if (root == null || root == p || root == q) {
            return root;
        }

        // Search for p and q in left and right subtrees
        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);

        // If both left and right are non-null, root is the LCA
        if (left != null && right != null) {
            return root;
        }

        // Otherwise, return whichever is non-null (or null if both are null)
        return left != null ? left : right;
    }

    // Approach 2: Using parent pointers (store path from root to each node)
    // Time: O(n), Space: O(n)
    public TreeNode lowestCommonAncestorWithParentMap(TreeNode root, TreeNode p, TreeNode q) {
        // Map to store parent of each node
        Map<TreeNode, TreeNode> parent = new HashMap<>();
        parent.put(root, null);

        // BFS/DFS to populate parent map
        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);

        // Traverse until we find both p and q
        while (!parent.containsKey(p) || !parent.containsKey(q)) {
            TreeNode node = stack.pop();

            if (node.left != null) {
                parent.put(node.left, node);
                stack.push(node.left);
            }

            if (node.right != null) {
                parent.put(node.right, node);
                stack.push(node.right);
            }
        }

        // Get all ancestors of p
        Set<TreeNode> ancestors = new HashSet<>();
        while (p != null) {
            ancestors.add(p);
            p = parent.get(p);
        }

        // Find first ancestor of q that's also an ancestor of p
        while (!ancestors.contains(q)) {
            q = parent.get(q);
        }

        return q;
    }
}

/**
 * Test cases for Lowest Common Ancestor problems
 */
class LowestCommonAncestorTest {
    public static void main(String[] args) {
        testLCA_BST();
        testLCA_BinaryTree();
    }

    private static void testLCA_BST() {
        System.out.println("=== Testing LeetCode 235: LCA of BST ===");
        LCA_BST_235 solution = new LCA_BST_235();

        // Build BST: [6,2,8,0,4,7,9,null,null,3,5]
        //         6
        //       /   \
        //      2     8
        //     / \   / \
        //    0   4 7   9
        //       / \
        //      3   5
        TreeNode root = new TreeNode(6);
        root.left = new TreeNode(2);
        root.right = new TreeNode(8);
        root.left.left = new TreeNode(0);
        root.left.right = new TreeNode(4);
        root.right.left = new TreeNode(7);
        root.right.right = new TreeNode(9);
        root.left.right.left = new TreeNode(3);
        root.left.right.right = new TreeNode(5);

        // Test 1: LCA of 2 and 8
        TreeNode p1 = root.left; // 2
        TreeNode q1 = root.right; // 8
        TreeNode result1 = solution.lowestCommonAncestor(root, p1, q1);
        TreeNode result1Iter = solution.lowestCommonAncestorIterative(root, p1, q1);
        System.out.println("Test 1 - LCA(2, 8):");
        System.out.println("  Recursive: " + result1.val + " (Expected: 6) - " + (result1.val == 6 ? "PASS" : "FAIL"));
        System.out.println("  Iterative: " + result1Iter.val + " (Expected: 6) - " + (result1Iter.val == 6 ? "PASS" : "FAIL"));

        // Test 2: LCA of 2 and 4
        TreeNode p2 = root.left; // 2
        TreeNode q2 = root.left.right; // 4
        TreeNode result2 = solution.lowestCommonAncestor(root, p2, q2);
        TreeNode result2Iter = solution.lowestCommonAncestorIterative(root, p2, q2);
        System.out.println("Test 2 - LCA(2, 4):");
        System.out.println("  Recursive: " + result2.val + " (Expected: 2) - " + (result2.val == 2 ? "PASS" : "FAIL"));
        System.out.println("  Iterative: " + result2Iter.val + " (Expected: 2) - " + (result2Iter.val == 2 ? "PASS" : "FAIL"));

        // Test 3: LCA of 3 and 5
        TreeNode p3 = root.left.right.left; // 3
        TreeNode q3 = root.left.right.right; // 5
        TreeNode result3 = solution.lowestCommonAncestor(root, p3, q3);
        TreeNode result3Iter = solution.lowestCommonAncestorIterative(root, p3, q3);
        System.out.println("Test 3 - LCA(3, 5):");
        System.out.println("  Recursive: " + result3.val + " (Expected: 4) - " + (result3.val == 4 ? "PASS" : "FAIL"));
        System.out.println("  Iterative: " + result3Iter.val + " (Expected: 4) - " + (result3Iter.val == 4 ? "PASS" : "FAIL"));

        System.out.println();
    }

    private static void testLCA_BinaryTree() {
        System.out.println("=== Testing LeetCode 236: LCA of Binary Tree ===");
        LCA_BinaryTree_236 solution = new LCA_BinaryTree_236();

        // Build Binary Tree: [3,5,1,6,2,0,8,null,null,7,4]
        //         3
        //       /   \
        //      5     1
        //     / \   / \
        //    6   2 0   8
        //       / \
        //      7   4
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(5);
        root.right = new TreeNode(1);
        root.left.left = new TreeNode(6);
        root.left.right = new TreeNode(2);
        root.right.left = new TreeNode(0);
        root.right.right = new TreeNode(8);
        root.left.right.left = new TreeNode(7);
        root.left.right.right = new TreeNode(4);

        // Test 1: LCA of 5 and 1
        TreeNode p1 = root.left; // 5
        TreeNode q1 = root.right; // 1
        TreeNode result1 = solution.lowestCommonAncestor(root, p1, q1);
        TreeNode result1Map = solution.lowestCommonAncestorWithParentMap(root, p1, q1);
        System.out.println("Test 1 - LCA(5, 1):");
        System.out.println("  Recursive: " + result1.val + " (Expected: 3) - " + (result1.val == 3 ? "PASS" : "FAIL"));
        System.out.println("  Parent Map: " + result1Map.val + " (Expected: 3) - " + (result1Map.val == 3 ? "PASS" : "FAIL"));

        // Test 2: LCA of 5 and 4
        TreeNode p2 = root.left; // 5
        TreeNode q2 = root.left.right.right; // 4
        TreeNode result2 = solution.lowestCommonAncestor(root, p2, q2);
        TreeNode result2Map = solution.lowestCommonAncestorWithParentMap(root, p2, q2);
        System.out.println("Test 2 - LCA(5, 4):");
        System.out.println("  Recursive: " + result2.val + " (Expected: 5) - " + (result2.val == 5 ? "PASS" : "FAIL"));
        System.out.println("  Parent Map: " + result2Map.val + " (Expected: 5) - " + (result2Map.val == 5 ? "PASS" : "FAIL"));

        // Test 3: LCA of 6 and 4
        TreeNode p3 = root.left.left; // 6
        TreeNode q3 = root.left.right.right; // 4
        TreeNode result3 = solution.lowestCommonAncestor(root, p3, q3);
        TreeNode result3Map = solution.lowestCommonAncestorWithParentMap(root, p3, q3);
        System.out.println("Test 3 - LCA(6, 4):");
        System.out.println("  Recursive: " + result3.val + " (Expected: 5) - " + (result3.val == 5 ? "PASS" : "FAIL"));
        System.out.println("  Parent Map: " + result3Map.val + " (Expected: 5) - " + (result3Map.val == 5 ? "PASS" : "FAIL"));

        // Test 4: LCA of 0 and 8
        TreeNode p4 = root.right.left; // 0
        TreeNode q4 = root.right.right; // 8
        TreeNode result4 = solution.lowestCommonAncestor(root, p4, q4);
        TreeNode result4Map = solution.lowestCommonAncestorWithParentMap(root, p4, q4);
        System.out.println("Test 4 - LCA(0, 8):");
        System.out.println("  Recursive: " + result4.val + " (Expected: 1) - " + (result4.val == 1 ? "PASS" : "FAIL"));
        System.out.println("  Parent Map: " + result4Map.val + " (Expected: 1) - " + (result4Map.val == 1 ? "PASS" : "FAIL"));

        System.out.println();
    }
}
