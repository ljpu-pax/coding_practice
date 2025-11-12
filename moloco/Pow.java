import java.util.*;

/**
 * LeetCode 50: Pow(x, n)
 *
 * Implement pow(x, n), which calculates x raised to the power n (i.e., x^n).
 *
 * Example 1:
 * Input: x = 2.00000, n = 10
 * Output: 1024.00000
 *
 * Example 2:
 * Input: x = 2.10000, n = 3
 * Output: 9.26100
 *
 * Example 3:
 * Input: x = 2.00000, n = -2
 * Output: 0.25000
 * Explanation: 2^-2 = 1/2^2 = 1/4 = 0.25
 *
 * Constraints:
 * - -100.0 < x < 100.0
 * - -2^31 <= n <= 2^31-1
 * - n is an integer.
 * - Either x is not zero or n > 0.
 * - -10^4 <= x^n <= 10^4
 */
class Pow {
    /**
     * Approach 1: Fast Exponentiation (Recursive)
     *
     * Key Insight: x^n = (x^2)^(n/2) if n is even
     *              x^n = x * x^(n-1) if n is odd
     *
     * This reduces time complexity from O(n) to O(log n)
     *
     * Time: O(log n)
     * Space: O(log n) for recursion stack
     */
    public double myPow(double x, int n) {
        long N = n; // Use long to handle Integer.MIN_VALUE edge case
        if (N < 0) {
            x = 1 / x;
            N = -N;
        }
        return fastPow(x, N);
    }

    private double fastPow(double x, long n) {
        if (n == 0) {
            return 1.0;
        }

        double half = fastPow(x, n / 2);

        if (n % 2 == 0) {
            return half * half;
        } else {
            return half * half * x;
        }
    }

    /**
     * Approach 2: Fast Exponentiation (Iterative)
     *
     * Use bit manipulation to iteratively calculate the power.
     * If bit i is set in n, multiply result by x^(2^i)
     *
     * Time: O(log n)
     * Space: O(1)
     */
    public double myPowIterative(double x, int n) {
        long N = n;
        if (N < 0) {
            x = 1 / x;
            N = -N;
        }

        double result = 1.0;
        double currentProduct = x;

        while (N > 0) {
            if (N % 2 == 1) {
                result *= currentProduct;
            }
            currentProduct *= currentProduct;
            N /= 2;
        }

        return result;
    }

    /**
     * Approach 3: Fast Exponentiation with Bit Manipulation
     *
     * Similar to approach 2 but using bitwise operations.
     *
     * Time: O(log n)
     * Space: O(1)
     */
    public double myPowBitwise(double x, int n) {
        long N = n;
        if (N < 0) {
            x = 1 / x;
            N = -N;
        }

        double result = 1.0;
        double base = x;

        while (N > 0) {
            if ((N & 1) == 1) { // Check if last bit is 1
                result *= base;
            }
            base *= base;
            N >>= 1; // Right shift (divide by 2)
        }

        return result;
    }

    /**
     * Approach 4: Naive approach (for comparison - too slow)
     *
     * Simply multiply x by itself n times.
     *
     * Time: O(n)
     * Space: O(1)
     *
     * Note: This will TLE on LeetCode for large n
     */
    public double myPowNaive(double x, int n) {
        long N = n;
        if (N < 0) {
            x = 1 / x;
            N = -N;
        }

        double result = 1.0;
        for (long i = 0; i < N; i++) {
            result *= x;
        }

        return result;
    }

    /**
     * Approach 5: Recursive with memoization (alternative)
     *
     * Cache results to avoid recalculation.
     *
     * Time: O(log n)
     * Space: O(log n)
     */
    public double myPowMemo(double x, int n) {
        Map<Long, Double> memo = new HashMap<>();
        long N = n;
        if (N < 0) {
            x = 1 / x;
            N = -N;
        }
        return powWithMemo(x, N, memo);
    }

