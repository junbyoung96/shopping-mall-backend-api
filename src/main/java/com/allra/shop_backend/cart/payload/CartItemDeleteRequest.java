package com.allra.shop_backend.cart.payload;

import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class CartItemDeleteRequest {
    @Positive
    private long userId;
}
