package com.allra.shop_backend.cart.payload;

import jakarta.validation.constraints.Positive;

public record CartItemCreateRequest(@Positive long userId, @Positive long productId, @Positive int quantity) {
}
