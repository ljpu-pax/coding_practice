package upstart;

import java.util.*;

/**
 * Problem 3: Decipher using two hashmaps
 * Use two hashmaps to decipher an encoded message.
 * Get from map1 then use result to get from map2.
 */
public class Decipher {

    public String decipher(String encoded, Map<String, String> map1, Map<String, String> map2) {
        if (encoded == null || map1 == null || map2 == null) {
            return encoded;
        }

        // First lookup in map1
        String intermediate = map1.get(encoded);
        if (intermediate == null) {
            return encoded; // If not found, return original
        }

        // Second lookup in map2
        String decoded = map2.get(intermediate);
        if (decoded == null) {
            return intermediate; // If not found, return intermediate result
        }

        return decoded;
    }

    public List<String> decipherList(List<String> encodedList, Map<String, String> map1, Map<String, String> map2) {
        List<String> result = new ArrayList<>();
        for (String encoded : encodedList) {
            result.add(decipher(encoded, map1, map2));
        }
        return result;
    }

    public static void main(String[] args) {
        Decipher solution = new Decipher();

        // Setup hashmaps
        Map<String, String> map1 = new HashMap<>();
        map1.put("abc", "xyz");
        map1.put("def", "uvw");
        map1.put("ghi", "rst");

        Map<String, String> map2 = new HashMap<>();
        map2.put("xyz", "hello");
        map2.put("uvw", "world");
        map2.put("rst", "java");

        // Test case 1: single string
        String encoded = "abc";
        System.out.println("Input: \"" + encoded + "\"");
        System.out.println("Output: \"" + solution.decipher(encoded, map1, map2) + "\"");
        // Expected: "hello" (abc -> xyz -> hello)

        // Test case 2: another string
        encoded = "def";
        System.out.println("\nInput: \"" + encoded + "\"");
        System.out.println("Output: \"" + solution.decipher(encoded, map1, map2) + "\"");
        // Expected: "world" (def -> uvw -> world)

        // Test case 3: list of strings
        List<String> encodedList = Arrays.asList("abc", "def", "ghi");
        System.out.println("\nInput: " + encodedList);
        System.out.println("Output: " + solution.decipherList(encodedList, map1, map2));
        // Expected: [hello, world, java]
    }
}
