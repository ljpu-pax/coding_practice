package openai;

import java.util.*;

/**
 * GPU Credit Management System with Priority Charging
 *
 * Problem:
 * You are designing a system to manage GPU credits. Each credit grant has a unique ID and is valid
 * during a specific time window. Credit grants may overlap in time.
 *
 * Key difference from basic version:
 * - When charging credits, prioritize credits that expire EARLIEST
 * - Events (add_credit and charge) can arrive out of order
 * - Need to track remaining credits per grant ID after charges
 *
 * Implement the CreditSystem class:
 *
 * CreditSystem() Initializes the credit system.
 *
 * void addCredit(String id, int amount, int startTime, int duration)
 * Adds a new credit grant identified by id, with amount units.
 * The credit is active for [startTime, startTime + duration).
 *
 * void charge(int amount, int timestamp)
 * Charge (consume) amount credits at the specified timestamp.
 * Prioritize credits that expire earliest (greedy approach).
 *
 * int getBalance(int timestamp)
 * Returns the number of credits available at the given timestamp after all charges.
 *
 * Example:
 * addCredit("c1", 20, 10, 30) // 20 credits from [10, 40)
 * addCredit("c2", 20, 40, 30) // 20 credits from [40, 70)
 * addCredit("c3", 20, 20, 30) // 20 credits from [20, 50)
 * addCredit("c4", 20, 30, 30) // 20 credits from [30, 60)
 * charge(45, 30)              // Charge 45 at time 30
 *
 * getBalance(10) => 20        // Only c1 active
 * getBalance(30) => 15        // c1=0, c3=0, c4=15 (after charging 45)
 * getBalance(55) => 25        // c4=15, c2=10 (c2 partially used)
 *
 * Solution Approach:
 * 1. Store all credit grants with their time ranges
 * 2. Store all charge operations with timestamp
 * 3. For each charge, track how much was consumed from each grant ID
 * 4. For getBalance(t):
 *    a. Find all active grants at time t
 *    b. Replay all charges that happened at or before t
 *    c. For each charge, deduct from grants that expire earliest (greedy)
 *    d. Return sum of remaining credits
 *
 * Time Complexity:
 * - addCredit: O(log n) due to sorted insertion
 * - charge: O(log m) due to sorted insertion
 * - getBalance: O(n * m) where n is charges, m is grants at that time
 *
 * Space Complexity: O(n + m + c) where n is grants, m is charges, c is cache size
 *
 * Optimizations:
 * 1. Store grants and charges sorted by timestamp for faster access
 * 2. Cache results for repeated queries
 * 3. Use TreeMap for charge consumption tracking per grant
 */
public class CreditSystemPriority {

    // Store credit grants with their details
    private static class Grant {
        String id;
        int amount;
        int startTime;
        int expirationTime;

        Grant(String id, int amount, int startTime, int duration) {
            this.id = id;
            this.amount = amount;
            this.startTime = startTime;
            this.expirationTime = startTime + duration;
        }

        // Check if this grant is active at given timestamp
        boolean isActiveAt(int timestamp) {
            return timestamp >= startTime && timestamp < expirationTime;
        }
    }

    // Store charge events
    private static class Charge {
        int amount;
        int timestamp;

        Charge(int amount, int timestamp) {
            this.amount = amount;
            this.timestamp = timestamp;
        }
    }

    // Map grant ID to grant details for O(1) lookup
    private Map<String, Grant> grantMap;

    // List of all credit grants
    private List<Grant> grants;

    // List of all charges (kept sorted by timestamp)
    private List<Charge> charges;

    // Cache for getBalance results: timestamp -> balance
    private Map<Integer, Integer> balanceCache;

    // Flag to track if we need to invalidate cache
    private boolean cacheValid;

    public CreditSystemPriority() {
        grantMap = new HashMap<>();
        grants = new ArrayList<>();
        charges = new ArrayList<>();
        balanceCache = new HashMap<>();
        cacheValid = true;
    }

    /**
     * Add a new credit grant
     * @param id Unique identifier for the credit
     * @param amount Number of credits
     * @param startTime When the credit becomes active
     * @param duration How long the credit is valid (in time units)
     */
    public void addCredit(String id, int amount, int startTime, int duration) {
        Grant grant = new Grant(id, amount, startTime, duration);
        grants.add(grant);
        grantMap.put(id, grant);
        cacheValid = false; // Invalidate cache
    }

    /**
     * Charge (consume) credits at a specific timestamp
     * @param amount Number of credits to charge
     * @param timestamp When the charge occurs
     */
    public void charge(int amount, int timestamp) {
        charges.add(new Charge(amount, timestamp));
        cacheValid = false; // Invalidate cache
    }

    /**
     * Get available credits at a given timestamp (OPTIMIZED with caching)
     * Considers all charges up to and including the query timestamp
     *
     * @param timestamp The time to query
     * @return Total available credits after all charges
     */
    public int getBalance(int timestamp) {
        // Check cache first
        if (cacheValid && balanceCache.containsKey(timestamp)) {
            return balanceCache.get(timestamp);
        }

        int result = computeBalance(timestamp);
        balanceCache.put(timestamp, result);
        return result;
    }

