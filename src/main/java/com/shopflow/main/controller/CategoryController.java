package com.shopflow.main.controller;

import com.shopflow.main.dto.category.*;
import com.shopflow.main.service.CategoryService;
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
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category tree management")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get full category tree")
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        return ResponseEntity.ok(categoryService.getCategoryTree());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    @Operation(summary = "Create a category", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    @Operation(summary = "Update a category", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id,
                                                           @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    @Operation(summary = "Delete a category", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
