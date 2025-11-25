package org.seoyoon.backend.admin_user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    List<AdminUser> findByStoreId(Long storeId);

    Optional<AdminUser> findByLogInId(String loginId);

    Optional<AdminUser> findFirstByStoreIdAndRole(Long storeId, RoleType role);
}
