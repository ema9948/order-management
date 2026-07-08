package com.ordersystem.order_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank(message = "Nombre del producto es obligatorio")
    private String name;

    private String description;

    @NotNull(message = "Precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private BigDecimal price;

    @NotNull(message = "Stock es obligatorio")
    @Positive(message = "El stock debe ser mayor a 0")
    private Integer stock;
}