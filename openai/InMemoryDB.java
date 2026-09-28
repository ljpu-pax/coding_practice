package openai;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * In-Memory Database with SQL-like Operations
 *
 * Problem:
 * Implement an in-memory database that supports:
 * - create_table(table_name, columns): Create a table with schema
 * - insert(table_name, row): Insert a row into the table
 * - select(table_name, where, order_by): Query with filtering and sorting
 *
 * Requirements:
 * - WHERE: Support single and multiple conditions (AND only)
 * - ORDER BY: Support single and multiple columns
 * - Backward compatibility: Handle None/null parameters
 *
 * Examples:
 * - select("users", where=lambda row: row["age"] > 28, order_by="name")
 * - select("users", where={"age": 30, "name": "Alice"}, order_by=["age", "name"])
 * - select("users", where=null, order_by="age")
 *
 * Time Complexity:
 * - create_table: O(1)
 * - insert: O(1)
 * - select: O(n log n) where n is number of rows (due to sorting)
 *
 * Space Complexity: O(n × m) where n is rows, m is columns
 */
public class InMemoryDB {

    // Table storage: table_name -> list of rows
    private Map<String, List<Map<String, Object>>> tables;

    // Schema storage: table_name -> list of column names
    private Map<String, List<String>> schemas;

    public InMemoryDB() {
        this.tables = new HashMap<>();
        this.schemas = new HashMap<>();
    }

    /**
     * Create a table with the given schema
     */
    public void createTable(String tableName, List<String> columns) {
        schemas.put(tableName, new ArrayList<>(columns));
        tables.put(tableName, new ArrayList<>());
    }

    /**
     * Insert a row into the table
     */
    public void insert(String tableName, Map<String, Object> row) {
        if (!tables.containsKey(tableName)) {
            throw new IllegalArgumentException("Table does not exist: " + tableName);
        }

        // Create a copy to avoid external modifications
        Map<String, Object> rowCopy = new HashMap<>(row);
        tables.get(tableName).add(rowCopy);
    }

    /**
     * Select rows from table with optional WHERE and ORDER BY
     *
     * @param tableName The table to query
     * @param where Filter condition (can be Predicate, Map, or null)
     * @param orderBy Column(s) to sort by (can be String, List<String>, or null)
     * @return List of matching rows
     */
    public List<Map<String, Object>> select(String tableName, Object where, Object orderBy) {
        if (!tables.containsKey(tableName)) {
            throw new IllegalArgumentException("Table does not exist: " + tableName);
        }

        List<Map<String, Object>> rows = tables.get(tableName);

        // Apply WHERE filter
        if (where != null) {
            rows = applyWhere(rows, where);
        }

        // Apply ORDER BY
        if (orderBy != null) {
            rows = applyOrderBy(rows, orderBy);
        }

        return rows;
    }

    /**
     * Overloaded select: only table name
     */
    public List<Map<String, Object>> select(String tableName) {
        return select(tableName, null, null);
    }

    /**
     * Overloaded select: table name + where
     */
    public List<Map<String, Object>> select(String tableName, Object where) {
        return select(tableName, where, null);
    }

