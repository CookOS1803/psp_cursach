package com.cookos;

import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.hibernate.Hibernate;
import org.junit.*;

import com.cookos.dao.GenericDao;
import com.cookos.model.*;
import com.cookos.util.HashPassword;

public class DatabaseTests {
    
    @Test
    public void addAdmin() {
        var userDao = new GenericDao<>(User.class);
        
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
    }

    @Test
    public void linkSpecialityAndSubject() {
        var specialityDao = new GenericDao<>(Speciality.class);
        var subjectDao = new GenericDao<>(Subject.class);

        var subject = subjectDao.findById(222);
        Speciality spec = null;
        try {
            spec = specialityDao.findById(12);
        } catch (Exception e) {
            fail(e.getCause().getCause().getMessage());
            return;
        }

        spec.getSubjects().add(subject);

        

        try {
            specialityDao.update(spec);        
        } catch (Exception e) {
            fail();
        }
    }
}
