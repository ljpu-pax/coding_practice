package doordash;
import java.util.*;

public class DasherAssignment {
    private Map<String, Integer> dasherToIndex = new HashMap<>();
    private List<String> dashers = new ArrayList<>();

    public void addDasher(String dasherId) {
        dashers.add(dasherId);
        dasherToIndex.put(dasherId, dashers.size() - 1);
    }

    public void removeDasher(String dasherId) {
        Integer index = dasherToIndex.get(dasherId);
        if (index != null) {
            dashers.remove((int) index);
            dasherToIndex.remove(dasherId);
        }
    }

    public String assignDasher() {
        if (dashers.isEmpty()) return null;
        int randomIndex = new Random().nextInt(dashers.size());
        return dashers.get(randomIndex);
    }

    // Test method
    public static void main(String[] args) {
        DasherAssignment da = new DasherAssignment();
        da.addDasher("D1");
        da.addDasher("D2");
        da.addDasher("D3");
        da.removeDasher("D2");
        // This may throw IndexOutOfBoundsException due to stale indices
        try {
            String assigned = da.assignDasher();
            System.out.println("Assigned Dasher: " + assigned);
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e);
        }
    }
}

