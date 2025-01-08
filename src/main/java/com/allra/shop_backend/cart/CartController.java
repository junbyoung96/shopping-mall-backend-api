package com.allra.shop_backend.cart;

import com.allra.shop_backend.cart.entity.CartItem;
import com.allra.shop_backend.cart.payload.CartItemCreateRequest;
import com.allra.shop_backend.cart.payload.CartItemDeleteRequest;
import com.allra.shop_backend.cart.payload.CartItemUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/carts")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping
    public ResponseEntity<CartItem> createCartItem(@Valid @RequestBody CartItemCreateRequest cartItemCreateRequest) {
        CartItem cartItem = cartService.createCartItem(cartItemCreateRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/carts/{cartItemId}")
                .buildAndExpand(cartItem.getId()).toUri();
        return ResponseEntity.created(location).body(cartItem);
    }

    @PatchMapping("/{cartItemId}")
    public ResponseEntity<CartItem> updateCartItem(@PathVariable Long cartItemId, @RequestBody CartItemUpdateRequest request) {
        CartItem cartItem = cartService.updateCartItem(cartItemId, request.getQuantity());
        return ResponseEntity.ok(cartItem);
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long cartItemId, @Valid @RequestBody CartItemDeleteRequest request) {
        cartService.deleteCartItem(request.getUserId(), cartItemId);
        return ResponseEntity.noContent().build();
    }
}
