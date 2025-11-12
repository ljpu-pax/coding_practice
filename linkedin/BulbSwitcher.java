import java.util.*;

/**
 * LeetCode 319: Bulb Switcher (Medium)
 *
 * There are n bulbs that are initially off. You first turn on all the bulbs, then you turn off
 * every second bulb.
 *
 * On the third round, you toggle every third bulb (turning on if it's off or turning off if it's on).
 * For the ith round, you toggle every i bulb. For the nth round, you only toggle the last bulb.
 *
 * Return the number of bulbs that are on after n rounds.
 *
 * Example 1:
 * Input: n = 3
 * Output: 1
 * Explanation: At first, the three bulbs are [off, off, off].
 * After the first round, the three bulbs are [on, on, on].
 * After the second round, the three bulbs are [on, off, on].
 * After the third round, the three bulbs are [on, off, off].
 * So you should return 1 because there is only one bulb is on.
 *
 * Example 2:
 * Input: n = 0
 * Output: 0
 *
 * Example 3:
 * Input: n = 1
 * Output: 1
 *
 * Constraints:
 * - 0 <= n <= 10^9
 */
class BulbSwitcher {

    /**
     * Approach 1: Brute Force Simulation (TLE for large n)
     *
     * Simulate all rounds and count final on bulbs
     * Time: O(n²) - too slow for large n
     * Space: O(n)
     */
    public int bulbSwitchBruteForce(int n) {
        if (n == 0) return 0;

        boolean[] bulbs = new boolean[n + 1]; // bulbs[1..n], index 0 unused

        // Round i: toggle every i-th bulb
        for (int round = 1; round <= n; round++) {
            for (int bulb = round; bulb <= n; bulb += round) {
                bulbs[bulb] = !bulbs[bulb];
            }
        }

        // Count on bulbs
        int count = 0;
        for (int i = 1; i <= n; i++) {
            if (bulbs[i]) {
                count++;
            }
        }

        return count;
    }

    /**
     * Approach 2: Mathematical Analysis (Optimal)
     *
     * Key insight: A bulb ends up ON if it's toggled an ODD number of times.
     * Bulb i is toggled in round j if j divides i.
     * Number of toggles = number of divisors of i.
     *
     * A number has an odd number of divisors if and only if it's a PERFECT SQUARE.
     * Example: 9 has divisors {1, 3, 9} (3 divisors - odd)
     *          12 has divisors {1, 2, 3, 4, 6, 12} (6 divisors - even)
     *
     * Therefore: Count of ON bulbs = count of perfect squares <= n = floor(sqrt(n))
     *
     * Time: O(1)
     * Space: O(1)
     */
    public int bulbSwitch(int n) {
        return (int) Math.sqrt(n);
    }

    /**
     * Approach 3: Count perfect squares manually (for verification)
     *
     * Time: O(sqrt(n))
     * Space: O(1)
     */
    public int bulbSwitchCount(int n) {
        if (n == 0) return 0;

        int count = 0;
        for (int i = 1; i * i <= n; i++) {
            count++;
        }

        return count;
    }

    /**
     * Helper: Get divisor count for a number
     */
    private int getDivisorCount(int num) {
        int count = 0;
        for (int i = 1; i * i <= num; i++) {
            if (num % i == 0) {
                if (i * i == num) {
                    count++; // Perfect square, count once
                } else {
                    count += 2; // Count both i and num/i
                }
            }
        }
        return count;
    }

    /**
     * Approach 4: Detailed simulation with explanation
     */
    public int bulbSwitchWithExplanation(int n) {
        if (n == 0) return 0;

        System.out.println("Analyzing bulbs 1 to " + n + ":");
        System.out.println();

        int onCount = 0;

        for (int bulb = 1; bulb <= Math.min(n, 10); bulb++) { // Show first 10 only
            int divisors = getDivisorCount(bulb);
            boolean isOn = (divisors % 2 == 1);
            boolean isPerfectSquare = isPerfectSquare(bulb);

            System.out.printf("Bulb %d: %d divisors (toggles) -> %s (Perfect square: %s)\n",
                           bulb, divisors, isOn ? "ON" : "OFF", isPerfectSquare);

            if (bulb <= n && isOn) {
                onCount++;
            }
        }

        if (n > 10) {
            // Count remaining perfect squares
            for (int i = 4; i * i <= n; i++) {
                onCount++;
            }
        }

        System.out.println();
        System.out.println("Total ON bulbs: " + (int) Math.sqrt(n));

        return (int) Math.sqrt(n);
    }

