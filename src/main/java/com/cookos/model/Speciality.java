package com.cookos.model;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@Table(name = "speciality")
public class Speciality {

    @ManyToMany(mappedBy = "specialities")
    @Singular private Set<Subject> subjects;

    @Id
    @Column(name = "id")
    private int id;    

    @Column(name = "name")
    private String name;

    @Column(name = "mult5")
    private float mult5;

    @Column(name = "mult6")
    private float mult6;

    @Column(name = "mult7")
    private float mult7;

    @Column(name = "mult8")
    private float mult8;

    @Column(name = "mult9")
    private float mult9;

    public Speciality(Set<Subject> subjects, int id, String name, float mult5, float mult6, float mult7, float mult8, float mult9) {
        this.id = id;
        this.name = name;
        this.mult5 = mult5;
        this.mult6 = mult6;
        this.mult7 = mult7;
        this.mult8 = mult8;
        this.mult9 = mult9;
        this.subjects = subjects;
    }

    public Speciality() {
        this.subjects = new HashSet<>();
    }
}
