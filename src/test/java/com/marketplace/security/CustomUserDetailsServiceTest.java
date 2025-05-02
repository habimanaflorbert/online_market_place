package com.marketplace.security;

import com.marketplace.entity.Role;
import com.marketplace.entity.User;
import com.marketplace.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setRole(Role.SHOPPER);
        testUser.setEnabled(true);
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetailsForValidUser() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        // Then
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertEquals(1, userDetails.getAuthorities().size());
        assertEquals("ROLE_SHOPPER", userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void loadUserByUsername_ShouldThrowExceptionForInvalidUser() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(UsernameNotFoundException.class, () -> 
            userDetailsService.loadUserByUsername("nonexistent@example.com")
        );
    }

    @Test
    void loadUserByUsername_ShouldThrowExceptionForDisabledUser() {
        // Given
        testUser.setEnabled(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // When/Then
        assertThrows(UsernameNotFoundException.class, () -> 
            userDetailsService.loadUserByUsername("test@example.com")
        );
    }
} 