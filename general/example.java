import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Modern Airline Reservation System
 * 
 * This system provides a complete airline reservation solution with:
 * - Flight search capabilities
 * - Seat availability viewing
 * - Seat reservation with payment processing
 * - Purchase confirmation
 * - Cancellation functionality
 */
public class example {
    
    public static void main(String[] args) {
        AirlineReservationSystem system = new AirlineReservationSystem();
        system.initializeSampleFlights();
        
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== Modern Airline Reservation System ===");
        System.out.println("Welcome! Please select an option:");
        
        while (true) {
            System.out.println("\n1. Search Flights");
            System.out.println("2. View Available Seats");
            System.out.println("3. Reserve Seats");
            System.out.println("4. Purchase Reserved Seats");
            System.out.println("5. Cancel Purchase");
            System.out.println("6. View All Flights");
            System.out.println("7. Exit");
            System.out.print("Enter your choice (1-7): ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1:
                        searchFlights(system, scanner);
                        break;
                    case 2:
                        viewAvailableSeats(system, scanner);
                        break;
                    case 3:
                        reserveSeats(system, scanner);
                        break;
                    case 4:
                        purchaseSeats(system, scanner);
                        break;
                    case 5:
                        cancelPurchase(system, scanner);
                        break;
                    case 6:
                        system.displayAllFlights();
                        break;
                    case 7:
                        System.out.println("Thank you for using our system!");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }
    
    private static void searchFlights(AirlineReservationSystem system, Scanner scanner) {
        System.out.println("\n=== Flight Search ===");
        System.out.print("Enter departure city (or press Enter to skip): ");
        String departureCity = scanner.nextLine().trim();
        
        System.out.print("Enter arrival city (or press Enter to skip): ");
        String arrivalCity = scanner.nextLine().trim();
        
        System.out.print("Enter departure date (YYYY-MM-DD) (or press Enter to skip): ");
        String dateStr = scanner.nextLine().trim();
        
        List<Flight> results = system.searchFlights(departureCity, arrivalCity, dateStr);
        
        if (results.isEmpty()) {
            System.out.println("No flights found matching your criteria.");
        } else {
            System.out.println("\nFound " + results.size() + " flight(s):");
            results.forEach(System.out::println);
        }
    }
    
    private static void viewAvailableSeats(AirlineReservationSystem system, Scanner scanner) {
        System.out.println("\n=== View Available Seats ===");
        system.displayAllFlights();
        
        System.out.print("Enter flight ID: ");
        String flightId = scanner.nextLine().trim();
        
        system.displayAvailableSeats(flightId);
    }
    
    private static void reserveSeats(AirlineReservationSystem system, Scanner scanner) {
        System.out.println("\n=== Reserve Seats ===");
        system.displayAllFlights();
        
        System.out.print("Enter flight ID: ");
        String flightId = scanner.nextLine().trim();
        
        System.out.print("Enter number of seats to reserve: ");
        int numSeats = Integer.parseInt(scanner.nextLine().trim());
        
        System.out.print("Enter passenger name: ");
        String passengerName = scanner.nextLine().trim();
        
        Reservation reservation = system.reserveSeats(flightId, numSeats, passengerName);
        if (reservation != null) {
            System.out.println("Reservation successful! Reservation ID: " + reservation.getReservationId());
            System.out.println("Please complete payment within 15 minutes to confirm your booking.");
        }
    }
    
    private static void purchaseSeats(AirlineReservationSystem system, Scanner scanner) {
        System.out.println("\n=== Purchase Reserved Seats ===");
        System.out.print("Enter reservation ID: ");
        String reservationId = scanner.nextLine().trim();
        
        System.out.print("Enter payment method (credit/debit): ");
        String paymentMethod = scanner.nextLine().trim();
        
        boolean success = system.purchaseSeats(reservationId, paymentMethod);
        if (success) {
            System.out.println("Purchase successful! Your seats are confirmed.");
        } else {
            System.out.println("Purchase failed. Please check your reservation ID and try again.");
        }
    }
    
    private static void cancelPurchase(AirlineReservationSystem system, Scanner scanner) {
        System.out.println("\n=== Cancel Purchase ===");
        System.out.print("Enter reservation ID: ");
        String reservationId = scanner.nextLine().trim();
        
        boolean success = system.cancelPurchase(reservationId);
        if (success) {
            System.out.println("Cancellation successful! Your refund will be processed.");
        } else {
            System.out.println("Cancellation failed. Please check your reservation ID and try again.");
        }
    }
}

/**
 * Represents a flight in the system
 */
class Flight {
    private String flightId;
    private String departureCity;
    private String arrivalCity;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Aircraft aircraft;
    
    public Flight(String flightId, String departureCity, String arrivalCity, 
                  LocalDateTime departureTime, LocalDateTime arrivalTime) {
        this.flightId = flightId;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.aircraft = new Aircraft(24, 6); // 24 rows, 6 seats per row
    }
    
    // Getters
    public String getFlightId() { return flightId; }
    public String getDepartureCity() { return departureCity; }
    public String getArrivalCity() { return arrivalCity; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public Aircraft getAircraft() { return aircraft; }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("Flight %s: %s → %s | Depart: %s | Arrive: %s | Available Seats: %d",
                flightId, departureCity, arrivalCity, 
                departureTime.format(formatter), arrivalTime.format(formatter),
                aircraft.getAvailableSeats());
    }
}

/**
 * Represents the aircraft with seat configuration
 */
class Aircraft {
    private int rows;
    private int seatsPerRow;
    private Map<String, Seat> seats;
    
    public Aircraft(int rows, int seatsPerRow) {
        this.rows = rows;
        this.seatsPerRow = seatsPerRow;
        this.seats = new ConcurrentHashMap<>();
        initializeSeats();
    }
    
    private void initializeSeats() {
        String[] seatLetters = {"A", "B", "C", "D", "E", "F"};
        for (int row = 1; row <= rows; row++) {
            for (int col = 0; col < seatsPerRow; col++) {
                String seatId = row + seatLetters[col];
                seats.put(seatId, new Seat(seatId, row, seatLetters[col]));
            }
        }
    }
    
    public int getAvailableSeats() {
        return (int) seats.values().stream()
                .filter(seat -> seat.getStatus() == SeatStatus.AVAILABLE)
                .count();
    }
    
    public List<Seat> getAvailableSeatsList() {
        return seats.values().stream()
                .filter(seat -> seat.getStatus() == SeatStatus.AVAILABLE)
                .collect(Collectors.toList());
    }
    
    public boolean reserveSeats(int numSeats, String reservationId) {
        List<Seat> availableSeats = getAvailableSeatsList();
        if (availableSeats.size() < numSeats) {
            return false;
        }
        
        for (int i = 0; i < numSeats; i++) {
            availableSeats.get(i).reserve(reservationId);
        }
        return true;
    }
    
    public void displaySeatMap() {
        System.out.println("\nSeat Map (X = Occupied, O = Available, R = Reserved):");
        System.out.println("   A B C   D E F");
        
        for (int row = 1; row <= rows; row++) {
            System.out.printf("%2d ", row);
            for (int col = 0; col < seatsPerRow; col++) {
                String seatId = String.valueOf(row) + (col < 3 ? "ABC".charAt(col) : "DEF".charAt(col - 3));
                Seat seat = seats.get(seatId);
                char status = seat.getStatus() == SeatStatus.AVAILABLE ? 'O' : 
                             seat.getStatus() == SeatStatus.RESERVED ? 'R' : 'X';
                System.out.print(status + " ");
                if (col == 2) System.out.print("  ");
            }
            System.out.println();
        }
    }
    
    public void releaseSeats(String reservationId) {
        seats.values().stream()
                .filter(seat -> reservationId.equals(seat.getReservationId()))
                .forEach(Seat::release);
    }
    
    public void confirmSeats(String reservationId) {
        seats.values().stream()
                .filter(seat -> reservationId.equals(seat.getReservationId()))
                .forEach(Seat::confirm);
    }
}

/**
 * Represents a seat on the aircraft
 */
class Seat {
    private String seatId;
    private int row;
    private String column;
    private SeatStatus status;
    private String reservationId;
    private String passengerName;
    
    public Seat(String seatId, int row, String column) {
        this.seatId = seatId;
        this.row = row;
        this.column = column;
        this.status = SeatStatus.AVAILABLE;
    }
    
    public void reserve(String reservationId) {
        this.status = SeatStatus.RESERVED;
        this.reservationId = reservationId;
    }
    
    public void confirm() {
        this.status = SeatStatus.OCCUPIED;
    }
    
    public void release() {
        this.status = SeatStatus.AVAILABLE;
        this.reservationId = null;
        this.passengerName = null;
    }
    
    // Getters
    public String getSeatId() { return seatId; }
    public SeatStatus getStatus() { return status; }
    public String getReservationId() { return reservationId; }
    public String getPassengerName() { return passengerName; }
    
    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }
}

/**
 * Enum for seat status
 */
enum SeatStatus {
    AVAILABLE, RESERVED, OCCUPIED
}

/**
 * Represents a reservation
 */
class Reservation {
    private String reservationId;
    private String flightId;
    private String passengerName;
    private int numSeats;
    private LocalDateTime reservationTime;
    private ReservationStatus status;
    
