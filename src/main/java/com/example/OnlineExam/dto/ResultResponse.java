package com.example.OnlineExam.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ResultResponse(Long resultId, Long attemptId, String examTitle,
                             Integer totalQuestions, Integer attemptedQuestions,
                             Integer correctAnswers, Integer wrongAnswers,
                             BigDecimal score, BigDecimal percentage,
                             LocalDateTime submittedAt) {}
