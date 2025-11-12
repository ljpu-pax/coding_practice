# StubHub Interview: Get Event API Problem

## Problem Description
You're given a `getEvent` API endpoint that retrieves event data and its related transactions from a database. Find and fix issues related to:
- **Security** (SQL injection, input validation)
- **Performance** (N+1 query problem, inefficient DB calls, caching)
- **Readability** (code duplication, unclear logic)

## Files
- `GetEventAPI.java` - The problematic code with issues
- `GetEventAPIFixed.java` - The fixed version with all improvements

## Issues Found

### 1. Security Issues

#### SQL Injection Vulnerability
**Problem:** Direct string concatenation in SQL queries
```java
String query = "SELECT * FROM events WHERE event_id = '" + eventId + "'";
```

**Solution:** Use PreparedStatement with parameterized queries
```java
String query = "SELECT * FROM events WHERE event_id = ?";
PreparedStatement pstmt = dbConnection.prepareStatement(query);
pstmt.setString(1, eventId);
```

#### Missing Input Validation
**Problem:** No validation on `eventId` parameter

**Solution:** Add input validation
```java
private boolean isValidEventId(String eventId) {
    if (eventId == null || eventId.isEmpty()) {
        return false;
    }
    return eventId.matches("^[a-zA-Z0-9\\-]{1,50}$");
}
```

### 2. Performance Issues

#### N+1 Query Problem
**Problem:** Multiple database queries executed in loops
- 1 query for the event
- 1 query for all transactions
- N queries for users (one per transaction)

**Solution:** Use a single JOIN query to fetch all data at once
```java
String query = """
    SELECT
        e.event_id, e.name, e.venue, e.event_date, e.price,
        t.transaction_id, t.user_id, t.amount, t.status,
        u.user_id as txn_user_id, u.name as user_name, u.email
    FROM events e
    LEFT JOIN transactions t ON e.event_id = t.event_id
    LEFT JOIN users u ON t.user_id = u.user_id
    WHERE e.event_id = ?
    """;
```

#### Missing Caching
**Problem:** No caching layer, every request hits the database

**Solution:** Add in-memory cache with TTL
```java
private Map<String, CachedEventResponse> cache = new ConcurrentHashMap<>();
private static final long CACHE_TTL_MS = 60000; // 1 minute
```

#### Resource Management
**Problem:** Potential resource leaks with manual close() calls

**Solution:** Use try-with-resources
```java
try (PreparedStatement pstmt = dbConnection.prepareStatement(query);
     ResultSet rs = pstmt.executeQuery()) {
    // process results
}
```

### 3. Readability Issues

#### Code Duplication
**Problem:** Two separate loops to calculate revenue and count
```java
// Loop 1: Calculate total revenue
for (Transaction txn : transactions) {
    if (txn.getStatus().equals("completed")) {
        totalRevenue += txn.getAmount();
    }
}

// Loop 2: Calculate completed count
for (Transaction txn : transactions) {
    if (txn.getStatus().equals("completed")) {
        completedCount++;
    }
}
```

**Solution:** Single pass calculation
```java
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
```

#### Repeated Object Building
**Problem:** Repetitive object construction code

**Solution:** Extract to helper methods
```java
private Event buildEvent(ResultSet rs) throws SQLException { ... }
private Transaction buildTransaction(ResultSet rs) throws SQLException { ... }
private User buildUser(ResultSet rs) throws SQLException { ... }
```

## Key Improvements Summary

| Issue | Before | After |
|-------|--------|-------|
| **SQL Injection** | String concatenation | PreparedStatement |
| **DB Queries** | 1 + 1 + N queries | 1 query with JOINs |
| **Input Validation** | None | Regex validation |
| **Caching** | No cache | In-memory cache with TTL |
| **Code Duplication** | 2 separate loops | 1 combined loop |
| **Resource Management** | Manual close() | try-with-resources |

## Performance Impact

**Before:**
- Database calls: 2 + N (where N = number of transactions)
- Example: Event with 100 transactions = 102 DB queries

**After:**
- Database calls: 1 (or 0 with cache hit)
- Same event: 1 DB query (or cached)

**Performance improvement: ~100x for typical events**

## Interview Tips

When reviewing code for these types of issues:
1. **Security first** - Look for SQL injection, XSS, input validation
2. **Check the database** - Count queries, look for N+1 patterns
3. **Look for loops** - Duplicate iterations are code smell
4. **Resource management** - Ensure connections/streams are closed
5. **Consider caching** - Is this data read-heavy? Can it be cached?

## How to Practice

1. Open `GetEventAPI.java`
2. Try to identify all issues yourself
3. Write down your findings
4. Compare with `GetEventAPIFixed.java`
5. Practice explaining the issues and solutions out loud
