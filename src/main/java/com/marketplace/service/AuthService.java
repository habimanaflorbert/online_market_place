package com.marketplace.service;

import com.marketplace.dto.*;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
    void verifyEmail(String token);
    UserProfile getProfile(String email);
    UserProfile updateProfile(String email, UserProfile profile);
    AuthResponse authenticate(AuthRequest request);
} 