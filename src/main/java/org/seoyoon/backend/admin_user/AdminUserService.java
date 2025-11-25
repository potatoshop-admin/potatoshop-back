package org.seoyoon.backend.admin_user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final AdminUserRepository adminUserRepository;

//    @Transactional
//    public AdminUser updateAdminUserRole(Long adminUserId, RoleType newRole) {
//
//        AdminUser adminUser = adminUserRepository.findById(adminUserId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // MANAGER로 변경할 때만 검증
//        if (newRole == RoleType.MANAGER) {
//
//            Long storeId = adminUser.getStoreId();
//
//            Optional<AdminUser> currentManager = adminUserRepository
//                    .findFirstByStoreIdAndRole(storeId, RoleType.MANAGER);
//
//            // 기존 매니저가 있고, 대상 유저가 아닐 때
//            if (currentManager != null &&
//                    !currentManager.get().getAdminUserId().equals(adminUserId)) {
//
//                currentManager.get().setRole(RoleType.STAFF);
//                adminUserRepository.save(currentManager.get());
//            }
//        }
//
//        adminUser.setRole(newRole);
//        return adminUserRepository.save(adminUser);
//    }
    @Transactional
    public AdminUser updateAdminUserRole(Long adminUserId, RoleType newRole) {

        AdminUser adminUser = adminUserRepository.findById(adminUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // MANAGER로 변경할 때만 기존 매니저 해제 필요
        if (newRole == RoleType.MANAGER) {

            Long storeId = adminUser.getStoreId();

            Optional<AdminUser> currentManager = adminUserRepository
                    .findFirstByStoreIdAndRole(storeId, RoleType.MANAGER);
            System.out.println(currentManager);
            // 기존 매니저가 있고, 대상 유저가 아닐 때만 STAFF로 변경
            currentManager.ifPresent(manager -> {
                System.out.println("111");
                if (!manager.getAdminUserId().equals(adminUserId)) {
                    manager.setRole(RoleType.STAFF);
                    adminUserRepository.save(manager);
                }
            });

        }
        System.out.println("222222222222" + adminUser);
        adminUser.setRole(newRole);
        System.out.println("3333333333333" + adminUser);
        return adminUserRepository.save(adminUser);
    }
}
