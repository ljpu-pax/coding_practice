package lpu.doordash;

// Import statements
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

// Main application class
public class BootstrapApplication {

    // Data Models
    static class Address {
        private String street;
        private String city;

        public Address(String street, String city) {
            this.street = street;
            this.city = city;
        }

        // Getters
        public String getStreet() { return street; }
        public String getCity() { return city; }

        // toString for display purposes
        @Override
        public String toString() {
            return "Address{street='" + street + "', city='" + city + "'}";
        }
    }

    static class Payment {
        private String cardNumber;
        private String expiryDate;

        public Payment(String cardNumber, String expiryDate) {
            this.cardNumber = cardNumber;
            this.expiryDate = expiryDate;
        }

        // Getters
        public String getCardNumber() { return cardNumber; }
        public String getExpiryDate() { return expiryDate; }

        // toString for display purposes
        @Override
        public String toString() {
            return "Payment{cardNumber='" + cardNumber + "', expiryDate='" + expiryDate + "'}";
        }
    }

    // Simulated AddressService
    static class AddressService {
        public Address getAddress(String userId) throws Exception {
            if ("failAddress".equals(userId)) {
                throw new Exception("Address service failure");
            }
            return new Address("123 Main St", "Anytown");
        }
    }

    // Simulated PaymentService
    static class PaymentService {
        public Payment getPayment(String userId) throws Exception {
            if ("failPayment".equals(userId)) {
                throw new Exception("Payment service failure");
            }
            return new Payment("4111111111111111", "12/25");
        }
    }

    // Controller logic
    static class BootstrapController {
        private AddressService addressService;
        private PaymentService paymentService;

        public BootstrapController(AddressService addressService, PaymentService paymentService) {
            this.addressService = addressService;
            this.paymentService = paymentService;
        }

        public Map<String, Object> getUserData(String userId) {
            ExecutorService executor = Executors.newFixedThreadPool(2);
            Future<Address> addressFuture = executor.submit(() -> {
                try {
                    return addressService.getAddress(userId);
                } catch (Exception e) {
                    return null;
                }
            });

            Future<Payment> paymentFuture = executor.submit(() -> {
                try {
                    return paymentService.getPayment(userId);
                } catch (Exception e) {
                    return null;
                }
            });

            Map<String, Object> response = new HashMap<>();
            List<String> errors = new ArrayList<>();

            try {
                Address address = addressFuture.get(2, TimeUnit.SECONDS);
                if (address != null) {
                    response.put("address", address);
                } else {
                    errors.add("Failed to retrieve address.");
                }
            } catch (Exception e) {
                errors.add("Address service timeout.");
            }

            try {
                Payment payment = paymentFuture.get(2, TimeUnit.SECONDS);
                if (payment != null) {
                    response.put("payment", payment);
                } else {
                    errors.add("Failed to retrieve payment information.");
                }
            } catch (Exception e) {
                errors.add("Payment service timeout.");
            }

            executor.shutdown();

            if (response.isEmpty()) {
                response.put("errors", errors);
            } else if (!errors.isEmpty()) {
                response.put("errors", errors);
            }

            return response;
        }
    }

    // Main method for demonstration
    public static void main(String[] args) {
        AddressService addressService = new AddressService();
        PaymentService paymentService = new PaymentService();
        BootstrapController controller = new BootstrapController(addressService, paymentService);

        // Example usage
        Map<String, Object> result = controller.getUserData("123");
        System.out.println("Result: " + result);
    }

    // Embedded unit tests
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    public static class BootstrapApplicationTests {

        private BootstrapController controller;

        @BeforeAll
        public void setup() {
            AddressService addressService = new AddressService();
            PaymentService paymentService = new PaymentService();
            controller = new BootstrapController(addressService, paymentService);
        }

        @Test
        public void testSuccessfulResponse() {
            Map<String, Object> result = controller.getUserData("123");
            assertTrue(result.containsKey("address"));
            assertTrue(result.containsKey("payment"));
            assertFalse(result.containsKey("errors"));
        }

        @Test
        public void testAddressServiceFailure() {
            Map<String, Object> result = controller.getUserData("failAddress");
            assertFalse(result.containsKey("address"));
            assertTrue(result.containsKey("payment"));
            assertTrue(result.containsKey("errors"));
        }

        @Test
        public void testPaymentServiceFailure() {
            Map<String, Object> result = controller.getUserData("failPayment");
            assertTrue(result.containsKey("address"));
            assertFalse(result.containsKey("payment"));
            assertTrue(result.containsKey("errors"));
        }

        @Test
        public void testBothServicesFailure() {
            Map<String, Object> result = controller.getUserData("failBoth");
            assertFalse(result.containsKey("address"));
            assertFalse(result.containsKey("payment"));
            assertTrue(result.containsKey("errors"));
        }
    }
}
