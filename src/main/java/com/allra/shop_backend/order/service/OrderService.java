package com.allra.shop_backend.order.service;

import com.allra.shop_backend.cart.entity.CartItem;
import com.allra.shop_backend.order.entity.Order;
import com.allra.shop_backend.order.entity.OrderItem;
import com.allra.shop_backend.order.enums.OrderStatus;
import com.allra.shop_backend.order.payload.PaymentApiResponse;
import com.allra.shop_backend.order.repository.OrderRepository;
import com.allra.shop_backend.product.Product;
import com.allra.shop_backend.product.ProductRepository;
import com.allra.shop_backend.user.User;
import com.allra.shop_backend.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    private final PaymentService paymentService;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Order createOrder(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));

        if (user.getCart() == null || user.getCart().getCartItems().isEmpty()) {
            throw new RuntimeException("장바구니가 비어 있습니다.");
        }

        Order order = new Order(user);
        for (CartItem item : user.getCart().getCartItems()) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new EntityNotFoundException("선택하신 상품을 찾을 수 없습니다."));

            product.updateStock(item.getQuantity());
            order.getOrderItems().add(new OrderItem(order, product, item.getQuantity()));
        }

        order.calculateTotalPayment();
        order = orderRepository.save(order);

        PaymentApiResponse paymentResponse = paymentService.processPayment(order);

        if ("SUCCESS".equals(paymentResponse.status())) {
            order.updateStatus(OrderStatus.PAID);
            user.getCart().clearCartItems();
        }

        return order;
    }

    public List<Order> getOrdersByUserId(long userId){
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
