import java.util.*;

/**
 * Count Pairs of Leaf Nodes with Distance <= K
 *
 * Given a binary tree and an integer k, count the number of pairs of leaf nodes
 * where the distance between them is at most k.
 *
 * The distance between two nodes is defined as the number of edges in the shortest
 * path between them.
 *
 * This is similar to LeetCode 1530: Number of Good Leaf Nodes Pairs
 *
 * Example 1:
 * Input: root = [1,2,3,null,4], distance = 3
 *       1
 *      / \
 *     2   3
 *      \
 *       4
 * Output: 1
 * Explanation: The leaf nodes are 3 and 4, and the distance between them is 3.
 *
 * Example 2:
 * Input: root = [1,2,3,4,5,6,7], distance = 3
 *       1
 *      / \
 *     2   3
 *    / \ / \
 *   4  5 6  7
 * Output: 2
 * Explanation: Good pairs are [4,5] and [6,7], both with distance 2.
 *
 * Example 3:
 * Input: root = [7,1,4,6,null,5,3,null,null,null,null,null,2], distance = 3
 * Output: 1
 * Explanation: The only good pair is [2,5].
 */

/**
 * Definition for a binary tree node.
 */
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int x) { val = x; }
}

class LeafPairsDistance {
    /**
     * Approach 1: DFS with Distance Array
     *
     * For each subtree, return an array of distances from root to all leaves.
     * For each node, combine distances from left and right subtrees to count pairs.
     *
     * Key Insight: Distance between two leaves through a common ancestor =
     *              distance_to_left_leaf + distance_to_right_leaf + 2
     *
     * Time: O(n * d^2) where n = number of nodes, d = max distance
     * Space: O(n * d) for storing distances
     */
    public int countPairs(TreeNode root, int distance) {
        int[] count = new int[1];
        dfs(root, distance, count);
        return count[0];
    }

    // Returns array where arr[i] = number of leaves at distance i from this node
    private int[] dfs(TreeNode node, int distance, int[] count) {
        if (node == null) {
            return new int[distance + 1];
        }

        // Leaf node
        if (node.left == null && node.right == null) {
            int[] distances = new int[distance + 1];
            distances[0] = 1; // One leaf at distance 0 (itself)
            return distances;
        }

        // Get distances from left and right subtrees
        int[] leftDistances = dfs(node.left, distance, count);
        int[] rightDistances = dfs(node.right, distance, count);

        // Count pairs where one leaf is in left subtree and one is in right
        for (int i = 0; i < distance; i++) {
            for (int j = 0; j < distance; j++) {
                if (i + j + 2 <= distance) {
                    count[0] += leftDistances[i] * rightDistances[j];
                }
            }
        }

        // Build distance array for current node
        int[] currentDistances = new int[distance + 1];
        for (int i = 0; i < distance; i++) {
            currentDistances[i + 1] += leftDistances[i];
            currentDistances[i + 1] += rightDistances[i];
        }

        return currentDistances;
    }

    /**
     * Approach 2: DFS with List (More flexible)
     *
     * Similar to approach 1 but using lists instead of arrays.
     *
     * Time: O(n * d^2)
     * Space: O(n * d)
     */
    public int countPairsWithList(TreeNode root, int distance) {
        int[] count = new int[1];
        dfsWithList(root, distance, count);
        return count[0];
    }

    // Returns list of distances from current node to all leaf nodes in subtree
    private List<Integer> dfsWithList(TreeNode node, int distance, int[] count) {
        List<Integer> distances = new ArrayList<>();

        if (node == null) {
            return distances;
        }

        // Leaf node
        if (node.left == null && node.right == null) {
            distances.add(1); // Distance 1 from parent
            return distances;
        }

        // Get distances from subtrees
        List<Integer> leftDistances = dfsWithList(node.left, distance, count);
        List<Integer> rightDistances = dfsWithList(node.right, distance, count);

        // Count pairs
        for (int left : leftDistances) {
            for (int right : rightDistances) {
                if (left + right <= distance) {
                    count[0]++;
                }
            }
        }

        // Increment all distances by 1 (going up one level)
        for (int left : leftDistances) {
            if (left + 1 <= distance) {
                distances.add(left + 1);
            }
        }
        for (int right : rightDistances) {
            if (right + 1 <= distance) {
                distances.add(right + 1);
            }
        }

        return distances;
    }

