import java.util.*;

/**
 * LeetCode 127. Word Ladder
 *
 * Problem:
 * Given two words, beginWord and endWord, and a dictionary wordList,
 * return the length of the shortest transformation sequence from beginWord to endWord.
 *
 * Rules:
 * - Only one letter can be changed at a time
 * - Each transformed word must exist in the wordList
 * - beginWord is not in wordList
 *
 * Return 0 if no transformation sequence exists.
 *
 * Example 1:
 * Input: beginWord = "hit", endWord = "cog",
 *        wordList = ["hot","dot","dog","lot","log","cog"]
 * Output: 5
 * Explanation: "hit" -> "hot" -> "dot" -> "dog" -> "cog"
 *
 * Example 2:
 * Input: beginWord = "hit", endWord = "cog",
 *        wordList = ["hot","dot","dog","lot","log"]
 * Output: 0
 * Explanation: endWord "cog" is not in wordList
 *
 * Constraints:
 * - 1 <= beginWord.length <= 10
 * - endWord.length == beginWord.length
 * - 1 <= wordList.length <= 5000
 * - wordList[i].length == beginWord.length
 * - All strings consist of lowercase English letters
 * - beginWord != endWord
 *
 * Time Complexity: O(N * L^2) where N = wordList size, L = word length
 * Space Complexity: O(N * L^2)
 */

class WordLadder {

    /**
     * Solution 1: BFS (Optimal)
     * Time: O(N * L^2)
     * Space: O(N * L^2)
     *
     * Key Insight:
     * - This is a shortest path problem → Use BFS
     * - Each word is a node, edges exist between words differing by 1 letter
     * - BFS guarantees shortest path in unweighted graph
     */
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Set<String> wordSet = new HashSet<>(wordList);

        // If endWord not in dictionary, no solution
        if (!wordSet.contains(endWord)) {
            return 0;
        }

        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.offer(beginWord);
        visited.add(beginWord);

        int level = 1;

