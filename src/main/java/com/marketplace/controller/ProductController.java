package com.marketplace.controller;

import com.marketplace.dto.ProductRequest;
import com.marketplace.dto.ProductResponse;
import com.marketplace.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product", description = "Product management APIs")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Create a new product")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER') and @productService.isProductOwner(#id)")
    @Operation(summary = "Update product details")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
                System.out.println();
                System.out.println(productService.isProductOwner(id));
                System.out.println();
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @GetMapping
    @Operation(summary = "List all products with pagination and filtering")
    public ResponseEntity<Page<ProductResponse>> listProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean featured,
            Pageable pageable) {
        return ResponseEntity.ok(productService.listProducts(search, categoryId, featured, pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER') and @productService.isProductOwner(#id)")
    @Operation(summary = "Delete product")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/featured")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mark product as featured")
    public ResponseEntity<ProductResponse> markAsFeatured(
            @PathVariable Long id,
            @RequestParam boolean featured) {
        return ResponseEntity.ok(productService.markAsFeatured(id, featured));
    }

    @GetMapping("/store/{storeId}")
    @Operation(summary = "List products by store")
    public ResponseEntity<Page<ProductResponse>> listProductsByStore(
            @PathVariable Long storeId,
            Pageable pageable) {
        return ResponseEntity.ok(productService.listProductsByStore(storeId, pageable));
    }

    @GetMapping("/store/{storeId}/all")
    @Operation(summary = "Get all products by store")
    public ResponseEntity<List<ProductResponse>> getProductsByStore(@PathVariable long storeId) {
        return ResponseEntity.ok(productService.getProductsByStore(storeId));
    }
} 