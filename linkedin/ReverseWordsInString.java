// LeetCode 151: Reverse Words in a String
// https://leetcode.com/problems/reverse-words-in-a-string/
// Difficulty: Medium

// Given an input string s, reverse the order of the words.

// A word is defined as a sequence of non-space characters.
// The words in s will be separated by at least one space.

// Return a string of the words in reverse order concatenated by a single space.

// Note that s may contain leading or trailing spaces or multiple spaces between two words.
// The returned string should only have a single space separating the words.
// Do not include any extra spaces.

// Example 1:
// Input: s = "the sky is blue"
// Output: "blue is sky the"

// Example 2:
// Input: s = "  hello world  "
// Output: "world hello"
// Explanation: Your reversed string should not contain leading or trailing spaces.

// Example 3:
// Input: s = "a good   example"
// Output: "example good a"
// Explanation: You need to reduce multiple spaces between two words to a single space
// in the reversed string.

class ReverseWordsInString {
    // Approach 1: Using built-in functions (split and join)
    // Time: O(n), Space: O(n)
    public String reverseWords(String s) {
        // Trim and split by one or more spaces
        String[] words = s.trim().split("\\s+");

        // Reverse the array
        int left = 0;
        int right = words.length - 1;

        while (left < right) {
            String temp = words[left];
            words[left] = words[right];
            words[right] = temp;
            left++;
            right--;
        }

        // Join with single space
        return String.join(" ", words);
    }

    // Approach 2: Using StringBuilder (more efficient)
    // Time: O(n), Space: O(n)
    public String reverseWordsStringBuilder(String s) {
        // Trim leading and trailing spaces
        s = s.trim();

        // Split by spaces and build result
        StringBuilder result = new StringBuilder();
        int end = s.length();

        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == ' ') {
                // Found a space, add the word if not empty
                if (i + 1 < end) {
                    if (result.length() > 0) {
                        result.append(" ");
                    }
                    result.append(s.substring(i + 1, end));
                }
                // Skip multiple spaces
                while (i > 0 && s.charAt(i - 1) == ' ') {
                    i--;
                }
                end = i;
            }
        }

        // Add the first word
        if (end > 0) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(s.substring(0, end));
        }

        return result.toString();
    }

    // Approach 3: Using Stack
    // Time: O(n), Space: O(n)
    public String reverseWordsStack(String s) {
        String[] words = s.trim().split("\\s+");
        StringBuilder result = new StringBuilder();

        // Add words in reverse order
        for (int i = words.length - 1; i >= 0; i--) {
            result.append(words[i]);
            if (i > 0) {
                result.append(" ");
            }
        }

        return result.toString();
    }

    // Approach 4: Two-pass approach (manual parsing)
    // Time: O(n), Space: O(n)
    public String reverseWordsManual(String s) {
        StringBuilder result = new StringBuilder();
        int n = s.length();
        int i = n - 1;

        while (i >= 0) {
            // Skip trailing spaces
            while (i >= 0 && s.charAt(i) == ' ') {
                i--;
            }

            if (i < 0) break;

            // Find the end of the word
            int end = i;

            // Find the start of the word
            while (i >= 0 && s.charAt(i) != ' ') {
                i--;
            }

            // Add word to result
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(s.substring(i + 1, end + 1));
        }

        return result.toString();
    }

    // Follow-up: If the string data type is mutable in your language,
    // can you solve it in-place with O(1) extra space?
    // (In Java, strings are immutable, but here's the algorithm)
    public String reverseWordsInPlace(String s) {
        char[] chars = s.toCharArray();

        // Step 1: Reverse the entire string
        reverse(chars, 0, chars.length - 1);

        // Step 2: Reverse each word and clean up spaces
        int n = cleanSpaces(chars);

        // Step 3: Reverse each word back
        int start = 0;
        for (int i = 0; i <= n; i++) {
            if (i == n || chars[i] == ' ') {
                reverse(chars, start, i - 1);
                start = i + 1;
            }
        }

        return new String(chars, 0, n);
    }

    private void reverse(char[] chars, int left, int right) {
        while (left < right) {
            char temp = chars[left];
            chars[left] = chars[right];
            chars[right] = temp;
            left++;
            right--;
        }
    }

    private int cleanSpaces(char[] chars) {
        int n = chars.length;
        int i = 0, j = 0;

        while (j < n) {
            // Skip spaces
            while (j < n && chars[j] == ' ') j++;

            // Copy word
            while (j < n && chars[j] != ' ') {
                chars[i++] = chars[j++];
            }

            // Skip spaces after word
            while (j < n && chars[j] == ' ') j++;

            // Add single space between words
            if (j < n) chars[i++] = ' ';
        }

        return i;
    }

    public static void main(String[] args) {
        ReverseWordsInString solution = new ReverseWordsInString();

        // Test case 1
        System.out.println("Test 1: \"" + solution.reverseWords("the sky is blue") + "\"");
        // "blue is sky the"

        // Test case 2
        System.out.println("Test 2: \"" + solution.reverseWords("  hello world  ") + "\"");
        // "world hello"

        // Test case 3
        System.out.println("Test 3: \"" + solution.reverseWords("a good   example") + "\"");
        // "example good a"

        // Test StringBuilder approach
        System.out.println("\nStringBuilder approach:");
        System.out.println("Test 4: \"" + solution.reverseWordsStringBuilder("the sky is blue") + "\"");

        // Test Stack approach
        System.out.println("\nStack approach:");
        System.out.println("Test 5: \"" + solution.reverseWordsStack("the sky is blue") + "\"");

        // Test Manual approach
        System.out.println("\nManual approach:");
        System.out.println("Test 6: \"" + solution.reverseWordsManual("the sky is blue") + "\"");

        // Test In-place approach
        System.out.println("\nIn-place approach:");
        System.out.println("Test 7: \"" + solution.reverseWordsInPlace("the sky is blue") + "\"");

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Single word: \"" + solution.reverseWords("hello") + "\"");
        System.out.println("Two words: \"" + solution.reverseWords("hello world") + "\"");
        System.out.println("Multiple spaces: \"" + solution.reverseWords("  Bob    Loves  Alice   ") + "\"");
    }
}

/*
 * Key Insights:
 *
 * 1. Split and Reverse approach:
 *    - Use regex \\s+ to split by one or more spaces
 *    - Handles multiple spaces automatically
 *    - Most readable solution
 *
 * 2. StringBuilder approach:
 *    - More efficient than string concatenation
 *    - Manually parse from end to beginning
 *    - Good for avoiding multiple splits
 *
 * 3. In-place approach (for mutable strings):
 *    - Reverse entire string
 *    - Clean up extra spaces
 *    - Reverse each word individually
 *    - Example: "the sky" -> "yks eht" -> "sky the"
 *
 * Time Complexity: O(n) for all approaches
 * Space Complexity: O(n) in Java (strings are immutable)
 *                   O(1) possible in languages with mutable strings
 *
 * Interview Tips:
 * - Clarify if input can be modified
 * - Ask about using built-in functions vs manual parsing
 * - Consider handling edge cases: empty string, single word, multiple spaces
 * - Discuss trade-offs between readability and efficiency
 */
