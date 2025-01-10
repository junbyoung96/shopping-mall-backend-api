package com.allra.shop_backend.order;

import com.allra.shop_backend.order.entity.Order;
import com.allra.shop_backend.order.payload.OrderCreateRequest;
import com.allra.shop_backend.order.payload.OrderResponse;
import com.allra.shop_backend.order.service.OrderService;
import com.allra.shop_backend.user.User;
import com.allra.shop_backend.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    /**
     * 장바구니에 담긴 모든 항목을 주문합니다.
     * @param request {@link OrderResponse}
     * @return {@link OrderResponse}
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request){
        Order order = orderService.createOrder(request.userId());
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/orders/{orderId}")
                .buildAndExpand(order.getId()).toUri();
        OrderResponse response = new OrderResponse(order);
        return ResponseEntity.created(location).body(response);
    }

    /**
     * 특정 사용자의 모든 주문내역을 조회합니다.
     * @param userId
     * @return {@link OrderResponse}
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders(@RequestParam long userId){
        User user = userService.getUser(userId);
        List<Order> orders = orderService.getOrdersByUserId(user.getId());

        List<OrderResponse> orderResponses = orders.stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(orderResponses);
    }
}