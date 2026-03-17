package com.buy01.product_service.controller;

import com.buy01.product_service.models.Category;
import com.buy01.product_service.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    // Admin endpoint to seed categories if needed
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        category.setSlug(category.getName().toLowerCase().replace(" ", "-"));
        return ResponseEntity.ok(categoryRepository.save(category));
    }
}