import java.util.*;

/**
 * Applied Intuition Interview Question
 *
 * Problem: Given a list of equations as strings, solve for all variables
 *
 * Input: ["a = b + c", "c = 4", "b = c + 5"]
 * Output: {"a": 13, "b": 9, "c": 4}
 *
 * Approach:
 * 1. Parse each equation into left side (variable) and right side (expression)
 * 2. Use topological sort to determine order of evaluation
 * 3. Evaluate equations in dependency order using memoization
 */
class EquationSolver {

    /**
     * Main solution using topological sort and evaluation
     */
    public Map<String, Integer> solveEquations(List<String> equations) {
        Map<String, Integer> result = new HashMap<>();
        Map<String, String> expressions = new HashMap<>(); // variable -> expression
        Map<String, List<String>> graph = new HashMap<>(); // dependency graph
        Map<String, Integer> indegree = new HashMap<>();

        // Parse equations and build dependency graph
        for (String equation : equations) {
            String[] parts = equation.split("=");
            String variable = parts[0].trim();
            String expression = parts[1].trim();

            expressions.put(variable, expression);
            graph.putIfAbsent(variable, new ArrayList<>());
            indegree.putIfAbsent(variable, 0);

            // Find dependencies (variables on the right side)
            List<String> dependencies = extractVariables(expression);
            for (String dep : dependencies) {
                graph.putIfAbsent(dep, new ArrayList<>());
                graph.get(dep).add(variable);
                indegree.put(variable, indegree.getOrDefault(variable, 0) + 1);
                indegree.putIfAbsent(dep, 0);
            }
        }

        // Topological sort using BFS (Kahn's algorithm)
        Queue<String> queue = new LinkedList<>();
        for (String var : indegree.keySet()) {
            if (indegree.get(var) == 0) {
                queue.offer(var);
            }
        }

        // Process in topological order
        while (!queue.isEmpty()) {
            String var = queue.poll();

            // Evaluate the expression for this variable
            int value = evaluateExpression(expressions.get(var), result);
            result.put(var, value);

            // Reduce indegree of dependent variables
            for (String dependent : graph.get(var)) {
                indegree.put(dependent, indegree.get(dependent) - 1);
                if (indegree.get(dependent) == 0) {
                    queue.offer(dependent);
                }
            }
        }

        return result;
    }

    /**
     * Extract all variable names from an expression
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
     */
    private int evaluateExpression(String expression, Map<String, Integer> values) {
        // Replace variables with their values
        String[] tokens = expression.split("\\s+");
        List<Object> elements = new ArrayList<>();

        for (String token : tokens) {
            if (token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")) {
                elements.add(token);
            } else if (isNumber(token)) {
                elements.add(Integer.parseInt(token));
            } else {
                elements.add(values.get(token));
            }
        }

        // Evaluate the expression (simple left-to-right for +/-)
        return calculate(elements);
    }

    /**
     * Calculate result from tokens (handles +, -, *, /)
     */
    private int calculate(List<Object> elements) {
        if (elements.isEmpty()) {
            return 0;
        }

        // Handle multiplication and division first
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

        // Handle addition and subtraction
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

    /**
     * Alternative solution using DFS with memoization
     */
    public Map<String, Integer> solveEquationsDFS(List<String> equations) {
        Map<String, Integer> memo = new HashMap<>();
        Map<String, String> expressions = new HashMap<>();

        // Parse equations
        for (String equation : equations) {
            String[] parts = equation.split("=");
            String variable = parts[0].trim();
            String expression = parts[1].trim();
            expressions.put(variable, expression);
        }

        // Solve each variable using DFS
        for (String var : expressions.keySet()) {
            if (!memo.containsKey(var)) {
                solve(var, expressions, memo, new HashSet<>());
            }
        }

        return memo;
    }

    /**
     * DFS helper to solve a variable
     */
    private int solve(String var, Map<String, String> expressions,
                      Map<String, Integer> memo, Set<String> visiting) {
        // Already computed
        if (memo.containsKey(var)) {
            return memo.get(var);
        }

        // Cycle detection
        if (visiting.contains(var)) {
            throw new IllegalArgumentException("Circular dependency detected");
        }

        visiting.add(var);

        String expression = expressions.get(var);
        String[] tokens = expression.split("\\s+");
        List<Object> elements = new ArrayList<>();

        for (String token : tokens) {
            if (token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")) {
                elements.add(token);
            } else if (isNumber(token)) {
                elements.add(Integer.parseInt(token));
            } else {
                // Recursively solve dependency
                elements.add(solve(token, expressions, memo, visiting));
            }
        }

        int result = calculate(elements);
        memo.put(var, result);
        visiting.remove(var);

        return result;
    }
}

/**
 * Test cases
 */
class EquationSolverTest {
    public static void main(String[] args) {
        testBasicCases();
        testComplexCases();
        testDFSApproach();
    }

