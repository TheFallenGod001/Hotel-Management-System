package model;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import model.roomUtil.Amenities;

class SeaView extends Amenities{
    String location;
    public SeaView(String location){
        super.featureName = "Sea View";
        this.location = location;
        this.additionalCosts = 1500;
    }
    public SeaView(SeaView Other){
        super.featureName = "Sea View";
        this.location = Other.location;
        this.additionalCosts = 1500;
    }

    public String returnFeatureDesc(){
        return location;
    }

    public double getAdditionalCost(){
        return this.additionalCosts;
    }
    public Amenities parseAmenity(String amenityString){
        if(this.returnName().equals(amenityString)) return new SeaView("Default");
        else return null;
    }
}

class AttachedBath extends Amenities{
    public AttachedBath(){
        super.featureName = "Attached Bath";
        super.additionalCosts = 100;
    }

    public String returnFeatureDesc(){
        return "Attached Bathroom Available";
    }

    public double getAdditionalCost(){
        return this.additionalCosts;
    }

    public Amenities parseAmenity(String amenityString){
        if(this.returnName().equals(amenityString)) return new AttachedBath();
        else return null;
    }
}

class AC extends Amenities{
    double powerCons; //KWh
    public AC(){
        this(1.2);
    }
    public AC(double powerCons){
        super.featureName = "AC";
        this.powerCons = powerCons;
        this.additionalCosts = 300 + (powerCons * 100);
    }
    public AC(AC Other){
        super.featureName = "AC";
        this.powerCons = Other.powerCons;
        this.additionalCosts = Other.additionalCosts;
    }

    public String returnFeatureDesc(){
        String s;
        s = Double.toString(powerCons);
        s = s.concat(" KWh");
        return s;
    }

    public double getAdditionalCost(){
        return this.additionalCosts;
    }

    public Amenities parseAmenity(String amenityString){
        if(this.returnName().equals(amenityString)) return new AC();
        else return null;
    }
}

class WIFI extends Amenities{
    enum signalStrength{
        I_5G,
        I_4G,
        I_2G
    };
    signalStrength strength;
    public WIFI(){
        this(signalStrength.I_2G);
    }
    public WIFI(signalStrength strength){
        super.featureName = "Wi-Fi";
        this.strength = strength;
        switch(strength){
            case I_2G: 
                super.additionalCosts = 0;
                break;
            case I_4G:
                super.additionalCosts = 100;
                break;
            case I_5G:
                super.additionalCosts = 250;
                break;
        }
    }
    public WIFI(WIFI Other){
        this.featureName = "Wi-Fi";
        this.strength = Other.strength;
        this.additionalCosts = Other.additionalCosts;
    }

    public String returnFeatureDesc(){
        String s;
        switch(this.strength){
            case I_5G:
                s = "5G signal";
                break;
            case I_4G:
                s = "4G signal";
                break;
            case I_2G:
                s = "2G signal";
                break;
            default:
                s = "No signal type assigned.";
        }
        return s;
    }
    public signalStrength getStrength() { return this.strength; }
    public double getAdditionalCost() { return this.additionalCosts; }
    public Amenities parseAmenity(String amenityString){
        if(this.returnName().equals(amenityString)) return new WIFI();
        else return null;
    }
}

public class Room{
    static final Class<?>[] amenities = {SeaView.class, AttachedBath.class, AC.class, WIFI.class};
    static final double prices[] = {2500.0, 6000.0, 15000.0};
    static final double percPerCap = .25;
    public enum roomType{
        REGULAR(0),
        DELUXE(1),
        SUITE(2);
        int val, price;

        roomType(int val){
            this.val = val;
        }
        public double getPrice(){
            return Room.prices[val];
        }
        public String toString(){
            switch(val){
                case 0:
                    return "REGULAR";
                case 1:
                    return "DELUXE";
                case 2:
                    return "SUITE";
                default:
                    return "NONE";
            }
        }
    }
    private int roomNo;
    roomType type;
    int capacity;
    private boolean maintanence;
    private boolean occupied;
    private Customer occupant;
    private int floorNumber;
    Set<Amenities> features;

