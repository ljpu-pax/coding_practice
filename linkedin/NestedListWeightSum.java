import java.util.*;

/**
 * This is the interface that allows for creating nested lists.
 * You should not implement it, or speculate about its implementation
 */
interface NestedInteger {
    // @return true if this NestedInteger holds a single integer, rather than a nested list.
    public boolean isInteger();

    // @return the single integer that this NestedInteger holds, if it holds a single integer
    // Return null if this NestedInteger holds a nested list
    public Integer getInteger();

    // Set this NestedInteger to hold a single integer.
    public void setInteger(int value);

    // Set this NestedInteger to hold a nested list and adds a nested integer to it.
    public void add(NestedInteger ni);

    // @return the nested list that this NestedInteger holds, if it holds a nested list
    // Return empty list if this NestedInteger holds a single integer
    public List<NestedInteger> getList();
}

/**
 * Nested List Weight Sum I (LeetCode 339)
 * Given a nested list of integers, return the sum of all integers in the list weighted by their depth.
 * Each element is either an integer, or a list -- whose elements may also be integers or other lists.
 *
 * Example 1:
 * Input: [[1,1],2,[1,1]]
 * Output: 10
 * Explanation: Four 1's at depth 2, one 2 at depth 1. 1*2 + 1*2 + 2*1 + 1*2 + 1*2 = 10.
 *
 * Example 2:
 * Input: [1,[4,[6]]]
 * Output: 27
 * Explanation: One 1 at depth 1, one 4 at depth 2, and one 6 at depth 3. 1*1 + 4*2 + 6*3 = 27.
 */
class NestedListWeightSum1 {
    // DFS Approach
    public int depthSum(List<NestedInteger> nestedList) {
        return dfs(nestedList, 1);
    }

    private int dfs(List<NestedInteger> list, int depth) {
        int sum = 0;
        for (NestedInteger ni : list) {
            if (ni.isInteger()) {
                sum += ni.getInteger() * depth;
            } else {
                sum += dfs(ni.getList(), depth + 1);
            }
        }
        return sum;
    }

    // BFS Approach
    public int depthSumBFS(List<NestedInteger> nestedList) {
        Queue<NestedInteger> queue = new LinkedList<>();
        queue.addAll(nestedList);

        int depth = 1;
        int totalSum = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                NestedInteger ni = queue.poll();
                if (ni.isInteger()) {
                    totalSum += ni.getInteger() * depth;
                } else {
                    queue.addAll(ni.getList());
                }
            }
            depth++;
        }

        return totalSum;
    }
}

/**
 * Nested List Weight Sum II (LeetCode 364)
 * Given a nested list of integers, return the sum of all integers in the list weighted by their depth.
 * Each element is either an integer, or a list -- whose elements may also be integers or other lists.
 *
 * Different from the previous question where weight is increasing from root to leaf, now the weight is
 * defined from bottom up. i.e., the leaf level integers have weight 1, and the root level integers have
 * the largest weight.
 *
 * Example 1:
 * Input: [[1,1],2,[1,1]]
 * Output: 8
 * Explanation: Four 1's at depth 1, one 2 at depth 2. 1*1 + 1*1 + 2*2 + 1*1 + 1*1 = 8.
 *
 * Example 2:
 * Input: [1,[4,[6]]]
 * Output: 17
 * Explanation: One 1 at depth 3, one 4 at depth 2, and one 6 at depth 1. 1*3 + 4*2 + 6*1 = 17.
 */
class NestedListWeightSum2 {
    // Approach 1: Two Pass - Find max depth first, then calculate sum
    public int depthSumInverse(List<NestedInteger> nestedList) {
        int maxDepth = getMaxDepth(nestedList);
        return dfs(nestedList, maxDepth);
    }

    private int getMaxDepth(List<NestedInteger> list) {
        int maxDepth = 1;
        for (NestedInteger ni : list) {
            if (!ni.isInteger()) {
                maxDepth = Math.max(maxDepth, 1 + getMaxDepth(ni.getList()));
            }
        }
        return maxDepth;
    }

