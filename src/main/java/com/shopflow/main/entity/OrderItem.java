package com.shopflow.main.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shopflow.main.listener.FirebaseSyncListener;

@Entity
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    private Integer quantite;
    private BigDecimal prixUnitaire;
}
