package openai;

import java.util.*;

/**
 * Spreadsheet - SIMPLIFIED for Interview
 *
 * Problem:
 * Implement spreadsheet with:
 * - setCell(name, value): Set cell to constant
 * - setCell(name, formula): Set cell to formula like "=A1+B1"
 * - getCell(name): Get cell value
 *
 * Part 1: Naive - compute on-the-fly (easy, do this first!)
 * Part 2: Optimized - cache values, update dependents when source changes
 *
 * SIMPLIFIED APPROACH:
 * - Store formulas as strings
 * - Parse and evaluate when needed
 * - Track dependencies for updates
 */
public class SpreadsheetSimple {

    static class Cell {
        String name;
        Integer value;        // null if formula-based
        String formula;       // null if constant value
        List<String> depends; // cells this depends on
        List<String> dependents; // cells that depend on this

        Cell(String name) {
            this.name = name;
            this.depends = new ArrayList<>();
            this.dependents = new ArrayList<>();
        }
    }

    private Map<String, Cell> cells = new HashMap<>();

    /**
     * Set cell to constant value
     */
    public void setCell(String name, int value) {
        Cell cell = getOrCreate(name);
        cell.value = value;
        cell.formula = null;
        cell.depends.clear();

        // Update all dependents
        updateDependents(name);
    }

    /**
     * Set cell to formula (e.g., "=A1+B1")
     */
    public void setCell(String name, String formula) {
        if (!formula.startsWith("=")) {
            setCell(name, Integer.parseInt(formula));
            return;
        }

        Cell cell = getOrCreate(name);
        cell.formula = formula;
        cell.value = null;

        // Parse dependencies
        cell.depends = parseDependencies(formula);

        // Check for cycles
        if (hasCycle(name)) {
            throw new IllegalStateException("Cycle detected for cell " + name);
        }

        // Register as dependent in source cells
        for (String dep : cell.depends) {
            getOrCreate(dep).dependents.add(name);
        }

        // Update all dependents (including this cell)
        updateDependents(name);
    }

    /**
     * Get cell value - computes if needed
     */
    public int getCell(String name) {
        Cell cell = cells.get(name);
        if (cell == null) {
            throw new IllegalArgumentException("Cell does not exist: " + name);
        }

        // If constant value, return it
        if (cell.value != null) {
            return cell.value;
        }

        // Compute from formula
        return evaluate(cell.formula);
    }

    /**
     * Parse formula to extract cell references
     * Simple version: only handles "=A1+B1+C1" format
     */
    private List<String> parseDependencies(String formula) {
        List<String> deps = new ArrayList<>();
        String expr = formula.substring(1); // Remove '='
        String[] parts = expr.split("\\+");

        for (String part : parts) {
            part = part.trim();
            if (!part.isEmpty() && Character.isLetter(part.charAt(0))) {
                deps.add(part);
            }
        }

        return deps;
    }

    /**
     * Evaluate formula - recursively get values
     */
    private int evaluate(String formula) {
        String expr = formula.substring(1); // Remove '='
        String[] parts = expr.split("\\+");

        int sum = 0;
        for (String part : parts) {
            part = part.trim();
            if (Character.isLetter(part.charAt(0))) {
                // It's a cell reference
                sum += getCell(part);
            } else {
                // It's a number
                sum += Integer.parseInt(part);
            }
        }

        return sum;
    }

    /**
     * Check if adding this cell would create a cycle
     */
    private boolean hasCycle(String cellName) {
        Set<String> visited = new HashSet<>();
        return hasCycleDFS(cellName, visited);
    }

    private boolean hasCycleDFS(String cellName, Set<String> visited) {
        if (visited.contains(cellName)) {
            return true;
        }

        Cell cell = cells.get(cellName);
        if (cell == null || cell.depends.isEmpty()) {
            return false;
        }

        visited.add(cellName);

        for (String dep : cell.depends) {
            if (hasCycleDFS(dep, visited)) {
                return true;
            }
        }

        visited.remove(cellName);
        return false;
    }

    /**
     * Update all cells that depend on this one
     */
    private void updateDependents(String cellName) {
        Cell cell = cells.get(cellName);
        if (cell == null) return;

        // For formula cells, recompute and cache
        if (cell.formula != null) {
            cell.value = evaluate(cell.formula);
        }

        // Recursively update dependents
        for (String dependent : cell.dependents) {
            updateDependents(dependent);
        }
    }

    /**
     * Get or create cell
     */
    private Cell getOrCreate(String name) {
        return cells.computeIfAbsent(name, Cell::new);
    }

    /**
     * Print all cells (for debugging)
     */
    public void printCells() {
        System.out.println("=== Cells ===");
        for (Cell cell : cells.values()) {
            String val = cell.value != null ? cell.value.toString() : "computed";
            String formula = cell.formula != null ? cell.formula : "constant";
            System.out.printf("%s: value=%s, formula=%s, depends=%s%n",
                            cell.name, val, formula, cell.depends);
        }
        System.out.println();
    }

