package service;

import dao.*;
import java.util.ArrayList;
import java.time.LocalDate;

public class HotelService {
    private CustomerDAO customerDAO;
    private RoomDAO roomDAO;
    private RecordsDAO recordsDAO;
    private BillsDAO billsDAO;

    private CustomerHandlingService customerService;
    private RoomHandlingService roomService;
    private BillingService billingService;
    private BookingService bookingService;

    public HotelService() {
        initializeSystem();
    }

    private void initializeSystem() {
        this.customerDAO = new CustomerDAO();
        this.roomDAO = new RoomDAO();
        this.recordsDAO = new RecordsDAO();
        this.billsDAO = new BillsDAO();

        this.roomService = new RoomHandlingService(roomDAO, recordsDAO);
        this.billingService = new BillingService(billsDAO, 0.18); // 18% default tax
        this.customerService = new CustomerHandlingService(customerDAO, recordsDAO, roomService, billingService);
        
        this.bookingService = new BookingService(customerService, roomService, recordsDAO, billingService);
    }
    
    public BookingService getBookingService() {
        return bookingService;
    }

    public RoomHandlingService getRoomService() {
        return roomService;
    }

    public CustomerHandlingService getCustomerService() {
        return customerService;
    }

    public BillingService getBillingService() {
        return billingService;
    }

    public double getOccupancyRate(){
        int totalRooms = roomDAO.getRooms().size();
        if (totalRooms == 0) return 0.0;
        return (double) roomService.getOccupiedRooms().size() / totalRooms;
    }

    public double getTotalRevenue(LocalDate date){
        double totalRevenue = 0;
        ArrayList<model.StayRecord> records = recordsDAO.query(
            rec -> {
                java.time.LocalDateTime ts = rec.getTimeStamp();
                return ts != null && 
                       rec.getPaymentStatus() == model.Payment.PaymentStatus.SUCCESS && 
                       ts.toLocalDate().isEqual(date);
            }
        );
        for(model.StayRecord rec : records) {
            totalRevenue += rec.getPaymentAmt();
        }
        return totalRevenue;
    }
    public double getTotalRevenue(LocalDate startDate, LocalDate endDate){
        double totalRevenue = 0;
        ArrayList<model.StayRecord> records = recordsDAO.query(
            rec -> {
                java.time.LocalDateTime ts = rec.getTimeStamp();
                if (ts == null || rec.getPaymentStatus() != model.Payment.PaymentStatus.SUCCESS) return false;
                LocalDate recDate = ts.toLocalDate();
                return !recDate.isBefore(startDate) && !recDate.isAfter(endDate);
            }
        );
        for(model.StayRecord rec : records){
            totalRevenue += rec.getPaymentAmt();
        }

        return totalRevenue;
    }

    public String dailySummaryCheckInCount(){
        return this.dailySummaryCheckInCount(LocalDate.now());
    }
    public String dailySummaryCheckInCount(LocalDate date){
        ArrayList<model.StayRecord> records = recordsDAO.query(
            rec -> {
                LocalDate inDate = rec.getTimeRecord().getCheckInLocalDate();
                return inDate != null && inDate.isEqual(date);
            }
        );
        int checkInCount = records.size();

        for(model.Customer cust : customerService.getActiveCustomers()){
            LocalDate inDate = cust.getCurrStay().getTimeRecord().getCheckInLocalDate();
            if(inDate != null && inDate.isEqual(date)){
                checkInCount++;
            }
        }

        return "Check in count: " + checkInCount;
    }
    public String dailySummaryCheckOutCount(){
        return this.dailySummaryCheckOutCount(LocalDate.now());
    }
    public String dailySummaryCheckOutCount(LocalDate date){
        ArrayList<model.StayRecord> records = recordsDAO.query(
            rec -> {
                LocalDate outDate = rec.getTimeRecord().getCheckOutLocalDate();
                return outDate != null && outDate.isEqual(date);
            }
        );
        int checkOutCount = records.size();

        for(model.Customer cust : customerService.getActiveCustomers()){
            LocalDate outDate = cust.getCurrStay().getTimeRecord().getCheckOutLocalDate();
            if(outDate != null && outDate.isEqual(date)){
                checkOutCount++;
            }
        }

        return "Check out count: " + checkOutCount;
    }
    public String dailySummaryRevenue(){
        return this.dailySummaryRevenue(LocalDate.now());
    }
    public String dailySummaryRevenue(LocalDate date){
        return "Revenue: " + this.getTotalRevenue(date);
    }  
    
    public String dailySummary(){
        return String.format("%-25s | %-25s | %-25s\n", this.dailySummaryCheckInCount(), dailySummaryCheckOutCount(), dailySummaryRevenue());
    }
    public String dailySummary(LocalDate date){
        return String.format("%-25s | %-25s | %-25s\n", this.dailySummaryCheckInCount(date), dailySummaryCheckOutCount(date), dailySummaryRevenue(date));
    }

    public void rollbackAllUnsavedChanges() {
        getCustomerService().rollbackChanges();
        getRoomService().rollbackChanges();
    }
}
