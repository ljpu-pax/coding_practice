import java.util.*;

/**
 * LeetCode 366: Find Leaves of Binary Tree (Medium)
 *
 * Given the root of a binary tree, collect a tree's nodes as if you were doing this:
 * - Collect all the leaf nodes.
 * - Remove all the leaf nodes.
 * - Repeat until the tree is empty.
 *
 * Example 1:
 *        1
 *       / \
 *      2   3
 *     / \
 *    4   5
 *
 * Input: root = [1,2,3,4,5]
 * Output: [[4,5,3],[2],[1]]
 * Explanation:
 * [[4,5,3],[2],[1]] is the unique solution.
 *
 * Example 2:
 * Input: root = [1]
 * Output: [[1]]
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 100]
 * - -100 <= Node.val <= 100
 */

/**
 * Definition for a binary tree node
 */
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

class FindLeavesOfBinaryTree {

    /**
     * Approach 1: DFS with Height Calculation (Optimal)
     *
     * Key insight: Nodes at the same "height from bottom" are collected together.
     * Height from bottom = max distance to a leaf
     * - Leaf nodes have height 0
     * - Parent of leaves have height 1
     * - Root has the maximum height
     *
     * Time: O(N) - visit each node once
     * Space: O(H) for recursion stack where H = height
     */
    public List<List<Integer>> findLeaves(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        getHeight(root, result);
        return result;
    }

    private int getHeight(TreeNode node, List<List<Integer>> result) {
        if (node == null) {
            return -1; // Null nodes have height -1
        }

        // Get height from children
        int leftHeight = getHeight(node.left, result);
        int rightHeight = getHeight(node.right, result);

        // Current node's height = max of children + 1
        int height = Math.max(leftHeight, rightHeight) + 1;

        // Ensure result list has enough sublists
        if (result.size() == height) {
            result.add(new ArrayList<>());
        }

        // Add current node to its height level
        result.get(height).add(node.val);

        return height;
    }

    /**
     * Approach 2: Simulation (Actually removing nodes)
     *
     * Actually modify the tree structure by removing leaves
     * Time: O(N * H) where H = height (need to traverse tree H times)
     * Space: O(N)
     *
     * Not recommended but shows the literal interpretation
     */
    public List<List<Integer>> findLeavesSimulation(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();

        while (root != null) {
            List<Integer> leaves = new ArrayList<>();
            root = removeLeaves(root, leaves);
            result.add(leaves);
        }

        return result;
    }

    private TreeNode removeLeaves(TreeNode node, List<Integer> leaves) {
        if (node == null) {
            return null;
        }

        // Leaf node - collect and remove
        if (node.left == null && node.right == null) {
            leaves.add(node.val);
            return null;
        }

        // Recursively remove leaves from subtrees
        node.left = removeLeaves(node.left, leaves);
        node.right = removeLeaves(node.right, leaves);

        return node;
    }

    /**
     * Approach 3: DFS with explicit height tracking
     */
    public List<List<Integer>> findLeavesWithMap(TreeNode root) {
        Map<Integer, List<Integer>> heightMap = new HashMap<>();
        int maxHeight = dfs(root, heightMap);

        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i <= maxHeight; i++) {
            result.add(heightMap.get(i));
        }

        return result;
    }

    private int dfs(TreeNode node, Map<Integer, List<Integer>> heightMap) {
        if (node == null) {
            return -1;
        }

        int leftHeight = dfs(node.left, heightMap);
        int rightHeight = dfs(node.right, heightMap);
        int height = Math.max(leftHeight, rightHeight) + 1;

        heightMap.putIfAbsent(height, new ArrayList<>());
        heightMap.get(height).add(node.val);

        return height;
    }

    /**
     * Approach 4: Iterative with queue (level-order style)
     *
     * Use BFS-like approach but process from leaves upward
     */
    public List<List<Integer>> findLeavesIterative(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        Map<TreeNode, Integer> heights = new HashMap<>();

        // Calculate heights using post-order traversal
        calculateHeights(root, heights);

        // Group nodes by height
        int maxHeight = heights.getOrDefault(root, 0);
        for (int i = 0; i <= maxHeight; i++) {
            result.add(new ArrayList<>());
        }

        for (Map.Entry<TreeNode, Integer> entry : heights.entrySet()) {
            int height = entry.getValue();
            result.get(height).add(entry.getKey().val);
        }

        return result;
    }

    private void calculateHeights(TreeNode node, Map<TreeNode, Integer> heights) {
        if (node == null) {
            return;
        }

        calculateHeights(node.left, heights);
        calculateHeights(node.right, heights);

        int leftHeight = node.left == null ? -1 : heights.get(node.left);
        int rightHeight = node.right == null ? -1 : heights.get(node.right);

        heights.put(node, Math.max(leftHeight, rightHeight) + 1);
    }
}

