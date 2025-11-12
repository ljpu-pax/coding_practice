package model;

import java.time.LocalDate;

public class Customer {
    private final int id;
    private final String name;
    private final String city;
    private final int xCor;
    private final int yCor;
    private final LocalDate birthDate;

    public Customer(int id, String name, String city, int xCor, int yCor, LocalDate birthDate) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.xCor = xCor;
        this.yCor = yCor;
        this.birthDate = birthDate;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public int getXCor() { return xCor; }
    public int getYCor() { return yCor; }
    public LocalDate getBirthDate() { return birthDate; }

    public double distanceTo(Event event) {
        int dx = this.xCor - event.getXCor();
        int dy = this.yCor - event.getYCor();
        return Math.sqrt(dx * dx + dy * dy);
    }
}
