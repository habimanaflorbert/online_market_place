package com.marketplace.service.impl;

import com.marketplace.dto.OrderItemRequest;
import com.marketplace.dto.OrderItemResponse;
import com.marketplace.dto.OrderRequest;
import com.marketplace.dto.OrderResponse;
import com.marketplace.entity.*;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.repository.OrderRepository;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.StoreRepository;
import com.marketplace.repository.UserRepository;
import com.marketplace.service.EmailService;
import com.marketplace.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        Order order = Order.builder()
                .user(currentUser)
                .store(store)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            if (product.getStock() < itemRequest.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .price(product.getPrice())
                    .subtotal(subtotal)
                    .build();

            orderItems.add(orderItem);

            // Update product stock
            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(totalAmount);
        order.setItems(orderItems);
        order = orderRepository.save(order);

        // Send order confirmation email
        emailService.sendOrderStatusUpdateEmail(currentUser, order.getId().toString(), order.getStatus().name());

        return mapToResponse(order);
    }

    @Override
    public OrderResponse getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return mapToResponse(order);
    }

    @Override
    public Page<OrderResponse> listMyOrders(Pageable pageable) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return orderRepository.findByUser(currentUser, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<OrderResponse> listStoreOrders(Long storeId, Pageable pageable) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));
        return orderRepository.findByStore(store, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        order = orderRepository.save(order);

        // Send order status update email
        emailService.sendOrderStatusUpdateEmail(order.getUser(), order.getId().toString(), newStatus.name());

        return mapToResponse(order);
    }

    @Override
    public boolean isOrderStoreSeller(Long orderId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return order.getStore().getOwner().equals(currentUser);
    }

    @Override
    public List<OrderResponse> getOrdersByUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return orderRepository.findByUser(currentUser, Pageable.unpaged()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getOrdersByStore(long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));
        
        return orderRepository.findByStore(store, Pageable.unpaged()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userEmail(order.getUser().getEmail())
                .userName(order.getUser().getName())
                .storeId(order.getStore().getId())
                .storeName(order.getStore().getName())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(order.getItems().stream()
                        .map(this::mapToItemResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    private OrderItemResponse mapToItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .subtotal(item.getSubtotal())
                .build();
    }
} 