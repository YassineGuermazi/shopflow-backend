package com.shopflow.main.controller;

import com.shopflow.main.dto.coupon.*;
import com.shopflow.main.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Tag(name = "Coupons", description = "Coupon management")
public class CouponController {

    private final CouponService couponService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all coupons", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<java.util.List<CouponResponse>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a coupon", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CouponResponse> createCoupon(@Valid @RequestBody CouponRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(couponService.createCoupon(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a coupon", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CouponResponse> updateCoupon(@PathVariable Long id,
                                                       @Valid @RequestBody CouponRequest request) {
        return ResponseEntity.ok(couponService.updateCoupon(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate a coupon", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validate/{code}")
    @Operation(summary = "Check if a coupon code is valid")
    public ResponseEntity<CouponResponse> validateCoupon(@PathVariable String code) {
        return ResponseEntity.ok(couponService.validateCoupon(code));
    }
}