package com.shopflow.main.service;

import com.shopflow.main.dto.category.*;
import com.shopflow.main.entity.Category;
import com.shopflow.main.exception.ResourceNotFoundException;
import com.shopflow.main.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getCategoryTree() {
        List<Category> roots = categoryRepository.findByParentIsNull();
        return roots.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setNom(request.getNom());
        category.setDescription(request.getDescription());
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
            category.setParent(parent);
        }
        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        category.setNom(request.getNom());
        category.setDescription(request.getDescription());
        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private CategoryResponse toResponse(Category c) {
        return CategoryResponse.builder()
                .id(c.getId())
                .nom(c.getNom())
                .description(c.getDescription())
                .parentId(c.getParent() != null ? c.getParent().getId() : null)
                .children(c.getChildren() != null
                        ? c.getChildren().stream().map(this::toResponse).collect(Collectors.toList())
                        : List.of())
                .build();
    }
}