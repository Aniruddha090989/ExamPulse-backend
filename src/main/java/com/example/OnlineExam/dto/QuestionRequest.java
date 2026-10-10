package com.example.OnlineExam.dto;

import com.example.OnlineExam.model.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record QuestionRequest(
        @NotBlank String question,
        QuestionType type,
        Long subjectId,
        @NotEmpty List<String> choices,
        @NotEmpty List<Integer> correctIndexes
) {}
