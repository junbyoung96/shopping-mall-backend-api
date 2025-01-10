package com.allra.shop_backend.cart.payload;

import jakarta.validation.constraints.Positive;

public record CartItemUpdateRequest(@Positive int quantity, @Positive long userId) {
}
