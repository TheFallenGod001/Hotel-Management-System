package model;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class TimeInfo{
        LocalDate checkInDate;
        LocalDate checkOutDate;
        LocalTime checkInTime;
        LocalTime checkOutTime;
        String dateFormat;
        String timeFormat;
        DateTimeFormatter dateFormatter;
        DateTimeFormatter timeFormatter;
        public static final String dateStoreFormat = "dd-MM-yyyy";
        public static final String timeStoreFormat = "HH:mm:ss";
        boolean dateFormatChange;
        boolean timeFormatChange;

        public TimeInfo(){
            this.dateFormat = "dd-MM-yyyy";
            this.timeFormat = "HH:mm:ss";
            dateFormatter = DateTimeFormatter.ofPattern(this.dateFormat);
            timeFormatter = DateTimeFormatter.ofPattern(this.timeFormat);
            dateFormatChange = false;
            timeFormatChange = false;
        }
        public TimeInfo(String dateFormat, String timeFormat){
            this.dateFormat = dateFormat;
            this.timeFormat = timeFormat;
            dateFormatter = DateTimeFormatter.ofPattern(this.dateFormat);
            timeFormatter = DateTimeFormatter.ofPattern(this.timeFormat);
            dateFormatChange = false;
            timeFormatChange = false;
        }
        public TimeInfo(TimeInfo other){
            this.checkInDate = other.checkInDate;
            this.checkOutDate = other.checkOutDate;
            this.checkInTime = other.checkInTime;
            this.checkOutTime = other.checkOutTime;
            this.dateFormatter = DateTimeFormatter.ofPattern(other.dateFormat);
            this.timeFormatter = DateTimeFormatter.ofPattern(other.timeFormat);
            this.dateFormatChange = false;
            this.dateFormat = other.dateFormat;
            this.timeFormatChange = false;
            this.timeFormat = other.timeFormat;
        }
        
        public void updateFormat(String format, boolean date){
            if (date){ 
                this.dateFormat = format;
                dateFormatChange = true;
            }
            else{
                this.timeFormat = format;
                timeFormatChange = true;
            }
        }
        public void updateFormat(String dateFormat, String timeFormat){
            this.dateFormat = dateFormat;
            this.timeFormat = timeFormat;
            dateFormatChange = true;
            timeFormatChange = true;
        }
        public void setCheckIn(LocalDate checkInDate, LocalTime checkInTime){
            this.checkInDate = checkInDate;
            this.checkInTime = checkInTime;
        }
        public void setCheckIn(LocalDate checkInDate){
            this.setCheckIn(checkInDate, LocalTime.now());
        }
        public void checkInNow(){
            this.checkInDate = LocalDate.now();
            this.checkInTime = LocalTime.now();
        }
        public void setCheckOut(LocalDate checkOutDate, LocalTime checkOutTime){
            if (this.checkInDate == null || this.checkInTime == null) {
                throw new IllegalArgumentException("Cant check out before fully checking in.");
            }
            if (LocalDateTime.of(this.checkInDate, this.checkInTime).isBefore(LocalDateTime.of(checkOutDate, checkOutTime))){
                this.checkOutDate = checkOutDate;
                this.checkOutTime = checkOutTime;
            }
            else {throw new IllegalArgumentException("Cant check out on a date before check in date.");}
        }
        public void setCheckOut(LocalDate checkOutDate){
            this.setCheckOut(checkOutDate, LocalTime.now());
        }
        public void checkOutNow(){
            this.setCheckOut(LocalDate.now(), LocalTime.now());
        }

        public String getCheckInDate(){
            if (dateFormatChange) {
                dateFormatter = DateTimeFormatter.ofPattern(this.dateFormat);
                dateFormatChange = false;
            }
            if (checkInDate == null) return "";
            return dateFormatter.format(checkInDate);
        }
        public String getCheckInTime(){
            if (timeFormatChange){
                timeFormatter = DateTimeFormatter.ofPattern(this.timeFormat);
                timeFormatChange = false;
            }
            if (checkInTime == null) return "";
            return timeFormatter.format(checkInTime);
        }
        public String getCheckInInfo(boolean store){
            String s;
            if(store){
                String tempDform = this.dateFormat, tempTform = this.timeFormat;
                updateFormat(dateStoreFormat, timeStoreFormat);
                s = getCheckInDate();
                s = s.concat(" ");
                s = s.concat(getCheckInTime());  
                updateFormat(tempDform, tempTform);
            }
            else{
                s = getCheckInDate();
                s = s.concat(" ");
                s = s.concat(getCheckInTime());  
            }

            return s;
        }
        
        public LocalDate getCheckInLocalDate() {
            return this.checkInDate;
        }
        public LocalTime getCheckInLocalTime() {
            return this.checkInTime;
        }
        public LocalDateTime getCheckInLocalDateTime() {
            if (this.checkInDate == null || this.checkInTime == null) return null;
            return LocalDateTime.of(this.checkInDate, this.checkInTime);
        }

        public String getCheckOutDate(){
            if (dateFormatChange) {
                dateFormatter = DateTimeFormatter.ofPattern(this.dateFormat);
                dateFormatChange = false;
            }
            if(checkOutDate == null) return "";
            return dateFormatter.format(checkOutDate);
        }
        public String getCheckOutTime(){
            if (timeFormatChange){
                timeFormatter = DateTimeFormatter.ofPattern(this.timeFormat);
                timeFormatChange = false;
            }
            if(checkOutTime == null) return "";
            return timeFormatter.format(checkOutTime);
        }
        public String getCheckOutInfo(boolean store){
            String s;
            if(store){
                String tempDform = this.dateFormat, tempTform = this.timeFormat;
                updateFormat(dateStoreFormat, timeStoreFormat);
                s = getCheckOutDate();
                s = s.concat(" ");
                s = s.concat(getCheckOutTime());  
                updateFormat(tempDform, tempTform);
            }
            else{
                s = getCheckOutDate();
                s = s.concat(" ");
                s = s.concat(getCheckOutTime());  
            }

            return s;
        }
        
        public LocalDate getCheckOutLocalDate() {
            return this.checkOutDate;
        }
        public LocalTime getCheckOutLocalTime() {
            return this.checkOutTime;
        }
        public LocalDateTime getCheckOutLocalDateTime() {
            if (this.checkOutDate == null || this.checkOutTime == null) return null;
            return LocalDateTime.of(this.checkOutDate, this.checkOutTime);
        }

        public static TimeInfo parseString(String info){
            String temp[] = info.split("\\|", -1);
            TimeInfo tempTinfo = new TimeInfo();
            
            try {
                if (temp.length > 0 && !temp[0].trim().isEmpty()) {
                    String checkInInfo[] = temp[0].trim().split(" ");
                    if (checkInInfo.length == 2) {
                        tempTinfo.checkInDate = LocalDate.parse(checkInInfo[0], DateTimeFormatter.ofPattern(dateStoreFormat));
                        tempTinfo.checkInTime = LocalTime.parse(checkInInfo[1], DateTimeFormatter.ofPattern(timeStoreFormat));
                    }
                }
                
                if (temp.length > 1 && !temp[1].trim().isEmpty()) {
                    String checkOutInfo[] = temp[1].trim().split(" ");
                    if (checkOutInfo.length == 2) {
                        tempTinfo.checkOutDate = LocalDate.parse(checkOutInfo[0], DateTimeFormatter.ofPattern(dateStoreFormat));
                        tempTinfo.checkOutTime = LocalTime.parse(checkOutInfo[1], DateTimeFormatter.ofPattern(timeStoreFormat));
                    }
                }
            } catch(Exception e) { 
                System.err.println("Warning: Could not parse dates in TimeInfo string: " + info); 
            }
            
            return tempTinfo;
        }
        public String toString(){
            String s1 = this.getCheckInInfo(true);
            String s2 = this.getCheckOutInfo(true);
            
            if (((s1 != null) && (!s1.equals("")) && (s2 != null) && (!s2.equals("")))) return String.format("%s|%s", s1, s2);
            else return "";
        }
}