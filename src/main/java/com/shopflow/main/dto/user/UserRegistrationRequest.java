package com.shopflow.main.dto.user;

import com.shopflow.main.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationRequest {
    @NotBlank @Size(min = 2)
    private String firstName;
    @NotBlank @Size(min = 2)
    private String lastName;
    @Email @NotBlank
    private String email; // Must be unique
    @NotBlank @Size(min = 8)
    private String password; // Strong password
    private Role role; // ADMIN, SELLER, or CUSTOMER [cite: 15]
}