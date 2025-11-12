package service;

import model.Event;
import model.User;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.logging.Logger;

// TODO: You will implement these methods during the interview.
public class OutreachEngine {
    private static final Logger log = Logger.getLogger(OutreachEngine.class.getName());
    private final EventService eventService;

    public OutreachEngine(EventService eventService) {
        this.eventService = eventService;
    }

    // TODO 1: Implement basic filtering (same city & future events)
    public CompletableFuture<List<Event>> getEventsInSameCity(User user) {
        return eventService.fetchAllEventsAsync()
                .thenApply(events -> events.stream()
                    .filter(e -> e.getCity().equalsIgnoreCase(user.getCity()))
                    .filter(e -> e.getStartTime().isAfter(LocalDateTime.now()))
                    .sorted(Comparator.comparing(Event::getStartTime))
                    .collect(Collectors.toList()))
                .exceptionally(ex -> {log.warning("Failed city filter: " + ex); return List.of(); });
                    
    }

    // TODO 2: Implement birthday-based recommendation
    public CompletableFuture<Optional<Event>> getClosestToBirthday(User user) {
        return eventService.fetchAllEventsAsync()
            .thenApply(events -> {
                LocalDate today = LocalDate.now();
                LocalDate nextBirthday = user.getBirthday().withYear(today.getYear());
                if (nextBirthday.isBefore(today)) {
                    nextBirthday = nextBirthday.plusYears(1);
                }
                final LocalDateTime birthday = nextBirthday.atStartOfDay();
                final LocalDateTime now = LocalDateTime.now();

                return events.stream()
                    .filter(e -> e.getStartTime().isAfter(now))
                    .min(Comparator.comparingLong((Event e) ->
                            Math.abs(Duration.between(birthday, e.getStartTime()).toDays()))
                        .thenComparingDouble(Event::getPrice));
            })
            .orTimeout(1, TimeUnit.SECONDS)
            .exceptionally(ex -> {log.warning("birthday calc failed" + ex); return Optional.empty(); });
    }

    // TODO 3: Implement nearest events (distance-based)
    public CompletableFuture<List<Event>> getNearestEvents(User user, int k) {
        // TODO: implement logic
        return eventService.fetchAllEventsAsync()
            .thenApply(events -> events.stream()
                .sorted(Comparator.comparingDouble(
                    e -> harversine(user.getLatitude(), user.getLongitude(),
                                   e.getLatitude(), e.getLongitude())))
                    .limit(k)
                    .collect(Collectors.toList()))
                .orTimeout(1, TimeUnit.SECONDS)
                .exceptionally(ex -> { log.warning("Nearest calc failed: " + ex); return List.of(); });
    }

    // TODO 4: Combine multiple campaigns
    public CompletableFuture<Map<String, Object>> runCampaigns(User user) {
        // TODO: combine results from multiple async methods
        CompletableFuture<List<Event>> city = getEventsInSameCity(user);
        CompletableFuture<Optional<Event>> birthday = getClosestToBirthday(user);
        CompletableFuture<List<Event>> nearest = getNearestEvents(user, 5);

        return CompletableFuture.allOf(city, birthday, nearest)
            .thenApply(v -> Map.of(
                "same_city", city.join(),
                "birthday", birthday.join().orElse(null),
                "nearest", nearest.join()
            ));

    }

    private static double harversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*
                Math.sin(dLon/2)*Math.sin(dLon/2);
        return 2 * R * Math.asin(Math.sqrt(a));
    }
}
