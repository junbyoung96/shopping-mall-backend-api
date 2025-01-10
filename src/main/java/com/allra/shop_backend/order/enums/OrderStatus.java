package com.allra.shop_backend.order.enums;

public enum OrderStatus {
    PROCESSING,    // 결제 진행중
    PAID,          // 결제 완료
    SHIPPED,       // 배송 시작
    DELIVERED,     // 배송 완료
    CANCELED       // 주문 취소
}
