package com.cookos.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "performance")
public class Performance {

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
}
