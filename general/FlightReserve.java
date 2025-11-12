// A prominent airline just suffered a weather related outage, their legacy system
// had some trouble dealing with things.  Lucky for you, you just started a
// company to make modernized airline reservation software.  So, your job is to
// create a new airline reservation system.  

// Use any language and tooling of your choice, including LLMs, so we can get an
// idea of how you usually work.

// The system should be invocable for testing (e.g. via CLI), and must implement
// these operations:
// - Search for a flight, by any of: departing city, arriving city, flight times.
// - For a specific flight, view available seats.
// - For a specific flight, reserve some number of seats until the customer
// finishes payment.
// - Purchase reserved seat(s).  Assume a stub for now that we could fill in later.
// - Cancel a previously purchased seat(s).

// You can assume a fixed plane dimension, such as 24 rows with 6 seats each.

// You can also assume a fixed list of flights that you can create, for example:
// 2025-03-01 San Francisco -> Portland,
// Departing at 8:45 am, arriving at 10:05 am

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

public class FlightReserve {
    
    // ===== Core Models =====
    
    public static class Flight {
        private String flightNumber;
        private String departureCity;
        private String arrivalCity;
        private LocalDate departureDate;
        private LocalTime departureTime;
        private LocalTime arrivalTime;
        private String airline;
        private FlightStatus status;
        private Map<String, Seat> seats;
        
        public Flight(String flightNumber, String departureCity, String arrivalCity, 
                     LocalDate departureDate, LocalTime departureTime, LocalTime arrivalTime, String airline) {
            this.flightNumber = flightNumber;
            this.departureCity = departureCity;
            this.arrivalCity = arrivalCity;
            this.departureDate = departureDate;
            this.departureTime = departureTime;
            this.arrivalTime = arrivalTime;
            this.airline = airline;
            this.status = FlightStatus.SCHEDULED;
            this.seats = new HashMap<>();
            initializeSeats();
        }
        
        private void initializeSeats() {
            // Fixed plane dimensions: 24 rows with 6 seats each (A, B, C, D, E, F)
            for (int row = 1; row <= 24; row++) {
                for (char col = 'A'; col <= 'F'; col++) {
                    String seatId = row + String.valueOf(col);
                    seats.put(seatId, new Seat(seatId, row, col));
                }
            }
        }
        
        // Getters
        public String getFlightNumber() { return flightNumber; }
        public String getDepartureCity() { return departureCity; }
        public String getArrivalCity() { return arrivalCity; }
        public LocalDate getDepartureDate() { return departureDate; }
        public LocalTime getDepartureTime() { return departureTime; }
        public LocalTime getArrivalTime() { return arrivalTime; }
        public String getAirline() { return airline; }
        public FlightStatus getStatus() { return status; }
        public Map<String, Seat> getSeats() { return seats; }
        
        public void setStatus(FlightStatus status) { this.status = status; }
        
        @Override
        public String toString() {
            return String.format("%s: %s → %s on %s at %s (Arrives: %s)", 
                flightNumber, departureCity, arrivalCity, departureDate, departureTime, arrivalTime);
        }
    }
    
    public static class Seat {
        private String seatId;
        private int row;
        private char column;
        private SeatStatus status;
        private String passengerName;
        private double price;
        
        public Seat(String seatId, int row, char column) {
            this.seatId = seatId;
            this.row = row;
            this.column = column;
            this.status = SeatStatus.AVAILABLE;
            this.price = calculateBasePrice();
        }
        
        private double calculateBasePrice() {
            // Premium seats (first 3 rows, window seats A and F)
            if (row <= 3 || column == 'A' || column == 'F') {
                return 150.0 + (row * 5.0);
            }
            // Economy seats
            return 100.0 + (row * 2.0);
        }
        
        // Getters and setters
        public String getSeatId() { return seatId; }
        public int getRow() { return row; }
        public char getColumn() { return column; }
        public SeatStatus getStatus() { return status; }
        public String getPassengerName() { return passengerName; }
        public double getPrice() { return price; }
        
        public void setStatus(SeatStatus status) { this.status = status; }
        public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
        
        public boolean isWindowSeat() {
            return column == 'A' || column == 'F';
        }
        
        public boolean isAisleSeat() {
            return column == 'C' || column == 'D';
        }
        
        @Override
        public String toString() {
            return String.format("Seat %s (Row %d, %s) - %s - $%.2f", 
                seatId, row, column, status, price);
        }
    }
    
    public static class Reservation {
        private String reservationId;
        private String flightNumber;
        private List<String> seatIds;
        private String customerName;
        private String customerEmail;
        private ReservationStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime expiresAt;
        private double totalPrice;
        
        public Reservation(String reservationId, String flightNumber, List<String> seatIds, 
                         String customerName, String customerEmail) {
            this.reservationId = reservationId;
            this.flightNumber = flightNumber;
            this.seatIds = new ArrayList<>(seatIds);
            this.customerName = customerName;
            this.customerEmail = customerEmail;
            this.status = ReservationStatus.PENDING;
            this.createdAt = LocalDateTime.now();
            this.expiresAt = LocalDateTime.now().plusMinutes(15); // 15 minutes to complete payment
        }
        
