package nvda;

import java.util.*;

public class LongestSubstring {
    
    // LeetCode 3: Longest Substring Without Repeating Characters
    // Sliding window with HashMap - O(n) time, O(min(m,n)) space
    public int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> map = new HashMap<>();
        int left = 0, maxLen = 0;
        
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            
            if (map.containsKey(c) && map.get(c) >= left) {
                left = map.get(c) + 1;
            }
            
            map.put(c, right);
            maxLen = Math.max(maxLen, right - left + 1);
        }
        
        return maxLen;
    }

    // LeetCode 5: Longest Palindromic Substring
    // Expand around centers - O(n²) time, O(1) space
    public String longestPalindrome(String s) {
        if (s == null || s.length() < 1) return "";
        
        int start = 0, end = 0;
        
        for (int i = 0; i < s.length(); i++) {
            int len1 = expandAroundCenter(s, i, i);     // Odd length
            int len2 = expandAroundCenter(s, i, i + 1); // Even length
            int len = Math.max(len1, len2);
            
            if (len > end - start) {
                start = i - (len - 1) / 2;
                end = i + len / 2;
            }
        }
        
        return s.substring(start, end + 1);
    }
    
    private int expandAroundCenter(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        return right - left - 1;
    }

    // LeetCode 76: Minimum Window Substring
    // Sliding window with two pointers - O(|s| + |t|) time, O(|s| + |t|) space
    public String minWindow(String s, String t) {
        if (s.length() < t.length()) return "";
        
        Map<Character, Integer> need = new HashMap<>();
        for (char c : t.toCharArray()) {
            need.put(c, need.getOrDefault(c, 0) + 1);
        }
        
        int left = 0, right = 0;
        int valid = 0; // Number of characters with correct count
        int start = 0, len = Integer.MAX_VALUE;
        Map<Character, Integer> window = new HashMap<>();
        
        while (right < s.length()) {
            char c = s.charAt(right);
            right++;
            
            if (need.containsKey(c)) {
                window.put(c, window.getOrDefault(c, 0) + 1);
                if (window.get(c).equals(need.get(c))) {
                    valid++;
                }
            }
            
            while (valid == need.size()) {
                if (right - left < len) {
                    start = left;
                    len = right - left;
                }
                
                char d = s.charAt(left);
                left++;
                
                if (need.containsKey(d)) {
                    if (window.get(d).equals(need.get(d))) {
                        valid--;
                    }
                    window.put(d, window.get(d) - 1);
                }
            }
        }
        
        return len == Integer.MAX_VALUE ? "" : s.substring(start, start + len);
    }

    // LeetCode 159: Longest Substring with At Most Two Distinct Characters
    // Sliding window - O(n) time, O(1) space
    public int lengthOfLongestSubstringTwoDistinct(String s) {
        Map<Character, Integer> map = new HashMap<>();
        int left = 0, maxLen = 0;
        
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            map.put(c, map.getOrDefault(c, 0) + 1);
            
            while (map.size() > 2) {
                char leftChar = s.charAt(left);
                map.put(leftChar, map.get(leftChar) - 1);
                if (map.get(leftChar) == 0) {
                    map.remove(leftChar);
                }
                left++;
            }
            
            maxLen = Math.max(maxLen, right - left + 1);
        }
        
        return maxLen;
    }

    // LeetCode 340: Longest Substring with At Most K Distinct Characters
    // Sliding window - O(n) time, O(k) space
    public int lengthOfLongestSubstringKDistinct(String s, int k) {
        Map<Character, Integer> map = new HashMap<>();
        int left = 0, maxLen = 0;
        
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            map.put(c, map.getOrDefault(c, 0) + 1);
            
            while (map.size() > k) {
                char leftChar = s.charAt(left);
                map.put(leftChar, map.get(leftChar) - 1);
                if (map.get(leftChar) == 0) {
                    map.remove(leftChar);
                }
                left++;
            }
            
            maxLen = Math.max(maxLen, right - left + 1);
        }
        
        return maxLen;
    }

    // LeetCode 424: Longest Repeating Character Replacement
    // Sliding window - O(n) time, O(1) space
    public int characterReplacement(String s, int k) {
        int[] count = new int[26];
        int left = 0, maxCount = 0, maxLen = 0;
        
        for (int right = 0; right < s.length(); right++) {
            maxCount = Math.max(maxCount, ++count[s.charAt(right) - 'A']);
            
            if (right - left + 1 - maxCount > k) {
                count[s.charAt(left) - 'A']--;
                left++;
            }
            
            maxLen = Math.max(maxLen, right - left + 1);
        }
        
        return maxLen;
    }

    public static void main(String[] args) {
        LongestSubstring solver = new LongestSubstring();
        
        // Test Longest Substring Without Repeating Characters
        System.out.println("=== Longest Substring Without Repeating Characters (LeetCode 3) ===");
        String[] testCases1 = {"abcabcbb", "bbbbb", "pwwkew", ""};
        for (String test : testCases1) {
            System.out.println("Input: \"" + test + "\" -> " + solver.lengthOfLongestSubstring(test));
        }
        System.out.println();
        
        // Test Longest Palindromic Substring
        System.out.println("=== Longest Palindromic Substring (LeetCode 5) ===");
        String[] testCases2 = {"babad", "cbbd", "a", "ac"};
        for (String test : testCases2) {
            System.out.println("Input: \"" + test + "\" -> \"" + solver.longestPalindrome(test) + "\"");
        }
        System.out.println();
        
        // Test Minimum Window Substring
        System.out.println("=== Minimum Window Substring (LeetCode 76) ===");
        String s = "ADOBECODEBANC";
        String t = "ABC";
        System.out.println("Input: s=\"" + s + "\", t=\"" + t + "\" -> \"" + solver.minWindow(s, t) + "\"");
        System.out.println();
        
        // Test Longest Substring with At Most Two Distinct Characters
        System.out.println("=== Longest Substring with At Most Two Distinct Characters (LeetCode 159) ===");
        String testCase3 = "eceba";
        System.out.println("Input: \"" + testCase3 + "\" -> " + solver.lengthOfLongestSubstringTwoDistinct(testCase3));
        System.out.println();
        
        // Test Longest Substring with At Most K Distinct Characters
        System.out.println("=== Longest Substring with At Most K Distinct Characters (LeetCode 340) ===");
        String testCase4 = "eceba";
        int k = 2;
        System.out.println("Input: \"" + testCase4 + "\", k=" + k + " -> " + solver.lengthOfLongestSubstringKDistinct(testCase4, k));
        System.out.println();
        
        // Test Longest Repeating Character Replacement
        System.out.println("=== Longest Repeating Character Replacement (LeetCode 424) ===");
        String testCase5 = "AABABBA";
        int k2 = 1;
        System.out.println("Input: \"" + testCase5 + "\", k=" + k2 + " -> " + solver.characterReplacement(testCase5, k2));
    }
}
