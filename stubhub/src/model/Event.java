package model;

import java.time.LocalDateTime;

public class Event {
    private final String id;
    private final String name;
    private final String city;
    private final double latitude;
    private final double longitude;
    private final double price;
    private final LocalDateTime startTime;

    public Event(String id, String name, String city, double latitude, double longitude,
                 double price, LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.price = price;
        this.startTime = startTime;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getPrice() { return price; }
    public LocalDateTime getStartTime() { return startTime; }

    @Override
    public String toString() {
        return String.format("Event{id='%s', city='%s', price=%.2f, start=%s}",
                id, city, price, startTime);
    }
}
