public class FloatStringAdder {
    public String addFloatStrings(String num1, String num2) {
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
        String intRes = addIntegerStrings(int1, int2, carry);

        // Trim trailing zeros in fractional part (optional)
        String fractional = fracRes.toString().replaceFirst("0+$", "");
        return fractional.isEmpty() ? intRes : intRes + "." + fractional;
    }

    private String addIntegerStrings(String a, String b, int carry) {
        StringBuilder sb = new StringBuilder();
        int i = a.length() - 1, j = b.length() - 1;

        while (i >= 0 || j >= 0 || carry > 0) {
            int d1 = i >= 0 ? a.charAt(i--) - '0' : 0;
            int d2 = j >= 0 ? b.charAt(j--) - '0' : 0;
            int sum = d1 + d2 + carry;
            sb.append(sum % 10);
            carry = sum / 10;
        }

        return sb.reverse().toString();
    }

    private String padRight(String s, int len) {
        while (s.length() < len) s += "0";
        return s;
    }

    public static void main(String[] args) {
        FloatStringAdder adder = new FloatStringAdder();
        System.out.println(adder.addFloatStrings("123.45", "77.5501"));  // "200.0001"
        System.out.println(adder.addFloatStrings("1.2", "3.45"));        // "4.65"
        System.out.println(adder.addFloatStrings("0.9", "0.2"));         // "1.1"
    }
}