    /**
     * Approach 3: Convert to Graph + BFS (Less efficient but more intuitive)
     *
     * 1. Build parent pointers
     * 2. Find all leaf nodes
     * 3. For each leaf, do BFS to find all leaves within distance k
     *
     * Time: O(n^2) in worst case
     * Space: O(n)
     */
    public int countPairsGraph(TreeNode root, int distance) {
        // Build graph with parent pointers
        Map<TreeNode, TreeNode> parent = new HashMap<>();
        List<TreeNode> leaves = new ArrayList<>();
        buildGraph(root, null, parent, leaves);

        int count = 0;

        // For each leaf, BFS to find other leaves within distance
        for (int i = 0; i < leaves.size(); i++) {
            count += countReachableLeaves(leaves.get(i), leaves, parent, distance, i);
        }

        return count / 2; // Divide by 2 because we count each pair twice
    }

    private void buildGraph(TreeNode node, TreeNode par,
                           Map<TreeNode, TreeNode> parent, List<TreeNode> leaves) {
        if (node == null) return;

        parent.put(node, par);

        if (node.left == null && node.right == null) {
            leaves.add(node);
            return;
        }

        buildGraph(node.left, node, parent, leaves);
        buildGraph(node.right, node, parent, leaves);
    }

    private int countReachableLeaves(TreeNode start, List<TreeNode> leaves,
                                     Map<TreeNode, TreeNode> parent, int distance, int startIdx) {
        Queue<TreeNode> queue = new LinkedList<>();
        Set<TreeNode> visited = new HashSet<>();
        queue.offer(start);
        visited.add(start);

        int count = 0;
        int dist = 0;

        while (!queue.isEmpty() && dist <= distance) {
            int size = queue.size();

            for (int i = 0; i < size; i++) {
                TreeNode curr = queue.poll();

                // Check if it's a leaf (not the starting leaf)
                if (curr != start && curr.left == null && curr.right == null) {
                    // Only count if this leaf comes after start in the list (to avoid duplicates)
                    int leafIdx = leaves.indexOf(curr);
                    if (leafIdx > startIdx) {
                        count++;
                    }
                }

                // Add neighbors (parent, left child, right child)
                if (parent.get(curr) != null && !visited.contains(parent.get(curr))) {
                    visited.add(parent.get(curr));
                    queue.offer(parent.get(curr));
                }
                if (curr.left != null && !visited.contains(curr.left)) {
                    visited.add(curr.left);
                    queue.offer(curr.left);
                }
                if (curr.right != null && !visited.contains(curr.right)) {
                    visited.add(curr.right);
                    queue.offer(curr.right);
                }
            }

            dist++;
        }

        return count;
    }
}

/**
 * Related variations
 */
class LeafPairsDistanceVariations {
    /**
     * Find the actual pairs of leaves within distance k
     */
    public List<int[]> findLeafPairs(TreeNode root, int distance) {
        List<int[]> result = new ArrayList<>();
        dfs(root, distance, result);
        return result;
    }

