package com.example.OnlineExam.repository;

import com.example.OnlineExam.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Long> {

    Optional<Result> findByAttemptAttemptId(Long attemptId);

    Optional<Result> findByUserUserIdAndExamExamId(
            Long userId,
            Long examId
    );

    List<Result> findByUserUserIdOrderByCreatedAtDesc(
            Long userId
    );

    List<Result> findByExamExamIdOrderByCreatedAtDesc(
            Long examId
    );

    boolean existsByUserUserIdAndExamExamId(
            Long userId,
            Long examId
    );

    @Query("SELECT COUNT(DISTINCT r.user.userId) FROM Result r")
    long countDistinctStudents();

    // Used when deleting an examination
    @Modifying
    @Query("DELETE FROM Result r WHERE r.exam.examId = :examId")
    void deleteByExamId(@Param("examId") Long examId);
}