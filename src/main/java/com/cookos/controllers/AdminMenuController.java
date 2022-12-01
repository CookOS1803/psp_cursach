package com.cookos.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OnDelete;

import com.cookos.Client;
import com.cookos.model.Student;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AdminMenuController {

    @FXML private TableView<List<Object>> studentsTable;
    
    @FXML
    private void initialize() throws ClassNotFoundException, IOException {
        
        var students = (List<Student>)Client.istream.readObject();

        initializeStudentsTable(students);
    }

    private void initializeStudentsTable(List<Student> students) {
        addColumn(studentsTable, "id", 0);
        addColumn(studentsTable, "Last name", 1);
        addColumn(studentsTable, "First name", 2);
        addColumn(studentsTable, "Patronymic", 3);
        addColumn(studentsTable, "Phone", 4);
        addColumn(studentsTable, "Address", 5);
        addColumn(studentsTable, "Email", 6);
        addColumn(studentsTable, "Education form", 7);
        addColumn(studentsTable, "Speciality", 8);

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

    private void addColumn(TableView<List<Object>> table, String columnName, int columnIndex) {
        var column = new TableColumn<List<Object>, Object>(columnName);
        column.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get(columnIndex)));
        table.getColumns().add(column);
    }
}
