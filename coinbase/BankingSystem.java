package coinbase;

import java.util.*;

/**
 * BankingSystem - A banking system implementation for managing accounts
 * 
 * This class provides functionality to create and manage bank accounts.
 * Each account has a unique accountId and maintains a balance.
 * 
 * Example usage:
 * BankingSystem bank = new BankingSystem();
 * boolean created = bank.createAccount(1234567890, "ACC001"); // returns true
 * boolean duplicate = bank.createAccount(1234567891, "ACC001"); // returns false
 */
public class BankingSystem {
    
    // Map to store accounts: accountId -> Account
    private Map<String, Account> accounts;
    
    // List to store scheduled transfers
    private List<ScheduledTransfer> scheduledTransfers;
    
    /**
     * Account class to represent a bank account
     */
    private static class Account {
        private String accountId;
        private double balance;
        private int createdAt;
        private double totalTransactedOut; // Track total amount transacted out
        
        public Account(String accountId, int timestamp) {
            this.accountId = accountId;
            this.balance = 0.0;
            this.createdAt = timestamp;
            this.totalTransactedOut = 0.0;
        }
        
        // Getters
        public String getAccountId() { return accountId; }
        public double getBalance() { return balance; }
        public int getCreatedAt() { return createdAt; }
        public double getTotalTransactedOut() { return totalTransactedOut; }
        
        // Setters
        public void setBalance(double balance) { this.balance = balance; }
        public void addTransactedOut(double amount) { this.totalTransactedOut += amount; }
    }
    
    /**
     * ScheduledTransfer class to represent TTL transfers
     */
    private static class ScheduledTransfer {
        private String fromAccountId;
        private String toAccountId;
        private double amount;
        private int executeAt;
        private boolean executed;
        
        public ScheduledTransfer(String fromAccountId, String toAccountId, double amount, int executeAt) {
            this.fromAccountId = fromAccountId;
            this.toAccountId = toAccountId;
            this.amount = amount;
            this.executeAt = executeAt;
            this.executed = false;
        }
        
        // Getters
        public String getFromAccountId() { return fromAccountId; }
        public String getToAccountId() { return toAccountId; }
        public double getAmount() { return amount; }
        public int getExecuteAt() { return executeAt; }
        public boolean isExecuted() { return executed; }
        
        // Setters
        public void setExecuted(boolean executed) { this.executed = executed; }
    }
    
    /**
     * Initializes the banking system with no accounts.
     */
    public BankingSystem() {
        this.accounts = new HashMap<>();
        this.scheduledTransfers = new ArrayList<>();
    }
    
    /**
     * Creates a new account identified by accountId with a zero balance.
     * 
     * @param timestamp the timestamp when the account is created
     * @param accountId the unique identifier for the account
     * @return true if the account is created successfully
     * @return false if an account with the same accountId already exists
     * 
     * Example:
     * BankingSystem bank = new BankingSystem();
     * boolean success = bank.createAccount(1234567890, "ACC001"); // returns true
     * boolean duplicate = bank.createAccount(1234567891, "ACC001"); // returns false
     */
    public boolean createAccount(int timestamp, String accountId) {
        // Check if account with this ID already exists
        if (accounts.containsKey(accountId)) {
            return false; // Account already exists
        }
        
        // Create new account with zero balance
        Account newAccount = new Account(accountId, timestamp);
        accounts.put(accountId, newAccount);
        return true; // Account created successfully
    }
    
    /**
     * Gets the balance of an account.
     * 
     * @param accountId the account identifier
     * @return the account balance, or -1 if account doesn't exist
     */
    public double getBalance(String accountId) {
        Account account = accounts.get(accountId);
        return account != null ? account.getBalance() : -1;
    }
    
    /**
     * Deposits money into an account.
     * 
     * @param timestamp the timestamp of the deposit
     * @param accountId the account identifier
     * @param amount the amount to deposit
     * @return true if deposit successful, false if account doesn't exist
     */
    public boolean deposit(int timestamp, String accountId, double amount) {
        Account account = accounts.get(accountId);
        if (account == null) {
            return false; // Account doesn't exist
        }
        
        account.setBalance(account.getBalance() + amount);
        return true;
    }
    
