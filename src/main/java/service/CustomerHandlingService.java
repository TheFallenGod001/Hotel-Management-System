package service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import dao.*;
import model.*;
import java.util.function.*;

public class CustomerHandlingService {
    private CustomerDAO custData;
    private RecordsDAO recordsData;
    private RoomHandlingService roomService;
    private BillingService billing;

    public CustomerHandlingService(CustomerDAO custData, RecordsDAO recordsData, RoomHandlingService roomService, BillingService billing){
        this.custData = custData;
        this.recordsData = recordsData;
        this.roomService = roomService;
        this.billing = billing;
    }
    public boolean customerRegistered(String custID){
        return custData.getCustomerByID(custID) != null;
    }

    public ArrayList<StayRecord> getCustomerHistory(String custID){
        if(!customerRegistered(custID)){
            throw new IllegalArgumentException("This customer is not registerd: " + custID);
        }

        return recordsData.query(rec -> {return rec.getCustomerID().equals(custID);});
    }

    public void registerCustomer(Customer cust){
        if(customerRegistered(cust.getCustomerID())){
            throw new IllegalArgumentException("Customer registered already: " + cust.getCustomerID());
        }
        custData.addCustomer(cust);
        custData.appendCustomers();
    }
    public void unregisterCustomer(Customer cust){
        this.unregisterCustomer(cust.getCustomerID());
    }
    public void unregisterCustomer(String custID){
        Customer temp = custData.getCustomerByID(custID);
        if(temp != null){
            if(temp.hasActiveStay()) throw new IllegalStateException("Cant delete customer with active stay...");
        }
        custData.deleteCustomer(custID); //will do nothing if customer not registered
    }
    public void unregisterCustomers(ArrayList<String> custIDs){
        custData.deleteCustomers(custIDs);
    }   

    public double totalAmountSpent(String custID){
        if(!customerRegistered(custID)) throw new IllegalArgumentException("Customer not registered: " + custID);
        double spending = 0;
        for(double money : recordsData.query(rec -> {return rec.getCustomerID().equals(custID);}, rec -> {return rec.getPaymentAmt();})){
            spending += money;
        }

        return spending;
    }
    public ArrayList<Customer> getActiveCustomers(){
        return this.custData.query(c -> c.hasActiveStay());
    }
    
    public ArrayList<Customer> getPendingPayments(){
        // Check records for any historical pending payments
        Set<String> result = new HashSet<>(this.recordsData.query(r -> r.getPaymentStatus() != Payment.PaymentStatus.SUCCESS, r -> r.getCustomerID()));
        
        // Check current active customers who have checked in but haven't paid yet
        ArrayList<Customer> activeUnpaid = custData.query(c -> c.getCheckInStatus() && !c.getPaidStatus());
        for(Customer c : activeUnpaid){
            result.add(c.getCustomerID());
        }

        return custData.fetchCustomersByID(new ArrayList<>(result));
    }

    public void customerCheckIn(String custID, Room room, boolean write){
        this.customerCheckIn(custID, c -> c.checkIn(room), write);
    }
    public void customerCheckIn(String custID, Room room){
        this.customerCheckIn(custID, room, true);
    }
    public void customerCheckIn(String custID, Room room, LocalDate checkInDate, boolean write){
        this.customerCheckIn(custID, c -> c.checkIn(checkInDate, room) , write);
    }
    public void customerCheckIn(String custID, Room room, LocalDate checkInDate){
        this.customerCheckIn(custID, room, checkInDate, true);
    }
    public void customerCheckIn(String custID, Room room, LocalDate checkInDate, LocalTime checkInTime, boolean write){
        this.customerCheckIn(custID, c -> c.checkIn(checkInDate, checkInTime, room), write);
    }
    public void customerCheckIn(String custID, Room room, LocalDate checkInDate, LocalTime checkInTime){
        this.customerCheckIn(custID, room, checkInDate, checkInTime, true);
    }
    private void customerCheckIn(String custID, Consumer<Customer> checkInMethod,  boolean write){
        Customer cust = custData.getCustomerByID(custID);
        if(cust == null) throw new IllegalStateException("Customer is not registered: " + custID);
        checkInMethod.accept(cust);
        roomService.roomCheckIn(cust.getCurrStay().getRoomNumber(), cust, write);
        custData.updateCustomer(cust);

        if(write) this.custData.writeCustomers();
    }

