package org.seoyoon.backend.orders;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrdersService {
    private final OrdersRepository ordersRepository;

    @Transactional
    public Orders updateOrderStatus(Long orderId, OrderStatusType status) {

        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        order.setOrderStatus(status);
        return ordersRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        if (!ordersRepository.existsById(orderId)) {
            throw new RuntimeException("주문이 존재하지 않습니다.");
        }
        ordersRepository.deleteById(orderId);
    }
}
