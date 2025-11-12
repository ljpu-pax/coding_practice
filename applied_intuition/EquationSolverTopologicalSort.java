import java.util.*;

/**
 * Applied Intuition Interview Question
 *
 * Problem: Solve equations using Topological Sort
 *
 * Input: ["a = b + c", "c = 4", "b = c + 5"]
 * Output: {"a": 13, "b": 9, "c": 4}
 *
 * Solution: Topological Sort (Kahn's Algorithm)
 *
 * Why Topological Sort?
 * - Equations have dependencies: a depends on b and c
 * - Must solve in correct order: c -> b -> a
 * - This is a DAG (Directed Acyclic Graph) problem
 * - Topological sort gives us the correct evaluation order
 *
 * Example visualization:
 *
 * Equations:
 *   a = b + c
 *   c = 4
 *   b = c + 5
 *
 * Dependency Graph:
 *        c (no dependencies, indegree = 0)
 *       / \
 *      /   \
 *     v     v
 *     b     a (depends on b and c, indegree = 2)
 *     |
 *     v
 *     a
 *
 * Topological Order: c -> b -> a
 * Evaluation:
 *   1. c = 4
 *   2. b = c + 5 = 4 + 5 = 9
 *   3. a = b + c = 9 + 4 = 13
 */
class EquationSolverTopologicalSort {

    /**
     * Main solution: Topological Sort + Evaluation
     *
     * Algorithm:
     * 1. Parse equations and build dependency graph
     * 2. Build indegree map (count incoming edges)
     * 3. Initialize queue with variables that have indegree 0
     * 4. Process variables in topological order:
     *    - Evaluate expression
     *    - Reduce indegree of dependent variables
     *    - Add variables with indegree 0 to queue
     *
     * Time: O(V + E) where V = variables, E = dependencies
     * Space: O(V + E) for graph and maps
     */
    public Map<String, Integer> solveEquations(List<String> equations) {
        // Step 1: Initialize data structures
        Map<String, Integer> result = new HashMap<>();           // Final results
        Map<String, String> expressions = new HashMap<>();       // var -> expression string
        Map<String, List<String>> graph = new HashMap<>();       // dependency graph
        Map<String, Integer> indegree = new HashMap<>();         // var -> indegree count

        // Step 2: Parse equations and build dependency graph
        for (String equation : equations) {
            String[] parts = equation.split("=");
            String variable = parts[0].trim();
            String expression = parts[1].trim();

            // Store expression
            expressions.put(variable, expression);

            // Initialize graph entry
            graph.putIfAbsent(variable, new ArrayList<>());
            indegree.putIfAbsent(variable, 0);

            // Find variables this equation depends on
            List<String> dependencies = extractVariables(expression);

            for (String dep : dependencies) {
                // Add edge: dep -> variable (variable depends on dep)
                graph.putIfAbsent(dep, new ArrayList<>());
                graph.get(dep).add(variable);

                // Increase indegree of dependent variable
                indegree.put(variable, indegree.getOrDefault(variable, 0) + 1);
                indegree.putIfAbsent(dep, 0);
            }
        }

        // Step 3: Topological Sort using Kahn's Algorithm
        // Start with variables that have no dependencies (indegree = 0)
        Queue<String> queue = new LinkedList<>();
        for (String var : indegree.keySet()) {
            if (indegree.get(var) == 0) {
                queue.offer(var);
                System.out.println("Starting variable (no dependencies): " + var);
            }
        }

        // Step 4: Process variables in topological order
        int processedCount = 0;
        while (!queue.isEmpty()) {
            String var = queue.poll();
            processedCount++;

            // Evaluate the expression for this variable
            String expr = expressions.get(var);
            int value = evaluateExpression(expr, result);
            result.put(var, value);

            System.out.println("Evaluated: " + var + " = " + expr + " = " + value);

            // Reduce indegree of all variables that depend on this one
            for (String dependent : graph.get(var)) {
                int newIndegree = indegree.get(dependent) - 1;
                indegree.put(dependent, newIndegree);

                // If indegree becomes 0, all dependencies are resolved
                if (newIndegree == 0) {
                    queue.offer(dependent);
                    System.out.println("  -> " + dependent + " is now ready (all dependencies resolved)");
                }
            }
        }

        // Step 5: Check for circular dependencies
        if (processedCount != indegree.size()) {
            throw new IllegalArgumentException("Circular dependency detected!");
        }

        return result;
    }

