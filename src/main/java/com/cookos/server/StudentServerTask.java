package com.cookos.server;

import java.io.*;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.cookos.dao.GenericDao;
import com.cookos.model.Performance;
import com.cookos.model.Student;
import com.cookos.model.User;
import com.cookos.net.*;

public class StudentServerTask implements Runnable {

    private static final float BASE_SCHOLARSHIP = 200f;
    private ObjectOutputStream ostream;
    private ObjectInputStream istream;
    private int userId;
    private int studentId;

    public StudentServerTask(ObjectOutputStream ostream, ObjectInputStream istream, int userId, int studentId) throws IOException
    {
        this.ostream = ostream;
        this.istream = istream;
        this.userId = userId;
        this.studentId = studentId;
    }

    @Override
    public void run() {
        sendModels();
        
        while (true) {
            try {
                var message = (StudentMessage)istream.readObject();

                switch (message.getOperationType()) {
                    case ChangePassword -> change(message, User.class);
                    case ChangeInfo -> change(message, Student.class);
                    case CalculateScholarship -> calculateScholarship(message);
                }

                sendModels();

            } catch (Exception e) {
                e.printStackTrace();

                return;
            }
        }
    }

    private void calculateScholarship(StudentMessage message) throws IOException {
        try (var studentDao = new GenericDao<>(Student.class)) {
            var student = studentDao.findByUniqueColumn("id", studentId);
            
            float scholarship = student.getSpecialScholarship().getSocial()
                              + student.getSpecialScholarship().getPersonal()
                              + student.getSpecialScholarship().getNamed();
            
            float sum = 0;
            int zeroes = 0;

            for (var performance : student.getPerformance()) {
                if (performance.getTotalScore() != 0f) {
                    sum += performance.getTotalScore();
                } else {
                    zeroes++;
                }
            }

            if (student.getPerformance().size() != zeroes) {
                float average = sum / (student.getPerformance().size() - zeroes);

                if (average >= 9) {
                    scholarship += BASE_SCHOLARSHIP * student.getSpeciality().getMult9();
                } else if (average >= 8) {
                    scholarship += BASE_SCHOLARSHIP * student.getSpeciality().getMult8();
                } else if (average >= 7) {
                    scholarship += BASE_SCHOLARSHIP * student.getSpeciality().getMult7();
                } else if (average >= 6) {
                    scholarship += BASE_SCHOLARSHIP * student.getSpeciality().getMult6();
                } else if (average >= 5) {
                    scholarship += BASE_SCHOLARSHIP * student.getSpeciality().getMult5();
                }
            }

            ostream.writeFloat(scholarship);
            ostream.flush();
            
        } catch (Exception e) {
            e.printStackTrace();
            ostream.writeFloat(-33);
            ostream.flush();
        }

        
    }

    @SuppressWarnings("unchecked")
    private <T> void change(StudentMessage message, Class<T> type) throws IOException {
        var value = (T)message.getValue();

        try (var dao = new GenericDao<>(type)) {
            dao.update(value);
        } catch (Exception e) {
            ostream.writeObject(ServerMessage.builder()
                                             .answerType(AnswerType.Failure)
                                             .message("Ошибка при изменении данных")
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
            var userDao = new GenericDao<>(User.class);
        ) {
            var user = userDao.findByUniqueColumn("id", userId);
            var student = studentDao.findByUniqueColumn("id", studentId);

            var modelBundle = StudentModelBundle.builder()
                                                .user(user)
                                                .student(student)
                                                .performance(student.getPerformance().stream().toList())
                                                .build();
            ostream.writeObject(modelBundle);
            ostream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
