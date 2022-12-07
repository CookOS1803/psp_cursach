package com.cookos.gui.dialogs;

import com.cookos.model.Student;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;

public class ChangeInfoDialog extends ChangeDialog<Student> {
    @FXML private TextField phoneField = new TextField();
    @FXML private TextField emailField = new TextField();

    public ChangeInfoDialog(Student student) {
        super();

        phoneField.setPromptText("Номер телефона");
        emailField.setPromptText("Email");

        contentVbox.getChildren().addAll(phoneField, emailField);

        setResultConverter(button -> {
            if (button.getButtonData() == ButtonData.CANCEL_CLOSE) {
                return null;
            }

            var alert = new Alert(AlertType.ERROR);

            if (
                phoneField.getText().isBlank() ||
                emailField.getText().isBlank() 
            ) {
                alert.setHeaderText("Заполните все поля");
                alert.show();

                return null;
            }

            student.setPhone(phoneField.getText().strip());
            student.setEmail(emailField.getText().strip());

            return student;
        });
    }
}
