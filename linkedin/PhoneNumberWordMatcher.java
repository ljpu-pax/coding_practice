import java.util.*;

/**
 * Phone Number Word Matcher
 *
 * Similar to: LeetCode 17. Letter Combinations of a Phone Number (variant)
 *
 * Problem:
 * Given a list of lowercase English words and a digit string phoneNumber,
 * return all words that match the phone number pattern.
 *
 * Keypad mapping:
 * 2 → "abc", 3 → "def", 4 → "ghi", 5 → "jkl",
 * 6 → "mno", 7 → "pqrs", 8 → "tuv", 9 → "wxyz"
 * 0 → space, 1 → no letters (ignore)
 *
 * Example:
 * Input: knownWords = ["aa", "ab", "ba", "qq", "hello", "b"]
 *        phoneNumber = "1221"
 * Output: ["aa", "ab", "ba"]
 *
 * Explanation:
 * - Remove '1's → "22"
 * - '2' maps to 'a', 'b', or 'c'
 * - Words of length 2 that match: "aa", "ab", "ba"
 *
 * Time Complexity: O(N * L) where N = number of words, L = average word length
 * Space Complexity: O(1) if we don't count output
 */

class PhoneNumberWordMatcher {

    // Keypad mapping
    private static final String[] KEYPAD = {
        "",      // 0 - space (we'll handle separately)
        "",      // 1 - no letters
        "abc",   // 2
        "def",   // 3
        "ghi",   // 4
        "jkl",   // 5
        "mno",   // 6
        "pqrs",  // 7
        "tuv",   // 8
        "wxyz"   // 9
    };

    /**
     * Solution 1: Filter and Check Each Word
     *
     * Algorithm:
     * 1. Remove all '1's from phoneNumber (they don't map to letters)
     * 2. For each word in knownWords:
     *    - Check if word length matches cleaned phoneNumber length
     *    - Check if each character in word can be mapped from corresponding digit
     * 3. Return matching words
     */
    public List<String> findMatchingWords(List<String> knownWords, String phoneNumber) {
        List<String> result = new ArrayList<>();

        // Step 1: Remove '1's from phoneNumber
        String cleanedNumber = phoneNumber.replace("1", "");

        // Edge case: if cleaned number is empty
        if (cleanedNumber.isEmpty()) {
            return result;
        }

        // Step 2: Check each word
        for (String word : knownWords) {
            if (matches(word, cleanedNumber)) {
                result.add(word);
            }
        }

        return result;
    }

    /**
     * Check if a word matches the phone number pattern
     */
    private boolean matches(String word, String phoneNumber) {
        // Length must match
        if (word.length() != phoneNumber.length()) {
            return false;
        }

        // Check each character
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            int digit = phoneNumber.charAt(i) - '0';

            // Check if digit is valid (2-9)
            if (digit < 2 || digit > 9) {
                return false;
            }

            // Check if character can be mapped from this digit
            if (KEYPAD[digit].indexOf(c) == -1) {
                return false;
            }
        }

