module org.marketplace.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires static lombok;


    opens org.marketplace.client to javafx.fxml;
    exports org.marketplace.client;
}