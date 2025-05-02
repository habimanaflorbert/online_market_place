package com.marketplace.security;

import com.marketplace.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    void webSecurityCustomizer_ShouldConfigureWebSecurity() {
        // When
        var customizer = securityConfig.webSecurityCustomizer();

        // Then
        assertNotNull(customizer);
    }

    @Test
    void passwordEncoder_ShouldReturnBCryptPasswordEncoder() {
        // When
        PasswordEncoder encoder = securityConfig.passwordEncoder();

        // Then
        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);
    }

    @Test
    void authenticationProvider_ShouldConfigureProvider() {
        // When
        AuthenticationProvider provider = securityConfig.authenticationProvider();

        // Then
        assertNotNull(provider);
    }
} 