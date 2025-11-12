import java.util.*;

/**
 * LeetCode 1048: Longest String Chain
 *
 * You are given an array of words where each word consists of lowercase English letters.
 *
 * wordA is a predecessor of wordB if and only if we can insert exactly one letter anywhere in wordA
 * without changing the order of the other characters to make it equal to wordB.
 *
 * For example, "abc" is a predecessor of "abac", while "cba" is not a predecessor of "bcad".
 *
 * A word chain is a sequence of words [word1, word2, ..., wordk] with k >= 1, where word1 is a
 * predecessor of word2, word2 is a predecessor of word3, and so on. A single word is trivially
 * a word chain with k == 1.
 *
 * Return the length of the longest possible word chain with words chosen from the given list of words.
 *
 * Example 1:
 * Input: words = ["a","b","ba","bca","bda","bdca"]
 * Output: 4
 * Explanation: One of the longest word chains is ["a","ba","bda","bdca"].
 *
 * Example 2:
 * Input: words = ["xbc","pcxbcf","xb","cxbc","pcxbc"]
 * Output: 5
 * Explanation: All the words can be put in a word chain ["xb", "xbc", "cxbc", "pcxbc", "pcxbcf"].
 *
 * Example 3:
 * Input: words = ["abcd","dbqca"]
 * Output: 1
 * Explanation: The trivial word chain ["abcd"] is one of the longest word chains.
 * ["abcd","dbqca"] is not a valid word chain because the ordering of the letters is changed.
 *
 * Constraints:
 * - 1 <= words.length <= 1000
 * - 1 <= words[i].length <= 16
 * - words[i] only consists of lowercase English letters.
 */
class LongestStringChain {
    /**
     * Approach 1: Dynamic Programming with HashMap
     *
     * Key Insight: Sort words by length, then for each word, check all possible predecessors
     * by removing one character at a time.
     *
     * Algorithm:
     * 1. Sort words by length
     * 2. Use a map to store the longest chain ending at each word
     * 3. For each word, try removing each character to find predecessors
     * 4. Update the chain length based on the best predecessor
     *
     * Time: O(n * L^2) where n = number of words, L = max word length
     * Space: O(n)
     */
    public int longestStrChain(String[] words) {
        // Sort words by length
        Arrays.sort(words, (a, b) -> a.length() - b.length());

        // dp[word] = longest chain ending at this word
        Map<String, Integer> dp = new HashMap<>();
        int maxChain = 1;

        for (String word : words) {
            int currentChain = 1;

            // Try removing each character to find predecessors
            for (int i = 0; i < word.length(); i++) {
                String predecessor = word.substring(0, i) + word.substring(i + 1);
                if (dp.containsKey(predecessor)) {
                    currentChain = Math.max(currentChain, dp.get(predecessor) + 1);
                }
            }

            dp.put(word, currentChain);
            maxChain = Math.max(maxChain, currentChain);
        }

        return maxChain;
    }

    /**
     * Approach 2: DFS with Memoization
     *
     * Build the chain using DFS and cache results.
     *
     * Time: O(n * L^2)
     * Space: O(n) for memoization
     */
    public int longestStrChainDFS(String[] words) {
        Set<String> wordSet = new HashSet<>(Arrays.asList(words));
        Map<String, Integer> memo = new HashMap<>();
        int maxChain = 1;

        for (String word : words) {
            maxChain = Math.max(maxChain, dfs(word, wordSet, memo));
        }

        return maxChain;
    }

    private int dfs(String word, Set<String> wordSet, Map<String, Integer> memo) {
        if (memo.containsKey(word)) {
            return memo.get(word);
        }

        int maxLength = 1;

        // Try adding each character at each position to find successors
        for (int i = 0; i <= word.length(); i++) {
            for (char c = 'a'; c <= 'z'; c++) {
                String next = word.substring(0, i) + c + word.substring(i);
                if (wordSet.contains(next)) {
                    maxLength = Math.max(maxLength, 1 + dfs(next, wordSet, memo));
                }
            }
        }

        memo.put(word, maxLength);
        return maxLength;
    }

