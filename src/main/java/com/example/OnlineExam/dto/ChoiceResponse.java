
package com.example.OnlineExam.dto;

public record ChoiceResponse(
        Long choiceId,
        String choice,
        Boolean isCorrect
) {}
