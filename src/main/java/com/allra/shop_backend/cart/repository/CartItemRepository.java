package com.allra.shop_backend.cart.repository;

import com.allra.shop_backend.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {
}
