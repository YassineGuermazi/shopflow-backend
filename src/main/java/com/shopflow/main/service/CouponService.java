package com.shopflow.main.service;

import com.shopflow.main.dto.coupon.*;
import com.shopflow.main.entity.Coupon;
import com.shopflow.main.exception.ResourceNotFoundException;
import com.shopflow.main.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public java.util.List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(this::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public CouponResponse createCoupon(CouponRequest request) {
        if (couponRepository.existsByCode(request.getCode())) {
            throw new com.shopflow.main.exception.BusinessException("Coupon code already exists");
        }
        Coupon coupon = Coupon.builder()
                .code(request.getCode())
                .type(request.getType())
                .valeur(request.getValeur())
                .dateExpiration(request.getDateExpiration())
                .usagesMax(request.getUsagesMax())
                .usagesActuels(0)
                .actif(true)
                .build();
        return toResponse(couponRepository.save(coupon));
    }

    @Transactional
    public CouponResponse updateCoupon(Long id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found: " + id));
        coupon.setValeur(request.getValeur());
        coupon.setDateExpiration(request.getDateExpiration());
        coupon.setUsagesMax(request.getUsagesMax());
        return toResponse(couponRepository.save(coupon));
    }

    @Transactional
    public void deleteCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found: " + id));
        coupon.setActif(false);
        couponRepository.save(coupon);
    }

    public CouponResponse validateCoupon(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found: " + code));
        boolean valid = coupon.isActif()
                && coupon.getDateExpiration().isAfter(LocalDateTime.now())
                && coupon.getUsagesActuels() < coupon.getUsagesMax();
        CouponResponse response = toResponse(coupon);
        response.setValid(valid);
        return response;
    }

    private CouponResponse toResponse(Coupon c) {
        return CouponResponse.builder()
                .id(c.getId())
                .code(c.getCode())
                .type(c.getType())
                .valeur(c.getValeur())
                .dateExpiration(c.getDateExpiration())
                .usagesMax(c.getUsagesMax())
                .usagesActuels(c.getUsagesActuels())
                .actif(c.isActif())
                .build();
    }
}