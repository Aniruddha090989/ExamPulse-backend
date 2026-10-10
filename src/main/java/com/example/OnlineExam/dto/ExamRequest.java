package com.example.OnlineExam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

public record ExamRequest(
        @NotBlank String title,
        Long subjectId,
        @Positive Integer durationMinutes,
        @NotEmpty List<Long> questionIds,
        LocalDateTime startTime,
        LocalDateTime endTime
) {}
