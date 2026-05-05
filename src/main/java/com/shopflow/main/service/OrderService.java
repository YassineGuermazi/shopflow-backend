package com.shopflow.main.service;

import com.shopflow.main.dto.order.*;
import com.shopflow.main.entity.*;
import com.shopflow.main.exception.BusinessException;
import com.shopflow.main.exception.ResourceNotFoundException;
import com.shopflow.main.exception.UnauthorizedException;
import com.shopflow.main.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    private static final BigDecimal FRAIS_LIVRAISON = new BigDecimal("7.00");

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        User customer = getCurrentUser();
        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() -> new BusinessException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new BusinessException("Cannot place order with empty cart");
        }

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(customer.getId())) {
            throw new UnauthorizedException("Address does not belong to you");
        }

        // Final stock check + deduct
        System.out.println("📦 Processing stock for " + cart.getItems().size() + " items...");
        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            
            int requestedQty = cartItem.getQuantite();
            int currentStock = product.getStock() != null ? product.getStock() : 0;
            
            System.out.println("📉 Product: " + product.getNom() + " | Current: " + currentStock + " | Requested: " + requestedQty);
            
            if (currentStock < requestedQty) {
                throw new BusinessException("Insufficient stock for: " + product.getNom());
            }
            
            // Standard JPA update (most reliable for MySQL transactions)
            product.setStock(currentStock - requestedQty);
            productRepository.saveAndFlush(product);
            
            System.out.println("✅ MySQL UPDATED: " + product.getNom() + " New Stock = " + product.getStock());
        }

        BigDecimal sousTotal = cart.getItems().stream()
                .map(i -> {
                    BigDecimal price = i.getProduct().getPrixPromo() != null ? 
                                       i.getProduct().getPrixPromo() : 
                                       i.getProduct().getPrix();
                    return price.multiply(BigDecimal.valueOf(i.getQuantite()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = BigDecimal.ZERO;
        if (cart.getAppliedCoupon() != null) {
            Coupon coupon = cart.getAppliedCoupon();
            if (coupon.getType() == CouponType.PERCENT) {
                discount = sousTotal.multiply(coupon.getValeur()).divide(BigDecimal.valueOf(100));
            } else {
                discount = coupon.getValeur();
            }
            coupon.setUsagesActuels(coupon.getUsagesActuels() + 1);
            couponRepository.save(coupon);
        }

        BigDecimal totalTTC = sousTotal.subtract(discount).add(FRAIS_LIVRAISON);

        Order order = Order.builder()
                .customer(customer)
                .statut(OrderStatus.PENDING)
                .numeroCommande(generateOrderNumber())
                .adresseLivraison(formatAddress(address))
                .sousTotal(sousTotal)
                .fraisLivraison(FRAIS_LIVRAISON)
                .totalTTC(totalTTC)
                .dateCommande(LocalDateTime.now())
                .isNew(true)
                .build();

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            BigDecimal price = cartItem.getProduct().getPrix();
            if (cartItem.getVariant() != null && cartItem.getVariant().getPrixDelta() != null) {
                price = price.add(cartItem.getVariant().getPrixDelta());
            }
            return OrderItem.builder()
                    .order(savedOrder)
                    .product(cartItem.getProduct())
                    .variant(cartItem.getVariant())
                    .quantite(cartItem.getQuantite())
                    .prixUnitaire(price)
                    .build();
        }).collect(Collectors.toList());

        orderItemRepository.saveAll(orderItems);
        savedOrder.setItems(orderItems);

        // Clear cart items via orphanRemoval
        cart.getItems().clear();
        cart.setAppliedCoupon(null);
        cartRepository.saveAndFlush(cart);

        return toResponse(savedOrder);
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        validateOrderAccess(order);
        return toResponse(order);
    }

    public Page<OrderResponse> getMyOrders(int page, int size) {
        User customer = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCommande").descending());
        return orderRepository.findByCustomerId(customer.getId(), pageable).map(this::toResponse);
    }

    public Page<OrderResponse> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCommande").descending());
        return orderRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional
    public OrderResponse updateStatus(Long id, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        order.setStatut(request.getStatut());
        order.setNew(false);
        return toResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        validateOrderOwnership(order);

        if (order.getStatut() != OrderStatus.PENDING && order.getStatut() != OrderStatus.PAID) {
            throw new BusinessException("Order cannot be cancelled in status: " + order.getStatut());
        }

        // Restore stock
        order.getItems().forEach(item -> {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantite());
            productRepository.save(product);
        });

        order.setStatut(OrderStatus.CANCELLED);
        return toResponse(orderRepository.save(order));
    }

    // ---- Helpers ----

    private OrderResponse toResponse(Order o) {
        List<OrderItemResponse> items = o.getItems() != null
                ? o.getItems().stream().map(this::toItemResponse).collect(Collectors.toList())
                : List.of();
        return OrderResponse.builder()
                .id(o.getId())
                .numeroCommande(o.getNumeroCommande())
                .statut(o.getStatut())
                .sousTotal(o.getSousTotal())
                .fraisLivraison(o.getFraisLivraison())
                .totalTTC(o.getTotalTTC())
                .dateCommande(o.getDateCommande())
                .adresseLivraison(o.getAdresseLivraison())
                .items(items)
                .customerName(o.getCustomer().getPrenom() + " " + o.getCustomer().getNom())
                .isNew(o.isNew())
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItem i) {
        String variantDetails = i.getVariant() != null
                ? i.getVariant().getAttribut() + ": " + i.getVariant().getValeur() : null;
        BigDecimal subtotal = i.getPrixUnitaire().multiply(BigDecimal.valueOf(i.getQuantite()));
        return OrderItemResponse.builder()
                .id(i.getId())
                .productId(i.getProduct().getId())
                .productName(i.getProduct().getNom())
                .variantId(i.getVariant() != null ? i.getVariant().getId() : null)
                .variantDetails(variantDetails)
                .quantite(i.getQuantite())
                .prixUnitaire(i.getPrixUnitaire())
                .sousTotal(subtotal)
                .build();
    }

    private String generateOrderNumber() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        String random = String.format("%05d", new Random().nextInt(99999));
        return "ORD-" + year + "-" + random;
    }

    private String formatAddress(Address a) {
        return a.getRue() + ", " + a.getVille() + " " + a.getCodePostal() + ", " + a.getPays();
    }

    private void validateOrderAccess(Order order) {
        User current = getCurrentUser();
        if (current.getRole() != Role.ADMIN && !order.getCustomer().getId().equals(current.getId())) {
            throw new UnauthorizedException("Access denied");
        }
    }

    private void validateOrderOwnership(Order order) {
        User current = getCurrentUser();
        if (!order.getCustomer().getId().equals(current.getId())) {
            throw new UnauthorizedException("Not your order");
        }
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Authenticated user not found"));
    }
}