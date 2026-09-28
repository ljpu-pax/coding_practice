import java.util.*;

/**
 * Problem: Implement a helper function to validate if an object matches a required schema
 *
 * Input:
 * 1. object: A nested object structure with key-value pairs
 *    - Values can be primitives (String, Integer, Boolean, etc.)
 *    - Values can be nested objects
 *
 * 2. required_schema: A schema defining required fields and their types
 *    - Specifies field names and expected types
 *    - Can specify nested object requirements
 *
 * Example:
 * Object:
 *   name: "abc"
 *   age: 10
 *   school: "university"
 *   address: {
 *     color: "blue"
 *     height: 100
 *   }
 *
 * Schema:
 *   name: String
 *   age: Integer
 *   school: String
 *   address: {
 *     color: String
 *     height: Integer
 *   }
 *
 * Output: true (all fields match)
 */
public class ObjectSchemaValidator {

    /**
     * Main validation function
     */
    public static ValidationResult validate(Map<String, Object> object, Map<String, Object> schema) {
        return validateHelper(object, schema, "");
    }

    /**
     * Helper function with path tracking for better error messages
     */
    private static ValidationResult validateHelper(Map<String, Object> object,
                                                   Map<String, Object> schema,
                                                   String path) {
        // Check all required fields in schema
        for (Map.Entry<String, Object> schemaEntry : schema.entrySet()) {
            String fieldName = schemaEntry.getKey();
            Object expectedType = schemaEntry.getValue();
            String currentPath = path.isEmpty() ? fieldName : path + "." + fieldName;

            // Check if field exists in object
            if (!object.containsKey(fieldName)) {
                return ValidationResult.failure(
                    "Missing required field: " + currentPath
                );
            }

            Object actualValue = object.get(fieldName);

            // Validate the field
            ValidationResult result = validateField(actualValue, expectedType, currentPath);
            if (!result.isValid) {
                return result;
            }
        }

        return ValidationResult.success();
    }

    /**
     * Validate a single field against its expected type
     */
    private static ValidationResult validateField(Object actualValue,
                                                  Object expectedType,
                                                  String path) {
        // Handle nested object
        if (expectedType instanceof Map) {
            if (!(actualValue instanceof Map)) {
                return ValidationResult.failure(
                    "Expected object at " + path + ", got " + getTypeName(actualValue)
                );
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> nestedObject = (Map<String, Object>) actualValue;
            @SuppressWarnings("unchecked")
            Map<String, Object> nestedSchema = (Map<String, Object>) expectedType;

            return validateHelper(nestedObject, nestedSchema, path);
        }

        // Handle type class
        if (expectedType instanceof Class) {
            Class<?> expectedClass = (Class<?>) expectedType;

            if (!isTypeMatch(actualValue, expectedClass)) {
                return ValidationResult.failure(
                    "Type mismatch at " + path + ": expected " +
                    expectedClass.getSimpleName() + ", got " + getTypeName(actualValue)
                );
            }

            return ValidationResult.success();
        }

        // Handle string type names (e.g., "String", "Integer")
        if (expectedType instanceof String) {
            String typeName = (String) expectedType;

            if (!isTypeNameMatch(actualValue, typeName)) {
                return ValidationResult.failure(
                    "Type mismatch at " + path + ": expected " +
                    typeName + ", got " + getTypeName(actualValue)
                );
            }

            return ValidationResult.success();
        }

        return ValidationResult.failure(
            "Invalid schema type at " + path
        );
    }

    /**
     * Check if value matches expected class
     */
    private static boolean isTypeMatch(Object value, Class<?> expectedClass) {
        if (value == null) {
            return false;
        }

        // Handle primitive wrappers
        if (expectedClass == Integer.class || expectedClass == int.class) {
            return value instanceof Integer;
        }
        if (expectedClass == Double.class || expectedClass == double.class) {
            return value instanceof Double || value instanceof Integer;
        }
        if (expectedClass == Boolean.class || expectedClass == boolean.class) {
            return value instanceof Boolean;
        }
        if (expectedClass == String.class) {
            return value instanceof String;
        }
        if (expectedClass == Long.class || expectedClass == long.class) {
            return value instanceof Long || value instanceof Integer;
        }

        return expectedClass.isInstance(value);
    }

    /**
     * Check if value matches type name string
     */
    private static boolean isTypeNameMatch(Object value, String typeName) {
        if (value == null) {
            return false;
        }

        switch (typeName.toLowerCase()) {
            case "string":
                return value instanceof String;
            case "integer":
            case "int":
                return value instanceof Integer;
            case "double":
            case "float":
                return value instanceof Double || value instanceof Float || value instanceof Integer;
            case "boolean":
            case "bool":
                return value instanceof Boolean;
            case "long":
                return value instanceof Long || value instanceof Integer;
            case "object":
                return value instanceof Map;
            case "array":
            case "list":
                return value instanceof List;
            default:
                return false;
        }
    }

    /**
     * Get type name of an object for error messages
     */
    private static String getTypeName(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof Map) {
            return "Object";
        }
        if (obj instanceof List) {
            return "Array";
        }
        return obj.getClass().getSimpleName();
    }

