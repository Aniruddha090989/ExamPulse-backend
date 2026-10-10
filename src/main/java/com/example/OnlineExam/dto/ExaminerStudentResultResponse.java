package com.example.OnlineExam.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExaminerStudentResultResponse(

        Long resultId,

        Long examId,

        Long studentId,

        String studentUsername,

        String studentEmail,

        Integer totalQuestions,

        Integer attemptedQuestions,

        Integer correctAnswers,

        Integer wrongAnswers,

        BigDecimal score,

        BigDecimal percentage,

        LocalDateTime submittedAt

) {
}