package com.marketplace.service;

import com.marketplace.dto.AuthRequest;
import com.marketplace.dto.AuthResponse;
import com.marketplace.dto.RegisterRequest;
import com.marketplace.entity.Role;
import com.marketplace.entity.User;
import com.marketplace.repository.UserRepository;
import com.marketplace.security.JwtUtil;
import com.marketplace.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private AuthRequest authRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");
        registerRequest.setRole(Role.SHOPPER);

        authRequest = new AuthRequest();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password");

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.SHOPPER);
        testUser.setEnabled(true);
    }

    @Test
    void register_ShouldRegisterUserSuccessfully() {
        // Given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtil.generateToken(any(User.class))).thenReturn("test.token");

        // When
        AuthResponse response = authService.register(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("SHOPPER", response.getRole());
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrowExceptionWhenEmailExists() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When/Then
        assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(jwtUtil, never()).generateToken(any(User.class));
    }

    @Test
    void authenticate_ShouldAuthenticateUserSuccessfully() {
        // Given
        when(userRepository.findByEmail(authRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(any(User.class))).thenReturn("test.token");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(new UsernamePasswordAuthenticationToken(testUser, null));

        // When
        AuthResponse response = authService.authenticate(authRequest);

        // Then
        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("SHOPPER", response.getRole());
        assertEquals("test.token", response.getToken());
        verify(userRepository, times(2)).findByEmail(authRequest.getEmail());
        verify(jwtUtil).generateToken(testUser);
    }

    @Test
    void authenticate_ShouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(RuntimeException.class, () -> authService.authenticate(authRequest));
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(jwtUtil, never()).generateToken(any(User.class));
        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
} 