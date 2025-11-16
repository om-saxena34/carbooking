
package ok;
import java.util.*;

//Abstract superclass for all vehicles
abstract class Vehicle {
 protected String id;
 protected String brand;
 protected String model;
 protected double basePricePerDay;
 protected boolean isAvailable;
 protected int usageCount;  // for maintenance tracking

 public Vehicle(String id, String brand, String model, double basePricePerDay) {
     this.id = id;
     this.brand = brand;
     this.model = model;
     this.basePricePerDay = basePricePerDay;
     this.isAvailable = true;
     this.usageCount = 0;
 }

 public String getId() {
     return id;
 }

 public boolean isAvailable() {
     return isAvailable;
 }

 public void rent() {
     isAvailable = false;
 }

 public void returnVehicle() {
     isAvailable = true;
 }

 public void incrementUsage() {
     usageCount++;
 }

 public int getUsageCount() {
     return usageCount;
 }

 public double calculatePrice(int days) {
     return basePricePerDay * days;
 }

 public abstract String getType();

 public String getInfo() {
     return String.format("%s | %s %s | ID: %s | $%.2f/day | %s | Used: %d times",
         getType(), brand, model, id, basePricePerDay,
         (isAvailable ? "Available" : "Rented"), usageCount);
 }
}

//Car subclass
class Car extends Vehicle {
 public Car(String id, String brand, String model, double basePricePerDay) {
     super(id, brand, model, basePricePerDay);
 }

 @Override
 public String getType() {
     return "Car";
 }
}

//Bike subclass
class Bike extends Vehicle {
 public Bike(String id, String brand, String model, double basePricePerDay) {
     super(id, brand, model, basePricePerDay);
 }

 @Override
 public String getType() {
     return "Bike";
 }
}

//Truck subclass
class Truck extends Vehicle {
 public Truck(String id, String brand, String model, double basePricePerDay) {
     super(id, brand, model, basePricePerDay);
 }

 @Override
 public String getType() {
     return "Truck";
 }
}

//Customer class
class Customer {
 private String id;
 private String name;
 private int loyaltyPoints;

 public Customer(String id, String name) {
     this.id = id;
     this.name = name;
     this.loyaltyPoints = 0;
 }

 public String getId() {
     return id;
 }

 public String getName() {
     return name;
 }

 public int getLoyaltyPoints() {
     return loyaltyPoints;
 }

 public void addLoyaltyPoints(int pts) {
     loyaltyPoints += pts;
 }
}

//Rental class
class Rental {
 private Vehicle vehicle;
 private Customer customer;
 private int days;

 public Rental(Vehicle vehicle, Customer customer, int days) {
     this.vehicle = vehicle;
     this.customer = customer;
     this.days = days;
 }

 public Vehicle getVehicle() {
     return vehicle;
 }

 public Customer getCustomer() {
     return customer;
 }

 public int getDays() {
     return days;
 }
}

//Vehicle Rental System main logic
class VehicleRentalSystem {
 private List<Vehicle> vehicles = new ArrayList<>();
 private List<Customer> customers = new ArrayList<>();
 private List<Rental> rentals = new ArrayList<>();

 private Scanner scanner = new Scanner(System.in);

 private final String ADMIN_PASSWORD = "admin123";

 public void addVehicle(Vehicle v) {
     vehicles.add(v);
 }

 public Customer getOrCreateCustomer(String name) {
     for (Customer c : customers) {
         if (c.getName().equalsIgnoreCase(name)) {
             return c;
         }
     }
     Customer c = new Customer("CUST" + (customers.size() + 1), name);
     customers.add(c);
     return c;
 }

 public void rentVehicleFlow() {
     System.out.println("\n===== Rent Vehicle =====");
     System.out.print("Enter your name: ");
     String name = scanner.nextLine().trim();
     if (name.isEmpty()) {
         System.out.println("Name cannot be empty. Returning to main menu.");
         return;
     }
     Customer cust = getOrCreateCustomer(name);

     System.out.println("\nAvailable Vehicles:");
     boolean any = false;
     for (Vehicle v : vehicles) {
         if (v.isAvailable()) {
             System.out.println("  - " + v.getInfo());
             any = true;
         }
     }
     if (!any) {
         System.out.println("No vehicles available right now.");
         return;
     }

     System.out.print("\nEnter Vehicle ID to rent: ");
     String vid = scanner.nextLine().trim();
     Vehicle sel = null;
     for (Vehicle v : vehicles) {
         if (v.getId().equalsIgnoreCase(vid) && v.isAvailable()) {
             sel = v;
             break;
         }
     }
     if (sel == null) {
         System.out.println("Invalid selection or vehicle not available.");
         return;
     }
     System.out.print("Enter number of days to rent: ");
     int days;
     try {
         days = Integer.parseInt(scanner.nextLine().trim());
         if (days <= 0) {
             System.out.println("Rental days must be positive.");
             return;
         }
     } catch (NumberFormatException e) {
         System.out.println("Invalid input for days.");
         return;
     }
     double price = sel.calculatePrice(days);
     System.out.printf("Total price: $%.2f%n", price);

     System.out.print("Confirm rental? (Y/N): ");
     String ans = scanner.nextLine().trim();
     if (ans.equalsIgnoreCase("Y")) {
         sel.rent();
         sel.incrementUsage();
         rentals.add(new Rental(sel, cust, days));
         cust.addLoyaltyPoints(days * 10);
         System.out.println("Vehicle rented successfully! You earned " + (days*10) + " loyalty points.");
     } else {
         System.out.println("Rental cancelled.");
     }
 }

