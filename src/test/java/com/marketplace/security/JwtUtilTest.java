package com.marketplace.security;

import com.marketplace.entity.Role;
import com.marketplace.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtUtilTest {

    @Spy
    private JwtUtil jwtUtil;
    private User testUser;
    private static final String SECRET_KEY = "your-secret-key-here-must-be-at-least-256-bits-long";
    private static final long EXPIRATION_TIME = 86400000; // 24 hours

    @BeforeEach
    void setUp() {
        jwtUtil = spy(new JwtUtil());
        jwtUtil.setSecretKey(SECRET_KEY);
        jwtUtil.setExpirationTime(EXPIRATION_TIME);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.SHOPPER);
    }

    @Test
    void generateToken_ShouldGenerateValidToken() {
        // When
        String token = jwtUtil.generateToken(testUser);

        // Then
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void extractUsername_ShouldExtractUsernameFromToken() {
        // Given
        String token = jwtUtil.generateToken(testUser);

        // When
        String username = jwtUtil.extractUsername(token);

        // Then
        assertEquals("test@example.com", username);
    }

    @Test
    void isTokenValid_ShouldReturnTrueForValidToken() {
        // Given
        String token = jwtUtil.generateToken(testUser);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");

        // When
        boolean isValid = jwtUtil.isTokenValid(token, userDetails);

        // Then
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalseForInvalidUsername() {
        // Given
        String token = jwtUtil.generateToken(testUser);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("different@example.com");

        // When
        boolean isValid = jwtUtil.isTokenValid(token, userDetails);

        // Then
        assertFalse(isValid);
    }

    @Test
    void isTokenExpired_ShouldReturnFalseForValidToken() {
        // Given
        String token = jwtUtil.generateToken(testUser);

        // When
        boolean isExpired = jwtUtil.isTokenExpired(token);

        // Then
        assertFalse(isExpired);
    }

    @Test
    void isTokenExpired_ShouldReturnTrueForExpiredToken() {
        // Given
        String token = jwtUtil.generateToken(testUser);
        Date expiredDate = new Date(System.currentTimeMillis() - 1000); // 1 second in the past
        doReturn(expiredDate).when(jwtUtil).extractExpiration(token);

        // When
        boolean result = jwtUtil.isTokenExpired(token);

        // Then
        assertTrue(result);
    }
} 