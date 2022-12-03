package com.cookos.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.cookos.Client;
import com.cookos.model.Model;
import com.cookos.model.Student;
import com.cookos.net.AnswerType;
import com.cookos.net.ClientMessage;
import com.cookos.net.OperationType;
import com.cookos.net.ServerMessage;
import com.cookos.util.FXMLHelpers;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;

public class AdminMenuController {

    @FXML private TabPane tabPane;
    @FXML private TitledPane addStudentPane;
    private AddStudentMenuController addStudentController;

    @FXML private TableView<List<Object>> studentsTable;
    private List<Student> students;

    private Runnable updateTablesTask;
    
    @FXML
    @SuppressWarnings("unchecked")
    private void initialize() throws ClassNotFoundException, IOException {

        var addStudentLoader = FXMLHelpers.makeLoader("addstudentmenu");
        var pane = (Pane)addStudentLoader.load();
        addStudentPane.setContent(pane);
        addStudentController = addStudentLoader.getController();

        addStudentController.setAdminMenuController(this);
        
        addCellFactories(studentsTable);

        updateTablesTask = () -> {            
            Platform.runLater(() -> tabPane.setDisable(true));

            try {
                students = (List<Student>)Client.istream.readObject();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
                return;
            }

            Platform.runLater(() -> {
                initializeStudentsTable();

                tabPane.setDisable(false);
            });

        };

        new Thread(updateTablesTask).start();
    }

    private void addCellFactories(TableView<List<Object>> table) {
        for (int i = 0; i < table.getColumns().size(); i++) {
            eee(table, i);
        }
    }

    private void initializeStudentsTable() {
        ObservableList<List<Object>> items = FXCollections.observableArrayList();
        
        for (var s : students) {            
            var row = new ArrayList<Object>();

            row.add(s.getId());
            row.add(s.getLastName());
            row.add(s.getFirstName());
            row.add(s.getPatronymic());
            row.add(s.getPhone());
            row.add(s.getAddress());
            row.add(s.getEmail());
            row.add(s.getEducationForm());
            row.add(s.getSpeciality().getName());

            items.add(row);
        }
        
        studentsTable.setItems(items);
    }

    @SuppressWarnings("unchecked")
    private void eee(TableView<List<Object>> table, int columnIndex) {
        ((TableColumn<List<Object>, Object>)table.getColumns().get(columnIndex)).setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get(columnIndex)));
    }

    public void addModel(@SuppressWarnings("all") Model model) {
        try {
            Client.ostream.writeObject(ClientMessage.builder()
                                                    .operationType(OperationType.Add)
                                                    .value(model)
                                                    .build());
            Client.ostream.flush();

            var message = (ServerMessage)Client.istream.readObject();

            if (message.getAnswerType() == AnswerType.Failure) {
                System.out.println(message.getMessage());
            } else {
                new Thread(updateTablesTask).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
