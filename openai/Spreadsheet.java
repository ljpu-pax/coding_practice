package openai;

import java.util.*;

/**
 * Spreadsheet with Formula Support - Optimized Version
 *
 * Problem:
 * Implement a spreadsheet that supports:
 * - setCell(name, value): Set cell to a constant value
 * - setCell(name, formula): Set cell to a formula (e.g., "=A1+B1")
 * - getCell(name): Get cell value (must be O(1) after optimization)
 *
 * Requirements:
 * - Part 1: Basic implementation (getCell can compute on-the-fly)
 * - Part 2: Optimized - when setCell updates a cell, all dependent cells auto-update
 * - Must detect and prevent cycles
 *
 * Examples:
 * setCell("A1", "1")
 * setCell("A2", "2")
 * setCell("A3", "=A1+A2")  // A3 = 3
 * setCell("A1", "5")       // A3 automatically becomes 7
 * getCell("A3")            // Returns 7 in O(1)
 *
 * Key Insight:
 * - Track dependencies: each cell knows its sources (what it depends on)
 * - Track dependents: each cell knows its effected cells (what depends on it)
 * - When a cell updates, propagate changes to all dependents
 *
 * Time Complexity:
 * - getCell: O(1) ✅
 * - setCell with value: O(D) where D is number of dependent cells
 * - setCell with formula: O(S + D) where S is sources, D is dependents
 *
 * Space Complexity: O(N × E) where N is cells, E is average edges in dependency graph
 */
public class Spreadsheet {

    /**
     * Cell class - stores value, sources, and dependents
     */
    static class Cell {
        String name;
        int value;
        Map<Cell, Integer> sources;  // cells this depends on -> count (for formulas like A1+A1)
        Set<Cell> dependents;        // cells that depend on this

        Cell(String name, int value) {
            this.name = name;
            this.value = value;
            this.sources = new HashMap<>();
            this.dependents = new HashSet<>();
        }

        /**
         * Update value when a source cell changes
         */
        void updateValueByCell(Cell sourceCell, int oldValue, int newValue) {
            if (!sources.containsKey(sourceCell)) {
                throw new IllegalStateException("Illegal update: " + sourceCell.name + " not in sources of " + name);
            }

            int count = sources.get(sourceCell);
            value += (newValue - oldValue) * count;
        }

        /**
         * Update source dependency count
         * @return new count (0 if source removed)
         */
        int updateSource(Cell sourceCell, int deltaCount, Cell fromCell) {
            int currentCount = sources.getOrDefault(sourceCell, 0);
            int fromCellCount = sources.getOrDefault(fromCell, 0);

            int newCount = currentCount + deltaCount * fromCellCount;

            if (newCount == 0) {
                sources.remove(sourceCell);
            } else if (newCount < 0) {
                throw new IllegalStateException("Illegal source update: negative count");
            } else {
                sources.put(sourceCell, newCount);
            }

            return newCount;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Cell{name=").append(name)
              .append(", value=").append(value)
              .append(", sources=[");

            for (Map.Entry<Cell, Integer> entry : sources.entrySet()) {
                sb.append(entry.getKey().name).append("×").append(entry.getValue()).append(" ");
            }

            sb.append("], dependents=[");
            for (Cell dep : dependents) {
                sb.append(dep.name).append(" ");
            }
            sb.append("]}");

            return sb.toString();
        }
    }

    private Map<String, Cell> cells;

    public Spreadsheet() {
        this.cells = new HashMap<>();
    }

    /**
     * Get cell value in O(1)
     */
    public int getCell(String name) {
        if (!cells.containsKey(name)) {
            throw new IllegalArgumentException("Cell does not exist: " + name);
        }
        return cells.get(name).value;
    }

    /**
     * Set cell with constant value
     */
    public void setCell(String name, int value) {
        setCellInternal(name, value, new ArrayList<>());
    }

    /**
     * Set cell with formula (list of source cell names)
     * Example: setCell("A3", Arrays.asList("A1", "A2")) means A3 = A1 + A2
     */
    public void setCell(String name, List<String> sourceCellNames) {
        setCellInternal(name, 0, sourceCellNames);
    }

