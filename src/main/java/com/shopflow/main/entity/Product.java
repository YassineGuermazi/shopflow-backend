package com.shopflow.main.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shopflow.main.listener.FirebaseSyncListener;

@Entity
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal prix;
    private BigDecimal prixPromo;
    private Integer stock;
    private boolean actif = true;
    private LocalDateTime dateCreation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    @ElementCollection
    private List<String> images;

    @ManyToMany
    @JoinTable(name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> categories;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> variants;

    @JsonIgnore  //bch maya3mlch loop
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;
}