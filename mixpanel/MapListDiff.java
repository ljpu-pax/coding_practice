package mixpanel;

import java.util.*;

public class MapListDiff {

    public static class ListDiff {
        String key;
        int index;
        String action; // "ADD", "DELETE", "UPDATE"
        String value;

        ListDiff(String key, int index, String action, String value) {
            this.key = key;
            this.index = index;
            this.action = action;
            this.value = value;
        }

        @Override
        public String toString() {
            return "ListDiff{" +
                    "key='" + key + '\'' +
                    ", index=" + index +
                    ", action='" + action + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public static List<ListDiff> compareListValues(Map<String, List<String>> map1, Map<String, List<String>> map2) {
        List<ListDiff> result = new ArrayList<>();

        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(map1.keySet());
        allKeys.addAll(map2.keySet());

        for (String key : allKeys) {
            List<String> list1 = map1.getOrDefault(key, new ArrayList<>());
            List<String> list2 = map2.getOrDefault(key, new ArrayList<>());
            int maxLen = Math.max(list1.size(), list2.size());

            for (int i = 0; i < maxLen; i++) {
                String val1 = i < list1.size() ? list1.get(i) : null;
                String val2 = i < list2.size() ? list2.get(i) : null;

                if (Objects.equals(val1, val2)) {
                    continue; // no diff
                } else if (val1 == null) {
                    result.add(new ListDiff(key, i, "ADD", val2));
                } else if (val2 == null) {
                    result.add(new ListDiff(key, i, "DELETE", val1));
                } else {
                    result.add(new ListDiff(key, i, "UPDATE", val2));
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        Map<String, List<String>> map1 = new HashMap<>();
        map1.put("apple", Arrays.asList("a", "b", "c"));
        map1.put("peach", Arrays.asList("red", "green"));

        Map<String, List<String>> map2 = new HashMap<>();
        map2.put("apple", Arrays.asList("a", "x", "c", "d"));
        map2.put("peach", Arrays.asList("red"));

        List<ListDiff> diffs = compareListValues(map1, map2);
        for (ListDiff d : diffs) {
            System.out.println(d);
        }
    }
}

