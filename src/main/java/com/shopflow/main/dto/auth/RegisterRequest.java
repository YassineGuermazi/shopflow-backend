package com.shopflow.main.dto.auth;

import com.shopflow.main.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data 
public class RegisterRequest {
    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 8, message = "Password must be at least 8 characters")
    private String motDePasse;

    @NotBlank private String prenom;
    @NotBlank private String nom;

    private Role role; // CUSTOMER or SELLER (par defaut customer)

    // Seller-only fields
    private String nomBoutique;
    private String descriptionBoutique;
    private String logo;
}