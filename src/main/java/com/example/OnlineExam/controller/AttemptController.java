package com.example.OnlineExam.controller;

import com.example.OnlineExam.dto.AnswerRequest;
import com.example.OnlineExam.dto.ResultResponse;
import com.example.OnlineExam.model.ExamAttempt;
import com.example.OnlineExam.model.Result;
import com.example.OnlineExam.security.CustomUserDetails;
import com.example.OnlineExam.service.AttemptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attempts")
@RequiredArgsConstructor
public class AttemptController {

    private final AttemptService attemptService;


    @PostMapping("/start")
    public Map<String, Object> start(
            @RequestParam Long examId,
            Authentication authentication
    ) {

        Long userId = getCurrentUserId(authentication);

        ExamAttempt attempt =
                attemptService.start(examId, userId);

        return Map.of(
                "attemptId", attempt.getAttemptId(),
                "startAt", attempt.getStartAt(),
                "status", attempt.getStatus()
        );
    }


    @PostMapping("/{attemptId}/answers")
    public Map<String, Object> saveAnswer(
            @PathVariable Long attemptId,
            @Valid @RequestBody AnswerRequest request,
            Authentication authentication
    ) {

        Long userId = getCurrentUserId(authentication);

        var answer =
                attemptService.saveAnswer(
                        attemptId,
                        userId,
                        request
                );

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "answerId",
                answer.getAnswerId()
        );

        response.put(
                "questionId",
                answer.getQuiz().getQuizId()
        );

        response.put(
                "choiceId",
                answer.getSelectedChoice() == null
                        ? null
                        : answer.getSelectedChoice().getChoiceId()
        );

        return response;
    }


    @PostMapping("/{attemptId}/submit")
    public ResultResponse submit(
            @PathVariable Long attemptId,
            Authentication authentication
    ) {

        Long userId = getCurrentUserId(authentication);

        return attemptService.submit(
                attemptId,
                userId,
                false
        );
    }


    @PostMapping("/{attemptId}/auto-submit")
    public ResultResponse autoSubmit(
            @PathVariable Long attemptId,
            Authentication authentication
    ) {

        Long userId = getCurrentUserId(authentication);

        return attemptService.submit(
                attemptId,
                userId,
                true
        );
    }


    @GetMapping("/results")
    public List<ResultResponse> getResults(
            Authentication authentication
    ) {

        Long userId = getCurrentUserId(authentication);

        return attemptService.getResults(userId);
    }


    @GetMapping("/{attemptId}/result")
    public ResultResponse getResult(
            @PathVariable Long attemptId,
            Authentication authentication
    ) {

        Long userId = getCurrentUserId(authentication);

        return attemptService.getResult(
                attemptId,
                userId
        );
    }


    @GetMapping("/check")
    public Map<String, Object> checkAttempted(
            @RequestParam Long examId,
            Authentication authentication
    ) {

        Long userId = getCurrentUserId(authentication);

        Result result =
                attemptService.getPreviousResult(
                        examId,
                        userId
                );

        if (result == null) {
            return Map.of(
                    "attempted",
                    false
            );
        }

        return Map.of(
                "attempted",
                true,
                "attemptId",
                result.getAttempt().getAttemptId()
        );
    }


    private Long getCurrentUserId(
            Authentication authentication
    ) {

        CustomUserDetails userDetails =
                (CustomUserDetails)
                        authentication.getPrincipal();

        return userDetails.getUserId();
    }
}