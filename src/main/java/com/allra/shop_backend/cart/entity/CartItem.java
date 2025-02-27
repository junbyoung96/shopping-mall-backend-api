package com.allra.shop_backend.cart.entity;

import com.allra.shop_backend.common.exception.OutOfStockException;
import com.allra.shop_backend.product.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "cart_items",
        indexes = {
                @Index(name = "idx_cart_id", columnList = "cart_id")
        })
@EntityListeners(AuditingEntityListener.class)
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    @JsonIgnore
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    @Column(nullable = false)
    @Check(constraints = "quantity >= 0")
    private Integer quantity;

    @CreatedDate
    @Column(nullable = false)
    @ColumnDefault("NOW()")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    public CartItem(Cart cart, Product product, int quantity) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }

    public void updateQuantity(int updatedQuantity){
        if (updatedQuantity < 1) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }

        if(this.product.getStock() < updatedQuantity){
            throw new OutOfStockException("선택하신 수량이 상품의 재고보다 많아 선택할 수 없습니다.");
        }
        this.quantity = updatedQuantity;
    }
}
