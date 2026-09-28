package openai;

import java.util.*;

/**
 * Generic Type Inference - SIMPLIFIED for Interview
 *
 * Problem:
 * Given a function signature with generic types and actual arguments,
 * infer the return type.
 *
 * Example:
 * Function: (T, S) -> [S, T]
 * Call with: (int, str)
 * Return: [str, int]  (T=int, S=str)
 *
 * SIMPLIFIED: Use strings for everything, simple parsing
 */
public class GenericTypeInferenceSimple {

    static class Node {
        String value;  // "int" or "T" or "[int,str]"

        Node(String value) {
            this.value = value;
        }

        boolean isList() {
            return value.startsWith("[");
        }

        boolean isGeneric() {
            // Generic types are single uppercase letters not in brackets
            if (isList()) {
                return value.contains("T") || value.contains("S");
            }
            return value.length() == 1 && Character.isUpperCase(value.charAt(0));
        }

        @Override
        public String toString() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Node)) return false;
            return this.value.equals(((Node) obj).value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }

    static class Function {
        List<Node> params;
        Node output;

        Function(List<Node> params, Node output) {
            this.params = params;
            this.output = output;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("(");
            for (int i = 0; i < params.size(); i++) {
                sb.append(params.get(i));
                if (i < params.size() - 1) sb.append(",");
            }
            sb.append(") -> ").append(output);
            return sb.toString();
        }
    }

    /**
     * Main method: infer return type
     */
    static Node getReturnType(List<Node> actualArgs, Function func) {
        if (actualArgs.size() != func.params.size()) {
            throw new IllegalArgumentException("Parameter count mismatch");
        }

        // Step 1: Build binding map (T -> int, S -> str, etc.)
        Map<String, String> bindings = new HashMap<>();

        for (int i = 0; i < actualArgs.size(); i++) {
            bind(func.params.get(i).value, actualArgs.get(i).value, bindings);
        }

        // Step 2: Replace generics in output
        String result = replace(func.output.value, bindings);
        return new Node(result);
    }

    /**
     * Bind generic types to concrete types
     * Example: bind("T", "int", map) -> map.put("T", "int")
     *          bind("[T,S]", "[int,str]", map) -> T=int, S=str
     */
    static void bind(String pattern, String actual, Map<String, String> bindings) {
        // Case 1: Pattern is single generic (T, S)
        if (pattern.length() == 1 && Character.isUpperCase(pattern.charAt(0))) {
            if (bindings.containsKey(pattern)) {
                // Check consistency
                if (!bindings.get(pattern).equals(actual)) {
                    throw new IllegalArgumentException(
                        pattern + " already bound to " + bindings.get(pattern) +
                        ", cannot bind to " + actual
                    );
                }
            } else {
                bindings.put(pattern, actual);
            }
            return;
        }

        // Case 2: Both are same concrete type
        if (pattern.equals(actual)) {
            return;
        }

        // Case 3: Both are lists - parse and bind recursively
        if (pattern.startsWith("[") && actual.startsWith("[")) {
            List<String> patternParts = parse(pattern);
            List<String> actualParts = parse(actual);

            if (patternParts.size() != actualParts.size()) {
                throw new IllegalArgumentException(
                    "List size mismatch: " + pattern + " vs " + actual
                );
            }

            for (int i = 0; i < patternParts.size(); i++) {
                bind(patternParts.get(i), actualParts.get(i), bindings);
            }
            return;
        }

        // Case 4: Mismatch
        throw new IllegalArgumentException(
            "Type mismatch: " + pattern + " vs " + actual
        );
    }

    /**
     * Parse list string "[int,str]" -> ["int", "str"]
     * Handles nested lists: "[[int,str],float]" -> ["[int,str]", "float"]
     */
    static List<String> parse(String list) {
        // Remove outer brackets
        String inner = list.substring(1, list.length() - 1);

        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int depth = 0;

        for (char c : inner.toCharArray()) {
            if (c == '[') {
                depth++;
                current.append(c);
            } else if (c == ']') {
                depth--;
                current.append(c);
            } else if (c == ',' && depth == 0) {
                parts.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }

        if (current.length() > 0) {
            parts.add(current.toString());
        }

        return parts;
    }

    /**
     * Replace generics in output
     * Example: replace("[S,T]", {T=int, S=str}) -> "[str,int]"
     */
    static String replace(String output, Map<String, String> bindings) {
        // If it's a single generic, replace directly
        if (output.length() == 1 && Character.isUpperCase(output.charAt(0))) {
            return bindings.get(output);
        }

        // If it's a list, parse and replace recursively
        if (output.startsWith("[")) {
            List<String> parts = parse(output);
            List<String> replaced = new ArrayList<>();

            for (String part : parts) {
                replaced.add(replace(part, bindings));
            }

            return "[" + String.join(",", replaced) + "]";
        }

        // Concrete type, return as-is
        return output;
    }

    /**
     * Tests
     */
    public static void main(String[] args) {
        System.out.println("=== Test 1: Simple generic ===");
        Function f1 = new Function(
            Arrays.asList(new Node("T")),
            new Node("T")
        );
        System.out.println("Function: " + f1);
        Node r1 = getReturnType(Arrays.asList(new Node("int")), f1);
        System.out.println("Call: (int)");
        System.out.println("Result: " + r1);  // int
        System.out.println();

        System.out.println("=== Test 2: Two generics ===");
        Function f2 = new Function(
            Arrays.asList(new Node("T"), new Node("S")),
            new Node("[S,T]")
        );
        System.out.println("Function: " + f2);
        Node r2 = getReturnType(
            Arrays.asList(new Node("int"), new Node("str")),
            f2
        );
        System.out.println("Call: (int, str)");
        System.out.println("Result: " + r2);  // [str,int]
        System.out.println();

        System.out.println("=== Test 3: Nested types ===");
        Function f3 = new Function(
            Arrays.asList(new Node("[[T,float],T]"), new Node("S")),
            new Node("[S,T]")
        );
        System.out.println("Function: " + f3);
        Node r3 = getReturnType(
            Arrays.asList(new Node("[[str,float],str]"), new Node("[float,int]")),
            f3
        );
        System.out.println("Call: ([[str,float],str], [float,int])");
        System.out.println("Result: " + r3);  // [[float,int],str]
        System.out.println();

        System.out.println("=== Test 4: Type consistency check ===");
        try {
            Function f4 = new Function(
                Arrays.asList(new Node("T"), new Node("T")),
                new Node("T")
            );
            System.out.println("Function: " + f4);
            getReturnType(
                Arrays.asList(new Node("int"), new Node("str")),
                f4
            );
            System.out.println("ERROR: Mismatch not detected!");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Detected: " + e.getMessage());
        }
        System.out.println();

        System.out.println("=== Test 5: Complex nested ===");
        Function f5 = new Function(
            Arrays.asList(new Node("[T,T]")),
            new Node("T")
        );
        System.out.println("Function: " + f5);
        Node r5 = getReturnType(
            Arrays.asList(new Node("[int,int]")),
            f5
        );
        System.out.println("Call: ([int,int])");
        System.out.println("Result: " + r5);  // int
        System.out.println();

        System.out.println("=== Test 6: Triple nested ===");
        Function f6 = new Function(
            Arrays.asList(new Node("[[[T,S],[S,T]]]")),
            new Node("[T,S]")
        );
        System.out.println("Function: " + f6);
        Node r6 = getReturnType(
            Arrays.asList(new Node("[[[int,str],[str,int]]]")),
            f6
        );
        System.out.println("Call: ([[[int,str],[str,int]]])");
        System.out.println("Result: " + r6);  // [int,str]
    }
}

