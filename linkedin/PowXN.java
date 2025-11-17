// LeetCode 50: Pow(x, n)
// https://leetcode.com/problems/powx-n/
// Difficulty: Medium

// Implement pow(x, n), which calculates x raised to the power n (i.e., x^n).

// Example 1:
// Input: x = 2.00000, n = 10
// Output: 1024.00000

// Example 2:
// Input: x = 2.10000, n = 3
// Output: 9.26100

// Example 3:
// Input: x = 2.00000, n = -2
// Output: 0.25000
// Explanation: 2^-2 = 1/2^2 = 1/4 = 0.25

// Constraints:
// -100.0 < x < 100.0
// -2^31 <= n <= 2^31-1
// n is an integer.
// Either x is not zero or n > 0.
// -10^4 <= x^n <= 10^4

class PowXN {
    // Approach 1: Fast Power (Recursive) - Binary Exponentiation
    // Time: O(log n), Space: O(log n) due to recursion stack
    public double myPow(double x, int n) {
        long N = n; // Convert to long to handle Integer.MIN_VALUE case

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

        // Calculate half power
        double half = fastPow(x, n / 2);

        if (n % 2 == 0) {
            return half * half;
        } else {
            return half * half * x;
        }
    }

    // Approach 2: Fast Power (Iterative) - Binary Exponentiation
    // Time: O(log n), Space: O(1)
    public double myPowIterative(double x, int n) {
        long N = n;

        if (N < 0) {
            x = 1 / x;
            N = -N;
        }

        double result = 1.0;
        double currentProduct = x;

        while (N > 0) {
            // If current bit is 1, multiply result by current product
            if (N % 2 == 1) {
                result *= currentProduct;
            }

            // Square the current product for next bit
            currentProduct *= currentProduct;

            // Move to next bit
            N /= 2;
        }

        return result;
    }

    // Approach 3: Brute Force (for comparison - will TLE on LeetCode)
    // Time: O(n), Space: O(1)
    public double myPowBruteForce(double x, int n) {
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

    public static void main(String[] args) {
        PowXN solution = new PowXN();

        // Test case 1: Positive power
        System.out.println("Test 1: " + solution.myPow(2.0, 10));       // 1024.0
        System.out.println("Test 1 (iterative): " + solution.myPowIterative(2.0, 10)); // 1024.0

        // Test case 2: Decimal base
        System.out.println("Test 2: " + solution.myPow(2.1, 3));        // 9.261
        System.out.println("Test 2 (iterative): " + solution.myPowIterative(2.1, 3)); // 9.261

        // Test case 3: Negative power
        System.out.println("Test 3: " + solution.myPow(2.0, -2));       // 0.25
        System.out.println("Test 3 (iterative): " + solution.myPowIterative(2.0, -2)); // 0.25

        // Test case 4: Power of 0
        System.out.println("Test 4: " + solution.myPow(2.0, 0));        // 1.0

        // Test case 5: Power of 1
        System.out.println("Test 5: " + solution.myPow(2.0, 1));        // 2.0

        // Test case 6: Base 0
        System.out.println("Test 6: " + solution.myPow(0.0, 5));        // 0.0

        // Test case 7: Base 1
        System.out.println("Test 7: " + solution.myPow(1.0, 1000));     // 1.0

        // Test case 8: Negative base
        System.out.println("Test 8: " + solution.myPow(-2.0, 3));       // -8.0
        System.out.println("Test 9: " + solution.myPow(-2.0, 4));       // 16.0

        // Test case 10: Edge case - Integer.MIN_VALUE
        System.out.println("Test 10: " + solution.myPow(2.0, -2147483648)); // Very small number
    }
}

/*
 * Key Insights:
 *
 * 1. Binary Exponentiation (Fast Power Algorithm):
 *    - Instead of multiplying x n times (O(n)), we can do it in O(log n)
 *    - Key idea: x^n = (x^2)^(n/2) if n is even
 *                x^n = x * (x^2)^((n-1)/2) if n is odd
 *
 * 2. Example: 2^10
 *    - 2^10 = (2^2)^5 = 4^5
 *    - 4^5 = 4 * (4^2)^2 = 4 * 16^2
 *    - 16^2 = (16^2)^1 = 256^1
 *    - 256^1 = 256 * 256^0 = 256 * 1 = 256
 *    - Working backwards: 256 * 4 = 1024
 *
 * 3. Handling negative exponents:
 *    - x^(-n) = 1 / (x^n)
 *    - Convert negative to positive and take reciprocal of x
 *
 * 4. Edge case: Integer.MIN_VALUE
 *    - n = -2147483648, and -n would overflow int
 *    - Solution: use long instead of int
 *
 * 5. Iterative approach uses bit manipulation concept:
 *    - Think of n in binary: 10 = 1010₂
 *    - 2^10 = 2^8 * 2^2 (only bits that are 1)
 *    - We build powers of 2 by squaring: x, x^2, x^4, x^8, ...
 *    - Multiply result by powers corresponding to 1 bits
 *
 * Time Complexity: O(log n)
 * Space Complexity: O(log n) recursive, O(1) iterative
 */
