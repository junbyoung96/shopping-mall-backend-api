package com.allra.shop_backend.cart.payload;

import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class CartItemUpdateRequest {
    @Positive
    private int quantity;
}
