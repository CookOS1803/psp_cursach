package com.cookos.controllers;

import com.cookos.model.EducationForm;
import com.cookos.model.Student;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import lombok.Setter;

public class AddStudentMenuController {
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
        var form = paidButton.isSelected() ? EducationForm.Paid : EducationForm.Free;

        var student = Student.builder()
                             .id(Integer.valueOf(idField.getText().strip()))
                             .lastName(lastNameField.getText().strip())
                             .firstName(firstNameField.getText().strip())
                             .patronymic(patronymicField.getText().strip())
                             .phone(phoneField.getText().strip())
                             .email(emailField.getText().strip())
                             .educationForm(form)
                             .specialityId(Integer.valueOf(specialityField.getText().strip()))
                             .build();
        
        adminMenuController.addModel(student);
    }
}
