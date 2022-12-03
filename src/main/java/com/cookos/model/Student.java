package com.cookos.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.cookos.net.ModelType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "students")
public class Student implements Serializable, Model {

    @OneToMany(mappedBy = "student")
    @ToString.Exclude
    @Builder.Default
    private Set<Performance> performance = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "speciality_id")
    @ToString.Exclude
    private Speciality speciality;

    @OneToOne
    @JoinColumn(name = "id")
    @ToString.Exclude
    private SpecialScholarship specialScholarship;

    @Id
    @Column(name = "id")
    private int id;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;

    @Column(name = "patronymic")
    private String patronymic;

    @Column(name = "phone")
    private String phone;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "email")
    private String email;

    @Column(name = "education_form")
    @Enumerated(EnumType.STRING)
    private EducationForm educationForm;

    @Column(name = "speciality_id", insertable = false, updatable = false)
    private int specialityId;

    @Override
    public ModelType getModelType() {
        return ModelType.Student;
    }
}
