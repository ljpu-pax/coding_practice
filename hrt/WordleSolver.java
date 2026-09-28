import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Wordle-style solver (HRT interview problem)
 *
 * Feedback per position:
 *   2 = Correct — right letter, right position
 *   1 = Present — letter in word, wrong position
 *   0 = Absent  — letter not in word
 *
 * Repeated-letter scoring uses a two-pass algorithm:
 *   Pass 1: mark exact matches, remove matched letters from target pool
 *   Pass 2: for remaining positions, consume from pool for Present/Absent
 *
 * Guess selection heuristic:
 *   For each candidate, score = sum of letterFreq[l] for each distinct letter l,
 *   where letterFreq[l] = number of remaining candidates that contain l.
 *   Tie-break: lexicographic order.
 *
 * Complexity:
 *   Time:  O(A * N * L) — A attempts, N candidates, L word length
 *   Space: O(N * L)     — candidate list
 */
public class WordleSolver {

  static class Result {
    final String guess;
    final int attempts;

    Result(String guess, int attempts) {
      this.guess = guess;
      this.attempts = attempts;
    }

    @Override
    public String toString() {
      return "(" + guess + ", " + attempts + ")";
    }
  }

  /** Compute per-position feedback for guess against target. */
  static int[] feedback(String guess, String target) {
    int len = guess.length();
    int[] result = new int[len];
    int[] pool = new int[26]; // unmatched target letter counts
    boolean[] exactMatched = new boolean[len];

    // Pass 1: exact matches
    for (int i = 0; i < len; i++) {
      if (guess.charAt(i) == target.charAt(i)) {
        result[i] = 2;
        exactMatched[i] = true;
      } else {
        pool[target.charAt(i) - 'a']++;
      }
    }

    // Pass 2: present / absent
    for (int i = 0; i < len; i++) {
      if (exactMatched[i]) {
        continue;
      }
      int idx = guess.charAt(i) - 'a';
      if (pool[idx] > 0) {
        result[i] = 1;
        pool[idx]--;
      }
    }

    return result;
  }

  /** Pick next guess: highest letter-frequency score, lex tie-break. */
  static String pickGuess(List<String> candidates) {
    int[] letterFreq = new int[26];
    for (String word : candidates) {
      boolean[] seen = new boolean[26];
      for (char ch : word.toCharArray()) {
        int idx = ch - 'a';
        if (!seen[idx]) {
          letterFreq[idx]++;
          seen[idx] = true;
        }
      }
    }

    String best = null;
    int bestScore = -1;
    for (String word : candidates) {
      boolean[] seen = new boolean[26];
      int score = 0;
      for (char ch : word.toCharArray()) {
        int idx = ch - 'a';
        if (!seen[idx]) {
          score += letterFreq[idx];
          seen[idx] = true;
        }
      }
      if (score > bestScore || (score == bestScore && word.compareTo(best) < 0)) {
        best = word;
        bestScore = score;
      }
    }
    return best;
  }

  /** Keep only candidates that produce the same feedback against guess. */
  static List<String> prune(List<String> candidates, String guess, int[] fb) {
    List<String> remaining = new ArrayList<>();
    for (String candidate : candidates) {
      if (Arrays.equals(feedback(guess, candidate), fb)) {
        remaining.add(candidate);
      }
    }
    return remaining;
  }

  public static Result solve(String[] dictionary, String target, int maxAttempts) {
    if (dictionary == null || dictionary.length == 0) {
      return new Result("", 0);
    }

    List<String> candidates = new ArrayList<>(Arrays.asList(dictionary));
    String lastGuess = "";
    int attempts = 0;

    while (attempts < maxAttempts && !candidates.isEmpty()) {
      String guess = pickGuess(candidates);
      lastGuess = guess;
      attempts++;

      int[] fb = feedback(guess, target);
      if (Arrays.stream(fb).allMatch(v -> v == 2)) {
        return new Result(guess, attempts);
      }

      candidates = prune(candidates, guess, fb);
    }

    return new Result(lastGuess, attempts);
  }

  // --- Tests ---
  public static void main(String[] args) {
    String[] dict1 = {"crane", "slate", "trace", "blink", "ghost", "lucky"};
    System.out.println("Ex1 (expect ghost,2):    " + solve(dict1, "ghost", 6));

    String[] dict2 = {"allee", "apple", "ample", "amble", "eagle", "ladle"};
    System.out.println("Ex2 (expect apple,2):    " + solve(dict2, "apple", 6));

    System.out.println("Empty (expect ,0):       " + solve(new String[]{}, "test", 6));

    System.out.println("Single (expect hello,1): " + solve(new String[]{"hello"}, "hello", 6));

    String[] dict3 = {"aaaaa", "bbbbb", "ccccc", "ddddd", "eeeee"};
    System.out.println("Exhausted (expect <=2):  " + solve(dict3, "eeeee", 2));

    System.out.println("Feedback aabbb/aaccc (expect 2,2,0,0,0): "
        + Arrays.toString(feedback("aabbb", "aaccc")));

    System.out.println("Feedback speed/spell (expect 2,2,2,0,0): "
        + Arrays.toString(feedback("speed", "spell")));
  }
}
