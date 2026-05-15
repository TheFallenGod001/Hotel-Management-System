package service;
import model.*;
import dao.*;

import java.util.ArrayList;
import java.util.function.Predicate;

public class BookingService {
    private CustomerHandlingService custHandler;
    private RoomHandlingService roomHandler;
    private RecordsDAO recordData;
    private BillingService billing;
    
    public BookingService(CustomerHandlingService custData, RoomHandlingService roomHandler, RecordsDAO recordData, BillingService billing ){
        this.custHandler= custData;
        this.roomHandler = roomHandler;
        this.recordData = recordData;
        this.billing = billing;
    }
    
    public void walkInCheckIn(Customer cust, Room room){
        if(!custHandler.customerRegistered(cust.getCustomerID())) custHandler.registerCustomer(cust);
        if(!roomHandler.roomExists(room.getRoomNo())) throw new IllegalArgumentException("Room does not exist: " + room.getRoomNo());
        
        if(!roomHandler.isRoomAvailable(room.getRoomNo())) throw new IllegalStateException("Room is already booked: " + room.getRoomNo());
        
        custHandler.customerCheckIn(cust.getCustomerID(), room);
    }

    public void groupCheckIn(ArrayList<Customer> customers, ArrayList<Room> rooms){ //1 to 1 mapping btwn customers and rooms
        for(Customer c : customers){
            if(!custHandler.customerRegistered(c.getCustomerID())) custHandler.registerCustomer(c);
        }
        for(Room r : rooms){
            if(!roomHandler.isRoomAvailable(r.getRoomNo())) throw new IllegalArgumentException("Room is not available: " + r.getRoomNo());
        }
        if (customers.size() != rooms.size()) throw new IllegalArgumentException("Number of customers and rooms have to be same...");
        for(int i = 0; i < customers.size(); i++){
            custHandler.customerCheckIn(customers.get(i).getCustomerID(), rooms.get(i));
        }
    }

    public ArrayList<Room> fetchRoomsByPreference(Predicate<Room> preference){
        return roomHandler.queryRooms(preference);
    }
    public ArrayList<Integer> fetchRoomNosByPreference(Predicate<Room> preference){
        return roomHandler.queryRooms(preference, r -> r.getRoomNo());
    }
    public ArrayList<Room> fetchAvailableRoomsByPreference(Predicate<Room> preference){
        return roomHandler.queryRooms(preference.and(r -> !r.getFlag(true)));
    }
    public ArrayList<Integer> fetchAvailableRoomNosByPreference(Predicate<Room> preference){
        return roomHandler.queryRooms(preference.and(r -> !r.getFlag(true)), r -> r.getRoomNo());
    }

    public double preBookingEstimation(Room room, int expectedNights){
        return billing.getBillAmount(room, expectedNights);
    }

    public void customerCheckOut(String custID){
        custHandler.customerCheckOut(custID);
    }

    public void groupCustomerCheckOut(ArrayList<String> custIDs){
        for(String custID : custIDs){
            this.customerCheckOut(custID);
        }
    }

    public String customerPayment(String custID){
        return custHandler.customerPayment(custID, true);
    }

    public String groupCustomerPayment(ArrayList<String> custIDs){
        return custHandler.groupCustomerPayment(custIDs, true);
    }

    public void customerFinalizeStay(String custID){
        custHandler.customerFinalizeStay(custID);
    }
    public void customerFinalizeGroupStay(ArrayList<String> custID){
        for(String cust : custID){
            custHandler.customerFinalizeStay(cust);
        }
    }

    public String completeCheckOutProcess(String custID) {
        this.customerCheckOut(custID);
        String invoice = this.customerPayment(custID);
        this.customerFinalizeStay(custID);
        return invoice;
    }

    public String completeGroupCheckOutProcess(ArrayList<String> custIDs) {
        this.groupCustomerCheckOut(custIDs);
        String groupInvoice = this.groupCustomerPayment(custIDs);
        this.customerFinalizeGroupStay(custIDs);
        return groupInvoice;
    }

    public ArrayList<Customer> fetchActiveCustomers(){
        return this.custHandler.getActiveCustomers();
    }

    public ArrayList<Customer> fetchPendingPayments(){
        return this.custHandler.getPendingPayments();
    }

    public ArrayList<Customer> fetchCustomersByPreference(Predicate<Customer> filterCondition){
        return this.custHandler.queryCustomers(filterCondition);
    }

    public ArrayList<StayRecord> fetchRecords(Predicate<StayRecord> filterCondition){
        return this.recordData.query(filterCondition);
    }

    public ArrayList<String> fetchCustomerBills(Predicate<String> filterCondition){
        return this.billing.queryBills(filterCondition);
    }

    public ArrayList<String> fetchGroupBills(){
        return this.billing.fetchGroupBills();
    }

    public ArrayList<String> fetchGroupBillsByCustomer(String custID){
        return this.billing.queryBills(header -> header.contains("|GROUP|MULTI|") && header.contains(custID));
    }

    public ArrayList<String> fetchIndividualBillsByCustomer(String custID){
        return this.billing.queryBills(header -> !header.contains("|GROUP|MULTI|") && header.contains(custID));
    }

    public ArrayList<String> fetchAllBillsByCustomer(String custID){
        return this.billing.queryBills(header -> header.contains(custID));
    }
}
