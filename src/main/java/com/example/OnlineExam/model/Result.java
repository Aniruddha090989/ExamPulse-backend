package com.example.OnlineExam.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "result")
@Getter @Setter @NoArgsConstructor
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "attempt_id", nullable = false, unique = true)
    private ExamAttempt attempt;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Credential user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;
    @Column(name = "attempted_questions", nullable = false)
    private Integer attemptedQuestions = 0;
    @Column(name = "correct_answers", nullable = false)
    private Integer correctAnswers = 0;
    @Column(name = "wrong_answers", nullable = false)
    private Integer wrongAnswers = 0;
    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal score = BigDecimal.ZERO;
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal percentage = BigDecimal.ZERO;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void beforeCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
