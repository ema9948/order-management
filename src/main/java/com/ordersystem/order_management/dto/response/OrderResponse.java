package com.ordersystem.order_management.dto.response;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal totalAmount;
    private UserResponse user;
    private List<OrderItemResponse> items;

    @Data
    public static class OrderItemResponse {
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal priceAtOrder;
    }
}