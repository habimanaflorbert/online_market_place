package com.marketplace.service.impl;

import com.marketplace.entity.User;
import com.marketplace.service.EmailService;
import com.marketplace.dto.EmailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final EmailMessageProducer emailMessageProducer;

    @Override
    public void sendVerificationEmail(User user) {
        try {
            log.debug("Preparing verification email for: {}", user.getEmail());
            String emailText = String.format(
                "Hello %s,\n\n" +
                "Thank you for registering with our marketplace. Please click the link below to verify your email address:\n\n" +
                "http://localhost:8080/api/auth/verify/%s\n\n" +
                "If you did not register for our service, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Marketplace Team",
                user.getName(),
                user.getVerificationToken()
            );

            EmailMessage message = EmailMessage.builder()
                .to(user.getEmail())
                .subject("Verify your email address")
                .text(emailText)
                .name(user.getName())
                .verificationToken(user.getVerificationToken())
                .build();

            emailMessageProducer.sendEmailMessage(message);
            log.info("Verification email queued for: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to queue verification email for: {}. Error: {}", user.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to queue verification email. Please try again later.");
        }
    }

    @Override
    public void sendOrderStatusUpdateEmail(User user, String orderId, String status) {
        try {
            log.debug("Preparing order status update email for: {}", user.getEmail());
            String emailText = String.format(
                "Hello %s,\n\n" +
                "The status of your order #%s has been updated to: %s\n\n" +
                "Best regards,\n" +
                "Marketplace Team",
                user.getName(),
                orderId,
                status
            );

            EmailMessage message = EmailMessage.builder()
                .to(user.getEmail())
                .subject("Order Status Update")
                .text(emailText)
                .name(user.getName())
                .build();

            emailMessageProducer.sendEmailMessage(message);
            log.info("Order status update email queued for: {} (order: {})", user.getEmail(), orderId);
        } catch (Exception e) {
            log.error("Failed to queue order status update email for: {}. Error: {}", user.getEmail(), e.getMessage(), e);
        }
    }
} 