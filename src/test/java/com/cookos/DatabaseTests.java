package com.cookos;

import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.junit.*;

import com.cookos.dao.GenericDao;
import com.cookos.model.*;
import com.cookos.util.HashPassword;

public class DatabaseTests {
    
    @Test
    public void addAdmin() {
        try (var userDao = new GenericDao<>(User.class)) {
            User newUser = null;
            try {
                newUser = User.builder()
                              .login("da")
                              .password(HashPassword.getHash("da"))
                              .role(UserRole.Admin)
                              .build();
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e1) {
                fail("Unable to generate hash");
                return;
            }
            
            try {
                userDao.add(newUser);
            } catch (Exception e) {
                fail("Duplicate login");
            }
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void addSpeciality() throws Exception {
        try (var specialityDao = new GenericDao<>(Speciality.class)) {
            
            var newSpeciality = Speciality.builder()
                                          .id(12)
                                          .name("name")
                                          .mult5(0)
                                          .mult6(0.7f)
                                          .mult7(1)
                                          .mult8(1.2f)
                                          .mult9(1.4f)
                                          .build();

            specialityDao.add(newSpeciality);
        }
    }

    @Test
    public void addSubject() throws Exception {
        try (var subjectDao = new GenericDao<>(Subject.class)) {
            
            var newSubject = Subject.builder()
                                    .id(222)
                                    .name("sub")
                                    .hours(60)
                                    .build();
            
            subjectDao.add(newSubject);
        }
    }

    @Test
    public void addStudent() throws Exception {
        try (var studentDao = new GenericDao<>(Student.class); var specialityDao = new GenericDao<>(Speciality.class)) {
            
            var spec = specialityDao.findByUniqueColumn("id", 12);
            
            var newStudent = Student.builder()
                                    .id(322)
                                    .firstName("valeriy")
                                    .lastName("zhmishenko")
                                    .patronymic("albertovich")
                                    .phone("+375295555555")
                                    .address("ul. Pushkina 5, 2")
                                    .email("sd@s.by")
                                    .educationForm(EducationForm.Free)
                                    .speciality(spec)
                                    .build();

            studentDao.add(newStudent);
        }
    }

    @Test
    public void addSpecialScholarship() throws Exception {
        try (var studentDao = new GenericDao<>(Student.class); var specialScholarshipDao = new GenericDao<>(SpecialScholarship.class)) {
                        
            var newSch = SpecialScholarship.builder()
                                           .id(322)
                                           .social(200)
                                           .personal(300)
                                           .named(400)
                                           .build();

            specialScholarshipDao.add(newSch);
        }
    }

    @Test
    public void addPerformance() throws Exception {
        try (var performanceDao = new GenericDao<>(Performance.class);
        var studentDao = new GenericDao<>(Student.class);
        var subjectDao = new GenericDao<>(Subject.class)) {
            
            var student = studentDao.findByUniqueColumn("id", 322);
            var subject = subjectDao.findByUniqueColumn("id", 222);

            var newPerformance = Performance.builder()
                                            .totalScore(6.5f)
                                            .missedHours(8)
                                            .student(student)
                                            .subject(subject)
                                            .build();

            performanceDao.add(newPerformance);
        }
    }

    @Test
    public void checkPerformance() throws Exception {
        try (var performanceDao = new GenericDao<>(Performance.class)) {
            
            var performance = performanceDao.findByUniqueColumn("id", 1);

            System.out.println(performance);
            System.out.println(performance.getStudent().getLastName());
            System.out.println(performance.getSubject().getName());
        }
    }

    @Test
    public void checkSpecialScholarship() throws Exception {
        try (var specialScholarshipDao = new GenericDao<>(SpecialScholarship.class)) {
            
            var sc = specialScholarshipDao.findByUniqueColumn("id", 322);

            System.out.println(sc);
            System.out.println(sc.getStudent());
        }
    }

    @Test
    public void checkStudent() throws Exception {
        try (var studentDao = new GenericDao<>(Student.class)) {
            
            var student = studentDao.findByUniqueColumn("id", 322);

            System.out.println(student);
            System.out.println(student.getSpeciality().getStudents());
        }
    }

    @Test
    public void linkSpecialityAndSubject() {
        
        try (var subjectDao = new GenericDao<>(Subject.class); var specialityDao = new GenericDao<>(Speciality.class)) {            

            var subject = subjectDao.findByUniqueColumn("id", 222);
            var spec = specialityDao.findByUniqueColumn("id", 12);
    
            spec.getSubjects().add(subject);
                
            try {
                specialityDao.update(spec);  
            } catch (Exception e) {
                fail();
            }
            
        } catch (Exception e) {
            fail();
        }        
    }

    @Test
    public void checkSpecialityAndSubject() throws Exception {
        try (var subjectDao = new GenericDao<>(Subject.class)) {
            var l = subjectDao.selectAll();

            for (var subject : l) {
                System.out.println(subject.getSpecialities());
            }
        }
    }

    @Test
    public void addScholarship() throws Exception {
        try (var baseScholarshipDao = new GenericDao<>(BaseScholarship.class)) {
            baseScholarshipDao.add(new BaseScholarship(0, 200));
        }
    }

    @Test
    public void checkBaseScholarship() throws Exception {
        try (var baseScholarshipDao = new GenericDao<>(BaseScholarship.class)) {
            var l = baseScholarshipDao.selectAll();

            System.out.println(l);
        }
    }
}
