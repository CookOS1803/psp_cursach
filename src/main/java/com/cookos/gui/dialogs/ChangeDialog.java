package com.cookos.gui.dialogs;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar.ButtonData;
import lombok.Getter;
import lombok.Setter;

public class ChangeDialog<R> extends Dialog<R> {

    @Getter @Setter protected R changeableValue;
    
    public ChangeDialog() {
        super();

        getDialogPane().getButtonTypes().add(new ButtonType("Ok", ButtonData.OK_DONE));
        getDialogPane().getButtonTypes().add(new ButtonType("Cancel", ButtonData.CANCEL_CLOSE));

        setHeaderText("Write new data");
    }
}