    private boolean isPerfectSquare(int n) {
        int sqrt = (int) Math.sqrt(n);
        return sqrt * sqrt == n;
    }
}

/**
 * Related: Bulb Switcher II (LeetCode 672)
 */
class BulbSwitcherII {
    /**
     * There are n bulbs and 4 buttons:
     * 1. Flip all bulbs
     * 2. Flip bulbs with even indices
     * 3. Flip bulbs with odd indices
     * 4. Flip bulbs with (3k + 1) indices (1, 4, 7, ...)
     *
     * After pressing buttons m times, how many different status can be?
     */
    public int flipLights(int n, int m) {
        if (m == 0) return 1;

        if (n == 1) {
            return 2; // On or Off
        }

        if (n == 2) {
            if (m == 1) return 3;
            return 4;
        }

        // n >= 3
        if (m == 1) return 4;
        if (m == 2) return 7;
        return 8; // m >= 3
    }
}

/**
 * Related: Bulb Switcher III (LeetCode 1375)
 */
class BulbSwitcherIII {
    /**
     * There are n bulbs, initially off. Turn on bulbs one by one in given order.
     * At any moment, a bulb is blue if:
     * - It is on
     * - All bulbs to its left are also on
     *
     * Return number of moments where all turned-on bulbs are blue.
     */
    public int numTimesAllBlue(int[] light) {
        int count = 0;
        int maxReached = 0;

        for (int i = 0; i < light.length; i++) {
            maxReached = Math.max(maxReached, light[i]);

            // If max bulb reached == number of bulbs turned on, all are blue
            if (maxReached == i + 1) {
                count++;
            }
        }

        return count;
    }
}

/**
 * Related: Bulb Switcher IV (LeetCode 1529)
 */
class BulbSwitcherIV {
    /**
     * Given a target string of 0s and 1s, minimum flips to reach target.
     * Each flip flips the current bulb and all bulbs to the right.
     */
    public int minFlips(String target) {
        int flips = 0;
        char current = '0';

        for (char c : target.toCharArray()) {
            if (c != current) {
                flips++;
                current = c;
            }
        }

        return flips;
    }
}

/**
 * Test cases
 */
class BulbSwitcherTest {
    public static void main(String[] args) {
        testBasicCases();
        testMathematicalInsight();
        testRelatedProblems();
        testLargeCases();
    }

    private static void testBasicCases() {
        System.out.println("=== LeetCode 319: Bulb Switcher ===\n");
        BulbSwitcher solution = new BulbSwitcher();

        // Test 1
        int n1 = 3;
        int result1 = solution.bulbSwitch(n1);
        int brute1 = solution.bulbSwitchBruteForce(n1);
        System.out.println("Test 1: n = " + n1);
        System.out.println("  Optimal: " + result1);
        System.out.println("  Brute Force: " + brute1);
        System.out.println("  Expected: 1");
        System.out.println();

        // Test 2
        int n2 = 0;
        System.out.println("Test 2: n = " + n2);
        System.out.println("  Result: " + solution.bulbSwitch(n2));
        System.out.println("  Expected: 0");
        System.out.println();

        // Test 3
        int n3 = 1;
        System.out.println("Test 3: n = " + n3);
        System.out.println("  Result: " + solution.bulbSwitch(n3));
        System.out.println("  Expected: 1");
        System.out.println();
    }

    private static void testMathematicalInsight() {
        System.out.println("=== Mathematical Insight ===\n");
        BulbSwitcher solution = new BulbSwitcher();

        System.out.println("Perfect squares from 1 to 20:");
        for (int i = 1; i <= 20; i++) {
            int sqrt = (int) Math.sqrt(i);
            if (sqrt * sqrt == i) {
                System.out.println("  " + i + " = " + sqrt + "²");
            }
        }
        System.out.println();

        System.out.println("Detailed analysis for n = 10:");
        solution.bulbSwitchWithExplanation(10);
        System.out.println();
    }

