package com.shopflow.main.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shopflow.main.listener.FirebaseSyncListener;

import jakarta.persistence.*;
import lombok.*;

@Entity
@EntityListeners(FirebaseSyncListener.class)
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CartItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    private Integer quantite;
}