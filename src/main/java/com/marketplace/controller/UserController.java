package com.marketplace.controller;

import com.marketplace.dto.UserChangePassword;
import com.marketplace.dto.UserRequest;
import com.marketplace.dto.UserResponse;
import com.marketplace.entity.Role;
import com.marketplace.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User management APIs")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users (Admin only)")
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Operation(summary = "Get user by ID (Admin or self)")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getProfile(id));
    }

    @PutMapping("/profile")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update user profile")
    public ResponseEntity<UserResponse> updateProfile(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateProfile(
                userService.getCurrentUser().getId(), request));
    }

    @GetMapping("/profile")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user profile")
    public ResponseEntity<UserResponse> getProfile() {
        try {
            log.debug("Getting profile for current user");
            Long userId = userService.getCurrentUser().getId();
            log.debug("Current user ID: {}", userId);
            UserResponse profile = userService.getProfile(userId);
            log.debug("Profile retrieved successfully");
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            log.error("Error getting profile: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Operation(summary = "Delete user (Admin or self)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/role")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user role (Admin only)")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable Long id,
            @RequestParam Role role) {
        return ResponseEntity.ok(userService.updateUserRole(id, role));
    }

    @PutMapping("/change-password")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update user profile")
    public ResponseEntity<ResponseEntity<String>> updatePassword(@Valid @RequestBody UserChangePassword request) {
        System.out.println("isPass");
         ResponseEntity<String> isPass=ResponseEntity.ok(userService.updatePassword(
                userService.getCurrentUser().getId(), request));
            return ResponseEntity.ok(isPass);
         

    }
} 