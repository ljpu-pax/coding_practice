package nvda;

import java.util.*;

public class InvertBinaryTree {
    
    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int x) { val = x; }
    }

    // LeetCode 226: Invert Binary Tree
    // Recursive approach - O(n) time, O(h) space where h is height
    public TreeNode invertTreeRecursive(TreeNode root) {
        if (root == null) return null;
        
        // Swap left and right subtrees
        TreeNode temp = root.left;
        root.left = root.right;
        root.right = temp;
        
        // Recursively invert subtrees
        invertTreeRecursive(root.left);
        invertTreeRecursive(root.right);
        
        return root;
    }

    // Iterative approach using BFS - O(n) time, O(w) space where w is max width
    public TreeNode invertTreeIterative(TreeNode root) {
        if (root == null) return null;
        
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            
            // Swap children
            TreeNode temp = node.left;
            node.left = node.right;
            node.right = temp;
            
            // Add children to queue
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        
        return root;
    }

    // Helper method to print tree level by level
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;
        
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> level = new ArrayList<>();
            
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);
                
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            
            result.add(level);
        }
        
        return result;
    }

    // Helper method to create a sample tree
    public static TreeNode createSampleTree() {
        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(7);
        root.left.left = new TreeNode(1);
        root.left.right = new TreeNode(3);
        root.right.left = new TreeNode(6);
        root.right.right = new TreeNode(9);
        return root;
    }

    public static void main(String[] args) {
        InvertBinaryTree solver = new InvertBinaryTree();
        
        // Test with sample tree
        TreeNode original = createSampleTree();
        System.out.println("Original tree (level order):");
        System.out.println(solver.levelOrder(original));
        
        // Test recursive approach
        TreeNode recursiveResult = solver.invertTreeRecursive(createSampleTree());
        System.out.println("After recursive inversion:");
        System.out.println(solver.levelOrder(recursiveResult));
        
        // Test iterative approach
        TreeNode iterativeResult = solver.invertTreeIterative(createSampleTree());
        System.out.println("After iterative inversion:");
        System.out.println(solver.levelOrder(iterativeResult));
    }
}
