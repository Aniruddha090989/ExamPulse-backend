package com.example.OnlineExam.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subject")
@Getter @Setter @NoArgsConstructor
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "subject_name", nullable = false, unique = true, length = 100)
    private String subjectName;

    @Column(length = 100)
    private String category;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions = 0;
}
