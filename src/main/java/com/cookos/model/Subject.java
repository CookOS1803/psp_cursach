package com.cookos.model;

import java.util.*;

import javax.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "subjects")
public class Subject {

    @ManyToMany(targetEntity = Speciality.class, cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    @JoinTable(
        name = "speciality_subjects", 
        joinColumns = { @JoinColumn(name = "subjects_id", referencedColumnName = "id") }, 
        inverseJoinColumns = { @JoinColumn(name = "speciality_id", referencedColumnName = "id") }
    )
    @Builder.Default
    @ToString.Exclude
    private Set<Speciality> specialities = new HashSet<>();

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "hours")
    private int hours;
    
}
