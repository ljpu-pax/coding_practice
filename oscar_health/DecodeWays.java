/**
 * LeetCode 91 - Decode Ways
 *
 * Problem:
 * Given a numeric string s encoded with the mapping "A"->1 ... "Z"->26,
 * compute how many distinct decodings exist. Leading zeros or invalid pairs
 * such as "30" cannot be decoded.
 *
 * Approach:
 * Dynamic programming where dp[i] counts the decodings for the prefix of
 * length i. A non-zero digit at position i-1 can stand alone (dp[i-1]) and a
 * two-digit value between 10 and 26 formed with the previous digit can pair
 * up (dp[i-2]). Seed dp[0]=1 for the empty prefix and dp[1] based on whether
 * the first char is zero.
 *
 * Complexity:
 * Time O(n) and space O(n) for the dp array (can be reduced to O(1) by
 * keeping the previous two states).
 */
public class DecodeWays {
    /**
     * Returns the number of ways to decode the provided digit string using the mapping 1->A ... 26->Z.
     */
    public int numDecodings(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }

        int n = s.length();
        int[] dp = new int[n + 1];
        dp[0] = 1; // Empty prefix can be decoded in one way
        dp[1] = s.charAt(0) == '0' ? 0 : 1;

        for (int i = 2; i <= n; i++) {
            char current = s.charAt(i - 1);
            char prev = s.charAt(i - 2);

            if (current != '0') {
                dp[i] += dp[i - 1];
            }

            int twoDigit = (prev - '0') * 10 + (current - '0');
            if (twoDigit >= 10 && twoDigit <= 26) {
                dp[i] += dp[i - 2];
            }
        }

        return dp[n];
    }

    public static void main(String[] args) {
        DecodeWays solver = new DecodeWays();
        System.out.println(solver.numDecodings("12")); // 2 -> "AB", "L"
        System.out.println(solver.numDecodings("226")); // 3 -> "BZ", "VF", "BBF"
        System.out.println(solver.numDecodings("06")); // 0 -> invalid
        System.out.println(solver.numDecodings("11106")); // 2 -> "AAJF", "KJF"
    }
}
