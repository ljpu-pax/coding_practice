import java.util.*;

/**
 * Problem: Implement an in-memory key-value store with nested transactions
 *
 * Commands:
 * - SET <key> <value>: Set the value of a key
 * - RETURN <key>: Return the value of a key
 * - BEGIN: Start a new transaction block
 * - APPLY: Commit/apply the current transaction to parent scope
 * - DISCARD: Rollback/discard the current transaction
 *
 * Key Challenge: Handle nested transactions with proper variable scoping
 *
 * Example:
 * SET X 20
 * BEGIN
 *   BEGIN
 *     SET X 10
 *     RETURN X    -> 10
 *   APPLY
 * DISCARD
 * RETURN X        -> 20
 *
 * Explanation:
 * - Innermost BEGIN sets X=10, returns 10
 * - APPLY commits X=10 to outer BEGIN scope
 * - DISCARD rolls back outer BEGIN, X reverts to 20
 * - Final RETURN X returns 20
 */
public class TransactionalKeyValueStore {

    // Stack of scopes - each scope is a map of variables
    // Bottom of stack is global scope
    private Stack<Map<String, String>> scopeStack;

    public TransactionalKeyValueStore() {
        scopeStack = new Stack<>();
        // Initialize with global scope
        scopeStack.push(new HashMap<>());
    }

    /**
     * SET <key> <value>
     * Sets variable in current scope
     */
    public void set(String key, String value) {
        Map<String, String> currentScope = scopeStack.peek();
        currentScope.put(key, value);
    }

    /**
     * RETURN <key>
     * Returns value of key by searching from current scope up to global
     */
    public String returnValue(String key) {
        // Search from current scope backwards to global scope
        for (int i = scopeStack.size() - 1; i >= 0; i--) {
            Map<String, String> scope = scopeStack.get(i);
            if (scope.containsKey(key)) {
                return scope.get(key);
            }
        }
        return "NULL"; // Variable not found
    }

    /**
     * BEGIN
     * Start a new transaction/scope
     */
    public void begin() {
        // Create new empty scope
        scopeStack.push(new HashMap<>());
    }

    /**
     * APPLY
     * Commit current transaction to parent scope
     */
    public boolean apply() {
        if (scopeStack.size() <= 1) {
            return false; // Can't apply global scope
        }

        // Pop current scope
        Map<String, String> currentScope = scopeStack.pop();

        // Merge into parent scope
        Map<String, String> parentScope = scopeStack.peek();
        for (Map.Entry<String, String> entry : currentScope.entrySet()) {
            parentScope.put(entry.getKey(), entry.getValue());
        }

        return true;
    }

    /**
     * DISCARD
     * Rollback/discard current transaction
     */
    public boolean discard() {
        if (scopeStack.size() <= 1) {
            return false; // Can't discard global scope
        }

        // Simply pop the current scope - changes are lost
        scopeStack.pop();
        return true;
    }

    /**
     * Process a single command
     */
    public String processCommand(String command) {
        String[] parts = command.trim().split("\\s+");
        if (parts.length == 0) {
            return "ERROR: Empty command";
        }

        String cmd = parts[0].toUpperCase();

        switch (cmd) {
            case "SET":
                if (parts.length >= 3) {
                    set(parts[1], parts[2]);
                    return "OK";
                }
                return "ERROR: Invalid SET command";

            case "RETURN":
                if (parts.length >= 2) {
                    return returnValue(parts[1]);
                }
                return "ERROR: Invalid RETURN command";

            case "BEGIN":
                begin();
                return "Transaction started";

            case "APPLY":
                return apply() ? "Transaction applied" : "ERROR: No transaction to apply";

            case "DISCARD":
                return discard() ? "Transaction discarded" : "ERROR: No transaction to discard";

            default:
                return "ERROR: Unknown command";
        }
    }

    /**
     * Process multiple commands
     */
    public List<String> processCommands(String[] commands) {
        List<String> results = new ArrayList<>();
        for (String cmd : commands) {
            String result = processCommand(cmd);
            if (result.startsWith("RETURN:") || cmd.trim().toUpperCase().startsWith("RETURN")) {
                results.add(result);
            }
        }
        return results;
    }

