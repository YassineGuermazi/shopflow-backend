package com.shopflow.main.controller;

import com.shopflow.main.dto.order.*;
import com.shopflow.main.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order lifecycle management")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Place an order from the current cart")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order details")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/my")
    @Operation(summary = "Get my orders")
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getMyOrders(page, size));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all orders (ADMIN)")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(orderService.getAllOrders(page, size));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Update order status (SELLER/ADMIN)")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(id, request));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Cancel an order (PENDING or PAID only)")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }
}
