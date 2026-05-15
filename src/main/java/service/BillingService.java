package service;

import model.TimeInfo;
import model.roomUtil.Amenities;
import model.Customer;
import model.Payment;
import model.Room;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.function.Predicate;

import dao.BillsDAO;



public class BillingService {
    private BillsDAO billData;
    private double tax;

    BillingService(BillsDAO billData, double tax){
        this.billData = billData;
        this.tax = tax;
    }
    BillingService(BillsDAO billData){
        this(billData, 0.18);
    }

    public double getTax(){
        return this.tax;
    }

    public String processPayment(Customer cust, Room room, boolean returnInvoice){
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(TimeInfo.dateStoreFormat);
        TimeInfo stayTimeInfo = cust.getCurrStay().getTimeRecord();
        long nights = ChronoUnit.DAYS.between(LocalDate.parse(stayTimeInfo.getCheckInDate(), dateFormatter), LocalDate.parse(stayTimeInfo.getCheckOutDate(), dateFormatter));
        if (nights == 0) nights = 1; 

        double paymentAmt = room.calculateCost() * nights;
        paymentAmt *= (1 + this.tax);

        Payment payment = new Payment(cust.getCurrStay().getPaymentType(), paymentAmt);
        payment.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
        cust.setPayment(payment);
        String invoice = this.generateInvoice(cust.getCurrStay().getPaymentRecord(), cust, room);
        billData.writeInvoice(invoice);
        if(returnInvoice) return invoice;
        else return "";
    }

    public String processGroupPayment(ArrayList<Customer> customers, ArrayList<Room> rooms, boolean returnInvoice) {
        if (customers.size() != rooms.size()) throw new IllegalArgumentException("Mismatched customers and rooms.");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(TimeInfo.dateStoreFormat);

        for (int i = 0; i < customers.size(); i++) {
            Customer cust = customers.get(i);
            Room room = rooms.get(i);

            TimeInfo stayTimeInfo = cust.getCurrStay().getTimeRecord();
            long nights = ChronoUnit.DAYS.between(LocalDate.parse(stayTimeInfo.getCheckInDate(), dateFormatter), LocalDate.parse(stayTimeInfo.getCheckOutDate(), dateFormatter));
            if (nights == 0) nights = 1; 

            double paymentAmt = room.calculateCost() * nights;
            paymentAmt *= (1 + this.tax);

            Payment payment = new Payment(cust.getCurrStay().getPaymentType(), paymentAmt);
            payment.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
            cust.setPayment(payment);

            String individualInvoice = this.generateInvoice(cust.getCurrStay().getPaymentRecord(), cust, room);
            billData.writeInvoice(individualInvoice);
        }

        String invoice = this.generateGroupInvoice(customers, rooms);
        billData.writeInvoice(invoice);
        
        if(returnInvoice) return invoice;
        else return "";
    }

