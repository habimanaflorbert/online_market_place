package com.marketplace.service;

import com.marketplace.dto.UserChangePassword;
import com.marketplace.dto.UserRequest;
import com.marketplace.dto.UserResponse;
import com.marketplace.entity.Role;
import com.marketplace.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse updateProfile(Long id, UserRequest request);
    String updatePassword(Long id, UserChangePassword request);
    UserResponse getProfile(Long id);
    User getCurrentUser();
    Page<UserResponse> getAllUsers(Pageable pageable);
    void deleteUser(Long id);
    UserResponse updateUserRole(Long id, Role role);
} 