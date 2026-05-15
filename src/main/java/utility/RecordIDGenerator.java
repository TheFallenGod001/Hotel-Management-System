package utility;

public class RecordIDGenerator implements ID_Generator{
    static int lastCount = 0;

    public RecordIDGenerator(){}
    public RecordIDGenerator(int lastCount) {RecordIDGenerator.lastCount = lastCount;}

    public synchronized String generateID(){
        return "REC" + "-" + String.format("%05d", RecordIDGenerator.lastCount++);
    }

    public synchronized void setLastCount(int lastCount){
        RecordIDGenerator.lastCount = lastCount;
    }
}