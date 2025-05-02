package com.marketplace.controller;

import com.marketplace.dto.ProductRequest;
import com.marketplace.dto.ProductResponse;
import com.marketplace.entity.Role;
import com.marketplace.entity.User;
import com.marketplace.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private ProductController productController;

    private ProductRequest productRequest;
    private ProductResponse productResponse;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.SELLER);

        productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setDescription("Test Description");
        productRequest.setPrice(new BigDecimal("10.00"));
        productRequest.setStock(100L);
        productRequest.setStoreId(1L);

        productResponse = new ProductResponse();
        productResponse.setId(1L);
        productResponse.setName("Test Product");
        productResponse.setDescription("Test Description");
        productResponse.setPrice(new BigDecimal("10.00"));
        productResponse.setStock(100L);
        productResponse.setStoreId(1L);

        // Set up security context
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
    }

    @Test
    void createProduct_ShouldCreateProductSuccessfully() {
        // Given
        when(productService.createProduct(any(ProductRequest.class))).thenReturn(productResponse);

        // When
        ResponseEntity<ProductResponse> response = productController.createProduct(productRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Test Product", response.getBody().getName());
        assertEquals("Test Description", response.getBody().getDescription());
        assertEquals(new BigDecimal("10.00"), response.getBody().getPrice());
        assertEquals(100L, response.getBody().getStock());
        assertEquals(1L, response.getBody().getStoreId());
        
        verify(productService, times(1)).createProduct(any(ProductRequest.class));
    }

    @Test
    void getProduct_ShouldReturnProductSuccessfully() {
        // Given
        when(productService.getProduct(anyLong())).thenReturn(productResponse);

        // When
        ResponseEntity<ProductResponse> response = productController.getProduct(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Test Product", response.getBody().getName());
        assertEquals("Test Description", response.getBody().getDescription());
        assertEquals(new BigDecimal("10.00"), response.getBody().getPrice());
        assertEquals(100L, response.getBody().getStock());
        assertEquals(1L, response.getBody().getStoreId());
        
        verify(productService, times(1)).getProduct(anyLong());
    }

    @Test
    void updateProduct_ShouldUpdateProductSuccessfully() {
        // Given
        when(productService.updateProduct(anyLong(), any(ProductRequest.class))).thenReturn(productResponse);

        // When
        ResponseEntity<ProductResponse> response = productController.updateProduct(1L, productRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Test Product", response.getBody().getName());
        assertEquals("Test Description", response.getBody().getDescription());
        assertEquals(new BigDecimal("10.00"), response.getBody().getPrice());
        assertEquals(100L, response.getBody().getStock());
        assertEquals(1L, response.getBody().getStoreId());
        
        verify(productService, times(1)).updateProduct(anyLong(), any(ProductRequest.class));
    }

    @Test
    void deleteProduct_ShouldDeleteProductSuccessfully() {
        // Given
        doNothing().when(productService).deleteProduct(anyLong());

        // When
        ResponseEntity<Void> response = productController.deleteProduct(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        
        verify(productService, times(1)).deleteProduct(anyLong());
    }

    @Test
    void getProductsByStore_ShouldReturnProductsSuccessfully() {
        // Given
        List<ProductResponse> products = Arrays.asList(productResponse);
        when(productService.getProductsByStore(anyLong())).thenReturn(products);

        // When
        ResponseEntity<List<ProductResponse>> response = productController.getProductsByStore(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        assertEquals("Test Product", response.getBody().get(0).getName());
        
        verify(productService, times(1)).getProductsByStore(anyLong());
    }
} 