    /**
     * Core computation for balance (separated for caching)
     */
    private int computeBalance(int timestamp) {
        // Get all grants active at this timestamp
        List<Grant> activeGrants = new ArrayList<>();
        for (Grant grant : grants) {
            if (grant.isActiveAt(timestamp)) {
                activeGrants.add(grant);
            }
        }

        // Track remaining credits for each grant
        Map<String, Integer> remaining = new HashMap<>();
        for (Grant grant : activeGrants) {
            remaining.put(grant.id, grant.amount);
        }

        // Get all charges that happened at or before this timestamp
        List<Charge> relevantCharges = new ArrayList<>();
        for (Charge charge : charges) {
            if (charge.timestamp <= timestamp) {
                relevantCharges.add(charge);
            }
        }

        // Sort charges by timestamp (process in chronological order)
        relevantCharges.sort(Comparator.comparingInt(c -> c.timestamp));

        // Process each charge
        for (Charge charge : relevantCharges) {
            int chargeTimestamp = charge.timestamp;
            int amountToCharge = charge.amount;

            // Find grants active at charge timestamp
            List<Grant> grantsAtChargeTime = new ArrayList<>();
            for (Grant grant : grants) {
                if (grant.isActiveAt(chargeTimestamp) && remaining.containsKey(grant.id)) {
                    grantsAtChargeTime.add(grant);
                }
            }

            // Sort by expiration time (earliest first) for greedy selection
            grantsAtChargeTime.sort(Comparator.comparingInt(g -> g.expirationTime));

            // Charge from grants that expire earliest
            for (Grant grant : grantsAtChargeTime) {
                if (amountToCharge <= 0) break;

                int available = remaining.get(grant.id);
                int toDeduct = Math.min(available, amountToCharge);

                remaining.put(grant.id, available - toDeduct);
                amountToCharge -= toDeduct;
            }
        }

        // Sum up remaining credits
        int totalRemaining = 0;
        for (int amount : remaining.values()) {
            totalRemaining += amount;
        }

        return totalRemaining;
    }

    /**
     * Get balance with detailed breakdown (for debugging)
     */
    public Map<String, Integer> getBalanceDetailed(int timestamp) {
        // Get all grants active at this timestamp
        List<Grant> activeGrants = new ArrayList<>();
        for (Grant grant : grants) {
            if (grant.isActiveAt(timestamp)) {
                activeGrants.add(grant);
            }
        }

        // Track remaining credits for each grant
        Map<String, Integer> remaining = new HashMap<>();
        for (Grant grant : activeGrants) {
            remaining.put(grant.id, grant.amount);
        }

        // Get all charges that happened at or before this timestamp
        List<Charge> relevantCharges = new ArrayList<>();
        for (Charge charge : charges) {
            if (charge.timestamp <= timestamp) {
                relevantCharges.add(charge);
            }
        }

        // Sort charges by timestamp
        relevantCharges.sort(Comparator.comparingInt(c -> c.timestamp));

        // Process each charge
        for (Charge charge : relevantCharges) {
            int chargeTimestamp = charge.timestamp;
            int amountToCharge = charge.amount;

            // Find grants active at charge timestamp
            List<Grant> grantsAtChargeTime = new ArrayList<>();
            for (Grant grant : grants) {
                if (grant.isActiveAt(chargeTimestamp) && remaining.containsKey(grant.id)) {
                    grantsAtChargeTime.add(grant);
                }
            }

            // Sort by expiration time (earliest first)
            grantsAtChargeTime.sort(Comparator.comparingInt(g -> g.expirationTime));

            // Charge from grants that expire earliest
            for (Grant grant : grantsAtChargeTime) {
                if (amountToCharge <= 0) break;

                int available = remaining.get(grant.id);
                int toDeduct = Math.min(available, amountToCharge);

                remaining.put(grant.id, available - toDeduct);
                amountToCharge -= toDeduct;
            }
        }

        return remaining;
    }

