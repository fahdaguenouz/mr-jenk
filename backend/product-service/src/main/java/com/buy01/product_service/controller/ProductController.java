package com.buy01.product_service.controller;

import com.buy01.product_service.dto.ProductRequest;
import com.buy01.product_service.dto.ProductResponse;
import com.buy01.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ==========================================
    // SELLER ENDPOINTS (Requires Token + Role)
    // ==========================================

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal String sellerId) {

        ProductResponse response = productService.createProduct(request, sellerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<ProductResponse>> getMyProducts(
            @AuthenticationPrincipal String sellerId) {

        return ResponseEntity.ok(productService.getProductsBySeller(sellerId));
    }

    // NEW DELETE ENDPOINT
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable String id,
            @AuthenticationPrincipal String sellerId) { // Gets the UUID from the JWT

        productService.deleteProduct(id, sellerId);
        // Return 204 No Content indicating success but no response body
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal String sellerId) { // Gets the UUID from the JWT

        productService.updateProduct(id,request, sellerId);
        // Return 204 No Content indicating success but no response body
        return ResponseEntity.ok().build();
    }
    // ==========================================
    // PUBLIC ENDPOINTS (No Token Required)
    // ==========================================

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
}