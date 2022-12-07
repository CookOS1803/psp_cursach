package com.cookos.net;

import java.io.Serializable;
import java.util.List;

import com.cookos.model.*;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ModelBundle implements Serializable {
    private List<Student> students;
    private List<User> users;
    private List<SpecialScholarship> specialScholarships;
    private List<Performance> performances;
    private List<Speciality> specialities;
    private List<Subject> subjects;
    private BaseScholarship baseScholarship;
}