    /**
     * Internal setCell implementation
     */
    private int setCellInternal(String name, int value, List<String> sourceCellNames) {
        // Compute all sources (including transitive sources)
        Map<Cell, Integer> allSources = new HashMap<>();
        int newValue = value;

        for (String sourceName : sourceCellNames) {
            if (!cells.containsKey(sourceName)) {
                throw new IllegalArgumentException("Source cell does not exist: " + sourceName);
            }

            Cell sourceCell = cells.get(sourceName);

            // Add direct source
            allSources.put(sourceCell, allSources.getOrDefault(sourceCell, 0) + 1);

            // Add transitive sources
            for (Map.Entry<Cell, Integer> entry : sourceCell.sources.entrySet()) {
                Cell transitiveSource = entry.getKey();
                int count = entry.getValue();
                allSources.put(transitiveSource, allSources.getOrDefault(transitiveSource, 0) + count);
            }

            // Calculate value
            newValue += sourceCell.value;
        }

        if (!cells.containsKey(name)) {
            // Create new cell
            Cell cell = new Cell(name, newValue);
            cell.sources = allSources;

            // Register as dependent in all source cells
            for (Cell sourceCell : allSources.keySet()) {
                sourceCell.dependents.add(cell);
            }

            cells.put(name, cell);
            return cell.value;
        } else {
            // Update existing cell
            Cell cell = cells.get(name);

            // 1. Check for cycles
            if (allSources.containsKey(cell)) {
                throw new IllegalStateException("Cycle detected: " + name + " cannot depend on itself");
            }

            // 2. Calculate source differences
            Map<Cell, Integer> sourceDiffs = new HashMap<>(allSources);
            for (Map.Entry<Cell, Integer> entry : cell.sources.entrySet()) {
                Cell oldSource = entry.getKey();
                int oldCount = entry.getValue();
                sourceDiffs.put(oldSource, sourceDiffs.getOrDefault(oldSource, 0) - oldCount);
            }

            // 3. Update source cell's dependents
            for (Cell newSource : allSources.keySet()) {
                newSource.dependents.add(cell);
                newSource.dependents.addAll(cell.dependents);
            }

            // Remove from old sources
            for (Map.Entry<Cell, Integer> entry : cell.sources.entrySet()) {
                Cell oldSource = entry.getKey();
                if (!allSources.containsKey(oldSource)) {
                    oldSource.dependents.remove(cell);
                }
            }

            // 4. Update all dependent cells
            int oldValue = cell.value;
            cell.value = newValue;
            cell.sources = allSources;

            // Propagate changes to dependents
            propagateUpdate(cell, oldValue, newValue);

            return cell.value;
        }
    }

    /**
     * Propagate value change to all dependent cells
     */
    private void propagateUpdate(Cell updatedCell, int oldValue, int newValue) {
        for (Cell dependent : updatedCell.dependents) {
            dependent.updateValueByCell(updatedCell, oldValue, newValue);

            // Recursively update dependents of dependents
            if (!dependent.dependents.isEmpty()) {
                propagateUpdate(dependent, dependent.value - (newValue - oldValue) * dependent.sources.get(updatedCell),
                               dependent.value);
            }
        }
    }

    /**
     * Set cell from formula string (e.g., "=A1+A2")
     */
    public void setCellFromFormula(String name, String formula) {
        if (!formula.startsWith("=")) {
            // Constant value
            setCell(name, Integer.parseInt(formula));
            return;
        }

        // Parse formula (simple version: only handles addition)
        String expression = formula.substring(1); // Remove '='
        String[] parts = expression.split("\\+");

        List<String> sources = new ArrayList<>();
        for (String part : parts) {
            sources.add(part.trim());
        }

        setCell(name, sources);
    }

    /**
     * Print all cells
     */
    public void printCells() {
        System.out.println("=== All Cells ===");
        for (Cell cell : cells.values()) {
            System.out.println(cell);
        }
        System.out.println();
    }

