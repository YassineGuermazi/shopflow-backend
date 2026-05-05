package com.shopflow.main.dto.user;

import com.shopflow.main.entity.Role;
import lombok.Data;

@Data
public class UserUpdateRequest {
    private String prenom;
    private String nom;
    private Role role;
    private Boolean actif;
}
