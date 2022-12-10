package com.cookos.gui.controllers;

import java.io.IOException;
import java.util.List;

import com.cookos.Client;
import com.cookos.gui.dialogs.ChangeInfoDialog;
import com.cookos.gui.dialogs.ChangePasswordDialog;
import com.cookos.net.*;
import com.cookos.util.FXMLHelpers;
import com.cookos.util.TableIntitializers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;

public class StudentMenuController {
    @FXML private TabPane tabPane;

    @FXML private Label fullNameLabel;
    @FXML private Label phoneLabel;
    @FXML private Label addressLabel;
    @FXML private Label emailLabel;

    @FXML private TableView<List<Object>> performanceTable;

    private Runnable updateInfoTask;
    private StudentModelBundle modelBundle;

    @FXML
    private void initialize() {
        TableIntitializers.addCellFactories(performanceTable);

        updateInfoTask = () -> {
            Platform.runLater(() -> tabPane.setDisable(true));
            
            try {
                modelBundle = (StudentModelBundle)Client.istream.readObject();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> FXMLHelpers.onConnectionLost());
                return;
            }

            Platform.runLater(() -> {
                fullNameLabel.setText("%s %s %s".formatted(
                    modelBundle.getStudent().getLastName(),
                    modelBundle.getStudent().getFirstName(),
                    modelBundle.getStudent().getPatronymic()
                ));
                phoneLabel.setText(modelBundle.getStudent().getPhone());
                addressLabel.setText(modelBundle.getStudent().getAddress());
                emailLabel.setText(modelBundle.getStudent().getEmail());
                TableIntitializers.performanceForStudent(modelBundle.getPerformance(), performanceTable);

                tabPane.setDisable(false);
            });
        };

        new Thread(updateInfoTask).start();
    }

    @FXML
    private void changeInfo() throws ClassNotFoundException, IOException {
        var dialog = new ChangeInfoDialog(modelBundle.getStudent());

        var answer = dialog.showAndWait();

        if (answer.isPresent()) {
            sendMessage(answer.get(), StudentOperationType.ChangeInfo);
        }
    }

    @FXML
    private void changePassword() throws IOException, ClassNotFoundException {
        var dialog = new ChangePasswordDialog(modelBundle.getUser());

        var answer = dialog.showAndWait();

        if (answer.isPresent()) {
            sendMessage(answer.get(), StudentOperationType.ChangePassword);
        }
    }

    @FXML
    private void calculateScholarship() {
        float scholarship;

        try {
            Client.ostream.writeObject(StudentMessage.builder()
                                                     .operationType(StudentOperationType.CalculateScholarship)
                                                     .build()
            );

            Client.ostream.flush();

            scholarship = Client.istream.readFloat();
        } catch (IOException e) {
            e.printStackTrace();
            FXMLHelpers.onConnectionLost();
            return;
        }

        if (scholarship < 0) {
            FXMLHelpers.onConnectionLost();
            return;
        }

        var alert = new Alert(AlertType.INFORMATION);        

        alert.setHeaderText("Итоговая стипендия: " + scholarship);
        alert.show();

        new Thread(updateInfoTask).start();
    }    

    private void sendMessage(Object value, StudentOperationType operationType){
        try {
            Client.ostream.writeObject(StudentMessage.builder()
                                                     .operationType(operationType)
                                                     .value(value)
                                                     .build());
            Client.ostream.flush();
            
            var message = (ServerMessage)Client.istream.readObject();

            if (message.getAnswerType() == AnswerType.Failure) {
                var alert = new Alert(AlertType.ERROR);
                alert.setHeaderText(message.getMessage());
                alert.show();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            FXMLHelpers.onConnectionLost();
            return;
        }

        

        new Thread(updateInfoTask).start();
    }
}
