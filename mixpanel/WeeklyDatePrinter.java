package mixpanel;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;


// 给你两个日期，打印中间的所有weeks(周六是第一天），比如 ‘2017-10-27‘，’2017-11-5’ 
// 需要打印‘2017-10-27 to 2017-10-27‘，’2017-10-28 to 2017-11-4’ , ’2017-11-5 to 2017-11-5’

public class WeeklyDatePrinter {

    public static List<String> getWeeklyRanges(String startStr, String endStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startStr, formatter);
        LocalDate end = LocalDate.parse(endStr, formatter);

        List<String> result = new ArrayList<>();

        // First partial week (before Saturday)
        if (start.getDayOfWeek() != DayOfWeek.SATURDAY) {
            LocalDate firstEnd = start;
            DayOfWeek day = start.getDayOfWeek();
            int daysToSaturday = DayOfWeek.SATURDAY.getValue() - day.getValue();
            if (daysToSaturday > 0) {
                firstEnd = start.plusDays(daysToSaturday);
            }
            if (firstEnd.isAfter(end)) firstEnd = end;
            result.add(formatRange(start, firstEnd, formatter));
            start = firstEnd.plusDays(1);
        }

        // Full weeks
        while (!start.isAfter(end)) {
            LocalDate weekStart = start;
            LocalDate weekEnd = start.plusDays(6);
            if (weekEnd.isAfter(end)) weekEnd = end;
            result.add(formatRange(weekStart, weekEnd, formatter));
            start = weekEnd.plusDays(1);
        }

        return result;
    }

    private static String formatRange(LocalDate start, LocalDate end, DateTimeFormatter formatter) {
        return formatter.format(start) + " to " + formatter.format(end);
    }

    // Test
    public static void main(String[] args) {
        List<String> result = getWeeklyRanges("2017-10-27", "2017-11-5");
        for (String range : result) {
            System.out.println(range);
        }
    }
}
