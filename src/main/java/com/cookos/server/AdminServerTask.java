package com.cookos.server;

import java.io.*;

import com.cookos.dao.GenericDao;
import com.cookos.model.*;
import com.cookos.net.*;
import com.cookos.util.HashPassword;

public class AdminServerTask implements Runnable {

    private ObjectOutputStream ostream;
    private ObjectInputStream istream;

    public AdminServerTask(ObjectOutputStream ostream, ObjectInputStream istream) throws IOException
    {
        this.ostream = ostream;
        this.istream = istream;
    }

    @Override
    public void run() {
        sendModels();

        while (true) {
            try {
                var message = (ClientMessage)istream.readObject();
                
                switch (message.getOperationType()) {
                    case Add -> {
                        switch (message.getValue().getModelType()) {
                            case Performance -> unimplementedOperation();
                            case Student -> addStudent(message);
                            case Speciality -> add(message, Speciality.class);
                            case Subject -> add(message, Subject.class);
                            case SpecialScholarship -> unimplementedOperation();
                            case User -> add(message, User.class);
                            case Speciality_Subject -> linkSpecialityAndSubject(message);
                        }
                    }
                    case Update -> {
                        switch (message.getValue().getModelType()) {
                            case Performance -> update(message, Performance.class);
                            case Student -> unimplementedOperation();
                            case Speciality -> unimplementedOperation();
                            case Subject -> unimplementedOperation();
                            case SpecialScholarship -> update(message, SpecialScholarship.class);
                            case User -> unimplementedOperation();
                            case Speciality_Subject -> unimplementedOperation();
                        }
                    }
                    case Remove -> {
                        switch (message.getValue().getModelType()) {
                            case Performance -> unimplementedOperation();
                            case Student -> removeStudent(message);
                            case Speciality -> remove(message, Speciality.class, "Can't remove speciality when it has students");
                            case Subject -> remove(message, Subject.class);
                            case SpecialScholarship -> unimplementedOperation();
                            case User -> remove(message, Subject.class);
                            case Speciality_Subject -> unlinkSpecialityAndSubject(message);
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

    
    private void unimplementedOperation() throws IOException {
        ostream.writeObject(ServerMessage.builder()
                                         .answerType(AnswerType.Failure)
                                         .message("Unimplemented operation")
                                         .build()
        );
        ostream.flush();
    }

    private <T> void update(ClientMessage message, Class<T> type) throws IOException {
        update(message, type, "Error while updating " + message.getValue().getModelType());
    }

    @SuppressWarnings("unchecked")
    private <T> void update(ClientMessage message, Class<T> type, String errorMessage) throws IOException {
        
        try (var dao = new GenericDao<>(type)) {
            //var persistedValue = dao.findByUniqueColumn("id", ((Identifiable)message.getValue()).getId());

            dao.update((T)message.getValue());

        } catch (Exception e) {
            e.printStackTrace();

            ostream.writeObject(ServerMessage.builder()
                                             .answerType(AnswerType.Failure)
                                             .message(errorMessage)
                                             .build()
            );
            ostream.flush();

            return;
        }

        ostream.writeObject(ServerMessage.builder()
                                         .answerType(AnswerType.Success)
                                         .build()
        );
        ostream.flush();
    }

    private void unlinkSpecialityAndSubject(ClientMessage message) throws IOException {
        var speciality_subject = (Speciality_Subject)message.getValue();

        try (
            var subjectDao = new GenericDao<>(Subject.class);
            var specialityDao = new GenericDao<>(Speciality.class);
            var performanceDao = new GenericDao<>(Performance.class)
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

            return;
        } 

        ostream.writeObject(ServerMessage.builder()
                                         .answerType(AnswerType.Success)
                                         .build()
        );
        ostream.flush();
    }

    private void linkSpecialityAndSubject(ClientMessage message) throws IOException {
        var speciality_subject = (Speciality_Subject)message.getValue();

        try (
            var subjectDao = new GenericDao<>(Subject.class);
            var specialityDao = new GenericDao<>(Speciality.class);
            var performanceDao = new GenericDao<>(Performance.class);
        ) {            

            var speciality = specialityDao.findByUniqueColumn("id", speciality_subject.getSpecialityId());
            var subject = subjectDao.findByUniqueColumn("id", speciality_subject.getSubjectId());

            if (speciality.getSubjects().contains(subject)) {
                ostream.writeObject(ServerMessage.builder()
                                             .answerType(AnswerType.Failure)
                                             .message("Speciality already has this subject")
                                             .build()
                );
                ostream.flush();

                return;
            }
    
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

            return;
        } 

        ostream.writeObject(ServerMessage.builder()
                                         .answerType(AnswerType.Success)
                                         .build()
        );
        ostream.flush();
    }

    private <T> void remove(ClientMessage message, Class<T> type) throws IOException {
        remove(message, type, "Error while removing " + message.getValue().getModelType());
    }

    private <T> void remove(ClientMessage message, Class<T> type, String errorMessage) throws IOException {
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

            return;
        }

        ostream.writeObject(ServerMessage.builder()
                                         .answerType(AnswerType.Success)
                                         .build()
        );
        ostream.flush();
    }

    private void removeStudent(ClientMessage message) throws IOException {
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

            return;
        }

        ostream.writeObject(ServerMessage.builder()
                                         .answerType(AnswerType.Success)
                                         .build()
        );
        ostream.flush();
    }

    @SuppressWarnings("unchecked")
    private <T> void add(ClientMessage message, Class<T> type) throws IOException {
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

            return;
        }
        
        ostream.writeObject(ServerMessage.builder()
                                         .answerType(AnswerType.Success)
                                         .build()
        );
        ostream.flush();
    }

    private void addStudent(ClientMessage message) throws Exception {
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

                return;
            }

            student.setSpeciality(speciality);
        }

        

        try (var studentDao = new GenericDao<>(Student.class)) {
            studentDao.add(student);
        } catch (Exception e) {
            ostream.writeObject(ServerMessage.builder()
                                             .answerType(AnswerType.Failure)
                                             .message("Invalid student id")
                                             .build()
            );
            ostream.flush();

            return;
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

            return;
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

            return;
        }

        ostream.writeObject(ServerMessage.builder()
                                         .answerType(AnswerType.Success)
                                         .build()
        );
        ostream.flush();
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

}
