package doordash;
import java.util.*;

public class DasherWageCalculatorRushHour {

    static class Event implements Comparable<Event> {
        int time;
        int delta; // +1 for Pickup, -1 for Deliver

        Event(int time, int delta) {
            this.time = time;
            this.delta = delta;
        }

        @Override
        public int compareTo(Event other) {
            return Integer.compare(this.time, other.time);
        }
    }

    static class Interval {
        int start;
        int end;

        Interval(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    public static double calculateWage(List<String[]> actions, List<Interval> rushHours, double rate) {
        List<Event> events = new ArrayList<>();
        for (String[] action : actions) {
            String type = action[1];
            int time = Integer.parseInt(action[2]);
            int delta = type.equals("Pickup") ? 1 : -1;
            events.add(new Event(time, delta));
        }

        Collections.sort(events);

        double totalWage = 0.0;
        int currentOrders = 0;
        int prevTime = events.get(0).time;

        for (Event event : events) {
            int currTime = event.time;
            if (currTime > prevTime && currentOrders > 0) {
                totalWage += computeWageForInterval(prevTime, currTime, currentOrders, rate, rushHours);
            }
            currentOrders += event.delta;
            prevTime = currTime;
        }

        return totalWage;
    }

    private static double computeWageForInterval(int start, int end, int orders, double rate, List<Interval> rushHours) {
        double wage = 0.0;
        int curr = start;

        while (curr < end) {
            int next = end;
            boolean isRush = false;
            for (Interval rush : rushHours) {
                if (rush.start <= curr && curr < rush.end) {
                    next = Math.min(end, rush.end);
                    isRush = true;
                    break;
                } else if (curr < rush.start && rush.start < end) {
                    next = Math.min(end, rush.start);
                    isRush = false;
                    break;
                }
            }
            int duration = next - curr;
            double currentRate = isRush ? rate * 2 : rate;
            wage += currentRate * orders * duration;
            curr = next;
        }

        return wage;
    }

    public static void main(String[] args) {
        List<String[]> actions = Arrays.asList(
            new String[]{"order1", "Pickup", "0"},
            new String[]{"order2", "Pickup", "1"},
            new String[]{"order1", "Deliver", "5"},
            new String[]{"order3", "Pickup", "6"},
            new String[]{"order2", "Deliver", "12"},
            new String[]{"order3", "Deliver", "20"}
        );

        List<Interval> rushHours = Arrays.asList(
            new Interval(3, 7),  // 3 to 7
            new Interval(10, 13) // 10 to 13
        );

        double rate = 1.0; // 每小时每个订单的基础费率
        double wage = calculateWage(actions, rushHours, rate);
        System.out.printf("Total wage: $%.2f%n", wage);
    }
}
