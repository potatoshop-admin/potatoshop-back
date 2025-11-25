package org.seoyoon.backend.orders;

public enum OrderStatusType {
        // 결제 완료
        PAID,
        // 상품 준비중
        PROCESSING,
        // 배송 중
        SHIPPING,
        // 배송 완료
        DELIVERED,

        // 취소 요청
        CANCEL_REQUESTED,
        // 취소 완료
        CANCELLED,

        // 교환 요청
        EXCHANGE_REQUESTED,
        // 교환 완료
        EXCHANGED,

        // 반품 요청
        RETURN_REQUESTED,
        // 반품 완료
        RETURNED;
}
