import java.util.*;

/**
 * LeetCode 527: Word Abbreviation (Hard)
 *
 * Given an array of distinct strings words, return the minimal possible abbreviations
 * for every word.
 *
 * The following are the rules for a string abbreviation:
 * 1. The initial abbreviation for each word is: the first character + the number of
 *    characters in between + the last character.
 * 2. If more than one word shares the same abbreviation, then perform the following
 *    operation:
 *    Increase the prefix by 1 for every word in the conflict set until the abbreviation
 *    becomes unique.
 *
 * Example 1:
 * Input: words = ["like","god","internal","me","internet","interval","intension","face","intrusion"]
 * Output: ["li2e","god","internal","me","i6t","interval","inte4n","f2e","intr4n"]
 *
 * Example 2:
 * Input: words = ["aa","aaa"]
 * Output: ["aa","aaa"]
 *
 * Constraints:
 * - 1 <= words.length <= 400
 * - 2 <= words[i].length <= 400
 * - words[i] consists of lowercase English letters
 * - All the strings of words are unique
 */
class WordAbbreviation {

    /**
     * Approach 1: Group by abbreviation and resolve conflicts
     *
     * Time: O(N * L²) where N = number of words, L = average word length
     * Space: O(N * L)
     */
    public List<String> wordsAbbreviation(List<String> words) {
        int n = words.size();
        String[] result = new String[n];
        int[] prefix = new int[n]; // Prefix length for each word

        // Initially, all words use prefix length 1
        for (int i = 0; i < n; i++) {
            prefix[i] = 1;
            result[i] = getAbbr(words.get(i), 1);
        }

        // Group words by their abbreviations
        Map<String, List<Integer>> groups = new HashMap<>();
        for (int i = 0; i < n; i++) {
            groups.putIfAbsent(result[i], new ArrayList<>());
            groups.get(result[i]).add(i);
        }

        // Resolve conflicts
        for (List<Integer> group : groups.values()) {
            // Keep increasing prefix until all abbreviations are unique
            while (group.size() > 1) {
                Map<String, List<Integer>> subgroups = new HashMap<>();

                for (int idx : group) {
                    prefix[idx]++;
                    result[idx] = getAbbr(words.get(idx), prefix[idx]);
                    subgroups.putIfAbsent(result[idx], new ArrayList<>());
                    subgroups.get(result[idx]).add(idx);
                }

                // Update group to only conflicting words
                group = new ArrayList<>();
                for (List<Integer> subgroup : subgroups.values()) {
                    if (subgroup.size() > 1) {
                        group.addAll(subgroup);
                    }
                }
            }
        }

        return Arrays.asList(result);
    }

    /**
     * Get abbreviation with given prefix length
     */
    private String getAbbr(String word, int prefixLen) {
        int n = word.length();

        // If abbreviation would be longer than or equal to word, return word
        if (prefixLen + 2 >= n) {
            return word;
        }

        return word.substring(0, prefixLen) +
               (n - 1 - prefixLen) +
               word.charAt(n - 1);
    }

    /**
     * Approach 2: Trie-based solution for efficient conflict resolution
     *
     * Time: O(N * L)
     * Space: O(N * L)
     */
    public List<String> wordsAbbreviationTrie(List<String> words) {
        int n = words.size();
        String[] result = new String[n];

        // Group words by same length and same last character
        Map<String, List<Integer>> groups = new HashMap<>();
        for (int i = 0; i < n; i++) {
            String key = words.get(i).length() + "#" + words.get(i).charAt(words.get(i).length() - 1);
            groups.putIfAbsent(key, new ArrayList<>());
            groups.get(key).add(i);
        }

        // Build trie for each group and find abbreviations
        for (List<Integer> group : groups.values()) {
            Trie trie = new Trie();

            // Insert all words in group into trie
            for (int idx : group) {
                trie.insert(words.get(idx));
            }

            // Find shortest unique prefix for each word
            for (int idx : group) {
                int prefixLen = trie.findShortestPrefix(words.get(idx));
                result[idx] = getAbbr(words.get(idx), prefixLen);
            }
        }

        return Arrays.asList(result);
    }

    /**
     * Trie data structure for finding shortest unique prefix
     */
    static class Trie {
        TrieNode root = new TrieNode();

        void insert(String word) {
            TrieNode node = root;
            for (char c : word.toCharArray()) {
                node.children.putIfAbsent(c, new TrieNode());
                node = node.children.get(c);
                node.count++;
            }
        }

        int findShortestPrefix(String word) {
            TrieNode node = root;
            int prefixLen = 0;

            for (char c : word.toCharArray()) {
                node = node.children.get(c);
                prefixLen++;

                // If this is the only word with this prefix, we can stop
                if (node.count == 1) {
                    break;
                }
            }

            return prefixLen;
        }
    }

    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        int count = 0; // Number of words passing through this node
    }
}

/**
 * Test cases
 */
class WordAbbreviationTest {
    public static void main(String[] args) {
        testBasicCases();
        testEdgeCases();
        testBothApproaches();
    }

    private static void testBasicCases() {
        System.out.println("=== LeetCode 527: Word Abbreviation ===\n");
        WordAbbreviation solution = new WordAbbreviation();

        // Test 1
        List<String> words1 = Arrays.asList(
            "like", "god", "internal", "me", "internet", "interval", "intension", "face", "intrusion"
        );
        List<String> result1 = solution.wordsAbbreviation(words1);
        System.out.println("Test 1:");
        System.out.println("Input:  " + words1);
        System.out.println("Output: " + result1);
        System.out.println("Expected: [li2e, god, internal, me, i6t, interval, inte4n, f2e, intr4n]");
        System.out.println();

        // Test 2
        List<String> words2 = Arrays.asList("aa", "aaa");
        List<String> result2 = solution.wordsAbbreviation(words2);
        System.out.println("Test 2:");
        System.out.println("Input:  " + words2);
        System.out.println("Output: " + result2);
        System.out.println("Expected: [aa, aaa]");
        System.out.println();
    }

    private static void testEdgeCases() {
        System.out.println("=== Edge Cases ===\n");
        WordAbbreviation solution = new WordAbbreviation();

        // Test: All same prefix
        List<String> words1 = Arrays.asList("abcde", "abcdf", "abcdg");
        System.out.println("Same prefix: " + words1);
        System.out.println("Result: " + solution.wordsAbbreviation(words1));
        System.out.println();

        // Test: All different
        List<String> words2 = Arrays.asList("apple", "banana", "cherry");
        System.out.println("All different: " + words2);
        System.out.println("Result: " + solution.wordsAbbreviation(words2));
        System.out.println();
    }

    private static void testBothApproaches() {
        System.out.println("=== Comparing Approaches ===\n");
        WordAbbreviation solution = new WordAbbreviation();

        List<String> words = Arrays.asList("internal", "internet", "interval", "intension");

        List<String> result1 = solution.wordsAbbreviation(words);
        List<String> result2 = solution.wordsAbbreviationTrie(words);

        System.out.println("Input: " + words);
        System.out.println("Group-based: " + result1);
        System.out.println("Trie-based:  " + result2);
        System.out.println();
    }
}
