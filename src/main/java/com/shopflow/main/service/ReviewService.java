package com.shopflow.main.service;

import com.shopflow.main.dto.review.*;
import com.shopflow.main.entity.*;
import com.shopflow.main.exception.BusinessException;
import com.shopflow.main.exception.ResourceNotFoundException;
import com.shopflow.main.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewResponse createReview(ReviewRequest request) {
        User customer = getCurrentUser();
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        boolean hasPurchased = orderRepository.existsByCustomerIdAndItemsProductIdAndStatut(
                customer.getId(), product.getId(), OrderStatus.DELIVERED);
        if (!hasPurchased) {
            throw new BusinessException("You can only review products you have purchased");
        }

        if (reviewRepository.existsByCustomerIdAndProductId(customer.getId(), product.getId())) {
            throw new BusinessException("You have already reviewed this product");
        }

        Review review = Review.builder()
                .customer(customer)
                .product(product)
                .note(request.getNote())
                .commentaire(request.getCommentaire())
                .dateCreation(LocalDateTime.now())
                .approuve(false)
                .build();

        return toResponse(reviewRepository.save(review));
    }

    public List<ReviewResponse> getProductReviews(Long productId) {
        return reviewRepository.findByProductIdAndApprouveTrue(productId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponse approveReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + id));
        review.setApprouve(true);
        return toResponse(reviewRepository.save(review));
    }

    private ReviewResponse toResponse(Review r) {
        return ReviewResponse.builder()
                .id(r.getId())
                .productId(r.getProduct().getId())
                .productName(r.getProduct().getNom())
                .customerName(r.getCustomer().getPrenom() + " " + r.getCustomer().getNom())
                .note(r.getNote())
                .commentaire(r.getCommentaire())
                .dateCreation(r.getDateCreation())
                .approuve(r.isApprouve())
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Authenticated user not found"));
    }
}