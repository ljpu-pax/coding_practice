import java.util.*;

/**
 * LeetCode 244: Shortest Word Distance II (Medium)
 *
 * Design a data structure that will be initialized with a string array, and then it should
 * answer queries of the shortest distance between two different strings from the array.
 *
 * Implement the WordDistance class:
 * - WordDistance(String[] wordsDict) initializes the object with the strings array wordsDict.
 * - int shortest(String word1, String word2) returns the shortest distance between word1 and
 *   word2 in the array wordsDict.
 *
 * Example:
 * Input:
 * ["WordDistance", "shortest", "shortest"]
 * [[["practice", "makes", "perfect", "coding", "makes"]], ["coding", "practice"], ["makes", "coding"]]
 * Output:
 * [null, 3, 1]
 *
 * Explanation:
 * WordDistance wordDistance = new WordDistance(["practice", "makes", "perfect", "coding", "makes"]);
 * wordDistance.shortest("coding", "practice"); // return 3
 * wordDistance.shortest("makes", "coding");    // return 1
 *
 * Constraints:
 * - 1 <= wordsDict.length <= 3 * 10^4
 * - 1 <= wordsDict[i].length <= 10
 * - wordsDict[i] consists of lowercase English letters
 * - word1 and word2 are in wordsDict
 * - word1 != word2
 * - At most 5000 calls will be made to shortest
 */

/**
 * Approach 1: HashMap with List of Indices
 *
 * Preprocessing: O(N) to build map
 * Query: O(M + K) where M, K are counts of word1 and word2
 * Space: O(N)
 */
class WordDistance {
    private Map<String, List<Integer>> wordIndices;

    public WordDistance(String[] wordsDict) {
        wordIndices = new HashMap<>();

        // Build map: word -> list of indices
        for (int i = 0; i < wordsDict.length; i++) {
            wordIndices.putIfAbsent(wordsDict[i], new ArrayList<>());
            wordIndices.get(wordsDict[i]).add(i);
        }
    }

    public int shortest(String word1, String word2) {
        List<Integer> indices1 = wordIndices.get(word1);
        List<Integer> indices2 = wordIndices.get(word2);

        int minDist = Integer.MAX_VALUE;
        int i = 0, j = 0;

        // Two pointers on sorted lists
        while (i < indices1.size() && j < indices2.size()) {
            int idx1 = indices1.get(i);
            int idx2 = indices2.get(j);

            minDist = Math.min(minDist, Math.abs(idx1 - idx2));

            // Move pointer of smaller index
            if (idx1 < idx2) {
                i++;
            } else {
                j++;
            }
        }

        return minDist;
    }
}

/**
 * Approach 2: Brute Force (for comparison)
 *
 * Query: O(M * K) - check all pairs
 */
class WordDistanceBruteForce {
    private Map<String, List<Integer>> wordIndices;

    public WordDistanceBruteForce(String[] wordsDict) {
        wordIndices = new HashMap<>();
        for (int i = 0; i < wordsDict.length; i++) {
            wordIndices.putIfAbsent(wordsDict[i], new ArrayList<>());
            wordIndices.get(wordsDict[i]).add(i);
        }
    }

    public int shortest(String word1, String word2) {
        List<Integer> indices1 = wordIndices.get(word1);
        List<Integer> indices2 = wordIndices.get(word2);

        int minDist = Integer.MAX_VALUE;

        // Check all pairs
        for (int idx1 : indices1) {
            for (int idx2 : indices2) {
                minDist = Math.min(minDist, Math.abs(idx1 - idx2));
            }
        }

        return minDist;
    }
}

/**
 * Approach 3: Optimized with early termination
 */
class WordDistanceOptimized {
    private Map<String, List<Integer>> wordIndices;

    public WordDistanceOptimized(String[] wordsDict) {
        wordIndices = new HashMap<>();
        for (int i = 0; i < wordsDict.length; i++) {
            wordIndices.putIfAbsent(wordsDict[i], new ArrayList<>());
            wordIndices.get(wordsDict[i]).add(i);
        }
    }