    /**
     * Withdraws money from an account.
     * 
     * @param timestamp the timestamp of the withdrawal
     * @param accountId the account identifier
     * @param amount the amount to withdraw
     * @return true if withdrawal successful, false if account doesn't exist or insufficient funds
     */
    public boolean withdraw(int timestamp, String accountId, double amount) {
        Account account = accounts.get(accountId);
        if (account == null) {
            return false; // Account doesn't exist
        }
        
        if (account.getBalance() < amount) {
            return false; // Insufficient funds
        }
        
        account.setBalance(account.getBalance() - amount);
        account.addTransactedOut(amount); // Track transacted out amount
        return true;
    }
    
    /**
     * Transfers money from one account to another.
     * 
     * @param timestamp the timestamp of the transfer
     * @param fromAccountId the source account identifier
     * @param toAccountId the destination account identifier
     * @param amount the amount to transfer
     * @return true if transfer successful, false if either account doesn't exist or insufficient funds
     */
    public boolean transfer(int timestamp, String fromAccountId, String toAccountId, double amount) {
        Account fromAccount = accounts.get(fromAccountId);
        Account toAccount = accounts.get(toAccountId);
        
        if (fromAccount == null || toAccount == null) {
            return false; // One or both accounts don't exist
        }
        
        if (fromAccount.getBalance() < amount) {
            return false; // Insufficient funds
        }
        
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        fromAccount.addTransactedOut(amount); // Track transacted out amount
        toAccount.setBalance(toAccount.getBalance() + amount);
        return true;
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
     * Gets account information.
     * 
     * @param accountId the account identifier
     * @return account info string, or null if account doesn't exist
     */
    public String getAccountInfo(String accountId) {
        Account account = accounts.get(accountId);
        if (account == null) {
            return null;
        }
        
        return String.format("Account ID: %s, Balance: $%.2f, Created: %d, Transacted Out: $%.2f", 
                           account.getAccountId(), account.getBalance(), account.getCreatedAt(), 
                           account.getTotalTransactedOut());
    }
    
    /**
     * Returns the account ID with the maximum transacted out amount.
     * 
     * @return account ID with max transacted out amount, or null if no accounts
     */
    public String getMaxTransactedOutAccount() {
        if (accounts.isEmpty()) {
            return null;
        }
        
        String maxAccountId = null;
        double maxAmount = -1;
        
        for (Account account : accounts.values()) {
            if (account.getTotalTransactedOut() > maxAmount) {
                maxAmount = account.getTotalTransactedOut();
                maxAccountId = account.getAccountId();
            }
        }
        
        return maxAccountId;
    }
    
    /**
     * Schedules a transfer to be executed at a future timestamp (TTL).
     * 
     * @param fromAccountId the source account identifier
     * @param toAccountId the destination account identifier
     * @param amount the amount to transfer
     * @param executeAt the timestamp when the transfer should be executed
     * @return true if scheduled successfully, false if either account doesn't exist
     */
    public boolean scheduleTransfer(String fromAccountId, String toAccountId, double amount, int executeAt) {
        if (!accounts.containsKey(fromAccountId) || !accounts.containsKey(toAccountId)) {
            return false; // One or both accounts don't exist
        }
        
        ScheduledTransfer scheduledTransfer = new ScheduledTransfer(fromAccountId, toAccountId, amount, executeAt);
        scheduledTransfers.add(scheduledTransfer);
        return true;
    }
    
    /**
     * Executes all scheduled transfers that are due at the given timestamp.
     * 
     * @param currentTimestamp the current timestamp
     * @return number of transfers executed
     */
    public int executeScheduledTransfers(int currentTimestamp) {
        int executedCount = 0;
        
        for (ScheduledTransfer transfer : scheduledTransfers) {
            if (!transfer.isExecuted() && transfer.getExecuteAt() <= currentTimestamp) {
                if (transfer(transfer.getExecuteAt(), transfer.getFromAccountId(), 
                           transfer.getToAccountId(), transfer.getAmount())) {
                    transfer.setExecuted(true);
                    executedCount++;
                }
            }
        }
        
        return executedCount;
    }
    
    /**
     * Merges two accounts by transferring all balance from source to destination.
     * 
     * @param sourceAccountId the account to merge from
     * @param destinationAccountId the account to merge into
     * @return true if merge successful, false if either account doesn't exist
     */
    public boolean mergeAccounts(String sourceAccountId, String destinationAccountId) {
        Account sourceAccount = accounts.get(sourceAccountId);
        Account destAccount = accounts.get(destinationAccountId);
        
        if (sourceAccount == null || destAccount == null) {
            return false; // One or both accounts don't exist
        }
        
        if (sourceAccountId.equals(destinationAccountId)) {
            return false; // Cannot merge account with itself
        }
        
        // Transfer all balance from source to destination
        double balanceToTransfer = sourceAccount.getBalance();
        if (balanceToTransfer > 0) {
            destAccount.setBalance(destAccount.getBalance() + balanceToTransfer);
            sourceAccount.setBalance(0.0);
        }
        
        // Transfer transacted out amount tracking
        destAccount.addTransactedOut(sourceAccount.getTotalTransactedOut());
        
        // Remove source account
        accounts.remove(sourceAccountId);
        
        return true;
    }
    
    /**
     * Gets all pending scheduled transfers.
     * 
     * @return list of pending scheduled transfers
     */
    public List<String> getPendingScheduledTransfers() {
        List<String> pending = new ArrayList<>();
        
        for (ScheduledTransfer transfer : scheduledTransfers) {
            if (!transfer.isExecuted()) {
                pending.add(String.format("From: %s, To: %s, Amount: $%.2f, Execute At: %d",
                    transfer.getFromAccountId(), transfer.getToAccountId(), 
                    transfer.getAmount(), transfer.getExecuteAt()));
            }
        }
        
        return pending;
    }
    
    /**
     * Gets the total amount transacted out by an account.
     * 
     * @param accountId the account identifier
     * @return total transacted out amount, or -1 if account doesn't exist
     */
    public double getTotalTransactedOut(String accountId) {
        Account account = accounts.get(accountId);
        return account != null ? account.getTotalTransactedOut() : -1;
    }
    
    /**
     * Main method with example usage and test cases
     */
    public static void main(String[] args) {
        BankingSystem bank = new BankingSystem();
        
        System.out.println("=== BankingSystem Complete Example Usage ===");
        
        // Test createAccount
        System.out.println("\n1. Creating accounts:");
        System.out.println("Create ACC001: " + bank.createAccount(1234567890, "ACC001"));
        System.out.println("Create ACC002: " + bank.createAccount(1234567891, "ACC002"));
        System.out.println("Create ACC003: " + bank.createAccount(1234567892, "ACC003"));
        System.out.println("Create ACC001 again: " + bank.createAccount(1234567893, "ACC001")); // Should fail
        
        // Test deposits
        System.out.println("\n2. Deposit operations:");
        System.out.println("Deposit $100 to ACC001: " + bank.deposit(1234567894, "ACC001", 100.0));
        System.out.println("Deposit $200 to ACC002: " + bank.deposit(1234567895, "ACC002", 200.0));
        System.out.println("Deposit $150 to ACC003: " + bank.deposit(1234567896, "ACC003", 150.0));
        
        // Test withdrawals
        System.out.println("\n3. Withdrawal operations:");
        System.out.println("Withdraw $30 from ACC001: " + bank.withdraw(1234567897, "ACC001", 30.0));
        System.out.println("Withdraw $50 from ACC002: " + bank.withdraw(1234567898, "ACC002", 50.0));
        System.out.println("Withdraw $200 from ACC003 (insufficient funds): " + bank.withdraw(1234567899, "ACC003", 200.0));
        
        // Test transfers
        System.out.println("\n4. Transfer operations:");
        System.out.println("Transfer $20 from ACC001 to ACC002: " + bank.transfer(1234567900, "ACC001", "ACC002", 20.0));
        System.out.println("Transfer $40 from ACC002 to ACC003: " + bank.transfer(1234567901, "ACC002", "ACC003", 40.0));
        System.out.println("Transfer $10 from ACC003 to ACC001: " + bank.transfer(1234567902, "ACC003", "ACC001", 10.0));
        
        // Test max transacted out account
        System.out.println("\n5. Max transacted out account:");
        System.out.println("ACC001 transacted out: $" + bank.getTotalTransactedOut("ACC001"));
        System.out.println("ACC002 transacted out: $" + bank.getTotalTransactedOut("ACC002"));
        System.out.println("ACC003 transacted out: $" + bank.getTotalTransactedOut("ACC003"));
        System.out.println("Max transacted out account: " + bank.getMaxTransactedOutAccount());
        
        // Test TTL scheduled transfers
        System.out.println("\n6. TTL Scheduled transfers:");
        System.out.println("Schedule transfer $50 from ACC001 to ACC002 at timestamp 1234568000: " + 
                          bank.scheduleTransfer("ACC001", "ACC002", 50.0, 1234568000));
        System.out.println("Schedule transfer $30 from ACC002 to ACC003 at timestamp 1234568100: " + 
                          bank.scheduleTransfer("ACC002", "ACC003", 30.0, 1234568100));
        System.out.println("Schedule transfer $25 from ACC003 to ACC001 at timestamp 1234568200: " + 
                          bank.scheduleTransfer("ACC003", "ACC001", 25.0, 1234568200));
        
        System.out.println("Pending scheduled transfers:");
        for (String transfer : bank.getPendingScheduledTransfers()) {
            System.out.println("  " + transfer);
        }
        
        // Execute scheduled transfers
        System.out.println("\n7. Executing scheduled transfers:");
        System.out.println("Execute transfers at timestamp 1234568000: " + bank.executeScheduledTransfers(1234568000) + " transfers executed");
        System.out.println("Execute transfers at timestamp 1234568100: " + bank.executeScheduledTransfers(1234568100) + " transfers executed");
        System.out.println("Execute transfers at timestamp 1234568200: " + bank.executeScheduledTransfers(1234568200) + " transfers executed");
        
        // Test account merge
        System.out.println("\n8. Account merge:");
        System.out.println("Before merge - ACC001: " + bank.getAccountInfo("ACC001"));
        System.out.println("Before merge - ACC003: " + bank.getAccountInfo("ACC003"));
        System.out.println("Merge ACC003 into ACC001: " + bank.mergeAccounts("ACC003", "ACC001"));
        System.out.println("After merge - ACC001: " + bank.getAccountInfo("ACC001"));
        System.out.println("After merge - ACC003 exists: " + bank.hasAccount("ACC003"));
        
        // Final account information
        System.out.println("\n9. Final account information:");
        System.out.println(bank.getAccountInfo("ACC001"));
        System.out.println(bank.getAccountInfo("ACC002"));
        System.out.println("Account count: " + bank.getAccountCount());
        
        // Test edge cases
        System.out.println("\n10. Edge cases:");
        System.out.println("Get balance of non-existent account: $" + bank.getBalance("NONEXISTENT"));
        System.out.println("Deposit to non-existent account: " + bank.deposit(1234568300, "NONEXISTENT", 100.0));
        System.out.println("Transfer from non-existent account: " + bank.transfer(1234568301, "NONEXISTENT", "ACC001", 10.0));
        System.out.println("Merge non-existent account: " + bank.mergeAccounts("NONEXISTENT", "ACC001"));
        System.out.println("Schedule transfer with non-existent account: " + bank.scheduleTransfer("NONEXISTENT", "ACC001", 10.0, 1234568400));
    }
}
