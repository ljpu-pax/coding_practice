import java.util.*;

/**
 * LeetCode 791: Custom Sort String (Medium)
 *
 * You are given two strings order and s. All the characters of order are unique and were sorted in
 * some custom order previously.
 *
 * Permute the characters of s so that they match the order that order was sorted. More specifically,
 * if a character x occurs before a character y in order, then x should occur before y in the permuted string.
 *
 * Return any permutation of s that satisfies this property.
 *
 * Example 1:
 * Input: order = "cba", s = "abcd"
 * Output: "dcba"
 * Explanation: "c", "b", "a" appear in order, so the order of "a", "b", "c" should be "c", "b", and "a".
 * Since "d" does not appear in order, it can be at any position in the returned string. "dcba", "cdba", "cbda" are also valid outputs.
 *
 * Example 2:
 * Input: order = "bcafg", s = "abcd"
 * Output: "bcad"
 * Explanation: The characters "b", "c", and "a" from order dictate the order for the characters in s.
 * The character "d" in s does not appear in order, so its position is flexible.
 *
 * Constraints:
 * - 1 <= order.length <= 26
 * - 1 <= s.length <= 200
 * - order and s consist of lowercase English letters.
 * - All the characters of order are unique.
 *
 * Follow-up: Linear time O(n + m) solution required
 */
public class CustomSortString {

