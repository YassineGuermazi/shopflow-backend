package com.shopflow.main.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class AdminDashboardResponse {
    private BigDecimal chiffreAffairesGlobal;
    private Long totalCommandes;
    private Long totalUtilisateurs;
    private Long totalProduits;
    private List<TopProductDTO> topProduits;
    private List<TopSellerDTO> topVendeurs;
    private List<RecentOrderDTO> commandesRecentes;
}