        return true;
    }

    /**
     * Solution 2: Using HashSet for Optimization
     *
     * If we need to query multiple times with different phone numbers
     * but same word list, we can preprocess words into a more efficient structure.
     */
    public List<String> findMatchingWordsOptimized(List<String> knownWords, String phoneNumber) {
        List<String> result = new ArrayList<>();
        String cleanedNumber = phoneNumber.replace("1", "");

        if (cleanedNumber.isEmpty()) {
            return result;
        }

        // Convert words to digit patterns and store mapping
        Map<String, List<String>> patternToWords = new HashMap<>();

        for (String word : knownWords) {
            String pattern = wordToPattern(word);
            patternToWords.putIfAbsent(pattern, new ArrayList<>());
            patternToWords.get(pattern).add(word);
        }

        // Check if cleaned number exists in patterns
        if (patternToWords.containsKey(cleanedNumber)) {
            result.addAll(patternToWords.get(cleanedNumber));
        }

        return result;
    }

    /**
     * Convert a word to its digit pattern
     * Example: "hello" → "43556"
     */
    private String wordToPattern(String word) {
        StringBuilder pattern = new StringBuilder();

        for (char c : word.toCharArray()) {
            int digit = charToDigit(c);
            if (digit == -1) {
                return ""; // Invalid character
            }
            pattern.append(digit);
        }

        return pattern.toString();
    }

    /**
     * Convert a character to its corresponding digit
     */
    private int charToDigit(char c) {
        if (c >= 'a' && c <= 'z') {
            // Map a-z to digits 2-9
            if (c <= 'c') return 2;
            if (c <= 'f') return 3;
            if (c <= 'i') return 4;
            if (c <= 'l') return 5;
            if (c <= 'o') return 6;
            if (c <= 's') return 7;
            if (c <= 'v') return 8;
            if (c <= 'z') return 9;
        }
        return -1; // Invalid character
    }

    /**
     * Solution 3: Trie-based Approach (for very large word lists)
     *
     * Build a Trie of words, then traverse it following the digit pattern
     */
    class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isWord = false;
        String word = null;
    }

    public List<String> findMatchingWordsWithTrie(List<String> knownWords, String phoneNumber) {
        List<String> result = new ArrayList<>();
        String cleanedNumber = phoneNumber.replace("1", "");

        if (cleanedNumber.isEmpty()) {
            return result;
        }

        // Build Trie
        TrieNode root = buildTrie(knownWords);

        // DFS search
        dfs(root, cleanedNumber, 0, result);

        return result;
    }

    private TrieNode buildTrie(List<String> words) {
        TrieNode root = new TrieNode();

        for (String word : words) {
            TrieNode node = root;
            for (char c : word.toCharArray()) {
                node.children.putIfAbsent(c, new TrieNode());
                node = node.children.get(c);
            }
            node.isWord = true;
            node.word = word;
        }

        return root;
    }

    private void dfs(TrieNode node, String phoneNumber, int index, List<String> result) {
        // Base case: reached end of phone number
        if (index == phoneNumber.length()) {
            if (node.isWord) {
                result.add(node.word);
            }
            return;
        }

        // Get possible letters for current digit
        int digit = phoneNumber.charAt(index) - '0';
        if (digit < 2 || digit > 9) {
            return; // Invalid digit
        }

        String letters = KEYPAD[digit];

        // Try each possible letter
        for (char c : letters.toCharArray()) {
            if (node.children.containsKey(c)) {
                dfs(node.children.get(c), phoneNumber, index + 1, result);
            }
        }
    }
}

/**
 * Test Cases
 */
class PhoneNumberWordMatcherTest {
    public static void main(String[] args) {
        PhoneNumberWordMatcher solution = new PhoneNumberWordMatcher();

        // Test case 1: Basic example
        List<String> words1 = Arrays.asList("aa", "ab", "ba", "qq", "hello", "b");
        String phone1 = "1221";

        System.out.println("Test 1 (Solution 1):");
        System.out.println("Input: " + words1 + ", phoneNumber: " + phone1);
        System.out.println("Output: " + solution.findMatchingWords(words1, phone1));
        System.out.println("Expected: [aa, ab, ba]\n");

        System.out.println("Test 1 (Solution 2 - Optimized):");
        System.out.println("Output: " + solution.findMatchingWordsOptimized(words1, phone1));
        System.out.println();

        System.out.println("Test 1 (Solution 3 - Trie):");
        System.out.println("Output: " + solution.findMatchingWordsWithTrie(words1, phone1));
        System.out.println();

        // Test case 2: hello
        List<String> words2 = Arrays.asList("hello", "world", "help", "hell");
        String phone2 = "43556";

        System.out.println("Test 2:");
        System.out.println("Input: " + words2 + ", phoneNumber: " + phone2);
        System.out.println("Output: " + solution.findMatchingWords(words2, phone2));
        System.out.println("Expected: [hello]\n");

        // Test case 3: with 1's
        List<String> words3 = Arrays.asList("abc", "def", "ab", "de");
        String phone3 = "11222";

        System.out.println("Test 3 (with 1's):");
        System.out.println("Input: " + words3 + ", phoneNumber: " + phone3);
        System.out.println("Output: " + solution.findMatchingWords(words3, phone3));
        System.out.println("Expected: [abc]\n");

        // Test case 4: no matches
        List<String> words4 = Arrays.asList("qq", "zz");
        String phone4 = "22";

        System.out.println("Test 4 (no matches):");
        System.out.println("Input: " + words4 + ", phoneNumber: " + phone4);
        System.out.println("Output: " + solution.findMatchingWords(words4, phone4));
        System.out.println("Expected: []\n");
    }
}

