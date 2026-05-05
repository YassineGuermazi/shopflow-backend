package com.shopflow.main.dto.cart;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApplyCouponRequest {
    @NotBlank
    private String code;
}