    public Reservation(String reservationId, String flightId, String passengerName, int numSeats) {
        this.reservationId = reservationId;
        this.flightId = flightId;
        this.passengerName = passengerName;
        this.numSeats = numSeats;
        this.reservationTime = LocalDateTime.now();
        this.status = ReservationStatus.RESERVED;
    }
    
    // Getters
    public String getReservationId() { return reservationId; }
    public String getFlightId() { return flightId; }
    public String getPassengerName() { return passengerName; }
    public int getNumSeats() { return numSeats; }
    public LocalDateTime getReservationTime() { return reservationTime; }
    public ReservationStatus getStatus() { return status; }
    
    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }
    
    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(reservationTime.plusMinutes(15));
    }
}

/**
 * Enum for reservation status
 */
enum ReservationStatus {
    RESERVED, CONFIRMED, CANCELLED
}

/**
 * Main airline reservation system class
 */
class AirlineReservationSystem {
    private Map<String, Flight> flights;
    private Map<String, Reservation> reservations;
    private PaymentProcessor paymentProcessor;
    
    public AirlineReservationSystem() {
        this.flights = new ConcurrentHashMap<>();
        this.reservations = new ConcurrentHashMap<>();
        this.paymentProcessor = new PaymentProcessor();
    }
    
    public void initializeSampleFlights() {
        // Sample flights for 2025-03-01
        LocalDateTime baseDate = LocalDateTime.of(2025, 3, 1, 0, 0);
        
        addFlight("SF001", "San Francisco", "Portland", 
                 baseDate.withHour(8).withMinute(45), 
                 baseDate.withHour(10).withMinute(5));
        
        addFlight("SF002", "San Francisco", "Seattle", 
                 baseDate.withHour(10).withMinute(30), 
                 baseDate.withHour(12).withMinute(15));
        
        addFlight("SF003", "Portland", "San Francisco", 
                 baseDate.withHour(14).withMinute(0), 
                 baseDate.withHour(15).withMinute(20));
        
        addFlight("SF004", "Seattle", "San Francisco", 
                 baseDate.withHour(16).withMinute(30), 
                 baseDate.withHour(18).withMinute(45));
        
        addFlight("SF005", "San Francisco", "Los Angeles", 
                 baseDate.withHour(9).withMinute(15), 
                 baseDate.withHour(10).withMinute(45));
    }
    
