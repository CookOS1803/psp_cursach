package com.cookos.model;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@Table(name = "subjects")
public class Subject {

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
        name = "speciality_subjects", 
        joinColumns = { @JoinColumn(name = "subjects_id", referencedColumnName = "id") }, 
        inverseJoinColumns = { @JoinColumn(name = "speciality_id", referencedColumnName = "id") }
    )
    @Singular private Set<Speciality> specialities;

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "hours")
    private int hours;

    public Subject(Set<Speciality> specialities, int id, String name, int hours) {
        this.id = id;
        this.name = name;
        this.hours = hours;
        this.specialities = specialities;
    }

    public Subject() {
        this.specialities = new HashSet<>();
    }

    
}
