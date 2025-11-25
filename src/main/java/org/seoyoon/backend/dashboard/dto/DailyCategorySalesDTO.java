package org.seoyoon.backend.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyCategorySalesDTO {
    private String date;
    private List<CategorySalesDTO> categories;
}