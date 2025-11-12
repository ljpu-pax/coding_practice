package doordash;
import java.util.*;

public class DasherWageCalculator {

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

    public static double calculateWage(List<String[]> actions, double rate) {
        List<Event> events = new ArrayList<>();
        for (String[] action : actions) {
            String orderId = action[0];
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
            int duration = event.time - prevTime;
            totalWage += rate * currentOrders * duration;
            currentOrders += event.delta;
            prevTime = event.time;
        }

        return totalWage;
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

        double rate = 1.0; // 每小时每个订单的费率
        double wage = calculateWage(actions, rate);
        System.out.printf("Total wage: $%.2f%n", wage);
    }
}

