package stubhub;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * StubHub Interview Problem: Get Event API Endpoint
 *
 * Find issues in this code related to:
 * 1. Readability (code duplication, unclear logic)
 * 2. Security (SQL injection, input validation)
 * 3. Performance (N+1 query problem, missing caching, inefficient queries)
 */

public class GetEventAPI {

    private Connection dbConnection;

    public GetEventAPI(Connection connection) {
        this.dbConnection = connection;
    }

    /**
     * Get event endpoint - retrieves event and its transactions
     * PROBLEM: This code has multiple issues that need to be identified and fixed
     */
    public EventResponse getEvent(String eventId, String userId) throws SQLException {
        // Query events from database
        Statement stmt = dbConnection.createStatement();
        String query = "SELECT * FROM events WHERE event_id = '" + eventId + "'";
        ResultSet rs = stmt.executeQuery(query);

        Event event = null;
        if (rs.next()) {
            event = new Event();
            event.setId(rs.getString("event_id"));
            event.setName(rs.getString("name"));
            event.setVenue(rs.getString("venue"));
            event.setDate(rs.getDate("event_date"));
            event.setPrice(rs.getDouble("price"));
        }
        rs.close();
        stmt.close();

        if (event == null) {
            return null;
        }

        // Get transactions for this event - N+1 query problem!
        List<Transaction> transactions = new ArrayList<>();
        Statement stmt2 = dbConnection.createStatement();
        String query2 = "SELECT * FROM transactions WHERE event_id = '" + eventId + "'";
        ResultSet rs2 = stmt2.executeQuery(query2);

        while (rs2.next()) {
            Transaction txn = new Transaction();
            txn.setId(rs2.getString("transaction_id"));
            txn.setEventId(rs2.getString("event_id"));
            txn.setUserId(rs2.getString("user_id"));
            txn.setAmount(rs2.getDouble("amount"));
            txn.setStatus(rs2.getString("status"));
            transactions.add(txn);
        }
        rs2.close();
        stmt2.close();

        // Get user details for each transaction - another N+1 problem!
        for (Transaction txn : transactions) {
            Statement stmt3 = dbConnection.createStatement();
            String query3 = "SELECT * FROM users WHERE user_id = '" + txn.getUserId() + "'";
            ResultSet rs3 = stmt3.executeQuery(query3);

            if (rs3.next()) {
                User user = new User();
                user.setId(rs3.getString("user_id"));
                user.setName(rs3.getString("name"));
                user.setEmail(rs3.getString("email"));
                txn.setUser(user);
            }
            rs3.close();
            stmt3.close();
        }

        // Calculate total revenue - code duplication
        double totalRevenue = 0;
        for (Transaction txn : transactions) {
            if (txn.getStatus().equals("completed")) {
                totalRevenue += txn.getAmount();
            }
        }

        // Calculate completed transactions count - code duplication
        int completedCount = 0;
        for (Transaction txn : transactions) {
            if (txn.getStatus().equals("completed")) {
                completedCount++;
            }
        }

        EventResponse response = new EventResponse();
        response.setEvent(event);
        response.setTransactions(transactions);
        response.setTotalRevenue(totalRevenue);
        response.setCompletedTransactionCount(completedCount);

        return response;
    }

    // Supporting classes
    static class Event {
        private String id;
        private String name;
        private String venue;
        private java.sql.Date date;
        private double price;

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getVenue() { return venue; }
        public void setVenue(String venue) { this.venue = venue; }
        public java.sql.Date getDate() { return date; }
        public void setDate(java.sql.Date date) { this.date = date; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
    }

    static class Transaction {
        private String id;
        private String eventId;
        private String userId;
        private double amount;
        private String status;
        private User user;

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getEventId() { return eventId; }
        public void setEventId(String eventId) { this.eventId = eventId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
    }

    static class User {
        private String id;
        private String name;
        private String email;

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    static class EventResponse {
        private Event event;
        private List<Transaction> transactions;
        private double totalRevenue;
        private int completedTransactionCount;

        // Getters and setters
        public Event getEvent() { return event; }
        public void setEvent(Event event) { this.event = event; }
        public List<Transaction> getTransactions() { return transactions; }
        public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }
        public double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
        public int getCompletedTransactionCount() { return completedTransactionCount; }
        public void setCompletedTransactionCount(int count) { this.completedTransactionCount = count; }
    }
}
