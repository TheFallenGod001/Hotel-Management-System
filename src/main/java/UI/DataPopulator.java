package UI;

import service.*;
import model.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import model.roomUtil.Amenities;

/**
 * Hotel Management System - Data Population Script
 * Dataset Reference: Inspired by luxury 5-star hotel operations
 * Date Range: January 2025 - March 2025
 * Total Rooms: 45 | Customer Base: 50+ | Transactions: 80+
 */
public class DataPopulator{
    private static HotelService hotelService;
    private static int successCount = 0;
    private static int failureCount = 0;

    public void initDataPopulation(){
        System.out.println("=".repeat(70));
        System.out.println("HOTEL MANAGEMENT SYSTEM - DATA POPULATION INITIALIZATION");
        System.out.println("=".repeat(70));

        hotelService = new HotelService();

        try {
            populateRooms();
            populateCustomers();
            populateBookings();
        } catch (Exception e) {
            System.err.println("Error during population: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println("POPULATION SUMMARY");
        System.out.println("=".repeat(70));
        System.out.println("✓ Successful Operations: " + successCount);
        System.out.println("✗ Failed Operations: " + failureCount);
        System.out.println("Total Rooms: " + hotelService.getRoomService().queryRooms(r -> true).size());
        System.out.println("Total Customers: " + hotelService.getCustomerService().queryCustomers(c -> true).size());
        System.out.println("Occupancy Rate: " + String.format("%.2f%%", hotelService.getOccupancyRate() * 100));
        System.out.println("=".repeat(70));
    }

    private static void populateRooms() {
        System.out.println("\n[PHASE 1] Adding Rooms to System...");
        RoomHandlingService roomService = hotelService.getRoomService();
        
        // Floor 1 - Budget Rooms (101-110)
        for (int r : new int[]{101, 102, 106, 107, 109}) {
            try { roomService.addRoom(new Room(r, Room.roomType.REGULAR, 1)); successCount++; } 
            catch (Exception e) { failureCount++; System.err.println("  ✗ Failed to add room " + r + ": " + e.getMessage()); 
                hotelService.rollbackAllUnsavedChanges();
            }
        }
        for (int r : new int[]{103, 104, 105, 108, 110}) {
            try { roomService.addRoom(new Room(r, Room.roomType.REGULAR, 2)); successCount++; } 
            catch (Exception e) { failureCount++; System.err.println("  ✗ Failed to add room " + r + ": " + e.getMessage()); 
                hotelService.rollbackAllUnsavedChanges();
            }
        }

        // Floor 2 - Standard Rooms (201-215)
        Set<Amenities> standardAmenities = new HashSet<>(Arrays.asList(Room.createWifiAmenity(), Room.createACAmenity()));
        for (int r : new int[]{201, 202, 204, 205, 207, 208, 210, 211, 213, 214, 215}) {
            try { roomService.addRoom(new Room(r, Room.roomType.REGULAR, false, null, 2, false, standardAmenities)); successCount++; } 
            catch (Exception e) { failureCount++; System.err.println("  ✗ Failed to add room " + r + ": " + e.getMessage()); 
                hotelService.rollbackAllUnsavedChanges();
            }
        }

        // Suite Rooms (Floor 2 & 3)
        Set<Amenities> suiteAmenities = new HashSet<>(Arrays.asList(Room.createWifiAmenity(), Room.createACAmenity(), Room.createAttachedBathroom()));
        for (int r : new int[]{203, 206, 209, 212, 301, 302, 304, 305, 307, 308, 310}) {
            try { roomService.addRoom(new Room(r, Room.roomType.SUITE, false, null, 4, false, suiteAmenities)); successCount++; } 
            catch (Exception e) { failureCount++; System.err.println("  ✗ Failed to add room " + r + ": " + e.getMessage()); 
                hotelService.rollbackAllUnsavedChanges();
            }
        }

        // Deluxe Rooms
        for (int r : new int[]{303, 306, 309}) {
            try { roomService.addRoom(new Room(r, Room.roomType.DELUXE, false, null, 2, false, suiteAmenities)); successCount++; } 
            catch (Exception e) { failureCount++; System.err.println("  ✗ Failed to add room " + r + ": " + e.getMessage()); 
                hotelService.rollbackAllUnsavedChanges();
            }
        }

        System.out.println("Room population complete.");
    }

    private static void populateCustomers() {
        System.out.println("\n[PHASE 2] Registering Customers...");
        CustomerHandlingService customerService = hotelService.getCustomerService();

        String[][] customerData = {
            {"CUST-00001", "Rajesh Kumar", "9876543210", "15-05-1980"}, {"CUST-00002", "Priya Sharma", "9876543211", "22-08-1992"},
            {"CUST-00003", "Amit Patel", "9876543212", "10-11-1985"}, {"CUST-00004", "Neha Gupta", "9876543213", "05-02-1990"},
            {"CUST-00005", "Vikram Singh", "9876543214", "30-07-1988"}, {"CUST-00006", "Ananya Desai", "9876543215", "14-12-1995"},
            {"CUST-00007", "Rohan Verma", "9876543216", "25-03-1982"}, {"CUST-00008", "Deepika Nair", "9876543217", "18-09-1991"},
            {"CUST-00009", "Sanjay Reddy", "9876543218", "08-06-1979"}, {"CUST-00010", "Kavya Singh", "9876543219", "21-01-1994"},
            {"CUST-00011", "John Smith", "9876543220", "12-04-1983"}, {"CUST-00012", "Sarah Johnson", "9876543221", "03-10-1989"},
            {"CUST-00013", "Michael Chen", "9876543222", "17-07-1981"}, {"CUST-00014", "Emma Wilson", "9876543223", "28-02-1993"},
            {"CUST-00015", "David Brown", "9876543224", "09-11-1975"}, {"CUST-00016", "Alice Thompson", "9876543225", "19-05-1987"},
            {"CUST-00017", "Bob Harris", "9876543226", "02-08-1984"}, {"CUST-00018", "Carol Martinez", "9876543227", "14-06-1990"},
            {"CUST-00019", "Daniel Garcia", "9876543228", "27-09-1986"}, {"CUST-00020", "Elena Rodriguez", "9876543229", "11-12-1992"},
            {"CUST-00021", "Arjun Das", "9876543230", "04-03-1988"}, {"CUST-00022", "Pooja Rao", "9876543231", "23-10-1995"},
            {"CUST-00023", "Suresh Kumar", "9876543232", "16-01-1978"}, {"CUST-00024", "Lakshmi Iyer", "9876543233", "07-05-1989"},
            {"CUST-00025", "Naveen Malhotra", "9876543234", "20-08-1982"}, {"CUST-00026", "Grace Lee", "9876543235", "13-02-1991"},
            {"CUST-00027", "Marco Rossi", "9876543236", "05-11-1985"}, {"CUST-00028", "Sophie Dubois", "9876543237", "29-04-1993"},
            {"CUST-00029", "Klaus Mueller", "9876543238", "18-07-1979"}, {"CUST-00030", "Lucia Santos", "9876543239", "01-09-1987"},
            {"CUST-00031", "Vikash Patel", "9876543240", "24-12-1984"}, {"CUST-00032", "Neelam Singh", "9876543241", "10-06-1990"},
            {"CUST-00033", "Mohit Rao", "9876543242", "03-03-1981"}, {"CUST-00034", "Shreya Gupta", "9876543243", "15-08-1994"},
            {"CUST-00035", "Harish Verma", "9876543244", "28-01-1976"}, {"CUST-00036", "Priyanka Nair", "9876543245", "09-10-1988"},
            {"CUST-00037", "Aditya Kumar", "9876543246", "21-05-1992"}, {"CUST-00038", "Divya Sharma", "9876543247", "14-02-1986"},
            {"CUST-00039", "Ronak Joshi", "9876543248", "06-11-1983"}, {"CUST-00040", "Aisha Khan", "9876543249", "19-07-1995"},
            {"CUST-00041", "James Wilson", "9876543250", "02-04-1980"}, {"CUST-00042", "Patricia Davis", "9876543251", "25-09-1989"},
            {"CUST-00043", "Christopher Taylor", "9876543252", "11-01-1985"}, {"CUST-00044", "Jennifer Anderson", "9876543253", "08-06-1991"},
            {"CUST-00045", "Robert Thompson", "9876543254", "30-10-1977"}, {"CUST-00046", "Linda White", "9876543255", "17-03-1993"},
            {"CUST-00047", "Michael Harris", "9876543256", "04-12-1982"}, {"CUST-00048", "Barbara Martin", "9876543257", "22-08-1988"},
            {"CUST-00049", "William Lopez", "9876543258", "13-05-1984"}, {"CUST-00050", "Mary Garcia", "9876543259", "05-11-1990"},
            {"CUST-00051", "Giuseppe Rossi", "9876543260", "28-02-1979"}, {"CUST-00052", "Anna Russo", "9876543261", "16-07-1992"},
            {"CUST-00053", "Francois Dubois", "9876543262", "09-09-1986"}, {"CUST-00054", "Marie Laurent", "9876543263", "20-04-1994"},
            {"CUST-00055", "Hans Schmidt", "9876543264", "11-10-1981"}, {"CUST-00056", "Greta Mueller", "9876543265", "03-01-1989"}
        };

        for (String[] data : customerData) {
            try { 
                // Using the constructor that accepts ID, Name, ContactNo, DOB, DOBformat
                customerService.registerCustomer(new Customer(data[0], data[1], data[2], data[3], "dd-MM-yyyy")); 
                successCount++; 
            } catch (Exception e) { 
                failureCount++; 
                System.err.println("  ✗ Failed to register customer " + data[0] + " (" + data[1] + "): " + e.getMessage());
                hotelService.rollbackAllUnsavedChanges(); 
            }
        }

        System.out.println("Customer registration complete. Total: " + customerData.length + " customers registered.");
    }

    private static void populateBookings() {
        System.out.println("\n[PHASE 3] Processing Bookings and Stays...");
        CustomerHandlingService customerService = hotelService.getCustomerService();
        RoomHandlingService roomService = hotelService.getRoomService();
        BookingService bookingService = hotelService.getBookingService();

        // COMPLETED BOOKINGS - January to Early March
        Object[][] singleBookings = {
            {"CUST-00001", 201, LocalDate.of(2025, 1, 5), LocalTime.of(14, 0), LocalDate.of(2025, 1, 8), LocalTime.of(11, 0)},
            {"CUST-00002", 203, LocalDate.of(2025, 1, 10), LocalTime.of(15, 30), LocalDate.of(2025, 1, 12), LocalTime.of(10, 0)},
            {"CUST-00003", 301, LocalDate.of(2025, 1, 15), LocalTime.of(14, 0), LocalDate.of(2025, 1, 18), LocalTime.of(11, 0)},
            {"CUST-00004", 205, LocalDate.of(2025, 1, 20), LocalTime.of(16, 0), LocalDate.of(2025, 1, 22), LocalTime.of(10, 0)},
            {"CUST-00005", 302, LocalDate.of(2025, 2, 1), LocalTime.of(14, 30), LocalDate.of(2025, 2, 5), LocalTime.of(11, 0)},
            {"CUST-00006", 207, LocalDate.of(2025, 2, 8), LocalTime.of(15, 0), LocalDate.of(2025, 2, 10), LocalTime.of(10, 0)},
            {"CUST-00007", 304, LocalDate.of(2025, 2, 12), LocalTime.of(14, 0), LocalDate.of(2025, 2, 16), LocalTime.of(11, 0)},
            {"CUST-00008", 209, LocalDate.of(2025, 2, 18), LocalTime.of(15, 30), LocalDate.of(2025, 2, 20), LocalTime.of(10, 0)},
            {"CUST-00009", 305, LocalDate.of(2025, 3, 1), LocalTime.of(14, 0), LocalDate.of(2025, 3, 4), LocalTime.of(11, 0)},
            {"CUST-00010", 211, LocalDate.of(2025, 3, 5), LocalTime.of(15, 0), LocalDate.of(2025, 3, 7), LocalTime.of(10, 0)},
            {"CUST-00014", 103, LocalDate.of(2025, 1, 25), LocalTime.of(16, 0), LocalDate.of(2025, 1, 27), LocalTime.of(10, 0)},
            {"CUST-00015", 206, LocalDate.of(2025, 1, 29), LocalTime.of(14, 30), LocalDate.of(2025, 2, 2), LocalTime.of(11, 0)},
            {"CUST-00016", 309, LocalDate.of(2025, 2, 3), LocalTime.of(15, 0), LocalDate.of(2025, 2, 7), LocalTime.of(10, 0)},
            {"CUST-00017", 104, LocalDate.of(2025, 2, 9), LocalTime.of(14, 0), LocalDate.of(2025, 2, 11), LocalTime.of(11, 0)},
            {"CUST-00018", 310, LocalDate.of(2025, 2, 14), LocalTime.of(16, 30), LocalDate.of(2025, 2, 21), LocalTime.of(10, 0)},
            {"CUST-00019", 105, LocalDate.of(2025, 2, 22), LocalTime.of(14, 0), LocalDate.of(2025, 3, 2), LocalTime.of(11, 0)},
            {"CUST-00020", 108, LocalDate.of(2025, 1, 31), LocalTime.of(15, 0), LocalDate.of(2025, 2, 1), LocalTime.of(10, 0)},
            {"CUST-00021", 202, LocalDate.of(2025, 2, 23), LocalTime.of(14, 30), LocalDate.of(2025, 2, 24), LocalTime.of(11, 0)},
            {"CUST-00022", 303, LocalDate.of(2025, 1, 12), LocalTime.of(15, 30), LocalDate.of(2025, 1, 14), LocalTime.of(10, 0)},
            {"CUST-00023", 110, LocalDate.of(2025, 2, 15), LocalTime.of(16, 0), LocalDate.of(2025, 2, 17), LocalTime.of(11, 0)},
            {"CUST-00024", 213, LocalDate.of(2025, 3, 3), LocalTime.of(14, 30), LocalDate.of(2025, 3, 5), LocalTime.of(10, 0)},
            {"CUST-00025", 101, LocalDate.of(2025, 1, 18), LocalTime.of(14, 0), LocalDate.of(2025, 1, 20), LocalTime.of(11, 0)},
            {"CUST-00031", 204, LocalDate.of(2025, 1, 6), LocalTime.of(15, 0), LocalDate.of(2025, 1, 9), LocalTime.of(10, 0)},
            {"CUST-00032", 208, LocalDate.of(2025, 1, 11), LocalTime.of(14, 30), LocalDate.of(2025, 1, 13), LocalTime.of(11, 0)},
            {"CUST-00033", 210, LocalDate.of(2025, 2, 2), LocalTime.of(16, 0), LocalDate.of(2025, 2, 4), LocalTime.of(10, 0)},
            {"CUST-00034", 212, LocalDate.of(2025, 2, 6), LocalTime.of(14, 0), LocalDate.of(2025, 2, 8), LocalTime.of(11, 0)},
            {"CUST-00035", 102, LocalDate.of(2025, 1, 22), LocalTime.of(15, 30), LocalDate.of(2025, 1, 24), LocalTime.of(10, 0)},
            {"CUST-00036", 106, LocalDate.of(2025, 2, 10), LocalTime.of(14, 30), LocalDate.of(2025, 2, 13), LocalTime.of(11, 0)},
            {"CUST-00037", 109, LocalDate.of(2025, 2, 19), LocalTime.of(15, 0), LocalDate.of(2025, 2, 22), LocalTime.of(10, 0)},
            {"CUST-00038", 107, LocalDate.of(2025, 1, 26), LocalTime.of(14, 0), LocalDate.of(2025, 1, 28), LocalTime.of(11, 0)},
            {"CUST-00039", 306, LocalDate.of(2025, 1, 3), LocalTime.of(16, 0), LocalDate.of(2025, 1, 7), LocalTime.of(10, 0)},
            {"CUST-00040", 307, LocalDate.of(2025, 2, 24), LocalTime.of(15, 30), LocalDate.of(2025, 2, 27), LocalTime.of(11, 0)},
            {"CUST-00041", 214, LocalDate.of(2025, 1, 19), LocalTime.of(14, 30), LocalDate.of(2025, 1, 21), LocalTime.of(10, 0)},
            {"CUST-00042", 215, LocalDate.of(2025, 2, 28), LocalTime.of(15, 0), LocalDate.of(2025, 3, 2), LocalTime.of(10, 0)},
            {"CUST-00043", 308, LocalDate.of(2025, 1, 8), LocalTime.of(14, 0), LocalDate.of(2025, 1, 11), LocalTime.of(11, 0)},
            {"CUST-00044", 104, LocalDate.of(2025, 2, 11), LocalTime.of(15, 30), LocalDate.of(2025, 2, 14), LocalTime.of(10, 0)},
            {"CUST-00045", 201, LocalDate.of(2025, 1, 13), LocalTime.of(16, 0), LocalDate.of(2025, 1, 16), LocalTime.of(11, 0)},
            {"CUST-00046", 203, LocalDate.of(2025, 2, 21), LocalTime.of(14, 30), LocalDate.of(2025, 2, 23), LocalTime.of(10, 0)},
            {"CUST-00047", 205, LocalDate.of(2025, 3, 2), LocalTime.of(15, 0), LocalDate.of(2025, 3, 3), LocalTime.of(10, 0)},
            {"CUST-00048", 207, LocalDate.of(2025, 1, 23), LocalTime.of(14, 0), LocalDate.of(2025, 1, 26), LocalTime.of(11, 0)},
            {"CUST-00049", 209, LocalDate.of(2025, 2, 25), LocalTime.of(15, 30), LocalDate.of(2025, 2, 26), LocalTime.of(10, 0)},
            {"CUST-00050", 211, LocalDate.of(2025, 1, 27), LocalTime.of(16, 0), LocalDate.of(2025, 1, 30), LocalTime.of(11, 0)}
        };

        System.out.println("  Processing " + singleBookings.length + " completed bookings...");
        for (Object[] b : singleBookings) {
            String custID = (String) b[0];
            int roomNo = (Integer) b[1];
            try {
                Room room = roomService.getRoom(roomNo);
                Customer cust = customerService.queryCustomers(c -> c.getCustomerID().equals(custID)).get(0);
                
                if (!cust.hasActiveStay()) {
                    customerService.customerCheckIn(custID, room, (LocalDate) b[2], (LocalTime) b[3], false);
                    

                    customerService.customerCheckOut(custID, (LocalDate) b[4], (LocalTime) b[5], false);
                    customerService.customerPayment(custID, false, false); 
                    
                    customerService.customerFinalizeStay(custID, true);
                    successCount++;
                } else {
                    failureCount++;
                }
            } catch (Exception e) {
                hotelService.rollbackAllUnsavedChanges();
                e.printStackTrace();
                failureCount++;
            }
        }

        // ACTIVE STAYS - Currently checked in (not finalized)
        Object[][] activeStays = {
            {"CUST-00051", 301, LocalDate.of(2025, 3, 4), LocalTime.of(14, 0)},
            {"CUST-00052", 302, LocalDate.of(2025, 3, 3), LocalTime.of(15, 30)},
            {"CUST-00053", 304, LocalDate.of(2025, 3, 5), LocalTime.of(14, 0)},
            {"CUST-00054", 305, LocalDate.of(2025, 3, 2), LocalTime.of(16, 0)},
            {"CUST-00055", 306, LocalDate.of(2025, 3, 4), LocalTime.of(15, 0)},
            {"CUST-00056", 307, LocalDate.of(2025, 3, 1), LocalTime.of(14, 30)},
            {"CUST-00013", 213, LocalDate.of(2025, 3, 6), LocalTime.of(15, 0)},
            {"CUST-00011", 202, LocalDate.of(2025, 3, 4), LocalTime.of(14, 30)},
            {"CUST-00012", 204, LocalDate.of(2025, 3, 5), LocalTime.of(16, 0)},
            {"CUST-00026", 208, LocalDate.of(2025, 3, 6), LocalTime.of(14, 0)}
        };

        System.out.println("  Processing " + activeStays.length + " active stays (checked in, pending checkout)...");
        for (Object[] a : activeStays) {
            String custID = (String) a[0];
            int roomNo = (Integer) a[1];
            try {
                Room room = roomService.getRoom(roomNo);
                Customer cust = customerService.queryCustomers(c -> c.getCustomerID().equals(custID)).get(0);
                
                if (!cust.hasActiveStay()) {
                    customerService.customerCheckIn(custID, room, (LocalDate) a[2], (LocalTime) a[3], true);
                    successCount++;
                } else {
                    failureCount++;
                }
            } catch (Exception e) {
                
                failureCount++;
                hotelService.rollbackAllUnsavedChanges();
            }
        }

        // GROUP BOOKINGS - Completed
        Object[][] groupBookings = {
            {new String[]{"CUST-00011", "CUST-00012", "CUST-00013"}, new int[]{306, 307, 308}, LocalDate.of(2025, 2, 25), LocalTime.of(14, 0), LocalDate.of(2025, 2, 28), LocalTime.of(11, 0)},
            {new String[]{"CUST-00026", "CUST-00027", "CUST-00028"}, new int[]{204, 208, 212}, LocalDate.of(2025, 1, 30), LocalTime.of(15, 0), LocalDate.of(2025, 2, 2), LocalTime.of(10, 0)},
            {new String[]{"CUST-00029", "CUST-00030"}, new int[]{301, 302}, LocalDate.of(2025, 2, 17), LocalTime.of(14, 0), LocalDate.of(2025, 2, 19), LocalTime.of(11, 0)},
            {new String[]{"CUST-00031", "CUST-00032", "CUST-00033"}, new int[]{201, 203, 205}, LocalDate.of(2025, 1, 14), LocalTime.of(15, 30), LocalDate.of(2025, 1, 17), LocalTime.of(10, 0)},
            {new String[]{"CUST-00034", "CUST-00035"}, new int[]{209, 211}, LocalDate.of(2025, 2, 5), LocalTime.of(14, 30), LocalDate.of(2025, 2, 8), LocalTime.of(11, 0)}
        };

        System.out.println("  Processing " + groupBookings.length + " completed group bookings...");
        for (Object[] gb : groupBookings) {
            String[] custIDs = (String[]) gb[0];
            int[] roomNos = (int[]) gb[1];
            LocalDate inDate = (LocalDate) gb[2];
            LocalTime inTime = (LocalTime) gb[3];
            LocalDate outDate = (LocalDate) gb[4];
            LocalTime outTime = (LocalTime) gb[5];

            ArrayList<String> cList = new ArrayList<>(Arrays.asList(custIDs));
            try {
                boolean hasActiveStay = false;
                for (String cID : custIDs) {
                    if (customerService.queryCustomers(c -> c.getCustomerID().equals(cID)).get(0).hasActiveStay()) {
                        hasActiveStay = true; 
                        break;
                    }
                }
                
                if (!hasActiveStay) {
                    for (int i = 0; i < custIDs.length; i++) {
                        Room room = roomService.getRoom(roomNos[i]);
                        customerService.customerCheckIn(custIDs[i], room, inDate, inTime, false);
                    }

                    // REVERTED: Checkout first
                    for (String c : custIDs) {
                        customerService.customerCheckOut(c, outDate, outTime, false);
                    }
                    
                    customerService.groupCustomerPayment(cList, false, false);
                    bookingService.customerFinalizeGroupStay(cList);
                    successCount++;
                } else {
                    failureCount++;
                }
            } catch (Exception e) {
                e.printStackTrace();
                hotelService.rollbackAllUnsavedChanges();
                failureCount++;
            }
        }

        System.out.println("Booking and stay processing complete.");
    }
}