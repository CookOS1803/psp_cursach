package com.cookos.model;

import lombok.Getter;

@Getter
public class SubjectForSpeciality {

    private Subject subject;

    public SubjectForSpeciality(Subject subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return String.valueOf(subject.getId()) + ", " + subject.getName();
    }
}
