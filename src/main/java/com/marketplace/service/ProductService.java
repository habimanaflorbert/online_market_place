package com.marketplace.service;

import com.marketplace.dto.ProductRequest;
import com.marketplace.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    ProductResponse getProduct(Long id);
    Page<ProductResponse> listProducts(String search, Long categoryId, Boolean featured, Pageable pageable);
    void deleteProduct(Long id);
    ProductResponse markAsFeatured(Long id, boolean featured);
    Page<ProductResponse> listProductsByStore(Long storeId, Pageable pageable);
    boolean isProductOwner(Long productId);
    List<ProductResponse> getProductsByStore(long storeId);
} 