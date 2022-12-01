package com.cookos.controllers;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import com.cookos.Client;
import com.cookos.util.FXMLHelpers;

public class LoadingController{

    public void setup() throws UnknownHostException, IOException {        
        Client.socket = new Socket("127.0.0.1", 8080);
        Client.ostream = new ObjectOutputStream(Client.socket.getOutputStream());
        Client.ostream.flush();
        Client.istream = new ObjectInputStream(Client.socket.getInputStream());

        FXMLHelpers.setRoot("loginscreen");
    }
    
}
