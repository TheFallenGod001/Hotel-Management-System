package model;

import java.time.*;
import java.time.format.DateTimeFormatter;
import utility.CustomerIDGenerator;


public class Customer{
    private static CustomerIDGenerator generator = new CustomerIDGenerator();

    private String name;
    private String contactNumber;
    private String customerID;
    private LocalDate DOB;
    private StayRecord currLog;
    private boolean checkedIn, checkedOut, paid;

    public Customer(){
        this.customerID = generator.generateID();
        this.name = "";
        this.contactNumber = "";
        this.DOB = null;
        this.currLog = new StayRecord();
        currLog.setCustomerID(customerID);
        checkedIn = false;
        checkedOut = false;
        paid = false;
    }
    public Customer(String id){
        this.customerID = id;
        this.name = "";
        this.contactNumber = "";
        this.DOB = null;
        this.currLog = new StayRecord();
        this.currLog.setCustomerID(id);
        checkedIn = false;
        checkedOut = false;
        paid = false;
    }
    public Customer(String name, String contactNo, String DOB, String DOBformat){
        this.contactNumber = contactNo;
        this.customerID = generator.generateID();
        this.name = name;
        this.DOB = LocalDate.parse(DOB, DateTimeFormatter.ofPattern(DOBformat));
        currLog = new StayRecord();
        currLog.setCustomerID(this.customerID);
        checkedIn = false;
        checkedOut = false;
        paid = false;
    }
    public Customer(String id, String name, String contactNo, String DOB, String DOBformat){
        this.customerID = id;
        this.name = name;
        this.contactNumber = contactNo;
        this.DOB = LocalDate.parse(DOB, DateTimeFormatter.ofPattern(DOBformat));
        this.currLog = new StayRecord();
        this.currLog.setCustomerID(id);
        this.checkedIn = false;
        this.checkedOut = false;
        this.paid = false;
    }
    public Customer(String id, String name, String contactNo){
        this.customerID = id;
        this.name = name;
        this.contactNumber = contactNo;
        this.DOB = null;
        this.currLog = new StayRecord();
        this.currLog.setCustomerID(id);
        this.checkedIn = false;
        this.checkedOut = false;
        this.paid = false;
    }
    public Customer(Customer other){
        this.name = other.name;
        this.contactNumber = other.contactNumber;
        this.customerID = other.customerID;
        this.DOB = other.DOB;
        this.currLog = new StayRecord(other.currLog);
        this.checkedIn = other.checkedIn;
        this.checkedOut = other.checkedOut;
        this.paid = other.paid;
    }


    public void checkIn(Room room){
        if (this.currLog.getRoomNumber() != -1) {
            throw new IllegalStateException("Customer already checked in.");
        }
        this.currLog.setRoomNumber(room.getRoomNo());
        this.currLog.checkInNow();
        this.checkedIn = true;
    }
    public void checkIn(LocalDate checkInDate, Room room){
        if (this.checkedIn) {
            throw new IllegalStateException("Customer already checked in.");
        }
        this.currLog.setRoomNumber(room.getRoomNo());
        this.currLog.setCheckIn(checkInDate);
        this.checkedIn = true;
    }
    public void checkIn(LocalDate checkInDate, LocalTime checkInTime, Room room){
        if (this.checkedIn) {
            throw new IllegalStateException("Customer already checked in.");
        }
        this.currLog.setRoomNumber(room.getRoomNo());
        this.currLog.setCheckIn(checkInDate, checkInTime);
        this.checkedIn = true;
    }
    public void checkOut(){
        if (this.checkedOut) {
            throw new IllegalStateException("Customer already checked out.");
        }
        if (!this.checkedIn) {
            throw new IllegalStateException("Customer hasnt checked in yet.");
        }
        this.currLog.checkOutNow();
        this.checkedOut = true;
    }
    public void checkOut(LocalDate checkOutDate){
        if (this.checkedOut) {
            throw new IllegalStateException("Customer already checked out.");
        }
        if (!this.checkedIn) {
            throw new IllegalStateException("Customer hasnt checked in yet.");
        }
        this.currLog.setCheckOut(checkOutDate);
        this.checkedOut = true;
    }
    public void checkOut(LocalDate checkOutDate, LocalTime checkOutTime){
        if (this.checkedOut) {
            throw new IllegalStateException("Customer already checked out.");
        }
        if (!this.checkedIn) {
            throw new IllegalStateException("Customer hasnt checked in yet.");
        }
        this.currLog.setCheckOut(checkOutDate, checkOutTime);
        this.checkedOut = true;
    }
    public void setPayment(Payment payment){
        if(this.paid == true) { throw new IllegalStateException("Customer has already paid for the current stay..."); }
        this.currLog.setPaymentAmount(payment.getPaymentAmt());
        this.currLog.setPaymentType(payment.getPaymentType());
        this.currLog.setPaymentStatus(payment.getPaymentStatus());
        this.paid = true;
    }
    public StayRecord finalizeStay(){
        if (!(this.checkedIn && this.checkedOut && this.paid)) {
            throw new IllegalStateException("Stay incomplete, cant finalize.");
        }
        StayRecord completedStay = new StayRecord(this.currLog);
    
        this.currLog = new StayRecord();
        this.currLog.setCustomerID(this.customerID);
        this.checkedIn = false;
        this.checkedOut = false;
        this.paid = false;

        return completedStay;
    }

