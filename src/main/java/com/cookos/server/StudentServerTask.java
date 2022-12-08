package com.cookos.server;

import java.io.*;
import java.net.Socket;

import org.apache.logging.log4j.*;

import com.cookos.dao.GenericDao;
import com.cookos.model.BaseScholarship;
import com.cookos.model.EducationForm;
import com.cookos.model.Student;
import com.cookos.model.User;
import com.cookos.net.*;

public class StudentServerTask implements Runnable {

    private static final Logger logger = LogManager.getLogger(StudentServerTask.class);
    
    private Socket socket;
    private ObjectOutputStream ostream;
    private ObjectInputStream istream;
    private int userId;
    private int studentId;

    public StudentServerTask(Socket socket, ObjectOutputStream ostream, ObjectInputStream istream, int userId, int studentId) throws IOException
    {
        this.socket = socket;
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
                logger.info("%s:%d disconnected".formatted(socket.getInetAddress(), socket.getPort()));

                return;
            }
        }
    }

    private void calculateScholarship(StudentMessage message) throws IOException {
        try (
            var studentDao = new GenericDao<>(Student.class);
            var baseScholarshipDao = new GenericDao<>(BaseScholarship.class)
        ) {
            var student = studentDao.findByUniqueColumn("id", studentId);
            float baseScholarship = baseScholarshipDao.selectAll().get(0).getValue();
            
            float scholarship = student.getSpecialScholarship().getSocial()
                              + student.getSpecialScholarship().getPersonal()
                              + student.getSpecialScholarship().getNamed();
            
            if (student.getEducationForm() == EducationForm.Paid) {
                ostream.writeFloat(scholarship);
                ostream.flush();

                return;
            }

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
                    scholarship += baseScholarship * student.getSpeciality().getMult9();
                } else if (average >= 8) {
                    scholarship += baseScholarship * student.getSpeciality().getMult8();
                } else if (average >= 7) {
                    scholarship += baseScholarship * student.getSpeciality().getMult7();
                } else if (average >= 6) {
                    scholarship += baseScholarship * student.getSpeciality().getMult6();
                } else if (average >= 5) {
                    scholarship += baseScholarship * student.getSpeciality().getMult5();
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
