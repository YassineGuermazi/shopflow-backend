package com.shopflow.main.service;

import com.shopflow.main.dto.user.UserRequest;
import com.shopflow.main.dto.user.UserResponse;
import com.shopflow.main.entity.User;
import com.shopflow.main.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Requirement 2.2: Inscription with validation.
     * Requirement 3.2: Transactional support for business logic.
     */
    @Transactional
    public UserResponse createUser(UserRequest request) {
        // Requirement 2.2: Email uniqueness check
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        User user = User.builder()
                .email(request.getEmail())
                .prenom(request.getFirstName()) // Mapping Request DTO to Entity
                .nom(request.getLastName())
                .motDePasse(request.getPassword()) // Security encoding should be added later [cite: 29]
                .role(request.getRole())
                .actif(true) // Default to active
                .dateCreation(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    /**
     * Requirement 2.2: Fetching user profile data.
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, com.shopflow.main.dto.user.UserUpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        
        if (request.getPrenom() != null) user.setPrenom(request.getPrenom());
        if (request.getNom() != null) user.setNom(request.getNom());
        if (request.getRole() != null) user.setRole(request.getRole());
        if (request.getActif() != null) user.setActif(request.getActif());
        
        return mapToResponse(userRepository.save(user));
    }

    @Transactional
    public void changePassword(String email, com.shopflow.main.dto.user.ChangePasswordRequest request, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getMotDePasse())) {
            throw new RuntimeException("Current password incorrect");
        }
        
        user.setMotDePasse(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * Requirement 3.2: Manual mapping from Entity to Response DTO.
     */
    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .prenom(user.getPrenom())
                .nom(user.getNom())
                .role(user.getRole())
                .actif(user.isActif())
                .dateCreation(user.getDateCreation())
                .build();
    }
}