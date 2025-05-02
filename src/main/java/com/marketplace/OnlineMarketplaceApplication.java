package com.marketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class OnlineMarketplaceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnlineMarketplaceApplication.class, args);
    }
} 