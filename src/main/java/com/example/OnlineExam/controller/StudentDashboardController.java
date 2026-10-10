package com.example.OnlineExam.controller;

import com.example.OnlineExam.dto.StudentDashboardResponse;
import com.example.OnlineExam.security.CustomUserDetails;
import com.example.OnlineExam.service.StudentDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentDashboardController {

    private final StudentDashboardService studentDashboardService;

    @GetMapping("/dashboard")
    public StudentDashboardResponse getDashboard(
            Authentication authentication
    ) {

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        Long userId = userDetails.getUserId();

        return studentDashboardService.getDashboard(userId);
    }
}