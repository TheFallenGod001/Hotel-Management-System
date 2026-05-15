package utility;

import java.time.LocalDate;

public class BillIDGenerator implements ID_Generator {
    static int lastCount;   

    public BillIDGenerator(){
        lastCount = 0;
    }
    public BillIDGenerator(int lastCount){
        BillIDGenerator.lastCount = lastCount;
    }

    public String generateID(){
        return "INV-" + LocalDate.now() + "-" + BillIDGenerator.lastCount++; 
    }
    public String generateID(LocalDate billDate){
        return "INV-" + billDate + "-" + BillIDGenerator.lastCount++;
    }
    public void setLastCount(int lastCount){
        BillIDGenerator.lastCount = lastCount;
    }
}

