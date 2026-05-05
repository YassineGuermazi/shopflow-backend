package com.shopflow.main.repository;

import com.shopflow.main.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProductIdAndApprouveTrue(Long productId);

    boolean existsByCustomerIdAndProductId(Long customerId, Long productId);

    long countByProductIdAndApprouveTrue(Long productId);

    @Query("SELECT AVG(r.note) FROM Review r WHERE r.product.id = :productId AND r.approuve = true")
    Double findAverageRatingByProductId(@Param("productId") Long productId);
}