    /**
     * Approach 3: Bottom-up DP with Word Set
     *
     * Similar to approach 1 but more explicit about the DP state.
     *
     * Time: O(n * L^2)
     * Space: O(n)
     */
    public int longestStrChainBottomUp(String[] words) {
        // Group words by length
        Map<Integer, List<String>> wordsByLength = new HashMap<>();
        int minLen = Integer.MAX_VALUE;
        int maxLen = Integer.MIN_VALUE;

        for (String word : words) {
            int len = word.length();
            wordsByLength.computeIfAbsent(len, k -> new ArrayList<>()).add(word);
            minLen = Math.min(minLen, len);
            maxLen = Math.max(maxLen, len);
        }

        // dp[word] = longest chain ending at this word
        Map<String, Integer> dp = new HashMap<>();
        int maxChain = 1;

        // Initialize words of minimum length
        for (String word : wordsByLength.getOrDefault(minLen, new ArrayList<>())) {
            dp.put(word, 1);
        }

        // Process words by increasing length
        for (int len = minLen + 1; len <= maxLen; len++) {
            for (String word : wordsByLength.getOrDefault(len, new ArrayList<>())) {
                int currentChain = 1;

                // Try removing each character to find predecessors
                for (int i = 0; i < word.length(); i++) {
                    String predecessor = word.substring(0, i) + word.substring(i + 1);
                    if (dp.containsKey(predecessor)) {
                        currentChain = Math.max(currentChain, dp.get(predecessor) + 1);
                    }
                }

                dp.put(word, currentChain);
                maxChain = Math.max(maxChain, currentChain);
            }
        }

        return maxChain;
    }

    /**
     * Approach 4: Graph-based with Topological Sort
     *
     * Build a directed graph where edge u -> v exists if u is predecessor of v.
     * Then find the longest path in the DAG.
     *
     * Time: O(n * L^2 + n)
     * Space: O(n)
     */
    public int longestStrChainGraph(String[] words) {
        Map<String, Integer> wordIndex = new HashMap<>();
        for (int i = 0; i < words.length; i++) {
            wordIndex.put(words[i], i);
        }

        // Build adjacency list
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            graph.add(new ArrayList<>());
        }

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            // Try removing each character to find predecessors
            for (int j = 0; j < word.length(); j++) {
                String predecessor = word.substring(0, j) + word.substring(j + 1);
                if (wordIndex.containsKey(predecessor)) {
                    int predIdx = wordIndex.get(predecessor);
                    graph.get(predIdx).add(i); // edge from predecessor to word
                }
            }
        }

        // Find longest path using DFS
        int[] memo = new int[words.length];
        Arrays.fill(memo, -1);
        int maxChain = 1;

        for (int i = 0; i < words.length; i++) {
            maxChain = Math.max(maxChain, dfsGraph(i, graph, memo));
        }

        return maxChain;
    }

    private int dfsGraph(int node, List<List<Integer>> graph, int[] memo) {
        if (memo[node] != -1) {
            return memo[node];
        }

        int maxLength = 1;
        for (int neighbor : graph.get(node)) {
            maxLength = Math.max(maxLength, 1 + dfsGraph(neighbor, graph, memo));
        }

        memo[node] = maxLength;
        return maxLength;
    }
}

/**
 * Helper class to reconstruct the actual chain
 */
class LongestStringChainWithPath {
    /**
     * Return the actual longest chain, not just its length
     */
    public List<String> getLongestChain(String[] words) {
        Arrays.sort(words, (a, b) -> a.length() - b.length());

        Map<String, Integer> dp = new HashMap<>();
        Map<String, String> parent = new HashMap<>();
        String lastWord = "";
        int maxChain = 0;

        for (String word : words) {
            int currentChain = 1;
            String bestPredecessor = null;

            for (int i = 0; i < word.length(); i++) {
                String predecessor = word.substring(0, i) + word.substring(i + 1);
                if (dp.containsKey(predecessor)) {
                    int chainLength = dp.get(predecessor) + 1;
                    if (chainLength > currentChain) {
                        currentChain = chainLength;
                        bestPredecessor = predecessor;
                    }
                }
            }

            dp.put(word, currentChain);
            if (bestPredecessor != null) {
                parent.put(word, bestPredecessor);
            }

            if (currentChain > maxChain) {
                maxChain = currentChain;
                lastWord = word;
            }
        }

        // Reconstruct the chain
        List<String> chain = new ArrayList<>();
        String curr = lastWord;
        while (curr != null) {
            chain.add(curr);
            curr = parent.get(curr);
        }

        Collections.reverse(chain);
        return chain;
    }
}

/**
 * Test cases
 */
class LongestStringChainTest {
    public static void main(String[] args) {
        testBasicCases();
        testEdgeCases();
        testWithPath();
    }

