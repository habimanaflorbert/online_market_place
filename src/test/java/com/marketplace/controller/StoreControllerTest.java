package com.marketplace.controller;

import com.marketplace.dto.StoreRequest;
import com.marketplace.dto.StoreResponse;
import com.marketplace.entity.Role;
import com.marketplace.entity.User;
import com.marketplace.service.StoreService;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StoreControllerTest {

    @Mock
    private StoreService storeService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private StoreController storeController;

    private StoreRequest storeRequest;
    private StoreResponse storeResponse;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.SELLER);

        storeRequest = new StoreRequest();
        storeRequest.setName("Test Store");
        storeRequest.setDescription("Test Store Description");

        storeResponse = new StoreResponse();
        storeResponse.setId(1L);
        storeResponse.setName("Test Store");
        storeResponse.setDescription("Test Store Description");
        storeResponse.setOwnerEmail("test@example.com");

        // Set up security context
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        
        // Set up request attributes
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void createStore_ShouldCreateStoreSuccessfully() {
        // Given
        when(storeService.createStore(any(StoreRequest.class))).thenReturn(storeResponse);

        // When
        ResponseEntity<StoreResponse> response = storeController.createStore(storeRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Test Store", response.getBody().getName());
        assertEquals("Test Store Description", response.getBody().getDescription());
        assertEquals("test@example.com", response.getBody().getOwnerEmail());
        
        verify(storeService, times(1)).createStore(any(StoreRequest.class));
    }

    @Test
    void getStore_ShouldReturnStoreSuccessfully() {
        // Given
        when(storeService.getStore(anyLong())).thenReturn(storeResponse);

        // When
        ResponseEntity<StoreResponse> response = storeController.getStore(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Test Store", response.getBody().getName());
        assertEquals("Test Store Description", response.getBody().getDescription());
        assertEquals("test@example.com", response.getBody().getOwnerEmail());
        
        verify(storeService, times(1)).getStore(anyLong());
    }

    @Test
    void updateStore_ShouldUpdateStoreSuccessfully() {
        // Given
        when(storeService.updateStore(anyLong(), any(StoreRequest.class))).thenReturn(storeResponse);

        // When
        ResponseEntity<StoreResponse> response = storeController.updateStore(1L, storeRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Test Store", response.getBody().getName());
        assertEquals("Test Store Description", response.getBody().getDescription());
        assertEquals("test@example.com", response.getBody().getOwnerEmail());
        
        verify(storeService, times(1)).updateStore(anyLong(), any(StoreRequest.class));
    }

    @Test
    void deleteStore_ShouldDeleteStoreSuccessfully() {
        // Given
        doNothing().when(storeService).deleteStore(anyLong());

        // When
        ResponseEntity<Void> response = storeController.deleteStore(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        
        verify(storeService, times(1)).deleteStore(anyLong());
    }

    @Test
    void getStores_ShouldReturnStoresSuccessfully() {
        // Given
        List<StoreResponse> stores = Arrays.asList(storeResponse);
        when(storeService.getStores()).thenReturn(stores);

        // When
        ResponseEntity<List<StoreResponse>> response = storeController.getStores();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        assertEquals("Test Store", response.getBody().get(0).getName());
        
        verify(storeService, times(1)).getStores();
    }
} 