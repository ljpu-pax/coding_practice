package service;

import model.City;
import model.Customer;
import model.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Marketing Engine for StubHub - sends targeted event notifications to customers
 *
 * Q1: Notify customers of events in their city
 * Q2: Notify customers of event closest to their birthday
 * Q3: Notify customers of 5 closest events by distance
 * Q4: Notify customers of 5 cheapest tickets within Y mile radius
 */
public class MarketingEngine {

    private final List<Event> events;
    private final Map<String, List<Event>> cityToEvents;
    private final List<Event> eventsSortedByDate;

    // City coordinate map (roughly to scale with miles in USA)
    private static final Map<String, City> CITY_MAP = Map.of(
        "New York", new City("New York", 3572, 1455),
        "Los Angeles", new City("Los Angeles", 462, 975),
        "Boston", new City("Boston", 3778, 1566),
        "Chicago", new City("Chicago", 2608, 1525),
        "San Francisco", new City("San Francisco", 183, 1233),
        "Washington", new City("Washington", 3358, 1320)
    );

    public MarketingEngine(List<Event> allEvents) {
        LocalDateTime now = LocalDateTime.now();

        // Filter to only future events
        this.events = allEvents.stream()
                .filter(e -> e.getEventDate().isAfter(now))
                .collect(Collectors.toList());

        // Index events by city for Q1
        this.cityToEvents = new HashMap<>();
        for (Event event : this.events) {
            cityToEvents.computeIfAbsent(event.getCity(), k -> new ArrayList<>()).add(event);
        }

        // Sort events by date for Q2
        this.eventsSortedByDate = new ArrayList<>(this.events);
        this.eventsSortedByDate.sort(Comparator.comparing(Event::getEventDate));
    }

    /**
     * Q1: Send notification of all events in the customer's city
     */
    public void sendCustomerNotifications(Customer customer) {
        System.out.println("Calling send_customer_notifications for customer " + customer.getName());
        List<Event> cityEvents = cityToEvents.getOrDefault(customer.getCity(), List.of());
        System.out.println(cityEvents);
        System.out.println();
    }

    /**
     * Q2: Send notification of event closest to customer's next birthday
     *
     * Ask: What if there's a tie? -> Take first event
     * Ask: Does timezone matter? -> For this exercise, no
     */
    public void sendCustomerNotificationsByBirthday(Customer customer) {
        System.out.println("Calling send_customer_notifications_by_birthday for customer " + customer.getName());

        if (eventsSortedByDate.isEmpty()) {
            System.out.println("No events available");
            System.out.println();
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate nextBirthday = customer.getBirthDate().withYear(today.getYear());
        if (today.isAfter(nextBirthday)) {
            nextBirthday = nextBirthday.plusYears(1);
        }
        LocalDateTime birthdayDateTime = nextBirthday.atStartOfDay();

        System.out.println("next_birthday=" + nextBirthday);

        // Binary search for closest event
        int idx = binarySearchClosest(birthdayDateTime);
        System.out.println(eventsSortedByDate.get(idx));
        System.out.println();
    }

    private int binarySearchClosest(LocalDateTime target) {
        int left = 0, right = eventsSortedByDate.size() - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (eventsSortedByDate.get(mid).getEventDate().isBefore(target)) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        // Check if left-1 is closer
        if (left > 0) {
            long diffRight = Math.abs(java.time.Duration.between(
                eventsSortedByDate.get(left).getEventDate(), target).toDays());
            long diffLeft = Math.abs(java.time.Duration.between(
                eventsSortedByDate.get(left - 1).getEventDate(), target).toDays());
            if (diffLeft <= diffRight) {
                return left - 1;
            }
        }

        return left;
    }

    /**
     * Q3: Send notification of 5 closest events by distance
     * Assumption: ~10M events, called per user
     */
    public void sendCustomerNotificationsByProximity(Customer customer) {
        System.out.println("Calling send_customer_notifications_by_proximity for customer " + customer.getName());

        City customerCity = CITY_MAP.get(customer.getCity());
        if (customerCity == null) {
            System.out.println("No city found for " + customer.getName());
            System.out.println();
            return;
        }

        // Sort cities by distance to customer
        List<City> nearestCities = new ArrayList<>(CITY_MAP.values());
        nearestCities.sort(Comparator.comparingDouble(city -> customerCity.distanceTo(city)));

        // Collect 5 closest events
        List<Event> result = new ArrayList<>();
        for (City city : nearestCities) {
            List<Event> cityEvents = cityToEvents.getOrDefault(city.getName(), List.of());
            for (Event event : cityEvents) {
                result.add(event);
                if (result.size() == 5) {
                    break;
                }
            }
            if (result.size() == 5) {
                break;
            }
        }

        System.out.println("Closest events:");
        System.out.println(result);
        System.out.println();
    }

    /**
     * Q4: Send notification of 5 cheapest tickets within Y mile radius
     * API: https://sh-mockapi.azurewebsites.net/api/ticketprice
     *
     * Ask: Should we cache prices? For how long?
     * Ask: What if there are < 5 events in radius?
     */
    public void sendCheapestTicketsInRadius(Customer customer, double radiusMiles) {
        System.out.println("Calling send_cheapest_tickets_in_radius for customer " + customer.getName() +
                          " (radius: " + radiusMiles + " miles)");

        City customerCity = CITY_MAP.get(customer.getCity());
        if (customerCity == null) {
            System.out.println("No city found for " + customer.getName());
            System.out.println();
            return;
        }

        // Find all cities within radius
        List<City> citiesWithinRadius = new ArrayList<>();
        for (City city : CITY_MAP.values()) {
            if (customerCity.distanceTo(city) <= radiusMiles) {
                citiesWithinRadius.add(city);
            }
        }

        // Collect events from cities within radius
        List<Event> eventsInRadius = new ArrayList<>();
        for (City city : citiesWithinRadius) {
            eventsInRadius.addAll(cityToEvents.getOrDefault(city.getName(), List.of()));
        }

        System.out.println("event_ids_in_radius=" +
            eventsInRadius.stream().map(Event::getId).collect(Collectors.toList()));

        // Fetch prices for all events
        PriceService priceService = new PriceService();
        Map<Integer, model.TicketPrice> prices = priceService.getAllPrices();

        // Sort events by price and take cheapest 5
        List<Event> cheapestEvents = eventsInRadius.stream()
            .filter(e -> prices.containsKey(e.getId()))
            .sorted(Comparator.comparingDouble(e -> prices.get(e.getId()).getPrice()))
            .limit(5)
            .collect(Collectors.toList());

        System.out.println("5 cheapest tickets:");
        for (Event event : cheapestEvents) {
            model.TicketPrice price = prices.get(event.getId());
            System.out.println(event + " - " + price);
        }
        System.out.println();
    }
}
