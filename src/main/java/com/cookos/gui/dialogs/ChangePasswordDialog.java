package com.cookos.gui.dialogs;

import java.util.Arrays;

import com.cookos.model.User;
import com.cookos.util.HashPassword;

import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import lombok.Getter;
import lombok.Setter;

public class ChangePasswordDialog extends ChangeDialog<User> {
    
    @Getter @Setter private byte[] oldPasswordHash;
    private PasswordField oldPasswordField = new PasswordField();
    private PasswordField newPasswordField = new PasswordField();
    private PasswordField repeatPasswordField = new PasswordField();

    public ChangePasswordDialog(User user) {
        super();

        oldPasswordHash = user.getPassword();

        oldPasswordField.setPromptText("Старый пароль");
        newPasswordField.setPromptText("Новый пароль");
        repeatPasswordField.setPromptText("Повторите новый пароль");

        contentVbox.getChildren().addAll(oldPasswordField, newPasswordField, repeatPasswordField);

        setResultConverter(button -> {
            if (button.getButtonData() == ButtonData.CANCEL_CLOSE) {
                return null;
            }

            var alert = new Alert(AlertType.ERROR);

            if (
                oldPasswordField.getText().isEmpty() ||
                newPasswordField.getText().isEmpty() ||
                repeatPasswordField.getText().isEmpty()
            ) {
                alert.setHeaderText("Заполните все поля");
                alert.show();

                return null;
            }
            
            try {
                if (!Arrays.equals(oldPasswordHash, HashPassword.getHash(oldPasswordField.getText()))) {    
                    alert.setHeaderText("Неверный старый пароль");
                    alert.show();
                    
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            if (!newPasswordField.getText().equals(repeatPasswordField.getText())) {
                alert.setHeaderText("Пароли не совпадают");
                alert.show();
                
                return null;
            }

            try {
                user.setPassword(HashPassword.getHash(newPasswordField.getText()));
                return user;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }
}
