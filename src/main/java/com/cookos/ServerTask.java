package com.cookos;

import java.io.*;
import java.net.*;
import java.util.Arrays;

import com.cookos.dao.GenericDao;
import com.cookos.model.User;

public class ServerTask implements Runnable {
    
    private ObjectOutputStream ostream;
    private ObjectInputStream istream;
    private Socket socket;

    public ServerTask(Socket socket) throws IOException
    {
        this.socket = socket;
        
        ostream = new ObjectOutputStream(socket.getOutputStream());
        ostream.flush();
        istream = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        
        try {
            while (true) {
                var login = (String)istream.readObject();
                
                int hashLength = istream.readInt();
                var password = new byte[hashLength];
                istream.readFully(password);

                try (var userDao = new GenericDao<>(User.class)) {

                    var user = userDao.findByColumn("login", login);

                    if (user == null) {
                        ostream.writeObject("Wrong login");
                        ostream.flush();
                        
                        continue;
                    }

                    if (!Arrays.equals(password, user.getPassword())) {
                        ostream.writeObject("Wrong password");
                        ostream.flush();

                        continue;
                    }

                    ostream.writeObject("Done");
                    ostream.flush();
                    //break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
