package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import model.Payment.PaymentStatus;
import model.Payment.PaymentType;
import utility.RecordIDGenerator;

public class StayRecord {
    private static RecordIDGenerator generator = new RecordIDGenerator();

    private String recordID;
    private String customerID;
    private Payment paymentRecord;
    private TimeInfo timeRecord;
    private int roomNumber;

    public StayRecord(){
        this.recordID = generator.generateID();
        this.customerID = "";
        this.paymentRecord = new Payment(0);
        this.timeRecord = new TimeInfo();
        this.roomNumber = -1;
    }
    public StayRecord(String recordID){
        this.recordID = recordID;
        this.customerID = "";
        this.paymentRecord = new Payment(0);
        this.timeRecord = new TimeInfo();
        this.roomNumber = -1;
    }
    public StayRecord(String customerRecord, Payment paymentRec, TimeInfo timeRecord, int roomRecord){
        if (paymentRec == null || timeRecord == null || roomRecord < 0) {
            throw new IllegalArgumentException("StayRecord fields cannot be null");
        }
        this.recordID = generator.generateID();
        this.paymentRecord = new Payment(paymentRec);
        this.timeRecord = new TimeInfo(timeRecord);
        this.roomNumber = roomRecord;
        this.customerID = customerRecord;
    }
    public StayRecord(Customer cust, Payment paymentRec, TimeInfo timeRecord, Room room){
        this(cust.getCustomerID(), paymentRec, timeRecord, room.getRoomNo());
    }
    public StayRecord(StayRecord other){
        this.recordID = other.recordID; 
        this.customerID = other.getCustomerID();
        this.paymentRecord = other.getPaymentRecord();
        this.roomNumber = other.getRoomNumber();
        this.timeRecord = other.getTimeRecord();
    }

    public void updatePayment(double amt, Payment.PaymentStatus status, Payment.PaymentType type){
        this.paymentRecord.setPaymentAmount(amt);
        this.paymentRecord.setPaymentStatus(status);
        this.paymentRecord.setPaymentType(type);
    }
    public void updatePayment(double amt, Payment.PaymentType type){
        this.paymentRecord.setPaymentAmount(amt);
        this.paymentRecord.setPaymentType(type);
    }
    public void updatePayment(double amt){
        this.paymentRecord.setPaymentAmount(amt);
    }

    public void updateFormat(String format, boolean date){
        this.timeRecord.updateFormat(format, date);
    }
    public void updateFormat(String dateFormat, String timeFormat){
        this.timeRecord.updateFormat(dateFormat, timeFormat);
    }
    public void setCheckIn(LocalDate checkInDate, LocalTime checkInTime){
        this.timeRecord.setCheckIn(checkInDate, checkInTime);
    }
    public void setCheckIn(LocalDate checkInDate){
        this.timeRecord.setCheckIn(checkInDate);
    }
    public void checkInNow(){
        this.timeRecord.checkInNow();
    }
    public void setCheckOut(LocalDate checkOutDate, LocalTime checkOutTime){
        this.timeRecord.setCheckOut(checkOutDate, checkOutTime);
    }
    public void setCheckOut(LocalDate checkOutDate){
        this.timeRecord.setCheckOut(checkOutDate);
    }
    public void checkOutNow(){
        this.timeRecord.checkOutNow();
    }

    public Payment.PaymentType getPaymentType(){
        return this.paymentRecord.getPaymentType();
    }
    public Payment.PaymentStatus getPaymentStatus(){
        return this.paymentRecord.getPaymentStatus();
    }
    public double getPaymentAmt(){
        return this.paymentRecord.getPaymentAmt();
    }
    public LocalDateTime getTimeStamp(){
        return this.paymentRecord.getTimeStamp();
    }
    public void setPaymentType(PaymentType type){
        this.paymentRecord.setPaymentType(type);
    }
    public void setPaymentAmount(double amt){
        this.paymentRecord.setPaymentAmount(amt);
    }
    public void setPaymentStatus(PaymentStatus status){
        this.paymentRecord.setPaymentStatus(status);
    }

    public Payment getPaymentRecord(){
        return new Payment(paymentRecord);
    }

    public TimeInfo getTimeRecord(){
        return new TimeInfo(timeRecord);
    }

    public int getRoomNumber(){
        return this.roomNumber;
    }
    public String getCustomerID(){
        return this.customerID;
    }
    public String getRecordID(){
        return this.recordID;
    }
    public void setCustomerID(String customerID){
        this.customerID = customerID;
    }
    public void setRoomNumber(int roomNo){
        this.roomNumber = roomNo;
    }

    public boolean validateStay(){
    return !(
        this.roomNumber < 1 ||
        this.customerID == null ||
        this.paymentRecord == null ||
        this.recordID == null ||
        this.timeRecord == null ||
        this.timeRecord.getCheckInDate() == null || this.timeRecord.getCheckInTime() == null ||
        this.timeRecord.getCheckOutDate() == null || this.timeRecord.getCheckOutTime() == null
      );
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.recordID);
        sb.append(",");
        sb.append(this.paymentRecord.toString());
        sb.append(",");
        sb.append(this.timeRecord.toString());
        sb.append(",");
        sb.append(this.roomNumber);
        sb.append(",");
        sb.append(this.customerID);
        return sb.toString();
    }
    public static StayRecord parse(String record){
        String temp[] = record.split(",", 5);
        StayRecord tempRec = new StayRecord(temp[0]);
        if (temp.length != 5) throw new IllegalArgumentException("Invalid record format: " + record);
        try{
            tempRec.paymentRecord = Payment.parsePayment(temp[1]);
            tempRec.timeRecord = TimeInfo.parseString(temp[2]);
            tempRec.roomNumber = Integer.parseInt(temp[3]);
            tempRec.customerID = temp[4];
        }
        catch(Exception e) { throw new IllegalArgumentException("Invalid record format: " + record); }
        return tempRec;
    }

    public static void syncID(int lastCount){
        generator.setLastCount(lastCount);
    }
}
