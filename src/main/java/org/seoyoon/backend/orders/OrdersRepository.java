package org.seoyoon.backend.orders;


import org.seoyoon.backend.dashboard.dto.DailySalesDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByStoreId(Long storeId);
    List<Orders> findByStoreIdAndOrderStatus(Long storeId, OrderStatusType orderStatus);
    List<Orders> findByUserId(Long userId);

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Orders o " +
            "WHERE o.storeId = :storeId " +
            "AND o.orderStatus = org.seoyoon.backend.orders.OrderStatusType.DELIVERED " +
            "AND MONTH(o.createdAt) = MONTH(CURRENT_DATE) " +
            "AND YEAR(o.createdAt) = YEAR(CURRENT_DATE)")
    Long getMonthlySales(@Param("storeId") Long storeId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.storeId = :storeId")
    Long countByStore(@Param("storeId") Long storeId);

    @Query("SELECT COUNT(o) FROM Orders o " +
            "WHERE o.storeId = :storeId " +
            "AND o.orderStatus IN (" +
            "org.seoyoon.backend.orders.OrderStatusType.PAID, " +
            "org.seoyoon.backend.orders.OrderStatusType.PROCESSING, " +
            "org.seoyoon.backend.orders.OrderStatusType.CANCEL_REQUESTED, " +
            "org.seoyoon.backend.orders.OrderStatusType.EXCHANGE_REQUESTED, " +
            "org.seoyoon.backend.orders.OrderStatusType.RETURN_REQUESTED)")
    Long countPendingDelivery(@Param("storeId") Long storeId);

    @Query("SELECT COUNT(o) FROM Orders o WHERE o.storeId = :storeId AND o.orderStatus = :orderStatus")
    Long countByStatus(@Param("storeId") Long storeId,
                          @Param("orderStatus") OrderStatusType orderStatus);


    @Query("SELECT COUNT(o) > 0 FROM Orders o JOIN o.orderItems oi " +
            "WHERE o.userId = :userId AND oi.item.itemId = :itemId")
    boolean existsByUserIdAndItemId(@Param("userId") Long userId,
                                    @Param("itemId") Long itemId);

    @Query("SELECT COUNT(o) > 0 FROM Orders o " +
            "WHERE o.ordersId = :ordersId AND o.userId = :userId")
    boolean existsByOrderIdAndUserId(@Param("ordersId") Long ordersId,
                                     @Param("userId") Long userId);

    @Query("""
    SELECT new org.seoyoon.backend.dashboard.dto.DailySalesDTO(
        DATE(o.createdAt), COUNT(o))
    FROM Orders o
    WHERE o.storeId = :storeId
      AND o.createdAt BETWEEN :start AND :end
    GROUP BY DATE(o.createdAt)
    ORDER BY DATE(o.createdAt)
""")
    List<DailySalesDTO> findDailyOrderCount(
            @Param("storeId") Long storeId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
