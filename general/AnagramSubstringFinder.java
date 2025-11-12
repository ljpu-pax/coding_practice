import java.util.*;

public class AnagramSubstringFinder {
    public static String findAnagramWord(List<String> words, String s) {
        for (String word : words) {
            if (containsAnagram(s, word)) {
                return word;
            }
        }
        return null;
    }

    private static boolean containsAnagram(String s, String word) {
        int wordLen = word.length();
        if (s.length() < wordLen) return false;

        int[] wordFreq = new int[26];
        for (char c : word.toCharArray()) {
            wordFreq[c - 'a']++;
        }

        int[] windowFreq = new int[26];
        for (int i = 0; i < wordLen; i++) {
            windowFreq[s.charAt(i) - 'a']++;
        }

        if (Arrays.equals(wordFreq, windowFreq)) return true;

        for (int i = wordLen; i < s.length(); i++) {
            windowFreq[s.charAt(i) - 'a']++;
            windowFreq[s.charAt(i - wordLen) - 'a']--;
            if (Arrays.equals(wordFreq, windowFreq)) return true;
        }

        return false;
    }

    public static void main(String[] args) {
        List<String> words = Arrays.asList("cat", "baby", "bird", "fruit");
        String str1 = "tacjbcebef";
        String str2 = "bacdrigb";

        System.out.println("Input: " + str1);
        System.out.println("Output: " + findAnagramWord(words, str1)); // Output: "cat"

        System.out.println("Input: " + str2);
        System.out.println("Output: " + findAnagramWord(words, str2)); // Output: "bird"
    }
}

