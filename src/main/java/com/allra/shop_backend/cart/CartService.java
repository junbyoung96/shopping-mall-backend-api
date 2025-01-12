package com.allra.shop_backend.cart;

import com.allra.shop_backend.cart.entity.Cart;
import com.allra.shop_backend.cart.entity.CartItem;
import com.allra.shop_backend.cart.payload.CartItemCreateRequest;
import com.allra.shop_backend.cart.repository.CartItemRepository;
import com.allra.shop_backend.cart.repository.CartRepository;
import com.allra.shop_backend.common.exception.OutOfStockException;
import com.allra.shop_backend.common.exception.UnauthorizedAccessException;
import com.allra.shop_backend.product.Product;
import com.allra.shop_backend.product.ProductService;
import com.allra.shop_backend.user.User;
import com.allra.shop_backend.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final UserService userService;

    private final ProductService productService;

    @Transactional
    public CartItem createCartItem(CartItemCreateRequest request) {
        User user = userService.getUser(request.userId());
        Product product = productService.getProduct(request.productId());

        if (product.getStock() < request.quantity()) { //물건의 재고보다 많은 수량이 많은경우,
            throw new OutOfStockException("선택하신 수량이 상품의 재고보다 많아 선택할 수 없습니다.");
        }

        for(CartItem item : user.getCart().getCartItems()){ //기존 장바구니에 등록된 상품을 새로 추가하는경우, 기존 수량 + 새로운 수량
            if(Objects.equals(item.getProduct().getId(), product.getId())){
                item.updateQuantity(item.getQuantity() + request.quantity());
                return item;
            }
        }

        return cartItemRepository.save(new CartItem(user.getCart(), product, request.quantity()));
    }

    @Transactional
    public CartItem updateCartItem(long userId, long id, int updatedQuantity) {
        User user = userService.getUser(userId);
        CartItem cartItem = getCartItem(id);

        if (!Objects.equals(cartItem.getCart().getUser().getId(), user.getId())) {
            throw new UnauthorizedAccessException("리소스에 대한 권한이 없습니다.");
        }
        cartItem.updateQuantity(updatedQuantity);
        return cartItem;
    }

    public void deleteCartItem(long userId, long id) {
        User user = userService.getUser(userId);
        CartItem cartItem = getCartItem(id);

        if (!Objects.equals(cartItem.getCart().getUser().getId(), user.getId())) {
            throw new UnauthorizedAccessException("리소스에 대한 권한이 없습니다.");
        }
        cartItemRepository.delete(cartItem);
    }

    public CartItem getCartItem(long id) {
        return cartItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CartItem with ID " + id + " not found"));
    }

    public Cart getCartByUserId(long userId){
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Cart with UserId " + userId + " not found"));
    }

    public void clearCartItems(long cartId){
        cartItemRepository.deleteAllByCartId(cartId);
    }
}
