package com.allra.shop_backend.order.payload;

import jakarta.validation.constraints.Positive;

public record OrderCreateRequest(@Positive Long userId) {
}