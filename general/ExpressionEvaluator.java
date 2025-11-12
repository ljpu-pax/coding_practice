import java.util.Stack;

public class ExpressionEvaluator {
    public static int evaluate(String s) {
        Stack<Integer> stack = new Stack<>();
        long result = 0; // Use long to handle intermediate calculations
        long number = 0; // Use long to accumulate digits for large numbers
        int sign = 1; // 1 for positive, -1 for negative
        
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (Character.isDigit(c)) {
                // Accumulate digit into the current number
                number = number * 10 + (c - '0');
            } else if (c == '+') {
                // Add the previous number to result
                result += sign * number;
                number = 0; // Reset the number
                sign = 1; // Update the sign
            } else if (c == '-') {
                // Add the previous number to result
                result += sign * number;
                number = 0; // Reset the number
                sign = -1; // Update the sign
            } else if (c == '(') {
                // Push current result and sign onto the stack
                stack.push((int) result);
                stack.push(sign);
                // Reset result and sign for the sub-expression
                result = 0;
                sign = 1;
            } else if (c == ')') {
                // Complete the current sub-expression
                result += sign * number;
                number = 0; // Reset the number
                result *= stack.pop(); // Apply the previous sign
                result += stack.pop(); // Add the previous result
            }
        }

        // Add the last number to the result
        result += sign * number;

        // Ensure the result is within bounds of an integer
        if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE) {
            throw new ArithmeticException("Result out of bounds for integer type");
        }

        return (int) result;
    }

    public static void main(String[] args) {
        // Test case: Single large number
        String expression = "2147483647";
        System.out.println("Result: " + evaluate(expression)); // Should print 2147483647
    }
}
