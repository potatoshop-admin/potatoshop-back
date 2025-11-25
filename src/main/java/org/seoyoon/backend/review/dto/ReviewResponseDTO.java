package org.seoyoon.backend.review.dto;

import java.time.LocalDateTime;

public record ReviewResponseDTO(
        Long reviewId,
        Long itemId,
        String title,
        Long userId,
        String userName,
        String content,
        Integer rate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}