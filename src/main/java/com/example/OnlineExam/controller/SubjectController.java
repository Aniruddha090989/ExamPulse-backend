package com.example.OnlineExam.controller;

import com.example.OnlineExam.model.Subject;
import com.example.OnlineExam.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;


    // =========================================================
    // GET ALL SUBJECTS
    // =========================================================

    @GetMapping
    public List<Subject> getAll() {

        return subjectService.getAll();

    }


    // =========================================================
    // GET SUBJECT BY ID
    // =========================================================

    @GetMapping("/{id}")
    public Subject getById(
            @PathVariable Long id
    ) {

        return subjectService.getById(id);

    }


    // =========================================================
    // CREATE SUBJECT
    // =========================================================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Subject createSubject(
            @RequestBody Subject subject
    ) {

        return subjectService.create(
                subject
        );

    }


    // =========================================================
    // DELETE SUBJECT
    // =========================================================

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubject(
            @PathVariable Long id
    ) {

        subjectService.delete(id);

    }

}