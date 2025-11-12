package doordash;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DasherActiveTimeCalculator {

    // Class to represent each log entry
    static class LogEntry {
        int dasherId;
        int deliveryId;
        LocalDateTime timestamp;
        String status;

        public LogEntry(int dasherId, int deliveryId, String timestampStr, String status) {
            this.dasherId = dasherId;
            this.deliveryId = deliveryId;
            this.timestamp = LocalDateTime.parse(timestampStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            this.status = status;
        }
    }

    // Method to calculate active time per dasher
    public static Map<Integer, Long> calculateActiveTime(List<LogEntry> logs) {
        // Sort logs by timestamp
        logs.sort(Comparator.comparing(log -> log.timestamp));

        // Map to store logs per dasher and delivery
        Map<Integer, Map<Integer, List<LogEntry>>> dasherDeliveryLogs = new HashMap<>();

        for (LogEntry log : logs) {
            dasherDeliveryLogs
                .computeIfAbsent(log.dasherId, k -> new HashMap<>())
                .computeIfAbsent(log.deliveryId, k -> new ArrayList<>())
                .add(log);
        }

        // Map to store total active time per dasher
        Map<Integer, Long> dasherActiveTime = new HashMap<>();

        for (Map.Entry<Integer, Map<Integer, List<LogEntry>>> dasherEntry : dasherDeliveryLogs.entrySet()) {
            int dasherId = dasherEntry.getKey();
            long totalActiveTime = 0;

            for (List<LogEntry> deliveryLogs : dasherEntry.getValue().values()) {
                LocalDateTime acceptedTime = null;
                LocalDateTime deliveredTime = null;

                for (LogEntry log : deliveryLogs) {
                    if (log.status.equals("ACCEPTED")) {
                        acceptedTime = log.timestamp;
                    } else if (log.status.equals("DELIVERED")) {
                        deliveredTime = log.timestamp;
                    }
                }

                if (acceptedTime != null && deliveredTime != null && !deliveredTime.isBefore(acceptedTime)) {
                    totalActiveTime += Duration.between(acceptedTime, deliveredTime).getSeconds();
                }
            }

            dasherActiveTime.put(dasherId, totalActiveTime);
        }

        return dasherActiveTime;
    }

    // Sample test case
    public static void main(String[] args) {
        List<LogEntry> logs = Arrays.asList(
            new LogEntry(1, 101, "2024-01-01 10:00:00", "STARTED"),
            new LogEntry(1, 101, "2024-01-01 10:05:00", "ACCEPTED"),
            new LogEntry(1, 101, "2024-01-01 10:20:00", "DELIVERED"),
            new LogEntry(2, 102, "2024-01-01 11:00:00", "ACCEPTED"),
            new LogEntry(2, 102, "2024-01-01 11:30:00", "DELIVERED")
        );

        Map<Integer, Long> activeTimes = calculateActiveTime(logs);
        for (Map.Entry<Integer, Long> entry : activeTimes.entrySet()) {
            System.out.println("Dasher " + entry.getKey() + " active time: " + entry.getValue() + " seconds");
        }
    }
}
