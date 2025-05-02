package com.marketplace.service.impl;

import com.marketplace.dto.EmailMessage;
import com.marketplace.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailMessageProducer {
    
    private final RabbitTemplate rabbitTemplate;
    
    public void sendEmailMessage(EmailMessage message) {
        try {
            log.debug("Sending email message to queue: {}", message.getTo());
            rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_QUEUE, message);
            log.info("Email message sent to queue successfully for: {}", message.getTo());
        } catch (Exception e) {
            log.error("Failed to send email message to queue: {}. Error: {}", message.getTo(), e.getMessage(), e);
            throw new RuntimeException("Failed to queue email message. Please try again later.");
        }
    }
} 