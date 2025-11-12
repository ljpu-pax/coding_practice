package coinbase;

import java.util.*;

/**
 * InMemoryDB - A simple in-memory database implementation
 * 
 * This class provides basic CRUD operations for storing key-field-value data.
 * Each key can have multiple fields, and each field has a corresponding value.
 * 
 * Example usage:
 * InMemoryDB db = new InMemoryDB();
 * db.setData("user1", "name", "John");
 * db.setData("user1", "age", "25");
 * String name = db.getData("user1", "name"); // returns "John"
 * boolean deleted = db.deleteData("user1", "age"); // returns true
 */
public class InMemoryDB {
    
    // Map to store data: key -> (field -> value)
    private Map<String, Map<String, String>> database;
    
    public InMemoryDB() {
        this.database = new HashMap<>();
    }
    
    /**
     * Sets a field-value pair for the given key.
     * If the field already exists, replaces the existing value.
     * If the record doesn't exist, creates a new one.
     * 
     * @param key the record key
     * @param field the field name
     * @param value the field value
     * 
     * Example:
     * db.setData("user1", "name", "John");
     * db.setData("user1", "age", "25");
     */
    public void setData(String key, String field, String value) {
        // Create a new record if it doesn't exist
        database.putIfAbsent(key, new HashMap<>());
        
        // Set the field-value pair
        database.get(key).put(field, value);
    }
    
    /**
     * Gets the value for the specified field in the given record.
     * Returns empty string if the record or field doesn't exist.
     * 
     * @param key the record key
     * @param field the field name
     * @return the field value, or empty string if not found
     * 
     * Example:
     * String name = db.getData("user1", "name"); // returns "John"
     * String city = db.getData("user1", "city"); // returns ""
     */
    public String getData(String key, String field) {
        // Check if the record exists
        if (!database.containsKey(key)) {
            return "";
        }
        
        // Get the field value, return empty string if field doesn't exist
        return database.get(key).getOrDefault(field, "");
    }
    
    /**
     * Deletes the specified field from the given record.
     * Returns true if the field was successfully deleted, false otherwise.
     * 
     * @param key the record key
     * @param field the field name
     * @return true if field was deleted, false if key or field doesn't exist
     * 
     * Example:
     * boolean deleted = db.deleteData("user1", "age"); // returns true
     * boolean notFound = db.deleteData("user1", "city"); // returns false
     */
    public boolean deleteData(String key, String field) {
        // Check if the record exists
        if (!database.containsKey(key)) {
            return false;
        }
        
        // Check if the field exists and remove it
        Map<String, String> record = database.get(key);
        if (record.containsKey(field)) {
            record.remove(field);
            return true;
        }
        
        return false;
    }
    
    /**
     * Gets all fields for a given key.
     * 
     * @param key the record key
     * @return map of field-value pairs, or empty map if key doesn't exist
     */
    public Map<String, String> getAllFields(String key) {
        return database.getOrDefault(key, new HashMap<>());
    }
    
    /**
     * Checks if a record exists for the given key.
     * 
     * @param key the record key
     * @return true if record exists, false otherwise
     */
    public boolean hasRecord(String key) {
        return database.containsKey(key);
    }
    
    /**
     * Gets all keys in the database.
     * 
     * @return set of all keys
     */
    public Set<String> getAllKeys() {
        return new HashSet<>(database.keySet());
    }
    
    /**
     * Clears all data from the database.
     */
    public void clear() {
        database.clear();
    }
    
    /**
     * Main method with example usage and test cases
     */
    public static void main(String[] args) {
        InMemoryDB db = new InMemoryDB();
        
        System.out.println("=== InMemoryDB Example Usage ===");
        
        // Test setData
        System.out.println("\n1. Setting data:");
        db.setData("user1", "name", "John");
        db.setData("user1", "age", "25");
        db.setData("user1", "city", "New York");
        db.setData("user2", "name", "Jane");
        db.setData("user2", "age", "30");
        
        // Test getData
        System.out.println("\n2. Getting data:");
        System.out.println("user1.name = " + db.getData("user1", "name"));
        System.out.println("user1.age = " + db.getData("user1", "age"));
        System.out.println("user1.city = " + db.getData("user1", "city"));
        System.out.println("user2.name = " + db.getData("user2", "name"));
        System.out.println("user1.nonexistent = '" + db.getData("user1", "nonexistent") + "'");
        System.out.println("nonexistent.field = '" + db.getData("nonexistent", "field") + "'");
        
        // Test deleteData
        System.out.println("\n3. Deleting data:");
        System.out.println("Delete user1.age: " + db.deleteData("user1", "age"));
        System.out.println("Delete user1.nonexistent: " + db.deleteData("user1", "nonexistent"));
        System.out.println("Delete nonexistent.field: " + db.deleteData("nonexistent", "field"));
        
        // Verify deletion
        System.out.println("\n4. After deletion:");
        System.out.println("user1.age = '" + db.getData("user1", "age") + "'");
        System.out.println("user1.name = " + db.getData("user1", "name"));
        
        // Test updating existing field
        System.out.println("\n5. Updating existing field:");
        db.setData("user1", "name", "Johnny");
        System.out.println("user1.name after update = " + db.getData("user1", "name"));
        
        // Show all data
        System.out.println("\n6. All records:");
        for (String key : db.getAllKeys()) {
            System.out.println(key + ": " + db.getAllFields(key));
        }
    }
}
