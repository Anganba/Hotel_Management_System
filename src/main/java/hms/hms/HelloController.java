package hms.hms;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HelloController {
    @FXML
    private Button closeBTN;



    @FXML
    private Button loginBTN;

    @FXML
    private AnchorPane main_form;

    @FXML
    private PasswordField password;

    @FXML
    private StackPane stack_form;

    @FXML
    private TextField username;

    public void exit(){
        System.exit(0);
    }

    // Database Tools
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

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

    public void login(){
        String user = username.getText();
        String pass = password.getText();
        String sql = "SELECT * FROM admin WHERE username = ? and password = ? ";

        connect = database.connectDb();

        try{
            assert connect != null;
            prepare = connect.prepareStatement(sql);
            prepare.setString(1,user);
            prepare.setString(2,pass);

            result = prepare.executeQuery();

            Alert alert;

            if(user.isEmpty() || pass.isEmpty()){
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please fill all the blank fields!");
                alert.showAndWait();
            }
            else {

                if (result.next()) {
                    getData.username = username.getText();
                    // If the info is correct the dashboard will open or ELSE will execute
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully Login!");
                    alert.showAndWait();

                    loginBTN.getScene().getWindow().hide();

                    Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
                    Stage stage = new Stage();
                    Scene scene = new Scene(root);

                    stage.initStyle(StageStyle.DECORATED.UNDECORATED);
                    root.setOnMouseDragged(e -> this.dragStage(e, stage));
                    root.setOnMouseMoved(e -> this.calculateGap(e, stage));

                    stage.initStyle(StageStyle.TRANSPARENT);
                    stage.setScene(scene);
                    stage.show();

                } else {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Incorrect Credentials!");
                    alert.showAndWait();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}