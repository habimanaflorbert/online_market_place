package com.marketplace.service.impl;

import com.marketplace.dto.ReviewRequest;
import com.marketplace.dto.ReviewResponse;
import com.marketplace.entity.Product;
import com.marketplace.entity.Review;
import com.marketplace.entity.User;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.ReviewRepository;
import com.marketplace.repository.UserRepository;
import com.marketplace.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ReviewResponse createReview(Long productId, ReviewRequest request) {
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        Review review = Review.builder()
                .user(currentUser)
                .product(product)
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        review = reviewRepository.save(review);
        return mapToResponse(review);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long id, ReviewRequest request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        review = reviewRepository.save(review);
        return mapToResponse(review);
    }

    @Override
    public ReviewResponse getReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
        return mapToResponse(review);
    }

    @Override
    public Page<ReviewResponse> listProductReviews(Long productId, Pageable pageable) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        return reviewRepository.findByProduct(product, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
        reviewRepository.delete(review);
    }

    @Override
    public Page<ReviewResponse> listUserReviews(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return reviewRepository.findByUser(user, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public boolean isReviewOwner(Long reviewId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return reviewRepository.existsByIdAndUser(reviewId, currentUser);
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .userId(review.getUser().getId())
                .userName(review.getUser().getName())
                .userEmail(review.getUser().getEmail())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .build();
    }
} 