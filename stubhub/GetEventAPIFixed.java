package stubhub;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Fixed version of GetEventAPI with improvements for:
 * 1. Security: SQL injection prevention using PreparedStatement, input validation
 * 2. Performance: Eliminated N+1 queries with JOINs, added caching
 * 3. Readability: Removed code duplication, better structure
 */

public class GetEventAPIFixed {

    private Connection dbConnection;
    // Simple in-memory cache (in production, use Redis/Memcached)
    private Map<String, CachedEventResponse> cache = new ConcurrentHashMap<>();
    private static final long CACHE_TTL_MS = 60000; // 1 minute

    public GetEventAPIFixed(Connection connection) {
        this.dbConnection = connection;
    }

    /**
     * Fixed getEvent endpoint
     */
    public EventResponse getEvent(String eventId, String userId) throws SQLException {
        // SECURITY FIX: Input validation
        if (!isValidEventId(eventId)) {
            throw new IllegalArgumentException("Invalid event ID format");
        }

        // PERFORMANCE FIX: Check cache first
        CachedEventResponse cached = cache.get(eventId);
        if (cached != null && !cached.isExpired()) {
            return cached.response;
        }

        // PERFORMANCE FIX: Single query with JOINs instead of N+1 queries
        String query = "SELECT " +
            "e.event_id, e.name, e.venue, e.event_date, e.price, " +
            "t.transaction_id, t.user_id, t.amount, t.status, " +
            "u.user_id as txn_user_id, u.name as user_name, u.email " +
            "FROM events e " +
            "LEFT JOIN transactions t ON e.event_id = t.event_id " +
            "LEFT JOIN users u ON t.user_id = u.user_id " +
            "WHERE e.event_id = ?";

        Event event = null;
        Map<String, Transaction> transactionMap = new LinkedHashMap<>();

        // SECURITY FIX: Use PreparedStatement to prevent SQL injection
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
            pstmt.setString(1, eventId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Build event object (only once)
                    if (event == null) {
                        event = buildEvent(rs);
                    }

                    // Build transaction and user objects
                    String txnId = rs.getString("transaction_id");
                    if (txnId != null && !transactionMap.containsKey(txnId)) {
                        Transaction txn = buildTransaction(rs);
                        User user = buildUser(rs);
                        txn.setUser(user);
                        transactionMap.put(txnId, txn);
                    }
                }
            }
        }

        if (event == null) {
            return null;
        }

        List<Transaction> transactions = new ArrayList<>(transactionMap.values());

        // READABILITY FIX: Calculate stats in a single pass
        TransactionStats stats = calculateTransactionStats(transactions);

        EventResponse response = new EventResponse();
        response.setEvent(event);
        response.setTransactions(transactions);
        response.setTotalRevenue(stats.totalRevenue);
        response.setCompletedTransactionCount(stats.completedCount);

        // PERFORMANCE FIX: Cache the response
        cache.put(eventId, new CachedEventResponse(response));

        return response;
    }

    // SECURITY: Input validation helper
    private boolean isValidEventId(String eventId) {
        if (eventId == null || eventId.isEmpty()) {
            return false;
        }
        // Allow alphanumeric and hyphens only (adjust pattern based on requirements)
        return eventId.matches("^[a-zA-Z0-9\\-]{1,50}$");
    }

    // READABILITY: Extract method for building Event
    private Event buildEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(rs.getString("event_id"));
        event.setName(rs.getString("name"));
        event.setVenue(rs.getString("venue"));
        event.setDate(rs.getDate("event_date"));
        event.setPrice(rs.getDouble("price"));
        return event;
    }

    // READABILITY: Extract method for building Transaction
    private Transaction buildTransaction(ResultSet rs) throws SQLException {
        Transaction txn = new Transaction();
        txn.setId(rs.getString("transaction_id"));
        txn.setEventId(rs.getString("event_id"));
        txn.setUserId(rs.getString("user_id"));
        txn.setAmount(rs.getDouble("amount"));
        txn.setStatus(rs.getString("status"));
        return txn;
    }

    // READABILITY: Extract method for building User
    private User buildUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getString("txn_user_id"));
        user.setName(rs.getString("user_name"));
        user.setEmail(rs.getString("email"));
        return user;
    }

    // READABILITY FIX: Single pass calculation instead of duplicate loops
    private TransactionStats calculateTransactionStats(List<Transaction> transactions) {
        double totalRevenue = 0;
        int completedCount = 0;

        for (Transaction txn : transactions) {
            if ("completed".equals(txn.getStatus())) {
                totalRevenue += txn.getAmount();
                completedCount++;
            }
        }

        return new TransactionStats(totalRevenue, completedCount);
    }

    // Helper class for transaction statistics
    private static class TransactionStats {
        final double totalRevenue;
        final int completedCount;

        TransactionStats(double totalRevenue, int completedCount) {
            this.totalRevenue = totalRevenue;
            this.completedCount = completedCount;
        }
    }

    // Cache wrapper with TTL
    private static class CachedEventResponse {
        final EventResponse response;
        final long timestamp;

        CachedEventResponse(EventResponse response) {
            this.response = response;
            this.timestamp = System.currentTimeMillis();
        }

        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_TTL_MS;
        }
    }

    // Supporting classes (same as original)
    static class Event {
        private String id;
        private String name;
        private String venue;
        private java.sql.Date date;
        private double price;

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
