package sigma;

import java.util.*;

/**
 * Spreadsheet Version 1: Formula Support with Cycle Detection
 * 
 * 这是带公式支持和循环检测的版本，适用于以下面试问题：
 * - void setCell(String cell, String value)
 * - int getCell(String cell) 
 * - 支持公式如 "=A1+A2-5"
 * - 检测循环引用，返回-1
 * 
 * 核心特性：
 * - 记忆化优化 (memoization)
 * - 循环检测 (cycle detection)
 * - 支持加减运算
 * - 递归求值
 */
public class SpreadsheetFormulaVersion {
    
    // 存储原始输入：数字("13") 或 公式("=A1+B2-5")
    private final Map<String, String> cells = new HashMap<>();

    /**
     * 设置单元格值或公式
     * @param cell 单元格名称，如 "A1", "B2"
     * @param value 值或公式，如 "14" 或 "=A1+A2"
     */
    public void setCell(String cell, String value) {
        cells.put(cell, value);
    }

    /**
     * 获取单元格计算后的值
     * @param cell 单元格名称
     * @return 计算结果，循环引用时返回-1
     */
    public int getCell(String cell) {
        try {
            return eval(cell, new HashSet<>(), new HashMap<>());
        } catch (CircularReferenceException e) {
            return -1;
        }
    }

    /**
     * 递归求值，带记忆化和循环检测
     * @param cell 要求值的单元格
     * @param visited 访问过的单元格集合（用于循环检测）
     * @param memo 记忆化缓存
     * @return 计算结果
     */
    private int eval(String cell, Set<String> visited, Map<String, Integer> memo) {
        // 检查缓存
        if (memo.containsKey(cell)) {
            return memo.get(cell);
        }
        
        // 检查循环引用
        if (visited.contains(cell)) {
            throw new CircularReferenceException();
        }
        
        visited.add(cell);

        String raw = cells.get(cell);
        int value;
        
        if (raw == null) {
            // 未设置的单元格返回0
            value = 0;
        } else if (!raw.startsWith("=")) {
            // 纯数字
            value = Integer.parseInt(raw);
        } else {
            // 公式：去掉开头的'='
            String expr = raw.substring(1);
            value = evalExpression(expr, visited, memo);
        }

        memo.put(cell, value);
        visited.remove(cell);
        return value;
    }

    /**
     * 解析并计算表达式，如 "A1+5-B2+100"
     * @param expr 表达式字符串
     * @param visited 访问过的单元格集合
     * @param memo 记忆化缓存
     * @return 表达式计算结果
     */
    private int evalExpression(String expr, Set<String> visited, Map<String, Integer> memo) {
        int sum = 0;
        
        // 按 + 或 - 的前瞻拆分，如 "A1+5-B2+100" → ["A1", "+5", "-B2", "+100"]
        String[] tokens = expr.split("(?=[+-])");
        
        for (String tok : tokens) {
            if (tok.isEmpty()) continue;
            
            // 提取符号和主体
            char sign = tok.charAt(0);
            String term;
            
            if (sign == '+' || sign == '-') {
                term = tok.substring(1);
            } else {
                // 第一个token可能没有显式的+号
                sign = '+';
                term = tok;
            }
            
            // 计算term的值：字母开头当成cell，否则当成数字
            int val;
            if (Character.isLetter(term.charAt(0))) {
                val = eval(term, visited, memo);
            } else {
                val = Integer.parseInt(term);
            }
            
            sum += (sign == '-' ? -val : val);
        }
        
        return sum;
    }

    /**
     * 循环引用异常
     */
    private static class CircularReferenceException extends RuntimeException {}

    // ==================== 测试用例 ====================
    
    public static void main(String[] args) {
        System.out.println("=== SPREADSHEET FORMULA VERSION TESTS ===\n");
        
        testBasicOperations();
        testFormulas();
        testCycleDetection();
        testComplexFormulas();
        
        System.out.println("✅ All tests passed!");
    }
    
    public static void testBasicOperations() {
        System.out.println("TEST 1: Basic Operations");
        SpreadsheetFormulaVersion sheet = new SpreadsheetFormulaVersion();
        
        sheet.setCell("A1", "13");
        sheet.setCell("A2", "14");
        
        assert sheet.getCell("A1") == 13 : "A1 should be 13";
        assert sheet.getCell("A2") == 14 : "A2 should be 14";
        assert sheet.getCell("B1") == 0 : "Unset cell should be 0";
        
        System.out.println("✓ Basic operations work");
    }
    
    public static void testFormulas() {
        System.out.println("\nTEST 2: Formula Operations");
        SpreadsheetFormulaVersion sheet = new SpreadsheetFormulaVersion();
        
        sheet.setCell("A1", "13");
        sheet.setCell("A2", "14");
        sheet.setCell("A3", "=A1+A2");
        
        assert sheet.getCell("A3") == 27 : "A3 should be 27 (13+14)";
        
        // 测试减法
        sheet.setCell("B1", "=A2-A1");
        assert sheet.getCell("B1") == 1 : "B1 should be 1 (14-13)";
        
        // 测试混合运算
        sheet.setCell("C1", "=A1+5-A2+10");
        assert sheet.getCell("C1") == 14 : "C1 should be 14 (13+5-14+10)";
        
        System.out.println("✓ Formula operations work");
    }
    
    public static void testCycleDetection() {
        System.out.println("\nTEST 3: Cycle Detection");
        SpreadsheetFormulaVersion sheet = new SpreadsheetFormulaVersion();
        
        // 问题中的例子
        sheet.setCell("A3", "23");
        sheet.setCell("A4", "=A3+11");
        assert sheet.getCell("A4") == 34 : "A4 should be 34 (23+11)";
        
        // 创建循环：A3 -> A4 -> A3
        sheet.setCell("A3", "=A4+12");
        assert sheet.getCell("A3") == -1 : "A3 should return -1 (cycle detected)";
        
        // 自引用循环
        sheet.setCell("B1", "=B1+5");
        assert sheet.getCell("B1") == -1 : "B1 should return -1 (self-cycle)";
        
        System.out.println("✓ Cycle detection works");
    }
    
    public static void testComplexFormulas() {
        System.out.println("\nTEST 4: Complex Formulas");
        SpreadsheetFormulaVersion sheet = new SpreadsheetFormulaVersion();
        
        sheet.setCell("A1", "10");
        sheet.setCell("A2", "5");
        sheet.setCell("A3", "3");
        
        // 复杂表达式
        sheet.setCell("B1", "=A1+A2-A3+7-2");
        assert sheet.getCell("B1") == 17 : "B1 should be 17 (10+5-3+7-2)";
        
        // 嵌套引用
        sheet.setCell("B2", "=B1+A1");
        assert sheet.getCell("B2") == 27 : "B2 should be 27 (17+10)";
        
        System.out.println("✓ Complex formulas work");
    }
}