    /**
     * Validation result class
     */
    static class ValidationResult {
        boolean isValid;
        String errorMessage;

        private ValidationResult(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }

        static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        static ValidationResult failure(String message) {
            return new ValidationResult(false, message);
        }

        @Override
        public String toString() {
            return isValid ? "Valid" : "Invalid: " + errorMessage;
        }
    }

    /**
     * Builder class for easier schema creation
     */
    static class SchemaBuilder {
        private Map<String, Object> schema = new HashMap<>();

        public SchemaBuilder field(String name, Class<?> type) {
            schema.put(name, type);
            return this;
        }

        public SchemaBuilder field(String name, String typeName) {
            schema.put(name, typeName);
            return this;
        }

        public SchemaBuilder field(String name, Map<String, Object> nestedSchema) {
            schema.put(name, nestedSchema);
            return this;
        }

        public Map<String, Object> build() {
            return schema;
        }
    }

    // Test examples
    public static void main(String[] args) {
        System.out.println("=== Example 1: Simple Validation (Pass) ===");
        Map<String, Object> obj1 = new HashMap<>();
        obj1.put("name", "abc");
        obj1.put("age", 10);
        obj1.put("school", "university");

        Map<String, Object> schema1 = new SchemaBuilder()
            .field("name", String.class)
            .field("age", Integer.class)
            .field("school", String.class)
            .build();

        ValidationResult result1 = validate(obj1, schema1);
        System.out.println("Result: " + result1);

        System.out.println("\n=== Example 2: Missing Field (Fail) ===");
        Map<String, Object> obj2 = new HashMap<>();
        obj2.put("name", "abc");
        obj2.put("age", 10);
        // Missing "school"

        ValidationResult result2 = validate(obj2, schema1);
        System.out.println("Result: " + result2);

        System.out.println("\n=== Example 3: Type Mismatch (Fail) ===");
        Map<String, Object> obj3 = new HashMap<>();
        obj3.put("name", "abc");
        obj3.put("age", "10"); // String instead of Integer
        obj3.put("school", "university");

        ValidationResult result3 = validate(obj3, schema1);
        System.out.println("Result: " + result3);

        System.out.println("\n=== Example 4: Nested Object (Pass) ===");
        Map<String, Object> addressObj = new HashMap<>();
        addressObj.put("color", "blue");
        addressObj.put("height", 100);

        Map<String, Object> obj4 = new HashMap<>();
        obj4.put("name", "abc");
        obj4.put("age", 10);
        obj4.put("address", addressObj);

        Map<String, Object> addressSchema = new HashMap<>();
        addressSchema.put("color", String.class);
        addressSchema.put("height", Integer.class);

        Map<String, Object> schema4 = new SchemaBuilder()
            .field("name", String.class)
            .field("age", Integer.class)
            .field("address", addressSchema)
            .build();

        ValidationResult result4 = validate(obj4, schema4);
        System.out.println("Result: " + result4);

        System.out.println("\n=== Example 5: Deeply Nested (Pass) ===");
        Map<String, Object> innerObj = new HashMap<>();
        innerObj.put("value", 42);
        innerObj.put("label", "test");

        Map<String, Object> middleObj = new HashMap<>();
        middleObj.put("color", "blue");
        middleObj.put("height", 100);
        middleObj.put("meta", innerObj);

        Map<String, Object> obj5 = new HashMap<>();
        obj5.put("name", "abc");
        obj5.put("age", 10);
        obj5.put("data", middleObj);

        Map<String, Object> innerSchema = new HashMap<>();
        innerSchema.put("value", Integer.class);
        innerSchema.put("label", String.class);

        Map<String, Object> middleSchema = new HashMap<>();
        middleSchema.put("color", String.class);
        middleSchema.put("height", Integer.class);
        middleSchema.put("meta", innerSchema);

        Map<String, Object> schema5 = new SchemaBuilder()
            .field("name", String.class)
            .field("age", Integer.class)
            .field("data", middleSchema)
            .build();

        ValidationResult result5 = validate(obj5, schema5);
        System.out.println("Result: " + result5);

        System.out.println("\n=== Example 6: Nested Missing Field (Fail) ===");
        Map<String, Object> incompleteAddress = new HashMap<>();
        incompleteAddress.put("color", "blue");
        // Missing "height"

        Map<String, Object> obj6 = new HashMap<>();
        obj6.put("name", "abc");
        obj6.put("age", 10);
        obj6.put("address", incompleteAddress);

        ValidationResult result6 = validate(obj6, schema4);
        System.out.println("Result: " + result6);

        System.out.println("\n=== Example 7: String Type Names ===");
        Map<String, Object> obj7 = new HashMap<>();
        obj7.put("name", "test");
        obj7.put("count", 5);
        obj7.put("active", true);

        Map<String, Object> schema7 = new HashMap<>();
        schema7.put("name", "String");
        schema7.put("count", "Integer");
        schema7.put("active", "Boolean");

        ValidationResult result7 = validate(obj7, schema7);
        System.out.println("Result: " + result7);

        System.out.println("\n=== Example 8: Complex Real-World Example ===");
        Map<String, Object> locationObj = new HashMap<>();
        locationObj.put("latitude", 37.7749);
        locationObj.put("longitude", -122.4194);

        Map<String, Object> userObj = new HashMap<>();
        userObj.put("username", "johndoe");
        userObj.put("email", "john@example.com");
        userObj.put("age", 30);
        userObj.put("isActive", true);
        userObj.put("location", locationObj);

        Map<String, Object> locationSchema = new HashMap<>();
        locationSchema.put("latitude", "Double");
        locationSchema.put("longitude", "Double");

        Map<String, Object> userSchema = new SchemaBuilder()
            .field("username", "String")
            .field("email", "String")
            .field("age", "Integer")
            .field("isActive", "Boolean")
            .field("location", locationSchema)
            .build();

        ValidationResult result8 = validate(userObj, userSchema);
        System.out.println("Result: " + result8);
    }
}

