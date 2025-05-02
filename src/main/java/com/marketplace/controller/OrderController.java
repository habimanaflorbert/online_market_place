package com.marketplace.controller;

import com.marketplace.dto.OrderRequest;
import com.marketplace.dto.OrderResponse;
import com.marketplace.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order", description = "Order management APIs")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('SHOPPER')")
    @Operation(summary = "Create a new order")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('SHOPPER')")
    @Operation(summary = "List current user's orders")
    public ResponseEntity<Page<OrderResponse>> listMyOrders(Pageable pageable) {
        return ResponseEntity.ok(orderService.listMyOrders(pageable));
    }

    @GetMapping("/store/{storeId}")
    @PreAuthorize("hasRole('SELLER') and @storeService.isStoreOwner(#storeId)")
    @Operation(summary = "List orders for a store")
    public ResponseEntity<Page<OrderResponse>> listStoreOrders(
            @PathVariable Long storeId,
            Pageable pageable) {
        return ResponseEntity.ok(orderService.listStoreOrders(storeId, pageable));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('SELLER') and @orderService.isOrderStoreSeller(#id)")
    @Operation(summary = "Update order status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('SHOPPER')")
    @Operation(summary = "Get all orders for current user")
    public ResponseEntity<List<OrderResponse>> getOrdersByUser() {
        return ResponseEntity.ok(orderService.getOrdersByUser());
    }

    @GetMapping("/store/{storeId}/all")
    @PreAuthorize("hasRole('SELLER') and @storeService.isStoreOwner(#storeId)")
    @Operation(summary = "Get all orders for a store")
    public ResponseEntity<List<OrderResponse>> getOrdersByStore(@PathVariable long storeId) {
        return ResponseEntity.ok(orderService.getOrdersByStore(storeId));
    }
} 