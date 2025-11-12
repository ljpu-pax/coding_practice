package nvda;

import java.util.*;

public class ValidateIPAddress {

    public static String validIPAddress(String queryIP) {
        if (queryIP == null || queryIP.isEmpty()) return "Neither";
        if (queryIP.indexOf('.') != -1 && queryIP.indexOf(':') != -1) return "Neither";
        if (queryIP.indexOf('.') != -1) {
            return isValidIPv4(queryIP) ? "IPv4" : "Neither";
        } else if (queryIP.indexOf(':') != -1) {
            return isValidIPv6(queryIP) ? "IPv6" : "Neither";
        }
        return "Neither";
    }

    private static boolean isValidIPv4(String s) {
        // IPv4 must be split into 4 parts by '.' with no empty parts
        String[] parts = s.split("\\.", -1);
        if (parts.length != 4) return false;
        for (String part : parts) {
            if (part.length() == 0 || part.length() > 3) return false;
            // No leading zeros unless the number is exactly "0"
            if (part.length() > 1 && part.charAt(0) == '0') return false;
            int val = 0;
            for (int i = 0; i < part.length(); i++) {
                char c = part.charAt(i);
                if (c < '0' || c > '9') return false;
                val = val * 10 + (c - '0');
            }
            if (val < 0 || val > 255) return false;
        }
        // Ensure there are exactly 3 dots and no trailing/leading dots beyond split semantics
        return s.charAt(0) != '.' && s.charAt(s.length() - 1) != '.';
    }

    private static boolean isValidIPv6(String s) {
        // IPv6 must be split into 8 parts by ':' with no empty parts
        String[] parts = s.split(":", -1);
        if (parts.length != 8) return false;
        String hex = "0123456789abcdefABCDEF";
        for (String part : parts) {
            if (part.length() == 0 || part.length() > 4) return false;
            for (int i = 0; i < part.length(); i++) {
                char c = part.charAt(i);
                if (hex.indexOf(c) == -1) return false;
            }
        }
        // Ensure no leading/trailing colon beyond split semantics
        return s.charAt(0) != ':' && s.charAt(s.length() - 1) != ':';
    }

    public static void main(String[] args) {
        List<String> tests = Arrays.asList(
            "172.16.254.1",          // IPv4
            "172.16.254.01",         // Neither (leading zero)
            "256.256.256.256",       // Neither (out of range)
            "192.0.0.1",             // IPv4
            "2001:0db8:85a3:0000:0000:8a2e:0370:7334", // IPv6
            "2001:db8:85a3:0:0:8A2E:0370:7334",        // IPv6
            "2001:0db8:85a3::8A2E:0370:7334",          // Neither (compressed not allowed)
            "02001:0db8:85a3:0000:0000:8a2e:0370:7334"  // Neither (leading zero in group)
        );
        for (String ip : tests) {
            System.out.println(ip + " -> " + validIPAddress(ip));
        }
    }
}


