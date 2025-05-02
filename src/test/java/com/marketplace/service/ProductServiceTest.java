package com.marketplace.service;

import com.marketplace.dto.ProductRequest;
import com.marketplace.dto.ProductResponse;
import com.marketplace.entity.*;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.repository.CategoryRepository;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.StoreRepository;
import com.marketplace.repository.UserRepository;
import com.marketplace.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private ProductServiceImpl productService;

    private User testUser;
    private Store testStore;
    private ProductRequest productRequest;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.SELLER);

        testStore = new Store();
        testStore.setId(1L);
        testStore.setName("Test Store");
        testStore.setOwner(testUser);

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Test Category");

        productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setDescription("Test Description");
        productRequest.setPrice(new BigDecimal("10.00"));
        productRequest.setStock(100L);
        productRequest.setStoreId(1L);
        productRequest.setCategoryId(1L);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
    }

    @Test
    void createProduct_ShouldCreateProductSuccessfully() {
        // Given
        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(testStore));
        when(storeRepository.existsByIdAndOwner(anyLong(), any(User.class))).thenReturn(true);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId(1L);
            return product;
        });

        // When
        ProductResponse response = productService.createProduct(productRequest);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Product", response.getName());
        assertEquals(new BigDecimal("10.00"), response.getPrice());
        assertEquals(100L, response.getStock());
        
        verify(storeRepository, times(1)).findById(anyLong());
        verify(storeRepository, times(1)).existsByIdAndOwner(anyLong(), any(User.class));
        verify(categoryRepository, times(1)).findById(anyLong());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void createProduct_ShouldThrowExceptionWhenStoreNotFound() {
        // Given
        when(storeRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(productRequest));
        verify(storeRepository, times(1)).findById(anyLong());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void createProduct_ShouldThrowExceptionWhenUserNotStoreOwner() {
        // Given
        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(testStore));
        when(storeRepository.existsByIdAndOwner(anyLong(), any(User.class))).thenReturn(false);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));

        // When/Then
        assertThrows(IllegalStateException.class, () -> productService.createProduct(productRequest));
        verify(storeRepository, times(1)).findById(anyLong());
        verify(storeRepository, times(1)).existsByIdAndOwner(anyLong(), any(User.class));
        verify(categoryRepository, times(1)).findById(anyLong());
        verify(productRepository, never()).save(any(Product.class));
    }
} 