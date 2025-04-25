import java.util.*;

public class OrderValidator {
    public static boolean isValidOrderList(String[] orders) {
        Set<String> activePickups = new HashSet<>();
        
        for (String order : orders) {
            if (order.startsWith("P")) {
                if (activePickups.contains(order)) {
                    return false;
                }

                activePickups.add(order);
            } else if (order.startsWith("D")) {
                String pickupKey = "P" + order.substring(1);

                if (!activePickups.contains(pickupKey)) {
                    return false;
                }

                activePickups.remove(pickupKey);
            } else {
                return false;
            }
        }

        return activePickups.isEmpty();
    }

    public static void main(String[] args) {
        String[][] testCases = {
            {"P1", "P2", "D1", "D2"}, // valid
            {"P1", "D1", "P2", "D2"}, // valid
            {"P1", "D2", "D1", "P2"}, // invalid
            {"P1", "D2"},               // invalid
            {"P1", "P2"},               // invalid
            {"P1", "D1", "D1"},        // invalid
            {},                           // valid
            {"P1", "P1", "D1"},        // invalid
            {"P1", "P1", "D1", "D1"}, // invalid
            {"P1", "D1", "P1"},        // invalid
            {"P1", "D1", "P1", "D1"}  // invalid
        };

        for (String[] testCase : testCases) {
            System.out.println(Arrays.toString(testCase) + " => " + isValidOrderList(testCase));
        }
    }
}
