package model;

import java.time.LocalDateTime;

public class Event {
    private final int id;
    private final String name;
    private final String city;
    private final int xCor;
    private final int yCor;
    private final LocalDateTime eventDate;

    public Event(int id, String name, String city, int xCor, int yCor, LocalDateTime eventDate) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.xCor = xCor;
        this.yCor = yCor;
        this.eventDate = eventDate;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public int getXCor() { return xCor; }
    public int getYCor() { return yCor; }
    public LocalDateTime getEventDate() { return eventDate; }

    public double distanceTo(int x, int y) {
        int dx = this.xCor - x;
        int dy = this.yCor - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String toString() {
        return String.format("Event{id=%d, name='%s', city='%s', date=%s}",
                id, name, city, eventDate);
    }
}