/**
 * SIMPLIFIED APPROACH:
 * ====================
 *
 * Instead of complex Node tree structure, just use STRINGS!
 *
 * Key Simplifications:
 * 1. Store types as strings: "int", "T", "[int,str]"
 * 2. Simple parsing: split by comma, track bracket depth
 * 3. String replacement instead of tree traversal
 *
 * Step-by-Step (30 minutes):
 * ===========================
 *
 * Step 1 (5 min): Node and Function classes
 * ------------------------------------------
 * class Node {
 *     String value;  // Just store the string!
 * }
 *
 * Step 2 (10 min): Binding logic
 * -------------------------------
 * void bind(String pattern, String actual, Map bindings) {
 *     if (pattern is generic like "T"):
 *         bindings.put("T", actual)
 *     else if (both are lists):
 *         parse both, bind each part
 *     else if (same):
 *         return
 *     else:
 *         throw error
 * }
 *
 * Step 3 (10 min): Parsing lists
 * -------------------------------
 * List<String> parse(String list) {
 *     // "[int,str]" -> ["int", "str"]
 *     // Track bracket depth for nested lists
 * }
 *
 * Step 4 (5 min): Replace generics
 * ---------------------------------
 * String replace(String output, Map bindings) {
 *     if (single generic):
 *         return bindings.get(output)
 *     else if (list):
 *         parse, replace each part, join back
 *     else:
 *         return output
 * }
 *
 * TOTAL: ~30 minutes vs 50+ for complex version!
 *
 * Why This is Better:
 * ===================
 * ✅ Strings instead of tree structures
 * ✅ Simple parsing with bracket counting
 * ✅ No need for clone/equals/hashCode complexity
 * ✅ Easy to debug (just print strings)
 * ✅ ~150 lines vs 300+ in complex version
 *
 * Example Trace:
 * ==============
 * Function: (T, S) -> [S,T]
 * Call: (int, str)
 *
 * bind("T", "int", map) → map = {T=int}
 * bind("S", "str", map) → map = {T=int, S=str}
 * replace("[S,T]", map) → "[str,int]" ✓
 */
