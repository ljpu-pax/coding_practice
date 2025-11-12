package robinhood;

import java.util.*;

public class CountConsistentStrings {
    public int countConsistentStrings(String allowed, String[] words) {
        Set<Character> allowedSet = new HashSet<>();
        for (char c : allowed.toCharArray()) {
            allowedSet.add(c);
        }

        int count = 0;
        for (String word : words) {
            boolean consistent = true;
            for (char c : word.toCharArray()) {
                if (!allowedSet.contains(c)) {
                    consistent = false;
                    break;
                }
            }
            if (consistent) count++;
        }

        return count;
    }

    public static void main(String[] args) {
        String allowed = "ab";
        String[] words = {"ad","bd","aaab","baa","badab"};
        CountConsistentStrings s = new CountConsistentStrings();
        System.out.println(s.countConsistentStrings(allowed, words)); // Output: 2
    }
}

