package coinbase;

import java.util.*;

class BankingSystemImpl {

  private Map<String, Integer> accounts;
  private Map<String, Integer> outgoingTransactions; // Track total outgoing transactions per account
  private List<ScheduledPayment> scheduledPayments; // Track scheduled payments
  private int paymentCounter; // Counter for payment IDs
  private Map<String, String> mergedAccounts; // Track merged accounts: mergedAccountId -> targetAccountId
  private Map<String, List<BalanceHistory>> accountHistory; // Track balance history for each account
  
  // Inner class to represent balance history
  private static class BalanceHistory {
    private int timestamp;
    private int balance;
    
    public BalanceHistory(int timestamp, int balance) {
      this.timestamp = timestamp;
      this.balance = balance;
    }
    
    public int getTimestamp() { return timestamp; }
    public int getBalance() { return balance; }
  }
  
  // Inner class to represent scheduled payments
  private static class ScheduledPayment {
    private String paymentId;
    private String accountId;
    private int amount;
    private int executeAt;
    private boolean canceled;
    private boolean executed;
    
    public ScheduledPayment(String paymentId, String accountId, int amount, int executeAt) {
      this.paymentId = paymentId;
      this.accountId = accountId;
      this.amount = amount;
      this.executeAt = executeAt;
      this.canceled = false;
      this.executed = false;
    }
    
    // Getters
    public String getPaymentId() { return paymentId; }
    public String getAccountId() { return accountId; }
    public int getAmount() { return amount; }
    public int getExecuteAt() { return executeAt; }
    public boolean isCanceled() { return canceled; }
    public boolean isExecuted() { return executed; }
    
    // Setters
    public void setCanceled(boolean canceled) { this.canceled = canceled; }
    public void setExecuted(boolean executed) { this.executed = executed; }
  }
  
  public BankingSystemImpl() {
    // TODO: implement
    this.accounts = new HashMap<>();
    this.outgoingTransactions = new HashMap<>();
    this.scheduledPayments = new ArrayList<>();
    this.paymentCounter = 0;
    this.mergedAccounts = new HashMap<>();
    this.accountHistory = new HashMap<>();
  }

  // Helper method to get the actual account ID (handles merged accounts)
  private String getActualAccountId(String accountId) {
    return mergedAccounts.getOrDefault(accountId, accountId);
  }
  
  // Helper method to record balance history
  private void recordBalanceHistory(String accountId, int timestamp, int balance) {
    accountHistory.computeIfAbsent(accountId, k -> new ArrayList<>()).add(new BalanceHistory(timestamp, balance));
  }
  
  // Helper method to get balance at a specific timestamp
  private Optional<Integer> getBalanceAtTime(String accountId, int timeAt) {
    String actualAccountId = getActualAccountId(accountId);
    
    // Check if account existed at the given time
    if (!accountHistory.containsKey(actualAccountId)) {
      return Optional.empty();
    }
    
    List<BalanceHistory> history = accountHistory.get(actualAccountId);
    
    // Find the most recent balance entry at or before timeAt
    BalanceHistory latestEntry = null;
    for (BalanceHistory entry : history) {
      if (entry.getTimestamp() <= timeAt) {
        if (latestEntry == null || entry.getTimestamp() > latestEntry.getTimestamp()) {
          latestEntry = entry;
        }
      }
    }
    
    return latestEntry != null ? Optional.of(latestEntry.getBalance()) : Optional.empty();
  }

  // TODO: implement interface methods here
  public boolean createAccount(int timestamp, String accountId) {
    if (accounts.containsKey(accountId)) return false;
    accounts.put(accountId, 0);
    outgoingTransactions.put(accountId, 0); // Initialize outgoing transactions to 0
    recordBalanceHistory(accountId, timestamp, 0); // Record initial balance
    return true;
  }
  
  public Optional<Integer> deposit(int timestamp, String accountId, int amount) {
    // Process scheduled payments first
    processScheduledPayments(timestamp);
    
    String actualAccountId = getActualAccountId(accountId);
    if (!accounts.containsKey(actualAccountId)) {
      return Optional.empty();
    }
    
    int currentBalance = accounts.get(actualAccountId);
    int newBalance = currentBalance + amount;
    accounts.put(actualAccountId, newBalance);
    recordBalanceHistory(actualAccountId, timestamp, newBalance);
    
    return Optional.of(newBalance);
  }
  
