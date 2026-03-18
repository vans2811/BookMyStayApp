import java.util.*;

// Custom Exception for Invalid Booking
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// Reservation Model
class Reservation {
    String customerName;
    String roomType;

    public Reservation(String customerName, String roomType) {
        this.customerName = customerName;
        this.roomType = roomType;
    }
}

// Inventory Service
class InventoryService {
    private Map<String, Integer> inventory = new HashMap<>();

    public InventoryService() {
        inventory.put("Single", 2);
        inventory.put("Double", 1);
        inventory.put("Suite", 1);
    }

    public boolean isValidRoomType(String roomType) {
        return inventory.containsKey(roomType);
    }

    public boolean isAvailable(String roomType) {
        return inventory.getOrDefault(roomType, 0) > 0;
    }

    public void decrement(String roomType) throws InvalidBookingException {
        int count = inventory.get(roomType);

        if (count <= 0) {
            throw new InvalidBookingException("Inventory cannot go negative for room type: " + roomType);
        }

        inventory.put(roomType, count - 1);
    }

    public void displayInventory() {
        System.out.println("Current Inventory: " + inventory);
    }
}

// Validator Class
class BookingValidator {

    public static void validate(Reservation reservation, InventoryService inventoryService)
            throws InvalidBookingException {

        // Validate customer name
        if (reservation.customerName == null || reservation.customerName.trim().isEmpty()) {
            throw new InvalidBookingException("Customer name cannot be empty.");
        }

        // Validate room type
        if (!inventoryService.isValidRoomType(reservation.roomType)) {
            throw new InvalidBookingException("Invalid room type: " + reservation.roomType);
        }

        // Validate availability
        if (!inventoryService.isAvailable(reservation.roomType)) {
            throw new InvalidBookingException("No rooms available for type: " + reservation.roomType);
        }
    }
}

// Booking Service
class BookingService {
    private InventoryService inventoryService;

    public BookingService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void processBooking(Reservation reservation) {
        try {
            // Step 1: Validate input (Fail-Fast)
            BookingValidator.validate(reservation, inventoryService);

            // Step 2: Safe inventory update
            inventoryService.decrement(reservation.roomType);

            // Step 3: Confirm booking
            System.out.println("Booking Confirmed for " + reservation.customerName +
                    " | Room Type: " + reservation.roomType);

        } catch (InvalidBookingException e) {
            // Graceful failure handling
            System.out.println("Booking Failed: " + e.getMessage());
        }
    }
}

// Main Class
public class UseCase9ErrorHandlingValidation {

    public static void main(String[] args) {

        InventoryService inventoryService = new InventoryService();
        BookingService bookingService = new BookingService(inventoryService);

        // Test Cases

        // Valid booking
        bookingService.processBooking(new Reservation("Alice", "Single"));

        // Invalid room type
        bookingService.processBooking(new Reservation("Bob", "Luxury"));

        // Empty name
        bookingService.processBooking(new Reservation("", "Double"));

        // Exhaust inventory
        bookingService.processBooking(new Reservation("Charlie", "Suite"));
        bookingService.processBooking(new Reservation("David", "Suite")); // Should fail

        // Display final inventory
        inventoryService.displayInventory();
    }
}