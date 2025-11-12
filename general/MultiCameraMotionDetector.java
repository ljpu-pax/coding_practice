import java.util.ArrayList;
import java.util.List;

public class MultiCameraMotionDetector {

    // Class to represent a timestamp and its corresponding intensity
    static class TimestampIntensity {
        int timestamp;
        int intensity;

        TimestampIntensity(int timestamp, int intensity) {
            this.timestamp = timestamp;
            this.intensity = intensity;
        }
    }

    // Class to represent a period with start and end timestamps
    static class Period {
        int start;
        int end;

        Period(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return "[" + start + ", " + end + "]";
        }
    }

    // Method to detect high-intensity periods for a single camera
    public static List<Period> detectHighIntensityPeriods(List<TimestampIntensity> data, int threshold) {
        List<Period> result = new ArrayList<>();
        Integer start = null;

        for (int i = 0; i < data.size(); i++) {
            TimestampIntensity current = data.get(i);
            if (current.intensity >= threshold) {
                if (start == null) {
                    start = current.timestamp;
                }
            } else {
                if (start != null) {
                    int end = data.get(i - 1).timestamp;
                    result.add(new Period(start, end));
                    start = null;
                }
            }
        }

        if (start != null) {
            int end = data.get(data.size() - 1).timestamp;
            result.add(new Period(start, end));
        }

        return result;
    }

    // Method to find overlapping periods across all cameras
    public static List<Period> findCommonHighIntensityPeriods(List<List<Period>> allCameraPeriods) {
        List<Period> commonPeriods = new ArrayList<>(allCameraPeriods.get(0));

        for (int i = 1; i < allCameraPeriods.size(); i++) {
            commonPeriods = intersectPeriods(commonPeriods, allCameraPeriods.get(i));
            if (commonPeriods.isEmpty()) {
                break;
            }
        }

        return commonPeriods;
    }

    // Helper method to find intersection between two lists of periods
    private static List<Period> intersectPeriods(List<Period> list1, List<Period> list2) {
        List<Period> result = new ArrayList<>();
        int i = 0, j = 0;

        while (i < list1.size() && j < list2.size()) {
            Period p1 = list1.get(i);
            Period p2 = list2.get(j);

            int start = Math.max(p1.start, p2.start);
            int end = Math.min(p1.end, p2.end);

            if (start <= end) {
                result.add(new Period(start, end));
            }

            if (p1.end < p2.end) {
                i++;
            } else {
                j++;
            }
        }

        return result;
    }

    public static void main(String[] args) {
        int threshold = 6;

        // Sample data for Camera 1
        List<TimestampIntensity> camera1Data = new ArrayList<>();
        camera1Data.add(new TimestampIntensity(1, 5));
        camera1Data.add(new TimestampIntensity(2, 7));
        camera1Data.add(new TimestampIntensity(3, 3));
        camera1Data.add(new TimestampIntensity(4, 8));
        camera1Data.add(new TimestampIntensity(5, 9));
        camera1Data.add(new TimestampIntensity(6, 2));

        // Sample data for Camera 2
        List<TimestampIntensity> camera2Data = new ArrayList<>();
        camera2Data.add(new TimestampIntensity(1, 2));
        camera2Data.add(new TimestampIntensity(2, 3));
        camera2Data.add(new TimestampIntensity(3, 7));
        camera2Data.add(new TimestampIntensity(4, 6));
        camera2Data.add(new TimestampIntensity(5, 5));
        camera2Data.add(new TimestampIntensity(6, 8));

        // Detect high-intensity periods for each camera
        List<Period> camera1Periods = detectHighIntensityPeriods(camera1Data, threshold);
        List<Period> camera2Periods = detectHighIntensityPeriods(camera2Data, threshold);

        // Combine periods from all cameras
        List<List<Period>> allCameraPeriods = new ArrayList<>();
        allCameraPeriods.add(camera1Periods);
        allCameraPeriods.add(camera2Periods);

        // Find common high-intensity periods across all cameras
        List<Period> commonPeriods = findCommonHighIntensityPeriods(allCameraPeriods);

        // Output the common periods
        System.out.println("Common high-intensity periods across all cameras:");
        for (Period period : commonPeriods) {
            System.out.println(period);
        }
    }
}