  public Optional<Integer> transfer(int timestamp, String sourceAcctId, String targetAcctId, int amount) {
    // Process scheduled payments first
    processScheduledPayments(timestamp);
    
    System.out.println("DEBUG: transfer called: " + sourceAcctId + " -> " + targetAcctId + " amount=" + amount);
    
    String actualSourceId = getActualAccountId(sourceAcctId);
    String actualTargetId = getActualAccountId(targetAcctId);
    
    if (!accounts.containsKey(actualSourceId) || !accounts.containsKey(actualTargetId)) {
      System.out.println("DEBUG: transfer failed - account doesn't exist");
      return Optional.empty();
    }
    
    if (actualSourceId.equals(actualTargetId)) {
      System.out.println("DEBUG: transfer failed - same account");
      return Optional.empty();
    }
    
    int sourceBalance = accounts.get(actualSourceId);
    if (sourceBalance < amount) {
      System.out.println("DEBUG: transfer failed - insufficient funds. Balance=" + sourceBalance + " Amount=" + amount);
      return Optional.empty();
    }
    
    int targetBalance = accounts.get(actualTargetId);
    accounts.put(actualSourceId, sourceBalance - amount);
    accounts.put(actualTargetId, targetBalance + amount);
    
    // Track outgoing transaction for source account
    int currentOutgoing = outgoingTransactions.get(actualSourceId);
    outgoingTransactions.put(actualSourceId, currentOutgoing + amount);
    
    recordBalanceHistory(actualSourceId, timestamp, sourceBalance - amount);
    recordBalanceHistory(actualTargetId, timestamp, targetBalance + amount);
    
    System.out.println("DEBUG: transfer successful. Updated outgoingTransactions: " + outgoingTransactions);
    
    return Optional.of(sourceBalance - amount);
  }
  
  public List<String> topSpenders(int timestamp, int n) {
    // Process scheduled payments first
    processScheduledPayments(timestamp);
    
    // Debug: Print current state
    System.out.println("DEBUG: topSpenders called with n=" + n);
    System.out.println("DEBUG: outgoingTransactions = " + outgoingTransactions);
    System.out.println("DEBUG: accounts = " + accounts);
    
    // Create list of account entries with their outgoing transaction amounts
    List<Map.Entry<String, Integer>> accountEntries = new ArrayList<>(outgoingTransactions.entrySet());
    
    // Sort by outgoing amount (descending), then by accountId (ascending) in case of tie
    Collections.sort(accountEntries, (a, b) -> {
      int amountCompare = Integer.compare(b.getValue(), a.getValue()); // Descending order
      if (amountCompare != 0) {
        return amountCompare;
      }
      return a.getKey().compareTo(b.getKey()); // Ascending order for ties
    });
    
    // Take top n accounts and format them
    List<String> result = new ArrayList<>();
    int count = Math.min(n, accountEntries.size());
    
    for (int i = 0; i < count; i++) {
      Map.Entry<String, Integer> entry = accountEntries.get(i);
      String accountId = entry.getKey();
      int totalOutgoing = entry.getValue();
      result.add(accountId + "(" + totalOutgoing + ")");
    }
    
    System.out.println("DEBUG: result = " + result);
    return result;
  }
  
  public Optional<String> schedulePayment(int timestamp, String accountId, int amount, int delay) {
    System.out.println("DEBUG: schedulePayment called: accountId=" + accountId + " amount=" + amount + " delay=" + delay);
    
    // Check if account exists
    String actualAccountId = getActualAccountId(accountId);
    if (!accounts.containsKey(actualAccountId)) {
      System.out.println("DEBUG: schedulePayment failed - account doesn't exist");
      return Optional.empty();
    }
    
    // Generate payment ID
    paymentCounter++;
    String paymentId = "payment" + paymentCounter;
    
    // Create scheduled payment
    int executeAt = timestamp + delay;
    ScheduledPayment payment = new ScheduledPayment(paymentId, actualAccountId, amount, executeAt);
    scheduledPayments.add(payment);
    
    System.out.println("DEBUG: scheduled payment created: " + paymentId + " executeAt=" + executeAt);
    
    return Optional.of(paymentId);
  }
  
