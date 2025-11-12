import java.util.*;

public class TransactionAnalyzer {
    static class LogEntry {
        String transactionId;
        long timestamp;
        String ip;

        LogEntry(String transactionId, long timestamp, String ip) {
            this.transactionId = transactionId;
            this.timestamp = timestamp;
            this.ip = ip;
        }
    }

    public static Map<String, Double> computeAverageTransactionTime(List<LogEntry> startLogs, List<LogEntry> endLogs) {
        Map<String, LogEntry> startMap = new HashMap<>();
        for (LogEntry entry : startLogs) {
            startMap.put(entry.transactionId, entry);
        }

        Map<String, List<Long>> ipDurations = new HashMap<>();
        for (LogEntry endEntry : endLogs) {
            LogEntry startEntry = startMap.get(endEntry.transactionId);
            if (startEntry != null && startEntry.ip.equals(endEntry.ip)) {
                long duration = endEntry.timestamp - startEntry.timestamp;
                ipDurations.computeIfAbsent(endEntry.ip, k -> new ArrayList<>()).add(duration);
            }
        }

        Map<String, Double> averageDurations = new HashMap<>();
        for (Map.Entry<String, List<Long>> entry : ipDurations.entrySet()) {
            List<Long> durations = entry.getValue();
            double average = durations.stream().mapToLong(Long::longValue).average().orElse(0.0);
            averageDurations.put(entry.getKey(), average);
        }

        return averageDurations;
    }

    // Example usage
    public static void main(String[] args) {
        List<LogEntry> startLogs = Arrays.asList(
            new LogEntry("txn1", 1000, "192.168.1.1"),
            new LogEntry("txn2", 2000, "192.168.1.2")
        );

        List<LogEntry> endLogs = Arrays.asList(
            new LogEntry("txn1", 1500, "192.168.1.1"),
            new LogEntry("txn2", 2500, "192.168.1.2")
        );

        Map<String, Double> result = computeAverageTransactionTime(startLogs, endLogs);
        for (Map.Entry<String, Double> entry : result.entrySet()) {
            System.out.println("IP: " + entry.getKey() + ", Average Duration: " + entry.getValue());
        }
    }
}
