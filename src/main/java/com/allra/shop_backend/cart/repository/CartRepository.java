package com.allra.shop_backend.cart.repository;

import com.allra.shop_backend.cart.entity.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Long> {
    @EntityGraph(attributePaths = {"cartItems","cartItems.product"})
    Optional<Cart> findByUserId(long userId);
}
