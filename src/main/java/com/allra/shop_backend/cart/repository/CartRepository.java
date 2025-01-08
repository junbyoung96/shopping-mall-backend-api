package com.allra.shop_backend.cart.repository;

import com.allra.shop_backend.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart,Long> {
}
