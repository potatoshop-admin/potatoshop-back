package org.seoyoon.backend.cs;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Cs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long csId;
    private Long storeId;
    private Long userId;
    private Long ordersId;
    private String question;
    private String answer;

    @Enumerated(EnumType.STRING)
    private CsStatusType csStatus;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return this.question + this.answer;
    }
}

enum CsStatusType{
    WAITING,
    ANSWERED
}