    /**
     * Tests
     */
    public static void main(String[] args) {
        System.out.println("=== Test 1: Basic ===");
        SpreadsheetSimple sheet = new SpreadsheetSimple();
        sheet.setCell("A1", 7);
        sheet.setCell("A2", 6);
        sheet.setCell("A3", "=A1+A2");

        System.out.println("A1 = " + sheet.getCell("A1")); // 7
        System.out.println("A2 = " + sheet.getCell("A2")); // 6
        System.out.println("A3 = " + sheet.getCell("A3")); // 13
        System.out.println();

        System.out.println("=== Test 2: Update propagation ===");
        sheet.setCell("A1", 2);  // A3 should update
        System.out.println("A1 = " + sheet.getCell("A1")); // 2
        System.out.println("A3 = " + sheet.getCell("A3")); // 8
        System.out.println();

        System.out.println("=== Test 3: Complex example ===");
        SpreadsheetSimple sheet2 = new SpreadsheetSimple();
        sheet2.setCell("A1", "1");
        sheet2.setCell("A2", "2");
        sheet2.setCell("A3", "=A1+A2");
        sheet2.setCell("A4", "=A3+A2");
        sheet2.setCell("A5", "=A3+A4");
        sheet2.setCell("B1", "=A1+A2+A3+A4+A5");

        System.out.println("A1 = " + sheet2.getCell("A1")); // 1
        System.out.println("A2 = " + sheet2.getCell("A2")); // 2
        System.out.println("A3 = " + sheet2.getCell("A3")); // 3
        System.out.println("A4 = " + sheet2.getCell("A4")); // 5
        System.out.println("A5 = " + sheet2.getCell("A5")); // 8
        System.out.println("B1 = " + sheet2.getCell("B1")); // 19
        System.out.println();

        System.out.println("=== Test 4: Cascading update ===");
        sheet2.setCell("A1", "5");
        System.out.println("After A1 = 5:");
        System.out.println("A3 = " + sheet2.getCell("A3")); // 7
        System.out.println("A4 = " + sheet2.getCell("A4")); // 9
        System.out.println("A5 = " + sheet2.getCell("A5")); // 16
        System.out.println("B1 = " + sheet2.getCell("B1")); // 39
        System.out.println();

        System.out.println("=== Test 5: Cycle detection ===");
        try {
            SpreadsheetSimple sheet3 = new SpreadsheetSimple();
            sheet3.setCell("A1", "1");
            sheet3.setCell("A2", "=A1");
            sheet3.setCell("A1", "=A2"); // Cycle!
            System.out.println("ERROR: Cycle not detected!");
        } catch (IllegalStateException e) {
            System.out.println("✓ Cycle detected: " + e.getMessage());
        }
        System.out.println();

        System.out.println("=== Test 6: Multiple references ===");
        SpreadsheetSimple sheet4 = new SpreadsheetSimple();
        sheet4.setCell("A1", 10);
        sheet4.setCell("A2", 5);
        sheet4.setCell("B1", "=A1+A2");
        sheet4.setCell("B2", "=A1+A2");
        sheet4.setCell("C1", "=B1+B2");

        System.out.println("A1 = " + sheet4.getCell("A1")); // 10
        System.out.println("A2 = " + sheet4.getCell("A2")); // 5
        System.out.println("B1 = " + sheet4.getCell("B1")); // 15
        System.out.println("B2 = " + sheet4.getCell("B2")); // 15
        System.out.println("C1 = " + sheet4.getCell("C1")); // 30

        sheet4.setCell("A1", 20);
        System.out.println("\nAfter A1 = 20:");
        System.out.println("B1 = " + sheet4.getCell("B1")); // 25
        System.out.println("B2 = " + sheet4.getCell("B2")); // 25
        System.out.println("C1 = " + sheet4.getCell("C1")); // 50
    }
}

/**
 * SIMPLIFIED INTERVIEW APPROACH:
 * ===============================
 *
 * Step 1 (10 min): Basic structure
 * ---------------------------------
 * class Cell {
 *     String name;
 *     Integer value;
 *     String formula;
 * }
 *
 * void setCell(name, value) - just store value
 * int getCell(name) - return value or evaluate formula
 *
 * Step 2 (10 min): Formula evaluation
 * ------------------------------------
 * int evaluate(String formula) {
 *     parse "=A1+B1"
 *     recursively get values
 *     sum them up
 * }
 *
 * Step 3 (10 min): Dependency tracking
 * -------------------------------------
 * List<String> depends;
 * List<String> dependents;
 *
 * When setCell, register in source's dependents list
 *
 * Step 4 (10 min): Update propagation
 * ------------------------------------
 * void updateDependents(name) {
 *     recompute this cell
 *     for each dependent:
 *         updateDependents(dependent)
 * }
 *
 * Step 5 (5 min): Cycle detection
 * --------------------------------
 * DFS to check for cycles before adding dependency
 *
 * TOTAL: ~45 minutes
 *
 * KEY SIMPLIFICATIONS:
 * ====================
 * ✅ Store formulas as strings (no complex parsing)
 * ✅ Only support addition (can extend if time)
 * ✅ Cache values in Cell.value after computation
 * ✅ Simple recursive evaluation
 * ✅ String-based dependency tracking (no Cell references)
 *
 * COMPLEXITY:
 * ===========
 * - getCell: O(D) where D is depth of dependencies
 * - setCell: O(D × N) where N is number of dependents
 *
 * After optimization (caching):
 * - getCell: O(1) if cached, O(D) if recompute needed
 *
 * This is MUCH easier to code in 45 minutes! ✅
 */
