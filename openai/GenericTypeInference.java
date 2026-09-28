package openai;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Generic Type Inference System
 *
 * Problem:
 * Implement a type system that supports generic types and type inference.
 *
 * Classes:
 * 1. Node - Represents a type (either base type like "int" or composite like "[T, float]")
 * 2. Function - Represents a function signature with parameters and return type
 *
 * Key Features:
 * - Support generic types (T, S, etc.)
 * - Support nested types ([T, [S, int]])
 * - Type inference: given function signature and actual arguments, infer return type
 * - Type binding: match generic types with concrete types
 *
 * Example:
 * Function: ([[T, float], T], S) -> [S, T]
 * Invocation: ([[str, float], str], [float, int])
 * Result: [float, int], str] (T=str, S=[float, int])
 *
 * Time Complexity: O(N) where N is total nodes in type tree
 * Space Complexity: O(N) for binding map and cloning
 */
public class GenericTypeInference {

    /**
     * Node - Represents a type (base or composite)
     */
    static class Node {
        private static final Set<String> BASE_TYPES = new HashSet<>(Arrays.asList("str", "float", "int"));

        String base;           // Base type like "int", "T", null if composite
        List<Node> children;   // Child nodes for composite types like [T, float]

        // Constructor for base type
        Node(String base) {
            this.base = base;
            this.children = new ArrayList<>();
        }

        // Constructor for composite type
        Node(List<Node> children) {
            this.base = null;
            this.children = new ArrayList<>(children);
        }

        /**
         * Get content - either base string or children list
         */
        Object getContent() {
            if (base != null) {
                return base;
            }
            return children;
        }

        /**
         * Check if this is a base generic type (like T, S, not int/float/str)
         */
        boolean isBaseGenericType() {
            return base != null && !BASE_TYPES.contains(base);
        }

        /**
         * Check if this or any child contains generic types
         */
        boolean isGenericType() {
            if (isBaseGenericType()) {
                return true;
            }

            for (Node child : children) {
                if (child.isGenericType()) {
                    return true;
                }
            }

            return false;
        }

        /**
         * Clone this node (deep copy)
         */
        Node clone() {
            if (base != null) {
                return new Node(base);
            }

            List<Node> clonedChildren = children.stream()
                                                 .map(Node::clone)
                                                 .collect(Collectors.toList());
            return new Node(clonedChildren);
        }

        /**
         * String representation
         */
        @Override
        public String toString() {
            if (base != null) {
                return base;
            }

            String childrenStr = children.stream()
                                         .map(Node::toString)
                                         .collect(Collectors.joining(","));
            return "[" + childrenStr + "]";
        }

