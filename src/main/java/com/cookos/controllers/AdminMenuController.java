package com.cookos.controllers;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.cookos.Client;
import com.cookos.model.Identifiable;
import com.cookos.model.Model;
import com.cookos.model.Speciality_Subject;
import com.cookos.model.SubjectForSpeciality;
import com.cookos.net.*;
import com.cookos.util.CastHelpers;
import com.cookos.util.FXMLHelpers;
import com.cookos.util.TableIntitializers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
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
    @FXML private TitledPane addSpecialityPane;
    @FXML private TitledPane addSubjectPane;
    @FXML private FlowPane addAdminPane;

    @FXML private TableView<List<Object>> studentsTable;
    @FXML private TableView<List<Object>> specialitiesTable;
    @FXML private TableView<List<Object>> subjectsTable;
    @FXML private TableView<List<Object>> userStudentTable;
    @FXML private TableView<List<Object>> userAdminTable;
    @FXML private TableView<List<Object>> subjectsOfSpecialitiesTable;
    private ModelBundle modelBundle;

    private ContextMenu contextMenu;
    private MenuItem removeItem;
    private MenuItem changeItem;
    private MenuItem addSubjectItem;
    private ChoiceDialog<SubjectForSpeciality> addSubjectDialog;

    private Runnable updateTablesTask;
    
    @FXML
    private void initialize() throws ClassNotFoundException, IOException {

        initializeSubcontroller("addstudentmenu", addStudentPane);
        initializeSubcontroller("addspecialitymenu", addSpecialityPane);
        initializeSubcontroller("addsubjectmenu", addSubjectPane);

        var addAdminLoader = FXMLHelpers.makeLoader("addadminmenu");
        addAdminPane.getChildren().add((Pane)addAdminLoader.load());
        var addAdminController = (AdminSubController)addAdminLoader.getController();
        addAdminController.setAdminMenuController(this);
        
        TableIntitializers.addCellFactories(studentsTable);
        TableIntitializers.addCellFactories(specialitiesTable);
        TableIntitializers.addCellFactories(subjectsTable);
        TableIntitializers.addCellFactories(userStudentTable);
        TableIntitializers.addCellFactories(userAdminTable);
        TableIntitializers.addCellFactories(subjectsOfSpecialitiesTable);
        
        contextMenu = new ContextMenu();
        removeItem = new MenuItem("Remove");
        changeItem = new MenuItem("Change");
        addSubjectItem = new MenuItem("Add subject");
        addSubjectItem.setVisible(false);
        contextMenu.getItems().addAll(removeItem, changeItem, addSubjectItem);

        addSubjectDialog = new ChoiceDialog<>();        

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
                showSubjectsOfSpeciality(null);
                

                tabPane.setDisable(false);
            });

        };

        new Thread(updateTablesTask).start();
    }

    private void initializeSubcontroller(String fxml, TitledPane addPane) throws IOException {
        var loader = FXMLHelpers.makeLoader(fxml);
        addPane.setContent((Pane)loader.load());
        var controller = (AdminSubController)loader.getController();
        controller.setAdminMenuController(this);
    }

    @FXML
    private void onStudentsTableClick(MouseEvent event) {
        onTableClick(event, studentsTable, CastHelpers.toIdentifiables(modelBundle.getStudents()));
    }

    @FXML
    private void onSpecialitiesTableClick(MouseEvent event) {
        onTableClick(event, specialitiesTable, CastHelpers.toIdentifiables(modelBundle.getSpecialities()));

        int selectedIndex = specialitiesTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            addSubjectItem.setVisible(true);

            addSubjectItem.setOnAction(e -> {
                var subjects = CastHelpers.toSubjectForSpeciality(modelBundle.getSubjects());
                addSubjectDialog.getItems().clear();
                addSubjectDialog.getItems().addAll(subjects);
                addSubjectDialog.setSelectedItem(subjects.get(0));
    
                var answer = addSubjectDialog.showAndWait();

                if (answer.isPresent()) {
                    int specialityId = (Integer)specialitiesTable.getItems().get(selectedIndex).get(0);
        
                    addModel(Speciality_Subject.builder()
                                               .specialityId(specialityId)
                                               .subjectId(answer.get().getId())
                                               .build()
                    );
                }
            });
        }
    }

    @FXML
    private void onSubjectsTableClick(MouseEvent event) {
        onTableClick(event, subjectsTable, CastHelpers.toIdentifiables(modelBundle.getSubjects()));
    }

    @FXML
    private void onAdminsTableClick(MouseEvent event) {
        onTableClick(event, userAdminTable, CastHelpers.toIdentifiables(modelBundle.getUsers()));
    }

    @FXML
    private void showSubjectsOfSpeciality(MouseEvent event) {
        int selectedIndex = specialitiesTable.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex >= 0) {
            int id = (Integer)specialitiesTable.getItems().get(selectedIndex).get(0);
            
            var speciality = modelBundle.getSpecialities()
                                        .stream()
                                        .filter(s -> s.getId() == id)
                                        .collect(Collectors.toList())
                                        .get(0);
            
            TableIntitializers.subjects(speciality.getSubjects(), subjectsOfSpecialitiesTable);
        } else {
            subjectsOfSpecialitiesTable.getItems().clear();
        }
    }

    private void onTableClick(MouseEvent event, TableView<List<Object>> table, List<Identifiable> identifiables) {
        addSubjectItem.setVisible(false);

        if (contextMenu.isShowing()) {
            contextMenu.hide();
        }

        if (table.getSelectionModel().getSelectedIndex() < 0)
            return;

        if (event.getButton() == MouseButton.SECONDARY) {
            int selectedIndex = table.getSelectionModel().getSelectedIndex();
            int id = (Integer)table.getItems().get(selectedIndex).get(0);

            removeItem.setOnAction(e -> removeModel(
                (Model)identifiables.stream()
                                    .filter(s -> s.getId() == id)
                                    .collect(Collectors.toList())
                                    .get(0)
            ));
            contextMenu.show(table, event.getScreenX(), event.getScreenY());
        }
    }

    private void actionWithModel(Model model, OperationType operationType) {
        try {
            Client.ostream.writeObject(ClientMessage.builder()
                                                    .operationType(operationType)
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

    void addModel(Model model) {
        actionWithModel(model, OperationType.Add);
    }

    void removeModel(Model model) {
        actionWithModel(model, OperationType.Remove);        
    }
}
