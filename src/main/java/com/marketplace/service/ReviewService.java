package com.marketplace.service;

import com.marketplace.dto.ReviewRequest;
import com.marketplace.dto.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewResponse createReview(Long productId, ReviewRequest request);
    ReviewResponse updateReview(Long id, ReviewRequest request);
    ReviewResponse getReview(Long id);
    Page<ReviewResponse> listProductReviews(Long productId, Pageable pageable);
    void deleteReview(Long id);
    Page<ReviewResponse> listUserReviews(Long userId, Pageable pageable);
    boolean isReviewOwner(Long reviewId);
} 