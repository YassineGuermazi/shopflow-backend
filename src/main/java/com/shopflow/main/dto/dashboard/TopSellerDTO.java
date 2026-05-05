package com.shopflow.main.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TopSellerDTO {
    private Long sellerId;
    private String nomBoutique;
    private BigDecimal revenus;
    private Long totalCommandes;
}