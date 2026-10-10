package com.example.OnlineExam.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "quiz_choices")
@Getter
@Setter
@NoArgsConstructor
public class QuizChoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "choice_id")
    private Long choiceId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String choice;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect = false;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "quiz_id",
            nullable = false
    )
    private Quiz quiz;
}