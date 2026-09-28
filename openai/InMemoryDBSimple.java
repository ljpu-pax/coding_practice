package openai;

import java.util.*;

/**
 * In-Memory Database - SIMPLIFIED for Interview
 *
 * Problem:
 * Implement an in-memory database with:
 * - createTable(table_name, columns)
 * - insert(table_name, row)
 * - select(table_name, where, order_by)
 *
 * WHERE: Multiple conditions with AND
 * ORDER BY: Single or multiple columns
 *
 * SIMPLIFIED: No Predicate, just Map-based filtering
 */
public class InMemoryDBSimple {

    // Storage
    private Map<String, List<Map<String, Object>>> tables;
    private Map<String, List<String>> schemas;

    public InMemoryDBSimple() {
        this.tables = new HashMap<>();
        this.schemas = new HashMap<>();
    }

    /**
     * Create table with schema
     */
    public void createTable(String tableName, List<String> columns) {
        schemas.put(tableName, columns);
        tables.put(tableName, new ArrayList<>());
    }

    /**
     * Insert row into table
     */
    public void insert(String tableName, Map<String, Object> row) {
        tables.get(tableName).add(new HashMap<>(row));
    }

    /**
     * Select with optional WHERE and ORDER BY
     *
     * @param where - Map of column:value pairs (AND logic)
     * @param orderBy - List of column names to sort by
     */
    public List<Map<String, Object>> select(String tableName,
                                            Map<String, Object> where,
                                            List<String> orderBy) {
        List<Map<String, Object>> rows = new ArrayList<>(tables.get(tableName));

        // Apply WHERE filter
        if (where != null && !where.isEmpty()) {
            rows = filterRows(rows, where);
        }

        // Apply ORDER BY
        if (orderBy != null && !orderBy.isEmpty()) {
            rows = sortRows(rows, orderBy);
        }

        return rows;
    }

    /**
     * Filter rows by WHERE conditions (AND logic)
     */
    private List<Map<String, Object>> filterRows(List<Map<String, Object>> rows,
                                                  Map<String, Object> where) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            boolean match = true;

            // Check all conditions
            for (String column : where.keySet()) {
                Object expected = where.get(column);
                Object actual = row.get(column);

                if (!Objects.equals(expected, actual)) {
                    match = false;
                    break;
                }
            }

