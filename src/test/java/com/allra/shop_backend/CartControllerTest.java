package com.allra.shop_backend;

import com.allra.shop_backend.cart.CartService;
import com.allra.shop_backend.cart.entity.CartItem;
import com.allra.shop_backend.cart.payload.CartItemCreateRequest;
import com.allra.shop_backend.cart.payload.CartItemDeleteRequest;
import com.allra.shop_backend.cart.payload.CartItemUpdateRequest;
import com.allra.shop_backend.product.Product;
import com.allra.shop_backend.product.ProductRepository;
import com.allra.shop_backend.user.User;
import com.allra.shop_backend.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CartControllerTest {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    CartService cartService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    Product product1;
    User user;
    CartItem cartItem;

    @BeforeAll
    void setUp() {
        //더미 데이터 생성
        product1 = productRepository.save(new Product("Iphone 15 Pro", 1000000L, 5));
        System.out.println(product1.getId());
        user = userRepository.save(new User("junbyoung"));
    }

    @Test
    @Order(1)
    void addCartItem() throws Exception {
        CartItemCreateRequest requestBody = new CartItemCreateRequest(user.getId(), product1.getId(), product1.getStock());
        // DTO 직렬화
        String jsonContent = objectMapper.writeValueAsString(requestBody);

        cartItem = objectMapper.readValue(
                mockMvc.perform(MockMvcRequestBuilders.post("/api/carts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonContent))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                CartItem.class
        );

        //추가한 수량이 일치하는지 확인
        Assertions.assertEquals(cartItem.getQuantity(), requestBody.quantity());
    }

    @Test
    @Order(2)
    void updateCartItem() throws Exception {
        CartItem item = cartService.getCartItem(cartItem.getId());
        CartItemUpdateRequest requestBody = new CartItemUpdateRequest(4, user.getId());
        String jsonContent = objectMapper.writeValueAsString(requestBody);

        CartItem updatedItem = objectMapper.readValue(
                mockMvc.perform(MockMvcRequestBuilders.patch("/api/carts/{cartItemId}", item.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonContent))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                CartItem.class
        );

        //변경한 수량이 일치하는지 확인
        Assertions.assertEquals(updatedItem.getQuantity(), requestBody.quantity());
    }

    @Test
    @Order(3)
    void deleteCartItem() throws Exception {
        CartItem item = cartService.getCartItem(cartItem.getId());
        CartItemDeleteRequest requestBody = new CartItemDeleteRequest(user.getId());
        String jsonContent = objectMapper.writeValueAsString(requestBody);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/carts/{cartItemId}", item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isNoContent());
    }
}
