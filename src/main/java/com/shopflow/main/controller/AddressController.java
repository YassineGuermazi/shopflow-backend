package com.shopflow.main.controller;

import com.shopflow.main.dto.address.*;
import com.shopflow.main.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Addresses", description = "User shipping addresses")
@SecurityRequirement(name = "bearerAuth")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    @Operation(summary = "Get my addresses")
    public ResponseEntity<List<AddressResponse>> getAddresses() {
        return ResponseEntity.ok(addressService.getMyAddresses());
    }

    @PostMapping
    @Operation(summary = "Add a new address")
    public ResponseEntity<AddressResponse> addAddress(@Valid @RequestBody AddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.addAddress(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an address")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}