        while (!queue.isEmpty()) {
            int size = queue.size();

            // Process all words at current level
            for (int i = 0; i < size; i++) {
                String word = queue.poll();

                // Found the target
                if (word.equals(endWord)) {
                    return level;
                }

                // Try all possible one-letter transformations
                List<String> neighbors = getNeighbors(word, wordSet);
                for (String neighbor : neighbors) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.offer(neighbor);
                    }
                }
            }

            level++;
        }

        return 0; // No transformation found
    }

    /**
     * Get all valid neighbors (words differing by 1 letter)
     * Time: O(L^2) where L = word length (26 * L for loop, L for substring)
     */
    private List<String> getNeighbors(String word, Set<String> wordSet) {
        List<String> neighbors = new ArrayList<>();
        char[] chars = word.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char original = chars[i];

            // Try all 26 letters
            for (char c = 'a'; c <= 'z'; c++) {
                if (c == original) continue;

                chars[i] = c;
                String newWord = new String(chars);

                if (wordSet.contains(newWord)) {
                    neighbors.add(newWord);
                }
            }

            chars[i] = original; // Restore
        }

        return neighbors;
    }

    /**
     * Solution 2: Bidirectional BFS (Faster)
     * Time: O(N * L^2)
     * Space: O(N * L^2)
     *
     * Key Optimization:
     * - Search from both beginWord and endWord simultaneously
     * - Meet in the middle reduces search space
     * - Especially effective when branching factor is large
     */
    public int ladderLengthBidirectional(String beginWord, String endWord, List<String> wordList) {
        Set<String> wordSet = new HashSet<>(wordList);

        if (!wordSet.contains(endWord)) {
            return 0;
        }

        // Two sets: search from both ends
        Set<String> beginSet = new HashSet<>();
        Set<String> endSet = new HashSet<>();
        Set<String> visited = new HashSet<>();

        beginSet.add(beginWord);
        endSet.add(endWord);

        int level = 1;

        while (!beginSet.isEmpty() && !endSet.isEmpty()) {
            // Always expand the smaller set (optimization)
            if (beginSet.size() > endSet.size()) {
                Set<String> temp = beginSet;
                beginSet = endSet;
                endSet = temp;
            }

            Set<String> nextLevel = new HashSet<>();

            for (String word : beginSet) {
                char[] chars = word.toCharArray();

                for (int i = 0; i < chars.length; i++) {
                    char original = chars[i];

                    for (char c = 'a'; c <= 'z'; c++) {
                        chars[i] = c;
                        String newWord = new String(chars);

                        // Found connection between two searches
                        if (endSet.contains(newWord)) {
                            return level + 1;
                        }

                        if (wordSet.contains(newWord) && !visited.contains(newWord)) {
                            nextLevel.add(newWord);
                            visited.add(newWord);
                        }
                    }

                    chars[i] = original;
                }
            }

            beginSet = nextLevel;
            level++;
        }

        return 0;
    }

    /**
     * Solution 3: BFS with Pattern Matching (Alternative)
     * Time: O(N * L^2)
     * Space: O(N * L^2)
     *
     * Use intermediate states like "*ot" to group similar words
     * Can be more efficient when wordList is very large
     */
    public int ladderLengthPattern(String beginWord, String endWord, List<String> wordList) {
        Set<String> wordSet = new HashSet<>(wordList);

        if (!wordSet.contains(endWord)) {
            return 0;
        }

        // Build pattern map: "*ot" -> ["hot", "dot", "lot"]
        Map<String, List<String>> patterns = new HashMap<>();

        wordSet.add(beginWord); // Temporarily add for pattern generation

        for (String word : wordSet) {
            for (int i = 0; i < word.length(); i++) {
                String pattern = word.substring(0, i) + "*" + word.substring(i + 1);
                patterns.putIfAbsent(pattern, new ArrayList<>());
                patterns.get(pattern).add(word);
            }
        }

        wordSet.remove(beginWord); // Remove back

        // BFS
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.offer(beginWord);
        visited.add(beginWord);

        int level = 1;

        while (!queue.isEmpty()) {
            int size = queue.size();

            for (int i = 0; i < size; i++) {
                String word = queue.poll();

                if (word.equals(endWord)) {
                    return level;
                }

                // Get all patterns for this word
                for (int j = 0; j < word.length(); j++) {
                    String pattern = word.substring(0, j) + "*" + word.substring(j + 1);

                    if (patterns.containsKey(pattern)) {
                        for (String neighbor : patterns.get(pattern)) {
                            if (!visited.contains(neighbor)) {
                                visited.add(neighbor);
                                queue.offer(neighbor);
                            }
                        }
                    }
                }
            }

            level++;
        }

        return 0;
    }
}

/**
 * Test Cases
 */
class WordLadderTest {
    public static void main(String[] args) {
        WordLadder solution = new WordLadder();

        // Test 1: Valid transformation
        String begin1 = "hit";
        String end1 = "cog";
        List<String> wordList1 = Arrays.asList("hot","dot","dog","lot","log","cog");

        System.out.println("Test 1: begin=" + begin1 + ", end=" + end1);
        System.out.println("BFS: " + solution.ladderLength(begin1, end1, wordList1));
        System.out.println("Bidirectional BFS: " + solution.ladderLengthBidirectional(begin1, end1, wordList1));
        System.out.println("Pattern BFS: " + solution.ladderLengthPattern(begin1, end1, wordList1));
        System.out.println("Expected: 5\n");

        // Test 2: No transformation (endWord not in list)
        String begin2 = "hit";
        String end2 = "cog";
        List<String> wordList2 = Arrays.asList("hot","dot","dog","lot","log");

        System.out.println("Test 2: begin=" + begin2 + ", end=" + end2);
        System.out.println("BFS: " + solution.ladderLength(begin2, end2, wordList2));
        System.out.println("Expected: 0\n");

        // Test 3: Short path
        String begin3 = "a";
        String end3 = "c";
        List<String> wordList3 = Arrays.asList("a","b","c");

        System.out.println("Test 3: begin=" + begin3 + ", end=" + end3);
        System.out.println("BFS: " + solution.ladderLength(begin3, end3, wordList3));
        System.out.println("Expected: 2\n");

        // Test 4: Longer words
        String begin4 = "red";
        String end4 = "tax";
        List<String> wordList4 = Arrays.asList("ted","tex","red","tax","tad","den","rex","pee");

        System.out.println("Test 4: begin=" + begin4 + ", end=" + end4);
        System.out.println("BFS: " + solution.ladderLength(begin4, end4, wordList4));
        System.out.println("Expected: 4 (red->ted->tad->tax or red->ted->tex->tax)\n");
    }
}

