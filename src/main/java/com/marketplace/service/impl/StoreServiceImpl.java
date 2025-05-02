package com.marketplace.service.impl;

import com.marketplace.dto.StoreRequest;
import com.marketplace.dto.StoreResponse;
import com.marketplace.entity.Store;
import com.marketplace.entity.User;
import com.marketplace.exception.UnauthorizedException;
import com.marketplace.repository.StoreRepository;
import com.marketplace.repository.UserRepository;
import com.marketplace.service.StoreService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public StoreResponse createStore(StoreRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        Store store = Store.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(currentUser)
                .build();
        
        store = storeRepository.save(store);
        return mapToResponse(store);
    }

    @Override
    @Transactional
    public StoreResponse updateStore(Long id, StoreRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Store not found"));
        
        if (!store.getOwner().equals(currentUser)) {
            throw new UnauthorizedException("You are not authorized to update this store");
        }
        
        store.setName(request.getName());
        store.setDescription(request.getDescription());
        
        store = storeRepository.save(store);
        return mapToResponse(store);
    }

    @Override
    public StoreResponse getStore(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Store not found"));
        return mapToResponse(store);
    }

    @Override
    public List<StoreResponse> listStores() {
        return storeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteStore(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Store not found"));

        if (!store.getOwner().equals(currentUser)) {
            throw new UnauthorizedException("You are not authorized to delete this store");
        }

        storeRepository.deleteById(id);
    }

    @Override
    public StoreResponse getMyStore() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Store store = storeRepository.findByOwner(currentUser)
                .orElseThrow(() -> new EntityNotFoundException("Store not found"));
        return mapToResponse(store);
    }

    @Override
    public boolean isStoreOwner(Long storeId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return storeRepository.existsByIdAndOwner(storeId, currentUser);
    }

    @Override
    public List<StoreResponse> getStores() {
        return storeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private StoreResponse mapToResponse(Store store) {
        return StoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .description(store.getDescription())
                .ownerEmail(store.getOwner().getEmail())
                .ownerId(store.getOwner().getId())
                .build();
    }
} 