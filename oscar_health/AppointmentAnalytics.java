import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Appointment analytics helpers:
 * 1. Find the day with the highest appointment count (ties broken by the earlier day).
 * 2. Given an integer K, find the maximum number of appointments that fall inside any K-day consecutive window.
 */
public class AppointmentAnalytics {

    /** Returns the day that holds the most appointments. */
    public int mostBookedDay(List<Integer> apptDays) {
        if (apptDays == null || apptDays.isEmpty()) {
            throw new IllegalArgumentException("Appointment list cannot be null or empty");
        }

        Map<Integer, Integer> freq = buildFrequencyMap(apptDays);
        int bestDay = Integer.MAX_VALUE;
        int bestCount = -1;
        for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
            int day = entry.getKey();
            int count = entry.getValue();
            if (count > bestCount || (count == bestCount && day < bestDay)) {
                bestDay = day;
                bestCount = count;
            }
        }
        return bestDay;
    }

    /**
     * Returns the maximum appointments found within any consecutive K-day range.
     * Uses a sliding window over the sorted unique days; window is valid while
     * (rightDay - leftDay + 1) <= K.
     */
    public int maxAppointmentsInWindow(List<Integer> apptDays, int k) {
        if (apptDays == null || apptDays.isEmpty() || k <= 0) {
            return 0;
        }

        Map<Integer, Integer> freq = buildFrequencyMap(apptDays);
        List<Integer> uniqueDays = new ArrayList<>(freq.keySet());
        Collections.sort(uniqueDays);

        int maxAppts = 0;
        int windowCount = 0;
        int left = 0;
        for (int right = 0; right < uniqueDays.size(); right++) {
            int rightDay = uniqueDays.get(right);
            windowCount += freq.get(rightDay);

            while (left <= right && rightDay - uniqueDays.get(left) + 1 > k) {
                int leftDay = uniqueDays.get(left);
                windowCount -= freq.get(leftDay);
                left++;
            }

            maxAppts = Math.max(maxAppts, windowCount);
        }

        return maxAppts;
    }

    private Map<Integer, Integer> buildFrequencyMap(List<Integer> apptDays) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (Integer day : apptDays) {
            if (day == null) {
                continue;
            }
            freq.merge(day, 1, Integer::sum);
        }
        return freq;
    }

    public static void main(String[] args) {
        AppointmentAnalytics analytics = new AppointmentAnalytics();
        List<Integer> sample = Arrays.asList(14, 14, 2, 3, 1);
        System.out.println("Most booked day: " + analytics.mostBookedDay(sample)); // 14

        List<Integer> sample2 = Arrays.asList(14, 14, 2, 3, 1, 1);
        System.out.println("Max appts in 3-day window: " + analytics.maxAppointmentsInWindow(sample2, 3)); // 4 (days 1-3)
    }
}
