package org.seoyoon.backend.orders;

import lombok.RequiredArgsConstructor;
import org.seoyoon.backend.common.dto.ApiResponse;
import org.seoyoon.backend.item.Item;
import org.seoyoon.backend.item.ItemRepository;
import org.seoyoon.backend.order_item.OrderItem;
import org.seoyoon.backend.orders.dto.*;
import org.seoyoon.backend.user.User;
import org.seoyoon.backend.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersRepository ordersRepository;
    private final ItemRepository itemRepository;
    private final OrdersService ordersService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<Orders>> createOrder(@RequestBody CreateOrderRequest req) {

        Orders order = new Orders();
        order.setStoreId(req.storeId());
        order.setUserId(req.userId());
        order.setAddress(req.address());
        order.setOrderStatus(OrderStatusType.PAID);

        int total = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CreateOrderItemRequest itemReq : req.items()) {

            Optional<Item> itemOpt = itemRepository.findById(itemReq.itemId());
            if (itemOpt.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(new ApiResponse<>(false, null, "상품 ID " + itemReq.itemId() + " 없음", 404));
            }

            Item item = itemOpt.get();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrders(order);
            orderItem.setItem(item);
            orderItem.setQuantity(itemReq.quantity());
            orderItem.setSalePrice(item.getSalePrice());
            orderItem.setCostPrice(item.getCostPrice());

            total += item.getSalePrice() * itemReq.quantity();

            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        order.setTotalPrice(total);

        Orders saved = ordersRepository.save(order);

        return ResponseEntity.ok(
                new ApiResponse<>(true, saved, "주문 완료하였습니다.", 201)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrdersResponseDTO> >> getAllOrders( @RequestParam(required = false) String orderStatus) {
        var authentication = (UsernamePasswordAuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication();

        Long storeId = (Long) authentication.getDetails();

        List<Orders> orders;
        if(orderStatus != null){
            OrderStatusType orderStatusType = OrderStatusType.valueOf(orderStatus.toUpperCase());
            orders = ordersRepository.findByStoreIdAndOrderStatus(storeId,  orderStatusType);
        }else {
            orders = ordersRepository.findByStoreId(storeId);
        }

        List<OrdersResponseDTO> dtos = orders.stream()
                .map(this::toDTO)
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(true, dtos, "주문 조회를 완료헀습니다.", 200));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrdersResponseDTO>> getOrder(@PathVariable Long id) {
        if(ordersRepository.findById(id).isPresent()) {
            Orders order = ordersRepository.findById(id).orElseThrow();
            OrdersResponseDTO dto = toDTO(order);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, dto, "주문 조회 성공", 200)
            );
        }else {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, null, "대상을 찾지 못했습니다.", 404));
        }
    }
    @PatchMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrdersResponseDTO>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateOrderStatusRequest request
    ) {
        Orders updatedOrder = ordersService.updateOrderStatus(orderId, request.orderStatus());
        // 🔥 배송완료(DELIVERED) → 재고 차감
        if (request.orderStatus().equals(OrderStatusType.DELIVERED)) {
            updatedOrder.getOrderItems().forEach(orderItem -> {
                Item item = itemRepository.findById(orderItem.getItem().getItemId())
                        .orElseThrow(() -> new RuntimeException("아이템을 찾을 수 없습니다."));

                int newStock = item.getStock() - orderItem.getQuantity();
                if (newStock < 0) newStock = 0;

                item.setStock(newStock);
                itemRepository.save(item);
            });
        }

        System.out.println("updated order-----------------> " + toDTO(updatedOrder));
        return ResponseEntity.ok(
                new ApiResponse<>(true, toDTO(updatedOrder), "주문 상태가 변경되었습니다.", 200)
        );
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long orderId) {
        ordersService.deleteOrder(orderId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, null, "주문이 삭제되었습니다.", 200)
        );
    }

    private OrdersResponseDTO toDTO(Orders order) {

        User user = userRepository.findById(order.getUserId())
                .orElse(null);

        UserInfoDTO userInfo = null;
        if (user != null) {
            userInfo = new UserInfoDTO(
                    user.getUserId(),
                    user.getName(),
                    user.getAge(),
                    user.getGrade()
            );
        }
        return new OrdersResponseDTO(
                order.getOrdersId(),
                userInfo,
                order.getStoreId(),
                order.getAddress(),
                order.getOrderStatus().name(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                order.getOrderItems().stream()
                        .map(oi -> new OrderItemDTO(
                                oi.getOrderItemId(),
                                oi.getItem().getItemId(),
                                oi.getItem().getTitle(),
                                oi.getQuantity(),
                                oi.getSalePrice(),
                                oi.getCostPrice(),
                                oi.getProfitAmount()
                        ))
                        .toList()
        );
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Orders>>> getOrdersByUser(@PathVariable Long userId) {
        List<Orders> orders =  ordersRepository.findByUserId(userId);
        if(orders.isEmpty()){
            return ResponseEntity.status(404).body(new ApiResponse<>(false, null, "대상을 찾지 못했습니다.", 404));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, orders, "주문 조회를 완료헀습니다.", 200));
    }
}
