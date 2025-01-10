package com.allra.shop_backend.order.repository;

import com.allra.shop_backend.order.entity.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentLogRepository extends JpaRepository<PaymentLog,Long> {
}
