package com.example.OnlineExam.repository;

import com.example.OnlineExam.model.StudentReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentReportRepository extends JpaRepository<StudentReport, Long> {
    Optional<StudentReport> findByUserUserIdAndSubjectSubjectId(Long userId, Long subjectId);
}
