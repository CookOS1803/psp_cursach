package com.cookos;

import java.io.*;
import java.net.*;
import java.util.Arrays;

import com.cookos.dao.GenericDao;
import com.cookos.model.Student;
import com.cookos.model.User;
import com.cookos.net.LoginMessage;

public class ServerTask implements Runnable {
    
    private ObjectOutputStream ostream;
    private ObjectInputStream istream;
    //private Socket socket;

    public ServerTask(Socket socket) throws IOException
    {
        //this.socket = socket;
        
        ostream = new ObjectOutputStream(socket.getOutputStream());
        ostream.flush();
        istream = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        
        try {
            handleLogin();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        try (var studentDao = new GenericDao<>(Student.class)) {
            var students = studentDao.selectAll();

            for (var s : students) {
                System.out.println(s);
            }

            ostream.writeObject(students);
            ostream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void handleLogin() throws IOException, ClassNotFoundException, Exception {
        while (true) {
            var login = (String)istream.readObject();
            
            int hashLength = istream.readInt();
            var password = new byte[hashLength];
            istream.readFully(password);

            try (var userDao = new GenericDao<>(User.class)) {

                var user = userDao.findByColumn("login", login);

                if (user == null) {
                    ostream.writeObject(LoginMessage.WrongLogin);
                    ostream.flush();
                    
                    continue;
                }

                if (!Arrays.equals(password, user.getPassword())) {
                    ostream.writeObject(LoginMessage.WrongPassword);
                    ostream.flush();

                    continue;
                }

                ostream.writeObject(LoginMessage.Success);
                ostream.flush();
            }

            break;
        }
    }
}
