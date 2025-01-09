package com.allra.shop_backend.order.payload;

public record PaymentApiRequest(String orderId, long amount) {
}
