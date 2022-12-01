package com.cookos.util;

import java.io.IOException;

import com.cookos.Client;

import javafx.fxml.FXMLLoader;
import javafx.scene.*;

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
}
