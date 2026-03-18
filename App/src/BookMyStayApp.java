import java.util.*;

// Booking Request Model
class BookingRequest {
    String customerName;
    String roomType;

    public BookingRequest(String customerName, String roomType) {
        this.customerName = customerName;
        this.roomType = roomType;
    }
}

// Inventory Service (Thread-Safe)
class InventoryService {
    private Map<String, Integer> inventory = new HashMap<>();

    public InventoryService() {
        inventory.put("Single", 2);
        inventory.put("Double", 1);
        inventory.put("Suite", 1);
    }

    // Synchronized critical section
    public synchronized boolean allocateRoom(String roomType) {
        int count = inventory.getOrDefault(roomType, 0);

        if (count > 0) {
            inventory.put(roomType, count - 1);
            return true;
        }
        return false;
    }

    public synchronized void displayInventory() {
        System.out.println("Final Inventory: " + inventory);
    }
}

// Booking Processor (Runnable for Threads)
class BookingProcessor implements Runnable {

    private Queue<BookingRequest> requestQueue;
    private InventoryService inventoryService;
    private Set<String> allocatedRooms;

    public BookingProcessor(Queue<BookingRequest> requestQueue,
                            InventoryService inventoryService,
                            Set<String> allocatedRooms) {
        this.requestQueue = requestQueue;
        this.inventoryService = inventoryService;
        this.allocatedRooms = allocatedRooms;
    }

    @Override
    public void run() {
        while (true) {
            BookingRequest request;

            // Synchronize queue access
            synchronized (requestQueue) {
                if (requestQueue.isEmpty()) {
                    break;
                }
                request = requestQueue.poll();
            }

            processBooking(request);
        }
    }

    private void processBooking(BookingRequest request) {

        // Critical section for allocation
        synchronized (inventoryService) {

            boolean allocated = inventoryService.allocateRoom(request.roomType);

            if (allocated) {
                String roomId = generateRoomId(request.roomType);

                synchronized (allocatedRooms) {
                    allocatedRooms.add(roomId);
                }

                System.out.println(Thread.currentThread().getName() +
                        " -> Booking Confirmed: " + request.customerName +
                        " | " + request.roomType +
                        " | Room ID: " + roomId);

            } else {
                System.out.println(Thread.currentThread().getName() +
                        " -> Booking Failed (No Availability): " +
                        request.customerName + " | " + request.roomType);
            }
        }
    }

    private String generateRoomId(String roomType) {
        return roomType.substring(0, 2).toUpperCase() + "-" +
                UUID.randomUUID().toString().substring(0, 4);
    }
}

// Main Class
public class UseCase11ConcurrentBookingSimulation {

    public static void main(String[] args) {

        // Shared resources
        Queue<BookingRequest> requestQueue = new LinkedList<>();
        Set<String> allocatedRooms = new HashSet<>();
        InventoryService inventoryService = new InventoryService();

        // Simulate multiple booking requests
        requestQueue.add(new BookingRequest("Alice", "Single"));
        requestQueue.add(new BookingRequest("Bob", "Double"));
        requestQueue.add(new BookingRequest("Charlie", "Single"));
        requestQueue.add(new BookingRequest("David", "Suite"));
        requestQueue.add(new BookingRequest("Eve", "Suite")); // likely to fail

        // Create multiple threads (simulating users)
        Thread t1 = new Thread(new BookingProcessor(requestQueue, inventoryService, allocatedRooms), "Thread-1");
        Thread t2 = new Thread(new BookingProcessor(requestQueue, inventoryService, allocatedRooms), "Thread-2");
        Thread t3 = new Thread(new BookingProcessor(requestQueue, inventoryService, allocatedRooms), "Thread-3");

        // Start threads
        t1.start();
        t2.start();
        t3.start();

        // Wait for all threads to complete
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Final state
        inventoryService.displayInventory();
        System.out.println("Allocated Room IDs: " + allocatedRooms);
    }
}