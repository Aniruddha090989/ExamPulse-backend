package com.example.OnlineExam.dto;

import com.example.OnlineExam.model.Role;

public record UserResponse(Long userId, String username, String email, Role role) {}
