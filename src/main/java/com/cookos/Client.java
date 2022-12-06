package com.cookos;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.cookos.gui.controllers.LoadingController;
import com.cookos.util.FXMLHelpers;

public class Client extends Application {

    public static Socket socket = null;
    public static ObjectOutputStream ostream = null;
    public static ObjectInputStream istream = null;
    @SuppressWarnings("all") 
    public static Scene scene = null;

    @Override
    @SuppressWarnings("all")
    public void start(Stage stage) throws IOException, InterruptedException {
        var loader = FXMLHelpers.makeLoader("loading");
        var parent = (Parent)loader.load();

        scene = new Scene(parent, 640, 480);
        stage.setScene(scene);
        stage.show();
        
        var controller = (LoadingController)loader.getController();
        
        var loadingThread = new Thread(() -> {
            try {
                controller.setup();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        loadingThread.start();
    }

    public static void main(String[] args) {
        launch();
    }

}
