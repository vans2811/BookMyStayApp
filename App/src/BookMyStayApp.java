import java.io.*;
import java.util.*;

// Reservation Model (Serializable)
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    String reservationId;
    String customerName;
    String roomType;

    public Reservation(String reservationId, String customerName, String roomType) {
        this.reservationId = reservationId;
        this.customerName = customerName;
        this.roomType = roomType;
    }

    @Override
    public String toString() {
        return reservationId + " | " + customerName + " | " + roomType;
    }
}

// System State (Serializable)
class SystemState implements Serializable {
    private static final long serialVersionUID = 1L;

    Map<String, Integer> inventory;
    List<Reservation> bookingHistory;

    public SystemState(Map<String, Integer> inventory, List<Reservation> bookingHistory) {
        this.inventory = inventory;
        this.bookingHistory = bookingHistory;
    }
}

// Persistence Service
class PersistenceService {

    private static final String FILE_NAME = "system_state.dat";

    // Save state to file
    public static void save(SystemState state) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            oos.writeObject(state);
            System.out.println("System state saved successfully.");

        } catch (IOException e) {
            System.out.println("Error saving system state: " + e.getMessage());
        }
    }

    // Load state from file
    public static SystemState load() {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_NAME))) {

            System.out.println("System state loaded successfully.");
            return (SystemState) ois.readObject();

        } catch (FileNotFoundException e) {
            System.out.println("No saved state found. Starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading state. Starting with safe defaults.");
        }

        return null;
    }
}

// Main Class
public class UseCase12DataPersistenceRecovery {

    public static void main(String[] args) {

        Map<String, Integer> inventory;
        List<Reservation> bookingHistory;

        // Step 1: Load persisted state (if exists)
        SystemState state = PersistenceService.load();

        if (state != null) {
            inventory = state.inventory;
            bookingHistory = state.bookingHistory;
        } else {
            // Default initialization
            inventory = new HashMap<>();
            inventory.put("Single", 2);
            inventory.put("Double", 1);
            inventory.put("Suite", 1);

            bookingHistory = new ArrayList<>();
        }

        // Step 2: Display current state
        System.out.println("\n--- Current System State ---");
        System.out.println("Inventory: " + inventory);
        System.out.println("Booking History: " + bookingHistory);

        // Step 3: Simulate new booking
        Reservation newBooking = new Reservation("RES-" + (bookingHistory.size() + 1),
                "Guest" + (bookingHistory.size() + 1), "Single");

        if (inventory.get("Single") > 0) {
            inventory.put("Single", inventory.get("Single") - 1);
            bookingHistory.add(newBooking);
            System.out.println("\nNew Booking Added: " + newBooking);
        } else {
            System.out.println("\nNo Single rooms available.");
        }

        // Step 4: Save updated state before shutdown
        SystemState newState = new SystemState(inventory, bookingHistory);
        PersistenceService.save(newState);

        System.out.println("\n--- Shutdown Complete ---");
    }
}