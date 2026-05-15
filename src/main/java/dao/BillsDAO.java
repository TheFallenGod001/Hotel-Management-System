package dao;
import utility.BillIDGenerator;

import java.nio.file.*;
import java.io.*;

import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.function.*;

public class BillsDAO{
    private BillIDGenerator generator;

    private File billFolder;
    private String billID;

    public BillsDAO(){
        this.generator = new BillIDGenerator();
        this.billFolder = new File("data/Bills");
        this.syncWithExistingFiles(); 
    }
    public BillsDAO(File folder){
        this.generator = new BillIDGenerator();
        this.billFolder = folder;
        this.syncWithExistingFiles();
    }
    public BillsDAO(File folder, int lastCount){
        this.generator = new BillIDGenerator(lastCount);
        this.billFolder = folder;
        this.syncWithExistingFiles();
    }
    public BillsDAO(int lastCount){
        this.generator = new BillIDGenerator(lastCount);
        this.billFolder = new File("data/Bills");
        this.syncWithExistingFiles();
    }

    private void syncWithExistingFiles() {
        if (!this.billFolder.exists()) {
            this.generateNewID();
            return;
        }

        int maxId = 0;
        File[] files = this.billFolder.listFiles((dir, name) -> name.endsWith(".txt"));
        
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                String idStr = fileName.substring(0, fileName.lastIndexOf('.')); 
                
                String[] parts = idStr.split("-");
                if (parts.length > 0) {
                    try {
                        int currentId = Integer.parseInt(parts[parts.length - 1]);
                        if (currentId > maxId) {
                            maxId = currentId;
                        }
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }
        
        this.generator.setLastCount(maxId);

        this.generateNewID(); 
    }

    public void writeInvoice(String invoice){
        if(!this.billFolder.exists()){
            this.billFolder.mkdirs();
        }

        File billFile = new File(this.billFolder, this.billID + ".txt");
        if(billFile.exists()) throw new IllegalStateException("Bill with this ID already exists... " + this.billID);

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(billFile))){
            writer.write(invoice);
        }
        catch(Exception e) { e.printStackTrace(); }
    }
    public ArrayList<String> readAllBills(){
        ArrayList<String> result = new ArrayList<>();
        try(Stream<Path> paths = Files.walk(this.billFolder.toPath())){
            paths.filter(Files::isRegularFile)
            .forEach(path -> {
                try{
                    result.add(Files.readString(path));
                }
                catch(Exception e) { e.printStackTrace(); }
            });
        }
        catch(Exception e) { e.printStackTrace(); }

        return result;
    }

    public ArrayList<String> queryBills(
        Predicate<String> filterCondition
    ){
        ArrayList<String> result = new ArrayList<>();
        if (!this.billFolder.exists()) {
            return result; 
        }
        try(Stream<Path> paths = Files.walk(this.billFolder.toPath())){
            paths.filter(Files::isRegularFile)
            .forEach(path -> {
                try(Stream<String> lines = Files.lines(path)){
                    if(filterCondition.test(lines.findFirst().orElse(""))) result.add(Files.readString(path));
                }
                catch(Exception e) { e.printStackTrace(); }
            });
        }
        catch(Exception e) { e.printStackTrace(); }

        return result;
    }

    public String returnID(){
        return this.billID;
    }
    public void generateNewID(){
        this.billID = this.generator.generateID();
    }
    public void generateNewGroupID(){
        this.billID = "GRP-" + this.generator.generateID();
    }
}
