import java.util.*;

/**
 * LeetCode 843: Guess the Word
 *
 * You are given an array of unique strings words where words[i] is six letters long.
 * One word of words was chosen as a secret word.
 *
 * You are also given the helper object Master. You may call Master.guess(word) where word
 * is a six-letter-long string, and it must be from words. Master.guess(word) returns:
 * - -1 if word is not from words, or
 * - an integer representing the number of exact matches (value and position) of your guess
 *   to the secret word.
 *
 * There is a parameter allowedGuesses for each test case where allowedGuesses is the maximum
 * number of times you can call Master.guess(word).
 *
 * For each test case, you should call Master.guess with the secret word without exceeding
 * the maximum number of allowed guesses. You will get:
 * - "Either you took too many guesses, or you did not find the secret word." if you called
 *   Master.guess more than allowedGuesses times or if you did not call Master.guess with the
 *   secret word, or
 * - "You guessed the secret word correctly." if you called Master.guess with the secret word
 *   with the number of calls to Master.guess less than or equal to allowedGuesses.
 *
 * The test cases are generated such that you can guess the secret word with a reasonable strategy
 * (other than using the bruteforce method).
 *
 * Example 1:
 * Input: secret = "acckzz", words = ["acckzz","ccbazz","eiowzz","abcczz"], allowedGuesses = 10
 * Output: You guessed the secret word correctly.
 * Explanation:
 * master.guess("aaaaaa") returns -1, because "aaaaaa" is not in wordlist.
 * master.guess("acckzz") returns 6, because "acckzz" is secret and has all 6 matches.
 *
 * Example 2:
 * Input: secret = "hamada", words = ["hamada","khaled"], allowedGuesses = 10
 * Output: You guessed the secret word correctly.
 * Explanation: There are 2 words in words, you can guess both.
 *
 * Constraints:
 * - 1 <= words.length <= 100
 * - words[i].length == 6
 * - words[i] consist of lowercase English letters.
 * - All the strings of words are unique.
 * - secret exists in words.
 * - 10 <= allowedGuesses <= 30
 */

/**
 * This is the Master's API interface.
 */
interface Master {
    public int guess(String word);
}

class GuessTheWord {
    /**
     * Approach 1: Minimax - Choose word that minimizes maximum group size
     *
     * Strategy:
     * 1. For each candidate word, calculate how it would partition the remaining words
     * 2. Choose the word that minimizes the worst-case (largest) partition
     * 3. After guessing, eliminate words that don't match the returned count
     *
     * Time: O(N^2 * L) per guess where N = words.length, L = word length
     * Space: O(N)
     */
    public void findSecretWord(String[] words, Master master) {
        List<String> candidates = new ArrayList<>(Arrays.asList(words));

        for (int i = 0; i < 10; i++) {
            // Choose the best word to guess using minimax
            String guess = getBestGuess(candidates);

            int matches = master.guess(guess);

            if (matches == 6) {
                return; // Found the secret word
            }

            // Filter candidates: keep only words with same match count
            List<String> newCandidates = new ArrayList<>();
            for (String word : candidates) {
                if (countMatches(guess, word) == matches) {
                    newCandidates.add(word);
                }
            }
            candidates = newCandidates;
        }
    }

    private String getBestGuess(List<String> candidates) {
        String bestGuess = candidates.get(0);
        int minMaxGroupSize = Integer.MAX_VALUE;

        for (String candidate : candidates) {
            // For this candidate, calculate partition sizes
            int[] groups = new int[7]; // matches can be 0-6

            for (String word : candidates) {
                if (!word.equals(candidate)) {
                    int matches = countMatches(candidate, word);
                    groups[matches]++;
                }
            }

            // Find max group size (worst case)
            int maxGroupSize = 0;
            for (int size : groups) {
                maxGroupSize = Math.max(maxGroupSize, size);
            }

            // Choose word with smallest worst-case
            if (maxGroupSize < minMaxGroupSize) {
                minMaxGroupSize = maxGroupSize;
                bestGuess = candidate;
            }
        }

        return bestGuess;
    }

