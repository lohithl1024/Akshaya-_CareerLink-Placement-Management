package com.careerlink.service;

import com.careerlink.dto.auth.AuthResponse;
import com.careerlink.dto.auth.LoginRequest;
import com.careerlink.dto.auth.RegisterRequest;
import com.careerlink.dto.user.UserResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    UserResponse getCurrentUser(String email);
}
