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
                alert.setHeaderText("Заполните все поля");
                alert.show();

                return null;
            }

            int hours;
            float score;
            
            try {
                hours = Integer.valueOf(missedHoursField.getText());

                if (hours < 0) {
                    alert.setHeaderText("Пропущенные часы должны быть положительным числом");
                    alert.show();

                    return null;
                }
            } catch (Exception e) {
                alert.setHeaderText("Пропущенные часы должны быть числом");
                alert.show();

                return null;
            }

            try {
                score = Float.valueOf(totalScoreField.getText());

                if (score < 0) {
                    alert.setHeaderText("Общий балл должен быть положительным");
                    alert.show();

                    return null;
                }
            } catch (Exception e) {
                alert.setHeaderText("Общий балл должен быть числом");
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
