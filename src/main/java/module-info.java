module seeker1389.dolors.dolors {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jsoup;
    requires java.sql;

    opens seeker1389.dolors.dolors to javafx.fxml;
    exports seeker1389.dolors.dolors;
}