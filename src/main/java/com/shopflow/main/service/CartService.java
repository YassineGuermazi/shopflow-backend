package com.shopflow.main.service;

import com.shopflow.main.dto.cart.*;
import com.shopflow.main.dto.cart.CartItemResponse;
import com.shopflow.main.dto.cart.CartResponse;
import com.shopflow.main.entity.*;
import com.shopflow.main.exception.BusinessException;
import com.shopflow.main.exception.ResourceNotFoundException;
import com.shopflow.main.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    private static final BigDecimal FRAIS_LIVRAISON = new BigDecimal("7.00");

    public CartResponse getCart() {
        Cart cart = getOrCreateCart();
        return toResponse(cart);
    }

    @Transactional
    public CartResponse addItem(AddCartItemRequest request) {
        Cart cart = getOrCreateCart();
        Product product = productRepository.findByIdAndActifTrue(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        int available = product.getStock();
        if (request.getVariantId() != null) {
            ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));
            available += variant.getStockSupplementaire();
        }
        if (request.getQuantite() > available) {
            throw new BusinessException("Insufficient stock. Available: " + available);
        }

        CartItem existing = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(request.getProductId()))
                .findFirst().orElse(null);

        if (existing != null) {
            existing.setQuantite(existing.getQuantite() + request.getQuantite());
            cartItemRepository.save(existing);
        } else {
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantite(request.getQuantite())
                    .build();
            if (request.getVariantId() != null) {
                item.setVariant(productVariantRepository.findById(request.getVariantId()).orElse(null));
            }
            cart.getItems().add(cartItemRepository.save(item));
        }

        cart.setDateModification(LocalDateTime.now());
        cartRepository.save(cart);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse updateItem(Long itemId, UpdateCartItemRequest request) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        validateCartOwnership(item.getCart());

        if (request.getQuantite() > item.getProduct().getStock()) {
            throw new BusinessException("Insufficient stock");
        }
        item.setQuantite(request.getQuantite());
        cartItemRepository.save(item);
        return toResponse(item.getCart());
    }

    @Transactional
    public CartResponse removeItem(Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        validateCartOwnership(item.getCart());
        Cart cart = item.getCart();
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse applyCoupon(ApplyCouponRequest request) {
        Cart cart = getOrCreateCart();
        Coupon coupon = couponRepository.findByCodeAndActifTrue(request.getCode())
                .orElseThrow(() -> new BusinessException("Invalid coupon code"));

        if (coupon.getDateExpiration().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Coupon has expired");
        }
        if (coupon.getUsagesActuels() >= coupon.getUsagesMax()) {
            throw new BusinessException("Coupon usage limit reached");
        }

        cart.setAppliedCoupon(coupon);
        cartRepository.save(cart);
        return toResponse(cart);
    }

    @Transactional
    public void clearCart() {
        Cart cart = getOrCreateCart();
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cart.setAppliedCoupon(null);
        cart.setDateModification(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Transactional
    public CartResponse removeCoupon() {
        Cart cart = getOrCreateCart();
        cart.setAppliedCoupon(null);
        cartRepository.save(cart);
        return toResponse(cart);
    }

    // ---- Helpers ----

    private Cart getOrCreateCart() {
        User user = getCurrentUser();
        return cartRepository.findByCustomerId(user.getId())
                .orElseGet(() -> {
                    Cart c = Cart.builder()
                            .customer(user)
                            .dateModification(LocalDateTime.now())
                            .build();
                    return cartRepository.save(c);
                });
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        BigDecimal sousTotal = items.stream()
                .map(CartItemResponse::getSousTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = BigDecimal.ZERO;
        String couponCode = null;
        if (cart.getAppliedCoupon() != null) {
            Coupon c = cart.getAppliedCoupon();
            couponCode = c.getCode();
            if (c.getType() == CouponType.PERCENT) {
                discount = sousTotal.multiply(c.getValeur()).divide(BigDecimal.valueOf(100));
            } else {
                discount = c.getValeur();
            }
        }

        BigDecimal totalTTC = sousTotal.subtract(discount).add(FRAIS_LIVRAISON);
        if (totalTTC.compareTo(BigDecimal.ZERO) < 0) totalTTC = BigDecimal.ZERO;

        return CartResponse.builder()
                .id(cart.getId())
                .items(items)
                .sousTotal(sousTotal)
                .fraisLivraison(FRAIS_LIVRAISON)
                .totalTTC(totalTTC)
                .couponCode(couponCode)
                .discount(discount)
                .dateModification(cart.getDateModification())
                .build();
    }

    private CartItemResponse toItemResponse(CartItem item) {
        BigDecimal price = item.getProduct().getPrix();
        if (item.getVariant() != null && item.getVariant().getPrixDelta() != null) {
            price = price.add(item.getVariant().getPrixDelta());
        }
        String variantDetails = item.getVariant() != null
                ? item.getVariant().getAttribut() + ": " + item.getVariant().getValeur()
                : null;

        List<String> images = item.getProduct().getImages();
        String image = (images != null && !images.isEmpty()) ? images.get(0) : null;

        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getNom())
                .productImage(image)
                .variantId(item.getVariant() != null ? item.getVariant().getId() : null)
                .variantDetails(variantDetails)
                .quantite(item.getQuantite())
                .prixUnitaire(price)
                .sousTotal(price.multiply(BigDecimal.valueOf(item.getQuantite())))
                .build();
    }

    private void validateCartOwnership(Cart cart) {
        User current = getCurrentUser();
        if (!cart.getCustomer().getId().equals(current.getId())) {
            throw new BusinessException("Not your cart item");
        }
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Authenticated user not found"));
    }
}
