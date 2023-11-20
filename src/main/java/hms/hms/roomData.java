package hms.hms;

public class roomData {
    private Integer roomNumber;
    private String roomType;
    private String status;
    private Double price;
    private String notes;

    public roomData(Integer roomNumber, String roomType, String status,  String notes,Double price){
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.status = status;
        this.price = price;
        this.notes = notes;

    }

    public Integer getRoomNumber(){
        return roomNumber;
    }
    public String getRoomType(){
        return roomType;
    }
    public String getStatus(){
        return status;
    }
    public Double getPrice(){
        return price;
    }
    public String getNotes(){
        return notes;
    }
}
