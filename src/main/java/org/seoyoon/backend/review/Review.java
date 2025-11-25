package org.seoyoon.backend.review;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;
    private Long storeId;
    private Long itemId;
    private Long userId;
    private Integer rate;
    @Column(length = 1000)
    private String content;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @CreationTimestamp
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return this.rate.toString() + this.content;
    }
}