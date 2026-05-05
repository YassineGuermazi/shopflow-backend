package com.shopflow.main.repository;

import com.shopflow.main.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndActifTrue(Long id);

    Page<Product> findByActifTrue(Pageable pageable);

    Page<Product> findByCategoriesIdAndActifTrue(Long categoryId, Pageable pageable);

    Page<Product> findByPrixPromoIsNotNullAndActifTrue(Pageable pageable);

    Page<Product> findBySellerIdAndActifTrue(Long sellerId, Pageable pageable);

    Page<Product> findByNomContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndActifTrue(
            String nom, String description, Pageable pageable);

    long countBySellerId(Long sellerId);

    @Query("SELECT p FROM Product p WHERE p.seller.id = :sellerId AND p.stock <= :threshold AND p.actif = true")
    List<Product> findLowStockBySeller(@Param("sellerId") Long sellerId, @Param("threshold") int threshold);

    @Query("SELECT p FROM Product p JOIN p.orderItems oi GROUP BY p ORDER BY COUNT(oi) DESC")
    List<Product> findTopSelling(Pageable pageable);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE Product p SET p.stock = p.stock - :qty WHERE p.id = :id AND p.stock >= :qty")
    int decrementStock(@org.springframework.data.repository.query.Param("id") Long id, @org.springframework.data.repository.query.Param("qty") Integer qty);
}