package com.shopflow.main.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.shopflow.main.listener.FirebaseSyncListener;

@Entity
@Table(name = "orders")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private User customer;

    @Enumerated(EnumType.STRING)
    private OrderStatus statut;

    private String numeroCommande;
    private String adresseLivraison;
    private BigDecimal sousTotal;
    private BigDecimal fraisLivraison;
    private BigDecimal totalTTC;
    private LocalDateTime dateCommande;
    private boolean isNew;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;
}