module hms.hms {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;

    opens hms.hms to javafx.fxml;
    exports hms.hms;
}