    private List<int[]> dfs(TreeNode node, int distance, List<int[]> result) {
        List<int[]> leaves = new ArrayList<>();

        if (node == null) {
            return leaves;
        }

        // Leaf node: return [value, distance]
        if (node.left == null && node.right == null) {
            leaves.add(new int[]{node.val, 1});
            return leaves;
        }

        List<int[]> leftLeaves = dfs(node.left, distance, result);
        List<int[]> rightLeaves = dfs(node.right, distance, result);

        // Count pairs between left and right subtrees
        for (int[] left : leftLeaves) {
            for (int[] right : rightLeaves) {
                if (left[1] + right[1] <= distance) {
                    result.add(new int[]{left[0], right[0]});
                }
            }
        }

        // Increment distances and combine
        for (int[] leaf : leftLeaves) {
            if (leaf[1] + 1 <= distance) {
                leaves.add(new int[]{leaf[0], leaf[1] + 1});
            }
        }
        for (int[] leaf : rightLeaves) {
            if (leaf[1] + 1 <= distance) {
                leaves.add(new int[]{leaf[0], leaf[1] + 1});
            }
        }

        return leaves;
    }

    /**
     * Find maximum distance between any two leaves
     */
    public int maxLeafDistance(TreeNode root) {
        int[] maxDist = new int[1];
        maxDepth(root, maxDist);
        return maxDist[0];
    }

    private int maxDepth(TreeNode node, int[] maxDist) {
        if (node == null) return 0;
        if (node.left == null && node.right == null) return 1;

        int leftDepth = maxDepth(node.left, maxDist);
        int rightDepth = maxDepth(node.right, maxDist);

        // Update max distance (diameter through this node)
        maxDist[0] = Math.max(maxDist[0], leftDepth + rightDepth);

        return Math.max(leftDepth, rightDepth) + 1;
    }

    /**
     * Count leaf pairs where distance is exactly k
     */
    public int countPairsExactDistance(TreeNode root, int k) {
        int[] count = new int[1];
        dfsExact(root, k, count);
        return count[0];
    }

    private int[] dfsExact(TreeNode node, int k, int[] count) {
        if (node == null) {
            return new int[k + 1];
        }

        if (node.left == null && node.right == null) {
            int[] distances = new int[k + 1];
            distances[0] = 1;
            return distances;
        }

        int[] leftDistances = dfsExact(node.left, k, count);
        int[] rightDistances = dfsExact(node.right, k, count);

        // Count pairs with exactly distance k
        for (int i = 0; i < k; i++) {
            int j = k - 2 - i; // i + j + 2 = k
            if (j >= 0 && j < k) {
                count[0] += leftDistances[i] * rightDistances[j];
            }
        }

        int[] currentDistances = new int[k + 1];
        for (int i = 0; i < k; i++) {
            currentDistances[i + 1] += leftDistances[i];
            currentDistances[i + 1] += rightDistances[i];
        }

        return currentDistances;
    }
}

/**
 * Test cases
 */
class LeafPairsDistanceTest {
    public static void main(String[] args) {
        testBasicCases();
        testEdgeCases();
        testVariations();
    }

    private static void testBasicCases() {
        System.out.println("=== Testing Leaf Pairs Distance ===\n");
        LeafPairsDistance solution = new LeafPairsDistance();

        // Test 1: [1,2,3,null,4], distance = 3
        //       1
        //      / \
        //     2   3
        //      \
        //       4
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);
        root1.left.right = new TreeNode(4);

