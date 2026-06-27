package com.ordersystem.order_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank(message = "Username es obligatorio")
    private String username;
    @NotBlank(message = "Password es obligatorio")
    private String password;
}