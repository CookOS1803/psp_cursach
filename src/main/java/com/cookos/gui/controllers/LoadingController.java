package com.cookos.gui.controllers;

import java.io.*;
import java.net.Socket;

import com.cookos.Client;
import com.cookos.util.FXMLHelpers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LoadingController {

    @FXML private Label label;

    public void setup() throws IOException {        
        try {
            Client.socket = new Socket("127.0.0.1", 8080);
            Client.ostream = new ObjectOutputStream(Client.socket.getOutputStream());
            Client.ostream.flush();
            Client.istream = new ObjectInputStream(Client.socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();

            Platform.runLater(() -> label.setText("Не удалось подключиться к серверу"));
            return;
        }

        FXMLHelpers.setRoot("loginscreen");
    }
    
}
