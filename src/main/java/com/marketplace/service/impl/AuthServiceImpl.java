package com.marketplace.service.impl;

import com.marketplace.dto.*;
import com.marketplace.entity.Role;
import com.marketplace.entity.User;
import com.marketplace.exception.MarketplaceException;
import com.marketplace.repository.UserRepository;
import com.marketplace.security.JwtUtil;
import com.marketplace.service.AuthService;
import com.marketplace.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private EmailService emailService;
    
    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            log.debug("Login attempt for user: {}", request.getEmail());
            
            // First check if user exists and is enabled
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> {
                        log.error("User not found: {}", request.getEmail());
                        return new MarketplaceException("Invalid email or password. Please check your credentials and try again.");
                    });
            
            log.debug("User found: {}, enabled: {}", user.getEmail(), user.isEnabled());
            
            if (!user.isEnabled()) {
                log.error("Login failed - Email not verified for user: {}", request.getEmail());
                throw new MarketplaceException("Please verify your email address before logging in. Check your inbox for the verification link.");
            }
            
            log.debug("Attempting authentication for user: {}", request.getEmail());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            // Get the user from repository again after authentication
            user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new MarketplaceException("User not found after authentication"));
            
            String token = jwtUtil.generateToken(user);
            
            log.debug("User authenticated successfully: {}", request.getEmail());
            return AuthResponse.builder()
                    .token(token)
                    .email(user.getEmail())
                    .name(user.getName())
                    .role(user.getRole().name())
                    .emailVerified(user.isEnabled())
                    .build();
        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}. Error: {}", request.getEmail(), e.getMessage());
            throw new MarketplaceException("Invalid email or password. Please check your credentials and try again.");
        } catch (MarketplaceException e) {
            log.error("Marketplace exception during login: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error during authentication for user: {}. Error: {}", request.getEmail(), e.getMessage(), e);
            throw new MarketplaceException("Authentication failed. Please try again later.");
        }
    }
    
    @Override
    public AuthResponse authenticate(AuthRequest request) {
        try {
            log.debug("Authentication attempt for user: {}", request.getEmail());
            
            // First check if user exists and is enabled
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> {
                        log.error("User not found: {}", request.getEmail());
                        return new MarketplaceException("Invalid email or password. Please check your credentials and try again.");
                    });
            
            log.debug("User found: {}, enabled: {}", user.getEmail(), user.isEnabled());
            
            if (!user.isEnabled()) {
                log.error("Authentication failed - Email not verified for user: {}", request.getEmail());
                throw new MarketplaceException("Please verify your email address before logging in. Check your inbox for the verification link.");
            }
            
            log.debug("Attempting authentication for user: {}", request.getEmail());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            // Get the user from repository again after authentication
            user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new MarketplaceException("User not found after authentication"));
            
            String token = jwtUtil.generateToken(user);
            
            log.debug("User authenticated successfully: {}", request.getEmail());
            return AuthResponse.builder()
                    .token(token)
                    .email(user.getEmail())
                    .name(user.getName())
                    .role(user.getRole().name())
                    .emailVerified(user.isEnabled())
                    .build();
        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}. Error: {}", request.getEmail(), e.getMessage());
            throw new MarketplaceException("Invalid email or password. Please check your credentials and try again.");
        } catch (MarketplaceException e) {
            log.error("Marketplace exception during authentication: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error during authentication for user: {}. Error: {}", request.getEmail(), e.getMessage(), e);
            throw new MarketplaceException("Authentication failed. Please try again later.");
        }
    }
    
    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("Registration failed - Email already exists: {}", request.getEmail());
            throw new MarketplaceException("This email address is already registered. Please use a different email or try logging in.");
        }
        
        try {
            User user = User.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(request.getRole())
                    .enabled(false)  // Set to false to require email verification
                    .verificationToken(UUID.randomUUID().toString())  // Generate verification token
                    .build();
            
            user = userRepository.save(user);
            
            // Send verification email
            emailService.sendVerificationEmail(user);
            
            return AuthResponse.builder()
                    .token(jwtUtil.generateToken(user))
                    .email(user.getEmail())
                    .name(user.getName())
                    .role(user.getRole().name())
                    .emailVerified(false)  // Email is not verified yet
                    .build();
        } catch (Exception e) {
            log.error("Error during registration for user: {}", request.getEmail(), e);
            throw new MarketplaceException("Registration failed. Please try again later.");
        }
    }
    
    @Override
    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));
        
        user.setEnabled(true);
        user.setVerificationToken(null);
        userRepository.save(user);
    }
    
    @Override
    public UserProfile getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return UserProfile.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
    
    @Override
    public UserProfile updateProfile(String email, UserProfile profile) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (profile.getName() != null && !profile.getName().isEmpty()) {
            user.setName(profile.getName());
        }
        
        if (profile.getEmail() != null && !profile.getEmail().isEmpty() && !profile.getEmail().equals(email)) {
            if (userRepository.existsByEmail(profile.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(profile.getEmail());
        }
        
        if (profile.getCurrentPassword() != null && !profile.getCurrentPassword().isEmpty() &&
            profile.getNewPassword() != null && !profile.getNewPassword().isEmpty()) {
            if (!passwordEncoder.matches(profile.getCurrentPassword(), user.getPassword())) {
                throw new RuntimeException("Current password is incorrect");
            }
            user.setPassword(passwordEncoder.encode(profile.getNewPassword()));
        }
        
        userRepository.save(user);
        
        return UserProfile.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
} 