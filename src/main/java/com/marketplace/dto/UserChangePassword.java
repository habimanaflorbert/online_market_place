package com.marketplace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserChangePassword {
    @NotBlank(message = "Current Password is required")
    @Size(min = 8, message = "Current Password must be at least 8 characters")
    private String currentPassword;
    
    @NotBlank(message = "New Password is required")
    @Size(min = 8, message = "New Password must be at least 8 characters")
    private String newPassword;
    
}
