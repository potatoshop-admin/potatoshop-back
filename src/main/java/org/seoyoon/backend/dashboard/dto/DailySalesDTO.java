package org.seoyoon.backend.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailySalesDTO {
    private LocalDate date;
    private Long count;

    public DailySalesDTO(Object date, Long count) {
        this.date = ((java.sql.Date) date).toLocalDate(); // DATE() 함수 대응
        this.count = count;
    }
}