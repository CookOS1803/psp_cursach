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
@Table(name = "base_scholarship")
public class BaseScholarship implements Serializable, Model, Identifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "value")
    private float value;

    @Override
    public ModelType getModelType() {
        return ModelType.BaseScholarship;
    }
}
