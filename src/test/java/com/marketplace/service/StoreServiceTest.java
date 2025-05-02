package com.marketplace.service;

import com.marketplace.dto.StoreRequest;
import com.marketplace.dto.StoreResponse;
import com.marketplace.entity.Role;
import com.marketplace.entity.Store;
import com.marketplace.entity.User;
import com.marketplace.exception.UnauthorizedException;
import com.marketplace.repository.StoreRepository;
import com.marketplace.repository.UserRepository;
import com.marketplace.service.impl.StoreServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private StoreServiceImpl storeService;

    private User testUser;
    private Store testStore;
    private StoreRequest storeRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password")
                .role(Role.SELLER)
                .build();

        testStore = Store.builder()
                .id(1L)
                .name("Test Store")
                .description("Test Description")
                .owner(testUser)
                .build();

        storeRequest = new StoreRequest();
        storeRequest.setName("Test Store");
        storeRequest.setDescription("Test Description");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("test@example.com");
    }

    @Test
    void createStore_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(storeRepository.save(any(Store.class))).thenReturn(testStore);

        StoreResponse response = storeService.createStore(storeRequest);

        assertNotNull(response);
        assertEquals(testStore.getName(), response.getName());
        assertEquals(testStore.getDescription(), response.getDescription());
        assertEquals(testStore.getOwner().getEmail(), response.getOwnerEmail());

        verify(storeRepository).save(any(Store.class));
    }

    @Test
    void createStore_UserNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> storeService.createStore(storeRequest));

        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    void getStore_Success() {
        when(storeRepository.findById(1L)).thenReturn(Optional.of(testStore));

        StoreResponse response = storeService.getStore(1L);

        assertNotNull(response);
        assertEquals(testStore.getName(), response.getName());
        assertEquals(testStore.getDescription(), response.getDescription());
        assertEquals(testStore.getOwner().getEmail(), response.getOwnerEmail());
    }

    @Test
    void getStore_NotFound() {
        when(storeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> storeService.getStore(1L));
    }

    @Test
    void listStores_Success() {
        List<Store> stores = Arrays.asList(testStore);
        when(storeRepository.findAll()).thenReturn(stores);

        List<StoreResponse> responses = storeService.listStores();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testStore.getName(), responses.get(0).getName());
        assertEquals(testStore.getDescription(), responses.get(0).getDescription());
        assertEquals(testStore.getOwner().getEmail(), responses.get(0).getOwnerEmail());
    }

    @Test
    void updateStore_Success() {
        when(storeRepository.findById(1L)).thenReturn(Optional.of(testStore));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(storeRepository.save(any(Store.class))).thenReturn(testStore);

        StoreResponse response = storeService.updateStore(1L, storeRequest);

        assertNotNull(response);
        assertEquals(testStore.getName(), response.getName());
        assertEquals(testStore.getDescription(), response.getDescription());
        assertEquals(testStore.getOwner().getEmail(), response.getOwnerEmail());

        verify(storeRepository).save(any(Store.class));
    }

    @Test
    void updateStore_NotFound() {
        when(storeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> storeService.updateStore(1L, storeRequest));

        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    void updateStore_Unauthorized() {
        User differentUser = User.builder()
                .id(2L)
                .email("different@example.com")
                .password("password")
                .role(Role.SELLER)
                .build();

        Store storeWithDifferentOwner = Store.builder()
                .id(1L)
                .name("Test Store")
                .description("Test Description")
                .owner(differentUser)
                .build();

        when(storeRepository.findById(1L)).thenReturn(Optional.of(storeWithDifferentOwner));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        assertThrows(UnauthorizedException.class, () -> storeService.updateStore(1L, storeRequest));

        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    void deleteStore_Success() {
        when(storeRepository.findById(1L)).thenReturn(Optional.of(testStore));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        assertDoesNotThrow(() -> storeService.deleteStore(1L));

        verify(storeRepository).deleteById(1L);
    }

    @Test
    void deleteStore_NotFound() {
        when(storeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> storeService.deleteStore(1L));

        verify(storeRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteStore_Unauthorized() {
        User differentUser = User.builder()
                .id(2L)
                .email("different@example.com")
                .password("password")
                .role(Role.SELLER)
                .build();

        Store storeWithDifferentOwner = Store.builder()
                .id(1L)
                .name("Test Store")
                .description("Test Description")
                .owner(differentUser)
                .build();

        when(storeRepository.findById(1L)).thenReturn(Optional.of(storeWithDifferentOwner));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        assertThrows(UnauthorizedException.class, () -> storeService.deleteStore(1L));

        verify(storeRepository, never()).deleteById(anyLong());
    }
} 