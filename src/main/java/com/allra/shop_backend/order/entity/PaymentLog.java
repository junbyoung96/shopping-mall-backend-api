package com.allra.shop_backend.order.entity;

import com.allra.shop_backend.order.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "payment_log")
@EntityListeners(AuditingEntityListener.class)
public class PaymentLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long userId;

    private String transactionId;

    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private Long totalPayment = 0L;

    @CreatedDate
    @Column(nullable = false)
    @ColumnDefault("NOW()")
    private LocalDateTime createdAt;

    public PaymentLog(Long orderId, Long userId, Long totalPayment) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalPayment = totalPayment;
    }

    public void updateStatus(String transactionId, String message, PaymentStatus status) {
        this.transactionId = transactionId;
        this.message = message;
        this.status = status;

    }
}
