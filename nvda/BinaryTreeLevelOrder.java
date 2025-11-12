package nvda;

import java.util.*;

public class BinaryTreeLevelOrder {
    
    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int x) { val = x; }
    }

    // LeetCode 102: Binary Tree Level Order Traversal
    // BFS approach - O(n) time, O(w) space where w is max width
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

    // LeetCode 107: Binary Tree Level Order Traversal II (bottom-up)
    // Same as above but reverse the result
    public List<List<Integer>> levelOrderBottom(TreeNode root) {
        List<List<Integer>> result = levelOrder(root);
        Collections.reverse(result);
        return result;
    }

    // LeetCode 103: Binary Tree Zigzag Level Order Traversal
    // Alternate left-to-right and right-to-left
    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;
        
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        boolean leftToRight = true;
        
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> level = new ArrayList<>();
            
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);
                
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            
            if (!leftToRight) {
                Collections.reverse(level);
            }
            
            result.add(level);
            leftToRight = !leftToRight;
        }
        
        return result;
    }

    // LeetCode 199: Binary Tree Right Side View
    // Return the rightmost node at each level
    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;
        
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            int size = queue.size();
            
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                
                // Add the last node of each level (rightmost)
                if (i == size - 1) {
                    result.add(node.val);
                }
                
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
        }
        
        return result;
    }

    // Helper method to create a sample tree
    public static TreeNode createSampleTree() {
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(9);
        root.right = new TreeNode(20);
        root.right.left = new TreeNode(15);
        root.right.right = new TreeNode(7);
        return root;
    }

    public static void main(String[] args) {
        BinaryTreeLevelOrder solver = new BinaryTreeLevelOrder();
        TreeNode root = createSampleTree();
        
        System.out.println("Sample tree: [3,9,20,null,null,15,7]");
        System.out.println();
        
        // Test level order traversal
        System.out.println("Level Order Traversal (LeetCode 102):");
        System.out.println(solver.levelOrder(root));
        
        // Test bottom-up level order
        System.out.println("Bottom-up Level Order (LeetCode 107):");
        System.out.println(solver.levelOrderBottom(root));
        
        // Test zigzag level order
        System.out.println("Zigzag Level Order (LeetCode 103):");
        System.out.println(solver.zigzagLevelOrder(root));
        
        // Test right side view
        System.out.println("Right Side View (LeetCode 199):");
        System.out.println(solver.rightSideView(root));
    }
}