    public String generateGroupInvoice(ArrayList<Customer> customers, ArrayList<Room> rooms) {
        if (customers.size() != rooms.size()) throw new IllegalArgumentException("Mismatched customers and rooms.");
        
        StringBuilder invoice = new StringBuilder();
        billData.generateNewGroupID();
        String billID = billData.returnID();
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(TimeInfo.dateStoreFormat);
        
        ArrayList<String> custIDs = new ArrayList<>();
        for(Customer c : customers) custIDs.add(c.getCustomerID());
        String joinedIDs = String.join(",", custIDs);
        
        invoice.append(String.format("META_HEADER|%s|GROUP|MULTI|%s|%s\n", billID, LocalDate.now().format(dateFormatter), joinedIDs));
        invoice.append("=========================================================\n");
        invoice.append("              HOTEL GROUP INVOICE\n");
        invoice.append("=========================================================\n");
        invoice.append(String.format("Record ID: %-25s Date: %s\n", billID, LocalDate.now().format(dateFormatter)));
        
        double grandTotalBase = 0;
        double grandTotalAmenities = 0;
        
        for (int i = 0; i < customers.size(); i++) {
            Customer cust = customers.get(i);
            Room room = rooms.get(i);
            
            TimeInfo stayTimeInfo = cust.getCurrStay().getTimeRecord();
            long nights = ChronoUnit.DAYS.between(LocalDate.parse(stayTimeInfo.getCheckInDate(), dateFormatter), LocalDate.parse(stayTimeInfo.getCheckOutDate(), dateFormatter));
            if (nights == 0) nights = 1; 

            double baseRatePerNight = room.getBasePrice();
            double totalBaseCost = nights * baseRatePerNight;
            grandTotalBase += totalBaseCost;
            
            invoice.append("\n---------------------------------------------------------\n");
            invoice.append(String.format("STAY DETAILS: %s (Room %d)\n", cust.getName(), room.getRoomNo()));
            invoice.append("---------------------------------------------------------\n");
            invoice.append(String.format("%-12s: %s\n", "Customer ID", cust.getCustomerID()));
            invoice.append(String.format("%-12s: %s\n", "Room Type", room.getRoomType().name()));
            invoice.append(String.format("%-12s: %s %s to %s %s (%d Nights)\n", "Duration", 
                stayTimeInfo.getCheckInDate(), stayTimeInfo.getCheckInTime(), 
                stayTimeInfo.getCheckOutDate(), stayTimeInfo.getCheckOutTime(), nights));
            
            String baseRateString = String.format("Base Room Rate (%d Nights x %.1f)", nights, baseRatePerNight);
            invoice.append(String.format("%-42s : Rs. %8.2f\n", baseRateString, totalBaseCost));
            
            double roomAmenitiesCost = 0;
            if (!room.returnFeatures().isEmpty()) {
                invoice.append("\nAdditional Amenities:\n");
                for (Amenities a : room.returnFeatures()) {
                    String featureName = "- " + a.returnName() + " (" + a.returnFeatureDesc() + ")";
                    double amenityTotal = a.getAdditionalCost() * nights;
                    invoice.append(String.format("%-42s : Rs. %8.2f\n", featureName, amenityTotal));
                    roomAmenitiesCost += amenityTotal;
                }
            }
            grandTotalAmenities += roomAmenitiesCost;
        }

        double taxAmount = (grandTotalBase + grandTotalAmenities) * this.tax;
        invoice.append("\n---------------------------------------------------------\n");
        invoice.append("CONSOLIDATED CHARGES\n");
        invoice.append("---------------------------------------------------------\n");
        invoice.append(String.format("%-42s : Rs. %8.2f\n", "Total Base Cost", grandTotalBase));
        invoice.append(String.format("%-42s : Rs. %8.2f\n", "Total Amenities Cost", grandTotalAmenities));
        invoice.append(String.format("\n%-42s : Rs. %8.2f\n", String.format("Tax (%.0f%%)", this.tax * 100), taxAmount));

        invoice.append("\n=========================================================\n");
        double totalCost = (grandTotalBase + grandTotalAmenities) + taxAmount;
        invoice.append(String.format("GRAND TOTAL AMOUNT PAID                    : Rs. %.2f\n", totalCost));
        invoice.append("=========================================================\n");
        invoice.append("          Thank you for choosing our hotel!\n");

        return invoice.toString();
    }

