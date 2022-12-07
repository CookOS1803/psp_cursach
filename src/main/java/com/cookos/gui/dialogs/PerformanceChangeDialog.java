package com.cookos.gui.dialogs;

import com.cookos.model.Performance;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;

public class PerformanceChangeDialog extends ChangeDialog<Performance> {

    private TextField totalScoreField = new TextField();
    private TextField missedHoursField = new TextField();
    
    public PerformanceChangeDialog() {
        super();

        totalScoreField.setPromptText("Total score");
        missedHoursField.setPromptText("Missed hours");

        contentVbox.getChildren().addAll(totalScoreField, missedHoursField);

        setResultConverter(button -> {
            if (button.getButtonData() == ButtonData.CANCEL_CLOSE) {
                return null;
            }

            var alert = new Alert(AlertType.ERROR);

            if (
                totalScoreField.getText().isBlank() ||
                missedHoursField.getText().isBlank()
            ) {
                alert.setHeaderText("All fields must be filled");
                alert.show();

                return null;
            }

            int hours;
            float score;
            
            try {
                hours = Integer.valueOf(missedHoursField.getText());
            } catch (Exception e) {
                alert.setHeaderText("Missed hours must be numeric value");
                alert.show();

                return null;
            }

            try {
                score = Float.valueOf(totalScoreField.getText());
            } catch (Exception e) {
                alert.setHeaderText("Total score must be numeric value");
                alert.show();

                return null;
            }

            changeableValue.setMissedHours(hours);
            changeableValue.setTotalScore(score);

            return changeableValue;
        });
    }

    @Override
    public void setChangeableValue(Performance changeableValue) {
        super.setChangeableValue(changeableValue);

        totalScoreField.setText(String.valueOf(changeableValue.getTotalScore()));
        missedHoursField.setText(String.valueOf(changeableValue.getMissedHours()));
    }
}
