package com.cookos.model;

import java.io.Serializable;

import com.cookos.net.ModelType;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "special_scholarship")
public class SpecialScholarship implements Serializable, Model, Identifiable {
    
    @OneToOne
    @JoinColumn(name = "id")
    @ToString.Exclude
    private Student student;

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "social")
    private float social;

    @Column(name = "personal")
    private float personal;

    @Column(name = "named")
    private float named;

    @Override
    public ModelType getModelType() {
        return ModelType.SpecialScholarship;
    }
}