    private int dfs(List<NestedInteger> list, int depth) {
        int sum = 0;
        for (NestedInteger ni : list) {
            if (ni.isInteger()) {
                sum += ni.getInteger() * depth;
            } else {
                sum += dfs(ni.getList(), depth - 1);
            }
        }
        return sum;
    }

    // Approach 2: BFS with clever math (One Pass)
    // Key insight: Instead of multiplying by depth directly, we accumulate sums level by level
    // Each level's contribution gets added multiple times based on its inverse depth
    public int depthSumInverseBFS(List<NestedInteger> nestedList) {
        Queue<NestedInteger> queue = new LinkedList<>();
        queue.addAll(nestedList);

        int unweightedSum = 0; // sum of all integers without weight
        int weightedSum = 0;   // running weighted sum

        while (!queue.isEmpty()) {
            int size = queue.size();
            int levelSum = 0;

            for (int i = 0; i < size; i++) {
                NestedInteger ni = queue.poll();
                if (ni.isInteger()) {
                    levelSum += ni.getInteger();
                } else {
                    queue.addAll(ni.getList());
                }
            }

            unweightedSum += levelSum;
            weightedSum += unweightedSum;
        }

        return weightedSum;
    }
}

/**
 * Test class with concrete implementation of NestedInteger for testing
 */
class NestedIntegerImpl implements NestedInteger {
    private Integer value;
    private List<NestedInteger> list;

    public NestedIntegerImpl(Integer value) {
        this.value = value;
    }

    public NestedIntegerImpl(List<NestedInteger> list) {
        this.list = list;
    }

    @Override
    public boolean isInteger() {
        return value != null;
    }

    @Override
    public Integer getInteger() {
        return value;
    }

    @Override
    public void setInteger(int value) {
        this.value = value;
    }

    @Override
    public void add(NestedInteger ni) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(ni);
    }

    @Override
    public List<NestedInteger> getList() {
        return list;
    }
}

/**
 * Test cases for Nested List Weight Sum problems
 */
class NestedListWeightSumTest {
    public static void main(String[] args) {
        testWeightSum1();
        testWeightSum2();
    }

    private static void testWeightSum1() {
        System.out.println("=== Testing Nested List Weight Sum I ===");
        NestedListWeightSum1 solution = new NestedListWeightSum1();

        // Test 1: [[1,1],2,[1,1]]
        // Expected: 10 (Four 1's at depth 2, one 2 at depth 1)
        List<NestedInteger> test1 = new ArrayList<>();
        List<NestedInteger> inner1 = new ArrayList<>();
        inner1.add(new NestedIntegerImpl(1));
        inner1.add(new NestedIntegerImpl(1));
        test1.add(new NestedIntegerImpl(inner1));
        test1.add(new NestedIntegerImpl(2));
        List<NestedInteger> inner2 = new ArrayList<>();
        inner2.add(new NestedIntegerImpl(1));
        inner2.add(new NestedIntegerImpl(1));
        test1.add(new NestedIntegerImpl(inner2));

        int result1DFS = solution.depthSum(test1);
        int result1BFS = solution.depthSumBFS(test1);
        System.out.println("Test 1 - [[1,1],2,[1,1]]");
        System.out.println("  DFS Result: " + result1DFS + " (Expected: 10) - " + (result1DFS == 10 ? "PASS" : "FAIL"));
        System.out.println("  BFS Result: " + result1BFS + " (Expected: 10) - " + (result1BFS == 10 ? "PASS" : "FAIL"));

        // Test 2: [1,[4,[6]]]
        // Expected: 27 (1 at depth 1, 4 at depth 2, 6 at depth 3)
        List<NestedInteger> test2 = new ArrayList<>();
        test2.add(new NestedIntegerImpl(1));
        List<NestedInteger> inner3 = new ArrayList<>();
        inner3.add(new NestedIntegerImpl(4));
        List<NestedInteger> inner4 = new ArrayList<>();
        inner4.add(new NestedIntegerImpl(6));
        inner3.add(new NestedIntegerImpl(inner4));
        test2.add(new NestedIntegerImpl(inner3));

        int result2DFS = solution.depthSum(test2);
        int result2BFS = solution.depthSumBFS(test2);
        System.out.println("Test 2 - [1,[4,[6]]]");
        System.out.println("  DFS Result: " + result2DFS + " (Expected: 27) - " + (result2DFS == 27 ? "PASS" : "FAIL"));
        System.out.println("  BFS Result: " + result2BFS + " (Expected: 27) - " + (result2BFS == 27 ? "PASS" : "FAIL"));

        // Test 3: Empty list
        List<NestedInteger> test3 = new ArrayList<>();
        int result3DFS = solution.depthSum(test3);
        int result3BFS = solution.depthSumBFS(test3);
        System.out.println("Test 3 - []");
        System.out.println("  DFS Result: " + result3DFS + " (Expected: 0) - " + (result3DFS == 0 ? "PASS" : "FAIL"));
        System.out.println("  BFS Result: " + result3BFS + " (Expected: 0) - " + (result3BFS == 0 ? "PASS" : "FAIL"));

        System.out.println();
    }

