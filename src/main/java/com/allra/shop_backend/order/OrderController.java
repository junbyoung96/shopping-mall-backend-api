package com.allra.shop_backend.order;

import com.allra.shop_backend.order.entity.Order;
import com.allra.shop_backend.order.payload.OrderCreateRequest;
import com.allra.shop_backend.order.payload.OrderResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

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
}