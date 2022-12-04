package com.cookos.controllers;

import com.cookos.model.EducationForm;
import com.cookos.model.Student;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import lombok.Setter;

public class AddStudentController {
    @FXML private TextField idField;
    @FXML private TextField lastNameField;
    @FXML private TextField firstNameField;
    @FXML private TextField patronymicField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private TextField emailField;
    @FXML private TextField specialityField;
    @FXML private RadioButton paidButton;
    @FXML private RadioButton freeButton;

    @Setter private AdminMenuController adminMenuController;

    @FXML
    private void addStudent() {
        var alert = new Alert(AlertType.ERROR);
        
        if (
            idField.getText().isBlank() ||
            lastNameField.getText().isBlank() ||
            firstNameField.getText().isBlank() ||
            patronymicField.getText().isBlank() ||
            phoneField.getText().isBlank() ||
            addressField.getText().isBlank() ||
            emailField.getText().isBlank() ||
            specialityField.getText().isBlank()
        ) {
            alert.setHeaderText("All fields must be filled");
            alert.show();
            return;
        }

        int id, specialityId;

        try {
            id = Integer.valueOf(idField.getText().strip());
        } catch (Exception e) {
            alert.setHeaderText("Id must be numeric value");
            alert.show();
            return;
        }

        try {
            specialityId = Integer.valueOf(specialityField.getText().strip());
        } catch (Exception e) {
            alert.setHeaderText("Speciality id must be numeric value");
            alert.show();
            return;
        }

        var form = paidButton.isSelected() ? EducationForm.Paid : EducationForm.Free;

        var student = Student.builder()
                             .id(id)
                             .lastName(lastNameField.getText().strip())
                             .firstName(firstNameField.getText().strip())
                             .patronymic(patronymicField.getText().strip())
                             .phone(phoneField.getText().strip())
                             .email(emailField.getText().strip())
                             .educationForm(form)
                             .specialityId(specialityId)
                             .build();
        
        adminMenuController.addModel(student);
    }
}