/**
 * Follow-up: Dynamic Word Dictionary (Add/Delete Support)
 *
 * Design a data structure that supports:
 * - addWord(word)
 * - deleteWord(word)
 * - findMatchingWords(phoneNumber)
 *
 * Best approach: HashMap + Reference Counting or Trie
 */
class DynamicPhoneWordMatcher {
    private static final String[] KEYPAD = {
        "", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"
    };

    /**
     * Approach 1: HashMap with Pattern Mapping
     *
     * Pattern → List of words
     * Example: "22" → ["aa", "ab", "ba"]
     *
     * Time Complexity:
     * - addWord: O(L)
     * - deleteWord: O(L + M) where M = words with same pattern
     * - findMatchingWords: O(1) to O(M) where M = matching words
     */
    private Map<String, List<String>> patternToWords;

    public DynamicPhoneWordMatcher() {
        patternToWords = new HashMap<>();
    }

    public void addWord(String word) {
        String pattern = wordToPattern(word);
        if (pattern.isEmpty()) {
            return; // Invalid word
        }

        patternToWords.putIfAbsent(pattern, new ArrayList<>());
        // Avoid duplicates
        if (!patternToWords.get(pattern).contains(word)) {
            patternToWords.get(pattern).add(word);
        }
    }

    public void deleteWord(String word) {
        String pattern = wordToPattern(word);
        if (pattern.isEmpty()) {
            return;
        }

        if (patternToWords.containsKey(pattern)) {
            patternToWords.get(pattern).remove(word);
            // Clean up empty lists
            if (patternToWords.get(pattern).isEmpty()) {
                patternToWords.remove(pattern);
            }
        }
    }

    public List<String> findMatchingWords(String phoneNumber) {
        String cleanedNumber = phoneNumber.replace("1", "");

        if (cleanedNumber.isEmpty() || !patternToWords.containsKey(cleanedNumber)) {
            return new ArrayList<>();
        }

        return new ArrayList<>(patternToWords.get(cleanedNumber));
    }

    private String wordToPattern(String word) {
        StringBuilder pattern = new StringBuilder();
        for (char c : word.toCharArray()) {
            int digit = charToDigit(c);
            if (digit == -1) return "";
            pattern.append(digit);
        }
        return pattern.toString();
    }

    private int charToDigit(char c) {
        if (c >= 'a' && c <= 'z') {
            if (c <= 'c') return 2;
            if (c <= 'f') return 3;
            if (c <= 'i') return 4;
            if (c <= 'l') return 5;
            if (c <= 'o') return 6;
            if (c <= 's') return 7;
            if (c <= 'v') return 8;
            if (c <= 'z') return 9;
        }
        return -1;
    }
}

/**
 * Approach 2: Trie with Word Count
 *
 * Support add/delete efficiently with reference counting
 * Better for prefix searches and large datasets
 */
class DynamicPhoneWordMatcherTrie {
    private static final String[] KEYPAD = {
        "", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"
    };

