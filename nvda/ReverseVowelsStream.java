package nvda;

import java.util.*;

public class ReverseVowelsStream {

    interface InputStream {
        Character next_char();
        void reset();
    }

    interface OutputStream {
        void write_char(char c);
        void close();
    }

    static class SimpleInputStream implements InputStream {
        private final String data;
        private int index;

        public SimpleInputStream(String s) {
            this.data = s;
            this.index = 0;
        }

        public Character next_char() {
            if (index >= data.length()) return null;
            return data.charAt(index++);
        }

        public void reset() {
            index = 0;
        }
    }

    static class SimpleOutputStream implements OutputStream {
        private final StringBuilder sb = new StringBuilder();

        public void write_char(char c) {
            sb.append(c);
        }

        public void close() {
            System.out.println("Output: " + sb.toString());
        }
    }

    // ✅ 主逻辑
    public static void reverseVowels(InputStream input_s, OutputStream output_s) {
        Set<Character> vowels = new HashSet<>(
                Arrays.asList('a', 'e', 'i', 'o', 'u',
                              'A', 'E', 'I', 'O', 'U'));
        List<Character> vowelList = new ArrayList<>();

        // First pass: collect vowels
        Character c;
        while ((c = input_s.next_char()) != null) {
            if (vowels.contains(c)) {
                vowelList.add(c);
            }
        }

        // Second pass: output with reversed vowels
        input_s.reset();
        int vowelIndex = vowelList.size() - 1;

        while ((c = input_s.next_char()) != null) {
            if (vowels.contains(c)) {
                output_s.write_char(vowelList.get(vowelIndex--));
            } else {
                output_s.write_char(c);
            }
        }

        output_s.close();
    }

    // ✅ 测试
    public static void main(String[] args) {
        String input = "hello world!";
        SimpleInputStream input_s = new SimpleInputStream(input);
        SimpleOutputStream output_s = new SimpleOutputStream();

        System.out.println("Input : " + input);
        reverseVowels(input_s, output_s);
    }
}

