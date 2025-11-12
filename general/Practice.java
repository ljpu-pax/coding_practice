import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

enum OrderStatus {
    PICKED_UP, DELIVERED, CANCELLED
}

class OrderEvent {
    String dasherId;
    String deliveryId;
    LocalDateTime timestamp;
    OrderStatus status;
    
    public OrderEvent(String dasherId, String deliveryId, LocalDateTime timestamp, OrderStatus orderStatus) {
        this.dasherId = dasherId;
        this.deliveryId = deliveryId;
        this.timestamp = timestamp;
        this.status = orderStatus;
    }
}

class DasherPaymentCalculator {
    private final double ratePerMinute;

    public DasherPaymentCalculator(double ratePerMinute) {
        this.ratePerMinute = ratePerMinute;
    }

    public double computePayment(String dasherId, List<OrderEvent> events) {
        Map<String, LocalDateTime> pickupTimes = new HashMap<>();
        Map<String, LocalDateTime> deliveryTimes = new HashMap<>();
        Set<String> cancelled = new HashSet<>();

        for (OrderEvent e : events) {
            if (!e.dasherId.equals(dasherId)) continue;

            switch (e.status) {
                case PICKED_UP:
                    pickupTimes.put(e.dasherId, e.timestamp);
                    break;
                case DELIVERED:
                    deliveryTimes.put(e.dasherId, e.timestamp);
                    break;
                case CANCELLED:
                    cancelled.add(e.deliveryId);
                    break;
            }
        }

        double totalPay = 0.0;
        for (String deliveryId : pickupTimes.keySet()) {
            if (cancelled.contains(deliveryId)) continue;
            if (!deliveryTimes.containsKey(deliveryId)) continue;

            LocalDateTime start = pickupTimes.get(deliveryId);
            LocalDateTime end = deliveryTimes.get(deliveryId);
            long minutes = Duration.between(start, end).toMinutes();
            totalPay += minutes * ratePerMinute;
        }

        return totalPay;
    }
    
}

public class Practice {
    public static void main(String[] args) {
        List<OrderEvent> logs = new ArrayList<>();
        String dasherId = "dasher123";

        logs.add(new OrderEvent(dasherId, "1", LocalDateTime.of(2024, 5, 1, 12, 0), OrderStatus.PICKED_UP));
        logs.add(new OrderEvent(dasherId, "1", LocalDateTime.of(2024, 5, 1, 12, 30), OrderStatus.DELIVERED));

        logs.add(new OrderEvent(dasherId, "2", LocalDateTime.of(2024, 5, 1, 13, 0), OrderStatus.PICKED_UP));
        logs.add(new OrderEvent(dasherId, "2", LocalDateTime.of(2024, 5, 1, 13, 45), OrderStatus.DELIVERED));

        logs.add(new OrderEvent(dasherId, "3", LocalDateTime.of(2024, 5, 1, 14, 0), OrderStatus.PICKED_UP));
        logs.add(new OrderEvent(dasherId, "3", LocalDateTime.of(2024, 5, 1, 14, 10), OrderStatus.CANCELLED));

        DasherPaymentCalculator calc = new DasherPaymentCalculator(0.5);
        double pay = calc.computePayment(dasherId, logs);
        System.out.println("Total payment: $" + pay); // Should compute for orders 1 and 2 only
    }
}
