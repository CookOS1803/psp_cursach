package com.cookos.gui.dialogs;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

public class ChangeDialog<R> extends Dialog<R> {

    @Getter @Setter protected R changeableValue;
    protected VBox contentVbox = new VBox();
    
    public ChangeDialog() {
        super();

        getDialogPane().getButtonTypes().add(new ButtonType("Ok", ButtonData.OK_DONE));
        getDialogPane().getButtonTypes().add(new ButtonType("Cancel", ButtonData.CANCEL_CLOSE));
        getDialogPane().setContent(contentVbox);

        setHeaderText("Write new data");
    }
}
