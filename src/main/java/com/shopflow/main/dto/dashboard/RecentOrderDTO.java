package com.shopflow.main.dto.dashboard;

import com.shopflow.main.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RecentOrderDTO {
    private Long orderId;
    private String numeroCommande;
    private String customerName;
    private BigDecimal totalTTC;
    private OrderStatus statut;
    private LocalDateTime dateCommande;
}