package com.shopflow.main.controller;

import com.shopflow.main.dto.dashboard.*;
import com.shopflow.main.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Analytics and statistics")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Global admin dashboard stats")
    public ResponseEntity<AdminDashboardResponse> getAdminDashboard() {
        return ResponseEntity.ok(dashboardService.getAdminDashboard());
    }

    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Seller dashboard stats")
    public ResponseEntity<SellerDashboardResponse> getSellerDashboard() {
        return ResponseEntity.ok(dashboardService.getSellerDashboard());
    }
}