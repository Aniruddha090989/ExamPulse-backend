package com.example.OnlineExam.controller;

import com.example.OnlineExam.dto.QuestionRequest;
import com.example.OnlineExam.dto.QuestionResponse;
import com.example.OnlineExam.model.Subject;
import com.example.OnlineExam.service.QuestionService;
import com.example.OnlineExam.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final QuestionService questionService;
    private final SubjectService subjectService;


    @PostMapping("/subjects")
    @ResponseStatus(HttpStatus.CREATED)
    public Subject createSubject(@RequestBody Subject subject) {
        return subjectService.create(subject);
    }

    // ============================================================
    // GET ALL QUESTIONS
    // ============================================================

    @GetMapping("/questions")
    public List<QuestionResponse> getQuestions() {
        return questionService.getAll();
    }

    // ============================================================
    // CREATE QUESTION
    // ============================================================

    @PostMapping("/questions")
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionResponse createQuestion(
            @Valid @RequestBody QuestionRequest request
    ) {
        return questionService.create(request);
    }

    // ============================================================
    // GET QUESTION
    // ============================================================

    @GetMapping("/questions/{id}")
    public QuestionResponse getQuestion(
            @PathVariable Long id
    ) {
        return questionService.getById(id);
    }

    // ============================================================
    // UPDATE QUESTION
    // ============================================================

    @PutMapping("/questions/{id}")
    public QuestionResponse updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionRequest request
    ) {
        return questionService.update(id, request);
    }

    // ============================================================
    // DELETE QUESTION
    // ============================================================

    @DeleteMapping("/questions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuestion(
            @PathVariable Long id
    ) {
        questionService.delete(id);
    }
}