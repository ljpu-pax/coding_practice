package ambience;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class HotelBookingSystem {
    
    // ===== Core Domain Models =====
    
    public static class Hotel {
        private String id;
        private String name;
        private String city;
        private String address;
        private int stars;
        private List<Room> rooms;
        private Set<Amenity> amenities;
        private double rating;
        private int reviewCount;
        
        public Hotel(String id, String name, String city, String address, int stars) {
            this.id = id;
            this.name = name;
            this.city = city;
            this.address = address;
            this.stars = stars;
            this.rooms = new ArrayList<>();
            this.amenities = new HashSet<>();
            this.rating = 0.0;
            this.reviewCount = 0;
        }
        
        // Getters and setters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getCity() { return city; }
        public String getAddress() { return address; }
        public int getStars() { return stars; }
        public List<Room> getRooms() { return rooms; }
        public Set<Amenity> getAmenities() { return amenities; }
        public double getRating() { return rating; }
        public int getReviewCount() { return reviewCount; }
        
        public void addRoom(Room room) {
            this.rooms.add(room);
        }
        
        public void addAmenity(Amenity amenity) {
            this.amenities.add(amenity);
        }
        
        public void updateRating(double newRating) {
            this.rating = (this.rating * this.reviewCount + newRating) / (this.reviewCount + 1);
            this.reviewCount++;
        }
    }
    
    public static class Room {
        private String id;
        private String hotelId;
        private String type;
        private int capacity;
        private double pricePerNight;
        private Set<Amenity> amenities;
        private boolean isAvailable;
        
        public Room(String id, String hotelId, String type, int capacity, double pricePerNight) {
            this.id = id;
            this.hotelId = hotelId;
            this.type = type;
            this.capacity = capacity;
            this.pricePerNight = pricePerNight;
            this.amenities = new HashSet<>();
            this.isAvailable = true;
        }
        
        // Getters and setters
        public String getId() { return id; }
        public String getHotelId() { return hotelId; }
        public String getType() { return type; }
        public int getCapacity() { return capacity; }
        public double getPricePerNight() { return pricePerNight; }
        public Set<Amenity> getAmenities() { return amenities; }
        public boolean isAvailable() { return isAvailable; }
        
        public void setAvailable(boolean available) { isAvailable = available; }
        public void addAmenity(Amenity amenity) { this.amenities.add(amenity); }
    }
    
    public static class Booking {
        private String id;
        private String userId;
        private String hotelId;
        private String roomId;
        private LocalDate checkIn;
        private LocalDate checkOut;
        private int guestCount;
        private double totalPrice;
        private BookingStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public Booking(String id, String userId, String hotelId, String roomId, 
                      LocalDate checkIn, LocalDate checkOut, int guestCount, double totalPrice) {
            this.id = id;
            this.userId = userId;
            this.hotelId = hotelId;
            this.roomId = roomId;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
            this.guestCount = guestCount;
            this.totalPrice = totalPrice;
            this.status = BookingStatus.PENDING;
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }
        
        // Getters and setters
        public String getId() { return id; }
        public String getUserId() { return userId; }
        public String getHotelId() { return hotelId; }
        public String getRoomId() { return roomId; }
        public LocalDate getCheckIn() { return checkIn; }
        public LocalDate getCheckOut() { return checkOut; }
        public int getGuestCount() { return guestCount; }
        public double getTotalPrice() { return totalPrice; }
        public BookingStatus getStatus() { return status; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        
        public void setStatus(BookingStatus status) { 
            this.status = status; 
            this.updatedAt = LocalDateTime.now();
        }
        
        public int getNumberOfNights() {
            return checkOut.getDayOfYear() - checkIn.getDayOfYear();
        }
    }
    
    public static class User {
        private String id;
        private String name;
        private String email;
        private String phone;
        private List<String> bookingHistory;
        private UserType type;
        
        public User(String id, String name, String email, String phone, UserType type) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.type = type;
            this.bookingHistory = new ArrayList<>();
        }
        
        // Getters and setters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public List<String> getBookingHistory() { return bookingHistory; }
        public UserType getType() { return type; }
        
        public void addBooking(String bookingId) {
            this.bookingHistory.add(bookingId);
        }
    }
    
    // ===== Enums =====
    
    public enum BookingStatus {
        PENDING, CONFIRMED, CANCELLED, COMPLETED
    }
    
    public enum UserType {
        REGULAR, PREMIUM, VIP
    }
    
    public enum Amenity {
        WIFI, POOL, GYM, SPA, RESTAURANT, PARKING, ROOM_SERVICE, AIR_CONDITIONING
    }
    
    // ===== Service Layer =====
    
    public static class HotelBookingService {
        private Map<String, Hotel> hotels;
        private Map<String, Room> rooms;
        private Map<String, Booking> bookings;
        private Map<String, User> users;
        private Map<String, Set<String>> hotelBookings; // hotelId -> bookingIds
        
        public HotelBookingService() {
            this.hotels = new ConcurrentHashMap<>();
            this.rooms = new ConcurrentHashMap<>();
            this.bookings = new ConcurrentHashMap<>();
            this.users = new ConcurrentHashMap<>();
            this.hotelBookings = new ConcurrentHashMap<>();
        }
        
        // ===== Hotel Management =====
        
        public void addHotel(Hotel hotel) {
            hotels.put(hotel.getId(), hotel);
            hotelBookings.put(hotel.getId(), new HashSet<>());
        }
        
        public Hotel getHotel(String hotelId) {
            return hotels.get(hotelId);
        }
        
        public List<Hotel> searchHotels(String city, LocalDate checkIn, LocalDate checkOut, 
                                      int guestCount, double maxPrice, int minStars) {
            return hotels.values().stream()
                .filter(hotel -> hotel.getCity().equalsIgnoreCase(city))
                .filter(hotel -> hotel.getStars() >= minStars)
                .filter(hotel -> hasAvailableRooms(hotel.getId(), checkIn, checkOut, guestCount, maxPrice))
                .sorted((h1, h2) -> Double.compare(h2.getRating(), h1.getRating()))
                .collect(Collectors.toList());
        }
        
        // ===== Room Management =====
        
        public void addRoom(Room room) {
            rooms.put(room.getId(), room);
            Hotel hotel = hotels.get(room.getHotelId());
            if (hotel != null) {
                hotel.addRoom(room);
            }
        }
        
        public List<Room> getAvailableRooms(String hotelId, LocalDate checkIn, LocalDate checkOut, 
                                          int guestCount, double maxPrice) {
            return rooms.values().stream()
                .filter(room -> room.getHotelId().equals(hotelId))
                .filter(room -> room.getCapacity() >= guestCount)
                .filter(room -> room.getPricePerNight() <= maxPrice)
                .filter(room -> isRoomAvailable(room.getId(), checkIn, checkOut))
                .collect(Collectors.toList());
        }
        
        // ===== Booking Management =====
        
        public Booking createBooking(String userId, String hotelId, String roomId, 
                                   LocalDate checkIn, LocalDate checkOut, int guestCount) {
            // Validate inputs
            if (!users.containsKey(userId) || !hotels.containsKey(hotelId) || !rooms.containsKey(roomId)) {
                throw new IllegalArgumentException("Invalid user, hotel, or room ID");
            }
            
            if (checkIn.isAfter(checkOut) || checkIn.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Invalid check-in/check-out dates");
            }
            
            Room room = rooms.get(roomId);
            if (room.getCapacity() < guestCount) {
                throw new IllegalArgumentException("Room capacity insufficient for guest count");
            }
            
            if (!isRoomAvailable(roomId, checkIn, checkOut)) {
                throw new IllegalArgumentException("Room not available for selected dates");
            }
            
            // Calculate total price
            int nights = checkOut.getDayOfYear() - checkIn.getDayOfYear();
            double totalPrice = room.getPricePerNight() * nights;
            
            // Create booking
            String bookingId = generateBookingId();
            Booking booking = new Booking(bookingId, userId, hotelId, roomId, 
                                        checkIn, checkOut, guestCount, totalPrice);
            
            // Update data structures
            bookings.put(bookingId, booking);
            hotelBookings.get(hotelId).add(bookingId);
            users.get(userId).addBooking(bookingId);
            
            return booking;
        }
        
        public boolean cancelBooking(String bookingId, String userId) {
            Booking booking = bookings.get(bookingId);
            if (booking == null || !booking.getUserId().equals(userId)) {
                return false;
            }
            
            if (booking.getStatus() == BookingStatus.CANCELLED || 
                booking.getStatus() == BookingStatus.COMPLETED) {
                return false;
            }
            
            // Check if cancellation is allowed (e.g., within 24 hours of check-in)
            if (LocalDate.now().plusDays(1).isAfter(booking.getCheckIn())) {
                return false;
            }
            
            booking.setStatus(BookingStatus.CANCELLED);
            return true;
        }
        
        public List<Booking> getUserBookings(String userId) {
            if (!users.containsKey(userId)) {
                return new ArrayList<>();
            }
            
            return users.get(userId).getBookingHistory().stream()
                .map(bookings::get)
                .filter(Objects::nonNull)
                .sorted((b1, b2) -> b2.getCreatedAt().compareTo(b1.getCreatedAt()))
                .collect(Collectors.toList());
        }
        
        public List<Booking> getHotelBookings(String hotelId) {
            if (!hotelBookings.containsKey(hotelId)) {
                return new ArrayList<>();
            }
            
            return hotelBookings.get(hotelId).stream()
                .map(bookings::get)
                .filter(Objects::nonNull)
                .sorted((b1, b2) -> b2.getCreatedAt().compareTo(b1.getCreatedAt()))
                .collect(Collectors.toList());
        }
        
        // ===== User Management =====
        
        public void addUser(User user) {
            users.put(user.getId(), user);
        }
        
        public User getUser(String userId) {
            return users.get(userId);
        }
        
        // ===== Helper Methods =====
        
        private boolean hasAvailableRooms(String hotelId, LocalDate checkIn, LocalDate checkOut, 
                                       int guestCount, double maxPrice) {
            return getAvailableRooms(hotelId, checkIn, checkOut, guestCount, maxPrice).size() > 0;
        }
        
        private boolean isRoomAvailable(String roomId, LocalDate checkIn, LocalDate checkOut) {
            return bookings.values().stream()
                .filter(booking -> booking.getRoomId().equals(roomId))
                .filter(booking -> booking.getStatus() != BookingStatus.CANCELLED)
                .noneMatch(booking -> 
                    (checkIn.isBefore(booking.getCheckOut()) && checkOut.isAfter(booking.getCheckIn())));
        }
        
        private String generateBookingId() {
            return "BK" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
        }
        
        // ===== Analytics Methods =====
        
        public Map<String, Integer> getHotelOccupancyRates(LocalDate date) {
            Map<String, Integer> occupancy = new HashMap<>();
            
            for (Hotel hotel : hotels.values()) {
                int totalRooms = hotel.getRooms().size();
                int occupiedRooms = (int) hotel.getRooms().stream()
                    .filter(room -> !isRoomAvailable(room.getId(), date, date.plusDays(1)))
                    .count();
                
                occupancy.put(hotel.getId(), totalRooms > 0 ? (occupiedRooms * 100) / totalRooms : 0);
            }
            
            return occupancy;
        }
        
        public double getAverageBookingValue() {
            return bookings.values().stream()
                .mapToDouble(Booking::getTotalPrice)
                .average()
                .orElse(0.0);
        }
    }
    
    // ===== Main Application =====
    
    public static void main(String[] args) {
        System.out.println("=== Hotel Booking System Demo ===");
        
        HotelBookingService service = new HotelBookingService();
        
        // Initialize sample data
        initializeSampleData(service);
        
        // Demo the system
        demonstrateSystem(service);
    }
    
    private static void initializeSampleData(HotelBookingService service) {
        System.out.println("Initializing sample data...");
        
        // Create hotels
        Hotel hotel1 = new Hotel("H001", "Grand Plaza Hotel", "New York", "123 Main St", 5);
        hotel1.addAmenity(Amenity.WIFI);
        hotel1.addAmenity(Amenity.POOL);
        hotel1.addAmenity(Amenity.GYM);
        service.addHotel(hotel1);
        
        Hotel hotel2 = new Hotel("H002", "Comfort Inn", "New York", "456 Oak Ave", 3);
        hotel2.addAmenity(Amenity.WIFI);
        hotel2.addAmenity(Amenity.PARKING);
        service.addHotel(hotel2);
        
        // Create rooms
        Room room1 = new Room("R001", "H001", "Deluxe", 2, 200.0);
        room1.addAmenity(Amenity.AIR_CONDITIONING);
        room1.addAmenity(Amenity.ROOM_SERVICE);
        service.addRoom(room1);
        
        Room room2 = new Room("R002", "H001", "Suite", 4, 350.0);
        room2.addAmenity(Amenity.AIR_CONDITIONING);
        room2.addAmenity(Amenity.ROOM_SERVICE);
        service.addRoom(room2);
        
        Room room3 = new Room("R003", "H002", "Standard", 2, 120.0);
        room3.addAmenity(Amenity.AIR_CONDITIONING);
        service.addRoom(room3);
        
        // Create users
        User user1 = new User("U001", "John Doe", "john@email.com", "555-0101", UserType.REGULAR);
        User user2 = new User("U002", "Jane Smith", "jane@email.com", "555-0102", UserType.PREMIUM);
        service.addUser(user1);
        service.addUser(user2);
        
        System.out.println("Sample data initialized successfully!");
    }
    
    private static void demonstrateSystem(HotelBookingService service) {
        System.out.println("\n=== System Demonstration ===");
        
        // Search for hotels
        LocalDate checkIn = LocalDate.now().plusDays(7);
        LocalDate checkOut = LocalDate.now().plusDays(10);
        
        System.out.println("\n1. Searching for hotels in New York...");
        List<Hotel> availableHotels = service.searchHotels("New York", checkIn, checkOut, 2, 300.0, 3);
        System.out.println("Found " + availableHotels.size() + " available hotels:");
        availableHotels.forEach(hotel -> 
            System.out.println("  - " + hotel.getName() + " (" + hotel.getStars() + " stars)"));
        
        // Create a booking
        System.out.println("\n2. Creating a booking...");
        try {
            Booking booking = service.createBooking("U001", "H001", "R001", checkIn, checkOut, 2);
            System.out.println("Booking created successfully: " + booking.getId());
            System.out.println("Total price: $" + booking.getTotalPrice());
        } catch (Exception e) {
            System.out.println("Error creating booking: " + e.getMessage());
        }
        
        // Show user bookings
        System.out.println("\n3. User U001's bookings:");
        List<Booking> userBookings = service.getUserBookings("U001");
        userBookings.forEach(booking -> 
            System.out.println("  - " + booking.getId() + " at " + 
                             service.getHotel(booking.getHotelId()).getName()));
        
        // Show hotel occupancy
        System.out.println("\n4. Hotel occupancy rates for today:");
        Map<String, Integer> occupancy = service.getHotelOccupancyRates(LocalDate.now());
        occupancy.forEach((hotelId, rate) -> {
            Hotel hotel = service.getHotel(hotelId);
            System.out.println("  - " + hotel.getName() + ": " + rate + "%");
        });
        
        // Show analytics
        System.out.println("\n5. System analytics:");
        System.out.println("  - Average booking value: $" + String.format("%.2f", service.getAverageBookingValue()));
        
        System.out.println("\n=== Demo completed! ===");
    }
}