    private static void testWeightSum2() {
        System.out.println("=== Testing Nested List Weight Sum II ===");
        NestedListWeightSum2 solution = new NestedListWeightSum2();

        // Test 1: [[1,1],2,[1,1]]
        // Expected: 8 (Four 1's with weight 1, one 2 with weight 2)
        List<NestedInteger> test1 = new ArrayList<>();
        List<NestedInteger> inner1 = new ArrayList<>();
        inner1.add(new NestedIntegerImpl(1));
        inner1.add(new NestedIntegerImpl(1));
        test1.add(new NestedIntegerImpl(inner1));
        test1.add(new NestedIntegerImpl(2));
        List<NestedInteger> inner2 = new ArrayList<>();
        inner2.add(new NestedIntegerImpl(1));
        inner2.add(new NestedIntegerImpl(1));
        test1.add(new NestedIntegerImpl(inner2));

        int result1TwoPass = solution.depthSumInverse(test1);
        int result1BFS = solution.depthSumInverseBFS(test1);
        System.out.println("Test 1 - [[1,1],2,[1,1]]");
        System.out.println("  Two-Pass Result: " + result1TwoPass + " (Expected: 8) - " + (result1TwoPass == 8 ? "PASS" : "FAIL"));
        System.out.println("  BFS Result: " + result1BFS + " (Expected: 8) - " + (result1BFS == 8 ? "PASS" : "FAIL"));

        // Test 2: [1,[4,[6]]]
        // Expected: 17 (1 at weight 3, 4 at weight 2, 6 at weight 1)
        List<NestedInteger> test2 = new ArrayList<>();
        test2.add(new NestedIntegerImpl(1));
        List<NestedInteger> inner3 = new ArrayList<>();
        inner3.add(new NestedIntegerImpl(4));
        List<NestedInteger> inner4 = new ArrayList<>();
        inner4.add(new NestedIntegerImpl(6));
        inner3.add(new NestedIntegerImpl(inner4));
        test2.add(new NestedIntegerImpl(inner3));

        int result2TwoPass = solution.depthSumInverse(test2);
        int result2BFS = solution.depthSumInverseBFS(test2);
        System.out.println("Test 2 - [1,[4,[6]]]");
        System.out.println("  Two-Pass Result: " + result2TwoPass + " (Expected: 17) - " + (result2TwoPass == 17 ? "PASS" : "FAIL"));
        System.out.println("  BFS Result: " + result2BFS + " (Expected: 17) - " + (result2BFS == 17 ? "PASS" : "FAIL"));

        // Test 3: Empty list
        List<NestedInteger> test3 = new ArrayList<>();
        int result3TwoPass = solution.depthSumInverse(test3);
        int result3BFS = solution.depthSumInverseBFS(test3);
        System.out.println("Test 3 - []");
        System.out.println("  Two-Pass Result: " + result3TwoPass + " (Expected: 0) - " + (result3TwoPass == 0 ? "PASS" : "FAIL"));
        System.out.println("  BFS Result: " + result3BFS + " (Expected: 0) - " + (result3BFS == 0 ? "PASS" : "FAIL"));

        System.out.println();
    }
}