            if (match) {
                result.add(row);
            }
        }

        return result;
    }

    /**
     * Sort rows by ORDER BY columns
     */
    private List<Map<String, Object>> sortRows(List<Map<String, Object>> rows,
                                                List<String> orderBy) {
        List<Map<String, Object>> result = new ArrayList<>(rows);

        result.sort((row1, row2) -> {
            for (String column : orderBy) {
                Object val1 = row1.get(column);
                Object val2 = row2.get(column);

                int cmp = compareValues(val1, val2);
                if (cmp != 0) {
                    return cmp;
                }
            }
            return 0;
        });

        return result;
    }

    /**
     * Compare two values (handles nulls and different types)
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private int compareValues(Object val1, Object val2) {
        if (val1 == null && val2 == null) return 0;
        if (val1 == null) return -1;
        if (val2 == null) return 1;

        // Try to compare as Comparable
        if (val1 instanceof Comparable && val2 instanceof Comparable) {
            return ((Comparable) val1).compareTo(val2);
        }

        // Fallback: compare strings
        return val1.toString().compareTo(val2.toString());
    }

    /**
     * Convenience methods for backward compatibility
     */
    public List<Map<String, Object>> select(String tableName) {
        return select(tableName, null, null);
    }

    public List<Map<String, Object>> select(String tableName, Map<String, Object> where) {
        return select(tableName, where, null);
    }

    /**
     * Helper to create row easily
     */
    public static Map<String, Object> row(Object... keyValues) {
        Map<String, Object> row = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            row.put((String) keyValues[i], keyValues[i + 1]);
        }
        return row;
    }

    /**
     * Test cases
     */
    public static void main(String[] args) {
        InMemoryDBSimple db = new InMemoryDBSimple();

        // Create table
        db.createTable("users", Arrays.asList("id", "name", "age"));

        // Insert data
        db.insert("users", row("id", 1, "name", "Alice", "age", 30));
        db.insert("users", row("id", 2, "name", "Bob", "age", 25));
        db.insert("users", row("id", 3, "name", "Charlie", "age", 35));
        db.insert("users", row("id", 4, "name", "Diana", "age", 30));
        db.insert("users", row("id", 5, "name", "Eve", "age", 28));

        System.out.println("=== Test 1: Select all ===");
        print(db.select("users"));

        System.out.println("\n=== Test 2: WHERE age=30 ===");
        print(db.select("users", row("age", 30)));

        System.out.println("\n=== Test 3: WHERE age=30 AND name=Alice ===");
        print(db.select("users", row("age", 30, "name", "Alice")));

        System.out.println("\n=== Test 4: ORDER BY age ===");
        print(db.select("users", null, Arrays.asList("age")));

        System.out.println("\n=== Test 5: ORDER BY age, name ===");
        print(db.select("users", null, Arrays.asList("age", "name")));

        System.out.println("\n=== Test 6: WHERE age=30, ORDER BY name ===");
        print(db.select("users", row("age", 30), Arrays.asList("name")));

        // Products table
        System.out.println("\n=== Test 7: Products table ===");
        db.createTable("products", Arrays.asList("id", "name", "price", "category"));
        db.insert("products", row("id", 1, "name", "Laptop", "price", 1200, "category", "Electronics"));
        db.insert("products", row("id", 2, "name", "Mouse", "price", 25, "category", "Electronics"));
        db.insert("products", row("id", 3, "name", "Desk", "price", 300, "category", "Furniture"));

        System.out.println("Electronics, sorted by price:");
        print(db.select("products",
                        row("category", "Electronics"),
                        Arrays.asList("price")));
    }

    private static void print(List<Map<String, Object>> rows) {
        rows.forEach(System.out::println);
    }
}

/**
 * INTERVIEW CODING PLAN (25 minutes):
 * ====================================
 *
 * Step 1 (5 min): Basic structure
 * --------------------------------
 * class InMemoryDBSimple {
 *     Map<String, List<Map<String, Object>>> tables;
 *
 *     void createTable(String name, List<String> cols) {
 *         tables.put(name, new ArrayList<>());
 *     }
 *
 *     void insert(String table, Map<String, Object> row) {
 *         tables.get(table).add(row);
 *     }
 * }
 *
 * Step 2 (5 min): Basic select
 * -----------------------------
 * List<Map<String, Object>> select(String table, Map where, List orderBy) {
 *     return new ArrayList<>(tables.get(table));
 * }
 *
 * Step 3 (7 min): Add WHERE filter
 * ---------------------------------
 * if (where != null) {
 *     for each row:
 *         check if all conditions match
 *         if yes, add to result
 * }
 *
 * Step 4 (8 min): Add ORDER BY
 * -----------------------------
 * if (orderBy != null) {
 *     result.sort((r1, r2) -> {
 *         for each column:
 *             compare values
 *             if different, return comparison
 *         return 0
 *     })
 * }
 *
 * KEY SIMPLIFICATIONS vs Complex Version:
 * ========================================
 * ✅ No Predicate/lambda support (just Map-based WHERE)
 * ✅ No type checking at runtime (trust the input)
 * ✅ Simple helper method row() for easy testing
 * ✅ Removed @SuppressWarnings complexity
 * ✅ ~150 lines vs 350+ in complex version
 *
 * WHAT TO SAY IN INTERVIEW:
 * ==========================
 * "I'll start with a simple approach using Maps for storage.
 *  For WHERE, I'll support Map-based conditions with AND logic.
 *  For ORDER BY, I'll support multiple columns using a comparator.
 *  This keeps it simple while meeting all requirements."
 *
 * FOLLOW-UP IMPROVEMENTS:
 * =======================
 * - Add Predicate support for WHERE (if asked)
 * - Add OR logic (nested where conditions)
 * - Add indexes (HashMap for fast lookups)
 * - Add JOIN support
 * - Add DELETE/UPDATE operations
 */
