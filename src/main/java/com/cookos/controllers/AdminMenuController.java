package com.cookos.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.cookos.Client;
import com.cookos.model.Student;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AdminMenuController {

    @FXML private TabPane tabPane;

    @FXML private TableView<List<Object>> studentsTable;
    private List<Student> students;
    
    @FXML
    @SuppressWarnings("unchecked")
    private void initialize() throws ClassNotFoundException, IOException {

        tabPane.setDisable(true);

        new Thread(() -> {
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

        }).start();
        
        
    }

    private void initializeStudentsTable() {
        
        for (int i = 0; i < studentsTable.getColumns().size(); i++) {
            eee(studentsTable, i);
        }

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
}
