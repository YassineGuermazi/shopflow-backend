package com.shopflow.main.repository;

import com.shopflow.main.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Requirement 113: Used to build the category tree
    List<Category> findByParentIsNull();

    // Requirement 22: Useful for filtering active categories
    List<Category> findByNomContainingIgnoreCase(String query);
}