    private static void testBasicCases() {
        System.out.println("=== Testing LeetCode 1048: Longest String Chain ===\n");
        LongestStringChain solution = new LongestStringChain();

        // Test 1
        String[] words1 = {"a", "b", "ba", "bca", "bda", "bdca"};
        int result1 = solution.longestStrChain(words1);
        int result1DFS = solution.longestStrChainDFS(words1);
        int result1BU = solution.longestStrChainBottomUp(words1);
        int result1Graph = solution.longestStrChainGraph(words1);
        System.out.println("Test 1: words = [\"a\",\"b\",\"ba\",\"bca\",\"bda\",\"bdca\"]");
        System.out.println("  DP HashMap: " + result1 + " (Expected: 4) - " + (result1 == 4 ? "PASS" : "FAIL"));
        System.out.println("  DFS Memo: " + result1DFS + " (Expected: 4) - " + (result1DFS == 4 ? "PASS" : "FAIL"));
        System.out.println("  Bottom-Up: " + result1BU + " (Expected: 4) - " + (result1BU == 4 ? "PASS" : "FAIL"));
        System.out.println("  Graph: " + result1Graph + " (Expected: 4) - " + (result1Graph == 4 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 2
        String[] words2 = {"xbc", "pcxbcf", "xb", "cxbc", "pcxbc"};
        int result2 = solution.longestStrChain(words2);
        int result2DFS = solution.longestStrChainDFS(words2);
        int result2BU = solution.longestStrChainBottomUp(words2);
        int result2Graph = solution.longestStrChainGraph(words2);
        System.out.println("Test 2: words = [\"xbc\",\"pcxbcf\",\"xb\",\"cxbc\",\"pcxbc\"]");
        System.out.println("  DP HashMap: " + result2 + " (Expected: 5) - " + (result2 == 5 ? "PASS" : "FAIL"));
        System.out.println("  DFS Memo: " + result2DFS + " (Expected: 5) - " + (result2DFS == 5 ? "PASS" : "FAIL"));
        System.out.println("  Bottom-Up: " + result2BU + " (Expected: 5) - " + (result2BU == 5 ? "PASS" : "FAIL"));
        System.out.println("  Graph: " + result2Graph + " (Expected: 5) - " + (result2Graph == 5 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 3
        String[] words3 = {"abcd", "dbqca"};
        int result3 = solution.longestStrChain(words3);
        System.out.println("Test 3: words = [\"abcd\",\"dbqca\"]");
        System.out.println("  Result: " + result3 + " (Expected: 1) - " + (result3 == 1 ? "PASS" : "FAIL"));
        System.out.println();
    }

    private static void testEdgeCases() {
        System.out.println("=== Testing Edge Cases ===\n");
        LongestStringChain solution = new LongestStringChain();

        // Test 1: Single word
        String[] words1 = {"word"};
        int result1 = solution.longestStrChain(words1);
        System.out.println("Test 1: Single word");
        System.out.println("  Result: " + result1 + " (Expected: 1) - " + (result1 == 1 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 2: No chains possible
        String[] words2 = {"abc", "def", "ghi"};
        int result2 = solution.longestStrChain(words2);
        System.out.println("Test 2: No chains possible");
        System.out.println("  Result: " + result2 + " (Expected: 1) - " + (result2 == 1 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 3: Long chain
        String[] words3 = {"a", "ab", "abc", "abcd", "abcde"};
        int result3 = solution.longestStrChain(words3);
        System.out.println("Test 3: Long chain a->ab->abc->abcd->abcde");
        System.out.println("  Result: " + result3 + " (Expected: 5) - " + (result3 == 5 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 4: Multiple chains, different lengths
        String[] words4 = {"a", "ba", "bda", "bdca", "x", "xy", "xyz"};
        int result4 = solution.longestStrChain(words4);
        System.out.println("Test 4: Multiple chains");
        System.out.println("  Result: " + result4 + " (Expected: 4)");
        System.out.println();

        // Test 5: Same length words
        String[] words5 = {"abc", "def", "ghi", "jkl"};
        int result5 = solution.longestStrChain(words5);
        System.out.println("Test 5: All same length");
        System.out.println("  Result: " + result5 + " (Expected: 1) - " + (result5 == 1 ? "PASS" : "FAIL"));
        System.out.println();
    }

    private static void testWithPath() {
        System.out.println("=== Testing Chain Reconstruction ===\n");
        LongestStringChainWithPath solution = new LongestStringChainWithPath();

        // Test 1
        String[] words1 = {"a", "b", "ba", "bca", "bda", "bdca"};
        List<String> chain1 = solution.getLongestChain(words1);
        System.out.println("Test 1: words = [\"a\",\"b\",\"ba\",\"bca\",\"bda\",\"bdca\"]");
        System.out.println("  Chain: " + chain1);
        System.out.println("  Length: " + chain1.size() + " (Expected: 4)");
        System.out.println();

        // Test 2
        String[] words2 = {"xbc", "pcxbcf", "xb", "cxbc", "pcxbc"};
        List<String> chain2 = solution.getLongestChain(words2);
        System.out.println("Test 2: words = [\"xbc\",\"pcxbcf\",\"xb\",\"cxbc\",\"pcxbc\"]");
        System.out.println("  Chain: " + chain2);
        System.out.println("  Length: " + chain2.size() + " (Expected: 5)");
        System.out.println();

        // Test 3
        String[] words3 = {"a", "ab", "abc", "abcd", "abcde"};
        List<String> chain3 = solution.getLongestChain(words3);
        System.out.println("Test 3: Simple chain");
        System.out.println("  Chain: " + chain3);
        System.out.println("  Length: " + chain3.size() + " (Expected: 5)");
        System.out.println();
    }
}
