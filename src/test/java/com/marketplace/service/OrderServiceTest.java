package com.marketplace.service;

import com.marketplace.dto.OrderItemRequest;
import com.marketplace.dto.OrderRequest;
import com.marketplace.dto.OrderResponse;
import com.marketplace.entity.*;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.repository.*;
import com.marketplace.service.impl.OrderServiceImpl;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User testUser;
    private Product testProduct;
    private Store testStore;
    private OrderRequest orderRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.SHOPPER);

        testStore = new Store();
        testStore.setId(1L);
        testStore.setName("Test Store");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(new BigDecimal("10.00"));
        testProduct.setStock(10L);
        testProduct.setStore(testStore);

        List<OrderItemRequest> orderItems = new ArrayList<>();
        OrderItemRequest orderItem = new OrderItemRequest();
        orderItem.setProductId(1L);
        orderItem.setQuantity(2);
        orderItems.add(orderItem);

        orderRequest = new OrderRequest();
        orderRequest.setItems(orderItems);
        orderRequest.setStoreId(1L);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
    }

    @Test
    void createOrder_ShouldCreateOrderSuccessfully() {
        // Given
        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(testStore));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        // When
        OrderResponse response = orderService.createOrder(orderRequest);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1, response.getItems().size());
        assertEquals(new BigDecimal("20.00"), response.getTotalAmount());
        
        verify(storeRepository, times(1)).findById(anyLong());
        verify(productRepository, times(1)).findById(anyLong());
        verify(productRepository, times(1)).save(any(Product.class));
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(emailService, times(1)).sendOrderStatusUpdateEmail(eq(testUser), anyString(), anyString());
    }

    @Test
    void createOrder_ShouldThrowExceptionWhenStoreNotFound() {
        // Given
        when(storeRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(orderRequest));
        verify(storeRepository, times(1)).findById(anyLong());
        verify(productRepository, never()).findById(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_ShouldThrowExceptionWhenProductNotFound() {
        // Given
        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(testStore));
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(orderRequest));
        verify(storeRepository, times(1)).findById(anyLong());
        verify(productRepository, times(1)).findById(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_ShouldThrowExceptionWhenInsufficientStock() {
        // Given
        testProduct.setStock(1L);
        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(testStore));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(orderRequest));
        verify(storeRepository, times(1)).findById(anyLong());
        verify(productRepository, times(1)).findById(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
    }
} 