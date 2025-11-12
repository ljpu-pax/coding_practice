package sigma;

import java.util.*;

/**
 * LeetCode 631: Design Excel Sum Formula - SIMPLE VERSION
 * 
 * Just the core functionality without overengineering
 */
public class Excel {
    
    private int[][] cells;
    private Map<String, String[]> formulas; // Store formulas for cells that have them
    private Map<String, Set<String>> dependents; // Track which cells depend on each cell
    
    public Excel(int H, char W) {
        cells = new int[H + 1][W - 'A' + 1]; // 1-indexed rows, 0-indexed cols
        formulas = new HashMap<>();
        dependents = new HashMap<>();
    }
    
    public void set(int r, char c, int v) {
        String key = r + "," + c;
        
        // Remove formula if exists
        if (formulas.containsKey(key)) {
            removeDependencies(key);
            formulas.remove(key);
        }
        
        cells[r][c - 'A'] = v;
        updateDependents(key);
    }
    
    public int get(int r, char c) {
        return cells[r][c - 'A'];
    }
    
    public int sum(int r, char c, String[] strs) {
        String key = r + "," + c;
        
        // Remove old dependencies
        if (formulas.containsKey(key)) {
            removeDependencies(key);
        }
        
        // Store new formula
        formulas.put(key, strs);
        
        // Set up new dependencies
        setupDependencies(key, strs);
        
        // Calculate and store result
        int sum = calculateSum(strs);
        cells[r][c - 'A'] = sum;
        
        return sum;
    }
    
    private void setupDependencies(String formulaCell, String[] strs) {
        for (String str : strs) {
            if (str.contains(":")) {
                String[] parts = str.split(":");
                int[] start = parseCell(parts[0]);
                int[] end = parseCell(parts[1]);
                
                for (int row = start[0]; row <= end[0]; row++) {
                    for (int col = start[1]; col <= end[1]; col++) {
                        String depKey = row + "," + (char)('A' + col);
                        dependents.computeIfAbsent(depKey, k -> new HashSet<>()).add(formulaCell);
                    }
                }
            } else {
                int[] cell = parseCell(str);
                String depKey = cell[0] + "," + (char)('A' + cell[1]);
                dependents.computeIfAbsent(depKey, k -> new HashSet<>()).add(formulaCell);
            }
        }
    }
    
    private void removeDependencies(String formulaCell) {
        for (Set<String> deps : dependents.values()) {
            deps.remove(formulaCell);
        }
    }
    
    private void updateDependents(String changedCell) {
        Set<String> deps = dependents.get(changedCell);
        if (deps != null) {
            for (String depCell : new HashSet<>(deps)) {
                String[] parts = depCell.split(",");
                int r = Integer.parseInt(parts[0]);
                char c = parts[1].charAt(0);
                
                String[] formula = formulas.get(depCell);
                if (formula != null) {
                    int newSum = calculateSum(formula);
                    cells[r][c - 'A'] = newSum;
                    updateDependents(depCell); // Recursive update
                }
            }
        }
    }
    
    private int calculateSum(String[] strs) {
        int sum = 0;
        for (String str : strs) {
            if (str.contains(":")) {
                String[] parts = str.split(":");
                int[] start = parseCell(parts[0]);
                int[] end = parseCell(parts[1]);
                
                for (int row = start[0]; row <= end[0]; row++) {
                    for (int col = start[1]; col <= end[1]; col++) {
                        sum += cells[row][col];
                    }
                }
            } else {
                int[] cell = parseCell(str);
                sum += cells[cell[0]][cell[1]];
            }
        }
        return sum;
    }
    
    private int[] parseCell(String cell) {
        char col = cell.charAt(0);
        int row = Integer.parseInt(cell.substring(1));
        return new int[]{row, col - 'A'};
    }
    
    // Test with the failing case
    public static void main(String[] args) {
        System.out.println("=== Testing LeetCode case ===");
        Excel excel = new Excel(16, 'D');
        
        // Test the failing pattern
        excel.sum(2, 'A', new String[]{"A1:D1"}); // All zeros initially
        System.out.println("A2 = " + excel.get(2, 'A')); // Should be 0
        
        excel.sum(2, 'B', new String[]{"A1:D1"});
        System.out.println("B2 = " + excel.get(2, 'B')); // Should be 0
        
        excel.sum(3, 'A', new String[]{"A2:D2"}); // References previous row
        System.out.println("A3 = " + excel.get(3, 'A')); // Should be 0
        
        // Test basic functionality
        System.out.println("\n=== Basic tests ===");
        Excel excel2 = new Excel(3, 'C');
        
        excel2.set(1, 'A', 3);
        System.out.println("A1 = " + excel2.get(1, 'A')); // 3
        
        excel2.sum(1, 'B', new String[]{"A1"});
        System.out.println("B1 = " + excel2.get(1, 'B')); // 3
        
        excel2.set(2, 'B', 4);
        excel2.sum(1, 'C', new String[]{"A1", "B2"});
        System.out.println("C1 = " + excel2.get(1, 'C')); // 7
        
        excel2.sum(2, 'C', new String[]{"A1:B2"});
        System.out.println("C2 = " + excel2.get(2, 'C')); // 3+3+0+4 = 10
    }
}
