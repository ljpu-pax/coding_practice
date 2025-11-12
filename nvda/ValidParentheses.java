package nvda;

import java.util.*;

public class ValidParentheses {
    
    // LeetCode 20: Valid Parentheses
    // Stack approach - O(n) time, O(n) space
    public boolean isValid(String s) {
        Stack<Character> stack = new Stack<>();
        
        for (char c : s.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
            } else if (c == ')' || c == ']' || c == '}') {
                if (stack.isEmpty()) return false;
                
                char top = stack.pop();
                if ((c == ')' && top != '(') ||
                    (c == ']' && top != '[') ||
                    (c == '}' && top != '{')) {
                    return false;
                }
            }
        }
        
        return stack.isEmpty();
    }

    // LeetCode 22: Generate Parentheses
    // Backtracking - O(4^n / sqrt(n)) time, O(n) space
    public List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        backtrack(result, "", 0, 0, n);
        return result;
    }
    
    private void backtrack(List<String> result, String current, int open, int close, int max) {
        if (current.length() == max * 2) {
            result.add(current);
            return;
        }
        
        if (open < max) {
            backtrack(result, current + "(", open + 1, close, max);
        }
        
        if (close < open) {
            backtrack(result, current + ")", open, close + 1, max);
        }
    }

    // LeetCode 32: Longest Valid Parentheses
    // Stack approach - O(n) time, O(n) space
    public int longestValidParentheses(String s) {
        Stack<Integer> stack = new Stack<>();
        stack.push(-1); // Base for length calculation
        int maxLen = 0;
        
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                stack.push(i);
            } else {
                stack.pop();
                if (stack.isEmpty()) {
                    stack.push(i); // New base
                } else {
                    maxLen = Math.max(maxLen, i - stack.peek());
                }
            }
        }
        
        return maxLen;
    }

    // LeetCode 301: Remove Invalid Parentheses
    // BFS approach - O(2^n) time, O(n) space
    public List<String> removeInvalidParentheses(String s) {
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        
        queue.offer(s);
        visited.add(s);
        boolean found = false;
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            
            if (isValidParentheses(current)) {
                result.add(current);
                found = true;
            }
            
            if (found) continue; // Don't process further levels
            
            for (int i = 0; i < current.length(); i++) {
                if (current.charAt(i) != '(' && current.charAt(i) != ')') continue;
                
                String next = current.substring(0, i) + current.substring(i + 1);
                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.offer(next);
                }
            }
        }
        
        return result;
    }

    // LeetCode 678: Valid Parenthesis String
    // Greedy approach - O(n) time, O(1) space
    public boolean checkValidString(String s) {
        int minOpen = 0, maxOpen = 0;
        
        for (char c : s.toCharArray()) {
            if (c == '(') {
                minOpen++;
                maxOpen++;
            } else if (c == ')') {
                minOpen--;
                maxOpen--;
            } else { // '*'
                minOpen--;
                maxOpen++;
            }
            
            if (maxOpen < 0) return false;
            minOpen = Math.max(minOpen, 0);
        }
        
        return minOpen == 0;
    }

    // Helper method to test validity for parentheses only
    private boolean isValidParentheses(String s) {
        int count = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') count++;
            else if (c == ')') count--;
            if (count < 0) return false;
        }
        return count == 0;
    }

    public static void main(String[] args) {
        ValidParentheses solver = new ValidParentheses();
        
        // Test Valid Parentheses
        System.out.println("=== Valid Parentheses (LeetCode 20) ===");
        String[] testCases = {"()", "()[]{}", "(]", "([)]", "{[]}"};
        for (String test : testCases) {
            System.out.println("Input: \"" + test + "\" -> " + solver.isValid(test));
        }
        System.out.println();
        
        // Test Generate Parentheses
        System.out.println("=== Generate Parentheses (LeetCode 22) ===");
        int n = 3;
        List<String> result = solver.generateParenthesis(n);
        System.out.println("n = " + n + " -> " + result);
        System.out.println();
        
        // Test Longest Valid Parentheses
        System.out.println("=== Longest Valid Parentheses (LeetCode 32) ===");
        String[] testCases2 = {"(()", ")()())", "", "()(())"};
        for (String test : testCases2) {
            System.out.println("Input: \"" + test + "\" -> " + solver.longestValidParentheses(test));
        }
        System.out.println();
        
        // Test Remove Invalid Parentheses
        System.out.println("=== Remove Invalid Parentheses (LeetCode 301) ===");
        String testCase3 = "()())()";
        List<String> result3 = solver.removeInvalidParentheses(testCase3);
        System.out.println("Input: \"" + testCase3 + "\" -> " + result3);
        System.out.println();
        
        // Test Valid Parenthesis String
        System.out.println("=== Valid Parenthesis String (LeetCode 678) ===");
        String[] testCases4 = {"()", "(*)", "(*))", "((*)"};
        for (String test : testCases4) {
            System.out.println("Input: \"" + test + "\" -> " + solver.checkValidString(test));
        }
    }
}
