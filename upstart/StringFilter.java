package upstart;

import java.util.*;

/**
 * Problem 1: Filter list of strings
 * Given a list of strings and a given string, filter the list to include only strings
 * that have at least one letter appearing in the given string.
 */
public class StringFilter {

    public List<String> filterStrings(List<String> strings, String given) {
        if (strings == null || given == null) {
            return new ArrayList<>();
        }

        // Create a set of characters from the given string for O(1) lookup
        Set<Character> givenChars = new HashSet<>();
        for (char c : given.toCharArray()) {
            givenChars.add(c);
        }

        List<String> result = new ArrayList<>();
        for (String str : strings) {
            if (hasCommonChar(str, givenChars)) {
                result.add(str);
            }
        }

        return result;
    }

    private boolean hasCommonChar(String str, Set<Character> givenChars) {
        for (char c : str.toCharArray()) {
            if (givenChars.contains(c)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        StringFilter solution = new StringFilter();

        // Test case 1
        List<String> strings = Arrays.asList("hello", "world", "xyz", "abc");
        String given = "aeiou";
        System.out.println("Input: " + strings + ", Given: " + given);
        System.out.println("Output: " + solution.filterStrings(strings, given));
        // Expected: [hello, world] (they contain e and o)

        // Test case 2
        strings = Arrays.asList("test", "java", "python", "zzz");
        given = "programming";
        System.out.println("\nInput: " + strings + ", Given: " + given);
        System.out.println("Output: " + solution.filterStrings(strings, given));
        // Expected: [java, python] (they contain a, p, r, o, g, m, i, n)
    }
}
