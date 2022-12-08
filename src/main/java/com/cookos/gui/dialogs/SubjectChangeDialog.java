package com.cookos.gui.dialogs;

import com.cookos.model.Subject;

import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;

public class SubjectChangeDialog extends ChangeDialog<Subject> {
    
    private TextField nameField = new TextField();
    private TextField hoursField = new TextField();

    public SubjectChangeDialog() {
        
        nameField.setPromptText("Название");
        hoursField.setPromptText("Общее количество часов");

        contentVbox.getChildren().addAll(nameField, hoursField);

        setResultConverter(button -> {
            if (button.getButtonData() == ButtonData.CANCEL_CLOSE) {
                return null;
            }

            var alert = new Alert(AlertType.ERROR);

            if (
                nameField.getText().isBlank() ||
                hoursField.getText().isBlank()
            ) {
                alert.setHeaderText("Заполните все поля");
                alert.show();

                return null;
            }

            int hours;

            try {
                hours = Integer.valueOf(hoursField.getText());

                if (hours < 0) {                    
                    alert.setHeaderText("Часы должны быть положительным числом");
                    alert.show();

                    return null;
                }
                
            } catch (Exception e) {
                alert.setHeaderText("Часы должны быть числом");
                alert.show();
                
                return null;
            }

            changeableValue.setName(nameField.getText().strip());
            changeableValue.setHours(hours);

            return changeableValue;
        });
    }

    @Override
    public void setChangeableValue(Subject changeableValue) {
        super.setChangeableValue(changeableValue);

        nameField.setText(changeableValue.getName());
        hoursField.setText(String.valueOf(changeableValue.getHours()));
    }
}