    public void customerCheckOut(String custID, boolean write){
        this.customerCheckOut(custID, c -> c.checkOut(), write);
    }
    public void customerCheckOut(String custID){
        this.customerCheckOut(custID, true);
    }
    public void customerCheckOut(String custID, LocalDate checkOutDate, boolean write){
        this.customerCheckOut(custID, c -> c.checkOut(checkOutDate), write);
    }
    public void customerCheckOut(String custID, LocalDate checkOutDate){
        this.customerCheckOut(custID, checkOutDate, true);
    }
    public void customerCheckOut(String custID, LocalDate checkOutDate, LocalTime checkOutTime, boolean write){
        this.customerCheckOut(custID, c -> c.checkOut(checkOutDate, checkOutTime), write);
    }
    public void customerCheckOut(String custID, LocalDate checkOutDate, LocalTime checkOutTime){
        this.customerCheckOut(custID, checkOutDate, checkOutTime, true);
    }
    private void customerCheckOut(String custID, Consumer<Customer> checkOutMethod, boolean write){
        Customer cust = custData.getCustomerByID(custID);
        if(cust == null) throw new IllegalArgumentException("Customer does not exist: " + custID);
        checkOutMethod.accept(cust);
        roomService.roomCheckOut(cust.getCurrStay().getRoomNumber(), write);
        custData.updateCustomer(cust);

        if(write) this.custData.writeCustomers();
    }

    public void customerPayment(String custID){
        this.customerPayment(custID, false, true);
    }
    
    public String customerPayment(String custID, boolean returnInvoice){
        return this.customerPayment(custID, returnInvoice, true);
    }

    public String customerPayment(String custID, boolean returnInvoice, boolean write){
        Customer cust = custData.getCustomerByID(custID);
        if(cust == null) throw new IllegalArgumentException("Customer isnt registered: " + custID);
        
        String invoice = this.billing.processPayment(cust, roomService.getRoom(cust.getCurrStay().getRoomNumber()), returnInvoice);
        custData.updateCustomer(cust);
        
        if(write) custData.writeCustomers();
        
        return invoice;
    }
    
    public void groupCustomerPayment(ArrayList<String> custIDs) {
        this.groupCustomerPayment(custIDs, false, true);
    }

    public String groupCustomerPayment(ArrayList<String> custIDs, boolean returnInvoice) {
        return this.groupCustomerPayment(custIDs, returnInvoice, true);
    }

    public String groupCustomerPayment(ArrayList<String> custIDs, boolean returnInvoice, boolean write) {
        ArrayList<Customer> customers = new ArrayList<>();
        ArrayList<Room> rooms = new ArrayList<>();
        
        for (String custID : custIDs) {
            Customer cust = custData.getCustomerByID(custID);
            if (cust == null) throw new IllegalArgumentException("Customer isnt registered: " + custID);
            customers.add(cust);
            rooms.add(roomService.getRoom(cust.getCurrStay().getRoomNumber()));
        }
        
        String invoice = this.billing.processGroupPayment(customers, rooms, returnInvoice);
        custData.updateCustomers(customers);
        
        if(write) custData.writeCustomers();
        
        return invoice;
    }

    public void customerFinalizeStay(String custID, boolean write){
        Customer cust = custData.getCustomerByID(custID);

        if(cust == null) throw new IllegalArgumentException("Customer not registered: " + custID);
        if(!cust.getCheckInStatus()) throw new IllegalStateException("Customer not checked in: " + custID);
        if(!cust.getCheckOutStatus()) throw new IllegalStateException("Customer not checked out: " + custID);
        if(!cust.getPaidStatus()) throw new IllegalStateException("Customer yet to pay: " + custID);

        StayRecord completedStay = cust.finalizeStay();
        recordsData.addRecord(completedStay);
        recordsData.appendRecords();
        custData.updateCustomer(cust);

        if(write) custData.writeCustomers();
    }
    public void customerFinalizeStay(String custID){
        this.customerFinalizeStay(custID, true);
    }

    public <T> ArrayList<T> queryCustomers(Predicate<Customer> filterCondition, Function<Customer, T> mapper){
        return custData.query(filterCondition, mapper);
    }
    
    public ArrayList<Customer> queryCustomers(Predicate<Customer> filterCondition){
        return custData.query(filterCondition);
    }
    
    public <T> ArrayList<T> queryRecords(Predicate<StayRecord> filterCondition, Function<StayRecord, T> mapper){
        return recordsData.query(filterCondition, mapper);
    }
    
    public ArrayList<StayRecord> queryRecords(Predicate<StayRecord> filterCondition){
        return recordsData.query(filterCondition);
    }

    public ArrayList<Customer> getAllCustomers(){
        return custData.getCustomers();
    }
    public ArrayList<StayRecord> getAllRecords(){
        return recordsData.getRecords();
    }

    public void rollbackChanges() {
        custData.reload();
        recordsData.reload();
    }
}
