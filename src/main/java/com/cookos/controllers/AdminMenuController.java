package com.cookos.controllers;

import java.io.IOException;
import java.util.List;

import com.cookos.Client;
import com.cookos.model.Model;
import com.cookos.net.*;
import com.cookos.util.FXMLHelpers;
import com.cookos.util.TableIntitializers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

public class AdminMenuController {

    @FXML private TabPane tabPane;
    @FXML private TitledPane addStudentPane;
    @FXML private FlowPane addAdminPane;
    private AddStudentController addStudentController;
    private AddAdminController addAdminController;

    @FXML private TableView<List<Object>> studentsTable;
    @FXML private TableView<List<Object>> specialitiesTable;
    @FXML private TableView<List<Object>> subjectsTable;
    @FXML private TableView<List<Object>> userStudentTable;
    @FXML private TableView<List<Object>> userAdminTable;
    private ModelBundle modelBundle;

    private Runnable updateTablesTask;
    
    @FXML
    private void initialize() throws ClassNotFoundException, IOException {

        var addStudentLoader = FXMLHelpers.makeLoader("addstudentmenu");
        addStudentPane.setContent((Pane)addStudentLoader.load());
        addStudentController = addStudentLoader.getController();
        addStudentController.setAdminMenuController(this);

        var addAdminLoader = FXMLHelpers.makeLoader("addadminmenu");
        addAdminPane.getChildren().add((Pane)addAdminLoader.load());
        addAdminController = addAdminLoader.getController();
        addAdminController.setAdminMenuController(this);
        
        TableIntitializers.addCellFactories(studentsTable);
        TableIntitializers.addCellFactories(specialitiesTable);
        TableIntitializers.addCellFactories(subjectsTable);
        TableIntitializers.addCellFactories(userStudentTable);
        TableIntitializers.addCellFactories(userAdminTable);

        updateTablesTask = () -> {            
            Platform.runLater(() -> tabPane.setDisable(true));

            try {
                modelBundle = (ModelBundle)Client.istream.readObject();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
                return;
            }

            Platform.runLater(() -> {
                TableIntitializers.students(modelBundle.getStudents(), studentsTable);
                TableIntitializers.specialities(modelBundle.getSpecialities(), specialitiesTable);
                TableIntitializers.subjects(modelBundle.getSubjects(), subjectsTable);
                TableIntitializers.users(modelBundle.getUsers(), userStudentTable, userAdminTable);
                

                tabPane.setDisable(false);
            });

        };

        new Thread(updateTablesTask).start();
    }

    @FXML
    private void onStudentsTableClick(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            var contextMenu = new ContextMenu();
            var removeItem = new MenuItem("Remove");
            var changeItem = new MenuItem("Change");

            contextMenu.getItems().addAll(removeItem, changeItem);
            contextMenu.show(studentsTable, event.getScreenX(), event.getScreenY());
        }
    }

    public void addModel(@SuppressWarnings("all") Model model) {
        try {
            Client.ostream.writeObject(ClientMessage.builder()
                                                    .operationType(OperationType.Add)
                                                    .value(model)
                                                    .build()
            );
            Client.ostream.flush();

            var message = (ServerMessage)Client.istream.readObject();

            if (message.getAnswerType() == AnswerType.Failure) {
                var alert = new Alert(AlertType.ERROR);
                alert.setHeaderText(message.getMessage());
                alert.show();
            } else {
                new Thread(updateTablesTask).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
