package com.example.OnlineExam.repository;

import com.example.OnlineExam.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findBySubjectSubjectId(Long subjectId);
    long countBySubjectSubjectId(Long subjectId);
}