    private int countMatches(String word1, String word2) {
        int matches = 0;
        for (int i = 0; i < word1.length(); i++) {
            if (word1.charAt(i) == word2.charAt(i)) {
                matches++;
            }
        }
        return matches;
    }

    /**
     * Approach 2: Random with Filtering (Simpler but less optimal)
     *
     * Just pick a random word, then filter based on the result.
     *
     * Time: O(N * L) per guess
     * Space: O(N)
     */
    public void findSecretWordRandom(String[] words, Master master) {
        List<String> candidates = new ArrayList<>(Arrays.asList(words));
        Random rand = new Random();

        for (int i = 0; i < 10; i++) {
            String guess = candidates.get(rand.nextInt(candidates.size()));
            int matches = master.guess(guess);

            if (matches == 6) {
                return;
            }

            List<String> newCandidates = new ArrayList<>();
            for (String word : candidates) {
                if (countMatches(guess, word) == matches) {
                    newCandidates.add(word);
                }
            }
            candidates = newCandidates;
        }
    }

    /**
     * Approach 3: Choose word with minimum zero-match words
     *
     * Pick the word that has the fewest words with 0 matches.
     * This avoids guesses that don't eliminate many candidates.
     *
     * Time: O(N^2 * L) per guess
     * Space: O(N)
     */
    public void findSecretWordMinZero(String[] words, Master master) {
        List<String> candidates = new ArrayList<>(Arrays.asList(words));

        for (int i = 0; i < 10; i++) {
            String guess = getMinZeroMatchWord(candidates);
            int matches = master.guess(guess);

            if (matches == 6) {
                return;
            }

            List<String> newCandidates = new ArrayList<>();
            for (String word : candidates) {
                if (countMatches(guess, word) == matches) {
                    newCandidates.add(word);
                }
            }
            candidates = newCandidates;
        }
    }

    private String getMinZeroMatchWord(List<String> candidates) {
        String bestWord = candidates.get(0);
        int minZeroCount = Integer.MAX_VALUE;

        for (String candidate : candidates) {
            int zeroCount = 0;
            for (String word : candidates) {
                if (countMatches(candidate, word) == 0) {
                    zeroCount++;
                }
            }

            if (zeroCount < minZeroCount) {
                minZeroCount = zeroCount;
                bestWord = candidate;
            }
        }

        return bestWord;
    }

    /**
     * Approach 4: Frequency-based selection
     *
     * Choose words with common character patterns.
     *
     * Time: O(N^2 * L) per guess
     * Space: O(N)
     */
    public void findSecretWordFrequency(String[] words, Master master) {
        List<String> candidates = new ArrayList<>(Arrays.asList(words));

        for (int i = 0; i < 10; i++) {
            String guess = getMostCommonWord(candidates);
            int matches = master.guess(guess);

            if (matches == 6) {
                return;
            }

            List<String> newCandidates = new ArrayList<>();
            for (String word : candidates) {
                if (countMatches(guess, word) == matches) {
                    newCandidates.add(word);
                }
            }
            candidates = newCandidates;
        }
    }

    private String getMostCommonWord(List<String> candidates) {
        // Calculate character frequency at each position
        int[][] freq = new int[6][26];

        for (String word : candidates) {
            for (int i = 0; i < 6; i++) {
                freq[i][word.charAt(i) - 'a']++;
            }
        }

        // Find word with highest total frequency score
        String bestWord = candidates.get(0);
        int maxScore = 0;

        for (String word : candidates) {
            int score = 0;
            for (int i = 0; i < 6; i++) {
                score += freq[i][word.charAt(i) - 'a'];
            }

            if (score > maxScore) {
                maxScore = score;
                bestWord = word;
            }
        }

        return bestWord;
    }
}

/**
 * Mock implementation of Master for testing
 */
class MasterImpl implements Master {
    private String secret;
    private int guessCount;
    private int maxGuesses;

