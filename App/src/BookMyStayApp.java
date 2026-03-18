import java.util.*;

// Reservation Model
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

    // Calculate total cost
    public double calculateTotalCost(String reservationId) {
        double total = 0.0;

        List<AddOnService> services = reservationServicesMap.get(reservationId);
        if (services != null) {
            for (AddOnService s : services) {
                total += s.cost;
            }
        }
        return total;
    }

    // Display services
    public void displayServices(String reservationId) {
        List<AddOnService> services = reservationServicesMap.get(reservationId);

        System.out.println("\nServices for Reservation ID: " + reservationId);

        if (services == null || services.isEmpty()) {
            System.out.println("No services selected.");
            return;
        }

        for (AddOnService s : services) {
            System.out.println("- " + s);
        }

        System.out.println("Total Add-On Cost: ₹" + calculateTotalCost(reservationId));
    }
}

// Main Class (as requested)
public class BookMyStayApp {

    public static void main(String[] args) {

        // Sample reservations (from previous use case)
        Reservation r1 = new Reservation("RES-101", "Alice", "Single");
        Reservation r2 = new Reservation("RES-102", "Bob", "Double");

        // Add-on services
        AddOnService wifi = new AddOnService("WiFi", 200);
        AddOnService breakfast = new AddOnService("Breakfast", 300);
        AddOnService pickup = new AddOnService("Airport Pickup", 800);

        AddOnServiceManager manager = new AddOnServiceManager();

        // Guest selects services
        manager.addServices(r1.reservationId, Arrays.asList(wifi, breakfast));
        manager.addServices(r2.reservationId, Arrays.asList(breakfast, pickup));

        // Display results
        manager.displayServices(r1.reservationId);
        manager.displayServices(r2.reservationId);

        // Final cost output
        System.out.println("\nFinal Cost for " + r1.customerName + ": ₹" +
                manager.calculateTotalCost(r1.reservationId));

        System.out.println("Final Cost for " + r2.customerName + ": ₹" +
                manager.calculateTotalCost(r2.reservationId));
    }
}