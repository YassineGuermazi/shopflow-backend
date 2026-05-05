package com.shopflow.main.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class SellerDashboardResponse {
    private BigDecimal revenus;
    private Long commandesEnAttente;
    private Long totalProduits;
    private List<LowStockProductDTO> produitsStockFaible;
    private List<RecentOrderDTO> commandesRecentes;
}