    public int shortest(String word1, String word2) {
        List<Integer> indices1 = wordIndices.get(word1);
        List<Integer> indices2 = wordIndices.get(word2);

        int minDist = Integer.MAX_VALUE;
        int i = 0, j = 0;

        while (i < indices1.size() && j < indices2.size()) {
            int idx1 = indices1.get(i);
            int idx2 = indices2.get(j);

            minDist = Math.min(minDist, Math.abs(idx1 - idx2));

            // Early termination: if distance is 1, can't be better
            if (minDist == 1) {
                return 1;
            }

            if (idx1 < idx2) {
                i++;
            } else {
                j++;
            }
        }

        return minDist;
    }
}

/**
 * Related: Shortest Word Distance (LeetCode 243)
 */
class ShortestWordDistance {
    /**
     * One-time query, no preprocessing
     * Time: O(N)
     * Space: O(1)
     */
    public int shortestDistance(String[] wordsDict, String word1, String word2) {
        int idx1 = -1, idx2 = -1;
        int minDist = Integer.MAX_VALUE;

        for (int i = 0; i < wordsDict.length; i++) {
            if (wordsDict[i].equals(word1)) {
                idx1 = i;
                if (idx2 != -1) {
                    minDist = Math.min(minDist, idx1 - idx2);
                }
            } else if (wordsDict[i].equals(word2)) {
                idx2 = i;
                if (idx1 != -1) {
                    minDist = Math.min(minDist, idx2 - idx1);
                }
            }
        }

        return minDist;
    }
}

/**
 * Related: Shortest Word Distance III (LeetCode 245)
 */
class ShortestWordDistanceIII {
    /**
     * word1 and word2 may be the same (but different occurrences)
     * Time: O(N)
     * Space: O(1)
     */
    public int shortestWordDistance(String[] wordsDict, String word1, String word2) {
        int idx1 = -1, idx2 = -1;
        int minDist = Integer.MAX_VALUE;

        for (int i = 0; i < wordsDict.length; i++) {
            if (wordsDict[i].equals(word1)) {
                idx1 = i;
                if (idx2 != -1 && (idx1 != idx2 || word1.equals(word2))) {
                    minDist = Math.min(minDist, Math.abs(idx1 - idx2));
                }
            }

            if (wordsDict[i].equals(word2)) {
                idx2 = i;
                if (idx1 != -1 && (idx1 != idx2 || word1.equals(word2))) {
                    minDist = Math.min(minDist, Math.abs(idx1 - idx2));
                }
            }
        }

        return minDist;
    }
}

/**
 * Test cases
 */
class ShortestWordDistanceIITest {
    public static void main(String[] args) {
        testBasicCases();
        testMultipleOccurrences();
        testAllApproaches();
        testRelatedProblems();
    }

    private static void testBasicCases() {
        System.out.println("=== LeetCode 244: Shortest Word Distance II ===\n");

        String[] wordsDict = {"practice", "makes", "perfect", "coding", "makes"};
        WordDistance wd = new WordDistance(wordsDict);

        System.out.println("Words: " + Arrays.toString(wordsDict));
        System.out.println();

        // Test 1
        int result1 = wd.shortest("coding", "practice");
        System.out.println("shortest(\"coding\", \"practice\") = " + result1);
        System.out.println("Expected: 3 (indices 3 and 0)");
        System.out.println();

        // Test 2
        int result2 = wd.shortest("makes", "coding");
        System.out.println("shortest(\"makes\", \"coding\") = " + result2);
        System.out.println("Expected: 1 (indices 4 and 3, or 1 and 3 is closer)");
        System.out.println();
    }

    private static void testMultipleOccurrences() {
        System.out.println("=== Multiple Occurrences ===\n");

        String[] wordsDict = {"a", "b", "a", "b", "a", "b", "a"};
        WordDistance wd = new WordDistance(wordsDict);

        System.out.println("Words: " + Arrays.toString(wordsDict));
        int result = wd.shortest("a", "b");
        System.out.println("shortest(\"a\", \"b\") = " + result);
        System.out.println("Expected: 1 (many adjacent pairs)");
        System.out.println();
    }

