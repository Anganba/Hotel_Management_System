package hms.hms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class dashboardController implements Initializable {

    @FXML
    private AnchorPane main_form;

    @FXML
    private Button availableRooms_BTN;

    @FXML
    private Button availableRooms_addBTN;

    @FXML
    private Button availableRooms_checkInBTN;

    @FXML
    private Button availableRooms_clearBTN;

    @FXML
    private TableColumn<roomData, String> availableRooms_col_notes;

    @FXML
    private TableColumn<roomData, Double> availableRooms_col_price;

    @FXML
    private TableColumn<roomData, String> availableRooms_col_roomNumber;

    @FXML
    private TableColumn<roomData, String> availableRooms_col_roomType;

    @FXML
    private TableColumn<roomData, String> availableRooms_col_status;

    @FXML
    private Button availableRooms_deleteBTN;

    @FXML
    private AnchorPane availableRooms_form;

    @FXML
    private TextField availableRooms_notes;

    @FXML
    private TextField availableRooms_price;

    @FXML
    private TextField availableRooms_roomNumber;

    @FXML
    private ComboBox<?> availableRooms_roomType;

    @FXML
    private TextField availableRooms_search;

    @FXML
    private ComboBox<?> availableRooms_status;

    @FXML
    private TableView<roomData> availableRooms_tableView;

    @FXML
    private Button availableRooms_updateBTN;

    @FXML
    private Button closeBTN;

    @FXML
    private Button customers_BTN;






    @FXML
    private TableView<customerData> customers_tableView;

    @FXML
    private AnchorPane customers_form;
    @FXML
    private TableColumn<customerData, String> customers_customerNumber;
    @FXML
    private TableColumn<customerData, String> customers_firstName;
    @FXML
    private TableColumn<customerData, String> customers_lastName;
    @FXML
    private TableColumn<customerData, String> customers_phoneNumber;
    @FXML
    private TableColumn<customerData, String> customers_checkIn;
    @FXML
    private TableColumn<customerData, String> customers_checkOut;
    @FXML
    private TableColumn<customerData, Double> customers_totalPayment;



    @FXML
    private Button dashboard_BTN;

    @FXML
    private AreaChart<?, ?> dashboard_areaChart;

    @FXML
    private Label dashboard_bookToday;

    @FXML
    private AnchorPane dashboard_form;

    @FXML
    private Label dashboard_incomeToday;

    @FXML
    private Label dashboard_incomeTotal;

    @FXML
    private Button minimizeBTN;

    @FXML
    private Button signOut_BTN;

    @FXML
    private Label username;

    // Database Tools
    private Connection connect;
    private PreparedStatement prepare;
    private PreparedStatement prepare2;
    private Statement statement;
    protected Statement statement2;
    private ResultSet result;
    private ResultSet result2;

    private int count;
    public void dashboardCountBookToday(){
        Date date = new Date();
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        String sql = "SELECT COUNT(id) FROM customer WHERE checkin ='"+sqlDate+"'";
        connect = database.connectDb();
        count = 0;
        try{
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            while(result.next()){
                count = result.getInt("COUNT(id)");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void dashboardDisplayBookToday(){
        dashboardCountBookToday();
        dashboard_bookToday.setText(String.valueOf(count));
    }

    private double sumToday =0;
    public void dashboardSumIncomeToday(){
        Date date = new Date();
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        String sql = "SELECT SUM(total) FROM customer_receipt WHERE date = '"+sqlDate+"'";
        connect = database.connectDb();
        try{
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while(result.next()){
                sumToday = result.getDouble("SUM(total)");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void dashboardDisplayIncomeToday(){
        dashboardSumIncomeToday();
        dashboard_incomeToday.setText("$ "+String.valueOf(sumToday));
    }


    private double overall = 0;
    public void dashboardSumTotalIncome(){
        String sql = "SELECT SUM(total) FROM customer_receipt ";

        connect = database.connectDb();
        try{
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while(result.next()){
                overall = result.getDouble("SUM(total)");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void dashboardTotalIncome(){
        dashboardSumTotalIncome();
        dashboard_incomeTotal.setText("$ "+String.valueOf(overall));
    }

    public void displayUsername(){
        username.setText(getData.username);
    }

    public void dashboardChart(){
        dashboard_areaChart.getData().clear();
        String sql = "SELECT date, total FROM customer_receipt GROUP BY date ORDER BY TIMESTAMP(date) ASC LIMIT 8";

        connect = database.connectDb();
        XYChart.Series chart = new XYChart.Series();
        try{
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while(result.next()){
                chart.getData().add(new XYChart.Data(result.getString(1),result.getInt(2)));

            }
            dashboard_areaChart.getData().add(chart);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ObservableList<roomData> availableRoomsListData(){
        ObservableList<roomData> listData = FXCollections.observableArrayList();

        String sql = "SELECT * FROM room";
        connect = database.connectDb();
        try{
            roomData roomD;
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()){
                roomD = new roomData(result.getInt("roomNumber"),
                        result.getString("type"),
                        result.getString("status"),
                        result.getString("notes"),
                        result.getDouble("price")
                );
                listData.add(roomD);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return listData;
    }

    private ObservableList<roomData> roomDatalist;
    public void availableRoomsShowData(){
        roomDatalist = availableRoomsListData();
        availableRooms_col_roomNumber.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        availableRooms_col_roomType.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        availableRooms_col_status.setCellValueFactory(new PropertyValueFactory<>("status"));
        availableRooms_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        availableRooms_col_notes.setCellValueFactory(new PropertyValueFactory<>("notes"));

        availableRooms_tableView.setItems(roomDatalist);
    }

    public void availableRoomsSelectData(){
        roomData roomD = availableRooms_tableView.getSelectionModel().getSelectedItem();
        int num = availableRooms_tableView.getSelectionModel().getSelectedIndex();

        if((num - 1 ) < -1){
            return;
        }
        availableRooms_roomNumber.setText(String.valueOf(roomD.getRoomNumber()));
        availableRooms_price.setText(String.valueOf((roomD.getPrice())));
        availableRooms_notes.setText(String.valueOf(roomD.getNotes()));
        // need to work on status and type if it's possible

    }
    public void availableRoomsAdd(){
        String sql = "INSERT INTO room (roomNumber,type,status,notes,price) VALUES (?,?,?,?,?)";

        connect = database.connectDb();

        try{
            String roomNumber = availableRooms_roomNumber.getText();
            String type = (String) availableRooms_roomType.getSelectionModel().getSelectedItem();
            String status = (String) availableRooms_status.getSelectionModel().getSelectedItem();
            String price = availableRooms_price.getText();
            String notes = availableRooms_notes.getText();

            Alert alert;
            if (roomNumber.isEmpty() || type.isEmpty() || status.isEmpty() || price.isEmpty() || notes.isEmpty() ){
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please fill all blank fields");
                alert.showAndWait();
            }else{

                String check = "SELECT roomNumber FROM room WHERE roomNumber = '"+roomNumber+"' ";
                prepare = connect.prepareStatement(check);
                result = prepare.executeQuery();

                //Check if room Number already Exist!
                if(result.next()){
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Room #"+roomNumber+" already exist!");
                    alert.showAndWait();
                }
                else{
                    prepare = connect.prepareStatement(sql);
                    prepare.setString(1,roomNumber);
                    prepare.setString(2,type);
                    prepare.setString(3,status);
                    prepare.setString(4,notes);
                    prepare.setString(5,price);

                    prepare.executeUpdate();

                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully added!");
                    alert.showAndWait();

                    //To update the data on the table view
                    availableRoomsShowData();
                    //To clear the fields
                    availableRoomsClear();
                }

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void availableRoomsUpdate(){
        String type1 = (String) availableRooms_roomType.getSelectionModel().getSelectedItem();
        String status1 = (String) availableRooms_status.getSelectionModel().getSelectedItem();
        String price1 = availableRooms_price.getText();
        String notes1 = availableRooms_notes.getText();
        String roomNumber1 = availableRooms_roomNumber.getText();

        String sql = "UPDATE room SET type = '"+type1+"'," + "status = '"+status1+"', " +
                "price = '"+price1+"', notes ='"+notes1+"' WHERE roomNumber= '"+roomNumber1+"'";

        connect = database.connectDb();
        try{
            Alert alert;

            if(type1.isEmpty() || status1.isEmpty() || price1.isEmpty() || notes1.isEmpty()||roomNumber1.isEmpty()){
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please Select the room data first!");
                alert.showAndWait();
            }else{
                prepare = connect.prepareStatement(sql);
                prepare.executeUpdate();
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Message");
                alert.setHeaderText(null);
                alert.setContentText("Successfully Updated!");
                alert.showAndWait();

                //To show the updated the tableview
                availableRoomsShowData();
                availableRoomsClear();

            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void availableRoomsDelete(){
        // Resume Here!
        String type1 = (String) availableRooms_roomType.getSelectionModel().getSelectedItem();
        String status1 = (String) availableRooms_status.getSelectionModel().getSelectedItem();
        String price1 = availableRooms_price.getText();
        String notes1 = availableRooms_notes.getText();
        String roomNumber1 = availableRooms_roomNumber.getText();
        String deleteData = "DELETE FROM room WHERE roomNumber = '"+roomNumber1+"'";

        connect = database.connectDb();
        try{
            Alert alert;
            if(type1.isEmpty() || status1.isEmpty() || price1.isEmpty() || notes1.isEmpty() || roomNumber1.isEmpty()){
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please select the data first!");
                alert.showAndWait();
            }
            else{

                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Message");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure you want to delete Room #"+roomNumber1+" ?");
                Optional<ButtonType>  option = alert.showAndWait();

                if(option.get().equals(ButtonType.OK)){
                    statement = connect.createStatement();
                    statement.executeUpdate(deleteData);

                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully Deleted Room #"+roomNumber1+" !");
                    alert.showAndWait();

                    // To show the updated Table View
                    availableRoomsShowData();
                    availableRoomsClear();
                }else{
                    return;
                }
            }

        }
        catch (Exception e){e.printStackTrace();}
    }
    public void availableRoomsClear(){
        availableRooms_roomNumber.setText("");
        availableRooms_roomType.getSelectionModel().clearSelection();
        availableRooms_status.getSelectionModel().clearSelection();
        availableRooms_price.setText("");
        availableRooms_notes.setText("");
    }

    public void availableRoomsCheckIn(){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("checkin.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.initStyle(StageStyle.DECORATED.UNDECORATED);
            root.setOnMouseDragged(e -> this.dragStage(e, stage));
            root.setOnMouseMoved(e -> this.calculateGap(e, stage));

            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();

            //availableRooms_checkInBTN.getScene().getWindow().hide();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private String type[] = {"Single Room", "Double Room", "Triple Room", "Quad Room", "King Room"};
    public void availableRoomsRoomType(){
        List<String> listData = new ArrayList<>();
        for(String data: type){
            listData.add(data);
        }
        ObservableList list = FXCollections.observableArrayList(listData);
        availableRooms_roomType.setItems(list);
    }

    private String status[] = {"Available", "Not Available", "Occupied"};
    public void availableRoomsStatus(){
        List<String> listdata = new ArrayList<>();
        for (String data: status){
            listdata.add(data);
        }
        ObservableList list = FXCollections.observableArrayList(listdata);
        availableRooms_status.setItems(list);
    }

    private static Scene scene;
    private double gapX = 0, gapY = 0;

    private void calculateGap(MouseEvent event, Stage stage) {
        gapX = event.getScreenX() - stage.getX();
        gapY = event.getScreenY() - stage.getY();
    }
    private void dragStage(MouseEvent event, Stage stage) {
        stage.setX(event.getScreenX() - gapX);
        stage.setY(event.getScreenY() - gapY);
    }

    public ObservableList<customerData> customerListData(){
        ObservableList<customerData> listData = FXCollections.observableArrayList();
        String sql = "SELECT * FROM customer";
                connect = database.connectDb();
        try{
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            customerData custD;
            while(result.next()){
                custD = new customerData(result.getInt("customer_id"),result.getString("firstName"),result.getString("lastName"),result.getString("phoneNumber"),result.getDouble("total"),result.getDate("checkIn"),result.getDate("checkOut"));
                listData.add(custD);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return listData;
    }

    private ObservableList<customerData> listCustomerData;
    private void customerShowData(){
        listCustomerData = customerListData();
        customers_customerNumber.setCellValueFactory(new PropertyValueFactory<>("customerNum"));
        customers_firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        customers_lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        customers_phoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        customers_totalPayment.setCellValueFactory(new PropertyValueFactory<>("total"));
        customers_checkIn.setCellValueFactory(new PropertyValueFactory<>("checkIn"));
        customers_checkOut.setCellValueFactory(new PropertyValueFactory<>("checkOut"));

        customers_tableView.setItems(listCustomerData);
    }

    public void openDashboard(){
        dashboard_form.setVisible(true);
        availableRooms_form.setVisible(false);
        customers_form.setVisible(false);
        dashboardDisplayBookToday();
        dashboardDisplayIncomeToday();
        dashboardTotalIncome();
        dashboardChart();
    }
    public void switchForm(ActionEvent event){
        if(event.getSource() ==dashboard_BTN){
            openDashboard();
        } else if (event.getSource()==availableRooms_BTN) {
            dashboard_form.setVisible(false);
            availableRooms_form.setVisible(true);
            customers_form.setVisible(false);
            availableRoomsShowData();
        } else if (event.getSource()==customers_BTN) {
            dashboard_form.setVisible(false);
            availableRooms_form.setVisible(false);
            customers_form.setVisible(true);
            customerShowData();
        }
    }

    public void signout(){
        try{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Message");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to sign out?");

            Optional<ButtonType> option = alert.showAndWait();

            if(option.get().equals(ButtonType.OK)){
                Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.initStyle(StageStyle.DECORATED.UNDECORATED);
                root.setOnMouseDragged(e -> this.dragStage(e, stage));
                root.setOnMouseMoved(e -> this.calculateGap(e, stage));

                stage.initStyle(StageStyle.TRANSPARENT);
                stage.setScene(scene);
                stage.show();

                signOut_BTN.getScene().getWindow().hide();
            }
            else{
                return;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void close(){
        System.exit(0);
    }
    public void minimize(){
        Stage stage = (Stage)main_form.getScene().getWindow();
        stage.setIconified(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        availableRoomsRoomType();
        availableRoomsStatus();
        availableRoomsShowData();
        dashboardDisplayBookToday();
        customerShowData();
        dashboardDisplayIncomeToday();
        dashboardTotalIncome();
        dashboardChart();
        displayUsername();

        openDashboard();
    }
}