  public boolean cancelPayment(int timestamp, String accountId, String paymentId) {
    // Process scheduled payments first
    processScheduledPayments(timestamp);
    
    System.out.println("DEBUG: cancelPayment called: accountId=" + accountId + " paymentId=" + paymentId);
    
    // Find the payment
    for (ScheduledPayment payment : scheduledPayments) {
      if (payment.getPaymentId().equals(paymentId)) {
        // Check if account matches (handle merged accounts)
        String actualAccountId = getActualAccountId(accountId);
        if (!payment.getAccountId().equals(actualAccountId)) {
          System.out.println("DEBUG: cancelPayment failed - account doesn't match");
          return false;
        }
        
        // Check if already canceled or executed
        if (payment.isCanceled() || payment.isExecuted()) {
          System.out.println("DEBUG: cancelPayment failed - already canceled or executed");
          return false;
        }
        
        // Cancel the payment
        payment.setCanceled(true);
        System.out.println("DEBUG: payment canceled successfully");
        return true;
      }
    }
    
    System.out.println("DEBUG: cancelPayment failed - payment not found");
    return false;
  }
  
  // Helper method to process scheduled payments at a given timestamp
  private void processScheduledPayments(int timestamp) {
    System.out.println("DEBUG: processScheduledPayments called at timestamp=" + timestamp);
    
    // Sort payments by creation order (they're already in order since we add them sequentially)
    for (ScheduledPayment payment : scheduledPayments) {
      if (!payment.isCanceled() && !payment.isExecuted() && payment.getExecuteAt() <= timestamp) {
        String accountId = payment.getAccountId();
        int amount = payment.getAmount();
        
        System.out.println("DEBUG: Processing payment " + payment.getPaymentId() + " for account " + accountId + " amount=" + amount);
        
        // Check if account has sufficient funds
        int currentBalance = accounts.get(accountId);
        if (currentBalance >= amount) {
          // Process the payment
          accounts.put(accountId, currentBalance - amount);
          
          // Track as outgoing transaction
          int currentOutgoing = outgoingTransactions.get(accountId);
          outgoingTransactions.put(accountId, currentOutgoing + amount);
          
          System.out.println("DEBUG: Payment processed successfully. New balance=" + (currentBalance - amount) + " outgoing=" + (currentOutgoing + amount));
        } else {
          System.out.println("DEBUG: Payment skipped - insufficient funds. Balance=" + currentBalance + " Amount=" + amount);
        }
        
        // Mark as executed to prevent reprocessing
        payment.setExecuted(true);
      }
    }
  }
  
  // Test method to verify the implementation
  public static void main(String[] args) {
    BankingSystemImpl bank = new BankingSystemImpl();
    
    System.out.println("=== Testing BankingSystemImpl with Scheduled Payments ===");
    
    // Test case from the specification
    System.out.println("createAccount(1, \"account1\"): " + bank.createAccount(1, "account1"));
    System.out.println("createAccount(2, \"account2\"): " + bank.createAccount(2, "account2"));
    System.out.println("deposit(3, \"account1\", 2000): " + bank.deposit(3, "account1", 2000));
    System.out.println("deposit(4, \"account2\", 3000): " + bank.deposit(4, "account2", 3000));
    System.out.println("schedulePayment(5, \"account1\", 50, 10): " + bank.schedulePayment(5, "account1", 50, 10));
    System.out.println("schedulePayment(6, \"account2\", 1000, 5): " + bank.schedulePayment(6, "account2", 1000, 5));
    System.out.println("schedulePayment(7, \"account1\", 3000, 7): " + bank.schedulePayment(7, "account1", 3000, 7));
    System.out.println("deposit(11, \"account2\", 5): " + bank.deposit(11, "account2", 5));
    System.out.println("cancelPayment(12, \"account2\", \"payment1\"): " + bank.cancelPayment(12, "account2", "payment1"));
    System.out.println("cancelPayment(13, \"account1\", \"payment1\"): " + bank.cancelPayment(13, "account1", "payment1"));
    System.out.println("deposit(14, \"account1\", 5): " + bank.deposit(14, "account1", 5));
    System.out.println("deposit(15, \"account1\", 5): " + bank.deposit(15, "account1", 5));
    
    // Test topSpenders with scheduled payments
    System.out.println("\n=== Testing topSpenders with scheduled payments ===");
    System.out.println("topSpenders(15, 3): " + bank.topSpenders(15, 3));
    System.out.println("topSpenders(20, 3): " + bank.topSpenders(20, 3));
    
    // Debug: Check final state
    System.out.println("Final outgoing transactions: " + bank.outgoingTransactions);
    System.out.println("Final account balances: " + bank.accounts);
  }
  
