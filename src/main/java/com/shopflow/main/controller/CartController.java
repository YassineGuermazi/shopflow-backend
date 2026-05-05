package com.shopflow.main.controller;

import com.shopflow.main.dto.cart.*;
import com.shopflow.main.dto.cart.CartResponse;
import com.shopflow.main.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Shopping cart management")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get the current user's cart")
    public ResponseEntity<CartResponse> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @DeleteMapping
    @Operation(summary = "Clear the entire cart")
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/items")
    @Operation(summary = "Add an item to the cart")
    public ResponseEntity<CartResponse> addItem(@Valid @RequestBody AddCartItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(request));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update item quantity")
    public ResponseEntity<CartResponse> updateItem(@PathVariable Long itemId,
                                                   @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateItem(itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove an item from the cart")
    public ResponseEntity<CartResponse> removeItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItem(itemId));
    }

    @PostMapping("/coupon")
    @Operation(summary = "Apply a coupon code")
    public ResponseEntity<CartResponse> applyCoupon(@Valid @RequestBody ApplyCouponRequest request) {
        return ResponseEntity.ok(cartService.applyCoupon(request));
    }

    @DeleteMapping("/coupon")
    @Operation(summary = "Remove the applied coupon")
    public ResponseEntity<CartResponse> removeCoupon() {
        return ResponseEntity.ok(cartService.removeCoupon());
    }
}