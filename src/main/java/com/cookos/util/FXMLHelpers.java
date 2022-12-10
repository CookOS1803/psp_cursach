package com.cookos.util;

import java.io.IOException;

import com.cookos.Client;

import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class FXMLHelpers {
    
    @SuppressWarnings("all")
    public static void setRoot(String fxml) throws IOException {
        Client.scene.setRoot(loadFXML(fxml));
    }

    @SuppressWarnings("all")
    public static Parent loadFXML(String fxml) throws IOException {        
        return makeLoader(fxml).load();
    }

    public static FXMLLoader makeLoader(String fxml) {
        return new FXMLLoader(Client.class.getResource(fxml + ".fxml"));
    }

    public static void onConnectionLost() {
        var alert = new Alert(AlertType.ERROR);

        alert.setHeaderText("Потеряно соединение с сервером");
        alert.showAndWait();

        Client.stage.close();
    }
}
