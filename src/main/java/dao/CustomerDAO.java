package dao;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.*;

import model.Customer;

public class CustomerDAO {
    File customersFile;
    ArrayList<Customer> customers;
    ArrayList<Customer> newCustomers;
    boolean customersRead;

    public CustomerDAO(){
        customersFile = new File("data/Customers.csv");
        customersRead = false;
        customers = new ArrayList<>();
        newCustomers = new ArrayList<>();

        if (!customersFile.exists()) {
            customersFile.getParentFile().mkdirs();
            try {
                customersFile.createNewFile();
            } catch(Exception e) { throw new RuntimeException("Failed to create Customers file", e); }
        }
        this.readCustomers();
    }

    public CustomerDAO(String loc){
        customersFile = new File(loc);
        customersRead = false;
        customers = new ArrayList<>();
        newCustomers = new ArrayList<>();

        if (!customersFile.exists()) {
            customersFile.getParentFile().mkdirs();
            try {
                customersFile.createNewFile();
            } catch(Exception e) { throw new RuntimeException("Failed to create Customers file", e); }
        }
        this.readCustomers();
    }

    public void readCustomers(){
        if(customersRead) return;

        try(BufferedReader reader = new BufferedReader(new FileReader(customersFile))){
            customers.clear();
            String line;
            int maxId = 0;

            while((line = reader.readLine()) != null){
                if(line.trim().isEmpty()) continue; // Skip blank lines
                try {
                    Customer c = Customer.parse(line);
                    customers.add(c);
                    
                    String[] idParts = c.getCustomerID().split("-");
                    if (idParts.length > 1) {
                        int idNum = Integer.parseInt(idParts[1]);
                        if(idNum > maxId) maxId = idNum;
                    }
                } catch (Exception e) {
                    System.err.println("WARNING: Skipped corrupted Customer record -> " + line);
                }
            }

            customersRead = true;
            Customer.syncID(maxId+1);
        }
        catch(Exception e){ throw new RuntimeException("Failed to read from Customers file", e); }
    }

    public void addCustomer(Customer cust){
        Customer copy = new Customer(cust);
        if(customersRead) customers.add(copy);
        newCustomers.add(copy);
    }


    private void manipulateCustomers(boolean append){
        if(newCustomers.isEmpty()) return;

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(customersFile, append))){
            for(Customer cust : newCustomers){
                writer.append(cust.toString());
                writer.newLine();
            }
            newCustomers.clear();
        }
        catch(Exception e){ throw new RuntimeException("Failed to append to Customers file", e); }
    }

    public void flushWithNewCustomers(){
        manipulateCustomers(false);
        customersRead = false;
    }

    public void appendCustomers(){
        manipulateCustomers(true);
    }

    public void writeCustomers(){
        if(!customersRead) throw new IllegalStateException("Read records before writing...");
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(this.customersFile))){
            for(Customer customer : this.customers){
                writer.write(customer.toString());
                writer.newLine();
            }
        }
        catch(Exception e) { throw new RuntimeException("Failed to write to Customers file", e); }
    }

    public void deleteCustomer(String custID, boolean write){
        if(!customersRead) this.readCustomers();
        customers.removeIf(c -> c.getCustomerID().equals(custID));
        newCustomers.removeIf(c -> c.getCustomerID().equals(custID));

        if(write) this.writeCustomers();
    }
    public void deleteCustomer(String custID){
        this.deleteCustomer(custID, true);
    }
    public void deleteCustomers(ArrayList<String> customers){
        for(String custID : customers){
            this.deleteCustomer(custID, false);
        }

        this.writeCustomers();
    }

    public void updateCustomers(ArrayList<Customer> updatedCustomers){
        if(!customersRead) this.readCustomers();
        for(Customer cust : updatedCustomers){
            this.updateCustomer(cust);
        }
    }
    public void updateCustomer(Customer updatedCustomer){
        if(!customersRead) this.readCustomers();
        boolean found = false;

        for(int i = 0; i < this.customers.size(); i++){
            if(this.customers.get(i).getCustomerID().equals(updatedCustomer.getCustomerID())){
                this.customers.set(i, new Customer(updatedCustomer));
                found = true;
                break;
            }
        }
        if(!found) throw new IllegalArgumentException("Customer hasnt been stored: " + updatedCustomer.getCustomerID());
    }
    public void updateCustomerAttribute(String custID, Consumer<Customer> applier){
        if(!customersRead) this.readCustomers();
        boolean found = false;
        for(Customer cust : this.customers){
            if(cust.getCustomerID().equals(custID)){
                applier.accept(cust);
                found = true;
                break;
            }
        }
        if(!found) throw new IllegalArgumentException("Customer hasnt been stored: " + custID);
    }
    public ArrayList<Customer> getCustomers(){
        if(!customersRead) readCustomers();
        return new ArrayList<>(customers);
    }

    public ArrayList<Customer> fetchCustomersByID(ArrayList<String> ids){
        Set<String> idSet = new HashSet<>(ids);
        return query(c -> idSet.contains(c.getCustomerID()));
    }

    public Customer getCustomerByID(String id){
        ArrayList<Customer> result = query(c -> c.getCustomerID().equals(id));
        if(result.isEmpty()) return null;
        if(result.size() > 1){ throw new IllegalStateException("More than one customer under ID: " + id); }
        return result.get(0);
    }

    public ArrayList<Customer> fetchCustomersByName(String name){
        return query(c -> c.getName().equalsIgnoreCase(name));
    }

    public <T> ArrayList<T> query(
        Predicate<Customer> filterCondition,
        Function<Customer, T> mapper
    ){
        ArrayList<T> result = new ArrayList<>();
        if(!customersRead) readCustomers();

        for(Customer cust : customers){
            if(filterCondition.test(cust)){
                result.add(mapper.apply(cust));
            }
        }
        return result;
    }

    public ArrayList<Customer> query(
        Predicate<Customer> filterCondition
    ){
        ArrayList<Customer> result = new ArrayList<>();
        if(!customersRead) readCustomers();

        for(Customer cust : customers){
            if(filterCondition.test(cust)){
                result.add(new Customer(cust));
            }
        }
        return result;
    }

    public void reload() {
        this.customersRead = false;
        this.newCustomers.clear();
        this.readCustomers(); 
    }
}