    /**
     * Approach 1: Counting Sort (Linear Time - O(n + m))
     *
     * Key Insight:
     * - Count frequency of each character in s
     * - Build result by iterating through order and appending characters
     * - Append remaining characters not in order
     *
     * Time Complexity: O(n + m) where n = length of s, m = length of order
     * Space Complexity: O(1) - fixed size 26 for lowercase letters
     *
     * This is the optimal solution for linear time requirement
     */
    public String customSortString(String order, String s) {
        // Step 1: Count frequency of each character in s
        int[] count = new int[26];
        for (char c : s.toCharArray()) {
            count[c - 'a']++;
        }

        StringBuilder result = new StringBuilder();

        // Step 2: Build result following the order
        for (char c : order.toCharArray()) {
            int freq = count[c - 'a'];
            // Append character freq times
            for (int i = 0; i < freq; i++) {
                result.append(c);
            }
            // Mark as processed
            count[c - 'a'] = 0;
        }

        // Step 3: Append remaining characters not in order (in any order)
        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < count[i]; j++) {
                result.append((char) (i + 'a'));
            }
        }

        return result.toString();
    }

    /**
     * Approach 2: Custom Comparator with HashMap (O(n log n))
     *
     * Uses sorting with custom comparator based on order
     * Not linear time, but more intuitive
     */
    public String customSortStringComparator(String order, String s) {
        // Build priority map
        Map<Character, Integer> priority = new HashMap<>();
        for (int i = 0; i < order.length(); i++) {
            priority.put(order.charAt(i), i);
        }

        // Convert to list for sorting
        Character[] chars = new Character[s.length()];
        for (int i = 0; i < s.length(); i++) {
            chars[i] = s.charAt(i);
        }

        // Sort with custom comparator
        Arrays.sort(chars, (a, b) -> {
            int priorityA = priority.getOrDefault(a, Integer.MAX_VALUE);
            int priorityB = priority.getOrDefault(b, Integer.MAX_VALUE);
            return Integer.compare(priorityA, priorityB);
        });

        StringBuilder result = new StringBuilder();
        for (char c : chars) {
            result.append(c);
        }

        return result.toString();
    }

    /**
     * Approach 3: Bucket Sort (Also Linear - O(n + m))
     *
     * Similar to counting sort but uses buckets explicitly
     */
    public String customSortStringBucket(String order, String s) {
        // Count frequencies
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : s.toCharArray()) {
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }

        StringBuilder result = new StringBuilder();

        // Process characters in order
        for (char c : order.toCharArray()) {
            if (freq.containsKey(c)) {
                int count = freq.get(c);
                for (int i = 0; i < count; i++) {
                    result.append(c);
                }
                freq.remove(c);
            }
        }

        // Append remaining characters
        for (Map.Entry<Character, Integer> entry : freq.entrySet()) {
            char c = entry.getKey();
            int count = entry.getValue();
            for (int i = 0; i < count; i++) {
                result.append(c);
            }
        }

        return result.toString();
    }

    /**
     * Approach 4: Using Array as Bucket (Most Efficient Linear)
     *
     * Optimized version using array buckets
     */
    public String customSortStringOptimized(String order, String s) {
        // Create buckets for each character in order, plus one for "others"
        List<Character>[] buckets = new ArrayList[27]; // 26 for order positions + 1 for others
        for (int i = 0; i < 27; i++) {
            buckets[i] = new ArrayList<>();
        }

        // Map characters to their order index
        int[] orderIndex = new int[26];
        Arrays.fill(orderIndex, 26); // Default to last bucket (others)

        for (int i = 0; i < order.length(); i++) {
            orderIndex[order.charAt(i) - 'a'] = i;
        }

        // Place characters into buckets
        for (char c : s.toCharArray()) {
            int index = orderIndex[c - 'a'];
            buckets[index].add(c);
        }

        // Build result from buckets
        StringBuilder result = new StringBuilder();
        for (List<Character> bucket : buckets) {
            for (char c : bucket) {
                result.append(c);
            }
        }

        return result.toString();
    }

    /**
     * Extension: Handle case-sensitive strings
     */
    public String customSortCaseSensitive(String order, String s) {
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : s.toCharArray()) {
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }

        StringBuilder result = new StringBuilder();

        for (char c : order.toCharArray()) {
            if (freq.containsKey(c)) {
                int count = freq.get(c);
                for (int i = 0; i < count; i++) {
                    result.append(c);
                }
                freq.remove(c);
            }
        }

        for (Map.Entry<Character, Integer> entry : freq.entrySet()) {
            char c = entry.getKey();
            int count = entry.getValue();
            for (int i = 0; i < count; i++) {
                result.append(c);
            }
        }

        return result.toString();
    }

    // ==================== Test Cases ====================

    public static void main(String[] args) {
        CustomSortString solution = new CustomSortString();

        System.out.println("=== LeetCode 791: Custom Sort String ===\n");

        // Test 1: Basic example
        System.out.println("Test 1: Basic example");
        String order1 = "cba";
        String s1 = "abcd";
        System.out.println("Order: " + order1);
        System.out.println("String: " + s1);
        System.out.println("Result (Counting): " + solution.customSortString(order1, s1));
        System.out.println("Result (Comparator): " + solution.customSortStringComparator(order1, s1));
        System.out.println("Expected: dcba, cdba, or cbda (d can be anywhere)\n");

        // Test 2: Longer order
        System.out.println("Test 2: Longer order");
        String order2 = "bcafg";
        String s2 = "abcd";
        System.out.println("Order: " + order2);
        System.out.println("String: " + s2);
        System.out.println("Result (Counting): " + solution.customSortString(order2, s2));
        System.out.println("Result (Bucket): " + solution.customSortStringBucket(order2, s2));
        System.out.println("Expected: bcad or bcda\n");

        // Test 3: All characters in order
        System.out.println("Test 3: All characters appear in order");
        String order3 = "dcba";
        String s3 = "abcd";
        System.out.println("Order: " + order3);
        System.out.println("String: " + s3);
        System.out.println("Result: " + solution.customSortString(order3, s3));
        System.out.println("Expected: dcba\n");

        // Test 4: Repeated characters
        System.out.println("Test 4: Repeated characters");
        String order4 = "cba";
        String s4 = "aabbcc";
        System.out.println("Order: " + order4);
        System.out.println("String: " + s4);
        System.out.println("Result: " + solution.customSortString(order4, s4));
        System.out.println("Expected: ccbbaa\n");

        // Test 5: No overlap
        System.out.println("Test 5: No characters from order in s");
        String order5 = "abc";
        String s5 = "xyz";
        System.out.println("Order: " + order5);
        System.out.println("String: " + s5);
        System.out.println("Result: " + solution.customSortString(order5, s5));
        System.out.println("Expected: xyz (any order)\n");

        // Test 6: Single character
        System.out.println("Test 6: Single character");
        String order6 = "a";
        String s6 = "a";
        System.out.println("Order: " + order6);
        System.out.println("String: " + s6);
        System.out.println("Result: " + solution.customSortString(order6, s6));
        System.out.println("Expected: a\n");

        // Test 7: Complex example
        System.out.println("Test 7: Complex with duplicates");
        String order7 = "kqep";
        String s7 = "pekeq";
        System.out.println("Order: " + order7);
        System.out.println("String: " + s7);
        System.out.println("Result (Counting): " + solution.customSortString(order7, s7));
        System.out.println("Result (Optimized): " + solution.customSortStringOptimized(order7, s7));
        System.out.println("Expected: kqeep or kqepe\n");

        // Time complexity comparison
        System.out.println("=== Time Complexity ===");
        System.out.println("Approach 1 (Counting Sort): O(n + m) - LINEAR ✓");
        System.out.println("Approach 2 (Comparator): O(n log n)");
        System.out.println("Approach 3 (Bucket): O(n + m) - LINEAR ✓");
        System.out.println("Approach 4 (Optimized): O(n + m) - LINEAR ✓");
        System.out.println("\nWhere n = length of s, m = length of order");
    }
}