    public Room(int roomNo, roomType type, boolean occupied, Customer occupant, int capacity, boolean maintanence, Set<Amenities> features){
        this.roomNo = roomNo;
        this.floorNumber = roomNo/100; //there can only be 100 rooms in a floor at max
        this.type = type;
        this.occupied = occupied;
        this.occupant = occupant;
        this.capacity = capacity;
        this.maintanence = maintanence;
        this.features = (features != null) ? features : new HashSet<>();
    }
    public Room(int roomNo){
        this(roomNo, roomType.REGULAR, false, null, 1, false, null);
    }
    public Room(int roomNo, roomType type){
        this(roomNo, type, false, null, 1, false, null);
    }
    public Room(int roomNo, roomType type, int capacity){
        this(roomNo, type, false, null, capacity, false, null);
    }
    public Room(int roomNo, roomType type, Customer occupant){
        this(roomNo, type, true, occupant, 1, false, null);
    }
    public Room(int roomNo, roomType type, Set<Amenities> features){
        this(roomNo, type, false, null, 1, false, features);
    }


    public void setMaintanence(boolean flag){
        this.maintanence = flag;
    }
    public void setOccupied(boolean flag, Customer occupant){
        if (flag){
            if(occupant == null) throw new IllegalArgumentException("Occupant cannot be null if flag is true.");
            this.occupied = flag;
            this.occupant = occupant;
        }
        else if (this.occupied){
            this.occupant = null;
            this.occupied = flag;
        }
    }

    public int getRoomNo(){
        return this.roomNo;
    }
    public boolean getFlag(boolean occupied){// 0-> occupied, 1-> maintenance
        if (occupied) return this.occupied;
        else return this.maintanence;
    }
    public Customer returnCust(){
        if (this.occupied) return this.occupant;
        else return null;
    }
    public ArrayList<Amenities> returnFeatures(){
        return new ArrayList<>(this.features);
    }
    public double getPrice(){  
        return calculateCost();
    }
    public double getBasePrice(){
        return ((capacity - 1) * percPerCap + 1) * type.getPrice();
    }
    public int getFloor(){
        return this.floorNumber;
    }
    public roomType getRoomType(){
        return this.type;
    }
    public int getCapacity(){
        return this.capacity;
    }

    public String roomInfo(){
        int amenitiesCode = 0;
        double additionalCosts = 0;
        Iterator<Amenities> iter = features.iterator();
        while(iter.hasNext()){
            Amenities temp = iter.next();
            additionalCosts += temp.getAdditionalCost();
            for(int i = 0; i < amenities.length; i++){
                if(amenities[i].isInstance(temp)){
                    amenitiesCode |= (1 << i);
                    break;
                }
            }
        }
        return this.roomNo + "|" + this.type.name() + "|" + this.capacity + "|" +  amenitiesCode + "|" + additionalCosts;
    }

    public static double calculateCost(String roomInfo){
        double price = 0;
        String temp[] = roomInfo.split("\\|");
        if(temp.length != 5) throw new IllegalArgumentException("Room Info of invalid format: " + roomInfo);
        try{
            roomType tempType = roomType.valueOf(temp[1]);
            int tempCapacity = Integer.valueOf(temp[2]);
            price = ((tempCapacity - 1) * percPerCap + 1) * tempType.getPrice() + Double.valueOf(temp[4]);
        }
        catch(Exception e) { throw new IllegalArgumentException("Room Info of invalid format: " + roomInfo);}
        return price;
    }
    public double calculateCost(){
        double base = ((capacity - 1) * percPerCap + 1) * type.getPrice();

        double extra = 0;
        for (Amenities a : features){
            extra += a.getAdditionalCost();
        }

        return base + extra;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(roomNo)
        .append(",")
        .append(type.name())
        .append(",")
        .append(capacity)
        .append(",").append(maintanence)
        .append(",").append(occupied)
        .append(",").append((occupied && occupant != null) ? occupant.getCustomerID() : "NONE");

        if (!features.isEmpty()) {
            sb.append(",");

            ArrayList<Amenities> sorted = new ArrayList<>(features);
            sorted.sort((a, b) ->
                a.getClass().getSimpleName()
                .compareTo(b.getClass().getSimpleName())
            );

            boolean first = true;
            for (Amenities a : sorted) {
                if (!first) sb.append("|");
                first = false;

                if (a instanceof WIFI wifi) {
                    sb.append("WIFI:")
                    .append(wifi.getStrength().name());
                } 
                else if (a instanceof AC ac) {
                    sb.append("AC:")
                    .append(ac.powerCons);
                } 
                else if (a instanceof SeaView sv) {
                    sb.append("SEAVIEW:")
                    .append(sv.location);
                } 
                else if (a instanceof AttachedBath) {
                    sb.append("ATTACHEDBATH");
                }
            }
        }

        return sb.toString();
    }

