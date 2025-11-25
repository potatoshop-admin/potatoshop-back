package org.seoyoon.backend.dashboard;

import lombok.RequiredArgsConstructor;
import org.seoyoon.backend.common.dto.ApiResponse;
import org.seoyoon.backend.cs.CsRepository;
import org.seoyoon.backend.dashboard.dto.CategorySalesDTO;
import org.seoyoon.backend.dashboard.dto.DailyCategorySalesDTO;
import org.seoyoon.backend.dashboard.dto.DashboardDTO;
import org.seoyoon.backend.dashboard.dto.UserDailyDTO;
import org.seoyoon.backend.item.ItemRepository;
import org.seoyoon.backend.order_item.OrderItemRepository;
import org.seoyoon.backend.orders.OrderStatusType;
import org.seoyoon.backend.orders.OrdersRepository;
import org.seoyoon.backend.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    final public OrdersRepository ordersRepository;
    final public UserRepository userRepository;
    final public OrderItemRepository orderItemRepository;
    final public CsRepository csRepository;
    final public ItemRepository itemRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardDTO>> getDashboard() {
        var authentication = (UsernamePasswordAuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication();

        Long storeId = (Long) authentication.getDetails();

        DashboardDTO dto = new DashboardDTO();

        // 매출 금액
        dto.setMonthlySalesAmount(ordersRepository.getMonthlySales(storeId));

        // 전체 고객 수
        dto.setTotalCustomers(userRepository.countByStore(storeId));

        // 주문 수
        dto.setTotalOrders(ordersRepository.countByStore(storeId));

        // 상태별 주문
        dto.setPendingDelivery(ordersRepository.countPendingDelivery(storeId));
        dto.setShippingCount(ordersRepository.countByStatus(storeId, OrderStatusType.SHIPPING));
        dto.setDeliveredCount(ordersRepository.countByStatus(storeId, OrderStatusType.DELIVERED));

        // CS
        dto.setTotalCsCount(csRepository.countByStoreId(storeId));
        dto.setAnsweredCsCount(csRepository.countAnswered(storeId));

        // 최근 1달 주문
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime startOfDay = today.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        dto.setCategoryDailySale(orderItemRepository.findCategoryDailySales(storeId, startOfDay, endOfDay));

        List<String> categories = itemRepository.findDistinctCategories(storeId);

        List<DailyCategorySalesDTO> result = new ArrayList<>();
        List<UserDailyDTO> userStats = new ArrayList<>();
        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i).toLocalDate();
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = start.plusDays(1);

            List<CategorySalesDTO> daily = orderItemRepository.findCategorySalesByDay(storeId, start, end);

            // 누락 카테고리 채우기
            List<CategorySalesDTO> filled = categories.stream()
                    .map(category -> daily.stream()
                            .filter(d -> d.getCategory().equals(category))
                            .findFirst()
                            .orElse(new CategorySalesDTO(category, 0L))
                    ).toList();

            result.add(new DailyCategorySalesDTO(date.toString(), filled));

            Long totalUsers = userRepository.countTotalUsersUntil(storeId, end);
            Long newUsers = userRepository.countNewUsersByDay(storeId, start, end);

            userStats.add(new UserDailyDTO(date.toString(), totalUsers, newUsers));
        }

        dto.setDailyCategorySales(result);
        dto.setUserDailyStatus(userStats);

        return ResponseEntity.ok(new ApiResponse<>(true, dto, "대시보드 통계 조회 완료", 200));
    }
}
