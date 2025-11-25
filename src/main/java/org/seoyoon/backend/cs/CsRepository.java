package org.seoyoon.backend.cs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CsRepository extends JpaRepository<Cs, Long> {
    List<Cs> findByStoreId(Long storeId);

    // 전체 CS 건수
    @Query("SELECT COUNT(c) FROM Cs c WHERE c.storeId = :storeId")
    Long countByStoreId(@Param("storeId") Long storeId);

    // 답변 완료된 CS 건수
    @Query("SELECT COUNT(c) FROM Cs c WHERE c.storeId = :storeId AND c.csStatus = org.seoyoon.backend.cs.CsStatusType.ANSWERED")
    Long countAnswered(@Param("storeId") Long storeId);
}
