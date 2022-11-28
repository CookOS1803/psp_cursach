package com.cookos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import com.cookos.dao.GenericDao;
import com.cookos.model.User;
import com.cookos.util.HashPassword;


public class App extends Application {
    private static Scene scene; 

    @Override
    @SuppressWarnings("all")
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("loginscreen"), 640, 480);
        stage.setScene(scene);
        stage.show();
        
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        launch();

        //var userDao = new GenericDao<>(User.class);
        //
        //var newUser = User.builder()
        //                  .login("da")
        //                  .password(HashPassword.getHash("da"))
        //                  .build();
        //
        //try {
        //    userDao.add(newUser);
        //} catch (Exception e) {
        //    System.out.println("Duplicate login");
        //}
    }

}
