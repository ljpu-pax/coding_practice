package sig;

import java.util.*;

// Main class
public class CashRegisterSystem {

    // === Abstract Item ===
    public static abstract class Item {
        protected String name;
        protected String sku;

        public Item(String name, String sku) {
            this.name = name;
            this.sku = sku;
        }

        public String getName() { return name; }
        public String getSku() { return sku; }
        public abstract double getPrice(double quantity);
    }

    // === Standard Item ===
    public static class StandardItem extends Item {
        private double unitPrice;

        public StandardItem(String name, String sku, double unitPrice) {
            super(name, sku);
            this.unitPrice = unitPrice;
        }

        @Override
        public double getPrice(double quantity) {
            return unitPrice * quantity;
        }
    }

    // === Produce Item ===
    public static class ProduceItem extends Item {
        private double pricePerKg;

        public ProduceItem(String name, String sku, double pricePerKg) {
            super(name, sku);
            this.pricePerKg = pricePerKg;
        }

        @Override
        public double getPrice(double quantity) {
            return pricePerKg * quantity;
        }
    }

    // === Coupon ===
    public static class Coupon {
        private String sku;
        private double discountAmount;

        public Coupon(String sku, double discountAmount) {
            this.sku = sku;
            this.discountAmount = discountAmount;
        }

        public String getSku() { return sku; }
        public double getDiscountAmount() { return discountAmount; }
    }

    // === LineItem ===
    public static class LineItem {
        private Item item;
        private double quantity;
        private double subtotal;

        public LineItem(Item item, double quantity) {
            this.item = item;
            this.quantity = quantity;
            this.subtotal = item.getPrice(quantity);
        }

        public Item getItem() { return item; }
        public double getQuantity() { return quantity; }
        public double getSubtotal() { return subtotal; }

        public void applyDiscount(double discount) {
            this.subtotal -= discount;
        }
    }

    // === CashRegister Interface ===
    public interface CashRegister {
        void addItem(Item item, double quantity);
        void applyCoupon(Coupon coupon);
        double calculateTotal();
        void printReceipt();
    }

    // === Implementation ===
    public static class CashRegisterImpl implements CashRegister {
        private List<LineItem> items = new ArrayList<>();
        private List<Coupon> coupons = new ArrayList<>();

        @Override
        public void addItem(Item item, double quantity) {
            items.add(new LineItem(item, quantity));
        }

        @Override
        public void applyCoupon(Coupon coupon) {
            coupons.add(coupon);
        }

        @Override
        public double calculateTotal() {
            // Apply coupons
            for (Coupon coupon : coupons) {
                for (LineItem lineItem : items) {
                    if (lineItem.getItem().getSku().equals(coupon.getSku())) {
                        lineItem.applyDiscount(coupon.getDiscountAmount());
                    }
                }
            }

            double total = 0;
            for (LineItem lineItem : items) {
                total += lineItem.getSubtotal();
            }
            return total;
        }

        @Override
        public void printReceipt() {
            System.out.println("=== Receipt ===");
            for (LineItem line : items) {
                System.out.printf("%s x %.2f = $%.2f\n", line.getItem().getName(), line.getQuantity(), line.getSubtotal());
            }
            System.out.println("------------------");
            System.out.printf("Total: $%.2f\n", calculateTotal());
        }
    }

    // === Main for Demo ===
    public static void main(String[] args) {
        CashRegister register = new CashRegisterImpl();

        Item banana = new ProduceItem("Banana", "BAN123", 1.5);
        Item shampoo = new StandardItem("Shampoo", "SHA999", 5.0);
        Item apple = new ProduceItem("Apple", "APP222", 3.0);

        register.addItem(banana, 2.0);   // 2 kg
        register.addItem(shampoo, 1);   // 1 unit
        register.addItem(apple, 1.5);   // 1.5 kg

        Coupon coupon = new Coupon("SHA999", 1.0); // $1 off shampoo
        register.applyCoupon(coupon);

        register.printReceipt();
    }
}

