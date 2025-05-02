package com.marketplace.service;

import com.marketplace.entity.User;

public interface EmailService {
    void sendVerificationEmail(User user);
    void sendOrderStatusUpdateEmail(User user, String orderId, String status);
} 