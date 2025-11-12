package mixpanel;

public class FloatStringAdder {
    public String addFloatStrings(String num1, String num2) {
        String[] parts1 = splitNumber(num1);
        String[] parts2 = splitNumber(num2);

        // Add fractional parts
        String frac1 = padRight(parts1[1], Math.max(parts1[1].length(), parts2[1].length()));
        String frac2 = padRight(parts2[1], Math.max(parts1[1].length(), parts2[1].length()));
        StringBuilder fracResult = new StringBuilder();
        int carry = 0;

        for (int i = frac1.length() - 1; i >= 0; i--) {
            int sum = frac1.charAt(i) - '0' + frac2.charAt(i) - '0' + carry;
            fracResult.append(sum % 10);
            carry = sum / 10;
        }
        fracResult.reverse();

        // Add integer parts
        String int1 = padLeft(parts1[0], Math.max(parts1[0].length(), parts2[0].length()));
        String int2 = padLeft(parts2[0], Math.max(parts1[0].length(), parts2[0].length()));
        StringBuilder intResult = new StringBuilder();

        for (int i = int1.length() - 1; i >= 0; i--) {
            int sum = int1.charAt(i) - '0' + int2.charAt(i) - '0' + carry;
            intResult.append(sum % 10);
            carry = sum / 10;
        }
        if (carry > 0) {
            intResult.append(carry);
        }
        intResult.reverse();

        // Combine
        if (fracResult.length() > 0) {
            return intResult.toString() + "." + trimTrailingZeros(fracResult.toString());
        } else {
            return intResult.toString();
        }
    }

    private String[] splitNumber(String s) {
        String[] parts = s.split("\\.");
        if (parts.length == 1) return new String[]{parts[0], ""};
        return parts;
    }

    private String padLeft(String s, int len) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() + s.length() < len) sb.append('0');
        return sb.append(s).toString();
    }

    private String padRight(String s, int len) {
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < len) sb.append('0');
        return sb.toString();
    }

    private String trimTrailingZeros(String s) {
        int i = s.length() - 1;
        while (i >= 0 && s.charAt(i) == '0') i--;
        return i < 0 ? "0" : s.substring(0, i + 1);
    }

    // Test
    public static void main(String[] args) {
        FloatStringAdder adder = new FloatStringAdder();
        System.out.println(adder.addFloatStrings("123.45", "76.55")); // 200.0
        System.out.println(adder.addFloatStrings("0.1", "0.02"));     // 0.12
        System.out.println(adder.addFloatStrings("999", "1"));        // 1000
        System.out.println(adder.addFloatStrings("0.999", "0.001"));  // 1.0
    }
}
