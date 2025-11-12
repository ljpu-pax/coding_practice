import java.util.*;

public class ConsistentHashing {
    private TreeMap<Integer, String> ring = new TreeMap<>();

    public void addServer(String server) {
        int hash = server.hashCode();
        ring.put(hash, server);
    }

    public String getServer(String key) {
        int hash = key.hashCode();
        if (!ring.containsKey(hash)) {
            SortedMap<Integer, String> tailMap = ring.tailMap(hash);
            hash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
        }
        return ring.get(hash);
    }

    // Test method
    public static void main(String[] args) {
        ConsistentHashing ch = new ConsistentHashing();
        ch.addServer("Server1");
        ch.addServer("Server2");
        ch.addServer("Server3");

        String key1 = "User1";
        String key2 = "User2";

        String server1 = ch.getServer(key1);
        String server2 = ch.getServer(key2);

        System.out.println("User1 assigned to: " + server1);
        System.out.println("User2 assigned to: " + server2);

        if (server1.equals(server2)) {
            System.out.println("Both users assigned to the same server.");
        } else {
            System.out.println("Users assigned to different servers.");
        }
    }
}
