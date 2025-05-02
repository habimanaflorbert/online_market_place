package com.marketplace.service;

import com.marketplace.dto.StoreRequest;
import com.marketplace.dto.StoreResponse;

import java.util.List;

public interface StoreService {
    StoreResponse createStore(StoreRequest request);
    StoreResponse updateStore(Long id, StoreRequest request);
    StoreResponse getStore(Long id);
    List<StoreResponse> listStores();
    void deleteStore(Long id);
    StoreResponse getMyStore();
    boolean isStoreOwner(Long storeId);
    List<StoreResponse> getStores();
} 