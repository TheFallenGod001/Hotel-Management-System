package model;
import java.time.*;
import java.time.format.DateTimeFormatter;
import utility.PaymentIDGenerator;


public class Payment {
    private static PaymentIDGenerator generator = new PaymentIDGenerator();
    public enum PaymentType{
        UPI,
        CASH,
        CARD,
        NET_BANKING;

        public String getName(){
            switch(this){
                case UPI:
                    return "UPI";
                case CASH:
                    return "Cash";
                case CARD:
                    return "Card";
                case NET_BANKING:
                    return "Net Banking";
                default:
                    return "Unknown";
            }
        }
    }
    public enum PaymentStatus{
        PENDING(),
        SUCCESS(),
        FAILED();
        public String getName(){
            switch(this){
                case PENDING:
                    return "Pending";
                case SUCCESS:
                    return "Success";
                case FAILED:
                    return "Failed";
                default:
                    return "Unknown";
            }
        }
    }

    private String paymentID;
    private PaymentType type;
    private double paymentAmount;
    private PaymentStatus status;
    private LocalDateTime timeStamp;

    private Payment(){
        this.paymentAmount = 0;
    }
    public Payment(PaymentType type, double amt){
        paymentID = generator.generateID(type);
        timeStamp = LocalDateTime.now();
        this.status = PaymentStatus.PENDING; //will be updated by billing service 
        this.type = type;
        this.paymentAmount = amt;
    }
    public Payment(double amt){
        this(PaymentType.UPI, amt);
    }
    public Payment(Payment other){
        this.paymentAmount = other.paymentAmount;
        this.paymentID = other.paymentID;
        this.status = other.status;
        this.timeStamp = other.timeStamp;
        this.type = other.type;
    }

    public String getPaymentID(){
        return this.paymentID;
    }
    public PaymentType getPaymentType(){
        return this.type;
    }
    public PaymentStatus getPaymentStatus(){
        return this.status;
    }
    public double getPaymentAmt(){
        return this.paymentAmount;
    }
    public LocalDateTime getTimeStamp(){
        return this.timeStamp;
    }
    public void setPaymentType(PaymentType type){
        this.type = type;
    }
    public void setPaymentAmount(double amt){
        this.paymentAmount = amt;
    }
    public void setPaymentStatus(PaymentStatus status){
        this.status = status;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.paymentID + " ");
        sb.append(this.paymentAmount + " ");
        sb.append(this.type.name() + " ");
        sb.append(this.status.name() + " ");
        sb.append(this.getTimeStamp().format(DateTimeFormatter.ofPattern("dd-MM-yyyy|HH:mm:ss")));

        return sb.toString();
    }

    public static Payment parsePayment(String payment){
        Payment tempPayment = new Payment();
        String temp[] = payment.split(" ", 5);
        if (temp.length != 5) throw new IllegalArgumentException("Invalid Payment format : " + payment); 
        tempPayment.paymentID = temp[0];
        tempPayment.paymentAmount = Double.parseDouble(temp[1]);
        tempPayment.setPaymentType(PaymentType.valueOf(temp[2]));
        tempPayment.setPaymentStatus(PaymentStatus.valueOf(temp[3]));
        tempPayment.timeStamp = LocalDateTime.parse(temp[4], DateTimeFormatter.ofPattern("dd-MM-yyyy|HH:mm:ss"));

        return tempPayment;
    }

    public static void syncID(int lastCount){
        generator.setLastCount(lastCount);
    }
}
