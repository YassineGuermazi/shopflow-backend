package com.shopflow.main.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TopProductDTO {
    private Long productId;
    private String nom;
    private Long totalVentes;
    private BigDecimal revenus;
}
