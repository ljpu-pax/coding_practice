import java.util.*;

/**
 * LeetCode 67. Add Binary
 *
 * Problem:
 * Given two binary strings a and b, return their sum as a binary string.
 *
 * Example 1:
 * Input: a = "11", b = "1"
 * Output: "100"
 *
 * Example 2:
 * Input: a = "1010", b = "1011"
 * Output: "10101"
 *
 * Constraints:
 * - 1 <= a.length, b.length <= 10^4
 * - a and b consist only of '0' or '1' characters
 * - Each string does not contain leading zeros except for the zero itself
 *
 * Time Complexity: O(max(m, n)) where m = a.length, n = b.length
 * Space Complexity: O(max(m, n))
 */

class AddBinary {

    /**
     * Solution 1: Bit-by-Bit Addition (Optimal)
     * Time: O(max(m, n))
     * Space: O(max(m, n))
     *
     * Key Insight:
     * - Process from right to left (like manual addition)
     * - Track carry bit
     * - Handle different lengths
     */
    public String addBinary(String a, String b) {
        StringBuilder result = new StringBuilder();
        int i = a.length() - 1;
        int j = b.length() - 1;
        int carry = 0;

        while (i >= 0 || j >= 0 || carry > 0) {
            int sum = carry;

            if (i >= 0) {
                sum += a.charAt(i) - '0';
                i--;
            }

            if (j >= 0) {
                sum += b.charAt(j) - '0';
                j--;
            }

            result.append(sum % 2);
            carry = sum / 2;
        }

        return result.reverse().toString();
    }

    /**
     * Solution 2: Cleaner Version
     * Time: O(max(m, n))
     * Space: O(max(m, n))
     */
    public String addBinaryClean(String a, String b) {
        StringBuilder sb = new StringBuilder();
        int i = a.length() - 1;
        int j = b.length() - 1;
        int carry = 0;

        while (i >= 0 || j >= 0) {
            int sum = carry;
            if (i >= 0) sum += a.charAt(i--) - '0';
            if (j >= 0) sum += b.charAt(j--) - '0';

            sb.append(sum % 2);
            carry = sum / 2;
        }

        if (carry > 0) {
            sb.append(carry);
        }

        return sb.reverse().toString();
    }

    /**
     * Solution 3: Using BigInteger (Not Recommended for Interview)
     * Time: O(max(m, n))
     * Space: O(max(m, n))
     *
     * Works but defeats the purpose of the problem
     */
    public String addBinaryBigInteger(String a, String b) {
        java.math.BigInteger num1 = new java.math.BigInteger(a, 2);
        java.math.BigInteger num2 = new java.math.BigInteger(b, 2);
        java.math.BigInteger sum = num1.add(num2);
        return sum.toString(2);
    }

    /**
     * Solution 4: Bit Manipulation (Advanced)
     * Time: O(max(m, n))
     * Space: O(max(m, n))
     *
     * Only works for strings that fit in integer/long
     */
    public String addBinaryBitManipulation(String a, String b) {
        // Only works if strings fit in long (< 64 bits)
        if (a.length() > 63 || b.length() > 63) {
            return addBinary(a, b); // Fall back to solution 1
        }

        long x = Long.parseLong(a, 2);
        long y = Long.parseLong(b, 2);

        while (y != 0) {
            long answer = x ^ y; // XOR gives sum without carry
            long carry = (x & y) << 1; // AND and shift gives carry
            x = answer;
            y = carry;
        }

        return Long.toBinaryString(x);
    }

    /**
     * Solution 5: Recursive Approach (Educational)
     * Time: O(max(m, n))
     * Space: O(max(m, n)) - recursion stack
     */
    public String addBinaryRecursive(String a, String b) {
        return addHelper(a, b, 0);
    }

    private String addHelper(String a, String b, int carry) {
        if (a.isEmpty() && b.isEmpty() && carry == 0) {
            return "";
        }

        int sum = carry;

        if (!a.isEmpty()) {
            sum += a.charAt(a.length() - 1) - '0';
            a = a.substring(0, a.length() - 1);
        }

        if (!b.isEmpty()) {
            sum += b.charAt(b.length() - 1) - '0';
            b = b.substring(0, b.length() - 1);
        }

        return addHelper(a, b, sum / 2) + (sum % 2);
    }
}

/**
 * Test Cases
 */
