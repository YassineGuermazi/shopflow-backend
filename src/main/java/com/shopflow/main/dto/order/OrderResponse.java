package com.shopflow.main.dto.order;

import com.shopflow.main.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private String numeroCommande;
    private OrderStatus statut;
    private BigDecimal sousTotal;
    private BigDecimal fraisLivraison;
    private BigDecimal totalTTC;
    private LocalDateTime dateCommande;
    private String adresseLivraison;
    private List<OrderItemResponse> items;
    private String customerName;
    private boolean isNew;
}