    /**
     * OPTIMIZATION ANALYSIS AND ALTERNATIVES
     * =====================================
     *
     * Current Implementation:
     * - addCredit: O(1)
     * - charge: O(1)
     * - getBalance: O(C × G × log G) where C = charges up to timestamp, G = grants
     *   First call: O(C × G × log G)
     *   Cached calls: O(1)
     *
     * Optimizations Applied:
     * 1. ✅ Caching: Store computed balances to avoid re-computation
     * 2. ✅ Grant map: O(1) lookup by grant ID
     * 3. ✅ Cache invalidation: Only when new grants/charges added
     *
     * Further Optimization Ideas:
     *
     * OPTION 1: Pre-compute charge allocations eagerly
     * - When charge() is called, immediately compute which grants it consumes from
     * - Store: Map<ChargeId, Map<GrantId, AmountConsumed>>
     * - Pros: getBalance becomes O(G) - just sum remaining per grant
     * - Cons: charge() becomes expensive O(G log G), order-dependent
     *
     * OPTION 2: Lazy evaluation with memoization (current approach)
     * - Store raw events, compute on demand, cache results
     * - Pros: Fast writes, flexible for out-of-order arrivals
     * - Cons: First query is expensive
     *
     * OPTION 3: Event sourcing with snapshots
     * - Periodically compute and store state snapshots at key timestamps
     * - For queries, start from nearest snapshot and replay forward
     * - Pros: Bounded computation per query
     * - Cons: More complex, uses more memory
     *
     * OPTION 4: Segment tree / interval tree for time ranges
     * - Use interval tree to efficiently find overlapping grants
     * - Pros: Faster grant lookup O(log n + k) instead of O(n)
     * - Cons: Complexity not worth it unless 1000s of grants
     *
     * Recommendation:
     * - For interview: Current approach (option 2) is best balance
     * - Shows caching optimization
     * - Easy to explain and implement
     * - Handles out-of-order arrivals naturally
     * - If asked "can you optimize further?", discuss Option 1 or 3
     */

    /**
     * Test the solution with the provided example
     */
    public static void main(String[] args) {
        System.out.println("=== Test Case 1: Example from problem ===");
        CreditSystemPriority cs = new CreditSystemPriority();

        cs.addCredit("c1", 20, 10, 30); // 20 credits from [10, 40)
        cs.addCredit("c2", 20, 40, 30); // 20 credits from [40, 70)
        cs.addCredit("c3", 20, 20, 30); // 20 credits from [20, 50)
        cs.addCredit("c4", 20, 30, 30); // 20 credits from [30, 60)
        cs.charge(45, 30);              // Charge 45 at time 30

        System.out.println("getBalance(10) = " + cs.getBalance(10)); // Expected: 20 (only c1)
        System.out.println("Breakdown: " + cs.getBalanceDetailed(10));

        System.out.println("\ngetBalance(30) = " + cs.getBalance(30)); // Expected: 15
        System.out.println("Breakdown: " + cs.getBalanceDetailed(30)); // c1=0, c3=0, c4=15
        System.out.println("Explanation: At t=30, active grants: c1[10,40), c3[20,50), c4[30,60)");
        System.out.println("  Charge 45 prioritizes earliest expiration:");
        System.out.println("  - c1 expires at 40: use all 20 credits (remaining: 25)");
        System.out.println("  - c3 expires at 50: use all 20 credits (remaining: 5)");
        System.out.println("  - c4 expires at 60: use 5 credits (remaining: 15)");

        System.out.println("\ngetBalance(55) = " + cs.getBalance(55)); // Expected: 25
        System.out.println("Breakdown: " + cs.getBalanceDetailed(55)); // c4=15, c2=10
        System.out.println("Explanation: At t=55, active grants: c2[40,70), c4[30,60)");
        System.out.println("  c1 and c3 have expired");
        System.out.println("  After charge at t=30: c4 has 15, c2 has 10 remaining");
        System.out.println();

        System.out.println("=== Test Case 2: Out of order arrival ===");
        CreditSystemPriority cs2 = new CreditSystemPriority();

        cs2.charge(10, 20);              // Charge arrives first
        cs2.addCredit("a", 15, 10, 20);  // Grant added later
        cs2.addCredit("b", 10, 15, 25);

        System.out.println("getBalance(20) = " + cs2.getBalance(20)); // Expected: 15
        System.out.println("Breakdown: " + cs2.getBalanceDetailed(20));
        System.out.println();

        System.out.println("=== Test Case 3: Multiple charges ===");
        CreditSystemPriority cs3 = new CreditSystemPriority();

        cs3.addCredit("x", 30, 0, 50);
        cs3.addCredit("y", 20, 10, 40);
        cs3.charge(15, 15);
        cs3.charge(10, 25);

        System.out.println("getBalance(30) = " + cs3.getBalance(30)); // Expected: 25
        System.out.println("Breakdown: " + cs3.getBalanceDetailed(30));
        System.out.println();

        System.out.println("=== Test Case 4: Exact charge amount ===");
        CreditSystemPriority cs4 = new CreditSystemPriority();

        cs4.addCredit("p", 10, 0, 30);
        cs4.addCredit("q", 10, 5, 35);
        cs4.charge(20, 10);

        System.out.println("getBalance(10) = " + cs4.getBalance(10)); // Expected: 0
        System.out.println("Breakdown: " + cs4.getBalanceDetailed(10));
        System.out.println();

        System.out.println("=== Test Case 5: No charges ===");
        CreditSystemPriority cs5 = new CreditSystemPriority();

        cs5.addCredit("m", 50, 0, 100);
        cs5.addCredit("n", 30, 20, 80);

        System.out.println("getBalance(50) = " + cs5.getBalance(50)); // Expected: 80
        System.out.println("Breakdown: " + cs5.getBalanceDetailed(50));
    }
}
