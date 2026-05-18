module com.example.attendance_management {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.github.librepdf.openpdf;
    requires java.desktop;

    opens com.example.attendance_management to javafx.fxml;
    exports com.example.attendance_management;
}
