package com.shopflow.main.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LowStockProductDTO {
    private Long productId;
    private String nom;
    private Integer stock;
}