    /**
     * Extract variable names from an expression
     * Example: "b + c" -> ["b", "c"]
     *          "4" -> []
     *          "x + 3 * y" -> ["x", "y"]
     */
    private List<String> extractVariables(String expression) {
        List<String> variables = new ArrayList<>();
        String[] tokens = expression.split("[\\s+\\-*/()]+");

        for (String token : tokens) {
            token = token.trim();
            if (!token.isEmpty() && !isNumber(token)) {
                variables.add(token);
            }
        }

        return variables;
    }

    /**
     * Check if a string is a number
     */
    private boolean isNumber(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Evaluate an expression given already computed values
     * Example: "b + c" with result = {b: 9, c: 4} -> 13
     */
    private int evaluateExpression(String expression, Map<String, Integer> values) {
        String[] tokens = expression.split("\\s+");
        List<Object> elements = new ArrayList<>();

        // Replace variables with their values
        for (String token : tokens) {
            if (token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")) {
                elements.add(token);
            } else if (isNumber(token)) {
                elements.add(Integer.parseInt(token));
            } else {
                // Variable - look up its value
                if (!values.containsKey(token)) {
                    throw new IllegalStateException("Variable " + token + " not yet evaluated");
                }
                elements.add(values.get(token));
            }
        }

        return calculate(elements);
    }

    /**
     * Calculate result from tokens
     * Handles +, -, *, / with correct precedence
     */
    private int calculate(List<Object> elements) {
        if (elements.isEmpty()) {
            return 0;
        }

        // Handle multiplication and division first (higher precedence)
        List<Object> simplified = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            if (i > 0 && elements.get(i - 1).equals("*")) {
                int prev = (int) simplified.remove(simplified.size() - 1);
                simplified.remove(simplified.size() - 1); // remove "*"
                int curr = (int) elements.get(i);
                simplified.add(prev * curr);
            } else if (i > 0 && elements.get(i - 1).equals("/")) {
                int prev = (int) simplified.remove(simplified.size() - 1);
                simplified.remove(simplified.size() - 1); // remove "/"
                int curr = (int) elements.get(i);
                simplified.add(prev / curr);
            } else {
                simplified.add(elements.get(i));
            }
        }

        // Handle addition and subtraction (lower precedence)
        int result = (int) simplified.get(0);
        for (int i = 1; i < simplified.size(); i += 2) {
            String op = (String) simplified.get(i);
            int num = (int) simplified.get(i + 1);

            if (op.equals("+")) {
                result += num;
            } else if (op.equals("-")) {
                result -= num;
            }
        }

        return result;
    }
}

/**
 * Alternative: Topological Sort with DFS
 */
class TopologicalSortDFS {
    /**
     * DFS-based topological sort
     * Uses stack to reverse post-order traversal
     */
    public Map<String, Integer> solveEquationsDFS(List<String> equations) {
        Map<String, String> expressions = new HashMap<>();
        Map<String, List<String>> graph = new HashMap<>();
        Set<String> allVars = new HashSet<>();

        // Build graph
        for (String equation : equations) {
            String[] parts = equation.split("=");
            String variable = parts[0].trim();
            String expression = parts[1].trim();

            expressions.put(variable, expression);
            allVars.add(variable);
            graph.putIfAbsent(variable, new ArrayList<>());

            List<String> dependencies = extractVariables(expression);
            for (String dep : dependencies) {
                allVars.add(dep);
                graph.putIfAbsent(dep, new ArrayList<>());
                // Reverse edge for DFS: variable -> dep
                graph.get(variable).add(dep);
            }
        }

        // DFS to get topological order
        Stack<String> stack = new Stack<>();
        Set<String> visited = new HashSet<>();
        Set<String> visiting = new HashSet<>();

        for (String var : allVars) {
            if (!visited.contains(var)) {
                dfs(var, graph, visited, visiting, stack);
            }
        }

        // Evaluate in topological order
        Map<String, Integer> result = new HashMap<>();
        while (!stack.isEmpty()) {
            String var = stack.pop();
            if (expressions.containsKey(var)) {
                int value = evaluateExpression(expressions.get(var), result);
                result.put(var, value);
            }
        }

        return result;
    }

