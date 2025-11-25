package org.seoyoon.backend.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class User {
    @Id @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long userId;
    private Long storeId;
    @Column(nullable = false)
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;
    @Column(nullable = false)
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    private String logInId;
    @Column(nullable = false)
    private String email;
    @NotBlank(message = "이메일는 필수 입력 값입니다.")
    @Column(nullable = false)
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;
    @Column(nullable = true)
    private Integer age;
    @Column(nullable = true)
    private String birthday;

    @Enumerated(EnumType.STRING)
    private GradeType grade;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return this.name + this.logInId + this.createdAt;
    }
}