    class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        Map<String, Integer> wordCount = new HashMap<>(); // word -> count
    }

    private TrieNode root;

    public DynamicPhoneWordMatcherTrie() {
        root = new TrieNode();
    }

    /**
     * Add a word to the dictionary
     * Time: O(L) where L = word length
     */
    public void addWord(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }
        // Increment count (allows duplicates tracking)
        node.wordCount.put(word, node.wordCount.getOrDefault(word, 0) + 1);
    }

    /**
     * Delete a word from the dictionary
     * Time: O(L)
     */
    public void deleteWord(String word) {
        deleteHelper(root, word, 0);
    }

    private boolean deleteHelper(TrieNode node, String word, int index) {
        if (index == word.length()) {
            if (!node.wordCount.containsKey(word)) {
                return false; // Word not found
            }

            // Decrement count
            int count = node.wordCount.get(word);
            if (count > 1) {
                node.wordCount.put(word, count - 1);
            } else {
                node.wordCount.remove(word);
            }

            // Return true if node can be deleted (no words and no children)
            return node.wordCount.isEmpty() && node.children.isEmpty();
        }

        char c = word.charAt(index);
        if (!node.children.containsKey(c)) {
            return false; // Word not found
        }

        TrieNode child = node.children.get(c);
        boolean shouldDeleteChild = deleteHelper(child, word, index + 1);

        if (shouldDeleteChild) {
            node.children.remove(c);
            // Return true if this node can also be deleted
            return node.wordCount.isEmpty() && node.children.isEmpty();
        }

        return false;
    }

    /**
     * Find all matching words for phone number
     * Time: O(K^L) where K = avg letters per digit, L = phone length
     */
    public List<String> findMatchingWords(String phoneNumber) {
        List<String> result = new ArrayList<>();
        String cleanedNumber = phoneNumber.replace("1", "");

        if (cleanedNumber.isEmpty()) {
            return result;
        }

        dfs(root, cleanedNumber, 0, result);
        return result;
    }

    private void dfs(TrieNode node, String phoneNumber, int index, List<String> result) {
        if (index == phoneNumber.length()) {
            // Add all words at this node (with their counts)
            for (Map.Entry<String, Integer> entry : node.wordCount.entrySet()) {
                // Add each word 'count' times if duplicates are allowed
                for (int i = 0; i < entry.getValue(); i++) {
                    result.add(entry.getKey());
                }
            }
            return;
        }

        int digit = phoneNumber.charAt(index) - '0';
        if (digit < 2 || digit > 9) {
            return;
        }

        String letters = KEYPAD[digit];
        for (char c : letters.toCharArray()) {
            if (node.children.containsKey(c)) {
                dfs(node.children.get(c), phoneNumber, index + 1, result);
            }
        }
    }
}

/**
 * Test Cases for Dynamic Operations
 */
class DynamicPhoneWordMatcherTest {
    public static void main(String[] args) {
        System.out.println("=== Testing HashMap Approach ===\n");
        testHashMapApproach();

        System.out.println("\n=== Testing Trie Approach ===\n");
        testTrieApproach();
    }

    private static void testHashMapApproach() {
        DynamicPhoneWordMatcher matcher = new DynamicPhoneWordMatcher();

        // Add words
        matcher.addWord("aa");
        matcher.addWord("ab");
        matcher.addWord("ba");
        matcher.addWord("hello");

        System.out.println("After adding aa, ab, ba, hello:");
        System.out.println("Query '22': " + matcher.findMatchingWords("22"));
        System.out.println("Expected: [aa, ab, ba]\n");

        System.out.println("Query '43556': " + matcher.findMatchingWords("43556"));
        System.out.println("Expected: [hello]\n");

        // Delete a word
        matcher.deleteWord("ab");
        System.out.println("After deleting 'ab':");
        System.out.println("Query '22': " + matcher.findMatchingWords("22"));
        System.out.println("Expected: [aa, ba]\n");

        // Add more words
        matcher.addWord("ac");
        System.out.println("After adding 'ac':");
        System.out.println("Query '22': " + matcher.findMatchingWords("22"));
        System.out.println("Expected: [aa, ba, ac]\n");
    }

