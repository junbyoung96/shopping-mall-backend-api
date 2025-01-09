package com.allra.shop_backend.order.payload;

public record PaymentApiResponse(String status, String transactionId, String message) {
}
