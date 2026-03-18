import java.util.*;

// Reservation Model
class Reservation {
    String reservationId;
    String customerName;
    String roomType;
    String roomId;
    boolean isActive;

    public Reservation(String reservationId, String customerName, String roomType, String roomId) {
        this.reservationId = reservationId;
        this.customerName = customerName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.isActive = true;
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

    public void decrement(String roomType) {
        inventory.put(roomType, inventory.get(roomType) - 1);
    }

    public void increment(String roomType) {
        inventory.put(roomType, inventory.get(roomType) + 1);
    }

    public void displayInventory() {
        System.out.println("Current Inventory: " + inventory);
    }
}

// Booking History
class BookingHistory {
    private Map<String, Reservation> reservations = new HashMap<>();

    public void addReservation(Reservation r) {
        reservations.put(r.reservationId, r);
    }

    public Reservation getReservation(String reservationId) {
        return reservations.get(reservationId);
    }
}

// Cancellation Service
class CancellationService {

    private BookingHistory history;
    private InventoryService inventoryService;

    // Stack for rollback (LIFO)
    private Stack<String> rollbackStack = new Stack<>();

    public CancellationService(BookingHistory history, InventoryService inventoryService) {
        this.history = history;
        this.inventoryService = inventoryService;
    }

    public void cancelBooking(String reservationId) {

        Reservation r = history.getReservation(reservationId);

        // Validation
        if (r == null) {
            System.out.println("Cancellation Failed: Reservation does not exist.");
            return;
        }

        if (!r.isActive) {
            System.out.println("Cancellation Failed: Reservation already cancelled.");
            return;
        }

        // Step 1: Push room ID to rollback stack
        rollbackStack.push(r.roomId);

        // Step 2: Restore inventory
        inventoryService.increment(r.roomType);

        // Step 3: Mark reservation inactive
        r.isActive = false;

        // Step 4: Confirmation
        System.out.println("Booking Cancelled Successfully for " + r.customerName +
                " | Room ID Released: " + r.roomId);
    }

    public void displayRollbackStack() {
        System.out.println("Rollback Stack (Recent Releases): " + rollbackStack);
    }
}

// Main Class
public class UseCase10BookingCancellation {

    public static void main(String[] args) {

        InventoryService inventoryService = new InventoryService();
        BookingHistory history = new BookingHistory();

        // Simulate confirmed bookings
        Reservation r1 = new Reservation("RES-101", "Alice", "Single", "S-001");
        Reservation r2 = new Reservation("RES-102", "Bob", "Double", "D-001");

        history.addReservation(r1);
        history.addReservation(r2);

        // Decrement inventory (simulate allocation)
        inventoryService.decrement("Single");
        inventoryService.decrement("Double");

        // Cancellation Service
        CancellationService cancelService = new CancellationService(history, inventoryService);

        // Perform cancellations
        cancelService.cancelBooking("RES-101"); // valid
        cancelService.cancelBooking("RES-101"); // already cancelled
        cancelService.cancelBooking("RES-999"); // invalid

        // Display system state
        inventoryService.displayInventory();
        cancelService.displayRollbackStack();
    }
}