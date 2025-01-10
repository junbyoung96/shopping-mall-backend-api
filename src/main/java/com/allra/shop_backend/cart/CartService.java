package com.allra.shop_backend.cart;

import com.allra.shop_backend.cart.entity.CartItem;
import com.allra.shop_backend.cart.payload.CartItemCreateRequest;
import com.allra.shop_backend.cart.repository.CartItemRepository;
import com.allra.shop_backend.cart.repository.CartRepository;
import com.allra.shop_backend.common.exception.OutOfStockException;
import com.allra.shop_backend.common.exception.UnauthorizedAccessException;
import com.allra.shop_backend.product.Product;
import com.allra.shop_backend.product.ProductRepository;
import com.allra.shop_backend.user.User;
import com.allra.shop_backend.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    public CartItem createCartItem(CartItemCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        if (product.getStock() < request.quantity()) { //물건의 재고보다 많은 수량이 많은경우,
            throw new OutOfStockException("선택하신 수량이 상품의 재고보다 많아 선택할 수 없습니다.");
        }

        return cartItemRepository.save(new CartItem(user.getCart(), product, request.quantity()));
    }

    @Transactional
    public CartItem updateCartItem(long id, int updatedQuantity) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CartItem with ID " + id + " not found"));

        if (cartItem.getProduct().getStock() < updatedQuantity) { //남은 재고보다 많은 수량으로 변경할시,
            throw new OutOfStockException("선택하신 수량이 상품의 재고보다 많아 선택할 수 없습니다.");
        }

        cartItem.updateQuantity(updatedQuantity);

        return cartItem;
    }

    public void deleteCartItem(long userId, long id) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CartItem with ID " + id + " not found"));

        if (cartItem.getCart().getUser().getId() != userId) {
            throw new UnauthorizedAccessException("리소스에 대한 권한이 없습니다.");
        }
        cartItemRepository.delete(cartItem);
    }
}
