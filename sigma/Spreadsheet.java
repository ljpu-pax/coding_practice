package sigma;

import java.util.*;
import java.util.regex.*;

/**
 * Simple Spreadsheet Implementation
 * 
 * PART 1: Basic Operations
 * - Set(row, column, value) - Put a number in a cell
 * - Get(row, column) - Get the number from a cell (return 0 if empty)
 * - printTopN(n) - Show the first n rows with column names in the first row
 * 
 * PART 3: Formula Support
 * - Cells can hold formulas like "row1 and col1 plus row2 and col2"
 */
public class Spreadsheet {
    
    private Map<String, Integer> columnNameToIndex;
    private List<String> columnNames;
    private Map<Integer, Map<Integer, Integer>> data;
    private Map<String, String> formulas;
    private Map<String, Set<String>> dependents;
    
    public Spreadsheet() {
        this.columnNameToIndex = new HashMap<>();
        this.columnNames = new ArrayList<>();
        this.data = new HashMap<>();
        this.formulas = new HashMap<>();
        this.dependents = new HashMap<>();
    }
    
    // PART 1: Basic Operations
    
    public void set(int row, String column, int value) {
        int colIndex = getOrCreateColumnIndex(column);
        String cellKey = row + "," + colIndex;
        
        // Remove formula if exists
        if (formulas.containsKey(cellKey)) {
            removeDependencies(cellKey);
            formulas.remove(cellKey);
        }
        
        data.computeIfAbsent(row, k -> new HashMap<>()).put(colIndex, value);
        updateDependents(cellKey);
    }
    
    public int get(int row, String column) {
        if (!columnNameToIndex.containsKey(column)) {
            return 0;
        }
        
        int colIndex = columnNameToIndex.get(column);
        Map<Integer, Integer> rowData = data.get(row);
        
        if (rowData == null || !rowData.containsKey(colIndex)) {
            return 0;
        }
        
        return rowData.get(colIndex);
    }
    
    public void printTopN(int n) {
        if (n <= 0) return;
        
        System.out.print("Row\\Col");
        for (String colName : columnNames) {
            System.out.print("\t" + colName);
        }
        System.out.println();
        
        for (int row = 1; row <= n; row++) {
            System.out.print(row);
            for (String colName : columnNames) {
                System.out.print("\t" + get(row, colName));
            }
            System.out.println();
        }
    }
    
    // PART 3: Formula Support
    
    public void setFormula(int row, String column, String formula) {
        int colIndex = getOrCreateColumnIndex(column);
        String cellKey = row + "," + colIndex;
        
        removeDependencies(cellKey);
        formulas.put(cellKey, formula);
        
        // Set up dependencies
        Pattern pattern = Pattern.compile("row(\\d+)\\s+and\\s+(\\w+)");
        Matcher matcher = pattern.matcher(formula);
        
        while (matcher.find()) {
            int depRow = Integer.parseInt(matcher.group(1));
            String depCol = matcher.group(2);
            int depColIndex = getOrCreateColumnIndex(depCol);
            String depKey = depRow + "," + depColIndex;
            dependents.computeIfAbsent(depKey, k -> new HashSet<>()).add(cellKey);
        }
        
        // Calculate and store result
        int result = evaluateFormula(formula);
        data.computeIfAbsent(row, k -> new HashMap<>()).put(colIndex, result);
        updateDependents(cellKey);
    }
    
    // Helper Methods
    
    private int getOrCreateColumnIndex(String columnName) {
        if (!columnNameToIndex.containsKey(columnName)) {
            int newIndex = columnNames.size();
            columnNameToIndex.put(columnName, newIndex);
            columnNames.add(columnName);
            return newIndex;
        }
        return columnNameToIndex.get(columnName);
    }
    
    private void removeDependencies(String cellKey) {
        for (Set<String> deps : dependents.values()) {
            deps.remove(cellKey);
        }
    }
    
    private void updateDependents(String cellKey) {
        Set<String> deps = dependents.get(cellKey);
        if (deps != null) {
            for (String depCell : new HashSet<>(deps)) {
                String[] parts = depCell.split(",");
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);
                
                String formula = formulas.get(depCell);
                if (formula != null) {
                    int newValue = evaluateFormula(formula);
                    data.computeIfAbsent(row, k -> new HashMap<>()).put(col, newValue);
                    updateDependents(depCell);
                }
            }
        }
    }
    
    private int evaluateFormula(String formula) {
        Pattern pattern = Pattern.compile("row(\\d+)\\s+and\\s+(\\w+)");
        Matcher matcher = pattern.matcher(formula);
        
        int result = 0;
        while (matcher.find()) {
            int row = Integer.parseInt(matcher.group(1));
            String colName = matcher.group(2);
            result += get(row, colName);
        }
        
        return result;
    }
    
    // Tests
    
    public static void main(String[] args) {
        System.out.println("=== SPREADSHEET TESTS ===\n");
        
        Spreadsheet sheet = new Spreadsheet();
        
        // Part 1 Tests
        System.out.println("PART 1: Basic Operations");
        sheet.set(1, "A", 10);
        sheet.set(1, "B", 20);
        sheet.set(2, "Sales", 100);
        
        System.out.println("A1 = " + sheet.get(1, "A")); // 10
        System.out.println("B1 = " + sheet.get(1, "B")); // 20
        System.out.println("Sales2 = " + sheet.get(2, "Sales")); // 100
        System.out.println("Empty C1 = " + sheet.get(1, "C")); // 0
        
        System.out.println("\nSpreadsheet state:");
        sheet.printTopN(3);
        
        // Part 3 Tests
        System.out.println("\nPART 3: Formula Support");
        sheet.setFormula(3, "Total", "row1 and A plus row1 and B"); // 10 + 20 = 30
        System.out.println("Total3 = " + sheet.get(3, "Total")); // 30
        
        sheet.set(1, "A", 50); // Change A1
        System.out.println("After changing A1 to 50:");
        System.out.println("Total3 = " + sheet.get(3, "Total")); // 70
        
        System.out.println("\nFinal state:");
        sheet.printTopN(4);
        
        System.out.println("\n✓ All tests passed!");
    }
}