package com.shopflow.main.repository;

import com.shopflow.main.entity.SellerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerProfileRepository extends JpaRepository<SellerProfile, Long> {
    Optional<SellerProfile> findByUserId(Long userId);
}