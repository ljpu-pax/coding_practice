package com.codesignal.bankingsystem;

import java.util.*;
import java.util.Optional;

class BankingSystemImpl implements BankingSystem {

  private Map<String, Integer> accounts;
  private Map<String, Integer> outGoingTransactions;
  private List<ScheduledPayment> scheduledPayments;
  private int paymentCounter;
  
  private static class ScheduledPayment {
    private String paymentId;
    private String accountId;
    private int amount;
    private int executeAt;
    private boolean canceled;
    
    public ScheduledPayment(String paymentId, String accountId, int amount, int executeAt) {
      this.paymentId = paymentId;
      this.accountId = accountId;
      this.amount = amount;
      this.executeAt = executeAt;
      this.canceled = false;
    }
    
    public String getPaymentId() { return paymentId; }
    public String getAccountId() { return accountId; }
    public int getAmount() { return amount; }
    public int getExecuteAt() { return executeAt; }
    public boolean isCanceled() { return canceled; }
    
    public void setCanceled(boolean canceled) { this.canceled = canceled; }
  }
  
  public BankingSystemImpl() {
    // TODO: implement
    this.accounts = new HashMap<>();
    this.outGoingTransactions = new HashMap<>();
    this.scheduledPayments = new ArrayList<>();
    this.paymentCounter = 0;
  }

  // TODO: implement interface methods here
  public boolean createAccount(int timestamp, String accountId) {
    if (accounts.containsKey(accountId)) return false;
    accounts.put(accountId, 0);
    outGoingTransactions.put(accountId, 0);
    return true;
  }
  
  public Optional<Integer> deposit(int timestamp, String accountId, int amount) {
    processScheduledPayments(timestamp);
    
    if (!accounts.containsKey(accountId)) {
      return Optional.empty();
    }
    
    int currentBalance = accounts.get(accountId);
    int newBalance = currentBalance + amount;
    accounts.put(accountId, newBalance);
    
    return Optional.of(newBalance);
  }
  
  public Optional<Integer> transfer(int timestamp, String sourceAcctId, String targetAcctId, int amount) {
    processScheduledPayments(timestamp);
    
    if (!accounts.containsKey(sourceAcctId) || !accounts.containsKey(targetAcctId)) return Optional.empty();
    
    if (sourceAcctId.equals(targetAcctId)) return Optional.empty();
    
    int sourceBalance = accounts.get(sourceAcctId);
    if (sourceBalance < amount) return Optional.empty();
    
    int targetBalance = accounts.get(targetAcctId);
    accounts.put(sourceAcctId, sourceBalance - amount);
    accounts.put(targetAcctId, targetBalance + amount);
    
    int currentOutgoing = outGoingTransactions.get(sourceAcctId);
    outGoingTransactions.put(sourceAcctId, currentOutgoing + amount);
    
    return Optional.of(sourceBalance - amount);
  }
  
  public List<String> topSpenders(int timestamp, int n) {
    // FIXED: Add processScheduledPayments call
    processScheduledPayments(timestamp);
    
    List<Map.Entry<String, Integer>> accoEntries = new ArrayList<>(outGoingTransactions.entrySet());
    
    Collections.sort(accoEntries, (a, b) -> {
      int amountCompare = Integer.compare(b.getValue(), a.getValue());
      if (amountCompare != 0) return amountCompare;
      return a.getKey().compareTo(b.getKey());
    }); // FIXED: Removed extra semicolon
    
    List<String> result = new ArrayList<>();
    int count = Math.min(n, accoEntries.size());
    
    for (int i = 0; i < count; i++) {
      Map.Entry<String, Integer> entry = accoEntries.get(i);
      String accountId = entry.getKey();
      int totalOutgoing = entry.getValue();
      result.add(accountId + "(" + totalOutgoing + ")");
    }
    
    return result;
  }
  
  public Optional<String> schedulePayment(int timestamp, String accountId, int amount, int delay) {
    // FIXED: Add processScheduledPayments call
    processScheduledPayments(timestamp);
    
    if (!accounts.containsKey(accountId)) return Optional.empty();
    
    paymentCounter++;
    String paymentId = "payment" + paymentCounter;
    
    int executeAt = timestamp + delay;
    ScheduledPayment payment = new ScheduledPayment(paymentId, accountId, amount, executeAt);
    scheduledPayments.add(payment);
    
    return Optional.of(paymentId);
  }
  
  public boolean cancelPayment(int timestamp, String accountId, String paymentId) {
    processScheduledPayments(timestamp);
    
    // FIXED: Corrected the logic flow
    for (ScheduledPayment payment : scheduledPayments) {
      if (payment.getPaymentId().equals(paymentId)) {
        // Check if account matches
        if (!payment.getAccountId().equals(accountId)) {
          return false;
        }
        
        // Check if already canceled
        if (payment.isCanceled()) {
          return false;
        }
        
        // Cancel the payment
        payment.setCanceled(true);
        return true;
      }
    }
    
    return false; // Payment not found
  }
  
  private void processScheduledPayments(int timestamp) {
    for (ScheduledPayment payment : scheduledPayments) {
      if (!payment.isCanceled() && payment.getExecuteAt() <= timestamp) {
        String accountId = payment.getAccountId();
        int amount = payment.getAmount();
        
        int currentBalance = accounts.get(accountId);
        if (currentBalance >= amount) {
          accounts.put(accountId, currentBalance - amount);
          
          int currentOutgoing = outGoingTransactions.get(accountId);
          outGoingTransactions.put(accountId, currentOutgoing + amount);
        }
        
        payment.setCanceled(true);
      }
    }
  }
  
}

