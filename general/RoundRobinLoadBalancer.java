import java.util.*;

public class RoundRobinLoadBalancer {
    private List<String> servers = new ArrayList<>();
    private int currentIndex = 0;

    public RoundRobinLoadBalancer(List<String> serverList) {
        servers.addAll(serverList);
    }

    public String getNextServer() {
        currentIndex = currentIndex % servers.size();
        String server = servers.get(currentIndex);
        currentIndex++;
        return server;
    }

    // Test method
    public static void main(String[] args) {
        RoundRobinLoadBalancer lb = new RoundRobinLoadBalancer(Arrays.asList("S1", "S2"));
        try {
            System.out.println(lb.getNextServer()); // S1
            System.out.println(lb.getNextServer()); // S2
            System.out.println(lb.getNextServer()); // Should throw IndexOutOfBoundsException
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e);
        }
    }
}

