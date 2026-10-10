package com.example.OnlineExam.controller;

import com.example.OnlineExam.dto.ExamRequest;
import com.example.OnlineExam.dto.ExamResponse;
import com.example.OnlineExam.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;


    // =========================================================
    // GET ALL EXAMS
    // =========================================================

    @GetMapping
    public List<ExamResponse> getAll() {

        return examService.getAll();
    }


    // =========================================================
    // GET AVAILABLE EXAMS
    // =========================================================

    @GetMapping("/available")
    public List<ExamResponse> getAvailable() {

        return examService.getAvailable();
    }


    // =========================================================
    // GET EXAM BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ExamResponse getById(
            @PathVariable Long id
    ) {

        return examService.getById(id);
    }


    // =========================================================
    // CREATE EXAM
    // =========================================================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExamResponse createExam(
            @Valid @RequestBody ExamRequest request
    ) {

        return examService.toResponse(
                examService.create(request)
        );
    }


    // =========================================================
    // UPDATE EXAM
    // =========================================================

    @PutMapping("/{id}")
    public ExamResponse updateExam(
            @PathVariable Long id,
            @Valid @RequestBody ExamRequest request
    ) {

        return examService.toResponse(
                examService.update(
                        id,
                        request
                )
        );
    }


    // =========================================================
    // DELETE EXAM
    // =========================================================

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExam(
            @PathVariable Long id
    ) {

        examService.delete(id);
    }
}