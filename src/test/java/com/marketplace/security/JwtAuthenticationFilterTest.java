package com.marketplace.security;

import com.marketplace.entity.Role;
import com.marketplace.entity.User;
import com.marketplace.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private User testUser;
    private String token;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setEnabled(true);
        testUser.setRole(Role.SHOPPER);

        token = "valid.jwt.token";
        
        try {
            PrintWriter writer = mock(PrintWriter.class);
            when(response.getWriter()).thenReturn(writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void doFilterInternal_ValidToken_ShouldSetAuthentication() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtUtil.validateToken(anyString(), any(User.class))).thenReturn(true);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("test@example.com", authentication.getName());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_NoAuthHeader_ShouldContinueChain() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_InvalidTokenFormat_ShouldContinueChain() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn("InvalidFormat " + token);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_UserNotFound_ShouldContinueChain() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response.getWriter()).write("User not found");
    }

    @Test
    void doFilterInternal_InvalidToken_ShouldContinueChain() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtUtil.validateToken(anyString(), any(User.class))).thenReturn(false);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response.getWriter()).write("Invalid token");
    }

    @Test
    void doFilterInternal_UserDisabled_ShouldReturnUnauthorized() throws Exception {
        // Given
        testUser.setEnabled(false);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtUtil.validateToken(anyString(), any(User.class))).thenReturn(true);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response.getWriter()).write("User is disabled");
    }
} 