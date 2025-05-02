package com.marketplace.service;

import com.marketplace.dto.OrderRequest;
import com.marketplace.dto.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    OrderResponse getOrder(Long id);
    Page<OrderResponse> listMyOrders(Pageable pageable);
    Page<OrderResponse> listStoreOrders(Long storeId, Pageable pageable);
    OrderResponse updateOrderStatus(Long id, String status);
    boolean isOrderStoreSeller(Long orderId);
    List<OrderResponse> getOrdersByUser();
    List<OrderResponse> getOrdersByStore(long storeId);
} 