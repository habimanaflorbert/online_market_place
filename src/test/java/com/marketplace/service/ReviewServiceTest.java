package com.marketplace.service;

import com.marketplace.dto.ReviewRequest;
import com.marketplace.dto.ReviewResponse;
import com.marketplace.entity.Product;
import com.marketplace.entity.Review;
import com.marketplace.entity.User;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.ReviewRepository;
import com.marketplace.repository.UserRepository;
import com.marketplace.service.impl.ReviewServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User testUser;
    private Product testProduct;
    private Review testReview;
    private ReviewRequest reviewRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .build();

        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .build();

        testReview = Review.builder()
                .id(1L)
                .user(testUser)
                .product(testProduct)
                .rating(5)
                .comment("Great product!")
                .createdAt(LocalDateTime.now())
                .build();

        reviewRequest = new ReviewRequest();
        reviewRequest.setRating(5);
        reviewRequest.setComment("Great product!");

        // Mock security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createReview_ShouldCreateReviewSuccessfully() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // When
        ReviewResponse response = reviewService.createReview(1L, reviewRequest);

        // Then
        assertNotNull(response);
        assertEquals(testReview.getId(), response.getId());
        assertEquals(testReview.getRating(), response.getRating());
        assertEquals(testReview.getComment(), response.getComment());
        assertEquals(testUser.getEmail(), response.getUserEmail());
        assertEquals(testProduct.getId(), response.getProductId());
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void createReview_ShouldThrowExceptionWhenRatingInvalid() {
        // Given
        reviewRequest.setRating(0);

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> reviewService.createReview(1L, reviewRequest));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createReview_ShouldThrowExceptionWhenProductNotFound() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(EntityNotFoundException.class, () -> reviewService.createReview(1L, reviewRequest));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void getReview_ShouldReturnReviewSuccessfully() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        // When
        ReviewResponse response = reviewService.getReview(1L);

        // Then
        assertNotNull(response);
        assertEquals(testReview.getId(), response.getId());
        assertEquals(testReview.getRating(), response.getRating());
        assertEquals(testReview.getComment(), response.getComment());
        assertEquals(testUser.getEmail(), response.getUserEmail());
        assertEquals(testProduct.getId(), response.getProductId());
    }

    @Test
    void updateReview_ShouldReturnUpdatedReview() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // When
        ReviewResponse response = reviewService.updateReview(1L, reviewRequest);

        // Then
        assertNotNull(response);
        assertEquals(testReview.getId(), response.getId());
        assertEquals(testReview.getRating(), response.getRating());
        assertEquals(testReview.getComment(), response.getComment());
        assertEquals(testUser.getEmail(), response.getUserEmail());
        assertEquals(testProduct.getId(), response.getProductId());
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void listProductReviews_ShouldReturnPageOfReviews() {
        // Given
        Page<Review> reviewPage = new PageImpl<>(List.of(testReview));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(reviewRepository.findByProduct(eq(testProduct), any(PageRequest.class))).thenReturn(reviewPage);

        // When
        Page<ReviewResponse> response = reviewService.listProductReviews(1L, PageRequest.of(0, 10));

        // Then
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        ReviewResponse reviewResponse = response.getContent().get(0);
        assertEquals(testReview.getId(), reviewResponse.getId());
        assertEquals(testReview.getRating(), reviewResponse.getRating());
        assertEquals(testReview.getComment(), reviewResponse.getComment());
        assertEquals(testUser.getEmail(), reviewResponse.getUserEmail());
        assertEquals(testProduct.getId(), reviewResponse.getProductId());
    }

    @Test
    void deleteReview_ShouldDeleteReview() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        // When
        reviewService.deleteReview(1L);

        // Then
        verify(reviewRepository).delete(testReview);
    }
} 