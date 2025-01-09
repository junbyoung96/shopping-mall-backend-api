package com.allra.shop_backend.order.payload;

public record OrderItemResponse(String name, long price, int quantity) {
}
