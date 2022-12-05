package com.cookos.model;

public class SubjectForSpeciality extends Subject {

    public SubjectForSpeciality(Subject subject) {
        setId(subject.getId());
        setName(subject.getName());
    }

    @Override
    public String toString() {
        return String.valueOf(getId()) + ", " + getName();
    }
}