    private static void testBasicCases() {
        System.out.println("=== Testing Basic Cases ===\n");
        EquationSolver solver = new EquationSolver();

        // Test 1: Basic case from problem
        List<String> equations1 = Arrays.asList(
            "a = b + c",
            "c = 4",
            "b = c + 5"
        );
        Map<String, Integer> result1 = solver.solveEquations(equations1);
        System.out.println("Test 1: Basic equations");
        System.out.println("  Input: " + equations1);
        System.out.println("  Output: " + result1);
        System.out.println("  Expected: {a=13, b=9, c=4}");
        System.out.println("  Pass: " + (result1.get("a") == 13 &&
                                        result1.get("b") == 9 &&
                                        result1.get("c") == 4));
        System.out.println();

        // Test 2: Single variable
        List<String> equations2 = Arrays.asList("x = 10");
        Map<String, Integer> result2 = solver.solveEquations(equations2);
        System.out.println("Test 2: Single variable");
        System.out.println("  Output: " + result2);
        System.out.println("  Expected: {x=10}");
        System.out.println();

        // Test 3: Multiple dependencies
        List<String> equations3 = Arrays.asList(
            "x = 5",
            "y = x + 3",
            "z = y + x"
        );
        Map<String, Integer> result3 = solver.solveEquations(equations3);
        System.out.println("Test 3: Chain dependencies");
        System.out.println("  Output: " + result3);
        System.out.println("  Expected: {x=5, y=8, z=13}");
        System.out.println();
    }

    private static void testComplexCases() {
        System.out.println("=== Testing Complex Cases ===\n");
        EquationSolver solver = new EquationSolver();

        // Test 1: Multiple operations
        List<String> equations1 = Arrays.asList(
            "a = 2 + 3",
            "b = a + 5",
            "c = b - a"
        );
        Map<String, Integer> result1 = solver.solveEquations(equations1);
        System.out.println("Test 1: Multiple operations");
        System.out.println("  Output: " + result1);
        System.out.println("  Expected: {a=5, b=10, c=5}");
        System.out.println();

        // Test 2: Complex dependency tree
        List<String> equations2 = Arrays.asList(
            "d = a + b",
            "a = 1",
            "b = 2",
            "c = 3",
            "e = d + c"
        );
        Map<String, Integer> result2 = solver.solveEquations(equations2);
        System.out.println("Test 2: Complex dependency tree");
        System.out.println("  Output: " + result2);
        System.out.println("  Expected: {a=1, b=2, c=3, d=3, e=6}");
        System.out.println();
    }

    private static void testDFSApproach() {
        System.out.println("=== Testing DFS Approach ===\n");
        EquationSolver solver = new EquationSolver();

        List<String> equations = Arrays.asList(
            "a = b + c",
            "c = 4",
            "b = c + 5"
        );

        Map<String, Integer> result = solver.solveEquationsDFS(equations);
        System.out.println("Test 1: DFS with memoization");
        System.out.println("  Output: " + result);
        System.out.println("  Expected: {a=13, b=9, c=4}");
        System.out.println("  Pass: " + (result.get("a") == 13 &&
                                        result.get("b") == 9 &&
                                        result.get("c") == 4));
        System.out.println();
    }
}