    private void dfs(String var, Map<String, List<String>> graph,
                     Set<String> visited, Set<String> visiting, Stack<String> stack) {
        if (visiting.contains(var)) {
            throw new IllegalArgumentException("Circular dependency detected at: " + var);
        }

        if (visited.contains(var)) {
            return;
        }

        visiting.add(var);

        for (String dep : graph.get(var)) {
            dfs(dep, graph, visited, visiting, stack);
        }

        visiting.remove(var);
        visited.add(var);
        stack.push(var);
    }

    private List<String> extractVariables(String expression) {
        List<String> variables = new ArrayList<>();
        String[] tokens = expression.split("[\\s+\\-*/()]+");

        for (String token : tokens) {
            token = token.trim();
            if (!token.isEmpty() && !isNumber(token)) {
                variables.add(token);
            }
        }

        return variables;
    }

    private boolean isNumber(String s) {
        if (s == null || s.isEmpty()) return false;
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private int evaluateExpression(String expression, Map<String, Integer> values) {
        // Same implementation as above
        return 0; // Simplified for brevity
    }
}

/**
 * Test cases with detailed output
 */
class EquationSolverTopologicalSortTest {
    public static void main(String[] args) {
        testBasicExample();
        testComplexDependencies();
        testVisualization();
    }

    private static void testBasicExample() {
        System.out.println("=== Basic Example ===\n");
        EquationSolverTopologicalSort solver = new EquationSolverTopologicalSort();

        List<String> equations = Arrays.asList(
            "a = b + c",
            "c = 4",
            "b = c + 5"
        );

        System.out.println("Input equations:");
        for (String eq : equations) {
            System.out.println("  " + eq);
        }
        System.out.println();

        System.out.println("Topological sort evaluation:");
        Map<String, Integer> result = solver.solveEquations(equations);

        System.out.println("\nFinal result: " + result);
        System.out.println("Expected: {a=13, b=9, c=4}");
        System.out.println();
    }

    private static void testComplexDependencies() {
        System.out.println("=== Complex Dependencies ===\n");
        EquationSolverTopologicalSort solver = new EquationSolverTopologicalSort();

        List<String> equations = Arrays.asList(
            "result = a + b + c",
            "a = x + 1",
            "b = y + 2",
            "c = z + 3",
            "x = 10",
            "y = 20",
            "z = 30"
        );

        System.out.println("Input equations:");
        for (String eq : equations) {
            System.out.println("  " + eq);
        }
        System.out.println();

        System.out.println("Topological sort evaluation:");
        Map<String, Integer> result = solver.solveEquations(equations);

        System.out.println("\nFinal result: " + result);
        System.out.println();
    }

    private static void testVisualization() {
        System.out.println("=== Dependency Graph Visualization ===\n");

        System.out.println("Equations: a = b + c, c = 4, b = c + 5");
        System.out.println();
        System.out.println("Dependency Graph:");
        System.out.println("        c (indegree=0)");
        System.out.println("       / \\");
        System.out.println("      /   \\");
        System.out.println("     v     v");
        System.out.println("    b      |");
        System.out.println("    |      |");
        System.out.println("    v      v");
        System.out.println("     \\    /");
        System.out.println("      \\  /");
        System.out.println("       v v");
        System.out.println("        a (indegree=2)");
        System.out.println();
        System.out.println("Topological Order: c -> b -> a");
        System.out.println();
    }
}

/**
 * Key Insights
 */
class KeyInsights {
    /*
     * Why Topological Sort?
     * =====================
     * 1. Dependencies form a DAG (Directed Acyclic Graph)
     * 2. Must evaluate variables in dependency order
     * 3. Topological sort guarantees correct order
     * 4. Detects circular dependencies
     *
     * Kahn's Algorithm (BFS-based):
     * ==============================
     * - Use indegree count
     * - Start with indegree 0 (no dependencies)
     * - Process in queue order
     * - Update indegrees as we go
     * - Easy to detect cycles (processedCount != totalCount)
     *
     * DFS-based Topological Sort:
     * ===========================
     * - Use recursion + stack
     * - Post-order traversal
     * - Reverse of finish times
     * - Detects cycles via visiting set
     *
     * Time Complexity: O(V + E)
     * - V = number of variables
     * - E = number of dependencies
     * - Each variable processed once
     * - Each dependency edge traversed once
     *
     * Space Complexity: O(V + E)
     * - Graph storage: O(V + E)
     * - Maps: O(V)
     * - Queue/Stack: O(V)
     */
}
