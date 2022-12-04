package com.cookos;

import java.io.*;
import java.net.*;
import java.util.Arrays;

import com.cookos.dao.GenericDao;
import com.cookos.model.*;
import com.cookos.net.*;
import com.cookos.util.HashPassword;

public class ServerTask implements Runnable {
    
    private ObjectOutputStream ostream;
    private ObjectInputStream istream;

    public ServerTask(Socket socket) throws IOException
    {
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
        
        sendModels();

        while (true) {
            try {
                var message = (ClientMessage)istream.readObject();
                
                switch (message.getOperationType()) {
                    case Add -> {
                        switch (message.getValue().getModelType()) {
                            case Performance -> {}
                            case Student -> {if (!addStudent(message)) continue;}
                            case Speciality -> {if (!addSpeciality(message)) continue;}
                            case Subject -> {if (!addSubject(message)) continue;}
                            case SpecialScholarship -> {}
                            case User -> {if (!addUser(message)) continue;}
                        }
                    }
                    case Update -> {}
                    case Remove -> {}
                }

                sendModels();

            } catch (Exception e) {
                e.printStackTrace();

                try {
                    ostream.writeObject(ServerMessage.builder()
                                                     .answerType(AnswerType.Failure)
                                                     .message("Unhandled exception")
                                                     .build());
                    ostream.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                return;
            }
        }
    }

    private boolean addUser(ClientMessage message) {
        return false;
    }

    private boolean addSubject(ClientMessage message) {
        return false;
    }

    private boolean addSpeciality(ClientMessage message) {
        return false;
    }

    private boolean addStudent(ClientMessage message) throws Exception {
        var student = (Student)message.getValue();

        try (var specialityDao = new GenericDao<>(Speciality.class)) {
            var speciality = specialityDao.findByColumn("id", student.getSpecialityId());

            if (speciality == null) {
                ostream.writeObject(ServerMessage.builder()
                                                 .answerType(AnswerType.Failure)
                                                 .message("Wrong speciality id")
                                                 .build()
                );
                ostream.flush();

                return false;
            }

            student.setSpeciality(speciality);
        }

        try (var studentDao = new GenericDao<>(Student.class)) {
            studentDao.add(student);
        } catch (Exception e) {
            ostream.writeObject(ServerMessage.builder()
                                             .answerType(AnswerType.Failure)
                                             .message("Duplicate student id")
                                             .build()
            );
            ostream.flush();

            return false;
        }

        try (
            var performanceDao = new GenericDao<>(Performance.class);
            var scholarshipDao = new GenericDao<>(SpecialScholarship.class);
            var userDao = new GenericDao<>(User.class);
            var studentDao = new GenericDao<>(Student.class)
        ) {
            student = studentDao.findByColumn("id", student.getId());

            scholarshipDao.add(SpecialScholarship.builder().id(student.getId()).build());

            var login = String.valueOf(student.getId());
            userDao.add(User.builder()
                            .login(login)
                            .password(HashPassword.getHash(login))
                            .role(UserRole.Student)
                            .build()
            );

            for (var subject : student.getSpeciality().getSubjects()) {
                performanceDao.add(Performance.builder()
                                              .student(student)
                                              .subject(subject)
                                              .build()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            ostream.writeObject(ServerMessage.builder()
                                             .answerType(AnswerType.Failure)
                                             .message("Error while adding subentities")
                                             .build()
            );
            ostream.flush();

            return false;
        }

        ostream.writeObject(ServerMessage.builder()
                                         .answerType(AnswerType.Success)
                                         .build()
        );
        ostream.flush();

        return true;
    }

    private void sendModels() {
        try (
            var studentDao = new GenericDao<>(Student.class);
            var performanceDao = new GenericDao<>(Performance.class);
            var specialScholarshipDao = new GenericDao<>(SpecialScholarship.class);
            var subjectDao = new GenericDao<>(Subject.class);
            var specialityDao = new GenericDao<>(Speciality.class);
            var userDao = new GenericDao<>(User.class);
        ) {
            ostream.writeObject(ModelBundle.builder()
                                           .students(studentDao.selectAll())
                                           .performances(performanceDao.selectAll())
                                           .specialScholarships(specialScholarshipDao.selectAll())
                                           .subjects(subjectDao.selectAll())
                                           .specialities(specialityDao.selectAll())
                                           .users(userDao.selectAll())
                                           .build()
            );
            ostream.flush();
        } catch (Exception e) {
            e.printStackTrace();
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
