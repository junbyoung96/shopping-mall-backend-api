package com.allra.shop_backend.cart.payload;

import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class CartItemCreateRequest {
    @Positive
    private long userId;
    @Positive
    private long productId;
    @Positive
    private int quantity;
}
