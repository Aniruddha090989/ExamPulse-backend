package com.example.OnlineExam.dto;

import com.example.OnlineExam.model.QuestionType;

import java.util.List;

public record QuestionResponse(Long questionId, String question, QuestionType type, Long subjectId,
                               String subjectName, List<ChoiceResponse> choices) {}
