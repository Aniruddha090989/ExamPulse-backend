package com.example.OnlineExam.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ExaminerDashboardResponse(
        long managedSubjects,
        long totalQuestions,
        long activeExams,
        long evaluatedStudents,
        List<ExaminerDashboardExamResponse> exams
) {
}