package com.marketplace.controller;

import com.marketplace.dto.StoreRequest;
import com.marketplace.dto.StoreResponse;
import com.marketplace.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/stores")
@Tag(name = "Store", description = "Store management APIs")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Create a new store")
    public ResponseEntity<StoreResponse> createStore(@Valid @RequestBody StoreRequest request) {
        StoreResponse response = storeService.createStore(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER') and @storeService.isStoreOwner(#id)")
    @Operation(summary = "Update store details")
    public ResponseEntity<StoreResponse> updateStore(
            @PathVariable Long id,
            @Valid @RequestBody StoreRequest request) {
        return ResponseEntity.ok(storeService.updateStore(id, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get store by ID")
    public ResponseEntity<StoreResponse> getStore(@PathVariable Long id) {
        return ResponseEntity.ok(storeService.getStore(id));
    }

    @GetMapping
    @Operation(summary = "List all stores")
    public ResponseEntity<List<StoreResponse>> listStores() {
        return ResponseEntity.ok(storeService.listStores());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER') and @storeService.isStoreOwner(#id)")
    @Operation(summary = "Delete store")
    public ResponseEntity<Void> deleteStore(@PathVariable Long id) {
        storeService.deleteStore(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-store")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Get current user's store")
    public ResponseEntity<StoreResponse> getMyStore() {
        return ResponseEntity.ok(storeService.getMyStore());
    }

    @GetMapping("/all")
    @Operation(summary = "Get all stores")
    public ResponseEntity<List<StoreResponse>> getStores() {
        return ResponseEntity.ok(storeService.getStores());
    }
} 