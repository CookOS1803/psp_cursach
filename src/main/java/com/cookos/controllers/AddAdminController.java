package com.cookos.controllers;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import com.cookos.model.User;
import com.cookos.model.UserRole;
import com.cookos.util.HashPassword;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;

public class AddAdminController extends AdminSubController {
    @FXML private TextField loginField;
    @FXML private TextField passwordField;

    @FXML
    private void addAdmin() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        var alert = new Alert(AlertType.ERROR);
        
        if (
            loginField.getText().isBlank() ||
            passwordField.getText().isEmpty()
        ) {
            alert.setHeaderText("All fields must be filled");
            alert.show();
            return;
        }

        var admin = User.builder()
                        .login(loginField.getText().strip())
                        .password(HashPassword.getHash(passwordField.getText()))
                        .role(UserRole.Admin)
                        .build();
        adminMenuController.addModel(admin);
    }
}
