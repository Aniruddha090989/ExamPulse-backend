package com.example.OnlineExam.repository;

import com.example.OnlineExam.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {

    List<Exam> findBySubjectSubjectId(Long subjectId);

    // Remove exam-question relationships first
    @Modifying
    @Query(
            value = "DELETE FROM exam_questions WHERE exam_id = :examId",
            nativeQuery = true
    )
    void deleteExamQuestionLinks(
            @Param("examId") Long examId
    );
}