package com.example.OnlineExam.repository;

import com.example.OnlineExam.model.ExamAttempt;
import com.example.OnlineExam.model.AttemptStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {

    List<ExamAttempt> findByUserUserIdOrderByStartAtDesc(Long userId);

    Optional<ExamAttempt> findByAttemptIdAndUserUserId(
            Long attemptId,
            Long userId
    );

    Optional<ExamAttempt> findFirstByExamExamIdAndUserUserIdAndStatusOrderByStartAtDesc(
            Long examId,
            Long userId,
            AttemptStatus status
    );

    // Used when deleting an examination
    List<ExamAttempt> findByExamExamId(Long examId);
}