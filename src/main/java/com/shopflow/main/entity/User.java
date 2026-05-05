package com.shopflow.main.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shopflow.main.listener.FirebaseSyncListener;

@Entity
@Table(name = "users")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    private String prenom;
    private String nom;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean actif = true;
    private LocalDateTime dateCreation;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private SellerProfile sellerProfile;

   
}
