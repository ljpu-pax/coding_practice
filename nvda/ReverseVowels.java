package nvda;

import java.util.*;

public class ReverseVowels {

    public static String reverseVowels(String s) {
        if (s == null || s.length() == 0) return s;

        Set<Character> vowels = new HashSet<>(
                Arrays.asList('a', 'e', 'i', 'o', 'u',
                              'A', 'E', 'I', 'O', 'U'));

        char[] chars = s.toCharArray();
        int left = 0, right = chars.length - 1;

        while (left < right) {
            while (left < right && !vowels.contains(chars[left])) left++;
            while (left < right && !vowels.contains(chars[right])) right--;

            // Swap vowels
            char temp = chars[left];
            chars[left] = chars[right];
            chars[right] = temp;

            left++;
            right--;
        }

        return new String(chars);
    }

    // ✅ 测试入口
    public static void main(String[] args) {
        test("hello", "holle");
        test("leetcode", "leotcede");
        test("aA", "Aa");
        test("bcdfg", "bcdfg");
    }

    private static void test(String input, String expected) {
        String output = reverseVowels(input);
        System.out.println("Input: " + input);
        System.out.println("Output: " + output);
        System.out.println(output.equals(expected) ? "✅ Passed" : "❌ Failed (Expected: " + expected + ")");
        System.out.println();
    }
}

