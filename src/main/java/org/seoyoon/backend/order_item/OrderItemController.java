package org.seoyoon.backend.order_item;

import org.springframework.web.bind.annotation.RestController;


@RestController
public class OrderItemController {

//    private OrderItemRepository orderItemRepository;
//
//    public OrderItemController(OrderItemRepository orderItemRepository) {
//        this.orderItemRepository = orderItemRepository;
//    }

//    @GetMapping("/orderItems")
//    public ResponseEntity<ApiResponse<List<OrderItem>>> getOrderItems(){
//        var authentication = (UsernamePasswordAuthenticationToken)
//                SecurityContextHolder.getContext().getAuthentication();
//
//        Long storeId = (Long) authentication.getDetails();
//        List<OrderItem> orderItems = orderItemRepository.findByStoreId(storeId);
//        if (orderItems.isEmpty()){
//            return ResponseEntity.status(404).body(new ApiResponse<>(false, null, "대상을 찾지 못했습니다.", 404));
//        }
//        return ResponseEntity.ok(new ApiResponse<>(true, orderItems, "조회 완료했습니다.", 200));
//    }

}