    public static Amenities createWifiAmenity(String signalS){
        return new WIFI(WIFI.signalStrength.valueOf(signalS));
    }
    public static Amenities createWifiAmenity(){
        return new WIFI();
    }
    public static Amenities createSeaViewAmenity(String location){
        return new SeaView(location);
    }
    public static Amenities createAttachedBathroom(){
        return new AttachedBath();
    }
    public static Amenities createACAmenity(double powerConsumption){
        return new AC(powerConsumption);
    }
    public static Amenities createACAmenity(){
        return new AC();
    }
    public static ArrayList<Amenities> getAvailableAmenities(){
        ArrayList<Amenities> availAmenities = new ArrayList<>();

        availAmenities.add(new WIFI());
        availAmenities.add(new AC());
        availAmenities.add(new SeaView("NONE"));
        availAmenities.add(new AttachedBath());

        return availAmenities;
    }

    public static Room parseString(String roomDetails) {
        if (roomDetails == null || roomDetails.isEmpty()) {
            throw new IllegalArgumentException("Room string is empty");
        }

        String[] parts = roomDetails.split(",", 7);

        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid Room format: " + roomDetails);
        }

        int roomNo;
        int capacity;
        roomType type;
        boolean maintanence = false;
        boolean occupied = false;
        Customer occupant = null;

        try {
            roomNo = Integer.parseInt(parts[0]);
            type = roomType.valueOf(parts[1]);
            capacity = Integer.parseInt(parts[2]);
            if (parts.length >= 6) {
                maintanence = Boolean.parseBoolean(parts[3]);
                occupied = Boolean.parseBoolean(parts[4]);
                String occId = parts[5];
                if (occupied && !occId.equals("NONE")) {
                    occupant = new Customer(occId);
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid base room data: " + roomDetails);
        }

        Set<Amenities> features = new HashSet<>();

        String featuresString = "";
        if (parts.length == 4 && !parts[3].isEmpty()) {
            featuresString = parts[3];
        } else if (parts.length == 7 && !parts[6].isEmpty()) {
            featuresString = parts[6];
        }

        if (!featuresString.isEmpty()) {
            String[] ams = featuresString.split("\\|");

            for (String a : ams) {
                if (a.equals("ATTACHEDBATH")) {
                    features.add(new AttachedBath());
                    continue;
                }

                String[] kv = a.split(":", 2);
                if (kv.length < 2) {
                    throw new IllegalArgumentException("Malformed amenity: " + a);
                }

                String key = kv[0];
                String val = kv[1];

                switch (key) {
                    case "WIFI":
                        try {
                            features.add(new WIFI(WIFI.signalStrength.valueOf(val)));
                        } catch (Exception e) {
                            throw new IllegalArgumentException("Invalid WIFI value: " + val);
                        }
                        break;

                    case "AC":
                        try {
                            features.add(new AC(Double.parseDouble(val)));
                        } catch (Exception e) {
                            throw new IllegalArgumentException("Invalid AC value: " + val);
                        }
                        break;

                    case "SEAVIEW":
                        features.add(new SeaView(val));
                        break;

                    default:
                        throw new IllegalArgumentException("Unknown amenity: " + key);
                }
            }
        }

        Room room = new Room(roomNo, type, occupied, occupant, capacity, maintanence, features);

        return room;
    }
}