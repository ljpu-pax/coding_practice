package mixpanel;


public class FloatAdder {

    public static String addFloatStrings(String num1, String num2) {
        String[] parts1 = num1.split("\\.");
        String[] parts2 = num2.split("\\.");

        String int1 = parts1[0];
        String frac1 = parts1.length > 1 ? parts1[1] : "0";

        String int2 = parts2[0];
        String frac2 = parts2.length > 1 ? parts2[1] : "0";

        // Pad fractional parts to equal length
        int maxFracLen = Math.max(frac1.length(), frac2.length());
        frac1 = padRight(frac1, maxFracLen);
        frac2 = padRight(frac2, maxFracLen);

        // Add fractional parts
        StringBuilder fracRes = new StringBuilder();
        int carry = 0;
        for (int i = maxFracLen - 1; i >= 0; i--) {
            int sum = (frac1.charAt(i) - '0') + (frac2.charAt(i) - '0') + carry;
            fracRes.append(sum % 10);
            carry = sum / 10;
        }
        fracRes.reverse();

        // Add integer parts
        StringBuilder intRes = new StringBuilder();
        int1 = reverseAndPad(int1, int2.length());
        int2 = reverseAndPad(int2, int1.length());

        for (int i = 0; i < int1.length(); i++) {
            int sum = (int1.charAt(i) - '0') + (int2.charAt(i) - '0') + carry;
            intRes.append(sum % 10);
            carry = sum / 10;
        }
        if (carry > 0) intRes.append(carry);

        intRes.reverse();

        // Trim trailing zeros in fractional part
        String fracFinal = fracRes.toString().replaceFirst("0+$", "");
        return fracFinal.isEmpty() ? intRes.toString() : intRes + "." + fracFinal;
    }

    private static String padRight(String s, int len) {
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < len) sb.append('0');
        return sb.toString();
    }

    private static String reverseAndPad(String s, int len) {
        StringBuilder sb = new StringBuilder(s).reverse();
        while (sb.length() < len) sb.append('0');
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(addFloatStrings("123.45", "76.55"));   // 200.00
        System.out.println(addFloatStrings("0.1", "0.02"));       // 0.12
        System.out.println(addFloatStrings("999", "1"));          // 1000
        System.out.println(addFloatStrings("0.999", "0.001"));    // 1
        System.out.println(addFloatStrings("999.9", "0.1"));      // 1000
    }
}