  public boolean mergeAccounts(int timestamp, String accountId1, String accountId2) {
    // Process scheduled payments first
    processScheduledPayments(timestamp);
    
    System.out.println("DEBUG: mergeAccounts called: " + accountId1 + " <- " + accountId2);
    
    // Check if accounts are the same
    if (accountId1.equals(accountId2)) {
      System.out.println("DEBUG: mergeAccounts failed - same account");
      return false;
    }
    
    // Check if both accounts exist
    if (!accounts.containsKey(accountId1) || !accounts.containsKey(accountId2)) {
      System.out.println("DEBUG: mergeAccounts failed - account doesn't exist");
      return false;
    }
    
    // Get balances
    int balance1 = accounts.get(accountId1);
    int balance2 = accounts.get(accountId2);
    int totalBalance = balance1 + balance2;
    
    // Get outgoing transactions
    int outgoing1 = outgoingTransactions.get(accountId1);
    int outgoing2 = outgoingTransactions.get(accountId2);
    int totalOutgoing = outgoing1 + outgoing2;
    
    // Update account1 with combined balance and outgoing transactions
    accounts.put(accountId1, totalBalance);
    outgoingTransactions.put(accountId1, totalOutgoing);
    
    // Transfer all scheduled payments from account2 to account1
    for (ScheduledPayment payment : scheduledPayments) {
      if (payment.getAccountId().equals(accountId2) && !payment.isCanceled() && !payment.isExecuted()) {
        // Create a new payment for account1 with the same details
        ScheduledPayment newPayment = new ScheduledPayment(
          payment.getPaymentId(), 
          accountId1, 
          payment.getAmount(), 
          payment.getExecuteAt()
        );
        newPayment.setCanceled(payment.isCanceled());
        newPayment.setExecuted(payment.isExecuted());
        
        // Replace the old payment
        int index = scheduledPayments.indexOf(payment);
        scheduledPayments.set(index, newPayment);
      }
    }
    
    // Record the merge in balance history
    recordBalanceHistory(accountId1, timestamp, totalBalance);
    
    // Mark account2 as merged into account1
    mergedAccounts.put(accountId2, accountId1);
    
    // Remove account2 from active accounts
    accounts.remove(accountId2);
    outgoingTransactions.remove(accountId2);
    
    System.out.println("DEBUG: mergeAccounts successful. Account1 balance: " + totalBalance + " Outgoing: " + totalOutgoing);
    
    return true;
  }
  
  public Optional<Integer> getBalance(int timestamp, String accountId, int timeAt) {
    // Process scheduled payments first
    processScheduledPayments(timestamp);
    
    System.out.println("DEBUG: getBalance called: accountId=" + accountId + " timeAt=" + timeAt);
    
    // Check if account exists (either as active or merged)
    String actualAccountId = getActualAccountId(accountId);
    if (!accounts.containsKey(actualAccountId) && !accountHistory.containsKey(actualAccountId)) {
      System.out.println("DEBUG: getBalance failed - account doesn't exist");
      return Optional.empty();
    }
    
    // If account was merged, check if it existed at the given time
    if (mergedAccounts.containsKey(accountId)) {
      // Check if the merged account existed at timeAt
      if (!accountHistory.containsKey(accountId)) {
        System.out.println("DEBUG: getBalance failed - merged account didn't exist at timeAt");
        return Optional.empty();
      }
    }
    
    // Get balance at the specified time
    Optional<Integer> balance = getBalanceAtTime(accountId, timeAt);
    
    System.out.println("DEBUG: getBalance result: " + balance);
    
    return balance;
  }
  
}
