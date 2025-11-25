package org.seoyoon.backend.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDailyDTO {
    private String date;
    private Long totalUsers;
    private Long newUsers;
}