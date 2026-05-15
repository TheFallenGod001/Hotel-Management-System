package dao;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.*;

import model.StayRecord;

public class RecordsDAO {
    File recordsFile;
    ArrayList<StayRecord> records;
    ArrayList<StayRecord> newRecords;
    boolean recordsRead;

    public RecordsDAO(){
        recordsFile = new File("data/Records.csv");
        recordsRead = false;
        records = new ArrayList<>();
        newRecords = new ArrayList<>();
        if (!recordsFile.exists()) {
            recordsFile.getParentFile().mkdirs();
            try{
                recordsFile.createNewFile();
            }
            catch(Exception e) { e.printStackTrace(); }
        }
        this.readRecords();
    }
    
    public RecordsDAO(String loc){
        recordsFile = new File(loc);
        recordsRead = false;
        records = new ArrayList<>();
        newRecords = new ArrayList<>();
        if (!recordsFile.exists()) {
            recordsFile.getParentFile().mkdirs();
            try{
                recordsFile.createNewFile();
            }
            catch(Exception e) { e.printStackTrace(); }
        }
        this.readRecords(); 
    }

    public void readRecords(){
        if(recordsRead) return;
        try(BufferedReader recordsReader = new BufferedReader(new FileReader(recordsFile))){
            records.clear(); //flushing old stored data
            String line;
            
            int maxRecordId = 0;
            int maxPaymentId = 0;
            
            while( (line = recordsReader.readLine()) != null){
                if(line.trim().isEmpty()) continue; // Skip blank lines
                try {
                    StayRecord rec = StayRecord.parse(line);
                    records.add(rec);
                    
                    String[] recParts = rec.getRecordID().split("-");
                    if (recParts.length > 1) {
                        int recNum = Integer.parseInt(recParts[1]);
                        if(recNum > maxRecordId) maxRecordId = recNum;
                    }
                    

                    String payIdStr = rec.getPaymentRecord().getPaymentID();
                    if (payIdStr != null && payIdStr.contains("-")) {
                        String[] payParts = payIdStr.split("-");
                        if (payParts.length > 1) {
                            int payNum = Integer.parseInt(payParts[1]);
                            if(payNum > maxPaymentId) maxPaymentId = payNum;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("WARNING: Skipped corrupted StayRecord -> " + line);
                }
            }
            recordsRead = true;
            
            StayRecord.syncID(maxRecordId+1);
            model.Payment.syncID(maxPaymentId+1);
        }
        catch(Exception e){e.printStackTrace();}
    }

    public void addRecord(StayRecord rec){     
        StayRecord copy = new StayRecord(rec);
        if(recordsRead) records.add(copy);
        newRecords.add(copy);
    }

    private void manipulateRecords(boolean append){
        if(newRecords.isEmpty()) return;
        try(BufferedWriter recordsWriter = new BufferedWriter(new FileWriter(this.recordsFile, append))){
            for(StayRecord rec : newRecords){
                recordsWriter.append(rec.toString());
                recordsWriter.newLine();
            }
            this.newRecords.clear();
        }
        catch(Exception e) {e.printStackTrace();}
    }
    public void flushWithNewRecords(){
        this.manipulateRecords(false);
        this.recordsRead = false;
    }
    public void appendRecords(){
        this.manipulateRecords(true);
    }

    public ArrayList<StayRecord> getRecords(){
        if(!recordsRead) this.readRecords();
        return new ArrayList<>(this.records);
    }

    public ArrayList<StayRecord> fetchRecordsByRecordID(ArrayList<String> ID){
        Set<String> idSet = new HashSet<>(ID);
        return this.query(rec -> idSet.contains(rec.getRecordID()));
    }
    public ArrayList<StayRecord> fetchRecordsByRoomNo(int roomNo){
        return this.query(rec -> { return rec.getRoomNumber() == roomNo; });
    }
    public ArrayList<StayRecord> fetchRecordsByCustomerID(String custID){
        return this.query(rec -> { return rec.getCustomerID().equals(custID); });
    }

    public <T> ArrayList<T> query(
        Predicate<StayRecord> filterCondition,
        Function<StayRecord, T> mapper
    ){
        ArrayList<T> result = new ArrayList<>();
        if(!recordsRead){ this.readRecords(); }
        for(StayRecord rec : records){
            if(filterCondition.test(rec)) result.add(mapper.apply(rec));
        }

        return result;
    }

    public ArrayList<StayRecord> query(
        Predicate<StayRecord> filterCondition
    ){
        ArrayList<StayRecord> result = new ArrayList<>();
        if(!recordsRead){ this.readRecords(); }
        for(StayRecord rec : records){
            if(filterCondition.test(rec)) result.add(new StayRecord(rec));
        }

        return result;
    }
    public void reload() {
        this.recordsRead = false;
        this.newRecords.clear();
        this.readRecords();
    }
}