    private static void testAllApproaches() {
        System.out.println("=== Comparing Approaches ===\n");

        String[] wordsDict = {"practice", "makes", "perfect", "coding", "makes"};

        WordDistance wd1 = new WordDistance(wordsDict);
        WordDistanceBruteForce wd2 = new WordDistanceBruteForce(wordsDict);
        WordDistanceOptimized wd3 = new WordDistanceOptimized(wordsDict);

        String word1 = "makes";
        String word2 = "coding";

        long start = System.nanoTime();
        int result1 = wd1.shortest(word1, word2);
        long time1 = System.nanoTime() - start;

        start = System.nanoTime();
        int result2 = wd2.shortest(word1, word2);
        long time2 = System.nanoTime() - start;

        start = System.nanoTime();
        int result3 = wd3.shortest(word1, word2);
        long time3 = System.nanoTime() - start;

        System.out.println("Two Pointers: " + result1 + " (time: " + time1 + "ns)");
        System.out.println("Brute Force:  " + result2 + " (time: " + time2 + "ns)");
        System.out.println("Optimized:    " + result3 + " (time: " + time3 + "ns)");
        System.out.println();
    }

    private static void testRelatedProblems() {
        System.out.println("=== Related Problems ===\n");

        String[] wordsDict = {"practice", "makes", "perfect", "coding", "makes"};

        // LeetCode 243: Shortest Word Distance
        ShortestWordDistance swd = new ShortestWordDistance();
        int result243 = swd.shortestDistance(wordsDict, "coding", "practice");
        System.out.println("LeetCode 243 (one-time query):");
        System.out.println("  shortest(\"coding\", \"practice\") = " + result243);
        System.out.println();

        // LeetCode 245: Shortest Word Distance III
        String[] wordsDict2 = {"practice", "makes", "perfect", "coding", "makes"};
        ShortestWordDistanceIII swd3 = new ShortestWordDistanceIII();
        int result245 = swd3.shortestDistance(wordsDict2, "makes", "makes");
        System.out.println("LeetCode 245 (same word allowed):");
        System.out.println("  shortest(\"makes\", \"makes\") = " + result245);
        System.out.println("  (Distance between indices 1 and 4)");
        System.out.println();
    }
}

/**
 * Key Insights
 */
class ShortestWordDistanceInsights {
    /*
     * Problem Comparison:
     * ===================
     *
     * LeetCode 243: Shortest Word Distance
     * - One-time query, no preprocessing
     * - Time: O(N) per query
     * - Space: O(1)
     * - Best when: Single query or few queries
     *
     * LeetCode 244: Shortest Word Distance II (this problem)
     * - Multiple queries, preprocessing allowed
     * - Preprocessing: O(N)
     * - Query: O(M + K) where M, K = word counts
     * - Space: O(N)
     * - Best when: Many queries (>= N/log N queries)
     *
     * LeetCode 245: Shortest Word Distance III
     * - Same as 243 but word1 can equal word2
     * - Need to find distance between different occurrences
     * - Time: O(N)
     * - Space: O(1)
     *
     * Two Pointers Technique:
     * =======================
     * Since indices are stored in sorted order, we can use two pointers:
     *
     * indices1: [1, 4, 7]
     * indices2: [3, 6, 9]
     *           ^     ^
     *           i     j
     *
     * - If indices1[i] < indices2[j]: move i forward
     * - If indices1[i] > indices2[j]: move j forward
     * - Always calculate distance at each step
     *
     * This is O(M + K) instead of O(M * K) for brute force
     *
     * Why Preprocessing Helps:
     * ========================
     * Without preprocessing:
     * - Each query scans entire array: O(N)
     * - K queries: O(K * N)
     *
     * With preprocessing:
     * - Build map once: O(N)
     * - Each query uses two pointers: O(M + K)
     * - K queries: O(N + K * (M + K))
     *
     * Break-even point:
     * K * N vs N + K * (M + K)
     * Roughly when K > sqrt(N)
     *
     * Space-Time Tradeoff:
     * ====================
     * - No preprocessing: O(1) space, O(N) time per query
     * - With preprocessing: O(N) space, O(M + K) time per query
     *
     * Optimization Opportunities:
     * ===========================
     * 1. Early termination when distance = 1
     * 2. Binary search if one list much larger than other
     * 3. Cache common queries
     * 4. Use primitive arrays instead of ArrayList
     *
     * Related Patterns:
     * =================
     * - Two pointers on sorted arrays
     * - Merge two sorted arrays
     * - Find k-th smallest in two sorted arrays
     * - Median of two sorted arrays
     */
}