    public String getName(){
        return this.name;
    }
    public LocalDate getDOB(){
        return this.DOB;
    }
    public String getContact(){
        return this.contactNumber;
    }
    public String getCustomerID(){
        return this.customerID;
    }
    public StayRecord getCurrStay(){
        return new StayRecord(this.currLog);
    }
    public boolean getCheckInStatus(){
        return this.checkedIn;
    }
    public boolean getCheckOutStatus(){
        return this.checkedOut;
    }
    public boolean getPaidStatus(){
        return this.paid;
    }
    public boolean hasActiveStay(){
        return this.checkedIn && (!this.checkedOut);
    }


    public String toString(){
        StringBuilder sb = new StringBuilder();

        sb.append(this.customerID).append(",");
        sb.append(this.name).append(",");
        sb.append(this.contactNumber).append(",");
        
        if (this.DOB != null) {
            sb.append(DateTimeFormatter.ofPattern(TimeInfo.dateStoreFormat).format(this.DOB));
        }
        sb.append(",");

        sb.append(this.checkedIn).append(",");
        sb.append(this.checkedOut).append(",");
        sb.append(this.paid);

        if (this.currLog != null && this.currLog.getRoomNumber() != -1) {
            sb.append("|");
            sb.append(this.currLog.toString());
        }

        return sb.toString();

        }

    public String custInfo(){
        return this.customerID + "|" + this.name;
    }
   public static Customer parse(String storedData){
        try {
            String[] parts = storedData.split("\\|", 2);
            String customerPart = parts[0];

            String[] data = customerPart.split(",", 7);
            if (data.length != 7) {
                throw new IllegalArgumentException("Invalid customer data: " + customerPart);
            }

            Customer cust = new Customer(data[0]);
            cust.name = data[1];
            cust.contactNumber = data[2];

            if (!data[3].isEmpty()) {
                cust.DOB = LocalDate.parse(
                    data[3],
                    DateTimeFormatter.ofPattern(TimeInfo.dateStoreFormat)
                );
            } else {
                cust.DOB = null;
            }

            cust.checkedIn = Boolean.parseBoolean(data[4]);
            cust.checkedOut = Boolean.parseBoolean(data[5]);
            cust.paid = Boolean.parseBoolean(data[6]);

            if (parts.length == 2 && !parts[1].trim().isEmpty()) {
                cust.currLog = StayRecord.parse(parts[1]);
                cust.currLog.setCustomerID(cust.customerID);
            } else {
                cust.currLog = new StayRecord();
                cust.currLog.setCustomerID(cust.customerID);
            }

            return cust;
        } catch(Exception e){
            throw new IllegalArgumentException("Failed to parse Customer: " + storedData, e);
        }
        
    }

    public static void syncID(int lastCount){
        generator.setLastCount(lastCount);
    }
}
