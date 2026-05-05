package com.shopflow.main.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Entity
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Address {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String rue;
    private String ville;
    private String codePostal;
    private String pays;
    private boolean principal;
}