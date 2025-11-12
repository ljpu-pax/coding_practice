package mixpanel;

import java.util.*;

public class MapDiff {

    public static class Change {
        String key;
        String action; // "ADD", "UPDATE", or "DELETE"
        String newValue; // null for DELETE

        Change(String key, String action, String newValue) {
            this.key = key;
            this.action = action;
            this.newValue = newValue;
        }

        @Override
        public String toString() {
            return "Change{" +
                    "key='" + key + '\'' +
                    ", action='" + action + '\'' +
                    ", newValue='" + newValue + '\'' +
                    '}';
        }
    }

    public static List<Change> diff(Map<String, String> map1, Map<String, String> map2) {
        List<Change> result = new ArrayList<>();

        // Check for updates and deletes
        for (String key : map1.keySet()) {
            if (!map2.containsKey(key)) {
                result.add(new Change(key, "DELETE", null));
            } else if (!Objects.equals(map1.get(key), map2.get(key))) {
                result.add(new Change(key, "UPDATE", map2.get(key)));
            }
        }

        // Check for additions
        for (String key : map2.keySet()) {
            if (!map1.containsKey(key)) {
                result.add(new Change(key, "ADD", map2.get(key)));
            }
        }

        return result;
    }

    // Test
    public static void main(String[] args) {
        Map<String, String> map1 = new HashMap<>();
        map1.put("apple", "1");
        map1.put("peach", "red");

        Map<String, String> map2 = new HashMap<>();
        map2.put("apple", "2");
        map2.put("peach", "blue");
        map2.put("banana", "yellow");

        List<Change> changes = diff(map1, map2);
        for (Change c : changes) {
            System.out.println(c);
        }
    }
}

