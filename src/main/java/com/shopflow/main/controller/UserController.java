package com.shopflow.main.controller;

import com.shopflow.main.dto.user.UserRequest;
import com.shopflow.main.dto.user.UserResponse;
import com.shopflow.main.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor // Required for clean constructor injection [cite: 59]
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    /**
     * Requirement 2.2: Inscription with validation (@Valid)[cite: 28, 67].
     * Requirement 3.4: Endpoint for registration[cite: 89].
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest request) {
        return new ResponseEntity<>(userService.createUser(request), HttpStatus.CREATED);
    }

    /**
     * Requirement 3.4: Endpoint to list users (Typically for ADMIN)[cite: 14].
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody com.shopflow.main.dto.user.UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(userService.getCurrentUser(auth.getName()));
    }

    @PostMapping("/me/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody com.shopflow.main.dto.user.ChangePasswordRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        userService.changePassword(auth.getName(), request, passwordEncoder);
        return ResponseEntity.ok().build();
    }
}