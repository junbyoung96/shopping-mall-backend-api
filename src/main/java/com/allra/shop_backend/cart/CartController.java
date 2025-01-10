package com.allra.shop_backend.cart;

import com.allra.shop_backend.cart.entity.CartItem;
import com.allra.shop_backend.cart.payload.CartItemCreateRequest;
import com.allra.shop_backend.cart.payload.CartItemDeleteRequest;
import com.allra.shop_backend.cart.payload.CartItemUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;

    /**
     * 장바구니에 상품을 추가합니다.
     * @param request {@link CartItemCreateRequest}
     * @return {@link CartItem}
     */
    @PostMapping
    public ResponseEntity<CartItem> createCartItem(@Valid @RequestBody CartItemCreateRequest request) {
        CartItem cartItem = cartService.createCartItem(request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/carts/{cartItemId}")
                .buildAndExpand(cartItem.getId()).toUri();
        return ResponseEntity.created(location).body(cartItem);
    }

    /**
     * 장바구니에 담긴 상품의 수량을 변경합니다.
     * @param request {@link CartItemUpdateRequest}
     * @return {@link CartItem}
     */
    @PatchMapping("/{cartItemId}")
    public ResponseEntity<CartItem> updateCartItem(@PathVariable Long cartItemId, @RequestBody CartItemUpdateRequest request) {
        CartItem cartItem = cartService.updateCartItem(request.userId(), cartItemId, request.quantity());
        return ResponseEntity.ok(cartItem);
    }

    /**
     * 장바구니에 담긴 상품을 삭제합니다.
     * @param request {@link CartItemDeleteRequest}
     */
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long cartItemId, @Valid @RequestBody CartItemDeleteRequest request) {
        cartService.deleteCartItem(request.userId(), cartItemId);
        return ResponseEntity.noContent().build();
    }
}
