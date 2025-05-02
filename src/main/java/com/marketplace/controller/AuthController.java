package com.marketplace.controller;

import com.marketplace.dto.*;
import com.marketplace.security.JwtUtil;
import com.marketplace.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication APIs")
@Slf4j
public class AuthController {

    @Autowired
    private AuthService authService;

    // @Autowired
    // private JwtUtil jwtUtil;

    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account and sends a verification email"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(
        summary = "Login user",
        description = "Authenticates a user and returns a JWT token"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid credentials"),
        @ApiResponse(responseCode = "401", description = "Email not verified")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/verify/{token}")
    @Operation(
        summary = "Verify email",
        description = "Verifies a user's email address using the verification token"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email verified successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid verification token")
    })
    public ResponseEntity<Void> verifyEmail(
        @Parameter(description = "Verification token received via email")
        @PathVariable String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok().build();
    }

    // @GetMapping("/profile")
    // @Operation(summary = "Get user profile")
    // public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
    //     try {
    //         log.debug("Getting profile with token: {}", token);
    //         if (token == null || !token.startsWith("Bearer ")) {
    //             log.error("Invalid token format");
    //             return ResponseEntity.badRequest().body("Invalid token format");
    //         }
            
    //         String jwt = token.substring(7);
    //         String email = jwtUtil.extractUsername(jwt);
            
    //         if (email == null) {
    //             log.error("Could not extract email from token");
    //             return ResponseEntity.badRequest().body("Invalid token");
    //         }
            
    //         log.debug("Extracted email from token: {}", email);
    //         UserProfile profile = authService.getProfile(email);
    //         return ResponseEntity.ok(profile);
    //     } catch (Exception e) {
    //         log.error("Error getting profile: {}", e.getMessage());
    //         return ResponseEntity.badRequest().body("Error getting profile: " + e.getMessage());
    //     }
    // }

    // @PutMapping("/profile")
    // @Operation(summary = "Update user profile")
    // public ResponseEntity<?> updateProfile(
    //         @RequestHeader("Authorization") String token,
    //         @Valid @RequestBody UserProfile profile) {
    //     try {
    //         if (token == null || !token.startsWith("Bearer ")) {
    //             return ResponseEntity.badRequest().body("Invalid token format");
    //         }
            
    //         String jwt = token.substring(7);
    //         String email = jwtUtil.extractUsername(jwt);
            
    //         if (email == null) {
    //             return ResponseEntity.badRequest().body("Invalid token");
    //         }
            
    //         UserProfile updatedProfile = authService.updateProfile(email, profile);
    //         return ResponseEntity.ok(updatedProfile);
    //     } catch (Exception e) {
    //         return ResponseEntity.badRequest().body("Error updating profile: " + e.getMessage());
    //     }
    // }
} 