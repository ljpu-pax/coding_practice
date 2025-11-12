package model;

public class City {
    private final String name;
    private final int xCor;
    private final int yCor;

    public City(String name, int xCor, int yCor) {
        this.name = name;
        this.xCor = xCor;
        this.yCor = yCor;
    }

    public String getName() { return name; }
    public int getXCor() { return xCor; }
    public int getYCor() { return yCor; }

    public double distanceTo(City other) {
        int dx = this.xCor - other.xCor;
        int dy = this.yCor - other.yCor;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
