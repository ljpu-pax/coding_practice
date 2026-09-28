package openai;

import java.util.*;

/**
 * Toy Language Type System - SIMPLIFIED
 *
 * Types:
 * - Primitives: int, char, float
 * - Generics: T1, T2, T3
 * - Tuples: [int, T1, char]
 *
 * Example:
 * Function: [T1, T2, int, T1] -> [T1, T2]
 * Params: [int, char, int, int]
 * Return: [int, char]
 */
public class ToyLanguageSimple {

    /**
     * Node - can be primitive, generic, or tuple
     */
    static class Node {
        String value;        // "int", "T1", or null
        List<Node> children; // for tuples

        Node(String value) {
            this.value = value;
        }

        Node(List<Node> children) {
            this.children = children;
        }

        @Override
        public String toString() {
            if (value != null) return value;

            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < children.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(children.get(i));
            }
            return sb.append("]").toString();
        }
    }

    /**
     * Function signature
     */
    static class Function {
        List<Node> params;
        Node returnType;

        Function(List<Node> params, Node returnType) {
            this.params = params;
            this.returnType = returnType;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < params.size(); i++) {
                if (i > 0) sb.append("; ");
                sb.append(params.get(i));
            }
            return sb.append("] -> ").append(returnType).toString();
        }
    }

    /**
     * Main method: infer return type
     */
    static Node inferReturn(Function func, List<Node> params) {
        if (func.params.size() != params.size()) {
            throw new IllegalArgumentException("Parameter count mismatch");
        }

        // Build bindings: T1 -> int, T2 -> char, etc.
        Map<String, Node> bindings = new HashMap<>();

        for (int i = 0; i < params.size(); i++) {
            bind(func.params.get(i), params.get(i), bindings);
        }

        // Replace generics in return type
        return substitute(func.returnType, bindings);
    }

    /**
     * Bind generic types to concrete types
     */
    static void bind(Node pattern, Node actual, Map<String, Node> bindings) {
        // Generic like T1, T2
        if (pattern.value != null && pattern.value.matches("T\\d+")) {
            if (bindings.containsKey(pattern.value)) {
                // Check consistency
                if (!bindings.get(pattern.value).toString().equals(actual.toString())) {
                    throw new IllegalArgumentException(
                        "Conflict: " + pattern.value + " = " +
                        bindings.get(pattern.value) + ", not " + actual
                    );
                }
            } else {
                bindings.put(pattern.value, actual);
            }
            return;
        }

        // Primitive like int, char
        if (pattern.value != null) {
            if (actual.value == null || !pattern.value.equals(actual.value)) {
                throw new IllegalArgumentException(
                    "Mismatch: expected " + pattern + ", got " + actual
                );
            }
            return;
        }

        // Tuple
        if (pattern.children != null) {
            if (actual.children == null) {
                throw new IllegalArgumentException("Expected tuple, got " + actual);
            }
            if (pattern.children.size() != actual.children.size()) {
                throw new IllegalArgumentException("Tuple size mismatch");
            }

            for (int i = 0; i < pattern.children.size(); i++) {
                bind(pattern.children.get(i), actual.children.get(i), bindings);
            }
        }
    }

    /**
     * Replace generics with bound types
     */
    static Node substitute(Node node, Map<String, Node> bindings) {
        // Generic - replace
        if (node.value != null && bindings.containsKey(node.value)) {
            return bindings.get(node.value);
        }

        // Primitive - keep as-is
        if (node.value != null) {
            return node;
        }

        // Tuple - recursively substitute children
        List<Node> newChildren = new ArrayList<>();
        for (Node child : node.children) {
            newChildren.add(substitute(child, bindings));
        }
        return new Node(newChildren);
    }

    /**
     * Tests
     */
    public static void main(String[] args) {
        System.out.println("=== Part 1: toString ===");

        Node int1 = new Node("int");
        Node t1 = new Node("T1");
        Node tuple = new Node(Arrays.asList(int1, t1, new Node("char")));

        System.out.println("int: " + int1);
        System.out.println("T1: " + t1);
        System.out.println("Tuple: " + tuple);

        Function f = new Function(
            Arrays.asList(t1, new Node("T2"), int1),
            new Node(Arrays.asList(t1, new Node("T2")))
        );
        System.out.println("Function: " + f);
        System.out.println();

        System.out.println("=== Part 2: Inference ===");

        // Test 1: Basic
        Function f1 = new Function(
            Arrays.asList(
                new Node("T1"),
                new Node("T2"),
                new Node("int"),
                new Node("T1")
            ),
            new Node(Arrays.asList(new Node("T1"), new Node("T2")))
        );
        System.out.println("Func: " + f1);

        List<Node> p1 = Arrays.asList(
            new Node("int"),
            new Node("char"),
            new Node("int"),
            new Node("int")
        );
        System.out.println("Params: [int, char, int, int]");
        System.out.println("Return: " + inferReturn(f1, p1));  // [int, char]
        System.out.println();

        // Test 2: Type mismatch
        System.out.println("Test: Type mismatch");
        try {
            List<Node> p2 = Arrays.asList(
                new Node("int"),
                new Node("int"),
                new Node("int"),
                new Node("int")
            );
            inferReturn(f1, p2);
        } catch (Exception e) {
            System.out.println("✓ " + e.getMessage());
        }
        System.out.println();

        // Test 3: Type conflict
        System.out.println("Test: Type conflict");
        try {
            List<Node> p3 = Arrays.asList(
                new Node("int"),
                new Node("int"),
                new Node("int"),
                new Node("char")
            );
            inferReturn(f1, p3);
        } catch (Exception e) {
            System.out.println("✓ " + e.getMessage());
        }
        System.out.println();

        // Test 4: Nested
        System.out.println("Test: Nested tuples");
        Function f2 = new Function(
            Arrays.asList(
                new Node(Arrays.asList(new Node("T1"), new Node("T2")))
            ),
            new Node(Arrays.asList(new Node("T2"), new Node("T1")))
        );
        System.out.println("Func: " + f2);

        List<Node> p4 = Arrays.asList(
            new Node(Arrays.asList(new Node("int"), new Node("char")))
        );
        System.out.println("Params: [[int, char]]");
        System.out.println("Return: " + inferReturn(f2, p4));  // [char, int]
    }
}

/**
 * SIMPLIFIED APPROACH (30 min):
 * ==============================
 *
 * Step 1 (10 min): Classes & toString
 * ------------------------------------
 * class Node {
 *     String value;      // "int" or "T1"
 *     List<Node> children; // for tuples
 * }
 *
 * toString(): value OR "[child1, child2, ...]"
 *
 * Step 2 (10 min): Binding
 * -------------------------
 * void bind(pattern, actual, map) {
 *     if (pattern is generic):
 *         check conflict, then map.put(T1, actual)
 *     else if (pattern is primitive):
 *         check match
 *     else (tuple):
 *         recursively bind children
 * }
 *
 * Step 3 (10 min): Substitution
 * ------------------------------
 * Node substitute(node, map) {
 *     if (generic): return map.get(node.value)
 *     if (primitive): return node
 *     if (tuple): recursively substitute children
 * }
 *
 * KEY SIMPLIFICATIONS:
 * ====================
 * ✅ No helper methods like isPrimitive()
 * ✅ Use regex "T\\d+" to detect generics inline
 * ✅ Use toString() for equality checks
 * ✅ Short error messages
 * ✅ ~120 lines vs 250+ in complex version
 *
 * This is realistic for 30-40 minute interview! ✅
 */
