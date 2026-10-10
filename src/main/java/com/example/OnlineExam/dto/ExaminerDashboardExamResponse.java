package com.example.OnlineExam.dto;

import java.time.LocalDateTime;

public record ExaminerDashboardExamResponse(
        Long examId,
        String title,
        String subjectName,
        Integer totalQuestions,
        Integer durationMinutes,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String status
) {
}