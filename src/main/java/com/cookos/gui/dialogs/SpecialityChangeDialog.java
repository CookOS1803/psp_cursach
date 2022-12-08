package com.cookos.gui.dialogs;

import com.cookos.model.Speciality;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;

public class SpecialityChangeDialog extends ChangeDialog<Speciality> {
    
    private TextField nameField = new TextField();
    private TextField mult5Field = new TextField();
    private TextField mult6Field = new TextField();
    private TextField mult7Field = new TextField();
    private TextField mult8Field = new TextField();
    private TextField mult9Field = new TextField();

    public SpecialityChangeDialog() {
        nameField.setPromptText("Название");
        mult5Field.setPromptText("Множитель 5-5.9");
        mult6Field.setPromptText("Множитель 6-6.9");
        mult7Field.setPromptText("Множитель 7-7.9");
        mult8Field.setPromptText("Множитель 8-8.9");
        mult9Field.setPromptText("Множитель 9-10");

        contentVbox.getChildren().addAll(nameField, mult5Field, mult6Field, mult7Field, mult8Field, mult9Field);

        setResultConverter(button -> {            
            if (button.getButtonData() == ButtonData.CANCEL_CLOSE) {
                return null;
            }

            var alert = new Alert(AlertType.ERROR);            

            if (
                nameField.getText().isBlank() ||
                mult5Field.getText().isBlank() ||
                mult6Field.getText().isBlank() ||
                mult7Field.getText().isBlank() ||
                mult8Field.getText().isBlank() ||
                mult9Field.getText().isBlank()
            ) {
                alert.setHeaderText("Заполните все поля");
                alert.show();

                return null;
            }

            float mult5, mult6, mult7, mult8, mult9;

            try {
                mult5 = Float.parseFloat(mult5Field.getText().strip());
                mult6 = Float.parseFloat(mult6Field.getText().strip());
                mult7 = Float.parseFloat(mult7Field.getText().strip());
                mult8 = Float.parseFloat(mult8Field.getText().strip());
                mult9 = Float.parseFloat(mult9Field.getText().strip());

                if (mult5 < 0 || mult6 < 0 || mult7 < 0 || mult8 < 0 || mult9 < 0) {
                    alert.setHeaderText("Множители должны быть положительными");
                    alert.show();

                    return null;
                }
            } catch (Exception e) {
                alert.setHeaderText("Множители должны быть числами");
                alert.show();

                return null;
            }

            changeableValue.setName(nameField.getText().strip());
            changeableValue.setMult5(mult5);
            changeableValue.setMult6(mult6);
            changeableValue.setMult7(mult7);
            changeableValue.setMult8(mult8);
            changeableValue.setMult9(mult9);

            return changeableValue;
        });
    }

    @Override
    public void setChangeableValue(Speciality changeableValue) {
        super.setChangeableValue(changeableValue);

        nameField.setText(changeableValue.getName());
        mult5Field.setText(String.valueOf(changeableValue.getMult5()));
        mult6Field.setText(String.valueOf(changeableValue.getMult6()));
        mult7Field.setText(String.valueOf(changeableValue.getMult7()));
        mult8Field.setText(String.valueOf(changeableValue.getMult8()));
        mult9Field.setText(String.valueOf(changeableValue.getMult9()));
    }
}