    // Test examples
    public static void main(String[] args) {
        System.out.println("=== Example 1: Given Example ===");
        TransactionalKeyValueStore store1 = new TransactionalKeyValueStore();
        store1.set("X", "20");
        store1.begin();
        store1.begin();
        store1.set("X", "10");
        System.out.println("RETURN X: " + store1.returnValue("X")); // 10
        store1.apply();
        store1.discard();
        System.out.println("RETURN X: " + store1.returnValue("X")); // 20

        System.out.println("\n=== Example 2: Simple Transaction ===");
        TransactionalKeyValueStore store2 = new TransactionalKeyValueStore();
        store2.set("A", "5");
        store2.begin();
        store2.set("A", "10");
        System.out.println("RETURN A (in transaction): " + store2.returnValue("A")); // 10
        store2.discard();
        System.out.println("RETURN A (after discard): " + store2.returnValue("A")); // 5

        System.out.println("\n=== Example 3: Apply Transaction ===");
        TransactionalKeyValueStore store3 = new TransactionalKeyValueStore();
        store3.set("B", "100");
        store3.begin();
        store3.set("B", "200");
        System.out.println("RETURN B (in transaction): " + store3.returnValue("B")); // 200
        store3.apply();
        System.out.println("RETURN B (after apply): " + store3.returnValue("B")); // 200

        System.out.println("\n=== Example 4: Triple Nested ===");
        TransactionalKeyValueStore store4 = new TransactionalKeyValueStore();
        store4.set("Y", "1");
        store4.begin();        // Level 1
        store4.set("Y", "2");
        store4.begin();        // Level 2
        store4.set("Y", "3");
        store4.begin();        // Level 3
        store4.set("Y", "4");
        System.out.println("RETURN Y (level 3): " + store4.returnValue("Y")); // 4
        store4.apply();        // Level 3 -> Level 2
        System.out.println("RETURN Y (level 2): " + store4.returnValue("Y")); // 4
        store4.apply();        // Level 2 -> Level 1
        System.out.println("RETURN Y (level 1): " + store4.returnValue("Y")); // 4
        store4.discard();      // Discard level 1
        System.out.println("RETURN Y (global): " + store4.returnValue("Y")); // 1

        System.out.println("\n=== Example 5: Multiple Variables ===");
        TransactionalKeyValueStore store5 = new TransactionalKeyValueStore();
        store5.set("A", "10");
        store5.set("B", "20");
        store5.begin();
        store5.set("A", "30");
        store5.set("C", "40");
        System.out.println("RETURN A: " + store5.returnValue("A")); // 30
        System.out.println("RETURN B: " + store5.returnValue("B")); // 20 (from global)
        System.out.println("RETURN C: " + store5.returnValue("C")); // 40
        store5.discard();
        System.out.println("RETURN A: " + store5.returnValue("A")); // 10
        System.out.println("RETURN C: " + store5.returnValue("C")); // NULL

        System.out.println("\n=== Example 6: Reassignment in Same Transaction ===");
        TransactionalKeyValueStore store6 = new TransactionalKeyValueStore();
        store6.set("X", "100");
        store6.begin();
        store6.set("X", "200");
        store6.set("X", "300"); // Reassign in same scope
        System.out.println("RETURN X: " + store6.returnValue("X")); // 300
        store6.apply();
        System.out.println("RETURN X: " + store6.returnValue("X")); // 300

        System.out.println("\n=== Example 7: Mixed Apply and Discard ===");
        TransactionalKeyValueStore store7 = new TransactionalKeyValueStore();
        store7.set("VAL", "original");
        store7.begin();
        store7.set("VAL", "tx1");
        store7.begin();
        store7.set("VAL", "tx2");
        store7.apply();  // tx2 applies to tx1
        System.out.println("RETURN VAL: " + store7.returnValue("VAL")); // tx2
        store7.begin();
        store7.set("VAL", "tx3");
        store7.discard(); // tx3 discarded
        System.out.println("RETURN VAL: " + store7.returnValue("VAL")); // tx2
        store7.apply(); // tx1 (with tx2 changes) applies to global
        System.out.println("RETURN VAL: " + store7.returnValue("VAL")); // tx2
    }
}

/*
 * Key Implementation Details:
 *
 * 1. Scope Stack Approach:
 *    - Use a stack where each level represents a scope (transaction)
 *    - Bottom of stack is always the global scope
 *    - Each BEGIN pushes a new scope
 *
 * 2. Variable Lookup:
 *    - Search from current scope backwards to global scope
 *    - Return first match found (shadowing behavior)
 *
 * 3. APPLY (Commit):
 *    - Pop current scope
 *    - Merge all key-value pairs into parent scope
 *    - Variables set in current scope override parent
 *
 * 4. DISCARD (Rollback):
 *    - Simply pop current scope
 *    - All changes in that scope are lost
 *
 * 5. Nested Transactions:
 *    - Each BEGIN creates a new scope level
 *    - APPLY merges into immediate parent only
 *    - DISCARD affects only current level
 *
 * Time Complexity:
 * - SET: O(1)
 * - RETURN: O(d) where d is transaction depth (usually small)
 * - BEGIN: O(1)
 * - APPLY: O(k) where k is number of variables in current scope
 * - DISCARD: O(1)
 *
 * Space Complexity:
 * - O(n * d) where n is number of variables, d is transaction depth
 * - In practice, most variables are only in specific scopes
 *
 * Alternative Approach:
 * - Could use copy-on-write for each scope
 * - Could track deltas instead of full scopes
 * - This approach is simpler and efficient for typical use cases
 */