    public String generateInvoice(Payment payment, Customer cust, Room room){
        StringBuilder invoice = new StringBuilder();
        billData.generateNewID();
        String billID = billData.returnID();
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(TimeInfo.dateStoreFormat);
        TimeInfo stayTimeInfo = cust.getCurrStay().getTimeRecord();
        long nights = ChronoUnit.DAYS.between(LocalDate.parse(stayTimeInfo.getCheckInDate(), dateFormatter), LocalDate.parse(stayTimeInfo.getCheckOutDate(), dateFormatter));
        if (nights == 0) nights = 1; 

        double baseRatePerNight = room.getBasePrice();
        double totalBaseCost = nights * baseRatePerNight;

        invoice.append(String.format("META_HEADER|%s|%s|%s|%s\n", billID, cust.getCustomerID(), room.getRoomNo(), LocalDate.now().format(dateFormatter)));

        invoice.append("=========================================================\n");
        invoice.append("                    HOTEL INVOICE\n");
        invoice.append("=========================================================\n");
        invoice.append(String.format("Record ID: %-25s Date: %s\n", billID, LocalDate.now().format(dateFormatter)));

        invoice.append("---------------------------------------------------------\n");
        invoice.append("CUSTOMER DETAILS\n");
        invoice.append("---------------------------------------------------------\n");
        invoice.append(String.format("%-12s: %s\n", "Customer ID", cust.getCustomerID()));
        invoice.append(String.format("%-12s: %s\n", "Name", cust.getName()));
        invoice.append(String.format("%-12s: %s\n", "Contact", cust.getContact()));

        invoice.append("\n---------------------------------------------------------\n");
        invoice.append("STAY DETAILS\n");
        invoice.append("---------------------------------------------------------\n");
        invoice.append(String.format("%-12s: %d\n", "Room Number", room.getRoomNo()));
        invoice.append(String.format("%-12s: %s\n", "Room Type", room.getRoomType().name()));
        invoice.append(String.format("%-12s: %d Guests\n", "Capacity", room.getCapacity()));
        invoice.append(String.format("%-12s: %s %s\n", "Check-In", stayTimeInfo.getCheckInDate(), stayTimeInfo.getCheckInTime()));
        invoice.append(String.format("%-12s: %s %s\n", "Check-Out", stayTimeInfo.getCheckOutDate(), stayTimeInfo.getCheckOutTime()));

        invoice.append("\n---------------------------------------------------------\n");
        invoice.append("CHARGES BREAKDOWN\n");
        invoice.append("---------------------------------------------------------\n");

        String baseRateString = String.format("Base Room Rate (%d Nights x %.1f)", nights, baseRatePerNight);
        invoice.append(String.format("%-42s : Rs. %8.2f\n", baseRateString, totalBaseCost));

        double totalAmenitiesCost = 0;
        if (!room.returnFeatures().isEmpty()) { // Only print header if amenities exist
            invoice.append("\nAdditional Amenities:\n");
            for (Amenities a : room.returnFeatures()) {
                String featureName = "- " + a.returnName() + " (" + a.returnFeatureDesc() + ")";
                double amenityTotal = a.getAdditionalCost() * nights;
                invoice.append(String.format("%-42s : Rs. %8.2f\n", featureName, amenityTotal));
                totalAmenitiesCost += amenityTotal;
            }
        }

        double taxAmount = (totalBaseCost + totalAmenitiesCost) * this.tax;
        invoice.append(String.format("\n%-42s : Rs. %8.2f\n", String.format("Tax (%.0f%%)", this.tax * 100), taxAmount));

        invoice.append("\n---------------------------------------------------------\n");
        invoice.append("PAYMENT DETAILS\n");
        invoice.append("---------------------------------------------------------\n");
        invoice.append(String.format("%-15s: %s\n", "Payment ID", payment.getPaymentID()));
        invoice.append(String.format("%-15s: %s\n", "Payment Method", payment.getPaymentType().getName()));
        invoice.append(String.format("%-15s: %s\n", "Payment Status", payment.getPaymentStatus().getName()));
        invoice.append(String.format("%-15s: %s\n", "Timestamp", payment.getTimeStamp().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))));

        invoice.append("\n=========================================================\n");
        double totalCost = totalBaseCost + totalAmenitiesCost;
        totalCost *= (1 + this.tax);
        invoice.append(String.format("TOTAL AMOUNT PAID                          : Rs. %.2f\n", totalCost));
        invoice.append("=========================================================\n");
        invoice.append("          Thank you for choosing our hotel!\n");

        return invoice.toString();
    }

    public ArrayList<String> queryBills(Predicate<String> filterCondition) {
        return billData.queryBills(filterCondition);
    }

    public double getBillAmount(Room room, int expectedNights) {
        double paymentAmt = room.calculateCost() * expectedNights;
        return paymentAmt * (1 + this.tax);
    }

    public ArrayList<String> fetchGroupBills() {
        return this.queryBills(header -> header.contains("|GROUP|MULTI|"));
    }
}