    public MasterImpl(String secret, int maxGuesses) {
        this.secret = secret;
        this.guessCount = 0;
        this.maxGuesses = maxGuesses;
    }

    @Override
    public int guess(String word) {
        guessCount++;
        if (guessCount > maxGuesses) {
            return -1;
        }

        int matches = 0;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == secret.charAt(i)) {
                matches++;
            }
        }
        return matches;
    }

    public int getGuessCount() {
        return guessCount;
    }
}

/**
 * Test cases
 */
class GuessTheWordTest {
    public static void main(String[] args) {
        testBasicCases();
        testDifferentStrategies();
    }

    private static void testBasicCases() {
        System.out.println("=== Testing LeetCode 843: Guess the Word ===\n");

        // Test 1
        String[] words1 = {"acckzz", "ccbazz", "eiowzz", "abcczz"};
        String secret1 = "acckzz";
        testCase(words1, secret1, "Test 1");

        // Test 2
        String[] words2 = {"hamada", "khaled"};
        String secret2 = "hamada";
        testCase(words2, secret2, "Test 2");

        // Test 3: Harder case
        String[] words3 = {"gaxckt", "trlccr", "jxwhkz", "ycbfps", "peayuf",
                          "yiejjw", "ldzccp", "nqsjoa", "qrjasy", "pcldos",
                          "acrtag", "buyeia", "ubmtpj", "drtclz", "zqderp",
                          "snywek", "caoztp", "ibpghw", "evtkhl", "bhpfla",
                          "ymqhxk", "qkvipb", "tvmued", "rvbass", "axeasm",
                          "qolsjg", "roswcb", "vdjgxx", "bugqkr", "vpxbes",
                          "ixnmcr", "iwsfhy", "jhausl", "fyteng", "ykfkhr"};
        String secret3 = "hbaczn";
        testCase(words3, secret3, "Test 3");
    }

    private static void testCase(String[] words, String secret, String testName) {
        GuessTheWord solution = new GuessTheWord();
        MasterImpl master = new MasterImpl(secret, 10);

        System.out.println(testName + ": secret = \"" + secret + "\", words.length = " + words.length);

        solution.findSecretWord(words, master);

        int guesses = master.getGuessCount();
        System.out.println("  Guesses used: " + guesses + "/10 - " +
                          (guesses <= 10 ? "PASS" : "FAIL"));
        System.out.println();
    }

    private static void testDifferentStrategies() {
        System.out.println("=== Testing Different Strategies ===\n");

        String[] words = {"gaxckt", "trlccr", "jxwhkz", "ycbfps", "peayuf",
                         "yiejjw", "ldzccp", "nqsjoa", "qrjasy", "pcldos"};
        String secret = "gaxckt";

        // Strategy 1: Minimax
        GuessTheWord solution1 = new GuessTheWord();
        MasterImpl master1 = new MasterImpl(secret, 10);
        solution1.findSecretWord(words, master1);
        System.out.println("Strategy 1 (Minimax): " + master1.getGuessCount() + " guesses");

        // Strategy 2: Random
        GuessTheWord solution2 = new GuessTheWord();
        MasterImpl master2 = new MasterImpl(secret, 10);
        solution2.findSecretWordRandom(words, master2);
        System.out.println("Strategy 2 (Random): " + master2.getGuessCount() + " guesses");

        // Strategy 3: Min Zero
        GuessTheWord solution3 = new GuessTheWord();
        MasterImpl master3 = new MasterImpl(secret, 10);
        solution3.findSecretWordMinZero(words, master3);
        System.out.println("Strategy 3 (Min Zero): " + master3.getGuessCount() + " guesses");

        // Strategy 4: Frequency
        GuessTheWord solution4 = new GuessTheWord();
        MasterImpl master4 = new MasterImpl(secret, 10);
        solution4.findSecretWordFrequency(words, master4);
        System.out.println("Strategy 4 (Frequency): " + master4.getGuessCount() + " guesses");

        System.out.println();
    }
}
