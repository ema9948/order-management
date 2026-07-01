package com.ordersystem.order_management.service;

import com.ordersystem.order_management.dto.request.OrderRequest;
import com.ordersystem.order_management.dto.response.OrderResponse;
import com.ordersystem.order_management.dto.response.UserResponse;
import com.ordersystem.order_management.model.entity.Order;
import com.ordersystem.order_management.model.entity.OrderItem;
import com.ordersystem.order_management.model.entity.Product;
import com.ordersystem.order_management.model.entity.User;
import com.ordersystem.order_management.repository.OrderRepository;
import com.ordersystem.order_management.repository.ProductRepository;
import com.ordersystem.order_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");

        BigDecimal total = BigDecimal.ZERO;

        for (OrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + itemRequest.getProductId()));

            if (product.getStock() < itemRequest.getQuantity()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + product.getName() +
                        ". Disponible: " + product.getStock() + ", solicitado: " + itemRequest.getQuantity());
            }

            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product);

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            item.setPriceAtOrder(product.getPrice());

            order.getItems().add(item);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
        }

        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

    public List<OrderResponse> getCurrentUserOrders() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return orderRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + id));

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!order.getUser().getId().equals(user.getId()) && !userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new RuntimeException("No tienes permiso para ver este pedido");
        }

        return mapToResponse(order);
    }

    @Transactional
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + id));

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!order.getUser().getId().equals(user.getId()) && !userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new RuntimeException("No tienes permiso para cancelar este pedido");
        }

        if (!order.getStatus().equals("PENDING")) {
            throw new RuntimeException("Solo se pueden cancelar pedidos en estado PENDING");
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }


    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderDate(order.getOrderDate());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());

        UserResponse userResponse = new UserResponse();
        userResponse.setId(order.getUser().getId());
        userResponse.setUsername(order.getUser().getUsername());
        userResponse.setEmail(order.getUser().getEmail());
        userResponse.setRoles(order.getUser().getRoles());
        response.setUser(userResponse);

        List<OrderResponse.OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> {
                    OrderResponse.OrderItemResponse itemResponse = new OrderResponse.OrderItemResponse();
                    itemResponse.setProductId(item.getProduct().getId());
                    itemResponse.setProductName(item.getProduct().getName());
                    itemResponse.setQuantity(item.getQuantity());
                    itemResponse.setPriceAtOrder(item.getPriceAtOrder());
                    return itemResponse;
                })
                .collect(Collectors.toList());
        response.setItems(itemResponses);

        return response;
    }
}