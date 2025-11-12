import java.util.ArrayList;
import java.util.List;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    boolean isActive; // Indicates whether the node is active

    TreeNode(int val, boolean isActive) {
        this.val = val;
        this.left = null;
        this.right = null;
        this.isActive = isActive;
    }
}

public class BinaryTreeMaxPathSumVariant {
    static int maxLeafPathSum = Integer.MIN_VALUE;
    static int maxActivePathSum = Integer.MIN_VALUE;

    public static int findMaxLeafPathSum(TreeNode node) {
        if (node == null) {
            return 0;
        }

        // Check if the node is a leaf
        if (node.left == null && node.right == null) {
            return node.val;
        }

        // Recurse for left and right subtrees
        int leftSum = node.left != null ? findMaxLeafPathSum(node.left) : Integer.MIN_VALUE;
        int rightSum = node.right != null ? findMaxLeafPathSum(node.right) : Integer.MIN_VALUE;

        // Update maxLeafPathSum if a new maximum is found
        if (node.left != null && node.right != null) {
            maxLeafPathSum = Math.max(maxLeafPathSum, leftSum + rightSum + node.val);
        }

        // Return the maximum path sum starting from this node
        return Math.max(leftSum, rightSum) + node.val;
    }

    public static int findMaxActivePathSum(TreeNode node) {
        if (node == null) {
            return 0;
        }

        // Recurse for left and right subtrees
        int leftSum = Math.max(0, findMaxActivePathSum(node.left));
        int rightSum = Math.max(0, findMaxActivePathSum(node.right));

        // Consider only paths between active nodes
        if (node.isActive) {
            maxActivePathSum = Math.max(maxActivePathSum, leftSum + rightSum + node.val);
            return Math.max(leftSum, rightSum) + node.val;
        }

        // If the current node is not active, return 0
        return 0;
    }

    public static void calculateMaxSums(TreeNode root) {
        findMaxLeafPathSum(root); // Calculate max sum between leaves
        findMaxActivePathSum(root); // Calculate max sum between active nodes
    }

    public static void main(String[] args) {
        // Create the binary tree
        TreeNode root = new TreeNode(10, true);
        root.left = new TreeNode(2, true);
        root.right = new TreeNode(10, false);
        root.left.left = new TreeNode(20, true);
        root.left.right = new TreeNode(1, false);
        root.right.right = new TreeNode(-25, true);
        root.right.right.left = new TreeNode(3, true);
        root.right.right.right = new TreeNode(4, true);

        calculateMaxSums(root);

        System.out.println("Maximum Path Sum Between Leaves: " + maxLeafPathSum);
        System.out.println("Maximum Path Sum Between Active Nodes: " + maxActivePathSum);
    }
}