        int result1 = solution.countPairs(root1, 3);
        int result1List = solution.countPairsWithList(root1, 3);
        int result1Graph = solution.countPairsGraph(root1, 3);
        System.out.println("Test 1: [1,2,3,null,4], distance = 3");
        System.out.println("  Array: " + result1 + " (Expected: 1) - " + (result1 == 1 ? "PASS" : "FAIL"));
        System.out.println("  List: " + result1List + " (Expected: 1) - " + (result1List == 1 ? "PASS" : "FAIL"));
        System.out.println("  Graph: " + result1Graph + " (Expected: 1) - " + (result1Graph == 1 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 2: [1,2,3,4,5,6,7], distance = 3
        //       1
        //      / \
        //     2   3
        //    / \ / \
        //   4  5 6  7
        TreeNode root2 = new TreeNode(1);
        root2.left = new TreeNode(2);
        root2.right = new TreeNode(3);
        root2.left.left = new TreeNode(4);
        root2.left.right = new TreeNode(5);
        root2.right.left = new TreeNode(6);
        root2.right.right = new TreeNode(7);

        int result2 = solution.countPairs(root2, 3);
        int result2List = solution.countPairsWithList(root2, 3);
        int result2Graph = solution.countPairsGraph(root2, 3);
        System.out.println("Test 2: [1,2,3,4,5,6,7], distance = 3");
        System.out.println("  Array: " + result2 + " (Expected: 2) - " + (result2 == 2 ? "PASS" : "FAIL"));
        System.out.println("  List: " + result2List + " (Expected: 2) - " + (result2List == 2 ? "PASS" : "FAIL"));
        System.out.println("  Graph: " + result2Graph + " (Expected: 2) - " + (result2Graph == 2 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 3: Single node
        TreeNode root3 = new TreeNode(1);
        int result3 = solution.countPairs(root3, 1);
        System.out.println("Test 3: Single node, distance = 1");
        System.out.println("  Result: " + result3 + " (Expected: 0) - " + (result3 == 0 ? "PASS" : "FAIL"));
        System.out.println();
    }

    private static void testEdgeCases() {
        System.out.println("=== Testing Edge Cases ===\n");
        LeafPairsDistance solution = new LeafPairsDistance();

        // Test 1: Two leaves
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);

        System.out.println("Test 1: Two leaves");
        System.out.println("  Distance 1: " + solution.countPairs(root1, 1) + " (Expected: 0)");
        System.out.println("  Distance 2: " + solution.countPairs(root1, 2) + " (Expected: 1)");
        System.out.println("  Distance 3: " + solution.countPairs(root1, 3) + " (Expected: 1)");
        System.out.println();

        // Test 2: All left children (linked list)
        TreeNode root2 = new TreeNode(1);
        root2.left = new TreeNode(2);
        root2.left.left = new TreeNode(3);
        root2.left.left.left = new TreeNode(4);

        int result2 = solution.countPairs(root2, 5);
        System.out.println("Test 2: Linked list (all left), distance = 5");
        System.out.println("  Result: " + result2 + " (Expected: 0) - Only 1 leaf");
        System.out.println();

        // Test 3: Large distance
        TreeNode root3 = new TreeNode(1);
        root3.left = new TreeNode(2);
        root3.right = new TreeNode(3);
        root3.left.left = new TreeNode(4);
        root3.left.right = new TreeNode(5);

        int result3 = solution.countPairs(root3, 100);
        System.out.println("Test 3: Large distance (all pairs should be counted)");
        System.out.println("  Result: " + result3 + " (Expected: 3) - pairs: (4,5), (4,3), (5,3)");
        System.out.println();
    }

    private static void testVariations() {
        System.out.println("=== Testing Variations ===\n");
        LeafPairsDistanceVariations variations = new LeafPairsDistanceVariations();

        // Test 1: Find actual pairs
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);
        root1.left.left = new TreeNode(4);
        root1.left.right = new TreeNode(5);
        root1.right.left = new TreeNode(6);
        root1.right.right = new TreeNode(7);

        List<int[]> pairs = variations.findLeafPairs(root1, 3);
        System.out.println("Test 1: Find actual leaf pairs (distance <= 3)");
        System.out.println("  Pairs found: " + pairs.size());
        for (int[] pair : pairs) {
            System.out.println("    [" + pair[0] + ", " + pair[1] + "]");
        }
        System.out.println();

        // Test 2: Max leaf distance
        int maxDist = variations.maxLeafDistance(root1);
        System.out.println("Test 2: Maximum distance between leaves");
        System.out.println("  Result: " + maxDist + " (Expected: 4)");
        System.out.println();

        // Test 3: Exact distance
        int exactCount = variations.countPairsExactDistance(root1, 2);
        System.out.println("Test 3: Count pairs with exactly distance 2");
        System.out.println("  Result: " + exactCount + " (Expected: 2)");
        System.out.println();
    }
}
