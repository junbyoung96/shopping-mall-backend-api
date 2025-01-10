package com.allra.shop_backend.order.service;

import com.allra.shop_backend.cart.entity.CartItem;
import com.allra.shop_backend.common.exception.OutOfStockException;
import com.allra.shop_backend.order.entity.Order;
import com.allra.shop_backend.order.entity.OrderItem;
import com.allra.shop_backend.order.enums.OrderStatus;
import com.allra.shop_backend.order.payload.PaymentApiResponse;
import com.allra.shop_backend.order.repository.OrderRepository;
import com.allra.shop_backend.product.Product;
import com.allra.shop_backend.user.User;
import com.allra.shop_backend.user.UserService;
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

    private final UserService userService;

    private final PaymentService paymentService;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Order createOrder(long userId) {
        User user = userService.getUser(userId);

        if (user.getCart() == null || user.getCart().getCartItems().isEmpty()) {
            throw new RuntimeException("장바구니가 비어 있습니다.");
        }

        Order order = new Order(user);
        for (CartItem item : user.getCart().getCartItems()) {
            Product product = item.getProduct();
            if (product.getStock() < item.getQuantity()) {
                throw new OutOfStockException("상품의 재고가 부족합니다.");
            }
            product.updateStock(product.getStock() - item.getQuantity());
            order.getOrderItems().add(new OrderItem(order, item.getProduct(), item.getQuantity()));
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

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Order cancelOrder(long orderId, long userId) {
        Order order = getOrder(orderId);

        order.updateStatus(OrderStatus.CANCELED);

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.updateStock(product.getStock() + item.getQuantity());
        }

        //환불 요청 생략//

        return order;
    }

    public Order getOrder(long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order with ID " + id + " not found"));
    }

    public List<Order> getOrdersByUserId(long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
