package model;

public class TicketPrice {
    private final int id;
    private final String currencyCode;
    private final double price;

    public TicketPrice(int id, String currencyCode, double price) {
        this.id = id;
        this.currencyCode = currencyCode;
        this.price = price;
    }

    public int getId() { return id; }
    public String getCurrencyCode() { return currencyCode; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return String.format("TicketPrice{id=%d, price=%.2f %s}", id, price, currencyCode);
    }
}
