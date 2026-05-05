package com.shopflow.main.controller;

import com.shopflow.main.dto.review.*;
import com.shopflow.main.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Product reviews and ratings")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Post a review (verified purchase only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(request));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get approved reviews for a product")
    public ResponseEntity<List<ReviewResponse>> getProductReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve a review (ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ReviewResponse> approveReview(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.approveReview(id));
    }
}
