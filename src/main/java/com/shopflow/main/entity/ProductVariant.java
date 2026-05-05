package com.shopflow.main.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shopflow.main.listener.FirebaseSyncListener;

@Entity
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductVariant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private String attribut;
    private String valeur;
    private Integer stockSupplementaire;
    private BigDecimal prixDelta;
}