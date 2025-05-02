package com.marketplace.controller;

import com.marketplace.dto.ReviewRequest;
import com.marketplace.dto.ReviewResponse;
import com.marketplace.entity.Product;
import com.marketplace.entity.Review;
import com.marketplace.entity.Role;
import com.marketplace.entity.User;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.ReviewRepository;
import com.marketplace.repository.UserRepository;
import com.marketplace.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private ReviewController reviewController;

    private ReviewRequest reviewRequest;
    private ReviewResponse reviewResponse;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.SHOPPER);

        reviewRequest = new ReviewRequest();
        reviewRequest.setRating(5);
        reviewRequest.setComment("Great product!");

        reviewResponse = new ReviewResponse();
        reviewResponse.setId(1L);
        reviewResponse.setRating(5);
        reviewResponse.setComment("Great product!");
        reviewResponse.setUserEmail("test@example.com");

        // Set up security context
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
    }

    @Test
    void createReview_ShouldCreateReviewSuccessfully() {
        // Given
        when(reviewService.createReview(anyLong(), any(ReviewRequest.class))).thenReturn(reviewResponse);

        // When
        ResponseEntity<ReviewResponse> response = reviewController.createReview(1L, reviewRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals(5, response.getBody().getRating());
        assertEquals("Great product!", response.getBody().getComment());
        assertEquals("test@example.com", response.getBody().getUserEmail());
        assertNotNull(response.getHeaders().getLocation());
        assertTrue(response.getHeaders().getLocation().toString().endsWith("/1"));
        
        verify(reviewService).createReview(anyLong(), any(ReviewRequest.class));
    }

    @Test
    void getReview_ShouldReturnReviewSuccessfully() {
        // Given
        when(reviewService.getReview(anyLong())).thenReturn(reviewResponse);

        // When
        ResponseEntity<ReviewResponse> response = reviewController.getReview(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals(5, response.getBody().getRating());
        assertEquals("Great product!", response.getBody().getComment());
        assertEquals("test@example.com", response.getBody().getUserEmail());
        
        verify(reviewService).getReview(anyLong());
    }

    @Test
    void updateReview_ShouldUpdateReviewSuccessfully() {
        // Given
        when(reviewService.updateReview(anyLong(), any(ReviewRequest.class))).thenReturn(reviewResponse);

        // When
        ResponseEntity<ReviewResponse> response = reviewController.updateReview(1L, reviewRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals(5, response.getBody().getRating());
        assertEquals("Great product!", response.getBody().getComment());
        assertEquals("test@example.com", response.getBody().getUserEmail());
        
        verify(reviewService).updateReview(anyLong(), any(ReviewRequest.class));
    }

    @Test
    void deleteReview_ShouldDeleteReviewSuccessfully() {
        // Given
        doNothing().when(reviewService).deleteReview(anyLong());

        // When
        ResponseEntity<Void> response = reviewController.deleteReview(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        
        verify(reviewService).deleteReview(anyLong());
    }
} 