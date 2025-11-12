package model;

import java.time.LocalDate;

public class User {
    private final String id;
    private final String name;
    private final String city;
    private final double latitude;
    private final double longitude;
    private final LocalDate birthday;

    public User(String id, String name, String city, double latitude, double longitude, LocalDate birthday) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.birthday = birthday;
    }

    public String getCity() { return city; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public LocalDate getBirthday() { return birthday; }
}