 public void returnVehicleFlow() {
     System.out.println("\n===== Return Vehicle =====");
     System.out.print("Enter Vehicle ID to return: ");
     String vid = scanner.nextLine().trim();
     Rental found = null;
     for (Rental r : rentals) {
         if (r.getVehicle().getId().equalsIgnoreCase(vid)) {
             found = r;
             break;
         }
     }
     if (found == null) {
         System.out.println("This vehicle was not rented.");
         return;
     }
     Vehicle v = found.getVehicle();
     v.returnVehicle();
     rentals.remove(found);
     System.out.println("Vehicle returned successfully by " + found.getCustomer().getName() + ".");
 }

 public void showAllVehicles() {
     System.out.println("\n===== All Vehicles =====");
     for (Vehicle v : vehicles) {
         System.out.println("  - " + v.getInfo());
     }
 }

 public void showRentals() {
     System.out.println("\n===== Current Rentals =====");
     if (rentals.isEmpty()) {
         System.out.println("No active rentals.");
     } else {
         for (Rental r : rentals) {
             System.out.println("  - Vehicle " + r.getVehicle().getId() + " rented by " +
                 r.getCustomer().getName() + " for " + r.getDays() + " days.");
         }
     }
 }

 public void showCustomers() {
     System.out.println("\n===== Customers & Loyalty Points =====");
     if (customers.isEmpty()) {
         System.out.println("No customers found.");
         return;
     }
     for (Customer c : customers) {
         System.out.println("  - " + c.getId() + ": " + c.getName() + " — Points: " + c.getLoyaltyPoints());
     }
 }

 public void maintenanceCheck() {
     System.out.println("\n===== Maintenance Check =====");
     boolean needsMaintenance = false;
     for (Vehicle v : vehicles) {
         if (v.getUsageCount() >= 5) {
             System.out.println("⚠ " + v.getInfo() + " needs maintenance.");
             needsMaintenance = true;
         }
     }
     if (!needsMaintenance) {
         System.out.println("No vehicles need maintenance at this time.");
     }
 }

 public void adminMenu() {
     while (true) {
         System.out.println("\n*** Admin Menu ***");
         System.out.println("1. Show all vehicles");
         System.out.println("2. Show current rentals");
         System.out.println("3. Show customers & loyalty points");
         System.out.println("4. Maintenance check");
         System.out.println("5. Logout to user menu");
         System.out.print("Enter choice: ");
         String ch = scanner.nextLine().trim();
         switch (ch) {
             case "1" -> showAllVehicles();
             case "2" -> showRentals();
             case "3" -> showCustomers();
             case "4" -> maintenanceCheck();
             case "5" -> {
                 System.out.println("Logging out to user menu...");
                 return;
             }
             default -> System.out.println("Invalid choice, please try again.");
         }
     }
 }

 public void mainMenu() {
     while (true) {
         System.out.println("\n===== Vehicle Rental System =====");
         System.out.println("1. Rent Vehicle");
         System.out.println("2. Return Vehicle");
         System.out.println("3. Admin Login");
         System.out.println("4. Exit");
         System.out.print("Enter choice: ");
         String choice = scanner.nextLine().trim();
         switch (choice) {
             case "1" -> rentVehicleFlow();
             case "2" -> returnVehicleFlow();
             case "3" -> {
                 System.out.print("Enter admin password: ");
                 String pw = scanner.nextLine();
                 if (ADMIN_PASSWORD.equals(pw)) {
                     adminMenu();
                 } else {
                     System.out.println("Incorrect password.");
                 }
             }
             case "4" -> {
                 System.out.println("Thank you for using the Vehicle Rental System. Goodbye!");
                 return;
             }
             default -> System.out.println("Invalid option, please enter 1-4.");
         }
     }
 }
}

//Main class
public class CarRentSystem {
 public static void main(String[] args) {
     VehicleRentalSystem system = new VehicleRentalSystem();

     // Adding vehicles
     system.addVehicle(new Car("C001", "Toyota", "Camry", 60.0));
     system.addVehicle(new Car("C002", "Honda", "Accord", 70.0));
     system.addVehicle(new Bike("B001", "Yamaha", "MT-07", 40.0));
     system.addVehicle(new Bike("B002", "KTM", "Duke", 45.0));
     system.addVehicle(new Truck("T001", "Volvo", "FH16", 150.0));

     // Run the menu
     system.mainMenu();
 }
}