/*
 * Key Implementation Details:
 *
 * 1. Recursive Validation:
 *    - Use recursive helper to handle nested objects
 *    - Track current path for detailed error messages
 *
 * 2. Type Checking:
 *    - Support Class<?> objects (e.g., String.class, Integer.class)
 *    - Support string type names (e.g., "String", "Integer")
 *    - Handle nested objects as Map<String, Object>
 *
 * 3. Schema Format:
 *    - Map<String, Object> where value can be:
 *      - Class<?> for type checking
 *      - String for type name
 *      - Map<String, Object> for nested schema
 *
 * 4. Error Reporting:
 *    - ValidationResult contains success flag and error message
 *    - Error messages include full path to problematic field
 *    - First error stops validation (fail-fast)
 *
 * 5. Type Matching:
 *    - Handle primitive wrappers (Integer, Boolean, etc.)
 *    - Handle numeric coercion (Integer can be used where Long expected)
 *    - Case-insensitive string type names
 *
 * Time Complexity: O(n) where n is total number of fields (including nested)
 * Space Complexity: O(d) where d is maximum nesting depth (recursion stack)
 *
 * Extensions:
 * - Optional fields (use Optional<T> or special marker)
 * - Array/List validation with element type
 * - Custom validators (regex for strings, range for numbers)
 * - Union types (field can be multiple types)
 * - Collect all errors instead of fail-fast
 */
