package org.seoyoon.backend.admin_user;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class AdminUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminUserId;
    private Long storeId;
    private String name;
    private String logInId;
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RoleType role;

    //    내가 저장할 때 넣어주지 않아도 자동적으로 해당 값을 채워주게 해준다
    @CreationTimestamp
    private LocalDateTime createdAt;

    public String toString() {
        return this.name + this.role + this.logInId + this.createdAt;
    }
}