    private void addFlight(String flightId, String departureCity, String arrivalCity, 
                          LocalDateTime departureTime, LocalDateTime arrivalTime) {
        flights.put(flightId, new Flight(flightId, departureCity, arrivalCity, departureTime, arrivalTime));
    }
    
    public List<Flight> searchFlights(String departureCity, String arrivalCity, String dateStr) {
        return flights.values().stream()
                .filter(flight -> (departureCity.isEmpty() || 
                                 flight.getDepartureCity().equalsIgnoreCase(departureCity)))
                .filter(flight -> (arrivalCity.isEmpty() || 
                                 flight.getArrivalCity().equalsIgnoreCase(arrivalCity)))
                .filter(flight -> (dateStr.isEmpty() || 
                                 flight.getDepartureTime().toLocalDate().toString().equals(dateStr)))
                .collect(Collectors.toList());
    }
    
    public void displayAllFlights() {
        System.out.println("\n=== Available Flights ===");
        if (flights.isEmpty()) {
            System.out.println("No flights available.");
            return;
        }
        
        flights.values().forEach(System.out::println);
    }
    
    public void displayAvailableSeats(String flightId) {
        Flight flight = flights.get(flightId);
        if (flight == null) {
            System.out.println("Flight not found.");
            return;
        }
        
        System.out.println("\n=== Available Seats for Flight " + flightId + " ===");
        System.out.println("Available seats: " + flight.getAircraft().getAvailableSeats());
        flight.getAircraft().displaySeatMap();
    }
    
