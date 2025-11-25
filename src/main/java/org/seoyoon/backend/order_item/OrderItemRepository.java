package org.seoyoon.backend.order_item;

import org.seoyoon.backend.dashboard.dto.CategorySalesDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
    SELECT new org.seoyoon.backend.dashboard.dto.CategorySalesDTO(
        i.category,
        SUM(oi.quantity)
    )
    FROM Orders o
    JOIN o.orderItems oi
    JOIN oi.item i
    WHERE o.storeId = :storeId
      AND o.createdAt BETWEEN :startOfDay AND :endOfDay
      AND o.orderStatus IN ('PAID', 'PROCESSING', 'SHIPPING', 'DELIVERED')
    GROUP BY i.category
""")
    List<CategorySalesDTO> findCategoryDailySales(
            @Param("storeId") Long storeId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    @Query("SELECT new org.seoyoon.backend.dashboard.dto.CategorySalesDTO(" +
            "i.category, SUM(oi.quantity)) " +
            "FROM Orders o JOIN o.orderItems oi JOIN oi.item i " +
            "WHERE o.storeId = :storeId " +
            "AND o.createdAt BETWEEN :start AND :end " +
            "AND o.orderStatus IN ('PAID', 'PROCESSING', 'SHIPPING', 'DELIVERED') " +
            "GROUP BY i.category")
    List<CategorySalesDTO> findCategorySalesByDay(
            @Param("storeId") Long storeId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // 특정 아이템의 모든 주문 가져오기
    List<OrderItem> findByItem_ItemId(Long itemId);

    // 특정 아이템의 총 이익 합계
    @Query("SELECT SUM(oi.profitAmount) FROM OrderItem oi WHERE oi.item.itemId = :itemId")
    Long getTotalProfitByItemId(@Param("itemId") Long itemId);

    // 특정 아이템의 평균 이익률 (가중치 반영)
    @Query("SELECT (SUM(oi.profitAmount) * 10000 / SUM(oi.salePrice * oi.quantity)) " +
            "FROM OrderItem oi WHERE oi.item.itemId = :itemId")
    Integer getAverageProfitRateBpsByItemId(@Param("itemId") Long itemId);
}
