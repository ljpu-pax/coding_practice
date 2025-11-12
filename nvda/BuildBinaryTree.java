package nvda;

import java.util.*;

public class BuildBinaryTree {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int x) { val = x; }
    }

    // Leetcode 105: Construct Binary Tree from Preorder and Inorder Traversal
    // Example: preorder = [3,9,20,15,7], inorder = [9,3,15,20,7]
    // Returns:     3
    //            /   \
    //           9     20
    //                /  \
    //               15   7
    public TreeNode buildTreePreIn(int[] preorder, int[] inorder) {
        if(preorder == null || inorder == null || preorder.length != inorder.length) return null;
        Map<Integer, Integer> inMap = new HashMap<>();
        for(int i = 0; i < inorder.length; i++)
            inMap.put(inorder[i], i);
        
        return buildPreIn(preorder, 0, preorder.length - 1, inorder, 0, inorder.length - 1, inMap);
    }

    // Helper method for buildTreePreIn
    // Recursively builds tree by finding root in preorder, then splitting inorder array
    // Example: preorder=[3,9,20,15,7], inorder=[9,3,15,20,7]
    // Root=3, left subtree: preorder[1:2]=[9], inorder[0:0]=[9]
    // Right subtree: preorder[3:4]=[20,15,7], inorder[2:4]=[15,20,7]
    private TreeNode buildPreIn(int[] preorder, int preStart, int preEnd,
                                int[] inorder, int inStart, int inEnd,
                                Map<Integer, Integer> inMap) {
        if(preStart > preEnd || inStart > inEnd) return null;
        
        TreeNode root = new TreeNode(preorder[preStart]);
        int inRoot = inMap.get(root.val);
        int numsLeft = inRoot - inStart;

        root.left = buildPreIn(preorder, preStart + 1, preStart + numsLeft,
                               inorder, inStart, inRoot - 1, inMap);
        root.right = buildPreIn(preorder, preStart + numsLeft + 1, preEnd,
                                inorder, inRoot + 1, inEnd, inMap);
        return root;
    }

    // Leetcode 106: Construct Binary Tree from Inorder and Postorder Traversal
    // Example: inorder = [9,3,15,20,7], postorder = [9,15,7,20,3]
    // Returns:     3
    //            /   \
    //           9     20
    //                /  \
    //               15   7
    public TreeNode buildTreeInPost(int[] inorder, int[] postorder) {
        if(postorder == null || inorder == null || postorder.length != inorder.length) return null;
        Map<Integer, Integer> inMap = new HashMap<>();
        for(int i = 0; i < inorder.length; i++)
            inMap.put(inorder[i], i);
        
        return buildInPost(postorder, 0, postorder.length - 1, inorder, 0, inorder.length - 1, inMap);
    }

    // Helper method for buildTreeInPost
    // Recursively builds tree by finding root in postorder (last element), then splitting inorder array
    // Example: inorder=[9,3,15,20,7], postorder=[9,15,7,20,3]
    // Root=3, left subtree: postorder[0:0]=[9], inorder[0:0]=[9]
    // Right subtree: postorder[1:3]=[15,7,20], inorder[2:4]=[15,20,7]
    private TreeNode buildInPost(int[] postorder, int postStart, int postEnd,
                                 int[] inorder, int inStart, int inEnd,
                                 Map<Integer, Integer> inMap) {
        if(postStart > postEnd || inStart > inEnd) return null;
        
        TreeNode root = new TreeNode(postorder[postEnd]);
        int inRoot = inMap.get(root.val);
        int numsLeft = inRoot - inStart;

        root.left = buildInPost(postorder, postStart, postStart + numsLeft - 1,
                                inorder, inStart, inRoot - 1, inMap);
        root.right = buildInPost(postorder, postStart + numsLeft, postEnd - 1,
                                 inorder, inRoot + 1, inEnd, inMap);
        return root;
    }

    // Helper function to print tree inorder (for verification)
    // Example: For tree [3,9,20,15,7], prints: 9 3 15 20 7
    public void inorderTraversal(TreeNode root) {
        if(root == null) return;
        inorderTraversal(root.left);
        System.out.print(root.val + " ");
        inorderTraversal(root.right);
    }

    // Main function with examples
    // Demonstrates both Leetcode 105 and 106 solutions with the same tree structure
    public static void main(String[] args) {
        BuildBinaryTree solver = new BuildBinaryTree();

        // Test for Leetcode 105
        int[] preorder = {3,9,20,15,7};
        int[] inorder = {9,3,15,20,7};
        TreeNode rootPreIn = solver.buildTreePreIn(preorder, inorder);
        System.out.print("Inorder traversal of tree built from preorder and inorder (Leetcode 105): ");
        solver.inorderTraversal(rootPreIn);
        System.out.println();

        // Test for Leetcode 106
        int[] postorder = {9,15,7,20,3};
        TreeNode rootInPost = solver.buildTreeInPost(inorder, postorder);
        System.out.print("Inorder traversal of tree built from inorder and postorder (Leetcode 106): ");
        solver.inorderTraversal(rootInPost);
        System.out.println();
    }
}