    private double powWithMemo(double x, long n, Map<Long, Double> memo) {
        if (n == 0) return 1.0;
        if (n == 1) return x;

        if (memo.containsKey(n)) {
            return memo.get(n);
        }

        double half = powWithMemo(x, n / 2, memo);
        double result = half * half;
        if (n % 2 == 1) {
            result *= x;
        }

        memo.put(n, result);
        return result;
    }
}

/**
 * Related variations and applications
 */
class PowVariations {
    /**
     * Calculate x^n mod m (useful for large numbers)
     */
    public long powMod(long x, long n, long mod) {
        if (n == 0) return 1;

        long result = 1;
        x = x % mod;

        while (n > 0) {
            if (n % 2 == 1) {
                result = (result * x) % mod;
            }
            x = (x * x) % mod;
            n /= 2;
        }

        return result;
    }

    /**
     * Calculate integer power (return long)
     */
    public long intPow(long x, int n) {
        if (n == 0) return 1;

        long result = 1;
        long base = x;
        long N = Math.abs((long) n);

        while (N > 0) {
            if (N % 2 == 1) {
                result *= base;
            }
            base *= base;
            N /= 2;
        }

        return n < 0 ? 1 / result : result;
    }

    /**
     * Check if a number is a perfect power
     * Returns true if num = x^n for some integer x and n >= 2
     */
    public boolean isPerfectPower(int num) {
        if (num <= 1) return false;

        for (int exp = 2; exp <= 31; exp++) { // 2^31 is max for int
            int base = (int) Math.pow(num, 1.0 / exp);

            // Check base and base+1 due to floating point errors
            for (int b = Math.max(2, base - 1); b <= base + 1; b++) {
                long power = 1;
                for (int i = 0; i < exp; i++) {
                    power *= b;
                    if (power > num) break;
                }
                if (power == num) return true;
            }
        }

        return false;
    }

    /**
     * Matrix exponentiation (useful for Fibonacci, etc.)
     * Calculate matrix^n
     */
    public long[][] matrixPow(long[][] matrix, int n) {
        if (n == 1) return matrix;

        int size = matrix.length;
        long[][] result = new long[size][size];

        // Initialize result as identity matrix
        for (int i = 0; i < size; i++) {
            result[i][i] = 1;
        }

        long[][] base = matrix;

        while (n > 0) {
            if (n % 2 == 1) {
                result = multiplyMatrix(result, base);
            }
            base = multiplyMatrix(base, base);
            n /= 2;
        }

        return result;
    }

    private long[][] multiplyMatrix(long[][] a, long[][] b) {
        int size = a.length;
        long[][] result = new long[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }

        return result;
    }
}

/**
 * Test cases
 */
class PowTest {
    public static void main(String[] args) {
        testBasicCases();
        testEdgeCases();
        testVariations();
    }

