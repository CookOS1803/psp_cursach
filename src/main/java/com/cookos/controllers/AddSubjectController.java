package com.cookos.controllers;

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
            alert.setHeaderText("All fields must be filled");
            alert.show();
            return;
        }

        int id;

        try {
            id = Integer.valueOf(idField.getText().strip());
        } catch (Exception e) {
            alert.setHeaderText("Id must be numeric value");
            alert.show();
            return;
        }

        int hours;

        try {
            hours = Integer.valueOf(hoursField.getText().strip());

            if (hours < 0) {
                alert.setHeaderText("Total hours must be positive number");
                alert.show();
                return;
            }
        } catch (Exception e) {
            alert.setHeaderText("Total hours must be numeric value");
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
