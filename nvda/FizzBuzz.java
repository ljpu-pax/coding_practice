package nvda;

import java.util.*;

public class FizzBuzz {

    // Original FizzBuzz implementation
    public static List<String> fizzBuzz(int n) {
        List<String> res = new ArrayList<>(n);
        for (int i = 1; i <= n; i++) {
            boolean fizz = (i % 3 == 0);
            boolean buzz = (i % 5 == 0);
            if (fizz && buzz) res.add("FizzBuzz");
            else if (fizz) res.add("Fizz");
            else if (buzz) res.add("Buzz");
            else res.add(Integer.toString(i));
        }
        return res;
    }

    // FizzBuzz with map input - custom divisor rules
    public static List<String> fizzBuzzWithMap(int n, Map<Integer, String> rules) {
        List<String> res = new ArrayList<>(n);
        
        for (int i = 1; i <= n; i++) {
            StringBuilder result = new StringBuilder();
            
            // Check each rule in order (sorted by key for consistency)
            for (Map.Entry<Integer, String> entry : new TreeMap<>(rules).entrySet()) {
                if (i % entry.getKey() == 0) {
                    result.append(entry.getValue());
                }
            }
            
            // If no rules matched, add the number itself
            if (result.length() == 0) {
                result.append(i);
            }
            
            res.add(result.toString());
        }
        return res;
    }

    // Alternative: FizzBuzz with map specifying which numbers to process
    public static Map<Integer, String> fizzBuzzMapOutput(int n) {
        Map<Integer, String> result = new HashMap<>();
        
        for (int i = 1; i <= n; i++) {
            boolean fizz = (i % 3 == 0);
            boolean buzz = (i % 5 == 0);
            
            if (fizz && buzz) result.put(i, "FizzBuzz");
            else if (fizz) result.put(i, "Fizz");
            else if (buzz) result.put(i, "Buzz");
            else result.put(i, Integer.toString(i));
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println("Original FizzBuzz:");
        System.out.println(fizzBuzz(15));
        
        System.out.println("\nFizzBuzz with custom map rules:");
        Map<Integer, String> customRules = new HashMap<>();
        customRules.put(3, "Fizz");
        customRules.put(5, "Buzz");
        customRules.put(7, "Bang");
        System.out.println(fizzBuzzWithMap(21, customRules));
        
        System.out.println("\nFizzBuzz returning map:");
        System.out.println(fizzBuzzMapOutput(15));
    }
}


