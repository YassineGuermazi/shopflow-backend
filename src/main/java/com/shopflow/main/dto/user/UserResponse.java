package com.shopflow.main.dto.user;

import com.shopflow.main.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String prenom;
    private String nom;
    private Role role;
    private boolean actif;
    private LocalDateTime dateCreation;
}