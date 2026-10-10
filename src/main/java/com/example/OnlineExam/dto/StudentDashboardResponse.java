package com.example.OnlineExam.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record StudentDashboardResponse(
        Long userId,
        String username,
        String email,
        long examsTaken,
        BigDecimal averagePercentage,
        long activeExams,
        long attemptedQuestions,
        List<DashboardExamResponse> exams,
        List<DashboardResultResponse> recentResults
) {

    public record DashboardExamResponse(
            Long examId,
            String title,
            String subjectName,
            Integer durationMinutes,
            Integer totalQuestions,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {}

    public record DashboardResultResponse(
            Long resultId,
            Long attemptId,
            String examTitle,
            BigDecimal percentage,
            LocalDateTime submittedAt
    ) {}
}