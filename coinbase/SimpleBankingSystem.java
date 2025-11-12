package coinbase;

import java.util.*;

/**
 * SimpleBankingSystem - A simplified banking system implementation
 * 
 * This class provides basic operations for account creation, deposits, and transfers.
 * Matches the exact specification provided for the coding interview.
 * 
 * Example usage:
 * SimpleBankingSystem bank = new SimpleBankingSystem();
 * boolean created = bank.createAccount(1, "account1"); // returns true
 * Optional<Integer> balance = bank.deposit(5, "account1", 2700); // returns Optional.of(2700)
 * Optional<Integer> result = bank.transfer(7, "account1", "account2", 200); // returns Optional.of(2500)
 */
public class SimpleBankingSystem {
    
    // Map to store accounts: accountId -> balance
    private Map<String, Integer> accounts;
    
    /**
     * Initializes the banking system with no accounts.
     */
    public SimpleBankingSystem() {
        this.accounts = new HashMap<>();
    }
    
    /**
     * Creates a new account with the given identifier if it does not already exist.
     * 
     * @param timestamp the timestamp when the account is created
     * @param accountId the unique identifier for the account
     * @return true if the account was successfully created, false if account already exists
     * 
     * Example:
     * createAccount(1, "account1") -> returns true
     * createAccount(2, "account1") -> returns false (already exists)
     */
    public boolean createAccount(int timestamp, String accountId) {
        // Check if account already exists
        if (accounts.containsKey(accountId)) {
            return false; // Account already exists
        }
        
        // Create new account with zero balance
        accounts.put(accountId, 0);
        return true; // Account created successfully
    }
    
    /**
     * Deposits the given amount of money to the specified account.
     * 
     * @param timestamp the timestamp of the deposit
     * @param accountId the account identifier
     * @param amount the amount to deposit
     * @return the total amount of money in the account after the query, or Optional.empty() if account doesn't exist
     * 
     * Example:
     * deposit(5, "account1", 2700) -> returns Optional.of(2700)
     * deposit(4, "non-existing", 2700) -> returns Optional.empty()
     */
    public Optional<Integer> deposit(int timestamp, String accountId, int amount) {
        // Check if account exists
        if (!accounts.containsKey(accountId)) {
            return Optional.empty(); // Account doesn't exist
        }
        
        // Update account balance
        int currentBalance = accounts.get(accountId);
        int newBalance = currentBalance + amount;
        accounts.put(accountId, newBalance);
        
        return Optional.of(newBalance);
    }
    
    /**
     * Transfers the given amount of money from source account to target account.
     * 
     * @param timestamp the timestamp of the transfer
     * @param sourceAccountId the source account identifier
     * @param targetAccountId the target account identifier
     * @param amount the amount to transfer
     * @return the balance of sourceAccountId if transfer successful, or Optional.empty() otherwise
     * 
     * Returns Optional.empty() if:
     * - sourceAccountId or targetAccountId doesn't exist
     * - sourceAccountId and targetAccountId are the same
     * - sourceAccountId has insufficient funds
     * 
     * Example:
     * transfer(7, "account1", "account2", 200) -> returns Optional.of(2500)
     * transfer(6, "account1", "account2", 2701) -> returns Optional.empty() (insufficient funds)
     * transfer(8, "non-existing", "account2", 500) -> returns Optional.empty() (account doesn't exist)
     */
    public Optional<Integer> transfer(int timestamp, String sourceAccountId, String targetAccountId, int amount) {
        // Check if source and target accounts exist
        if (!accounts.containsKey(sourceAccountId) || !accounts.containsKey(targetAccountId)) {
            return Optional.empty(); // One or both accounts don't exist
        }
        
        // Check if source and target are the same account
        if (sourceAccountId.equals(targetAccountId)) {
            return Optional.empty(); // Cannot transfer to same account
        }
        
        // Check if source account has sufficient funds
        int sourceBalance = accounts.get(sourceAccountId);
        if (sourceBalance < amount) {
            return Optional.empty(); // Insufficient funds
        }
        
        // Perform the transfer
        int targetBalance = accounts.get(targetAccountId);
        accounts.put(sourceAccountId, sourceBalance - amount);
        accounts.put(targetAccountId, targetBalance + amount);
        
        // Return the new balance of source account
        return Optional.of(sourceBalance - amount);
    }
    
    /**
     * Gets the current balance of an account.
     * 
     * @param accountId the account identifier
     * @return the account balance, or Optional.empty() if account doesn't exist
     */
    public Optional<Integer> getBalance(String accountId) {
        if (!accounts.containsKey(accountId)) {
            return Optional.empty();
        }
        return Optional.of(accounts.get(accountId));
    }
    
    /**
     * Checks if an account exists.
     * 
     * @param accountId the account identifier
     * @return true if account exists, false otherwise
     */
    public boolean hasAccount(String accountId) {
        return accounts.containsKey(accountId);
    }
    
    /**
     * Gets all account IDs in the system.
     * 
     * @return set of all account IDs
     */
    public Set<String> getAllAccountIds() {
        return new HashSet<>(accounts.keySet());
    }
    
    /**
     * Gets the total number of accounts.
     * 
     * @return number of accounts
     */
    public int getAccountCount() {
        return accounts.size();
    }
    
    /**
     * Main method with example usage matching the provided specification
     */
    public static void main(String[] args) {
        SimpleBankingSystem bank = new SimpleBankingSystem();
        
        System.out.println("=== SimpleBankingSystem Example Usage ===");
        System.out.println("Following the exact specification provided:");
        
        // Test createAccount
        System.out.println("\n1. Creating accounts:");
        System.out.println("createAccount(1, \"account1\"): " + bank.createAccount(1, "account1"));
        System.out.println("createAccount(2, \"account1\"): " + bank.createAccount(2, "account1")); // Should return false
        System.out.println("createAccount(3, \"account2\"): " + bank.createAccount(3, "account2"));
        
        // Test deposit
        System.out.println("\n2. Deposit operations:");
        System.out.println("deposit(4, \"non-existing\", 2700): " + bank.deposit(4, "non-existing", 2700));
        System.out.println("deposit(5, \"account1\", 2700): " + bank.deposit(5, "account1", 2700));
        
        // Test transfer
        System.out.println("\n3. Transfer operations:");
        System.out.println("transfer(6, \"account1\", \"account2\", 2701): " + bank.transfer(6, "account1", "account2", 2701)); // Should return empty (insufficient funds)
        System.out.println("transfer(7, \"account1\", \"account2\", 200): " + bank.transfer(7, "account1", "account2", 200));
        System.out.println("transfer(8, \"non-existing\", \"account2\", 500): " + bank.transfer(8, "non-existing", "account2", 500));
        
        // Show final balances
        System.out.println("\n4. Final account balances:");
        System.out.println("account1 balance: " + bank.getBalance("account1"));
        System.out.println("account2 balance: " + bank.getBalance("account2"));
        
        // Additional test cases
        System.out.println("\n5. Additional test cases:");
        System.out.println("transfer(9, \"account1\", \"account1\", 100): " + bank.transfer(9, "account1", "account1", 100)); // Same account
        System.out.println("deposit(10, \"account2\", 500): " + bank.deposit(10, "account2", 500));
        System.out.println("transfer(11, \"account2\", \"account1\", 1000): " + bank.transfer(11, "account2", "account1", 1000));
        
        System.out.println("\n6. Final balances after additional operations:");
        System.out.println("account1 balance: " + bank.getBalance("account1"));
        System.out.println("account2 balance: " + bank.getBalance("account2"));
        System.out.println("Total accounts: " + bank.getAccountCount());
    }
}
