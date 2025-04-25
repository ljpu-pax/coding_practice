
import java.util.*;

// Given a string s representing a valid expression, implement a basic calculator to evaluate it, and return the result of the evaluation.
// "1 + 1" -> 2
// s consists of digits, '+', '-', '(', ')', and ' '

public class BasicCalculator {

    public int calculate1(String s) {
        Stack<Integer> stack = new Stack<>();
        int num = 0;
        int result = 0;
        int sign = 1;
        
        // "1 + 1" -> 2
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            
            if (Character.isDigit(c)) {
                num = num * 10 + (c - '0');
            }
            else if (c == '+') {
                result += sign * num;
                num = 0;
                sign = 1;
            }
            else if (c == '-') {
                result += sign * num;
                num = 0;
                sign = -1;
            }
            else if (c == '(') {
                stack.push(result);
                stack.push(sign);
                result = 0;
                sign = 1;
            }
            else if (c == ')') {
                result += sign * num;
                num = 0;
                result *= stack.pop();
                result += stack.pop();
            }
        }
        result += sign * num;
        return result;
    }
    
    private int index = 0;
    
    public int calculate2(String s) {
        index = 0;
        return dfs(s);
    }
    
    private int dfs(String s) {
        int num = 0;
        int result = 0;
        int sign = 1;
        
        while (index < s.length()) {
            char c = s.charAt(index);
            
            if (Character.isDigit(c)) {
                num = num * 10 + (c - '0');
            }
            else if (c == '+') {
                result += sign * num;
                num = 0;
                sign = 1;
            }
            else if (c == '-') {
                result += sign * num;
                num = 0;
                sign = -1;
            } 
            else if (c == '(') {
                index++;
                result += sign * dfs(s);
                num = 0;
            } 
            else if (c == ')') {
                result += sign * num;
                return result;
            }
            
            index++;
        }
        result += sign * num;
        return result;
    }
    

    public static void main(String[] args) {
        BasicCalculator calculator = new BasicCalculator();
        System.out.println(calculator.calculate2("1 + 1"));
        System.out.println(calculator.calculate2("100 + 10"));
        System.out.println(calculator.calculate2("(1+(4+5+2)-3)+(6+8)"));
        //  "(1+(4+5+2)-3)+(6+8)"
    }
}
