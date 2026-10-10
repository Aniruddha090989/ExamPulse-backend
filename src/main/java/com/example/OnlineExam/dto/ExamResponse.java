package com.example.OnlineExam.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ExamResponse(Long examId, String title, Long subjectId, String subjectName,
                           Integer durationMinutes, Integer totalQuestions,
                           LocalDateTime startTime, LocalDateTime endTime,
                           List<QuestionResponse> questions) {}