class AddBinaryTest {
    public static void main(String[] args) {
        AddBinary solution = new AddBinary();

        // Test 1: Basic addition
        String a1 = "11", b1 = "1";
        System.out.println("Test 1: a=\"" + a1 + "\", b=\"" + b1 + "\"");
        System.out.println("Solution 1: " + solution.addBinary(a1, b1));
        System.out.println("Solution 2: " + solution.addBinaryClean(a1, b1));
        System.out.println("Expected: \"100\"\n");

        // Test 2: Longer strings
        String a2 = "1010", b2 = "1011";
        System.out.println("Test 2: a=\"" + a2 + "\", b=\"" + b2 + "\"");
        System.out.println("Solution 1: " + solution.addBinary(a2, b2));
        System.out.println("Expected: \"10101\"\n");

        // Test 3: Different lengths
        String a3 = "1111", b3 = "1";
        System.out.println("Test 3: a=\"" + a3 + "\", b=\"" + b3 + "\"");
        System.out.println("Solution 1: " + solution.addBinary(a3, b3));
        System.out.println("Expected: \"10000\"\n");

        // Test 4: Both zeros
        String a4 = "0", b4 = "0";
        System.out.println("Test 4: a=\"" + a4 + "\", b=\"" + b4 + "\"");
        System.out.println("Solution 1: " + solution.addBinary(a4, b4));
        System.out.println("Expected: \"0\"\n");

        // Test 5: One is zero
        String a5 = "0", b5 = "1";
        System.out.println("Test 5: a=\"" + a5 + "\", b=\"" + b5 + "\"");
        System.out.println("Solution 1: " + solution.addBinary(a5, b5));
        System.out.println("Expected: \"1\"\n");

        // Test 6: All ones
        String a6 = "1111", b6 = "1111";
        System.out.println("Test 6: a=\"" + a6 + "\", b=\"" + b6 + "\"");
        System.out.println("Solution 1: " + solution.addBinary(a6, b6));
        System.out.println("Expected: \"11110\"\n");

        // Test 7: Very different lengths
        String a7 = "10100000100100110110010000010101111011011001101110111111111101000000101111001110001111100001101";
        String b7 = "110101001011101110001111100110001010100001101011101010000011011011001011101111001100000011011110011";
        System.out.println("Test 7: Very long strings");
        System.out.println("Solution 1: " + solution.addBinary(a7, b7));
        System.out.println("Expected: \"110111101100010011000101110110100000011101000101011001000011011000001100011110011010010011000000000\"\n");
    }
}

/**
 * Complexity Analysis:
 * ===================
 *
 * Solution 1 (Bit-by-Bit): ⭐ RECOMMENDED
 * - Time: O(max(m, n))
 * - Space: O(max(m, n)) for result string
 * - Pros: Clear, handles all cases, optimal
 * - Cons: None
 *
 * Solution 2 (Clean Version):
 * - Time: O(max(m, n))
 * - Space: O(max(m, n))
 * - Pros: More concise code
 * - Cons: Slightly less readable
 *
 * Solution 3 (BigInteger):
 * - Time: O(max(m, n))
 * - Space: O(max(m, n))
 * - Pros: Simple, handles any size
 * - Cons: Not demonstrating algorithm understanding
 *
 * Solution 4 (Bit Manipulation):
 * - Time: O(max(m, n))
 * - Space: O(max(m, n))
 * - Pros: Shows bit manipulation knowledge
 * - Cons: Doesn't work for very long strings
 *
 * Solution 5 (Recursive):
 * - Time: O(max(m, n))
 * - Space: O(max(m, n)) - recursion stack + substring
 * - Pros: Educational
 * - Cons: Inefficient (substring creates new strings)
 *
 *
 * Key Insights:
 * ============
 *
 * 1. Binary Addition Rules:
 *    0 + 0 = 0, carry 0
 *    0 + 1 = 1, carry 0
 *    1 + 0 = 1, carry 0
 *    1 + 1 = 0, carry 1
 *    1 + 1 + 1 (with carry) = 1, carry 1
 *
 * 2. Process Right to Left:
 *    - Like manual addition
 *    - Track carry for next position
 *
 * 3. Handle Different Lengths:
 *    - Continue until both strings exhausted AND carry is 0
 *    - Treat missing digits as 0
 *
 * 4. StringBuilder:
 *    - Append to end, reverse at the end
 *    - More efficient than prepending
 *
 *
 * Interview Tips:
 * ==============
 *
 * 1. Clarify Requirements:
 *    - Leading zeros in result? (No)
 *    - Empty strings? (Problem says length >= 1)
 *    - Maximum length? (10^4 in constraints)
 *
 * 2. Start Simple:
 *    "Process from right to left, like manual addition"
 *    "Track carry, add corresponding bits"
 *
 * 3. Walk Through Example:
 *    a = "1010"
 *    b = "1011"
 *
 *    Step by step:
 *    0 + 1 = 1, carry 0
 *    1 + 1 = 0, carry 1
 *    0 + 0 + carry 1 = 1, carry 0
 *    1 + 1 = 0, carry 1
 *    carry 1 remains
 *
 *    Result: "10101"
 *
 * 4. Edge Cases:
 *    - Both zero
 *    - One is zero
 *    - Different lengths (e.g., "1" + "1111")
 *    - All ones (maximum carry)
 *    - Final carry (result length = max(m,n) + 1)
 *
 * 5. Common Mistakes:
 *    - Forgetting final carry
 *    - Not handling different lengths
 *    - Prepending to string (inefficient)
 *    - Off-by-one errors in indices
 *
 * 6. Optimization Notes:
 *    - StringBuilder.reverse() is O(n)
 *    - Could build string right-to-left and avoid reverse
 *    - But reverse is clearer and still O(n) overall
 *
 * 7. Follow-up Questions:
 *    - Add in different bases (base 8, base 16)? → Similar approach
 *    - Subtract instead of add? → Need borrow instead of carry
 *    - Multiply two binary strings? → Different algorithm
 *    - What if strings are very large (> 10^4)? → Same approach works
 *
 * 8. Related Problems:
 *    - LeetCode 2: Add Two Numbers (linked list version)
 *    - LeetCode 415: Add Strings (decimal strings)
 *    - LeetCode 43: Multiply Strings
 *    - LeetCode 66: Plus One
 *
 * 9. Pattern Recognition:
 *    This is a "simulation" problem:
 *    - Simulate manual process
 *    - Process digit by digit
 *    - Track carry/borrow
 *    - Common in string math problems
 */