        /**
         * Equality check
         */
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Node)) {
                return false;
            }
            Node other = (Node) obj;
            return this.toString().equals(other.toString());
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }
    }

    /**
     * Function - Represents function signature
     */
    static class Function {
        List<Node> parameters;
        Node outputType;

        Function(List<Node> parameters, Node outputType) {
            this.parameters = parameters;
            this.outputType = outputType;
        }

        @Override
        public String toString() {
            String paramStr = parameters.stream()
                                        .map(Node::toString)
                                        .collect(Collectors.joining(","));
            return "(" + paramStr + ") -> " + outputType.toString();
        }
    }

    /**
     * Bind generic types to concrete types
     *
     * @param funcParam Function parameter type (may contain generics like T, S)
     * @param actualParam Actual argument type (concrete types)
     * @param bindingMap Map to store bindings (T -> int, S -> str, etc.)
     */
    static void binding(Node funcParam, Node actualParam, Map<String, Node> bindingMap) {
        // Case 1: funcParam is a base generic type (like T, S)
        if (funcParam.isGenericType() && funcParam.base != null) {
            // Check if already bound
            if (bindingMap.containsKey(funcParam.base)) {
                Node existing = bindingMap.get(funcParam.base);
                if (!existing.equals(actualParam)) {
                    throw new IllegalArgumentException(
                        "Type mismatch: " + funcParam.base + " already bound to " +
                        existing + ", cannot bind to " + actualParam
                    );
                }
            } else {
                // Bind generic type to concrete type
                bindingMap.put(funcParam.base, actualParam);
            }
            return;
        }

        // Case 2: Both are same concrete type
        if (funcParam.equals(actualParam)) {
            return;
        }

        // Case 3: Both are composite types - recursively bind children
        if (funcParam.base == null && actualParam.base == null) {
            if (funcParam.children.size() != actualParam.children.size()) {
                throw new IllegalArgumentException(
                    "Composite type size mismatch: " + funcParam + " vs " + actualParam
                );
            }

            for (int i = 0; i < funcParam.children.size(); i++) {
                binding(funcParam.children.get(i), actualParam.children.get(i), bindingMap);
            }
            return;
        }

        // Case 4: Type mismatch
        throw new IllegalArgumentException(
            "Parameter type mismatch: expected " + funcParam + ", got " + actualParam
        );
    }

    /**
     * Replace generic types in output with bound concrete types
     *
     * @param node Output type node (may contain generics)
     * @param bindingMap Map of generic type bindings
     * @return New node with generics replaced by concrete types
     */
    static Node replaceInvocationArguments(Node node, Map<String, Node> bindingMap) {
        // No generics - return clone
        if (!node.isGenericType()) {
            return node.clone();
        }

        // Base generic type - replace with bound type
        if (node.children.isEmpty()) {
            Node boundNode = bindingMap.get(node.base);
            if (boundNode == null) {
                throw new IllegalArgumentException("Unbound generic type: " + node.base);
            }
            return boundNode.clone();
        }

        // Composite type - recursively replace children
        List<Node> replacedChildren = node.children.stream()
                                                    .map(child -> replaceInvocationArguments(child, bindingMap))
                                                    .collect(Collectors.toList());
        return new Node(replacedChildren);
    }

    /**
     * Get return type for function invocation
     *
     * @param actualParams Actual argument types
     * @param function Function signature
     * @return Inferred return type
     */
    static Node getReturnType(List<Node> actualParams, Function function) {
        // Check parameter count
        if (actualParams.size() != function.parameters.size()) {
            throw new IllegalArgumentException(
                "Parameter count mismatch: expected " + function.parameters.size() +
                ", got " + actualParams.size()
            );
        }

        // Build binding map by matching parameters
        Map<String, Node> bindingMap = new HashMap<>();

        for (int i = 0; i < actualParams.size(); i++) {
            binding(function.parameters.get(i), actualParams.get(i), bindingMap);
        }

        // If output has no generics, return as-is
        if (!function.outputType.isGenericType()) {
            return function.outputType;
        }

        // Replace generics in output with bound types
        return replaceInvocationArguments(function.outputType, bindingMap);
    }

    /**
     * Test cases
     */
    public static void main(String[] args) {
        System.out.println("=== Test 1: Simple generic type ===");
        // Function: (T) -> T
        Function func1 = new Function(
            Arrays.asList(new Node("T")),
            new Node("T")
        );
        System.out.println("Function: " + func1);

        Node result1 = getReturnType(
            Arrays.asList(new Node("int")),
            func1
        );
        System.out.println("Invocation: (int)");
        System.out.println("Return type: " + result1); // int
        System.out.println();

        System.out.println("=== Test 2: Two generic types ===");
        // Function: (T, S) -> [S, T]
        Function func2 = new Function(
            Arrays.asList(new Node("T"), new Node("S")),
            new Node(Arrays.asList(new Node("S"), new Node("T")))
        );
        System.out.println("Function: " + func2);

        Node result2 = getReturnType(
            Arrays.asList(new Node("int"), new Node("str")),
            func2
        );
        System.out.println("Invocation: (int, str)");
        System.out.println("Return type: " + result2); // [str, int]
        System.out.println();

        System.out.println("=== Test 3: Nested generic types ===");
        // Build: [[T, float], T]
        Node node1 = new Node("T");
        Node node2 = new Node("float");
        Node node3 = new Node("T");
        Node node4 = new Node(Arrays.asList(node1, node2));
        Node node5 = new Node(Arrays.asList(node4, node3));

        // Function: ([[T, float], T], S) -> [S, T]
        Function func3 = new Function(
            Arrays.asList(node5, new Node("S")),
            new Node(Arrays.asList(new Node("S"), new Node("T")))
        );
        System.out.println("Function: " + func3);

        // Invocation arguments: [[str, float], str], [float, int]
        Node node11 = new Node("str");
        Node node22 = new Node("float");
        Node node33 = new Node("str");
        Node node44 = new Node(Arrays.asList(node11, node22));
        Node node55 = new Node(Arrays.asList(node44, node33));

        Node arg1 = node55;
        Node arg2 = new Node(Arrays.asList(new Node("float"), new Node("int")));

        Node result3 = getReturnType(Arrays.asList(arg1, arg2), func3);
        System.out.println("Invocation: ([[str, float], str], [float, int])");
        System.out.println("Return type: " + result3); // [[float, int], str]
        System.out.println();

        System.out.println("=== Test 4: Generic type binding ===");
        // Function: ([T, T]) -> T
        Function func4 = new Function(
            Arrays.asList(new Node(Arrays.asList(new Node("T"), new Node("T")))),
            new Node("T")
        );
        System.out.println("Function: " + func4);

        Node result4 = getReturnType(
            Arrays.asList(new Node(Arrays.asList(new Node("int"), new Node("int")))),
            func4
        );
        System.out.println("Invocation: ([int, int])");
        System.out.println("Return type: " + result4); // int
        System.out.println();

        System.out.println("=== Test 5: Type mismatch detection ===");
        try {
            // Function: (T, T) -> T
            Function func5 = new Function(
                Arrays.asList(new Node("T"), new Node("T")),
                new Node("T")
            );
            System.out.println("Function: " + func5);

            // Try to call with different types: (int, str) - should fail!
            getReturnType(
                Arrays.asList(new Node("int"), new Node("str")),
                func5
            );
            System.out.println("ERROR: Type mismatch not detected!");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Type mismatch detected: " + e.getMessage());
        }
        System.out.println();

        System.out.println("=== Test 6: Complex nested example ===");
        // Function: ([[T, S], [S, T]]) -> [T, S]
        Function func6 = new Function(
            Arrays.asList(
                new Node(Arrays.asList(
                    new Node(Arrays.asList(new Node("T"), new Node("S"))),
                    new Node(Arrays.asList(new Node("S"), new Node("T")))
                ))
            ),
            new Node(Arrays.asList(new Node("T"), new Node("S")))
        );
        System.out.println("Function: " + func6);

        Node result6 = getReturnType(
            Arrays.asList(
                new Node(Arrays.asList(
                    new Node(Arrays.asList(new Node("int"), new Node("str"))),
                    new Node(Arrays.asList(new Node("str"), new Node("int")))
                ))
            ),
            func6
        );
        System.out.println("Invocation: ([[int, str], [str, int]])");
        System.out.println("Return type: " + result6); // [int, str]
    }
}

