import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartyAnalytics {
    // Class to represent a party
    static class Party {
        String partyId;
        LocalDateTime startTime;
        LocalDateTime endTime;
        String neighborhood;
        String city;
        String state;

        Party(String partyId, String startTimestamp, String endTimestamp, String neighborhood, String city, String state) {
            this.partyId = partyId;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.startTime = LocalDateTime.parse(startTimestamp, formatter);
            this.endTime = LocalDateTime.parse(endTimestamp, formatter);
            this.neighborhood = neighborhood;
            this.city = city;
            this.state = state;
        }
    }

    static class Interval {
        LocalDateTime start;
        LocalDateTime end;

        Interval(LocalDateTime start, LocalDateTime end) {
            this.start = start;
            this.end = end;
        }
    }

    public static Map<String, int[]> getNeighborhoodTimeBlocks(List<Party> parties) {
        Map<String, List<Party>> neighborhoodMap = new HashMap<>();

        for (Party party : parties) {
            neighborhoodMap.computeIfAbsent(party.neighborhood, k -> new ArrayList<>()).add(party);
        }

        Map<String, int[]> result = new HashMap<>();

        for (Map.Entry<String, List<Party>> entry : neighborhoodMap.entrySet()) {
            String neighborhood = entry.getKey();
            List<Party> partyList = entry.getValue();
            
            LocalDateTime earliestStart = partyList.stream()
                    .map(p -> p.startTime)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);

            LocalDateTime latestEnd = partyList.stream()
                    .map(p -> p.endTime)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            if (earliestStart != null && latestEnd != null) {
                result.put(neighborhood, new int[]{earliestStart.getHour(), latestEnd.getHour()});
            }

        }
        return result;
    }

    public static Map<String, Long> calculateDeadzoneHours(List<Party> parties) {
        Map<String, List<Interval>> cityIntervals = new HashMap<>();

        for (Party party : parties) {
            cityIntervals
                .computeIfAbsent(party.city, k -> new ArrayList<>())
                .add(new Interval(party.startTime, party.endTime));
        }

        Map<String, Long> result = new HashMap<>();

        for (Map.Entry<String, List<Interval>> entry : cityIntervals.entrySet()) {
            String city = entry.getKey();
            List<Interval> intervals = entry.getValue();

            intervals.sort(Comparator.comparing(i -> i.start));

            List<Interval> merged = new ArrayList<>();

            for (Interval interval : intervals) {
                if (merged.isEmpty() || merged.get(merged.size() - 1).end.isBefore(interval.start)) {
                    merged.add(interval);
                } else {
                    Interval last = merged.get(merged.size() - 1);
                    last.end = last.end.isAfter(interval.end) ? last.end : interval.end;
                }
            }

             // Calculate gaps between merged intervals
             long deadzone = 0;
             for (int i = 1; i < merged.size(); i++) {
                 Duration gap = Duration.between(merged.get(i - 1).end, merged.get(i).start);
                 deadzone += gap.toHours();
             }
 
             result.put(city, deadzone);
        }

        return result;
    }

    // Example usage
    public static void main(String[] args) {
        List<Party> parties = Arrays.asList(
            new Party("p1", "2025-04-24 13:00:00", "2025-04-24 15:00:00", "Downtown", "Metropolis", "NY"),
            new Party("p2", "2025-04-24 17:00:00", "2025-04-24 20:00:00", "Downtown", "Metropolis", "NY"),
            new Party("p3", "2025-04-24 10:00:00", "2025-04-24 12:00:00", "Uptown", "Gotham", "NJ"),
            new Party("p4", "2025-04-24 14:00:00", "2025-04-24 16:00:00", "Uptown", "Gotham", "NJ"),
            new Party("p5", "2025-04-24 18:00:00", "2025-04-24 19:00:00", "Midtown", "Metropolis", "NY")
        );

        Map<String, int[]> neighborhoodBlocks = getNeighborhoodTimeBlocks(parties);
        System.out.println("Neighborhood Time Blocks:");
        for (Map.Entry<String, int[]> entry : neighborhoodBlocks.entrySet()) {
            System.out.println(entry.getKey() + ": Start Hour = " + entry.getValue()[0] + ", End Hour = " + entry.getValue()[1]);
        }

        Map<String, Long> cityDeadzones = calculateDeadzoneHours(parties);
        System.out.println("\nCity Deadzone Hours:");
        for (Map.Entry<String, Long> entry : cityDeadzones.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " hour(s)");
        }
    }
}
