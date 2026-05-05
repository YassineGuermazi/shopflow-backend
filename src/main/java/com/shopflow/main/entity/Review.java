package com.shopflow.main.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.shopflow.main.listener.FirebaseSyncListener;

@Entity
@EntityListeners(FirebaseSyncListener.class)
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer note; // 1-5
    private String commentaire;
    private LocalDateTime dateCreation;
    private boolean approuve = false;
}