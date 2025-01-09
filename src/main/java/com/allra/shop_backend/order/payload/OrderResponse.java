package com.allra.shop_backend.order.payload;

import com.allra.shop_backend.order.OrderStatus;
import com.allra.shop_backend.order.entity.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record OrderResponse(
        long id,
        long totalPayment,
        OrderStatus status,
        List<OrderItemResponse> orderItems,
        LocalDateTime createdAt
) {
    public OrderResponse(Order order) {
        this(
                order.getId(),
                order.getTotalPayment(),
                order.getStatus(),
                order.getOrderItems().stream()
                        .map(item -> new OrderItemResponse(
                                item.getProduct().getName(),
                                item.getProduct().getPrice(),
                                item.getQuantity()))
                        .collect(Collectors.toList()),
                order.getCreatedAt()
        );
    }
}