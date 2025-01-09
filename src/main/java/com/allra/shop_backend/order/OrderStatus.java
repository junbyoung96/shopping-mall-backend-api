package com.allra.shop_backend.order;

public enum OrderStatus {
    PAID,          // 결제 완료
    SHIPPED,       // 배송 시작
    DELIVERED,     // 배송 완료
    CANCELED       // 주문 취소
}
