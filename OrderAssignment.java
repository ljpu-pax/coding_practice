import java.util.*;

public class OrderAssignment {
    // Map to store assigned orders: orderId -> dasherId
    private Map<Integer, Integer> orderAssignments = new HashMap<>();

    // Set to track assigned Dashers
    private Set<Integer> assignedDashers = new HashSet<>();

    /**
     * Assigns an order to a Dasher.
     *
     * @param orderId  the ID of the order
     * @param dasherId the ID of the Dasher
     */
    public void assignOrder(int orderId, int dasherId) {
        if (orderAssignments.containsKey(orderId)) {
            System.out.println("Error: Order ID " + orderId + " is already assigned.");
            return;
        }
        if (assignedDashers.contains(dasherId)) {
            System.out.println("Error: Dasher ID " + dasherId + " is already assigned to another order.");
            return;
        }
        orderAssignments.put(orderId, dasherId);
        assignedDashers.add(dasherId);
    }

    /**
     * Prints the current order assignments.
     */
    public void printAssignments() {
        for (Map.Entry<Integer, Integer> entry : orderAssignments.entrySet()) {
            System.out.println("Order ID: " + entry.getKey() + " -> Dasher ID: " + entry.getValue());
        }
    }

    public static void main(String[] args) {
        OrderAssignment assignmentSystem = new OrderAssignment();

        // Sample data
        assignmentSystem.assignOrder(101, 201);
        assignmentSystem.assignOrder(102, 202);
        assignmentSystem.assignOrder(101, 203); // BUG 1: Duplicate order ID
        assignmentSystem.assignOrder(103, 201); // BUG 2: Dasher 201 already assigned

        assignmentSystem.printAssignments();
    }
}
