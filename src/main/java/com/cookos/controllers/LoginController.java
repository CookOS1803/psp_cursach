package com.cookos.controllers;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import com.cookos.Client;
import com.cookos.util.HashPassword;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML private Label resultLabel;
    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private Button submitButton;

    private String answer = null;
    
    @FXML
    private void submit() throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
        
        Client.ostream.writeObject(loginField.getText());
        Client.ostream.flush();

        var hash = HashPassword.getHash(passwordField.getText());
        Client.ostream.writeInt(hash.length);
        Client.ostream.flush();
        Client.ostream.write(hash, 0, hash.length);
        Client.ostream.flush();

        loginField.setDisable(true);
        passwordField.setDisable(true);
        submitButton.setDisable(true);

        new Thread(() -> {
            answer = "null";

            try {
                answer = (String)Client.istream.readObject();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }            

            Platform.runLater(() -> {
                resultLabel.setText(answer);
                loginField.setDisable(false);
                passwordField.setDisable(false);
                submitButton.setDisable(false);
            });
        }).start();
        
    }
}