/**
 * Related variations
 */
class FindLeavesVariations {
    /**
     * Variation 1: Return only the leaf nodes (not removing layers)
     */
    public List<Integer> getLeaves(TreeNode root) {
        List<Integer> leaves = new ArrayList<>();
        collectLeaves(root, leaves);
        return leaves;
    }

    private void collectLeaves(TreeNode node, List<Integer> leaves) {
        if (node == null) {
            return;
        }

        if (node.left == null && node.right == null) {
            leaves.add(node.val);
            return;
        }

        collectLeaves(node.left, leaves);
        collectLeaves(node.right, leaves);
    }

    /**
     * Variation 2: Count number of layers
     */
    public int countLayers(TreeNode root) {
        if (root == null) {
            return 0;
        }

        int leftHeight = countLayers(root.left);
        int rightHeight = countLayers(root.right);

        return Math.max(leftHeight, rightHeight) + 1;
    }

    /**
     * Variation 3: Get nodes at specific height from bottom
     */
    public List<Integer> getNodesAtHeight(TreeNode root, int targetHeight) {
        List<Integer> result = new ArrayList<>();
        getNodesAtHeightHelper(root, targetHeight, result);
        return result;
    }

    private int getNodesAtHeightHelper(TreeNode node, int target, List<Integer> result) {
        if (node == null) {
            return -1;
        }

        int leftHeight = getNodesAtHeightHelper(node.left, target, result);
        int rightHeight = getNodesAtHeightHelper(node.right, target, result);
        int height = Math.max(leftHeight, rightHeight) + 1;

        if (height == target) {
            result.add(node.val);
        }

        return height;
    }
}

/**
 * Test cases
 */
class FindLeavesOfBinaryTreeTest {
    public static void main(String[] args) {
        testBasicCases();
        testAllApproaches();
        testEdgeCases();
        testVariations();
    }

    private static void testBasicCases() {
        System.out.println("=== LeetCode 366: Find Leaves of Binary Tree ===\n");
        FindLeavesOfBinaryTree solution = new FindLeavesOfBinaryTree();

        // Test 1: Example tree
        //        1
        //       / \
        //      2   3
        //     / \
        //    4   5
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);
        root1.left.left = new TreeNode(4);
        root1.left.right = new TreeNode(5);

        List<List<Integer>> result1 = solution.findLeaves(root1);
        System.out.println("Test 1:");
        System.out.println("Tree structure:");
        System.out.println("       1");
        System.out.println("      / \\");
        System.out.println("     2   3");
        System.out.println("    / \\");
        System.out.println("   4   5");
        System.out.println("Result: " + result1);
        System.out.println("Expected: [[4,5,3],[2],[1]]");
        System.out.println();

