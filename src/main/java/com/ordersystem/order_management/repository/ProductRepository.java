package com.ordersystem.order_management.repository;
import com.ordersystem.order_management.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}