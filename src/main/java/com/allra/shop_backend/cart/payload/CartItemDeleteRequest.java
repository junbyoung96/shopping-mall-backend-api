package com.allra.shop_backend.cart.payload;

import jakarta.validation.constraints.Positive;

public record CartItemDeleteRequest(@Positive long userId) {
}
