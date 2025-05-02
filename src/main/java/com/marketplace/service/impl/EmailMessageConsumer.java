package com.marketplace.service.impl;

import com.marketplace.dto.EmailMessage;
import com.marketplace.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailMessageConsumer {
    
    private final JavaMailSender mailSender;
    private final MessageConverter messageConverter;
    
    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void processEmailMessage(EmailMessage message) {
        try {
            log.debug("Processing email message for: {}", message.getTo());
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(message.getTo());
            mailMessage.setSubject(message.getSubject());
            mailMessage.setText(message.getText());
            
            mailSender.send(mailMessage);
            log.info("Email sent successfully to: {}", message.getTo());
        } catch (Exception e) {
            log.error("Failed to process email message for: {}. Error: {}", message.getTo(), e.getMessage(), e);
            throw new RuntimeException("Failed to send email. Message will be requeued.", e);
        }
    }
} 