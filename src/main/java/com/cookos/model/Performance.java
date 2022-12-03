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
@Table(name = "performance")
public class Performance implements Serializable, Model {

    @ManyToOne
    @JoinColumn(name = "students_id")
    @ToString.Exclude
    private Student student;
    
    @ManyToOne
    @JoinColumn(name = "subjects_id")
    @ToString.Exclude
    private Subject subject;
    
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "total_score")
    private float totalScore;

    @Column(name = "missed_hours")
    private int missedHours;

    @Override
    public ModelType getModelType() {
        return ModelType.Performance;
    }
}
