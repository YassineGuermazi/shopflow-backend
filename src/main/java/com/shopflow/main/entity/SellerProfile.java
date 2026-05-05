package com.shopflow.main.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shopflow.main.listener.FirebaseSyncListener;

import jakarta.persistence.*;
import lombok.*;

@Entity
@EntityListeners(FirebaseSyncListener.class)
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SellerProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String nomBoutique;
    private String description;
    private String logo;
    private Double note;
}