        // Getters and setters
        public String getReservationId() { return reservationId; }
        public String getFlightNumber() { return flightNumber; }
        public List<String> getSeatIds() { return seatIds; }
        public String getCustomerName() { return customerName; }
        public String getCustomerEmail() { return customerEmail; }
        public ReservationStatus getStatus() { return status; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getExpiresAt() { return expiresAt; }
        public double getTotalPrice() { return totalPrice; }
        
        public void setStatus(ReservationStatus status) { this.status = status; }
        public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
        
        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiresAt);
        }
    }
    
    public static class Ticket {
        private String ticketId;
        private String reservationId;
        private String flightNumber;
        private String seatId;
        private String passengerName;
        private LocalDateTime issuedAt;
        private double price;
        
        public Ticket(String ticketId, String reservationId, String flightNumber, 
                     String seatId, String passengerName, double price) {
            this.ticketId = ticketId;
            this.reservationId = reservationId;
            this.flightNumber = flightNumber;
            this.seatId = seatId;
            this.passengerName = passengerName;
            this.issuedAt = LocalDateTime.now();
            this.price = price;
        }
        
        // Getters
        public String getTicketId() { return ticketId; }
        public String getReservationId() { return reservationId; }
        public String getFlightNumber() { return flightNumber; }
        public String getSeatId() { return seatId; }
        public String getPassengerName() { return passengerName; }
        public LocalDateTime getIssuedAt() { return issuedAt; }
        public double getPrice() { return price; }
    }
    
    // ===== Enums =====
    
    public enum FlightStatus {
        SCHEDULED, BOARDING, DEPARTED, ARRIVED, CANCELLED, DELAYED
    }
    
    public enum SeatStatus {
        AVAILABLE, RESERVED, OCCUPIED
    }
    
    public enum ReservationStatus {
        PENDING, CONFIRMED, CANCELLED, EXPIRED
    }
    
    // ===== Search Service =====
    
    public static class FlightSearchService {
        private List<Flight> flights;
        
        public FlightSearchService() {
            this.flights = new ArrayList<>();
        }
        
        public void addFlight(Flight flight) {
            flights.add(flight);
        }
        
        /**
         * Search for flights with flexible criteria including ranges
         * Any parameter can be null to skip that filter
         */
        public List<Flight> searchFlights(String departureCity, String arrivalCity, 
                                        LocalDate departureDate, LocalTime departureTime) {
            return searchFlights(departureCity, arrivalCity, departureDate, departureDate, 
                               departureTime, departureTime, null, null);
        }
        
        /**
         * Advanced search with date and time ranges
         */
        public List<Flight> searchFlights(String departureCity, String arrivalCity, 
                                        LocalDate departureDate, LocalDate departureDateEnd,
                                        LocalTime departureTime, LocalTime departureTimeEnd) {
            return searchFlights(departureCity, arrivalCity, departureDate, departureDateEnd, 
                               departureTime, departureTimeEnd, null, null);
        }
        
        /**
         * Full search with all range options
         */
        public List<Flight> searchFlights(String departureCity, String arrivalCity, 
                                        LocalDate departureDate, LocalDate departureDateEnd,
                                        LocalTime departureTime, LocalTime departureTimeEnd,
                                        LocalDate returnDate, LocalDate returnDateEnd) {
            return flights.stream()
                .filter(flight -> flight.getStatus() != FlightStatus.CANCELLED)
                .filter(flight -> departureCity == null || 
                    flight.getDepartureCity().equalsIgnoreCase(departureCity))
                .filter(flight -> arrivalCity == null || 
                    flight.getArrivalCity().equalsIgnoreCase(arrivalCity))
                .filter(flight -> departureDate == null || 
                    !flight.getDepartureDate().isBefore(departureDate))
                .filter(flight -> departureDateEnd == null || 
                    !flight.getDepartureDate().isAfter(departureDateEnd))
                .filter(flight -> departureTime == null || 
                    !flight.getDepartureTime().isBefore(departureTime))
                .filter(flight -> departureTimeEnd == null || 
                    !flight.getDepartureTime().isAfter(departureTimeEnd))
                .sorted((f1, f2) -> {
                    // Sort by departure date first, then by departure time
                    int dateCompare = f1.getDepartureDate().compareTo(f2.getDepartureDate());
                    if (dateCompare != 0) return dateCompare;
                    return f1.getDepartureTime().compareTo(f2.getDepartureTime());
                })
                .collect(Collectors.toList());
        }
        
        /**
         * Search by departure city only
         */
        public List<Flight> searchByDepartureCity(String departureCity) {
            return searchFlights(departureCity, null, null, null, null, null, null, null);
        }
        
        /**
         * Search by arrival city only
         */
        public List<Flight> searchByArrivalCity(String arrivalCity) {
            return searchFlights(null, arrivalCity, null, null, null, null, null, null);
        }
        
        /**
         * Search by departure date range
         */
        public List<Flight> searchByDepartureDateRange(LocalDate startDate, LocalDate endDate) {
            return searchFlights(null, null, startDate, endDate, null, null, null, null);
        }
        
        /**
         * Search by departure time range
         */
        public List<Flight> searchByDepartureTimeRange(LocalTime startTime, LocalTime endTime) {
            return searchFlights(null, null, null, null, startTime, endTime, null, null);
        }
        
        /**
         * Search by city pair (departure and arrival)
         */
        public List<Flight> searchByCityPair(String departureCity, String arrivalCity) {
            return searchFlights(departureCity, arrivalCity, null, null, null, null, null, null);
        }
        
        /**
         * Search by date range and time range combination
         */
        public List<Flight> searchByDateTimeRange(LocalDate startDate, LocalDate endDate, 
                                                LocalTime startTime, LocalTime endTime) {
            return searchFlights(null, null, startDate, endDate, startTime, endTime, null, null);
        }
        
        /**
         * Get all available flights
         */
        public List<Flight> getAllFlights() {
            return new ArrayList<>(flights);
        }
        
        /**
         * Get flight by flight number
         */
        public Flight getFlightByNumber(String flightNumber) {
            return flights.stream()
                .filter(flight -> flight.getFlightNumber().equals(flightNumber))
                .findFirst()
                .orElse(null);
        }
    }
    
    // ===== Seat Management Service =====
    
    public static class SeatManagementService {
        private FlightSearchService flightService;
        
        public SeatManagementService(FlightSearchService flightService) {
            this.flightService = flightService;
        }
        
        /**
         * Get all available seats for a specific flight
         * Fixed plane dimensions: 24 rows with 6 seats each = 144 total seats
         */
        public List<Seat> getAvailableSeats(String flightNumber) {
            Flight flight = flightService.getFlightByNumber(flightNumber);
            if (flight == null) {
                return new ArrayList<>();
            }
            
            return flight.getSeats().values().stream()
                .filter(seat -> seat.getStatus() == SeatStatus.AVAILABLE)
                .sorted((s1, s2) -> {
                    if (s1.getRow() != s2.getRow()) {
                        return Integer.compare(s1.getRow(), s2.getRow());
                    }
                    return Character.compare(s1.getColumn(), s2.getColumn());
                })
                .collect(Collectors.toList());
        }
        
        /**
         * Get seat map showing all seats and their status
         */
        public Map<String, List<Seat>> getSeatMap(String flightNumber) {
            Flight flight = flightService.getFlightByNumber(flightNumber);
            if (flight == null) {
                return new HashMap<>();
            }
            
            Map<String, List<Seat>> seatMap = new HashMap<>();
            for (int row = 1; row <= 24; row++) {
                List<Seat> rowSeats = new ArrayList<>();
                for (char col = 'A'; col <= 'F'; col++) {
                    String seatId = row + String.valueOf(col);
                    Seat seat = flight.getSeats().get(seatId);
                    if (seat != null) {
                        rowSeats.add(seat);
                    }
                }
                seatMap.put("Row " + row, rowSeats);
            }
            return seatMap;
        }
        
        /**
         * Get available seats by preference (window, aisle, etc.)
         */
        public List<Seat> getAvailableSeatsByPreference(String flightNumber, SeatPreference preference) {
            List<Seat> availableSeats = getAvailableSeats(flightNumber);
            
            switch (preference) {
                case WINDOW:
                    return availableSeats.stream()
                        .filter(Seat::isWindowSeat)
                        .collect(Collectors.toList());
                case AISLE:
                    return availableSeats.stream()
                        .filter(Seat::isAisleSeat)
                        .collect(Collectors.toList());
                case PREMIUM:
                    return availableSeats.stream()
                        .filter(seat -> seat.getRow() <= 3)
                        .collect(Collectors.toList());
                case ECONOMY:
                    return availableSeats.stream()
                        .filter(seat -> seat.getRow() > 3)
                        .collect(Collectors.toList());
                default:
                    return availableSeats;
            }
        }
        
        /**
         * Get seat statistics for a flight
         */
        public SeatStatistics getSeatStatistics(String flightNumber) {
            Flight flight = flightService.getFlightByNumber(flightNumber);
            if (flight == null) {
                return new SeatStatistics(0, 0, 0, 0);
            }
            
            Map<String, Seat> seats = flight.getSeats();
            int total = seats.size();
            int available = (int) seats.values().stream()
                .filter(seat -> seat.getStatus() == SeatStatus.AVAILABLE).count();
            int reserved = (int) seats.values().stream()
                .filter(seat -> seat.getStatus() == SeatStatus.RESERVED).count();
            int occupied = (int) seats.values().stream()
                .filter(seat -> seat.getStatus() == SeatStatus.OCCUPIED).count();
            
            return new SeatStatistics(total, available, reserved, occupied);
        }
        
        /**
         * Display visual seat map for a flight
         */
        public void displaySeatMap(String flightNumber) {
            Flight flight = flightService.getFlightByNumber(flightNumber);
            if (flight == null) {
                System.out.println("Flight not found: " + flightNumber);
                return;
            }
            
            System.out.println("Seat Map for Flight " + flightNumber + ":");
            System.out.println("     A     B     C     D     E     F");
            System.out.println("   ┌─────┬─────┬─────┬─────┬─────┬─────┐");
            
            for (int row = 24; row >= 1; row--) {
                System.out.printf("%2d │", row);
                for (char col = 'A'; col <= 'F'; col++) {
                    String seatId = row + String.valueOf(col);
                    Seat seat = flight.getSeats().get(seatId);
                    if (seat != null) {
                        switch (seat.getStatus()) {
                            case AVAILABLE:
                                System.out.print("  ✓  │");
                                break;
                            case RESERVED:
                                System.out.print("  R  │");
                                break;
                            case OCCUPIED:
                                System.out.print("  X  │");
                                break;
                        }
                    }
                }
                System.out.println();
                if (row > 1) {
                    System.out.println("   ├─────┼─────┼─────┼─────┼─────┼─────┤");
                }
            }
            System.out.println("   └─────┴─────┴─────┴─────┴─────┴─────┘");
            System.out.println("Legend: ✓ = Available, R = Reserved, X = Occupied");
        }
    }
    
    public enum SeatPreference {
        ANY, WINDOW, AISLE, PREMIUM, ECONOMY
    }
    
    public static class SeatStatistics {
        private int totalSeats;
        private int availableSeats;
        private int reservedSeats;
        private int occupiedSeats;
        
        public SeatStatistics(int total, int available, int reserved, int occupied) {
            this.totalSeats = total;
            this.availableSeats = available;
            this.reservedSeats = reserved;
            this.occupiedSeats = occupied;
        }
        
        // Getters
        public int getTotalSeats() { return totalSeats; }
        public int getAvailableSeats() { return availableSeats; }
        public int getReservedSeats() { return reservedSeats; }
        public int getOccupiedSeats() { return occupiedSeats; }
        
        public double getOccupancyRate() {
            return totalSeats > 0 ? (double)(occupiedSeats + reservedSeats) / totalSeats * 100 : 0.0;
        }
        
        @Override
        public String toString() {
            return String.format("Total: %d, Available: %d, Reserved: %d, Occupied: %d (%.1f%% occupancy)", 
                totalSeats, availableSeats, reservedSeats, occupiedSeats, getOccupancyRate());
        }
    }
    
    // ===== Reservation Service =====
    
    public static class ReservationService {
        private FlightSearchService flightService;
        private Map<String, Reservation> reservations;
        private Map<String, List<String>> flightReservations; // flightNumber -> reservationIds
        
        public ReservationService(FlightSearchService flightService) {
            this.flightService = flightService;
            this.reservations = new HashMap<>();
            this.flightReservations = new HashMap<>();
        }
        
        /**
         * Reserve seats for a specific flight until customer finishes payment
         */
        public Reservation reserveSeats(String flightNumber, List<String> seatIds, 
                                     String customerName, String customerEmail) {
            // Validate flight exists
            Flight flight = flightService.getFlightByNumber(flightNumber);
            if (flight == null) {
                throw new IllegalArgumentException("Flight not found: " + flightNumber);
            }
            
            // Validate seats are available
            for (String seatId : seatIds) {
                Seat seat = flight.getSeats().get(seatId);
                if (seat == null || seat.getStatus() != SeatStatus.AVAILABLE) {
                    throw new IllegalArgumentException("Seat not available: " + seatId);
                }
            }
            
            // Create reservation
            String reservationId = generateReservationId();
            Reservation reservation = new Reservation(reservationId, flightNumber, seatIds, 
                                                   customerName, customerEmail);
            
            // Reserve seats
            double totalPrice = 0.0;
            for (String seatId : seatIds) {
                Seat seat = flight.getSeats().get(seatId);
                seat.setStatus(SeatStatus.RESERVED);
                totalPrice += seat.getPrice();
            }
            
            reservation.setTotalPrice(totalPrice);
            
            // Store reservation
            reservations.put(reservationId, reservation);
            flightReservations.computeIfAbsent(flightNumber, k -> new ArrayList<>()).add(reservationId);
            
            return reservation;
        }
        
        /**
         * Direct purchase without reservation - immediate ticket purchase
         */
        public List<Ticket> purchaseSeatsDirectly(String flightNumber, List<String> seatIds, 
                                                String customerName, String customerEmail) {
            // Validate flight exists
            Flight flight = flightService.getFlightByNumber(flightNumber);
            if (flight == null) {
                throw new IllegalArgumentException("Flight not found: " + flightNumber);
            }
            
            // Validate seats are available
            for (String seatId : seatIds) {
                Seat seat = flight.getSeats().get(seatId);
                if (seat == null || seat.getStatus() != SeatStatus.AVAILABLE) {
                    throw new IllegalArgumentException("Seat not available: " + seatId);
                }
            }
            
            // Process payment immediately (stub - would integrate with payment system)
            double totalPrice = 0.0;
            for (String seatId : seatIds) {
                Seat seat = flight.getSeats().get(seatId);
                totalPrice += seat.getPrice();
            }
            
            boolean paymentSuccessful = processPayment(totalPrice);
            if (!paymentSuccessful) {
                throw new RuntimeException("Payment failed for direct purchase");
            }
            
            // Issue tickets immediately
            List<Ticket> tickets = new ArrayList<>();
            for (String seatId : seatIds) {
                String ticketId = generateTicketId();
                Ticket ticket = new Ticket(ticketId, null, flightNumber, seatId, customerName, 
                                         getSeatPrice(flightNumber, seatId));
                tickets.add(ticket);
                
                // Update seat status to occupied
                Seat seat = flight.getSeats().get(seatId);
                seat.setStatus(SeatStatus.OCCUPIED);
                seat.setPassengerName(customerName);
            }
            
            System.out.println("Direct purchase completed for " + customerName + 
                             " - " + seatIds.size() + " seats purchased for $" + totalPrice);
            
            return tickets;
        }
        
        /**
         * Purchase reserved seats (stub implementation)
         */
        public List<Ticket> purchaseReservedSeats(String reservationId) {
            Reservation reservation = reservations.get(reservationId);
            if (reservation == null || reservation.getStatus() != ReservationStatus.PENDING) {
                throw new IllegalArgumentException("Invalid or expired reservation: " + reservationId);
            }
            
            if (reservation.isExpired()) {
                reservation.setStatus(ReservationStatus.EXPIRED);
                throw new IllegalArgumentException("Reservation expired: " + reservationId);
            }
            
            // Process payment (stub - would integrate with payment system)
            boolean paymentSuccessful = processPayment(reservation.getTotalPrice());
            if (!paymentSuccessful) {
                throw new RuntimeException("Payment failed for reservation: " + reservationId);
            }
            
            // Issue tickets
            List<Ticket> tickets = new ArrayList<>();
            for (String seatId : reservation.getSeatIds()) {
                String ticketId = generateTicketId();
                Ticket ticket = new Ticket(ticketId, reservationId, reservation.getFlightNumber(), 
                                         seatId, reservation.getCustomerName(), 
                                         getSeatPrice(reservation.getFlightNumber(), seatId));
                tickets.add(ticket);
            }
            
            // Update reservation status
            reservation.setStatus(ReservationStatus.CONFIRMED);
            
            // Update seat status
            updateSeatStatus(reservation.getFlightNumber(), reservation.getSeatIds(), SeatStatus.OCCUPIED);
            
            return tickets;
        }
        
        /**
         * Cancel previously purchased seats
         */
        public boolean cancelTickets(String reservationId) {
            Reservation reservation = reservations.get(reservationId);
            if (reservation == null || reservation.getStatus() != ReservationStatus.CONFIRMED) {
                return false;
            }
            
            // Process refund (stub - would integrate with payment system)
            boolean refundSuccessful = processRefund(reservation.getTotalPrice());
            if (!refundSuccessful) {
                return false;
            }
            
            // Update seat status
            updateSeatStatus(reservation.getFlightNumber(), reservation.getSeatIds(), SeatStatus.AVAILABLE);
            
            // Update reservation status
            reservation.setStatus(ReservationStatus.CANCELLED);
            
            return true;
        }
        
        /**
         * Cancel directly purchased tickets (without reservation)
         */
        public boolean cancelDirectTickets(String flightNumber, List<String> seatIds, String customerName) {
            Flight flight = flightService.getFlightByNumber(flightNumber);
            if (flight == null) {
                return false;
            }
            
            // Find and validate seats
            double totalRefund = 0.0;
            List<Seat> seatsToCancel = new ArrayList<>();
            
            for (String seatId : seatIds) {
                Seat seat = flight.getSeats().get(seatId);
                if (seat != null && seat.getStatus() == SeatStatus.OCCUPIED && 
                    seat.getPassengerName().equals(customerName)) {
                    seatsToCancel.add(seat);
                    totalRefund += seat.getPrice();
                }
            }
            
            if (seatsToCancel.isEmpty()) {
                return false;
            }
            
            // Process refund (stub - would integrate with payment system)
            boolean refundSuccessful = processRefund(totalRefund);
            if (!refundSuccessful) {
                return false;
            }
            
            // Update seat status
            for (Seat seat : seatsToCancel) {
                seat.setStatus(SeatStatus.AVAILABLE);
                seat.setPassengerName(null);
            }
            
            System.out.println("Direct tickets cancelled for " + customerName + 
                             " - " + seatsToCancel.size() + " seats cancelled, refund: $" + totalRefund);
            
            return true;
        }
        
        public Reservation getReservation(String reservationId) {
            return reservations.get(reservationId);
        }
        
        // Helper methods
        private String generateReservationId() {
            return "RES" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
        }
        
        private String generateTicketId() {
            return "TKT" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
        }
        
        private boolean processPayment(double amount) {
            // Stub implementation - would integrate with payment gateway
            System.out.println("Processing payment of $" + amount);
            return true; // Assume payment always succeeds for demo
        }
        
        private boolean processRefund(double amount) {
            // Stub implementation - would integrate with payment gateway
            System.out.println("Processing refund of $" + amount);
            return true; // Assume refund always succeeds for demo
        }
        
        private double getSeatPrice(String flightNumber, String seatId) {
            // This would typically come from a pricing service
            // For demo, return a fixed price
            return 150.0;
        }
        
        private void updateSeatStatus(String flightNumber, List<String> seatIds, SeatStatus status) {
            Flight flight = flightService.getFlightByNumber(flightNumber);
            if (flight != null) {
                for (String seatId : seatIds) {
                    Seat seat = flight.getSeats().get(seatId);
                    if (seat != null) {
                        seat.setStatus(status);
                    }
                }
            }
        }
    }
    
    // ===== Main with Unit Tests =====
    
    public static void main(String[] args) {
        System.out.println("=== Flight Reserve System - Unit Tests ===");
        
        // Initialize the search service
        FlightSearchService searchService = new FlightSearchService();
        
        // Initialize the seat management service
        SeatManagementService seatService = new SeatManagementService(searchService);
        
        // Initialize the reservation service
        ReservationService reservationService = new ReservationService(searchService);
        
        // Initialize sample flights
        initializeSampleFlights(searchService);
        
        // Run all tests
        runAllTests(searchService, seatService, reservationService);
        
        System.out.println("\n=== All tests completed! ===");
    }
    
    private static void initializeSampleFlights(FlightSearchService searchService) {
        System.out.println("Initializing sample flights...");
        
        // Create sample flights for March 1, 2025
        LocalDate march1 = LocalDate.of(2025, 3, 1);
        
        // Morning flights
        searchService.addFlight(new Flight("AA101", "San Francisco", "Portland", 
                                         march1, LocalTime.of(8, 45), LocalTime.of(10, 5), "American"));
        searchService.addFlight(new Flight("UA201", "San Francisco", "Portland", 
                                         march1, LocalTime.of(9, 15), LocalTime.of(10, 35), "United"));
        searchService.addFlight(new Flight("DL301", "San Francisco", "Seattle", 
                                         march1, LocalTime.of(7, 30), LocalTime.of(9, 0), "Delta"));
        
        // Afternoon flights
        searchService.addFlight(new Flight("AA102", "Portland", "San Francisco", 
                                         march1, LocalTime.of(11, 0), LocalTime.of(12, 20), "American"));
        searchService.addFlight(new Flight("UA202", "Portland", "San Francisco", 
                                         march1, LocalTime.of(13, 45), LocalTime.of(15, 5), "United"));
        searchService.addFlight(new Flight("DL302", "Seattle", "San Francisco", 
                                         march1, LocalTime.of(14, 30), LocalTime.of(16, 0), "Delta"));
        
        // Evening flights
        searchService.addFlight(new Flight("AA103", "San Francisco", "Seattle", 
                                         march1, LocalTime.of(18, 0), LocalTime.of(19, 30), "American"));
        searchService.addFlight(new Flight("UA203", "Portland", "Seattle", 
                                         march1, LocalTime.of(19, 15), LocalTime.of(20, 15), "United"));
        
        // March 2 flights
        LocalDate march2 = LocalDate.of(2025, 3, 2);
        searchService.addFlight(new Flight("AA104", "San Francisco", "Portland", 
                                         march2, LocalTime.of(8, 45), LocalTime.of(10, 5), "American"));
        
        System.out.println("Sample flights initialized successfully!");
    }
    
    private static void runAllTests(FlightSearchService searchService, 
                                   SeatManagementService seatService,
                                   ReservationService reservationService) {
        
        System.out.println("\n=== Running Unit Tests ===");
        
        // Test 1: Search by departure city
        testSearchByDepartureCity(searchService);
        
        // Test 2: Search by arrival city
        testSearchByArrivalCity(searchService);
        
        // Test 3: Search by departure date range
        testSearchByDepartureDate(searchService);
        
        // Test 4: Search by departure time range
        testSearchByDepartureTime(searchService);
        
        // Test 5: Search by city pair
        testSearchByCityPair(searchService);
        
        // Test 6: Search by date and time range
        testSearchByDateTime(searchService);
        
        // Test 7: Search with multiple criteria and ranges
        testSearchWithMultipleCriteria(searchService);
        
        // Test 8: Edge cases
        testEdgeCases(searchService);
        
        // Test 9: Performance test
        testPerformance(searchService);
        
        // Test 10: View available seats
        testViewAvailableSeats(seatService);
        
        // Test 11: Seat map display
        testSeatMapDisplay(seatService);
        
        // Test 12: Seat preferences
        testSeatPreferences(seatService);
        
        // Test 13: Seat statistics
        testSeatStatistics(seatService);
        
        // Test 14: Visual seat map
        testVisualSeatMap(seatService);
        
        // Test 15: Reserve seats
        testReserveSeats(seatService, reservationService);
        
        // Test 16: Purchase reserved seats
        testPurchaseReservedSeats(seatService, reservationService);
        
        // Test 17: Cancel tickets
        testCancelTickets(seatService, reservationService);
        
        // Test 18: Direct purchase without reservation
        testDirectPurchase(seatService, reservationService);
        
        // Test 19: Cancel direct purchase tickets
        testCancelDirectTickets(seatService, reservationService);
        
        // Test 20: Direct purchase error handling
        testDirectPurchaseErrorHandling(seatService, reservationService);
    }
    
    private static void testSearchByDepartureCity(FlightSearchService searchService) {
        System.out.println("\n--- Test 1: Search by Departure City ---");
        
        List<Flight> sfFlights = searchService.searchByDepartureCity("San Francisco");
        System.out.println("Flights from San Francisco: " + sfFlights.size());
        
        // Verify results
        assert sfFlights.size() == 4 : "Expected 4 flights from San Francisco";
        assert sfFlights.stream().allMatch(f -> f.getDepartureCity().equals("San Francisco")) : 
            "All flights should depart from San Francisco";
        
        // Check specific flights
        boolean hasAA101 = sfFlights.stream().anyMatch(f -> f.getFlightNumber().equals("AA101"));
        assert hasAA101 : "Should find flight AA101";
        
        System.out.println("✓ Search by departure city test passed");
        sfFlights.forEach(System.out::println);
    }
    
    private static void testSearchByArrivalCity(FlightSearchService searchService) {
        System.out.println("\n--- Test 2: Search by Arrival City ---");
        
        List<Flight> portlandFlights = searchService.searchByArrivalCity("Portland");
        System.out.println("Flights to Portland: " + portlandFlights.size());
        
        // Verify results
        assert portlandFlights.size() == 2 : "Expected 2 flights to Portland";
        assert portlandFlights.stream().allMatch(f -> f.getArrivalCity().equals("Portland")) : 
            "All flights should arrive in Portland";
        
        System.out.println("✓ Search by arrival city test passed");
        portlandFlights.forEach(System.out::println);
    }
    
    private static void testSearchByDepartureDate(FlightSearchService searchService) {
        System.out.println("\n--- Test 3: Search by Departure Date Range ---");
        
        LocalDate march1 = LocalDate.of(2025, 3, 1);
        LocalDate march2 = LocalDate.of(2025, 3, 2);
        List<Flight> march1to2Flights = searchService.searchByDepartureDateRange(march1, march2);
        System.out.println("Flights from March 1-2, 2025: " + march1to2Flights.size());
        
        // Verify results
        assert march1to2Flights.size() == 9 : "Expected 9 flights from March 1-2";
        assert march1to2Flights.stream().allMatch(f -> 
            !f.getDepartureDate().isBefore(march1) && !f.getDepartureDate().isAfter(march2)) : 
            "All flights should be within March 1-2 date range";
        
        System.out.println("✓ Search by departure date range test passed");
    }
    
    private static void testSearchByDepartureTime(FlightSearchService searchService) {
        System.out.println("\n--- Test 4: Search by Departure Time Range ---");
        
        LocalTime morningStart = LocalTime.of(6, 0);
        LocalTime morningEnd = LocalTime.of(12, 0);
        List<Flight> morningFlights = searchService.searchByDepartureTimeRange(morningStart, morningEnd);
        System.out.println("Morning flights (6:00 AM - 12:00 PM): " + morningFlights.size());
        
        // Verify results
        assert morningFlights.size() == 6 : "Expected 6 morning flights";
        assert morningFlights.stream().allMatch(f -> 
            !f.getDepartureTime().isBefore(morningStart) && !f.getDepartureTime().isAfter(morningEnd)) : 
            "All flights should depart between 6:00 AM and 12:00 PM";
        
        System.out.println("✓ Search by departure time range test passed");
        morningFlights.forEach(System.out::println);
    }
    
    private static void testSearchByCityPair(FlightSearchService searchService) {
        System.out.println("\n--- Test 5: Search by City Pair ---");
        
        List<Flight> sfToPortland = searchService.searchByCityPair("San Francisco", "Portland");
        System.out.println("Flights from San Francisco to Portland: " + sfToPortland.size());
        
        // Verify results
        assert sfToPortland.size() == 2 : "Expected 2 flights from SF to Portland";
        assert sfToPortland.stream().allMatch(f -> 
            f.getDepartureCity().equals("San Francisco") && f.getArrivalCity().equals("Portland")) : 
            "All flights should be from SF to Portland";
        
        System.out.println("✓ Search by city pair test passed");
        sfToPortland.forEach(System.out::println);
    }
    
    private static void testSearchByDateTime(FlightSearchService searchService) {
        System.out.println("\n--- Test 6: Search by Date and Time Range ---");
        
        LocalDate march1 = LocalDate.of(2025, 3, 1);
        LocalDate march1End = LocalDate.of(2025, 3, 1);
        LocalTime morningStart = LocalTime.of(6, 0);
        LocalTime morningEnd = LocalTime.of(12, 0);
        
        List<Flight> morningMarch1Flights = searchService.searchByDateTimeRange(march1, march1End, morningStart, morningEnd);
        System.out.println("Morning flights on March 1 (6:00 AM - 12:00 PM): " + morningMarch1Flights.size());
        
        // Verify results
        assert morningMarch1Flights.size() == 5 : "Expected 5 morning flights on March 1";
        assert morningMarch1Flights.stream().allMatch(f -> 
            f.getDepartureDate().equals(march1) &&
            !f.getDepartureTime().isBefore(morningStart) && 
            !f.getDepartureTime().isAfter(morningEnd)) : 
            "All flights should be on March 1 and depart between 6:00 AM and 12:00 PM";
        
        System.out.println("✓ Search by date and time range test passed");
        morningMarch1Flights.forEach(System.out::println);
    }
    
    private static void testSearchWithMultipleCriteria(FlightSearchService searchService) {
        System.out.println("\n--- Test 7: Search with Multiple Criteria and Ranges ---");
        
        // Search for flights from SF to Portland on March 1-2, morning hours
        LocalDate march1 = LocalDate.of(2025, 3, 1);
        LocalDate march2 = LocalDate.of(2025, 3, 2);
        LocalTime morningStart = LocalTime.of(6, 0);
        LocalTime morningEnd = LocalTime.of(12, 0);
        
        List<Flight> multiCriteriaFlights = searchService.searchFlights("San Francisco", "Portland", 
                                                                       march1, march2, morningStart, morningEnd, null, null);
        System.out.println("Morning flights from SF to Portland on March 1-2: " + multiCriteriaFlights.size());
        
        // Verify results
        assert multiCriteriaFlights.size() == 3 : "Expected 3 flights matching all criteria";
        assert multiCriteriaFlights.stream().allMatch(f -> 
            f.getDepartureCity().equals("San Francisco") && 
            f.getArrivalCity().equals("Portland") && 
            !f.getDepartureDate().isBefore(march1) && !f.getDepartureDate().isAfter(march2) &&
            !f.getDepartureTime().isBefore(morningStart) && !f.getDepartureTime().isAfter(morningEnd)) : 
            "All flights should match the multiple criteria with ranges";
        
        System.out.println("✓ Search with multiple criteria and ranges test passed");
        multiCriteriaFlights.forEach(System.out::println);
    }
    
    private static void testEdgeCases(FlightSearchService searchService) {
        System.out.println("\n--- Test 8: Edge Cases ---");
        
        // Test with null parameters (should return all flights)
        List<Flight> allFlights = searchService.searchFlights(null, null, null, null, null, null, null, null);
        System.out.println("All flights (null criteria): " + allFlights.size());
        assert allFlights.size() == 9 : "Expected 9 total flights";
        
        // Test with non-existent city
        List<Flight> nonExistentCity = searchService.searchByDepartureCity("NonExistentCity");
        System.out.println("Flights from non-existent city: " + nonExistentCity.size());
        assert nonExistentCity.size() == 0 : "Expected 0 flights from non-existent city";
        
        // Test with non-existent date
        LocalDate nonExistentDate = LocalDate.of(2025, 12, 31);
        List<Flight> nonExistentDateFlights = searchService.searchByDepartureDateRange(nonExistentDate, nonExistentDate);
        System.out.println("Flights on non-existent date: " + nonExistentDateFlights.size());
        assert nonExistentDateFlights.size() == 0 : "Expected 0 flights on non-existent date";
        
        System.out.println("✓ Edge cases test passed");
    }
    
    private static void testPerformance(FlightSearchService searchService) {
        System.out.println("\n--- Test 9: Performance Test ---");
        
        long startTime = System.currentTimeMillis();
        
        // Perform 1000 searches
        for (int i = 0; i < 1000; i++) {
            searchService.searchByDepartureCity("San Francisco");
            searchService.searchByArrivalCity("Portland");
            searchService.searchByDepartureDateRange(LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 2));
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("1000 searches completed in: " + duration + "ms");
        assert duration < 1000 : "Performance test should complete within 1 second";
        
        System.out.println("✓ Performance test passed");
    }

    private static void testViewAvailableSeats(SeatManagementService seatService) {
        System.out.println("\n--- Test 10: View Available Seats ---");
        
        List<Seat> availableSeats = seatService.getAvailableSeats("AA101");
        System.out.println("Available seats on AA101: " + availableSeats.size());
        
        // Verify results - Fixed plane dimensions: 24 rows × 6 seats = 144 total seats
        assert availableSeats.size() == 144 : "Expected 144 available seats (24 rows × 6 seats)";
        assert availableSeats.stream().allMatch(seat -> seat.getStatus() == SeatStatus.AVAILABLE) : 
            "All seats should be available";
        
        // Check first seat details
        Seat firstSeat = availableSeats.get(0);
        System.out.println("First available seat: " + firstSeat);
        assert firstSeat.getSeatId().equals("1A") : "Expected first seat to be 1A";
        assert firstSeat.getRow() == 1 : "Expected first seat to be in row 1";
        assert firstSeat.getColumn() == 'A' : "Expected first seat to be in column A";
        
        // Check last seat details
        Seat lastSeat = availableSeats.get(availableSeats.size() - 1);
        System.out.println("Last available seat: " + lastSeat);
        assert lastSeat.getSeatId().equals("24F") : "Expected last seat to be 24F";
        assert lastSeat.getRow() == 24 : "Expected last seat to be in row 24";
        assert lastSeat.getColumn() == 'F' : "Expected last seat to be in column F";
        
        System.out.println("✓ View available seats test passed");
    }
    
    private static void testSeatMapDisplay(SeatManagementService seatService) {
        System.out.println("\n--- Test 11: Seat Map Display ---");
        
        Map<String, List<Seat>> seatMap = seatService.getSeatMap("AA101");
        System.out.println("Seat map rows: " + seatMap.size());
        
        // Verify results - Fixed plane dimensions: 24 rows × 6 seats
        assert seatMap.size() == 24 : "Expected 24 rows in seat map";
        
        // Check first row
        List<Seat> firstRow = seatMap.get("Row 1");
        System.out.println("First row seats: " + firstRow.size());
        assert firstRow.size() == 6 : "Expected 6 seats in first row";
        
        // Check seat IDs in first row (A, B, C, D, E, F)
        String[] expectedSeats = {"1A", "1B", "1C", "1D", "1E", "1F"};
        for (int i = 0; i < 6; i++) {
            assert firstRow.get(i).getSeatId().equals(expectedSeats[i]) : 
                "Expected seat " + expectedSeats[i] + " in position " + i;
        }
        
        // Check last row
        List<Seat> lastRow = seatMap.get("Row 24");
        assert lastRow.size() == 6 : "Expected 6 seats in last row";
        String[] expectedLastRowSeats = {"24A", "24B", "24C", "24D", "24E", "24F"};
        for (int i = 0; i < 6; i++) {
            assert lastRow.get(i).getSeatId().equals(expectedLastRowSeats[i]) : 
                "Expected seat " + expectedLastRowSeats[i] + " in position " + i;
        }
        
        System.out.println("✓ Seat map display test passed");
        
        // Display first few rows for visualization
        System.out.println("Sample seat map (first 3 rows):");
        for (int i = 1; i <= 3; i++) {
            List<Seat> row = seatMap.get("Row " + i);
            System.out.print("Row " + i + ": ");
            row.forEach(seat -> System.out.print(seat.getSeatId() + "(" + seat.getStatus() + ") "));
            System.out.println();
        }
    }
    
    private static void testSeatPreferences(SeatManagementService seatService) {
        System.out.println("\n--- Test 12: Seat Preferences ---");
        
        // Test window seats (A and F columns) - 24 rows × 2 seats = 48 window seats
        List<Seat> windowSeats = seatService.getAvailableSeatsByPreference("AA101", SeatPreference.WINDOW);
        System.out.println("Available window seats: " + windowSeats.size());
        assert windowSeats.size() == 48 : "Expected 48 window seats (24 rows × 2 window seats per row)";
        assert windowSeats.stream().allMatch(Seat::isWindowSeat) : "All seats should be window seats";
        
        // Test aisle seats (C and D columns) - 24 rows × 2 seats = 48 aisle seats
        List<Seat> aisleSeats = seatService.getAvailableSeatsByPreference("AA101", SeatPreference.AISLE);
        System.out.println("Available aisle seats: " + aisleSeats.size());
        assert aisleSeats.size() == 48 : "Expected 48 aisle seats (24 rows × 2 aisle seats per row)";
        assert aisleSeats.stream().allMatch(Seat::isAisleSeat) : "All seats should be aisle seats";
        
        // Test premium seats (first 3 rows) - 3 rows × 6 seats = 18 premium seats
        List<Seat> premiumSeats = seatService.getAvailableSeatsByPreference("AA101", SeatPreference.PREMIUM);
        System.out.println("Available premium seats: " + premiumSeats.size());
        assert premiumSeats.size() == 18 : "Expected 18 premium seats (3 rows × 6 seats)";
        assert premiumSeats.stream().allMatch(seat -> seat.getRow() <= 3) : "All seats should be in first 3 rows";
        
        // Test economy seats (rows 4-24) - 21 rows × 6 seats = 126 economy seats
        List<Seat> economySeats = seatService.getAvailableSeatsByPreference("AA101", SeatPreference.ECONOMY);
        System.out.println("Available economy seats: " + economySeats.size());
        assert economySeats.size() == 126 : "Expected 126 economy seats (21 rows × 6 seats)";
        assert economySeats.stream().allMatch(seat -> seat.getRow() > 3) : "All seats should be after row 3";
        
        System.out.println("✓ Seat preferences test passed");
    }
    
    private static void testSeatStatistics(SeatManagementService seatService) {
        System.out.println("\n--- Test 13: Seat Statistics ---");
        
        SeatStatistics stats = seatService.getSeatStatistics("AA101");
        System.out.println("Seat statistics: " + stats);
        
        // Verify results - Fixed plane dimensions: 24 rows × 6 seats = 144 total seats
        assert stats.getTotalSeats() == 144 : "Expected 144 total seats (24 rows × 6 seats)";
        assert stats.getAvailableSeats() == 144 : "Expected 144 available seats initially";
        assert stats.getReservedSeats() == 0 : "Expected 0 reserved seats initially";
        assert stats.getOccupiedSeats() == 0 : "Expected 0 occupied seats initially";
        assert stats.getOccupancyRate() == 0.0 : "Expected 0% occupancy rate initially";
        
        System.out.println("✓ Seat statistics test passed");
    }
    
    private static void testVisualSeatMap(SeatManagementService seatService) {
        System.out.println("\n--- Test 14: Visual Seat Map ---");
        
        System.out.println("Displaying visual seat map for flight AA101:");
        seatService.displaySeatMap("AA101");
        
        System.out.println("✓ Visual seat map test passed");
    }
    
    private static void testReserveSeats(SeatManagementService seatService, ReservationService reservationService) {
        System.out.println("\n--- Test 15: Reserve Seats ---");
        
        try {
            // Show seat map before reservation
            System.out.println("Seat map BEFORE reserving seats 1A and 1B:");
            seatService.displaySeatMap("AA101");
            
            // Reserve seats
            Reservation reservation = reservationService.reserveSeats("AA101", 
                Arrays.asList("1A", "1B"), "John Doe", "john@example.com");
            System.out.println("Seats reserved: " + reservation.getSeatIds().size());
            assert reservation.getSeatIds().size() == 2 : "Expected 2 seats to be reserved";
            assert reservation.getStatus() == ReservationStatus.PENDING : "Reservation should be pending";
            System.out.println("Reservation ID: " + reservation.getReservationId());
            System.out.println("Total price: $" + reservation.getTotalPrice());
            System.out.println("Expires at: " + reservation.getExpiresAt());
            
            // Show seat map after reservation
            System.out.println("\nSeat map AFTER reserving seats 1A and 1B:");
            seatService.displaySeatMap("AA101");
            
            System.out.println("✓ Reserve seats test passed");
        } catch (IllegalArgumentException e) {
            System.err.println("Error reserving seats: " + e.getMessage());
            assert false : "Reserving seats should not throw an exception";
        }
    }
    
    private static void testPurchaseReservedSeats(SeatManagementService seatService, ReservationService reservationService) {
        System.out.println("\n--- Test 16: Purchase Reserved Seats ---");
        
        try {
            // First reserve seats
            Reservation reservation = reservationService.reserveSeats("AA101", 
                Arrays.asList("2A", "2B"), "Jane Smith", "jane@example.com");
            
            // Show seat map after reservation but before purchase
            System.out.println("Seat map AFTER reserving seats 2A and 2B (before purchase):");
            seatService.displaySeatMap("AA101");
            
            // Then purchase them
            List<Ticket> tickets = reservationService.purchaseReservedSeats(reservation.getReservationId());
            System.out.println("Tickets purchased: " + tickets.size());
            assert tickets.size() == 2 : "Expected 2 tickets to be purchased";
            assert reservation.getStatus() == ReservationStatus.CONFIRMED : "Reservation should be confirmed";
            
            // Check ticket details
            for (Ticket ticket : tickets) {
                System.out.println("Ticket: " + ticket.getTicketId() + " for seat " + ticket.getSeatId());
                assert ticket.getPassengerName().equals("Jane Smith") : "Passenger name should match";
            }
            
            // Show seat map after purchase
            System.out.println("\nSeat map AFTER purchasing seats 2A and 2B:");
            seatService.displaySeatMap("AA101");
            
            System.out.println("✓ Purchase reserved seats test passed");
        } catch (IllegalArgumentException e) {
            System.err.println("Error purchasing reserved seats: " + e.getMessage());
            assert false : "Purchasing reserved seats should not throw an exception";
        }
    }
    
    private static void testCancelTickets(SeatManagementService seatService, ReservationService reservationService) {
        System.out.println("\n--- Test 17: Cancel Tickets ---");
        
        try {
            // First reserve and purchase seats
            Reservation reservation = reservationService.reserveSeats("AA101", 
                Arrays.asList("3A", "3B"), "Bob Johnson", "bob@example.com");
            reservationService.purchaseReservedSeats(reservation.getReservationId());
            
            // Show seat map after purchase but before cancellation
            System.out.println("Seat map AFTER purchasing seats 3A and 3B (before cancellation):");
            seatService.displaySeatMap("AA101");
            
            // Then cancel them
            boolean cancelled = reservationService.cancelTickets(reservation.getReservationId());
            assert cancelled : "Expected cancellation to succeed";
            assert reservation.getStatus() == ReservationStatus.CANCELLED : "Reservation should be cancelled";
            
            System.out.println("Tickets cancelled successfully for reservation: " + reservation.getReservationId());
            
            // Show seat map after cancellation
            System.out.println("\nSeat map AFTER cancelling seats 3A and 3B:");
            seatService.displaySeatMap("AA101");
            
            System.out.println("✓ Cancel tickets test passed");
        } catch (IllegalArgumentException e) {
            System.err.println("Error cancelling tickets: " + e.getMessage());
            assert false : "Cancelling tickets should not throw an exception";
        }
    }

    private static void testDirectPurchase(SeatManagementService seatService, ReservationService reservationService) {
        System.out.println("\n--- Test 18: Direct Purchase Without Reservation ---");
        
        try {
            // Show seat map before direct purchase
            System.out.println("Seat map BEFORE direct purchase of seats 4A and 4B:");
            seatService.displaySeatMap("AA101");
            
            // Direct purchase without reservation
            List<Ticket> tickets = reservationService.purchaseSeatsDirectly("AA101", 
                Arrays.asList("4A", "4B"), "Alice Brown", "alice@example.com");
            
            System.out.println("Direct purchase completed: " + tickets.size() + " tickets issued");
            assert tickets.size() == 2 : "Expected 2 tickets to be issued";
            
            // Check ticket details
            for (Ticket ticket : tickets) {
                System.out.println("Ticket: " + ticket.getTicketId() + " for seat " + ticket.getSeatId());
                assert ticket.getPassengerName().equals("Alice Brown") : "Passenger name should match";
                assert ticket.getReservationId() == null : "Direct purchase should not have reservation ID";
            }
            
            // Show seat map after direct purchase
            System.out.println("\nSeat map AFTER direct purchase of seats 4A and 4B:");
            seatService.displaySeatMap("AA101");
            
            System.out.println("✓ Direct purchase test passed");
        } catch (IllegalArgumentException e) {
            System.err.println("Error in direct purchase: " + e.getMessage());
            assert false : "Direct purchase should not throw an exception";
        }
    }
    
    private static void testCancelDirectTickets(SeatManagementService seatService, ReservationService reservationService) {
        System.out.println("\n--- Test 19: Cancel Direct Purchase Tickets ---");
        
        try {
            // First make a direct purchase
            List<Ticket> tickets = reservationService.purchaseSeatsDirectly("AA101", 
                Arrays.asList("5A", "5B"), "Charlie Wilson", "charlie@example.com");
            
            // Show seat map after purchase but before cancellation
            System.out.println("Seat map AFTER direct purchase of seats 5A and 5B (before cancellation):");
            seatService.displaySeatMap("AA101");
            
            // Cancel the directly purchased tickets
            boolean cancelled = reservationService.cancelDirectTickets("AA101", 
                Arrays.asList("5A", "5B"), "Charlie Wilson");
            assert cancelled : "Expected cancellation to succeed";
            
            System.out.println("Direct tickets cancelled successfully for Charlie Wilson");
            
            // Show seat map after cancellation
            System.out.println("\nSeat map AFTER cancelling directly purchased seats 5A and 5B:");
            seatService.displaySeatMap("AA101");
            
            System.out.println("✓ Cancel direct tickets test passed");
        } catch (IllegalArgumentException e) {
            System.err.println("Error cancelling direct tickets: " + e.getMessage());
            assert false : "Cancelling direct tickets should not throw an exception";
        }
    }
    
    private static void testDirectPurchaseErrorHandling(SeatManagementService seatService, ReservationService reservationService) {
        System.out.println("\n--- Test 20: Direct Purchase Error Handling ---");
        
        try {
            // Try to purchase already occupied seats
            List<Ticket> tickets = reservationService.purchaseSeatsDirectly("AA101", 
                Arrays.asList("4A", "4B"), "David Lee", "david@example.com");
            assert false : "Should not be able to purchase already occupied seats";
        } catch (IllegalArgumentException e) {
            System.out.println("Correctly caught error: " + e.getMessage());
            assert e.getMessage().contains("Seat not available") : "Error should mention seat not available";
        }
        
        try {
            // Try to purchase non-existent seats
            List<Ticket> tickets = reservationService.purchaseSeatsDirectly("AA101", 
                Arrays.asList("999Z", "888Y"), "Eve Smith", "eve@example.com");
            assert false : "Should not be able to purchase non-existent seats";
        } catch (IllegalArgumentException e) {
            System.out.println("Correctly caught error: " + e.getMessage());
            assert e.getMessage().contains("Seat not available") : "Error should mention seat not available";
        }
        
        try {
            // Try to purchase from non-existent flight
            List<Ticket> tickets = reservationService.purchaseSeatsDirectly("INVALID", 
                Arrays.asList("1A", "1B"), "Frank Johnson", "frank@example.com");
            assert false : "Should not be able to purchase from non-existent flight";
        } catch (IllegalArgumentException e) {
            System.out.println("Correctly caught error: " + e.getMessage());
            assert e.getMessage().contains("Flight not found") : "Error should mention flight not found";
        }
        
        System.out.println("✓ Direct purchase error handling test passed");
    }
}
