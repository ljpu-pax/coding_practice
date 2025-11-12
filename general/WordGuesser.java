import java.util.*;

public class WordGuesser {
    private final List<String> wordList;
    private final String secret;
    private int attempts = 0;

    public WordGuesser(List<String> wordList, String secret) {
        this.wordList = wordList;
        this.secret = secret;
    }

    // Compares guess to secret and returns a list of booleans indicating character match by position
    public List<Boolean> getFeedback(String guessWord) {
        List<Boolean> feedback = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            feedback.add(guessWord.charAt(i) == secret.charAt(i));
        }
        return feedback;
    }

    // Matches how many positions are equal
    private int matchCount(String a, String b) {
        int count = 0;
        for (int i = 0; i < 6; i++) {
            if (a.charAt(i) == b.charAt(i)) count++;
        }
        return count;
    }

    // Main guessing function
    public String efficientGuessWord() {
        List<String> candidates = new ArrayList<>(wordList);

        while (!candidates.isEmpty()) {
            String guess = selectBestGuess(candidates);
            attempts++;
            List<Boolean> feedback = getFeedback(guess);
            int matchNum = (int) feedback.stream().filter(b -> b).count();

            if (matchNum == 6) {
                System.out.println("✅ Found secret: " + guess + " in " + attempts + " attempts.");
                return guess;
            }

            List<String> nextCandidates = new ArrayList<>();
            for (String word : candidates) {
                if (matchCount(word, guess) == matchNum) {
                    nextCandidates.add(word);
                }
            }
            candidates = nextCandidates;
        }

        System.out.println("❌ Failed to guess the word.");
        return "";
    }

    // Minimax guess selection
    private String selectBestGuess(List<String> candidates) {
        int minMaxGroup = Integer.MAX_VALUE;
        String bestGuess = candidates.get(0);

        for (String candidate : candidates) {
            Map<Integer, Integer> groups = new HashMap<>();
            for (String other : candidates) {
                if (candidate.equals(other)) continue;
                int match = matchCount(candidate, other);
                groups.put(match, groups.getOrDefault(match, 0) + 1);
            }
            int worst = groups.values().stream().max(Integer::compareTo).orElse(0);
            if (worst < minMaxGroup) {
                minMaxGroup = worst;
                bestGuess = candidate;
            }
        }
        return bestGuess;
    }

    // For testing
    public static void main(String[] args) {
        List<String> words = Arrays.asList("coffee", "school", "abcdef", "abcfef", "coffed", "coffea");
        String secret = "coffee";
        WordGuesser guesser = new WordGuesser(words, secret);
        guesser.efficientGuessWord();
    }
}