/**
 * Complexity Analysis:
 * ===================
 *
 * Solution 1 (Standard BFS): ⭐ RECOMMENDED
 * - Time: O(N * L^2)
 *   - N words in dictionary
 *   - For each word, generate neighbors: O(L) positions * 26 letters * O(L) string creation
 *   - Total: O(N * 26 * L * L) = O(N * L^2)
 * - Space: O(N * L^2)
 *   - Queue: O(N) words
 *   - Visited: O(N) words
 *   - Each word: O(L)
 * - Pros: Clear, straightforward
 * - Cons: Can be slow for large dictionaries
 *
 * Solution 2 (Bidirectional BFS): ⭐ FASTEST
 * - Time: O(N * L^2) worst case, but much faster in practice
 * - Space: O(N * L^2)
 * - Pros: Significantly faster (search space reduced)
 * - Cons: More complex implementation
 *
 * Solution 3 (Pattern Matching):
 * - Time: O(N * L^2)
 * - Space: O(N * L^2) - extra space for pattern map
 * - Pros: Can be faster when many words share patterns
 * - Cons: More preprocessing overhead
 *
 *
 * Key Insights:
 * ============
 *
 * 1. Graph Representation:
 *    - Each word is a node
 *    - Edge exists if words differ by exactly 1 letter
 *    - This is an implicit graph (don't build edges explicitly)
 *
 * 2. Why BFS?
 *    - Need SHORTEST path
 *    - Unweighted graph → BFS guarantees shortest path
 *    - DFS would find A path, but not necessarily shortest
 *
 * 3. Bidirectional Search Optimization:
 *    - Normal BFS: explore from start, branching factor = b, depth = d
 *      Total nodes: b^d
 *    - Bidirectional: meet in middle at depth d/2
 *      Total nodes: 2 * b^(d/2) << b^d
 *
 * 4. Pattern Matching Trick:
 *    - Group words by patterns: "hot" → ["*ot", "h*t", "ho*"]
 *    - Find neighbors by looking up patterns instead of trying all letters
 *    - Useful when dictionary is very large
 *
 *
 * Interview Tips:
 * ==============
 *
 * 1. Identify Problem Type:
 *    "This is a shortest path problem in an unweighted graph"
 *    "BFS is the natural choice"
 *
 * 2. Clarify Requirements:
 *    - Can beginWord be in wordList? (Usually no)
 *    - Return length including begin/end? (Yes)
 *    - Case sensitive? (Usually lowercase only)
 *    - What if no path exists? (Return 0)
 *
 * 3. Start Simple:
 *    - Implement basic BFS first
 *    - Mention bidirectional optimization as follow-up
 *
 * 4. Walk Through Example:
 *    "hit" -> "hot" -> "dot" -> "dog" -> "cog"
 *    Level 1: "hit"
 *    Level 2: "hot" (change 'i' to 'o')
 *    Level 3: "dot", "lot" (change 'h' to 'd' or 'l')
 *    ...
 *
 * 5. Edge Cases:
 *    - endWord not in wordList → return 0
 *    - beginWord == endWord → return 1 (or 0 based on definition)
 *    - No valid path → return 0
 *    - wordList is empty → return 0
 *
 * 6. Common Mistakes:
 *    - Not checking if endWord is in wordList first
 *    - Forgetting to mark words as visited
 *    - Not handling level/distance correctly
 *    - Confusing path length with number of transformations
 *
 * 7. Follow-up Questions:
 *    - Find all shortest paths? → Modified BFS with parent tracking
 *    - Find all paths? → DFS with backtracking
 *    - Different cost for different letter changes? → Dijkstra
 *    - Add/remove words dynamically? → Need to rebuild pattern map
 *
 * 8. Optimization Notes:
 *    - Bidirectional BFS is 2-10x faster in practice
 *    - Pattern matching helps when L is small and N is large
 *    - Can use HashSet instead of List for wordList (O(1) lookup)
 */
