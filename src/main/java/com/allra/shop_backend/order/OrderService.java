package com.allra.shop_backend.order;

import com.allra.shop_backend.cart.entity.CartItem;
import com.allra.shop_backend.common.exception.OutOfStockException;
import com.allra.shop_backend.order.entity.Order;
import com.allra.shop_backend.order.entity.OrderItem;
import com.allra.shop_backend.order.payload.PaymentApiRequest;
import com.allra.shop_backend.order.payload.PaymentApiResponse;
import com.allra.shop_backend.order.repository.OrderRepository;
import com.allra.shop_backend.product.Product;
import com.allra.shop_backend.product.ProductRepository;
import com.allra.shop_backend.user.User;
import com.allra.shop_backend.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WebClient webClient;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Order createOrder(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));

        if (user.getCart().getCartItems().isEmpty()) {
            throw new RuntimeException("장바구니가 비어 있습니다.");
        }

        Order order = new Order(user);
        for (CartItem item : user.getCart().getCartItems()) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new EntityNotFoundException("선택하신 상품을 찾을 수 없습니다."));

            if (product.getStock() < item.getQuantity()) {
                throw new OutOfStockException("선택하신 수량이 상품의 재고보다 많아 선택할 수 없습니다.");
            }

            product.updateStock(product.getStock() - item.getQuantity());
            order.getOrderItems().add(new OrderItem(order, product, item.getQuantity()));
        }

        order.calculateTotalPayment();
        order = orderRepository.save(order);

        try {
            //외부 결제 API 요청
            PaymentApiResponse response = webClient
                    .post()
                    .uri("/api/v1/payment")
                    .bodyValue(new PaymentApiRequest(String.valueOf(order.getId()), order.getTotalPayment()))
                    .retrieve()
                    .bodyToMono(PaymentApiResponse.class)
                    .block();

            if (response != null && "SUCCESS".equals(response.status())) {
                // 주문 상태 변경
                order.updateStatus(OrderStatus.PAID);
                // 장바구니 비우기
                user.getCart().clearCartItems();
            }
        } catch (WebClientException ex) {
            throw new RuntimeException("결제에 실패하였습니다.");
        }

        return order;
    }
}
