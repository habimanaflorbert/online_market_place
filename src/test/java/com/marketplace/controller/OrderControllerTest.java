package com.marketplace.controller;

import com.marketplace.dto.OrderItemRequest;
import com.marketplace.dto.OrderRequest;
import com.marketplace.dto.OrderResponse;
import com.marketplace.entity.Role;
import com.marketplace.entity.User;
import com.marketplace.service.OrderService;
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
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private OrderController orderController;

    private OrderRequest orderRequest;
    private OrderResponse orderResponse;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.SHOPPER);

        OrderItemRequest orderItem = new OrderItemRequest();
        orderItem.setProductId(1L);
        orderItem.setQuantity(2);

        orderRequest = new OrderRequest();
        orderRequest.setItems(Arrays.asList(orderItem));

        orderResponse = new OrderResponse();
        orderResponse.setId(1L);
        orderResponse.setTotalAmount(new BigDecimal("20.00"));
        orderResponse.setStatus("PENDING");
        orderResponse.setUserEmail("test@example.com");

        // Set up security context
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        
        // Set up request attributes
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void createOrder_ShouldCreateOrderSuccessfully() {
        // Given
        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(orderResponse);

        // When
        ResponseEntity<OrderResponse> response = orderController.createOrder(orderRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals(new BigDecimal("20.00"), response.getBody().getTotalAmount());
        assertEquals("PENDING", response.getBody().getStatus());
        assertEquals("test@example.com", response.getBody().getUserEmail());
        
        verify(orderService, times(1)).createOrder(any(OrderRequest.class));
    }

    @Test
    void getOrder_ShouldReturnOrderSuccessfully() {
        // Given
        when(orderService.getOrder(anyLong())).thenReturn(orderResponse);

        // When
        ResponseEntity<OrderResponse> response = orderController.getOrder(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals(new BigDecimal("20.00"), response.getBody().getTotalAmount());
        assertEquals("PENDING", response.getBody().getStatus());
        assertEquals("test@example.com", response.getBody().getUserEmail());
        
        verify(orderService, times(1)).getOrder(anyLong());
    }

    @Test
    void updateOrderStatus_ShouldUpdateOrderStatusSuccessfully() {
        // Given
        when(orderService.updateOrderStatus(anyLong(), anyString())).thenReturn(orderResponse);

        // When
        ResponseEntity<OrderResponse> response = orderController.updateOrderStatus(1L, "PROCESSING");

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals(new BigDecimal("20.00"), response.getBody().getTotalAmount());
        assertEquals("PENDING", response.getBody().getStatus());
        assertEquals("test@example.com", response.getBody().getUserEmail());
        
        verify(orderService, times(1)).updateOrderStatus(anyLong(), anyString());
    }

    @Test
    void getOrdersByUser_ShouldReturnOrdersSuccessfully() {
        // Given
        List<OrderResponse> orders = Arrays.asList(orderResponse);
        when(orderService.getOrdersByUser()).thenReturn(orders);

        // When
        ResponseEntity<List<OrderResponse>> response = orderController.getOrdersByUser();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        assertEquals(new BigDecimal("20.00"), response.getBody().get(0).getTotalAmount());
        
        verify(orderService, times(1)).getOrdersByUser();
    }

    @Test
    void getOrdersByStore_ShouldReturnOrdersSuccessfully() {
        // Given
        List<OrderResponse> orders = Arrays.asList(orderResponse);
        when(orderService.getOrdersByStore(anyLong())).thenReturn(orders);

        // When
        ResponseEntity<List<OrderResponse>> response = orderController.getOrdersByStore(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        assertEquals(new BigDecimal("20.00"), response.getBody().get(0).getTotalAmount());
        
        verify(orderService, times(1)).getOrdersByStore(anyLong());
    }
} 