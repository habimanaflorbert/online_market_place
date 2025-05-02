package com.marketplace.controller;

import com.marketplace.dto.ReviewRequest;
import com.marketplace.dto.ReviewResponse;
import com.marketplace.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Review", description = "Review management APIs")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/product/{productId}")
    @PreAuthorize("hasRole('SHOPPER')")
    @Operation(summary = "Create a new review")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewRequest request) {
        ReviewResponse response = reviewService.createReview(productId, request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SHOPPER') and @reviewService.isReviewOwner(#id)")
    @Operation(summary = "Update review")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(id, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get review by ID")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReview(id));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "List reviews for a product")
    public ResponseEntity<Page<ReviewResponse>> listProductReviews(
            @PathVariable Long productId,
            Pageable pageable) {
        return ResponseEntity.ok(reviewService.listProductReviews(productId, pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SHOPPER') and @reviewService.isReviewOwner(#id)")
    @Operation(summary = "Delete review")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    @Operation(summary = "List reviews by user")
    public ResponseEntity<Page<ReviewResponse>> listUserReviews(
            @PathVariable Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(reviewService.listUserReviews(userId, pageable));
    }
} 