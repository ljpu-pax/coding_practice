import java.util.*;

public class SongPairFinder {
    public static List<String> findPair(List<String[]> songs, int targetSeconds) {
        Map<Integer, String> durationMap = new HashMap<>();

        for (String[] song : songs) {
            String title = song[0];
            String durationStr = song[1];
            int duration = parseDuration(durationStr);
            int complement = targetSeconds - duration;

            if (durationMap.containsKey(complement)) {
                return Arrays.asList(durationMap.get(complement), title);
            }

            durationMap.put(duration, title);
        }

        return Collections.emptyList(); // No pair found
    }

    private static int parseDuration(String durationStr) {
        String[] parts = durationStr.split(":");
        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);
        return minutes * 60 + seconds;
    }

    public static void main(String[] args) {
        List<String[]> songs = Arrays.asList(
            new String[]{"A", "3:44"},
            new String[]{"B", "5:00"},
            new String[]{"C", "3:16"}
        );

        List<String> result = findPair(songs, 420); // 7 minutes = 420 seconds
        if (!result.isEmpty()) {
            System.out.println("Pair found: " + result.get(0) + ", " + result.get(1));
        } else {
            System.out.println("No pair found.");
        }
    }
}

