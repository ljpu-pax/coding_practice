package upstart;

/**
 * Problem 2: Punctuation replacement
 * Replace every period with an exclamation mark.
 * If there's one or more consecutive exclamation marks, add one more exclamation mark.
 */
public class PunctuationReplacer {

    public String replacePunctuation(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < str.length()) {
            char c = str.charAt(i);

            if (c == '.') {
                // Replace period with exclamation mark
                result.append('!');
                i++;
            } else if (c == '!') {
                // Count consecutive exclamation marks
                int count = 0;
                while (i < str.length() && str.charAt(i) == '!') {
                    result.append('!');
                    count++;
                    i++;
                }
                // Add one more exclamation mark
                result.append('!');
            } else {
                result.append(c);
                i++;
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {
        PunctuationReplacer solution = new PunctuationReplacer();

        // Test case 1: period replacement
        String test1 = "Hello world. How are you.";
        System.out.println("Input: \"" + test1 + "\"");
        System.out.println("Output: \"" + solution.replacePunctuation(test1) + "\"");
        // Expected: "Hello world! How are you!"

        // Test case 2: exclamation mark addition
        String test2 = "Wow! Amazing!";
        System.out.println("\nInput: \"" + test2 + "\"");
        System.out.println("Output: \"" + solution.replacePunctuation(test2) + "\"");
        // Expected: "Wow!! Amazing!!"

        // Test case 3: multiple consecutive exclamation marks
        String test3 = "Yes!!! Really!!!";
        System.out.println("\nInput: \"" + test3 + "\"");
        System.out.println("Output: \"" + solution.replacePunctuation(test3) + "\"");
        // Expected: "Yes!!!! Really!!!!"

        // Test case 4: mixed
        String test4 = "Hello. World! Nice!!";
        System.out.println("\nInput: \"" + test4 + "\"");
        System.out.println("Output: \"" + solution.replacePunctuation(test4) + "\"");
        // Expected: "Hello! World!! Nice!!!"
    }
}
