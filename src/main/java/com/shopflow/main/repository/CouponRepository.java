package com.shopflow.main.repository;

import com.shopflow.main.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCodeAndActifTrue(String code);
    Optional<Coupon> findByCode(String code);
    boolean existsByCode(String code);
}