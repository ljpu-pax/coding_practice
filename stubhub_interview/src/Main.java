import model.*;
import service.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Sample events with future dates
        List<Event> events = List.of(
            new Event(1, "Phantom of the Opera", "New York", 3572, 1455,
                LocalDateTime.of(2025, 12, 23, 19, 0)),
            new Event(2, "Metallica", "Los Angeles", 462, 975,
                LocalDateTime.of(2025, 12, 2, 20, 0)),
            new Event(3, "Metallica", "New York", 3572, 1455,
                LocalDateTime.of(2025, 12, 6, 20, 0)),
            new Event(4, "Metallica", "Boston", 3778, 1566,
                LocalDateTime.of(2026, 10, 23, 20, 0)),
            new Event(5, "LadyGaga", "New York", 3572, 1455,
                LocalDateTime.of(2026, 9, 20, 21, 0)),
            new Event(6, "LadyGaga", "Boston", 3778, 1566,
                LocalDateTime.of(2026, 8, 1, 21, 0)),
            new Event(7, "LadyGaga", "Chicago", 2608, 1525,
                LocalDateTime.of(2026, 7, 4, 21, 0)),
            new Event(8, "LadyGaga", "San Francisco", 183, 1233,
                LocalDateTime.of(2026, 7, 7, 21, 0)),
            new Event(9, "LadyGaga", "Washington", 3358, 1320,
                LocalDateTime.of(2026, 5, 22, 21, 0)),
            new Event(10, "Metallica", "Chicago", 2608, 1525,
                LocalDateTime.of(2026, 1, 1, 20, 0)),
            new Event(11, "Phantom of the Opera", "San Francisco", 183, 1233,
                LocalDateTime.of(2026, 7, 4, 19, 0)),
            new Event(12, "Phantom of the Opera", "Chicago", 2608, 1525,
                LocalDateTime.of(2026, 5, 15, 19, 0))
        );

        Customer customer = new Customer(1, "Amos", "New York", 3572, 1455,
            LocalDate.of(1995, 5, 11));

        MarketingEngine engine = new MarketingEngine(events);

        // Q1: Events in same city
        engine.sendCustomerNotifications(customer);

        // Q2: Event closest to birthday
        engine.sendCustomerNotificationsByBirthday(customer);

        // Q3: 5 closest events by distance
        engine.sendCustomerNotificationsByProximity(customer);

        // Q4: 5 cheapest tickets within radius (TODO)
        engine.sendCheapestTicketsInRadius(customer, 2000);
    }
}
