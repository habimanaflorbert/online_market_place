package com.marketplace.service.impl;

import com.marketplace.dto.UserChangePassword;
import com.marketplace.dto.UserRequest;
import com.marketplace.dto.UserResponse;
import com.marketplace.entity.Role;
import com.marketplace.entity.User;
import com.marketplace.repository.UserRepository;
import com.marketplace.service.UserService;
import com.marketplace.exception.ResourceNotFoundException;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public UserResponse updateUserRole(Long id, Role role) {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("UserServiceImpl.updateUserRole()"+role);
        System.out.println();
        System.out.println();

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        user.setRole(role);
        user = userRepository.save(user);
        return mapToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!request.getEmail().equals(user.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new EntityExistsException("Email already exists");
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        // if (request.getPassword() != null && !request.getPassword().isEmpty()) {
        //     user.setPassword(passwordEncoder.encode(request.getPassword()));
        // }

        user = userRepository.save(user);
        return mapToResponse(user);
    }

    @Override
    public User getCurrentUser() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            log.debug("Getting current user with email: {}", email);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            log.debug("Current user found: {}", user.getEmail());
            return user;
        } catch (Exception e) {
            log.error("Error getting current user: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public UserResponse getProfile(Long id) {
        try {
            log.debug("Getting profile for user ID: {}", id);
            User user = userRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("User not found with ID: {}", id);
                        return new ResourceNotFoundException("User not found");
                    });
            log.debug("Found user: {}", user.getEmail());
            return mapToResponse(user);
        } catch (Exception e) {
            log.error("Error getting profile: {}", e.getMessage());
            throw e;
        }
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .build();
    }

    @Override
    public String updatePassword(Long id, UserChangePassword request) {
        User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.getCurrentPassword() != null && !request.getCurrentPassword().isEmpty()) {

            System.out.println(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()));
            // System.out.println(passwordEncoder.encode(request.getCurrentPassword()));

            if (passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
              if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user = userRepository.save(user);
            return "Password Updated";
          }
            }
            throw new EntityExistsException("Incorrect Password");
        }
        return "Incorrect Password";
    }
} 