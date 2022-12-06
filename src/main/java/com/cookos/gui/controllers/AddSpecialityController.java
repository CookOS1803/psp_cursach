package com.cookos.gui.controllers;

import com.cookos.model.Speciality;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class AddSpecialityController extends AdminSubController {
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField mult5Field;
    @FXML private TextField mult6Field;
    @FXML private TextField mult7Field;
    @FXML private TextField mult8Field;
    @FXML private TextField mult9Field;

    @FXML
    private void addSpeciality() {
        var alert = new Alert(AlertType.ERROR);

        if (
            idField.getText().isBlank() ||
            nameField.getText().isBlank() ||
            mult5Field.getText().isBlank() ||
            mult6Field.getText().isBlank() ||
            mult7Field.getText().isBlank() ||
            mult8Field.getText().isBlank() ||
            mult9Field.getText().isBlank()
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

        float mult5, mult6, mult7, mult8, mult9;

        try {
            mult5 = Float.parseFloat(mult5Field.getText().strip());
            mult6 = Float.parseFloat(mult6Field.getText().strip());
            mult7 = Float.parseFloat(mult7Field.getText().strip());
            mult8 = Float.parseFloat(mult8Field.getText().strip());
            mult9 = Float.parseFloat(mult9Field.getText().strip());

            if (mult5 < 0 || mult6 < 0 || mult7 < 0 || mult8 < 0 || mult9 < 0) {
                alert.setHeaderText("Multipliers must be positive numbers");
                alert.show();
                return;
            }
        } catch (Exception e) {
            alert.setHeaderText("Multipliers must be real numbers");
            alert.show();
            return;
        }

        var speciality = Speciality.builder()
                                   .id(id)
                                   .name(nameField.getText().strip())
                                   .mult5(mult5)
                                   .mult6(mult6)
                                   .mult7(mult7)
                                   .mult8(mult8)
                                   .mult9(mult9)
                                   .build();
        adminMenuController.addModel(speciality);
    }
}
