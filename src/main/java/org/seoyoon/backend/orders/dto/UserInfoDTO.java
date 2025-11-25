package org.seoyoon.backend.orders.dto;

import org.seoyoon.backend.user.GradeType;

public record UserInfoDTO(
        Long userId,
        String name,
        Integer age,
        GradeType grade
) {}