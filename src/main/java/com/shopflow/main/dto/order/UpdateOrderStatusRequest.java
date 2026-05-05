package com.shopflow.main.dto.order;

import com.shopflow.main.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    @NotNull
    private OrderStatus statut;
}