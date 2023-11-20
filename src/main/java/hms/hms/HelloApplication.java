package hms.hms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.input.MouseEvent;

import java.io.IOException;



public class HelloApplication extends Application {
    private double x = 0;
    private double y = 0;
    private static Scene scene;
    private double gapX = 0, gapY = 0;

    @Override
    public void start(Stage stage) throws IOException {
        //FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        Scene scene = new Scene(root);
        //Scene scene = new Scene(fxmlLoader.load(), 600, 500);

        stage.setResizable(false);
        stage.initStyle(StageStyle.DECORATED.UNDECORATED);
        root.setOnMouseDragged(e -> this.dragStage(e, stage));
        root.setOnMouseMoved(e -> this.calculateGap(e, stage));

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("Hello!");
        stage.setScene(scene);

        stage.show();
    }

    private void calculateGap(MouseEvent event, Stage stage) {
        gapX = event.getScreenX() - stage.getX();
        gapY = event.getScreenY() - stage.getY();
    }
    private void dragStage(MouseEvent event, Stage stage) {
        stage.setX(event.getScreenX() - gapX);
        stage.setY(event.getScreenY() - gapY);
    }


    public static void main(String[] args) {
        launch();
    }
}