    private static void testTrieApproach() {
        DynamicPhoneWordMatcherTrie matcher = new DynamicPhoneWordMatcherTrie();

        // Add words
        matcher.addWord("aa");
        matcher.addWord("ab");
        matcher.addWord("ba");
        matcher.addWord("hello");

        System.out.println("After adding aa, ab, ba, hello:");
        System.out.println("Query '22': " + matcher.findMatchingWords("22"));
        System.out.println("Expected: [aa, ab, ba]\n");

        System.out.println("Query '43556': " + matcher.findMatchingWords("43556"));
        System.out.println("Expected: [hello]\n");

        // Delete a word
        matcher.deleteWord("ab");
        System.out.println("After deleting 'ab':");
        System.out.println("Query '22': " + matcher.findMatchingWords("22"));
        System.out.println("Expected: [aa, ba]\n");

        // Add duplicate
        matcher.addWord("aa");
        System.out.println("After adding duplicate 'aa':");
        System.out.println("Query '22': " + matcher.findMatchingWords("22"));
        System.out.println("Note: Trie approach tracks duplicates\n");
    }
}

/**
 * Complexity Analysis:
 * ===================
 *
 * Solution 1 (Simple Filter):
 * - Time: O(N * L) where N = words count, L = average word length
 * - Space: O(1) excluding output
 * - Best for: One-time queries, small word lists
 *
 * Solution 2 (HashMap Preprocessing):
 * - Time: O(N * L) preprocessing + O(1) query
 * - Space: O(N * L) for pattern map
 * - Best for: Multiple queries with same word list
 *
 * Solution 3 (Trie):
 * - Time: O(N * L) build + O(L * K^L) search where K = avg letters per digit
 * - Space: O(N * L) for Trie
 * - Best for: Very large word lists, prefix-based pruning
 *
 * Dynamic HashMap:
 * - addWord: O(L)
 * - deleteWord: O(L + M) where M = words with same pattern
 * - findMatchingWords: O(M) where M = matching words
 * - Space: O(N * L)
 * - Best for: Moderate dynamic operations, fast queries
 *
 * Dynamic Trie:
 * - addWord: O(L)
 * - deleteWord: O(L)
 * - findMatchingWords: O(K^L * M) where M = matching words
 * - Space: O(N * L)
 * - Best for: Frequent add/delete, prefix searches, duplicate tracking
 *
 * Interview Tips:
 * ==============
 *
 * 1. Clarify Requirements:
 *    - Should we handle '0' (space)?
 *    - Are all inputs lowercase?
 *    - Can phoneNumber be empty?
 *    - Do we need to support duplicates?
 *
 * 2. Edge Cases:
 *    - phoneNumber contains only '1's → empty after cleaning
 *    - Empty word list → return empty
 *    - phoneNumber contains invalid digits
 *    - Delete non-existent word
 *    - Add duplicate words
 *
 * 3. Follow-up Questions:
 *    - What if we need to query many times? → Use Solution 2 (HashMap)
 *    - What if word list is huge? → Use Solution 3 (Trie)
 *    - What if we need dynamic add/delete? → Use Dynamic approaches above
 *    - What if we want to find all possible words (not just from known list)?
 *      → Generate all combinations (similar to LeetCode 17)
 *
 * 4. Trade-offs:
 *    HashMap Approach:
 *    + Faster queries O(1)
 *    + Simpler implementation
 *    - Slower delete for patterns with many words
 *    - No duplicate tracking (without modification)
 *
 *    Trie Approach:
 *    + Faster delete O(L)
 *    + Supports duplicate tracking
 *    + Memory efficient for words with common prefixes
 *    - Slower queries O(K^L)
 *    - More complex implementation
 */
