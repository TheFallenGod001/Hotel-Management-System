package utility;

public class CustomerIDGenerator implements ID_Generator{
    static int lastCount = 0;

    public CustomerIDGenerator(){}
    public CustomerIDGenerator(int lastCount) {CustomerIDGenerator.lastCount = lastCount;}

    public String generateID(){
        synchronized(CustomerIDGenerator.class) {
            return "CUST" + "-" + String.format("%05d", CustomerIDGenerator.lastCount++);
        }
    }

    public void setLastCount(int lastCount){
        synchronized(CustomerIDGenerator.class) {
            CustomerIDGenerator.lastCount = lastCount;
        }
    }
}