import java.util.*;

// Reservation Model (from previous use case)
class Reservation {
    String reservationId;
    String customerName;
    String roomType;

    public Reservation(String reservationId, String customerName, String roomType) {
        this.reservationId = reservationId;
        this.customerName = customerName;
        this.roomType = roomType;
    }
}

// Add-On Service Model
class AddOnService {
    String serviceName;
    double cost;

    public AddOnService(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    @Override
    public String toString() {
        return serviceName + " (₹" + cost + ")";
    }
}

// Add-On Service Manager
class AddOnServiceManager {

    // Map: Reservation ID → List of Services
    private Map<String, List<AddOnService>> reservationServicesMap = new HashMap<>();

    // Add services to reservation
    public void addServices(String reservationId, List<AddOnService> services) {
        reservationServicesMap.putIfAbsent(reservationId, new ArrayList<>());
        reservationServicesMap.get(reservationId).addAll(services);
    }

    // Calculate total add-on cost
    public double calculateTotalCost(String reservationId) {
        double total = 0.0;

        List<AddOnService> services = reservationServicesMap.get(reservationId);
        if (services != null) {
            for (AddOnService service : services) {
                total += service.cost;
            }
        }
        return total;
    }

    // Display services for a reservation
    public void displayServices(String reservationId) {
        List<AddOnService> services = reservationServicesMap.get(reservationId);

        System.out.println("\nAdd-On Services for Reservation ID: " + reservationId);

        if (services == null || services.isEmpty()) {
            System.out.println("No services selected.");
            return;
        }

        for (AddOnService service : services) {
            System.out.println("- " + service);
        }

        System.out.println("Total Add-On Cost: ₹" + calculateTotalCost(reservationId));
    }
}

// Main Class
public class UseCase7AddOnServiceSelection {

    public static void main(String[] args) {

        // Sample Reservations (Assume already created in Use Case 6)
        Reservation r1 = new Reservation("RES-101", "Alice", "Single");
        Reservation r2 = new Reservation("RES-102", "Bob", "Double");

        // Add-On Services
        AddOnService wifi = new AddOnService("WiFi", 200);
        AddOnService breakfast = new AddOnService("Breakfast", 300);
        AddOnService airportPickup = new AddOnService("Airport Pickup", 800);

        AddOnServiceManager manager = new AddOnServiceManager();

        // Guest selects services
        manager.addServices(r1.reservationId, Arrays.asList(wifi, breakfast));
        manager.addServices(r2.reservationId, Arrays.asList(breakfast, airportPickup));

        // Display results
        manager.displayServices(r1.reservationId);
        manager.displayServices(r2.reservationId);

        // Demonstrate cost retrieval
        System.out.println("\nFinal Add-On Cost for " + r1.customerName + ": ₹" +
                manager.calculateTotalCost(r1.reservationId));

        System.out.println("Final Add-On Cost for " + r2.customerName + ": ₹" +
                manager.calculateTotalCost(r2.reservationId));
    }
}