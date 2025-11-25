package org.seoyoon.backend.dashboard.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DashboardDTO {

    private Long monthlySalesAmount;
    private Long totalCustomers;
    private Long totalOrders;
    private Long pendingDelivery; // 미배송
    private Long shippingCount; // 배송중
    private Long deliveredCount; // 배송완료
    private Long totalCsCount;
    private Long answeredCsCount;

    private List<CategorySalesDTO> categoryDailySale;
    private List<DailyCategorySalesDTO> dailyCategorySales;
    private List<UserDailyDTO> userDailyStatus;
}
