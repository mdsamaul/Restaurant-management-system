package com.rms.service;
import com.rms.dto.request.LoginRequest;
import com.rms.dto.request.RegisterRequest;
import com.rms.dto.response.AuthResponse;
public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
