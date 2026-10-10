package com.example.OnlineExam.controller;

import com.example.OnlineExam.dto.ExaminerDashboardResponse;
import com.example.OnlineExam.dto.ExaminerStudentResultResponse;
import com.example.OnlineExam.service.ExaminerDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/examiner-dashboard")
@RequiredArgsConstructor
public class ExaminerDashboardController {

    private final ExaminerDashboardService examinerDashboardService;


    // =========================================================
    // EXAMINER DASHBOARD
    // =========================================================

    @GetMapping
    public ExaminerDashboardResponse getDashboard() {

        return examinerDashboardService.getDashboardData();
    }


    // =========================================================
    // VIEW STUDENT RESULTS FOR AN EXAM
    // =========================================================

    @GetMapping("/exams/{examId}/results")
    public List<ExaminerStudentResultResponse> getStudentResults(
            @PathVariable Long examId
    ) {

        return examinerDashboardService.getStudentResults(
                examId
        );
    }
}