    /**
     * Test cases
     */
    public static void main(String[] args) {
        Spreadsheet sheet = new Spreadsheet();

        System.out.println("=== Test 1: Basic operations ===");
        sheet.setCell("A1", 7);
        sheet.setCell("A2", 6);
        sheet.setCell("A3", Arrays.asList("A1", "A2"));
        System.out.println("A1 = " + sheet.getCell("A1")); // 7
        System.out.println("A2 = " + sheet.getCell("A2")); // 6
        System.out.println("A3 = " + sheet.getCell("A3")); // 13
        System.out.println();

        System.out.println("=== Test 2: Update propagation ===");
        sheet.setCell("A1", 2);  // A3 should automatically update
        System.out.println("A1 = " + sheet.getCell("A1")); // 2
        System.out.println("A3 = " + sheet.getCell("A3")); // 8 (2 + 6)
        System.out.println();

        System.out.println("=== Test 3: Formula string ===");
        Spreadsheet sheet2 = new Spreadsheet();
        sheet2.setCellFromFormula("A1", "1");
        sheet2.setCellFromFormula("A2", "2");
        sheet2.setCellFromFormula("A3", "=A1+A2");
        sheet2.setCellFromFormula("A4", "=A3+A2");
        sheet2.setCellFromFormula("A5", "=A3+A4");
        sheet2.setCellFromFormula("B1", "=A1+A2+A3+A4+A5");

        System.out.println("A1 = " + sheet2.getCell("A1")); // 1
        System.out.println("A2 = " + sheet2.getCell("A2")); // 2
        System.out.println("A3 = " + sheet2.getCell("A3")); // 3
        System.out.println("A4 = " + sheet2.getCell("A4")); // 5
        System.out.println("A5 = " + sheet2.getCell("A5")); // 8
        System.out.println("B1 = " + sheet2.getCell("B1")); // 19
        System.out.println();

        System.out.println("=== Test 4: Update cascading ===");
        sheet2.setCellFromFormula("A1", "5");  // All dependent cells update
        System.out.println("After A1 = 5:");
        System.out.println("A1 = " + sheet2.getCell("A1")); // 5
        System.out.println("A3 = " + sheet2.getCell("A3")); // 7
        System.out.println("A4 = " + sheet2.getCell("A4")); // 9
        System.out.println("A5 = " + sheet2.getCell("A5")); // 16
        System.out.println("B1 = " + sheet2.getCell("B1")); // 39
        System.out.println();

        System.out.println("=== Test 5: Complex dependencies ===");
        Spreadsheet sheet3 = new Spreadsheet();
        sheet3.setCell("A1", 7);
        sheet3.setCell("A2", 6);
        sheet3.setCell("A3", Arrays.asList("A1", "A2"));
        sheet3.setCell("A5", 9);
        sheet3.setCell("A4", Arrays.asList("A3", "A5"));
        sheet3.setCell("A9", Arrays.asList("A1"));
        sheet3.setCell("A10", Arrays.asList("A3", "A9"));
        sheet3.setCell("A11", Arrays.asList("A4", "A10"));

        sheet3.printCells();

        System.out.println("A10 = " + sheet3.getCell("A10")); // A3 + A9 = 13 + 7 = 20
        System.out.println("A4 = " + sheet3.getCell("A4"));   // A3 + A5 = 13 + 9 = 22
        System.out.println("A11 = " + sheet3.getCell("A11")); // A4 + A10 = 22 + 20 = 42
        System.out.println();

        System.out.println("=== Test 6: Update with complex dependencies ===");
        sheet3.setCell("A1", 8);
        System.out.println("After A1 = 8:");
        System.out.println("A3 = " + sheet3.getCell("A3"));   // 8 + 6 = 14
        System.out.println("A9 = " + sheet3.getCell("A9"));   // 8
        System.out.println("A10 = " + sheet3.getCell("A10")); // 14 + 8 = 22
        System.out.println("A4 = " + sheet3.getCell("A4"));   // 14 + 9 = 23
        System.out.println("A11 = " + sheet3.getCell("A11")); // 23 + 22 = 45
        System.out.println();

        System.out.println("=== Test 7: Cycle detection ===");
        try {
            Spreadsheet sheet4 = new Spreadsheet();
            sheet4.setCell("A1", 1);
            sheet4.setCell("A2", Arrays.asList("A1"));
            sheet4.setCell("A1", Arrays.asList("A2")); // Should throw exception
            System.out.println("ERROR: Cycle not detected!");
        } catch (IllegalStateException e) {
            System.out.println("✓ Cycle detected correctly: " + e.getMessage());
        }
    }
}

/**
 * OPTIMIZATION EXPLANATION:
 * =========================
 *
 * Part 1 (Naive):
 * ---------------
 * getCell() - compute value on-the-fly by recursively evaluating sources
 * Time: O(N) where N is transitive dependencies
 *
 * Part 2 (Optimized - THIS IMPLEMENTATION):
 * ------------------------------------------
 * getCell() - O(1) - just return stored value ✅
 * setCell() - O(D) - update all dependent cells immediately
 *
 * Key Data Structures:
 * 1. sources: Map<Cell, Integer> - what this cell depends on (with counts for A1+A1)
 * 2. dependents: Set<Cell> - what cells depend on this
 *
 * Update Algorithm:
 * 1. When cell X changes from oldValue to newValue
 * 2. For each cell Y in X.dependents:
 *    - Y.value += (newValue - oldValue) × Y.sources[X]
 * 3. Recursively update Y's dependents
 *
 * Cycle Detection:
 * - Before setting A = formula(B, C), check if A appears in B's or C's transitive sources
 *
 * INTERVIEW TIPS:
 * ===============
 * 1. Start with Part 1 (naive recursive getCell) - 10 min
 * 2. Discuss optimization approach - 5 min
 * 3. Implement Part 2 (this version) - 20 min
 * 4. Add cycle detection - 5 min
 * 5. Test - 5 min
 *
 * Total: ~45 minutes
 */
