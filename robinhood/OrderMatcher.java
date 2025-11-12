package robinhood;

import java.util.*;

public class OrderMatcher {
    static class Order {
        int price;
        int qty;
        Order(int price, int qty) {
            this.price = price;
            this.qty = qty;
        }
    }

    public static int matchOrders(List<String[]> orders) {
        // buyBook: 按价格降序
        PriorityQueue<Order> buyBook = new PriorityQueue<>((a, b) -> b.price - a.price);
        // sellBook: 按价格升序
        PriorityQueue<Order> sellBook = new PriorityQueue<>((a, b) -> a.price - b.price);

        int totalExecuted = 0;

        for (String[] ord : orders) {
            int price = Integer.parseInt(ord[0]);
            int qty = Integer.parseInt(ord[1]);
            String side = ord[2];

            if (side.equals("buy")) {
                // 买单匹配
                while (qty > 0 && !sellBook.isEmpty() && sellBook.peek().price <= price) {
                    Order sell = sellBook.peek();
                    int exec = Math.min(qty, sell.qty);
                    qty -= exec;
                    sell.qty -= exec;
                    totalExecuted += exec;

                    if (sell.qty == 0) {
                        sellBook.poll();
                    }
                }
                if (qty > 0) {
                    buyBook.offer(new Order(price, qty));
                }
            } else { // sell
                // 卖单匹配
                while (qty > 0 && !buyBook.isEmpty() && buyBook.peek().price >= price) {
                    Order buy = buyBook.peek();
                    int exec = Math.min(qty, buy.qty);
                    qty -= exec;
                    buy.qty -= exec;
                    totalExecuted += exec;

                    if (buy.qty == 0) {
                        buyBook.poll();
                    }
                }
                if (qty > 0) {
                    sellBook.offer(new Order(price, qty));
                }
            }
        }

        return totalExecuted;
    }

    public static void main(String[] args) {
        List<String[]> orders = Arrays.asList(
            new String[]{"150", "5", "buy"},  // A
            new String[]{"190", "1", "sell"}, // B
            new String[]{"200", "1", "sell"}, // C
            new String[]{"100", "9", "buy"},  // D
            new String[]{"140", "8", "sell"}, // E
            new String[]{"210", "4", "buy"}   // F
        );
        System.out.println(matchOrders(orders)); // 输出 9
    }
}

