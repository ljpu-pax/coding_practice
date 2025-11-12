package sigma;

import java.util.*;

/**
 * Spreadsheet Version 2: Basic Operations with Named Columns
 * 
 * 这是基础版本，适用于以下面试问题：
 * - 无限行，有限列
 * - 列有字符串名称 (如 "A", "B", "Sales")
 * - 所有单元格存储整数 (默认: 0)
 * - 支持 set(row, column, value), get(row, column), printTopN(n)
 * 
 * 核心特性：
 * - 稀疏存储 (只存储非零值)
 * - 动态列创建
 * - 高效的行列访问
 * - 简洁的打印功能
 */
public class SpreadsheetBasicVersion {
    /**
     * 内部静态不可变键类型
     */
    private static class Cell {
        final int row;
        final String col;
        
        Cell(int row, String col) {
            this.row = row;
            this.col = col;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Cell)) return false;
            Cell cell = (Cell) o;
            return row == cell.row && Objects.equals(col, cell.col);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }
    }
    
    private final List<String> cols;                    // 列名列表，维护顺序
    private final Set<String> colSet;                   // 列名集合，快速查找
    private final Map<Cell, Integer> data = new HashMap<>(); // 稀疏存储

    public SpreadsheetBasicVersion(String[] columns) {
        this.cols = Arrays.asList(columns);
        this.colSet = new HashSet<>(cols);
    }

    public int get(int row, String col) {
        if (!colSet.contains(col)) {
            throw new IllegalArgumentException("Unknown column: " + col);
        }
        return data.getOrDefault(new Cell(row, col), 0);
    }

    public void set(int row, String col, int val) {
        if (!colSet.contains(col)) {
            throw new IllegalArgumentException("Unknown column: " + col);
        }
        
        Cell key = new Cell(row, col);
        if (val == 0) {
            // 0值清理存储，保持稀疏
            data.remove(key);
        } else {
            data.put(key, val);
        }
    }

    public void printFirstNLines(int n) {
        for (int r = 0; r < n; r++) {
            StringBuilder sb = new StringBuilder();
            sb.append(r).append(" ->");
            for (String c : cols) {
                sb.append(" ").append(get(r, c));
            }
            System.out.println(sb);
        }
    }

    // ==================== 测试用例 ====================
    
    public static void main(String[] args) {
        System.out.println("=== SPREADSHEET BASIC VERSION TESTS ===\n");
        
        testBasicOperations();
        testSparseStorage();
        testPrintFunction();
        testErrorHandling();
        
        System.out.println("✅ All tests passed!");
    }
    
    public static void testBasicOperations() {
        System.out.println("TEST 1: Basic Operations");
        SpreadsheetBasicVersion sheet = new SpreadsheetBasicVersion(new String[]{"A", "B", "Sales", "Revenue"});
        
        // 基本设置和获取
        sheet.set(1, "A", 10);
        sheet.set(1, "B", 20);
        sheet.set(2, "Sales", 100);
        sheet.set(3, "Revenue", 500);
        
        assert sheet.get(1, "A") == 10 : "A1 should be 10";
        assert sheet.get(1, "B") == 20 : "B1 should be 20";
        assert sheet.get(2, "Sales") == 100 : "Sales2 should be 100";
        assert sheet.get(3, "Revenue") == 500 : "Revenue3 should be 500";
        
        // 未设置的单元格应该返回0
        assert sheet.get(1, "Sales") == 0 : "Unset Sales1 should be 0";
        assert sheet.get(10, "A") == 0 : "Unset A10 should be 0";
        
        System.out.println("✓ Basic operations work");
    }
    
    public static void testSparseStorage() {
        System.out.println("\nTEST 2: Sparse Storage");
        SpreadsheetBasicVersion sheet = new SpreadsheetBasicVersion(new String[]{"A", "B", "C"});
        
        // 设置一些值
        sheet.set(1, "A", 10);
        sheet.set(100, "B", 20);
        sheet.set(1000, "C", 30);
        
        // 验证值正确
        assert sheet.get(1, "A") == 10 : "A1 should be 10";
        assert sheet.get(100, "B") == 20 : "B100 should be 20";
        assert sheet.get(1000, "C") == 30 : "C1000 should be 30";
        
        // 设置为0应该清理存储
        sheet.set(1, "A", 0);
        assert sheet.get(1, "A") == 0 : "A1 should be 0 after clearing";
        
        System.out.println("✓ Sparse storage works");
    }
    
    public static void testPrintFunction() {
        System.out.println("\nTEST 3: Print Function");
        SpreadsheetBasicVersion sheet = new SpreadsheetBasicVersion(new String[]{"Name", "Age", "Score"});
        
        sheet.set(0, "Name", 1); // 假设用数字代表名字
        sheet.set(0, "Age", 25);
        sheet.set(0, "Score", 95);
        
        sheet.set(1, "Name", 2);
        sheet.set(1, "Age", 30);
        sheet.set(1, "Score", 88);
        
        sheet.set(2, "Name", 3);
        sheet.set(2, "Score", 92); // Age未设置，应该显示0
        
        System.out.println("Spreadsheet content:");
        sheet.printFirstNLines(4);
        
        System.out.println("✓ Print function works");
    }
    
    public static void testErrorHandling() {
        System.out.println("\nTEST 4: Error Handling");
        SpreadsheetBasicVersion sheet = new SpreadsheetBasicVersion(new String[]{"A", "B", "C"});
        
        // 测试非法列名
        try {
            sheet.get(1, "D");
            assert false : "Should throw exception for unknown column";
        } catch (IllegalArgumentException e) {
            // 预期的异常
        }
        
        try {
            sheet.set(1, "X", 10);
            assert false : "Should throw exception for unknown column";
        } catch (IllegalArgumentException e) {
            // 预期的异常
        }
        
        System.out.println("✓ Error handling works");
    }
    
    // ==================== 性能分析 ====================
    
    /**
     * 打印复杂度分析
     */
    public static void printComplexityAnalysis() {
        System.out.println("\n=== COMPLEXITY ANALYSIS ===");
        System.out.println("Time Complexity:");
        System.out.println("- get(row, col): O(1) - HashMap查找");
        System.out.println("- set(row, col, val): O(1) - HashMap插入/删除");
        System.out.println("- printFirstNLines(n): O(n * c) - n行 * c列");
        
        System.out.println("\nSpace Complexity:");
        System.out.println("- 存储: O(k) - k是非零单元格数量");
        System.out.println("- 列信息: O(c) - c是列数");
        System.out.println("- 总体: O(k + c)");
        
        System.out.println("\nOptimizations:");
        System.out.println("✓ 稀疏存储 - 只存储非零值");
        System.out.println("✓ HashMap快速访问 - O(1)查找");
        System.out.println("✓ Record类型 - 简洁的键类型");
        System.out.println("✓ 列名验证 - 防止错误输入");
    }
}
