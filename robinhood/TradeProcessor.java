package robinhood;

import java.util.*;

public class TradeProcessor {
    static class Trade {
        String type; // "BUY" or "SELL"
        int shares;
        int price;

        Trade(String type, int shares, int price) {
            this.type = type;
            this.shares = shares;
            this.price = price;
        }
    }

    public static int computeBalance(List<Trade> trades, int initialCash) {
        int cash = initialCash;
        int holding = 0;

        for (Trade t : trades) {
            if (t.type.equals("BUY")) {
                int cost = t.shares * t.price;
                if (cash >= cost) {
                    cash -= cost;
                    holding += t.shares;
                } else {
                    System.out.println("Not enough cash to buy " + t.shares + " shares.");
                }
            } else if (t.type.equals("SELL")) {
                if (holding >= t.shares) {
                    cash += t.shares * t.price;
                    holding -= t.shares;
                } else {
                    System.out.println("Not enough shares to sell " + t.shares + " shares.");
                }
            }
        }

        return cash;
    }

    public static void main(String[] args) {
        List<Trade> trades = List.of(
            new Trade("BUY", 10, 50),   // -500
            new Trade("SELL", 5, 60),   // +300
            new Trade("BUY", 20, 30)    // -600
        );
        int result = computeBalance(trades, 10000);
        System.out.println("Remaining cash balance: $" + result);  // 10000 - 500 + 300 - 600 = 9200
    }
}

