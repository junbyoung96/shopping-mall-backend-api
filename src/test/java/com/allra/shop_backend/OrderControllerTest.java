package com.allra.shop_backend;

import com.allra.shop_backend.cart.CartService;
import com.allra.shop_backend.cart.entity.CartItem;
import com.allra.shop_backend.cart.payload.CartItemCreateRequest;
import com.allra.shop_backend.cart.repository.CartItemRepository;
import com.allra.shop_backend.order.enums.OrderStatus;
import com.allra.shop_backend.order.payload.OrderRequest;
import com.allra.shop_backend.order.payload.OrderResponse;
import com.allra.shop_backend.order.payload.PaymentApiResponse;
import com.allra.shop_backend.order.repository.OrderRepository;
import com.allra.shop_backend.order.service.OrderService;
import com.allra.shop_backend.order.service.PaymentService;
import com.allra.shop_backend.product.Product;
import com.allra.shop_backend.product.ProductRepository;
import com.allra.shop_backend.user.User;
import com.allra.shop_backend.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderControllerTest {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CartService cartService;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    PaymentService paymentService;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;


    Product product1;
    Product product2;
    User user;
    CartItem cart1;
    CartItem cart2;
    OrderResponse order;

    @BeforeAll
    void setUp() {
        //더미 데이터 생성
        product1 = productRepository.save(new Product("Iphone 15 Pro", 1000000L, 5));
        product2 = productRepository.save(new Product("MacBook Air 16GB 256GB", 2500000L, 5));
        user = userRepository.save(new User("junbyoung"));
        cart1 = cartService.createCartItem(new CartItemCreateRequest(user.getId(), product1.getId(), 1));
        cart2 = cartService.createCartItem(new CartItemCreateRequest(user.getId(), product2.getId(), 1));
    }

    @Test
    @Order(1)
    void createOrder() throws Exception {
        // Mock PaymentService 동작 정의
        when(paymentService.processPayment(any(com.allra.shop_backend.order.entity.Order.class)))
                .thenReturn(new PaymentApiResponse("SUCCESS", "txn_661039264694246", "Payment processed successfully"));

        OrderRequest requestBody = new OrderRequest(user.getId());
        //DTO 직렬화
        String jsonContent = objectMapper.writeValueAsString(requestBody);

        order = objectMapper.readValue(
                mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonContent))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                OrderResponse.class
        );

        Assertions.assertEquals(product1.getPrice() + product2.getPrice(), order.totalPayment());
        Assertions.assertEquals(OrderStatus.PAID, order.status());

        //주문 완료후 카트가 비워졌는지 확인
        Assertions.assertEquals(0, cartItemRepository.count());
    }

    @Test
    @Order(2)
    void cancelOrder() throws Exception {
        OrderRequest requestBody = new OrderRequest(user.getId());
        String jsonContent = objectMapper.writeValueAsString(requestBody);

        order = objectMapper.readValue(
                mockMvc.perform(MockMvcRequestBuilders.patch("/api/orders/{orderId}/cancel", order.id())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonContent))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                OrderResponse.class
        );

        Assertions.assertEquals(OrderStatus.CANCELED, order.status());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public PaymentService paymentService() {
            //외부결제 모듈 Mocking
            return Mockito.mock(PaymentService.class);
        }
    }
}
