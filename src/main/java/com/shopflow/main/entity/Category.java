package com.shopflow.main.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shopflow.main.listener.FirebaseSyncListener;

@Entity
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String description;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category> children;
}