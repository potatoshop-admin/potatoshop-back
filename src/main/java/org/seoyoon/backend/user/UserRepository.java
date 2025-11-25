package org.seoyoon.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByStoreId(Long storeId);

    Optional<User> findByLogInId(String loginId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.storeId = :storeId")
    Long countByStore(@Param("storeId") Long storeId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.storeId = :storeId AND u.createdAt <= :date")
    Long countTotalUsersUntil(@Param("storeId") Long storeId,
                              @Param("date") LocalDateTime date);

    @Query("SELECT COUNT(u) FROM User u WHERE u.storeId = :storeId AND u.createdAt BETWEEN :start AND :end")
    Long countNewUsersByDay(@Param("storeId") Long storeId,
                            @Param("start") LocalDateTime start,
                            @Param("end") LocalDateTime end);
}
