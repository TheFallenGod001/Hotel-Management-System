package dao;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.*;

import model.Room;

public class RoomDAO {
    File roomsFile;
    ArrayList<Room> rooms;
    ArrayList<Room> newRooms;
    boolean roomsRead;

    public RoomDAO(){
        roomsFile = new File("data/Rooms.csv");
        roomsRead = false;
        rooms = new ArrayList<>();
        newRooms = new ArrayList<>();

        if (!roomsFile.exists()) {
            roomsFile.getParentFile().mkdirs();
            try {
                roomsFile.createNewFile();
            } catch(Exception e) { e.printStackTrace(); }
        }
    }

    public RoomDAO(String loc){
        roomsFile = new File(loc);
        roomsRead = false;
        rooms = new ArrayList<>();
        newRooms = new ArrayList<>();

        if (!roomsFile.exists()) {
            roomsFile.getParentFile().mkdirs();
            try {
                roomsFile.createNewFile();
            } catch(Exception e) { e.printStackTrace(); }
        }
    }

    public void addRoom(Room room){
        Room copy = Room.parseString(room.toString());
        if (copy.getRoomNo() < 0) throw new IllegalArgumentException("Room number cannot be less than 0");
        if(roomsRead) rooms.add(copy);
        newRooms.add(copy);
    }

    public void readRooms(){
        if(roomsRead) return;

        try(BufferedReader reader = new BufferedReader(new FileReader(roomsFile))){
            rooms.clear();
            String line;

            while((line = reader.readLine()) != null){
                if(line.trim().isEmpty()) continue; // Skip blank lines
                try {
                    rooms.add(Room.parseString(line));
                } catch (Exception e) {
                    System.err.println("WARNING: Skipped corrupted Room record -> " + line);
                }
            }

            roomsRead = true;
        }
        catch(Exception e){ e.printStackTrace(); }
    }

    private void manipulateRooms(boolean append){
        if(newRooms.isEmpty()) return;

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(roomsFile, append))){
            for(Room room : newRooms){
                writer.append(room.toString());
                writer.newLine();
            }
            newRooms.clear();
        }
        catch(Exception e){ e.printStackTrace(); }
    }

    public void flushWithNewRooms(){
        manipulateRooms(false);
        roomsRead = false;
    }

    public void appendRooms(){
        manipulateRooms(true);
    }

    public void writeRooms(){
        if(!roomsRead) throw new IllegalStateException("Read records before writing...");
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(this.roomsFile))){
            for(Room room : this.rooms){
                writer.write(room.toString());
                writer.newLine();
            }
        }
        catch(Exception e) { e.printStackTrace(); }
    }

    public void deleteRoom(int roomNo, boolean write){
        if(!roomsRead) this.readRooms();
        rooms.removeIf(r -> r.getRoomNo() == roomNo);
        newRooms.removeIf(r -> r.getRoomNo() == roomNo);

        if(write) this.writeRooms();
    }

    public void deleteRoom(int roomNo){
        this.deleteRoom(roomNo, true);
    }

    public void deleteRooms(ArrayList<Integer> roomNos){
        for(int roomNo : roomNos){
            this.deleteRoom(roomNo, false);
        }
        this.writeRooms();
    }

    public void updateRooms(ArrayList<Room> updatedRooms){
        if(!roomsRead) this.readRooms();
        for(Room room : updatedRooms){
            this.updateRoom(room);
        }
    }

    public void updateRoom(Room updatedRoom){
        if(!roomsRead) this.readRooms();
        boolean found = false;

        for(int i = 0; i < this.rooms.size(); i++){
            if(this.rooms.get(i).getRoomNo() == updatedRoom.getRoomNo()){
                this.rooms.set(i, Room.parseString(updatedRoom.toString()));
                found = true;
                break;
            }
        }

        if(!found) throw new IllegalArgumentException("Room hasnt been stored: " + updatedRoom.getRoomNo());
    }

    public void updateRoomAttribute(int roomNo, Consumer<Room> applier){
        if(!roomsRead) this.readRooms();
        boolean found = false;

        for(Room room : this.rooms){
            if(room.getRoomNo() == roomNo){
                applier.accept(room);
                found = true;
                break;
            }
        }

        if(!found) throw new IllegalArgumentException("Room hasnt been stored: " + roomNo);
    }

    public ArrayList<Room> getRooms(){
        if(!roomsRead) readRooms();
        return new ArrayList<>(rooms);
    }

    public ArrayList<Room> fetchRoomsByRoomNo(ArrayList<Integer> roomNos){
        Set<Integer> set = new HashSet<>(roomNos);
        return query(r -> set.contains(r.getRoomNo()));
    }

    public Room getRoomByRoomNo(int roomNo){
        ArrayList<Room> result = query(r -> r.getRoomNo() == roomNo);

        if(result.isEmpty()) return null;
        if(result.size() > 1){
            throw new IllegalStateException("More than one room under roomNo: " + roomNo);
        }

        return result.get(0);
    }

    public ArrayList<Room> fetchRoomsByFloor(int floor){
        return query(r -> r.getFloor() == floor);
    }

    public <T> ArrayList<T> query(
        Predicate<Room> filterCondition,
        Function<Room, T> mapper
    ){
        ArrayList<T> result = new ArrayList<>();
        if(!roomsRead) readRooms();

        for(Room room : rooms){
            if(filterCondition.test(room)){
                result.add(mapper.apply(room));
            }
        }

        return result;
    }

    public ArrayList<Room> query(
        Predicate<Room> filterCondition
    ){
        ArrayList<Room> result = new ArrayList<>();
        if(!roomsRead) readRooms();

        for(Room room : rooms){
            if(filterCondition.test(room)){
                result.add(Room.parseString(room.toString())); 
            }
        }

        return result;
    }

    public void reload() {
        this.roomsRead = false; 
        this.newRooms.clear();  
        this.readRooms();       
    }
}