module com.cookos {
    //requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;
    requires lombok;
    requires org.hibernate.orm.core;

    opens com.cookos to javafx.fxml;
    opens com.cookos.controllers to javafx.fxml;
    opens com.cookos.model to org.hibernate.orm.core;
    exports com.cookos;
    exports com.cookos.controllers;
}
