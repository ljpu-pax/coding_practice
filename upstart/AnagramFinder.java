package upstart;

import java.util.*;

/**
 * Problem 4: Find anagram with matching first/last character
 * Given a string and a list of words, find anagrams of the string.
 * If there are multiple anagrams, return the one whose first and last characters
 * match the original string's first and last characters.
 */
public class AnagramFinder {

    public String findAnagram(String original, List<String> words) {
        if (original == null || words == null || words.isEmpty()) {
            return null;
        }

        String sortedOriginal = sortString(original);
        List<String> anagrams = new ArrayList<>();

        // Find all anagrams
        for (String word : words) {
            if (word.length() == original.length() &&
                !word.equals(original) &&
                sortString(word).equals(sortedOriginal)) {
                anagrams.add(word);
            }
        }

        if (anagrams.isEmpty()) {
            return null;
        }

        if (anagrams.size() == 1) {
            return anagrams.get(0);
        }

        // Multiple anagrams found - return the one with matching first/last chars
        char firstChar = original.charAt(0);
        char lastChar = original.charAt(original.length() - 1);

        for (String anagram : anagrams) {
            if (anagram.charAt(0) == firstChar &&
                anagram.charAt(anagram.length() - 1) == lastChar) {
                return anagram;
            }
        }

        // If no match found, return the first anagram
        return anagrams.get(0);
    }

    private String sortString(String str) {
        char[] chars = str.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

    public static void main(String[] args) {
        AnagramFinder solution = new AnagramFinder();

        // Test case 1: single anagram
        String original = "listen";
        List<String> words = Arrays.asList("enlist", "google", "inlets", "banana");
        System.out.println("Original: \"" + original + "\"");
        System.out.println("Words: " + words);
        System.out.println("Output: \"" + solution.findAnagram(original, words) + "\"");
        // Expected: one of the anagrams (enlist or inlets)

        // Test case 2: multiple anagrams with matching first/last
        original = "silent";
        words = Arrays.asList("listen", "enlist", "tinsel", "inlets");
        System.out.println("\nOriginal: \"" + original + "\"");
        System.out.println("Words: " + words);
        System.out.println("Output: \"" + solution.findAnagram(original, words) + "\"");
        // Expected: if any anagram has 's' as first char and 't' as last char

        // Test case 3: no anagrams
        original = "hello";
        words = Arrays.asList("world", "java", "python");
        System.out.println("\nOriginal: \"" + original + "\"");
        System.out.println("Words: " + words);
        System.out.println("Output: " + solution.findAnagram(original, words));
        // Expected: null

        // Test case 4: anagram with exact first/last match
        original = "arc";
        words = Arrays.asList("car", "acr", "rac");
        System.out.println("\nOriginal: \"" + original + "\"");
        System.out.println("Words: " + words);
        System.out.println("Output: \"" + solution.findAnagram(original, words) + "\"");
        // Expected: "acr" (starts with 'a' and ends with 'c' like "arc")
    }
}
