import java.util.ArrayList;
import java.util.List;

public class MotionIntensityDetector {

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

        // Handle case where the last period goes till the end
        if (start != null) {
            int end = data.get(data.size() - 1).timestamp;
            result.add(new Period(start, end));
        }

        return result;
    }

    // Example usage
    public static void main(String[] args) {
        List<TimestampIntensity> data = new ArrayList<>();
        data.add(new TimestampIntensity(1, 5));
        data.add(new TimestampIntensity(2, 7));
        data.add(new TimestampIntensity(3, 3));
        data.add(new TimestampIntensity(4, 8));
        data.add(new TimestampIntensity(5, 9));
        data.add(new TimestampIntensity(6, 2));

        int threshold = 6;
        List<Period> periods = detectHighIntensityPeriods(data, threshold);
        for (Period period : periods) {
            System.out.println(period);
        }
    }
}

