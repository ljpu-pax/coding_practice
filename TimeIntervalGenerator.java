import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TimeIntervalGenerator {
    public static List<String> generateTimeIntervals(String startStr, String endStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime start = LocalDateTime.parse(startStr, formatter);
        LocalDateTime end = LocalDateTime.parse(endStr, formatter);

        List<String> result = new ArrayList<>();
        while (!start.isAfter(end)) {
            result.add(start.format(formatter));
            start = start.plusMinutes(5);
        }
        return result;
    }

    public static void main(String[] args) {
        String start = "2025-04-11 09:00";
        String end = "2025-04-11 10:00";
        List<String> intervals = generateTimeIntervals(start, end);
        intervals.forEach(System.out::println);
    }
}