    /**
     * Apply WHERE filter
     * Supports:
     * 1. Predicate<Map<String, Object>> - lambda function
     * 2. Map<String, Object> - multiple conditions with AND
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> applyWhere(List<Map<String, Object>> rows, Object where) {
        if (where instanceof Predicate) {
            // Lambda function filter
            Predicate<Map<String, Object>> predicate = (Predicate<Map<String, Object>>) where;
            return rows.stream()
                       .filter(predicate)
                       .collect(Collectors.toList());
        } else if (where instanceof Map) {
            // Multiple conditions with AND
            Map<String, Object> conditions = (Map<String, Object>) where;
            return rows.stream()
                       .filter(row -> matchesAllConditions(row, conditions))
                       .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("WHERE must be Predicate or Map");
        }
    }

    /**
     * Check if row matches all conditions (AND logic)
     */
    private boolean matchesAllConditions(Map<String, Object> row, Map<String, Object> conditions) {
        for (Map.Entry<String, Object> condition : conditions.entrySet()) {
            String column = condition.getKey();
            Object expectedValue = condition.getValue();

            if (!row.containsKey(column)) {
                return false;
            }

            Object actualValue = row.get(column);

            // Handle null values
            if (expectedValue == null) {
                if (actualValue != null) return false;
            } else if (!expectedValue.equals(actualValue)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Apply ORDER BY
     * Supports:
     * 1. String - single column
     * 2. List<String> - multiple columns
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> applyOrderBy(List<Map<String, Object>> rows, Object orderBy) {
        List<Map<String, Object>> result = new ArrayList<>(rows);

        if (orderBy instanceof String) {
            // Single column sort
            String column = (String) orderBy;
            result.sort(Comparator.comparing(row -> getComparableValue(row, column)));
        } else if (orderBy instanceof List) {
            // Multiple columns sort
            List<String> columns = (List<String>) orderBy;
            result.sort((row1, row2) -> {
                for (String column : columns) {
                    Comparable val1 = getComparableValue(row1, column);
                    Comparable val2 = getComparableValue(row2, column);

                    @SuppressWarnings("rawtypes")
                    int cmp = compareValues(val1, val2);
                    if (cmp != 0) {
                        return cmp;
                    }
                }
                return 0;
            });
        } else {
            throw new IllegalArgumentException("ORDER BY must be String or List<String>");
        }

        return result;
    }

    /**
     * Get comparable value from row
     */
    @SuppressWarnings("unchecked")
    private Comparable getComparableValue(Map<String, Object> row, String column) {
        Object value = row.get(column);
        if (value == null) {
            return null;
        }
        if (value instanceof Comparable) {
            return (Comparable) value;
        }
        // Fallback: use string representation
        return value.toString();
    }

    /**
     * Compare two values with null handling
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private int compareValues(Comparable val1, Comparable val2) {
        if (val1 == null && val2 == null) return 0;
        if (val1 == null) return -1;
        if (val2 == null) return 1;
        return val1.compareTo(val2);
    }

    /**
     * Print table contents (for debugging)
     */
    public void printTable(String tableName) {
        if (!tables.containsKey(tableName)) {
            System.out.println("Table does not exist: " + tableName);
            return;
        }

        System.out.println("Table: " + tableName);
        List<String> columns = schemas.get(tableName);
        System.out.println("Columns: " + columns);

        List<Map<String, Object>> rows = tables.get(tableName);
        for (Map<String, Object> row : rows) {
            System.out.println(row);
        }
    }

    /**
     * Test the implementation
     */
    public static void main(String[] args) {
        InMemoryDB db = new InMemoryDB();

        // Create table
        db.createTable("users", Arrays.asList("id", "name", "age"));

        // Insert data
        db.insert("users", createRow(1, "Alice", 30));
        db.insert("users", createRow(2, "Bob", 25));
        db.insert("users", createRow(3, "Charlie", 35));
        db.insert("users", createRow(4, "Diana", 30));
        db.insert("users", createRow(5, "Eve", 28));

        System.out.println("=== Test 1: Select all ===");
        List<Map<String, Object>> all = db.select("users");
        all.forEach(System.out::println);
        System.out.println();

        System.out.println("=== Test 2: WHERE with lambda (age > 28) ===");
        Predicate<Map<String, Object>> agePredicate = row -> (Integer) row.get("age") > 28;
        List<Map<String, Object>> filtered = db.select("users", agePredicate);
        filtered.forEach(System.out::println);
        System.out.println();

        System.out.println("=== Test 3: WHERE with lambda + ORDER BY name ===");
        List<Map<String, Object>> filteredSorted = db.select("users", agePredicate, "name");
        filteredSorted.forEach(System.out::println);
        System.out.println();

        System.out.println("=== Test 4: WHERE with Map (age=30) ===");
        Map<String, Object> whereCondition = new HashMap<>();
        whereCondition.put("age", 30);
        List<Map<String, Object>> exact = db.select("users", whereCondition);
        exact.forEach(System.out::println);
        System.out.println();

        System.out.println("=== Test 5: WHERE with Map (multiple conditions: age=30 AND name=Alice) ===");
        Map<String, Object> multiCondition = new HashMap<>();
        multiCondition.put("age", 30);
        multiCondition.put("name", "Alice");
        List<Map<String, Object>> multiFiltered = db.select("users", multiCondition);
        multiFiltered.forEach(System.out::println);
        System.out.println();

        System.out.println("=== Test 6: ORDER BY single column (age) ===");
        List<Map<String, Object>> sortedByAge = db.select("users", null, "age");
        sortedByAge.forEach(System.out::println);
        System.out.println();

        System.out.println("=== Test 7: ORDER BY multiple columns (age, name) ===");
        List<Map<String, Object>> sortedMultiple = db.select("users", null, Arrays.asList("age", "name"));
        sortedMultiple.forEach(System.out::println);
        System.out.println();

        System.out.println("=== Test 8: WHERE + ORDER BY multiple columns ===");
        Predicate<Map<String, Object>> ageFilter = row -> (Integer) row.get("age") >= 28;
        List<Map<String, Object>> complex = db.select("users", ageFilter, Arrays.asList("age", "name"));
        complex.forEach(System.out::println);
        System.out.println();

        System.out.println("=== Test 9: Create another table (products) ===");
        db.createTable("products", Arrays.asList("id", "name", "price", "category"));
        db.insert("products", createProductRow(1, "Laptop", 1200.0, "Electronics"));
        db.insert("products", createProductRow(2, "Mouse", 25.0, "Electronics"));
        db.insert("products", createProductRow(3, "Desk", 300.0, "Furniture"));
        db.insert("products", createProductRow(4, "Chair", 150.0, "Furniture"));

        System.out.println("All products:");
        db.select("products").forEach(System.out::println);
        System.out.println();

        System.out.println("Electronics, sorted by price:");
        Map<String, Object> categoryFilter = new HashMap<>();
        categoryFilter.put("category", "Electronics");
        db.select("products", categoryFilter, "price").forEach(System.out::println);
    }

    // Helper methods to create rows
    private static Map<String, Object> createRow(int id, String name, int age) {
        Map<String, Object> row = new HashMap<>();
        row.put("id", id);
        row.put("name", name);
        row.put("age", age);
        return row;
    }

    private static Map<String, Object> createProductRow(int id, String name, double price, String category) {
        Map<String, Object> row = new HashMap<>();
        row.put("id", id);
        row.put("name", name);
        row.put("price", price);
        row.put("category", category);
        return row;
    }
}

/**
 * INTERVIEW TIPS:
 * ================
 *
 * 1. Start Simple:
 *    - Begin with create_table and insert (5 min)
 *    - Then basic select without filters (5 min)
 *    - Add WHERE support (10 min)
 *    - Add ORDER BY support (10 min)
 *
 * 2. Key Design Decisions:
 *    - Use Map<String, Object> for rows (flexible schema)
 *    - Support both Predicate and Map for WHERE (flexibility)
 *    - Support both String and List<String> for ORDER BY
 *
 * 3. Backward Compatibility:
 *    - Use Object type for where/orderBy parameters
 *    - Check types at runtime
 *    - Provide overloaded methods for convenience
 *
 * 4. Edge Cases to Discuss:
 *    - Null values in WHERE conditions
 *    - Empty tables
 *    - Non-existent columns
 *    - Type mismatches
 *
 * 5. Follow-up Questions:
 *    Q: "What about OR conditions?"
 *    A: Could add another parameter or use complex Predicate
 *
 *    Q: "How to handle indexes?"
 *    A: Build HashMap<column, TreeMap<value, List<rowIndex>>>
 *
 *    Q: "What about JOINs?"
 *    A: Add join() method that takes two tables and join conditions
 *
 *    Q: "Thread safety?"
 *    A: Use ConcurrentHashMap or add synchronized blocks
 *
 * Time to Code: ~30-35 minutes
 * Lines of Code: ~200-250 lines
 */