/**
 * INTERVIEW STRATEGY:
 * ===================
 *
 * Step 1 (10 min): Node class
 * ----------------------------
 * - Constructor for base and composite types
 * - toString() method
 * - equals() method
 *
 * Step 2 (5 min): Function class
 * -------------------------------
 * - Store parameters and output type
 * - toString() method
 *
 * Step 3 (15 min): Type binding
 * ------------------------------
 * - binding() method: match generic types to concrete types
 * - Handle base generic types (T, S)
 * - Handle composite types recursively
 * - Detect conflicts
 *
 * Step 4 (10 min): Type replacement
 * ----------------------------------
 * - replaceInvocationArguments() method
 * - Replace generics in output with bound types
 * - Handle nested structures
 *
 * Step 5 (5 min): Main method
 * ----------------------------
 * - getReturnType() ties it all together
 * - Validate parameter count
 * - Build binding map
 * - Replace generics in output
 *
 * Step 6 (5 min): Testing
 * ------------------------
 * - Test simple cases
 * - Test nested cases
 * - Test error cases
 *
 * TOTAL: ~50 minutes
 *
 * KEY INSIGHTS:
 * =============
 * 1. Generic types are like variables that get "bound" to concrete types
 * 2. Binding happens by pattern matching parameters with arguments
 * 3. Once bound, replace all occurrences in output type
 * 4. Must handle nested structures recursively
 *
 * FOLLOW-UP QUESTIONS:
 * ====================
 * Q: "What about multiple occurrences of same generic type?"
 * A: binding() checks consistency - T must bind to same type everywhere
 *
 * Q: "What about unbounded generics in output?"
 * A: Throw error - all generics must be bound by parameters
 *
 * Q: "What about subtyping (int extends Number)?"
 * A: Current version uses exact matching, could extend with type hierarchy
 */
