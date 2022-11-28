package com.cookos.controllers;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.cookos.dao.GenericDao;
import com.cookos.model.User;
import com.cookos.util.HashPassword;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML private Label resultLabel;
    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    private GenericDao<User> userDao;
    
    public LoginController() {
        userDao = new GenericDao<>(User.class);
    }

    @FXML
    private void submit() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        var user = userDao.findByColumn("login", loginField.getText());

        if (user == null) {
            resultLabel.setText("Wrong login");
            return;
        }

        if (!Arrays.equals(user.getPassword(), HashPassword.getHash(passwordField.getText()))) {
            resultLabel.setText("Wrong password");
            return;
        }

        resultLabel.setText(user.getRole().toString());
    }
}
