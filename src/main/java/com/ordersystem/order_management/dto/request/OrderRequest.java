package com.ordersystem.order_management.dto.request;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    @NotEmpty(message = "El pedido debe tener al menos un ítem")
    @Valid
    private List<OrderItemRequest> items;

    @Data
    public static class OrderItemRequest {
        @NotNull(message = "ID del producto es obligatorio")
        private Long productId;

        @NotNull(message = "Cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser positiva")
        private Integer quantity;
    }
}