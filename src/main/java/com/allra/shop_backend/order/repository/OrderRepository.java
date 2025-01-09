package com.allra.shop_backend.order.repository;

import com.allra.shop_backend.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
}
