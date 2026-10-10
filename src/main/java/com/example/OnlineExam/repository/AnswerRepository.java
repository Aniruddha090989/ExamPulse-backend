package com.example.OnlineExam.repository;

import com.example.OnlineExam.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findByAttemptAttemptIdAndQuizQuizId(Long attemptId, Long quizId);
}
