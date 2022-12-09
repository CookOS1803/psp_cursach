package com.cookos.gui.controllers;

import com.cookos.model.Subject;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;

public class AddSubjectController extends AdminSubController {
    
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField hoursField;

    @FXML
    private void addSubject() {
        var alert = new Alert(AlertType.ERROR);
        
        if (
            idField.getText().isBlank() ||
            nameField.getText().isBlank() ||
            hoursField.getText().isBlank()
        ) {
            alert.setHeaderText("Заполните все поля");
            alert.show();
            return;
        }

        int id;

        try {
            id = Integer.valueOf(idField.getText().strip());
            
            if (id < 0) {
                alert.setHeaderText("ID должно быть положительным");
                alert.show();
                return;
            }
        } catch (Exception e) {
            alert.setHeaderText("ID должно быть числом");
            alert.show();
            return;
        }

        int hours;

        try {
            hours = Integer.valueOf(hoursField.getText().strip());

            if (hours < 0) {
                alert.setHeaderText("Часы должны быть положительным числом");
                alert.show();
                return;
            }
        } catch (Exception e) {
            alert.setHeaderText("Часы должны быть числом");
            alert.show();
            return;
        }

        var subject = Subject.builder()
                             .id(id)
                             .name(nameField.getText().strip())
                             .hours(hours)
                             .build();
        adminMenuController.addModel(subject);
    }
}
