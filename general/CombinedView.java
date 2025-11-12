import java.util.*;

public class CombinedView {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    public List<Integer> leftRightCombinedView(TreeNode root) {
        if (root == null) return new ArrayList<>();

        Queue<TreeNode> queue = new LinkedList<>();
        List<Integer> leftView = new ArrayList<>();
        List<Integer> rightView = new ArrayList<>();
        Set<Integer> leftViewSet = new HashSet<>();

        queue.offer(root);

        while (!queue.isEmpty()) {
            int size = queue.size();
            Integer leftMost = null, rightMost = null;

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();

                if (i == 0) leftMost = node.val;
                if (i == size - 1) rightMost = node.val;

                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }

            leftView.add(leftMost);
            rightView.add(rightMost);
            leftViewSet.add(leftMost);
        }

        // Final output: reversed left view + right view (excluding root if already printed)
        Collections.reverse(leftView);
        List<Integer> result = new ArrayList<>(leftView);

        for (int val : rightView) {
            // Skip only if root value is already printed once
            if (val == root.val && leftViewSet.contains(val)) continue;
            result.add(val);
        }

        return result;
    }

    public static void main(String[] args) {
        /*
                1
              /   \
             2     3
            /
           5
         */
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(5);

        CombinedView sol = new CombinedView();
        List<Integer> result = sol.leftRightCombinedView(root);
        System.out.println(result);  // Output: [5, 2, 1, 3, 5]
    }
}
