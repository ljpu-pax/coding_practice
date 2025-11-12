// 🧠 Problem Interpretation:
// You’re given:
// java
// Copy
// Edit
// int[][] metaPattern   // Each position is a set of allowed pattern IDs
// String candidate      // A string like "dog cat cat"
// You must:
// Assign unique integers to each unique word, starting from 1.

// Then match each word's assigned ID to the allowed ID list at that position in metaPattern.

// Return true if the whole candidate matches the pattern constraints.

package vanta;

import java.util.*;

public class MetaPatternMatcher {

    public boolean matchMetaPattern(int[][] metaPattern, String candidate) {
        String[] words = candidate.split(",");
        if (metaPattern.length != words.length) return false;

        // pattern ID → word (must be unique)
        Map<Integer, String> patternToWord = new HashMap<>();

        return backtrack(metaPattern, words, 0, patternToWord);
    }

    private boolean backtrack(int[][] metaPattern, String[] words, int index,
                              Map<Integer, String> patternToWord) {
        if (index == metaPattern.length) return true;

        String word = words[index].trim();
        for (int p : metaPattern[index]) {
            String existing = patternToWord.get(p);

            if (existing != null && !existing.equals(word)) continue; // number already mapped to a different word

            boolean isNew = !patternToWord.containsKey(p);
            patternToWord.put(p, word);

            if (backtrack(metaPattern, words, index + 1, patternToWord)) return true;

            if (isNew) patternToWord.remove(p); // backtrack only if we added new
        }

        return false;
    }

    // Test cases
    public static void main(String[] args) {
        MetaPatternMatcher matcher = new MetaPatternMatcher();

        System.out.println(matcher.matchMetaPattern(new int[][]{{1}, {1, 2}}, "dog,cat")); // true
        System.out.println(matcher.matchMetaPattern(new int[][]{{1, 2}, {2}, {1, 2}}, "ant,cat,falcon")); // false
        System.out.println(matcher.matchMetaPattern(new int[][]{{1, 2}, {1}, {1, 2}}, "dog,cat,cat")); // true
        System.out.println(matcher.matchMetaPattern(new int[][]{{1}, {1, 2}, {2, 500}}, "cat,dog,cat")); // ✅ true
        System.out.println(matcher.matchMetaPattern(new int[][]{{1}, {1}, {1}}, "a,b,a")); // ❌ false (1 cannot map to both "a" and "b")
        System.out.println(matcher.matchMetaPattern(new int[][]{{1}, {2}, {3}}, "a,a,a")); // ✅ true (each number maps to same word)
    }
}



