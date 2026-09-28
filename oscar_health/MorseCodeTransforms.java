import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LeetCode 804 - Unique Morse Code Words (encoding step) plus a follow-up decoder that
 * generates all possible original strings for a given Morse sequence using the same alphabet mapping.
 */
public class MorseCodeTransforms {
    private static final String[] MORSE = {
        ".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..",
        ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...",
        "-", "..-", "...-", ".--", "-..-", "-.--", "--.."
    };

    /** Encodes a lowercase alphabetic word into its Morse representation. */
    public String encodeWord(String word) {
        if (word == null || word.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (char ch : word.toCharArray()) {
            if (ch < 'a' || ch > 'z') {
                throw new IllegalArgumentException("Only lowercase a-z characters are supported");
            }
            sb.append(MORSE[ch - 'a']);
        }
        return sb.toString();
    }

    /**
     * Decodes a Morse string by enumerating every possible original string that could map to it.
     * Uses memoized DFS: at each position try appending any letter whose Morse code matches the next prefix.
     */
    public List<String> decodeAll(String morse) {
        if (morse == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        Map<Integer, List<String>> memo = new HashMap<>();
        return decodeFromIndex(morse, 0, memo);
    }

    private List<String> decodeFromIndex(String morse, int index, Map<Integer, List<String>> memo) {
        if (index == morse.length()) {
            List<String> base = new ArrayList<>();
            base.add("");
            return base;
        }

        if (memo.containsKey(index)) {
            return memo.get(index);
        }

        List<String> result = new ArrayList<>();
        for (int letter = 0; letter < MORSE.length; letter++) {
            String code = MORSE[letter];
            if (matches(morse, index, code)) {
                List<String> suffixes = decodeFromIndex(morse, index + code.length(), memo);
                char ch = (char) ('a' + letter);
                for (String suffix : suffixes) {
                    result.add(ch + suffix);
                }
            }
        }

        memo.put(index, result);
        return result;
    }

    private boolean matches(String morse, int index, String code) {
        int remaining = morse.length() - index;
        if (remaining < code.length()) {
            return false;
        }
        for (int i = 0; i < code.length(); i++) {
            if (morse.charAt(index + i) != code.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        MorseCodeTransforms transforms = new MorseCodeTransforms();
        System.out.println("Encoding examples:");
        System.out.println("gin -> " + transforms.encodeWord("gin"));
        System.out.println("zen -> " + transforms.encodeWord("zen"));

        String morse = "--...-.-"; // could be "mvk", "mwt" ... etc
        List<String> candidates = transforms.decodeAll(morse);
        System.out.println("Decoding " + morse + " -> " + candidates);
    }
}
