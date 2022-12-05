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
                            case Speciality -> {if (!add(message, Speciality.class)) continue;}
                            case Subject -> {if (!add(message, Subject.class)) continue;}
                            case SpecialScholarship -> {}
                            case User -> {if (!add(message, User.class)) continue;}
                            case Speciality_Subject -> {if (!linkSpecialityAndSubject(message)) continue;}
                        }
                    }
                    case Update -> {}
                    case Remove -> {
                        switch (message.getValue().getModelType()) {
                            case Performance -> {}
                            case Student -> {if (!removeStudent(message)) continue;}
                            case Speciality -> {
                                if (!remove(message, Speciality.class, "Can't remove speciality when it has students"))
                                    continue;
                            }
                            case Subject -> {if (!remove(message, Subject.class)) continue;}
                            case SpecialScholarship -> {}
                            case User -> {if (!remove(message, Subject.class)) continue;}
                            case Speciality_Subject -> {if (!unlinkSpecialityAndSubject(message)) continue;}
                        }
                    }
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

    private boolean unlinkSpecialityAndSubject(ClientMessage message) throws IOException {
        var speciality_subject = (Speciality_Subject)message.getValue();

        try (
            var subjectDao = new GenericDao<>(Subject.class);
            var specialityDao = new GenericDao<>(Speciality.class);
            var performanceDao = new GenericDao<>(Performance.class);
            var studentDao = new GenericDao<>(Student.class)
        ) {            

            var speciality = specialityDao.findByUniqueColumn("id", speciality_subject.getSpecialityId());
            var subject = subjectDao.findByUniqueColumn("id", speciality_subject.getSubjectId());
    
            speciality.getSubjects().remove(subject);
                
            specialityDao.update(speciality);

            for (var student : speciality.getStudents()) {
                var performanceId = subject.getPerformance()
                                           .stream()
                                           .filter(p -> p.getStudent().getId() == student.getId()).toList()
                                           .get(0)
                                           .getId();
                
                var performance = performanceDao.findByUniqueColumn("id", performanceId);        
                performanceDao.remove(performance);
            }
            
        } catch (Exception e) {
            e.printStackTrace();

            ostream.writeObject(ServerMessage.builder()
                                             .answerType(AnswerType.Failure)
                                             .message("Error while unlinking speciality and subject")
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

    private boolean linkSpecialityAndSubject(ClientMessage message) throws IOException {
        var speciality_subject = (Speciality_Subject)message.getValue();

        try (
            var subjectDao = new GenericDao<>(Subject.class);
            var specialityDao = new GenericDao<>(Speciality.class);
            var performanceDao = new GenericDao<>(Performance.class);
        ) {            

            var speciality = specialityDao.findByUniqueColumn("id", speciality_subject.getSpecialityId());
            var subject = subjectDao.findByUniqueColumn("id", speciality_subject.getSubjectId());
    
            speciality.getSubjects().add(subject);
                
            specialityDao.update(speciality);

            for (var student : speciality.getStudents()) {
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
                                             .message("Error linking speciality and subject")
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

    private <T> boolean remove(ClientMessage message, Class<T> type) throws IOException {
        return remove(message, type, "Error while removing " + message.getValue().getModelType());
    }

    private <T> boolean remove(ClientMessage message, Class<T> type, String errorMessage) throws IOException {
        try (var dao = new GenericDao<>(type)) {
            var persistedValue = dao.findByUniqueColumn("id", ((Identifiable)message.getValue()).getId());

            dao.remove(persistedValue);
        } catch (Exception e) {
            e.printStackTrace();

            ostream.writeObject(ServerMessage.builder()
                                             .answerType(AnswerType.Failure)
                                             .message(errorMessage)
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

    private boolean removeStudent(ClientMessage message) throws IOException {
        try (
            var studentDao = new GenericDao<>(Student.class);
            var userDao = new GenericDao<>(User.class)
        ) {
            var student = studentDao.findByUniqueColumn("id", ((Student)message.getValue()).getId());
            var user = userDao.findByUniqueColumn("login", String.valueOf(student.getId()));

            studentDao.remove(student);
            userDao.remove(user);
        } catch (Exception e) {
            e.printStackTrace();

            ostream.writeObject(ServerMessage.builder()
                                             .answerType(AnswerType.Failure)
                                             .message("Error while removing student")
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

    @SuppressWarnings("unchecked")
    private <T> boolean add(ClientMessage message, Class<T> type) throws IOException {
        var newValue = (T)message.getValue();

        try (var dao = new GenericDao<>(type)) {
            dao.add(newValue);
        } catch (Exception e) {
            ostream.writeObject(ServerMessage.builder()
                                             .answerType(AnswerType.Failure)
                                             .message("Duplicate %s id".formatted(message.getValue().getModelType()))
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

    private boolean addStudent(ClientMessage message) throws Exception {
        var student = (Student)message.getValue();

        try (var specialityDao = new GenericDao<>(Speciality.class)) {
            var speciality = specialityDao.findByUniqueColumn("id", student.getSpecialityId());

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

        

        try (var userDao = new GenericDao<>(User.class)) {
            var login = String.valueOf(student.getId());
            userDao.add(User.builder()
                            .login(login)
                            .password(HashPassword.getHash(login))
                            .role(UserRole.Student)
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            ostream.writeObject(ServerMessage.builder()
                                             .answerType(AnswerType.Failure)
                                             .message("Duplicate user login")
                                             .build()
            );
            ostream.flush();

            return false;
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
            var studentDao = new GenericDao<>(Student.class)
        ) {
            student = studentDao.findByUniqueColumn("id", student.getId());

            scholarshipDao.add(SpecialScholarship.builder().id(student.getId()).build());            

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
            var modelBundle = ModelBundle.builder()
                                         .students(studentDao.selectAll())
                                         .performances(performanceDao.selectAll())
                                         .specialScholarships(specialScholarshipDao.selectAll())
                                         .subjects(subjectDao.selectAll())
                                         .specialities(specialityDao.selectAll())
                                         .users(userDao.selectAll())
                                         .build();
            // eager ne rabotaet, poetomu vot
            for (var student : modelBundle.getStudents()) {
                student.getPerformance().forEach(p -> {});
            }
            ostream.writeObject(modelBundle);
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

                var user = userDao.findByUniqueColumn("login", login);

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