    private static void testBasicCases() {
        System.out.println("=== Testing LeetCode 50: Pow(x, n) ===\n");
        Pow solution = new Pow();

        // Test 1: Positive power
        double result1 = solution.myPow(2.0, 10);
        double result1Iter = solution.myPowIterative(2.0, 10);
        double result1Bit = solution.myPowBitwise(2.0, 10);
        System.out.println("Test 1: pow(2.0, 10)");
        System.out.println("  Recursive: " + result1 + " (Expected: 1024.0) - " +
                          (Math.abs(result1 - 1024.0) < 0.00001 ? "PASS" : "FAIL"));
        System.out.println("  Iterative: " + result1Iter + " (Expected: 1024.0) - " +
                          (Math.abs(result1Iter - 1024.0) < 0.00001 ? "PASS" : "FAIL"));
        System.out.println("  Bitwise: " + result1Bit + " (Expected: 1024.0) - " +
                          (Math.abs(result1Bit - 1024.0) < 0.00001 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 2: Decimal base
        double result2 = solution.myPow(2.1, 3);
        System.out.println("Test 2: pow(2.1, 3)");
        System.out.println("  Result: " + result2 + " (Expected: 9.261) - " +
                          (Math.abs(result2 - 9.261) < 0.001 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 3: Negative power
        double result3 = solution.myPow(2.0, -2);
        System.out.println("Test 3: pow(2.0, -2)");
        System.out.println("  Result: " + result3 + " (Expected: 0.25) - " +
                          (Math.abs(result3 - 0.25) < 0.00001 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 4: Power of 0
        double result4 = solution.myPow(2.0, 0);
        System.out.println("Test 4: pow(2.0, 0)");
        System.out.println("  Result: " + result4 + " (Expected: 1.0) - " +
                          (Math.abs(result4 - 1.0) < 0.00001 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 5: Power of 1
        double result5 = solution.myPow(2.0, 1);
        System.out.println("Test 5: pow(2.0, 1)");
        System.out.println("  Result: " + result5 + " (Expected: 2.0) - " +
                          (Math.abs(result5 - 2.0) < 0.00001 ? "PASS" : "FAIL"));
        System.out.println();
    }

    private static void testEdgeCases() {
        System.out.println("=== Testing Edge Cases ===\n");
        Pow solution = new Pow();

        // Test 1: Integer.MIN_VALUE
        double result1 = solution.myPow(2.0, Integer.MIN_VALUE);
        System.out.println("Test 1: pow(2.0, Integer.MIN_VALUE)");
        System.out.println("  Result: " + result1 + " (Should not crash) - PASS");
        System.out.println();

        // Test 2: Integer.MAX_VALUE
        double result2 = solution.myPow(1.0, Integer.MAX_VALUE);
        System.out.println("Test 2: pow(1.0, Integer.MAX_VALUE)");
        System.out.println("  Result: " + result2 + " (Expected: 1.0) - " +
                          (Math.abs(result2 - 1.0) < 0.00001 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 3: Negative base with even power
        double result3 = solution.myPow(-2.0, 2);
        System.out.println("Test 3: pow(-2.0, 2)");
        System.out.println("  Result: " + result3 + " (Expected: 4.0) - " +
                          (Math.abs(result3 - 4.0) < 0.00001 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 4: Negative base with odd power
        double result4 = solution.myPow(-2.0, 3);
        System.out.println("Test 4: pow(-2.0, 3)");
        System.out.println("  Result: " + result4 + " (Expected: -8.0) - " +
                          (Math.abs(result4 - (-8.0)) < 0.00001 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 5: Very small base
        double result5 = solution.myPow(0.00001, 5);
        System.out.println("Test 5: pow(0.00001, 5)");
        System.out.println("  Result: " + result5 + " (Very small number)");
        System.out.println();

        // Test 6: Base close to 1
        double result6 = solution.myPow(1.00001, 100000);
        System.out.println("Test 6: pow(1.00001, 100000)");
        System.out.println("  Result: " + result6);
        System.out.println();
    }

    private static void testVariations() {
        System.out.println("=== Testing Variations ===\n");
        PowVariations variations = new PowVariations();

        // Test 1: Power with modulo
        long result1 = variations.powMod(2, 10, 1000);
        System.out.println("Test 1: pow(2, 10) mod 1000");
        System.out.println("  Result: " + result1 + " (Expected: 24) - " + (result1 == 24 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 2: Integer power
        long result2 = variations.intPow(2, 10);
        System.out.println("Test 2: intPow(2, 10)");
        System.out.println("  Result: " + result2 + " (Expected: 1024) - " + (result2 == 1024 ? "PASS" : "FAIL"));
        System.out.println();

        // Test 3: Perfect power check
        boolean result3a = variations.isPerfectPower(8);
        boolean result3b = variations.isPerfectPower(10);
        System.out.println("Test 3: isPerfectPower");
        System.out.println("  8: " + result3a + " (Expected: true) - " + (result3a ? "PASS" : "FAIL"));
        System.out.println("  10: " + result3b + " (Expected: false) - " + (!result3b ? "PASS" : "FAIL"));
        System.out.println();

        // Test 4: Matrix exponentiation (Fibonacci example)
        long[][] fibMatrix = {{1, 1}, {1, 0}};
        long[][] result4 = variations.matrixPow(fibMatrix, 5);
        System.out.println("Test 4: Matrix exponentiation for Fibonacci");
        System.out.println("  F(5) = " + result4[0][1] + " (Expected: 5)");
        System.out.println();
    }
}
