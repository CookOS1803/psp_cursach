package com.cookos.gui.dialogs;

import com.cookos.model.Student;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;

public class StudentChangeDialog extends ChangeDialog<Student> {
    
    private TextField lastNameField = new TextField();
    private TextField firstNameField = new TextField();
    private TextField patronymicField = new TextField();
    private TextField phoneField = new TextField();
    private TextField addressField = new TextField();
    private TextField emailField = new TextField();

    public StudentChangeDialog() {
        lastNameField.setPromptText("Фамилия");
        firstNameField.setPromptText("Имя");
        patronymicField.setPromptText("Отчество");
        phoneField.setPromptText("Телефон");
        addressField.setPromptText("Адрес");
        emailField.setPromptText("Email");

        contentVbox.getChildren().addAll(lastNameField, firstNameField, patronymicField, phoneField, addressField, emailField);

        setResultConverter(button -> {
            if (button.getButtonData() == ButtonData.CANCEL_CLOSE) {
                return null;
            }

            var alert = new Alert(AlertType.ERROR);

            if (
                lastNameField.getText().isBlank() ||
                firstNameField.getText().isBlank() ||
                patronymicField.getText().isBlank() ||
                phoneField.getText().isBlank() ||
                addressField.getText().isBlank() ||
                emailField.getText().isBlank()
            ) {
                alert.setHeaderText("Заполните все поля");
                alert.show();

                return null;
            }

            changeableValue.setLastName(lastNameField.getText().strip());
            changeableValue.setFirstName(firstNameField.getText().strip());
            changeableValue.setPatronymic(patronymicField.getText().strip());
            changeableValue.setPhone(phoneField.getText().strip());
            changeableValue.setAddress(addressField.getText().strip());
            changeableValue.setEmail(emailField.getText().strip());

            return changeableValue;
        });
    }

    @Override
    public void setChangeableValue(Student changeableValue) {
        super.setChangeableValue(changeableValue);

        lastNameField.setText(changeableValue.getLastName());
        firstNameField.setText(changeableValue.getFirstName());
        patronymicField.setText(changeableValue.getPatronymic());
        phoneField.setText(changeableValue.getPhone());
        addressField.setText(changeableValue.getAddress());
        emailField.setText(changeableValue.getEmail());
    }
}