        // Test 2: Single node
        TreeNode root2 = new TreeNode(1);
        List<List<Integer>> result2 = solution.findLeaves(root2);
        System.out.println("Test 2: Single node");
        System.out.println("Result: " + result2);
        System.out.println("Expected: [[1]]");
        System.out.println();
    }

    private static void testAllApproaches() {
        System.out.println("=== Comparing All Approaches ===\n");
        FindLeavesOfBinaryTree solution = new FindLeavesOfBinaryTree();

        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);

        // Need to create separate trees for simulation approach
        TreeNode root2 = cloneTree(root);
        TreeNode root3 = cloneTree(root);
        TreeNode root4 = cloneTree(root);

        List<List<Integer>> result1 = solution.findLeaves(root);
        List<List<Integer>> result2 = solution.findLeavesSimulation(root2);
        List<List<Integer>> result3 = solution.findLeavesWithMap(root3);
        List<List<Integer>> result4 = solution.findLeavesIterative(root4);

        System.out.println("Height-based DFS: " + result1);
        System.out.println("Simulation:       " + result2);
        System.out.println("With HashMap:     " + result3);
        System.out.println("Iterative:        " + result4);
        System.out.println();
    }

    private static void testEdgeCases() {
        System.out.println("=== Edge Cases ===\n");
        FindLeavesOfBinaryTree solution = new FindLeavesOfBinaryTree();

        // Linear tree (left-skewed)
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(2);
        root1.left.left = new TreeNode(3);
        System.out.println("Left-skewed tree: " + solution.findLeaves(root1));

        // Linear tree (right-skewed)
        TreeNode root2 = new TreeNode(1);
        root2.right = new TreeNode(2);
        root2.right.right = new TreeNode(3);
        System.out.println("Right-skewed tree: " + solution.findLeaves(root2));

        // Perfect binary tree
        TreeNode root3 = new TreeNode(1);
        root3.left = new TreeNode(2);
        root3.right = new TreeNode(3);
        root3.left.left = new TreeNode(4);
        root3.left.right = new TreeNode(5);
        root3.right.left = new TreeNode(6);
        root3.right.right = new TreeNode(7);
        System.out.println("Perfect binary tree: " + solution.findLeaves(root3));
        System.out.println();
    }

    private static void testVariations() {
        System.out.println("=== Testing Variations ===\n");
        FindLeavesVariations variations = new FindLeavesVariations();

        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);

        System.out.println("Get all leaves: " + variations.getLeaves(root));
        System.out.println("Count layers: " + variations.countLayers(root));
        System.out.println("Nodes at height 0: " + variations.getNodesAtHeight(root, 0));
        System.out.println("Nodes at height 1: " + variations.getNodesAtHeight(root, 1));
        System.out.println("Nodes at height 2: " + variations.getNodesAtHeight(root, 2));
        System.out.println();
    }

    private static TreeNode cloneTree(TreeNode root) {
        if (root == null) {
            return null;
        }

        TreeNode newNode = new TreeNode(root.val);
        newNode.left = cloneTree(root.left);
        newNode.right = cloneTree(root.right);
        return newNode;
    }
}

/**
 * Key Insights
 */
class FindLeavesInsights {
    /*
     * Key Concept: Height from Bottom
     * ================================
     * Unlike typical tree height (distance from root), we use "height from bottom":
     * - Leaf nodes: height 0
     * - Parent of leaves: height 1
     * - Grandparent of leaves: height 2
     * - Root: maximum height
     *
     * Example:
     *        1 (height 2)
     *       / \
     *      2   3 (height 1, 0)
     *     / \
     *    4   5 (height 0)
     *
     * Grouping:
     * - Height 0: [4, 5, 3] (all leaves)
     * - Height 1: [2] (parent of leaves)
     * - Height 2: [1] (root)
     *
     * Why DFS Works:
     * ==============
     * - Post-order traversal naturally computes height from bottom
     * - Process children before parent
     * - Parent's height = max(left height, right height) + 1
     *
     * Comparison with Tree Traversals:
     * =================================
     * - Pre-order: Process root, then children (top-down)
     * - In-order: Process left, root, right (for BST)
     * - Post-order: Process children, then root (bottom-up) ✓ We use this!
     * - Level-order: Process level by level (BFS)
     *
     * Time Complexity Analysis:
     * =========================
     * Approach 1 (Height-based DFS):
     * - Time: O(N) - visit each node once
     * - Space: O(H) for recursion
     * - Best approach!
     *
     * Approach 2 (Simulation):
     * - Time: O(N * H) - traverse tree H times
     * - Space: O(N)
     * - Inefficient but intuitive
     *
     * Common Pitfalls:
     * ================
     * 1. Confusing "height from bottom" with "depth from top"
     * 2. Forgetting null nodes have height -1 (not 0)
     * 3. Not handling empty tree case
     * 4. Modifying tree structure unnecessarily
     *
     * Related Concepts:
     * =================
     * - Tree height/depth calculation
     * - Lowest Common Ancestor
     * - Diameter of Binary Tree
     * - Binary Tree Level Order Traversal
     * - Delete Leaves With a Given Value (LC 1325)
     *
     * Interview Tips:
     * ===============
     * 1. Start by drawing the tree and labeling heights
     * 2. Recognize post-order traversal pattern
     * 3. Explain why null nodes have height -1
     * 4. Mention O(N) time complexity is optimal
     * 5. Discuss space complexity: O(H) vs O(N)
     *
     * LinkedIn Connection:
     * ====================
     * This problem tests:
     * - Tree traversal understanding
     * - Recursive thinking
     * - Bottom-up computation
     * - Common in LinkedIn interviews for understanding tree algorithms
     */
}
