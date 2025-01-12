package com.allra.shop_backend.order.repository;

import com.allra.shop_backend.order.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = {"orderItems", "orderItems.product"})
    List<Order> findByUserIdOrderByCreatedAtDesc(long userId);

    @EntityGraph(attributePaths = {"orderItems", "orderItems.product"})
    Optional<Order> findById(long id);
}
