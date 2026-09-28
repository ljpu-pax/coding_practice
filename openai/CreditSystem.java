package openai;

import java.util.*;

/**
 * GPU Credit Management System
 *
 * Problem:
 * You are designing a system to manage GPU credits. Each credit grant has a unique ID and is valid
 * during a specific time window. Credit grants may overlap in time.
 *
 * Because of network delays, events such as adding or consuming credits may arrive and be processed
 * out of order with respect to their timestamp. At any time, you may query the system for the total
 * available credits at a particular timestamp or attempt to revoke credits at a specified time.
 *
 * Implement the CreditSystem class:
 *
 * CreditSystem() Initializes the credit system.
 *
 * void grantCredit(String id, int amount, int startTime, int expirationTime)
 * Adds a new credit grant identified by id, with amount units. The credit is active for all times
 * in the interval [startTime, expirationTime - 1] inclusively.
 *
 * void subtract(int amount, int timestamp)
 * Revoke amount credits from all currently available credits at the specified timestamp only.
 * This operation does not affect any available credit in the future timestamp.
 *
 * int getBalance(int timestamp)
 * Returns the number of credits available at the given timestamp. If usage at the given time
 * exceeds the total available credits, return -1.
 *
 * Constraints:
 * - No two operations (grantCredit or subtract) share the same timestamp.
 * - All grants, usage events, and queries may arrive in any order.
 * - All amount, startTime, and expirationTime are in the range [0, 1000].
 *
 * Example:
 * Input:
 * ["CreditSystem", "grantCredit", "getBalance", "grantCredit", "subtract", "subtract", "getBalance",
 *  "getBalance", "getBalance", "getBalance", "getBalance", "getBalance"]
 * [[], ["a", 3, 10, 60], [10], ["b", 2, 20, 40], [1, 30], [3, 50], [10], [20], [30], [35], [40], [50]]
 *
 * Output:
 * [null, null, 3, null, null, null, 3, 5, 4, 5, 3, 0]
 *
 * Explanation:
 * CreditSystem cs = new CreditSystem();
 * cs.grantCredit("a", 3, 10, 60); // Grants 3 credits for time [10, 59].
 * cs.getBalance(10);              // Returns 3.
 * cs.grantCredit("b", 2, 20, 40); // Grants 2 credits for time [20, 39].
 * cs.subtract(1, 30);             // Revoke 1 credit at t = 30.
 * cs.subtract(3, 50);             // Revoke 3 credits at t = 50.
 * cs.getBalance(10);              // Returns 3. Only grant "a" active at t = 10.
 * cs.getBalance(20);              // Returns 5. Both "a" and "b" are active at t = 20.
 * cs.getBalance(30);              // Returns 4. Only 1 credit was revoked at t = 30.
 * cs.getBalance(35);              // Returns 5. Both "a" and "b" are active at t = 35.
 * cs.getBalance(40);              // Returns 3. Only "a" is active at t = 40.
 * cs.getBalance(50);              // Returns 0. The remaining 3 credits are revoked.
 *
 * Solution Approach:
 * - Store all credit grants with their time ranges
 * - Store all subtract operations per timestamp
 * - For getBalance(t): calculate available credits from all active grants minus subtractions at t
 *
 * Time Complexity:
 * - grantCredit: O(1)
 * - subtract: O(1)
 * - getBalance: O(n) where n is number of grants
 *
 * Space Complexity: O(n + m) where n is grants, m is subtractions
 */
public class CreditSystem {

    // Store credit grants with their details
    private static class Grant {
        String id;
        int amount;
        int startTime;
        int expirationTime;

        Grant(String id, int amount, int startTime, int expirationTime) {
            this.id = id;
            this.amount = amount;
            this.startTime = startTime;
            this.expirationTime = expirationTime;
        }

        // Check if this grant is active at given timestamp
        boolean isActiveAt(int timestamp) {
            return timestamp >= startTime && timestamp < expirationTime;
        }
    }

    // List of all credit grants
    private List<Grant> grants;

    // Map: timestamp -> total amount subtracted at that timestamp
    private Map<Integer, Integer> subtractions;

    public CreditSystem() {
        grants = new ArrayList<>();
        subtractions = new HashMap<>();
    }

    /**
     * Adds a new credit grant
     * Time: O(1)
     */
    public void grantCredit(String id, int amount, int startTime, int expirationTime) {
        grants.add(new Grant(id, amount, startTime, expirationTime));
    }

    /**
     * Revoke credits at a specific timestamp
     * Time: O(1)
     */
    public void subtract(int amount, int timestamp) {
        subtractions.put(timestamp, subtractions.getOrDefault(timestamp, 0) + amount);
    }

    /**
     * Get available credits at a given timestamp
     * Time: O(n) where n is number of grants
     */
    public int getBalance(int timestamp) {
        // Calculate total credits from all active grants at this timestamp
        int totalCredits = 0;
        for (Grant grant : grants) {
            if (grant.isActiveAt(timestamp)) {
                totalCredits += grant.amount;
            }
        }

        // Subtract any usage at this timestamp
        int usage = subtractions.getOrDefault(timestamp, 0);

        // If usage exceeds available credits, return -1
        if (usage > totalCredits) {
            return -1;
        }

        return totalCredits - usage;
    }

    /**
     * Test the solution with the provided example
     */
    public static void main(String[] args) {
        CreditSystem cs = new CreditSystem();

        cs.grantCredit("a", 3, 10, 60); // Grants 3 credits for time [10, 59]
        System.out.println(cs.getBalance(10)); // Expected: 3

        cs.grantCredit("b", 2, 20, 40); // Grants 2 credits for time [20, 39]
        cs.subtract(1, 30); // Revoke 1 credit at t = 30
        cs.subtract(3, 50); // Revoke 3 credits at t = 50

        System.out.println(cs.getBalance(10)); // Expected: 3
        System.out.println(cs.getBalance(20)); // Expected: 5
        System.out.println(cs.getBalance(30)); // Expected: 4
        System.out.println(cs.getBalance(35)); // Expected: 5
        System.out.println(cs.getBalance(40)); // Expected: 3
        System.out.println(cs.getBalance(50)); // Expected: 0

        // Additional test cases
        System.out.println("\nAdditional tests:");

        // Test edge case: query before any grants
        CreditSystem cs2 = new CreditSystem();
        System.out.println(cs2.getBalance(5)); // Expected: 0

        // Test edge case: subtract more than available
        CreditSystem cs3 = new CreditSystem();
        cs3.grantCredit("c", 5, 0, 100);
        cs3.subtract(10, 50);
        System.out.println(cs3.getBalance(50)); // Expected: -1 (usage exceeds available)

        // Test overlapping grants
        CreditSystem cs4 = new CreditSystem();
        cs4.grantCredit("d", 10, 0, 50);
        cs4.grantCredit("e", 5, 25, 75);
        System.out.println(cs4.getBalance(30)); // Expected: 15 (both active)
        System.out.println(cs4.getBalance(60)); // Expected: 5 (only "e" active)
    }
}
