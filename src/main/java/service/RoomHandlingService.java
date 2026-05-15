package service;

import dao.RoomDAO;
import dao.RecordsDAO;
import model.Room;
import model.StayRecord;
import model.Customer;
import model.roomUtil.Amenities;

import java.util.ArrayList;
import java.util.function.*;
import java.util.HashSet;
import java.util.Set;

public class RoomHandlingService {
    private RoomDAO roomData;
    private RecordsDAO recordData;

    public RoomHandlingService(RoomDAO rData, RecordsDAO reData){
        this.recordData = reData;
        this.roomData = rData;
    }

    public boolean roomExists(int roomNo){
        return roomData.getRoomByRoomNo(roomNo) != null;
    }

    public void addRoom(Room room){
        if(roomExists(room.getRoomNo())){
            throw new IllegalArgumentException("Room already exists: " + room.getRoomNo());
        }
        roomData.addRoom(room);
        roomData.appendRooms();
    }

    public void removeRoom(int roomNo){
        Room temp = roomData.getRoomByRoomNo(roomNo);
        if(temp != null){
            if (temp.getFlag(true)) throw new IllegalStateException("Cant delete occupied room...");
        }
        roomData.deleteRoom(roomNo); // same philosophy as customer
    }

    public void removeRooms(ArrayList<Integer> roomNos){
        roomData.deleteRooms(roomNos);
    }

    public Room getRoom(int roomNo){
        Room room = roomData.getRoomByRoomNo(roomNo);
        if(room == null){
            throw new IllegalArgumentException("Room does not exist: " + roomNo);
        }
        return room;
    }

    //only for customerHandlingService
    void roomCheckIn(int roomNo, Customer cust, boolean write){
        Room room = this.getRoom(roomNo);
        if(!room.getFlag(true)){
            room.setOccupied(true, cust);
            roomData.updateRoomAttribute(roomNo, r -> r.setOccupied(true, cust));
            if(write) roomData.writeRooms();
        }
        else throw new IllegalStateException("Room is already occupied: " + roomNo);
    }

    //only for customerHandlingService
    void roomCheckOut(int roomNo, boolean write){
        Room room = this.getRoom(roomNo);
        if(room.getFlag(true)){
            room.setOccupied(false, null);
            roomData.updateRoomAttribute(roomNo, r -> r.setOccupied(false, null));
            if(write) roomData.writeRooms();
        }
        else throw new IllegalStateException("Room is not occupied: " + roomNo);
    }
    public void initiateMaintanence(int roomNo){
        Room room = this.getRoom(roomNo);
        if(room.getFlag(false)) throw new IllegalStateException("Room is already under maintanence... " + roomNo);
        room.setMaintanence(true);
        this.updateRoomAttribute(room.getRoomNo(), r -> r.setMaintanence(true));
    }
    public void completeMaintanence(int roomNo){
        Room room = this.getRoom(roomNo);
        if(!room.getFlag(false)) throw new IllegalStateException("Room not under maintanence... " + roomNo);
        room.setMaintanence(false);
        this.updateRoomAttribute(room.getRoomNo(), r -> r.setMaintanence(false));
    }
    public double getRoomPrice(int roomNo){
        Room room = this.getRoom(roomNo);
        return room.getPrice();
    }


    public ArrayList<Room> getRoomsWithAmenity(Amenities amenity){
        return roomData.query(r -> {
            for(Amenities a : r.returnFeatures()) {
                if(a.getClass().equals(amenity.getClass())) return true;
            }
            return false;
        });
    }
    public ArrayList<Room> getRoomsWithAmenity(ArrayList<Amenities> amenities){
        return this.queryRooms(r -> {
            for(Amenities a: amenities){
                if (!r.returnFeatures().contains(a)){ return false; }
            }
            return true;
        });
    }
    public ArrayList<Room> getAvailableRooms(){
        return roomData.query(r -> !r.getFlag(true));
    }

    public ArrayList<Room> getRoomsByFloor(int floor){
        return roomData.fetchRoomsByFloor(floor);
    }

    public boolean isRoomAvailable(int roomNo){
        Room room = getRoom(roomNo);
        if (room == null) throw new IllegalArgumentException("Room does not exist: " + roomNo);
        return !room.getFlag(true); 
    }

    public ArrayList<StayRecord> getRoomHistory(int roomNo){
        if(!roomExists(roomNo)){
            throw new IllegalArgumentException("Room does not exist: " + roomNo);
        }

        return recordData.query(rec -> rec.getRoomNumber() == roomNo);
    }

    public void updateRoom(Room room){
        roomData.updateRoom(room);
        roomData.writeRooms();
    }

    public void updateRoomAttribute(int roomNo, Consumer<Room> applier, boolean write){
        roomData.updateRoomAttribute(roomNo, applier);
        if(write) roomData.writeRooms();
    }

    public void updateRoomAttribute(int roomNo, Consumer<Room> applier){
        this.updateRoomAttribute(roomNo, applier, true);
    }

    public ArrayList<Room> getOccupiedRooms(){
        return roomData.query(r -> r.getFlag(true));
    }


    public ArrayList<Room> getRoomsWithPendingPayments(){
        ArrayList<Integer> result = recordData.query(
                r -> r.getPaymentStatus() != model.Payment.PaymentStatus.SUCCESS,
                r -> r.getRoomNumber()
            );
        Set<Integer> roomSet = new HashSet<>(result);

        return roomData.fetchRoomsByRoomNo(new ArrayList<>(roomSet));
    }

    public <T> ArrayList<T> queryRooms(Predicate<Room> filterCondition, Function<Room, T> mapper){
        return roomData.query(filterCondition, mapper);
    }
    
    public ArrayList<Room> queryRooms(Predicate<Room> filterCondition){
        return roomData.query(filterCondition);
    }
    
    public <T> ArrayList<T> queryRecords(Predicate<StayRecord> filterCondition, Function<StayRecord, T> mapper){
        return recordData.query(filterCondition, mapper);
    }
    
    public ArrayList<StayRecord> queryRecords(Predicate<StayRecord> filterCondition){
        return recordData.query(filterCondition);
    }

    public ArrayList<Room> getAllRooms(){
        return roomData.getRooms();
    }
    public ArrayList<StayRecord> getAllRecords(){
        return recordData.getRecords();
    }

    public void rollbackChanges() {
        roomData.reload();
        recordData.reload();
    }
}