    private static void testRelatedProblems() {
        System.out.println("=== Related Bulb Switcher Problems ===\n");

        // Bulb Switcher II
        BulbSwitcherII bs2 = new BulbSwitcherII();
        System.out.println("Bulb Switcher II:");
        System.out.println("  flipLights(3, 1) = " + bs2.flipLights(3, 1)); // 4
        System.out.println("  flipLights(3, 2) = " + bs2.flipLights(3, 2)); // 7
        System.out.println();

        // Bulb Switcher III
        BulbSwitcherIII bs3 = new BulbSwitcherIII();
        int[] light1 = {2, 1, 3, 5, 4};
        System.out.println("Bulb Switcher III:");
        System.out.println("  numTimesAllBlue([2,1,3,5,4]) = " + bs3.numTimesAllBlue(light1)); // 3
        System.out.println();

        // Bulb Switcher IV
        BulbSwitcherIV bs4 = new BulbSwitcherIV();
        System.out.println("Bulb Switcher IV:");
        System.out.println("  minFlips(\"10111\") = " + bs4.minFlips("10111")); // 3
        System.out.println("  minFlips(\"001011101\") = " + bs4.minFlips("001011101")); // 5
        System.out.println();
    }

    private static void testLargeCases() {
        System.out.println("=== Large Cases (Demonstrating O(1) Efficiency) ===\n");
        BulbSwitcher solution = new BulbSwitcher();

        int[] testCases = {100, 1000, 10000, 100000, 1000000, 1000000000};

        for (int n : testCases) {
            long start = System.nanoTime();
            int result = solution.bulbSwitch(n);
            long time = System.nanoTime() - start;

            System.out.printf("n = %,d -> %d bulbs on (time: %d ns)\n", n, result, time);
        }
        System.out.println();
    }
}

/**
 * Key Insights and Mathematical Proof
 */
class BulbSwitcherInsights {
    /*
     * Mathematical Proof:
     * ===================
     *
     * Claim: Bulb i ends up ON if and only if i is a perfect square.
     *
     * Proof:
     * ------
     * 1. Bulb i is toggled in round j if and only if j divides i
     * 2. Number of times bulb i is toggled = number of divisors of i
     * 3. A bulb ends up ON if toggled an odd number of times
     *
     * 4. When does a number have an odd number of divisors?
     *    - Divisors come in pairs: (d, n/d)
     *    - Example: 12 has divisors {1,12}, {2,6}, {3,4}
     *    - Exception: When d = n/d, i.e., d² = n
     *    - This happens when n is a perfect square
     *
     * 5. Example: n = 36
     *    - Divisors: 1, 2, 3, 4, 6, 9, 12, 18, 36
     *    - Pairs: (1,36), (2,18), (3,12), (4,9), (6,6)
     *    - The pair (6,6) counts only once!
     *    - Total: 9 divisors (odd)
     *
     * 6. Perfect squares from 1 to n: 1², 2², 3², ..., k² where k = floor(√n)
     *    - Count = k = floor(√n)
     *
     * Examples:
     * ---------
     * n = 1:  Perfect squares = {1} → 1 bulb on
     * n = 3:  Perfect squares = {1} → 1 bulb on
     * n = 4:  Perfect squares = {1, 4} → 2 bulbs on
     * n = 9:  Perfect squares = {1, 4, 9} → 3 bulbs on
     * n = 10: Perfect squares = {1, 4, 9} → 3 bulbs on
     *
     * Why Brute Force Fails:
     * =====================
     * - For n = 10⁹, need 10⁹ × 10⁹ operations
     * - That's 10¹⁸ operations → takes years!
     * - Mathematical solution: just sqrt(10⁹) ≈ 31623 → instant!
     *
     * Pattern Recognition:
     * ====================
     * n:      1  2  3  4  5  6  7  8  9  10
     * ON:     1  1  1  2  2  2  2  2  3  3
     * sqrt(n): 1  1  1  2  2  2  2  2  3  3
     *
     * Related Concepts:
     * =================
     * - Divisor function: τ(n) = number of divisors of n
     * - Perfect squares: Numbers with odd τ(n)
     * - Prime factorization: If n = p₁^a₁ × p₂^a₂ × ... × pₖ^aₖ
     *   then τ(n) = (a₁+1)(a₂+1)...(aₖ+1)
     * - τ(n) is odd ⟺ all aᵢ are even ⟺ n is perfect square
     *
     * Time Complexity Comparison:
     * ===========================
     * Brute Force: O(n²)
     * Count Squares: O(√n)
     * Mathematical: O(1)
     */
}
