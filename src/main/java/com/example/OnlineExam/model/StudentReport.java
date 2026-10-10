package com.example.OnlineExam.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "student_report", uniqueConstraints = @UniqueConstraint(name = "uq_student_subject_report", columnNames = {"user_id", "subject_id"}))
@Getter @Setter @NoArgsConstructor
public class StudentReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Credential user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "total_exams", nullable = false)
    private Integer totalExams = 0;
    @Column(name = "total_attempted", nullable = false)
    private Integer totalAttempted = 0;
    @Column(name = "total_score", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalScore = BigDecimal.ZERO;
    @Column(name = "average_score", nullable = false, precision = 6, scale = 2)
    private BigDecimal averageScore = BigDecimal.ZERO;
    @Column(name = "average_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal averagePercentage = BigDecimal.ZERO;
}
