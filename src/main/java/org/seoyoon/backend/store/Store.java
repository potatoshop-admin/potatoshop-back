package org.seoyoon.backend.store;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
public class Store {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeId;
    private String storeName;
    @CreationTimestamp
    private LocalDateTime createdTime;
    private Boolean active;

    public String toString(){
        return this.storeId + this.storeName + this.createdTime + this.active.toString();
    }
}