    public Reservation reserveSeats(String flightId, int numSeats, String passengerName) {
        Flight flight = flights.get(flightId);
        if (flight == null) {
            System.out.println("Flight not found.");
            return null;
        }
        
        if (flight.getAircraft().getAvailableSeats() < numSeats) {
            System.out.println("Not enough available seats.");
            return null;
        }
        
        String reservationId = generateReservationId();
        boolean reserved = flight.getAircraft().reserveSeats(numSeats, reservationId);
        
        if (reserved) {
            Reservation reservation = new Reservation(reservationId, flightId, passengerName, numSeats);
            reservations.put(reservationId, reservation);
            return reservation;
        }
        
        return null;
    }
    
    public boolean purchaseSeats(String reservationId, String paymentMethod) {
        Reservation reservation = reservations.get(reservationId);
        if (reservation == null) {
            System.out.println("Reservation not found.");
            return false;
        }
        
        if (reservation.isExpired()) {
            System.out.println("Reservation has expired.");
            return false;
        }
        
        if (reservation.getStatus() != ReservationStatus.RESERVED) {
            System.out.println("Reservation is not in valid state for purchase.");
            return false;
        }
        
        // Process payment
        boolean paymentSuccess = paymentProcessor.processPayment(paymentMethod, reservation.getNumSeats() * 150.0);
        
        if (paymentSuccess) {
            Flight flight = flights.get(reservation.getFlightId());
            flight.getAircraft().confirmSeats(reservationId);
            reservation.confirm();
            System.out.println("Payment processed successfully. Total: $" + (reservation.getNumSeats() * 150.0));
            return true;
        } else {
            System.out.println("Payment failed.");
            return false;
        }
    }
    
    public boolean cancelPurchase(String reservationId) {
        Reservation reservation = reservations.get(reservationId);
        if (reservation == null) {
            System.out.println("Reservation not found.");
            return false;
        }
        
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            System.out.println("Only confirmed reservations can be cancelled.");
            return false;
        }
        
        Flight flight = flights.get(reservation.getFlightId());
        flight.getAircraft().releaseSeats(reservationId);
        reservation.cancel();
        
        // Process refund
        double refundAmount = reservation.getNumSeats() * 150.0 * 0.8; // 80% refund
        System.out.println("Refund processed: $" + refundAmount);
        
        return true;
    }
    
    private String generateReservationId() {
        return "RES" + System.currentTimeMillis() % 100000;
    }
}

/**
 * Payment processor stub
 */
class PaymentProcessor {
    public boolean processPayment(String paymentMethod, double amount) {
        // Simulate payment processing
        System.out.println("Processing payment of $" + amount + " via " + paymentMethod + "...");
        
        // Simulate some payment failures (10% failure rate)
        if (Math.random() < 0.1) {
            System.out.println("Payment failed due to insufficient funds.");
            return false;
        }
        
        System.out.println("Payment successful!");
        return true;
    }
}
