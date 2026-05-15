package utility;
import model.Payment;

public class PaymentIDGenerator implements ID_Generator{
    static int lastCount[] = new int[Payment.PaymentType.values().length];
    Payment.PaymentType currType;

    public PaymentIDGenerator(){
        this.currType = Payment.PaymentType.UPI;
    }
    public PaymentIDGenerator(Payment.PaymentType type){
        this.currType = type;
    }

    public void setType(Payment.PaymentType type){
        this.currType = type;
    }

    public synchronized String generateID(Payment.PaymentType type){
        return type.name() + "-" + String.format("%05d", PaymentIDGenerator.lastCount[type.ordinal()]++);
    }
    public String generateID(){
        return generateID(this.currType);
    }

    public void setLastCount(int lastCount){
        setLastCount(lastCount, this.currType);
    }
    public synchronized void setLastCount(int lastCount, Payment.PaymentType type){
        PaymentIDGenerator.lastCount[type.ordinal()] = lastCount;
    }
}