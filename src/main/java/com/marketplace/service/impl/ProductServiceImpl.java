package com.marketplace.service.impl;

import com.marketplace.dto.ProductRequest;
import com.marketplace.dto.ProductResponse;
import com.marketplace.entity.*;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.repository.CategoryRepository;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.StoreRepository;
import com.marketplace.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service("productService") 
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));
        
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!storeRepository.existsByIdAndOwner(request.getStoreId(), currentUser)) {
            throw new IllegalStateException("You are not the owner of the store.");
        }
        
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .store(store)
                .category(category)
                .featured(false)
                .build();
        
        product = productRepository.save(product);
        return mapToResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category);
        
        product = productRepository.save(product);
        return mapToResponse(product);
    }

    @Override
    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return mapToResponse(product);
    }

    @Override
    public Page<ProductResponse> listProducts(String search, Long categoryId, Boolean featured, Pageable pageable) {
        Specification<Product> spec = Specification.where(null);
        
        if (search != null && !search.isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                cb.or(
                    cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("description")), "%" + search.toLowerCase() + "%")
                )
            );
        }
        
        if (categoryId != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("category").get("id"), categoryId)
            );
        }
        
        if (featured != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("featured"), featured)
            );
        }
        
        return productRepository.findAll(spec, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ProductResponse markAsFeatured(Long id, boolean featured) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        product.setFeatured(featured);
        product = productRepository.save(product);
        return mapToResponse(product);
    }

    @Override
    public Page<ProductResponse> listProductsByStore(Long storeId, Pageable pageable) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + storeId));
        return productRepository.findByStore(store, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public boolean isProductOwner(Long productId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return productRepository.existsByIdAndStoreOwner(productId, currentUser);
    }

    @Override
    public List<ProductResponse> getProductsByStore(long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));
        
        return productRepository.findByStore(store, Pageable.unpaged()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .featured(product.isFeatured())
                .storeId(product.getStore().getId())
                .storeName(product.getStore().getName())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .averageRating(calculateAverageRating(product))
                .reviewCount(product.getReviews() != null ? product.getReviews().size() : 0)
                .build();
    }

    private Double calculateAverageRating(Product product) {
        if (product.getReviews() == null || product.getReviews().isEmpty()) {
            return 0.0;
        }
        return product.getReviews().stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }
    
} 