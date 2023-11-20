package hms.hms;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;

public class checkinController implements Initializable {

    @FXML
    private DatePicker checkin_Date;

    @FXML
    private AnchorPane checkin_form;

    @FXML
    private DatePicker checkout_Date;

    @FXML
    private Label customerNumber;

    @FXML
    private TextField email;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField phoneNumber;
    @FXML
    private Label total;

    @FXML
    private Label totalDays;

    @FXML
    private ComboBox<?> roomNumber;

    @FXML
    private ComboBox<?> roomType;

    //Database Tools
    private Connection connect;
    private PreparedStatement prepare;
    private Statement statement;
    private ResultSet result;


    public void customerCheckIn(){
        String insertCustomerData = "INSERT INTO customer (customer_id,total,roomType,roomNumber,firstName,lastName,phoneNumber,email,checkin,checkout) VALUES(?,?,?,?,?,?,?,?,?,?)";
        connect = database.connectDb();

        try{
            String customerNum = customerNumber.getText();
            String roomT = (String)roomType.getSelectionModel().getSelectedItem();
            String roomN = (String)roomNumber.getSelectionModel().getSelectedItem();
            String firstN = firstName.getText();
            String lastN = lastName.getText();
            String phoneNum = phoneNumber.getText();
            String email1 = email.getText();
            String checkinDate = String.valueOf(checkin_Date.getValue());
            String checkoutData = String.valueOf(checkout_Date.getValue());

            Alert alert;

            if(customerNum.isEmpty() || firstN.isEmpty()|| lastN.isEmpty()|| phoneNum.isEmpty()||checkinDate.isEmpty()||checkoutData.isEmpty()){
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please fill in all the blank fields!");
                alert.showAndWait();
            }else{

                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Message");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure ?");
                Optional<ButtonType> option = alert.showAndWait();

                String totalC = String.valueOf(totalP);
                if(option.get().equals(ButtonType.OK)){
                    prepare = connect.prepareStatement(insertCustomerData);
                    prepare.setString(1, customerNum);
                    prepare.setString(2, totalC);
                    prepare.setString(3, roomT);
                    prepare.setString(4, roomN);
                    prepare.setString(5, firstN);
                    prepare.setString(6, lastN);
                    prepare.setString(7, phoneNum);
                    prepare.setString(8, email1);
                    prepare.setString(9, checkinDate);
                    prepare.setString(10, checkoutData);

                    prepare.executeUpdate();

                    String date = String.valueOf(checkin_Date.getValue());

                    String customerN = customerNumber.getText();
                    String customerReceipt = "INSERT INTO customer_receipt (customer_num,total,date) VALUES(?,?,?)";

                    prepare = connect.prepareStatement(customerReceipt);
                    prepare.setString(1,customerN);
                    prepare.setString(2,totalC);
                    prepare.setString(3,date);

                    prepare.execute();

                    String sqlEditStatus = "UPDATE room SET status = 'Occupied' WHERE roomNumber = '"+roomN+"'";
                    statement = connect.createStatement();
                    statement.executeUpdate(sqlEditStatus);

                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully checked in!");
                    alert.showAndWait();

                    reset();
                }
                else{
                    return;
                }

            }

            prepare = connect.prepareStatement(insertCustomerData);
            prepare.setString(1, customerNum);


        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void reset(){
        firstName.setText("");
        lastName.setText("");
        phoneNumber.setText("");
        email.setText("");
        roomType.getSelectionModel().clearSelection();
        roomNumber.getSelectionModel().clearSelection();
        totalDays.setText("---");
        total.setText("$0.0");
    }

    public void totalDays(){

        Alert alert;
        if(checkout_Date.getValue().isAfter(checkin_Date.getValue())){
            getData.totalDays = checkout_Date.getValue().compareTo(checkin_Date.getValue());

        }
        else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Invalid Check Out Date!");
            alert.showAndWait();
        }
        displayTotal();

    }

    public void roomTypeList(){
        String listType = "SELECT * FROM room WHERE status ='Available' GROUP BY type ORDER BY type ASC";
        connect = database.connectDb();
        try{
            ObservableList listData = FXCollections.observableArrayList();
            prepare = connect.prepareStatement(listType);
            result = prepare.executeQuery();

            while(result.next()){
                listData.add(result.getString("type"));

            }
            roomType.setItems(listData);
            roomNumberList();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void roomNumberList(){

        String item = (String)roomType.getSelectionModel().getSelectedItem();
        String availableRoomNumber = "SELECT * FROM room WHERE type ='"+item+"' and status = 'Available'  ORDER BY roomNumber ASC";
        connect = database.connectDb();
        try{
            ObservableList listData = FXCollections.observableArrayList();
            prepare = connect.prepareStatement(availableRoomNumber);
            result = prepare.executeQuery();

            while(result.next()){
                listData.add(result.getString("roomNumber"));

            }
            roomNumber.setItems(listData);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private float totalP = 0;
    public void displayTotal(){
        String totalD = String.valueOf(getData.totalDays);
        totalDays.setText(totalD);

        String selectItem = (String)roomNumber.getSelectionModel().getSelectedItem();

        String sql = "SELECT * FROM room WHERE roomNumber = '"+selectItem+"'";
        double priceData = 0;
        connect = database.connectDb();
        try{
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while(result.next()){
                priceData = result.getDouble("price");
            }
            totalP = (float) ((priceData) * getData.totalDays);
            total.setText("$"+String.valueOf(totalP));

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void customerNumber(){
        String customerNum = "SELECT customer_id FROM customer";
        connect = database.connectDb();

        try{
            prepare = connect.prepareStatement(customerNum);
            result = prepare.executeQuery();
            while(result.next()){
                getData.customerNum = result.getInt("customer_id");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void displayCustomerNumber(){
        customerNumber();
        customerNumber.setText(String.valueOf(getData.customerNum+1));

    }


    public void close(){
        Platform.exit();
    }

    @FXML
    private void exitButtonOnAction(ActionEvent event){
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }
    public void minimize(){
        Stage stage = (Stage)checkin_form.getScene().getWindow();
        stage.setIconified(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displayCustomerNumber();
        roomTypeList();
        roomNumberList();
    }
}
