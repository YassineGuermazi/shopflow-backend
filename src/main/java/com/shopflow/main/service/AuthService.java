package com.shopflow.main.service;

import com.shopflow.main.dto.auth.*;
import com.shopflow.main.entity.Role;
import com.shopflow.main.entity.SellerProfile;
import com.shopflow.main.entity.User;
import com.shopflow.main.exception.BusinessException;
import com.shopflow.main.repository.SellerProfileRepository;
import com.shopflow.main.repository.UserRepository;
import com.shopflow.main.security.JwtUtil;
import com.shopflow.main.security.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenStore refreshTokenStore;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already in use: " + request.getEmail());
        }

        Role role = request.getRole() != null ? request.getRole() : Role.CUSTOMER;

        User user = User.builder()
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .prenom(request.getPrenom())
                .nom(request.getNom())
                .role(role)
                .actif(true)
                .dateCreation(LocalDateTime.now())
                .build();

        userRepository.save(user);

        if (role == Role.SELLER) {
            if (request.getNomBoutique() == null) {
                throw new BusinessException("nomBoutique is required for SELLER registration");
            }
            SellerProfile profile = SellerProfile.builder()
                    .user(user)
                    .nomBoutique(request.getNomBoutique())
                    .description(request.getDescriptionBoutique())
                    .logo(request.getLogo())
                    .build();
            sellerProfileRepository.save(profile);
        }

        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        refreshTokenStore.store(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("User not found"));

        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        refreshTokenStore.store(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        String token = request.getRefreshToken();
        if (!refreshTokenStore.isValid(token) || !jwtUtil.validateToken(token)) {
            throw new BusinessException("Invalid or expired refresh token");
        }
        String email = jwtUtil.extractEmail(token);
        String newAccessToken = jwtUtil.generateAccessToken(email);
        String newRefreshToken = jwtUtil.generateRefreshToken(email);
        refreshTokenStore.invalidate(token);
        refreshTokenStore.store(newRefreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .email(email)
                .role(user.getRole().name())
                .build();
    }

    public void logout(RefreshTokenRequest request) {
        refreshTokenStore.invalidate(request.getRefreshToken());
    }
}