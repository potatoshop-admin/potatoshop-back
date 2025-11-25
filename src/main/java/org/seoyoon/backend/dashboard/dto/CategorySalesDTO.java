package org.seoyoon.backend.dashboard.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategorySalesDTO {
    private String category;
    private Long totalSales;

    // DTO 생성자 (JPA Constructor Expression 대응!)
    public CategorySalesDTO(String category, Long totalSales) {
        this.category = category;
        this.totalSales = totalSales;
    }

    public String getCategory() {
        return category;
    }

    public Long getTotalSales() {
        return totalSales;
    }
}