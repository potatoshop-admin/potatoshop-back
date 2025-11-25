package org.seoyoon.backend.admin_user;

import lombok.RequiredArgsConstructor;
import org.seoyoon.backend.common.dto.ApiResponse;
import org.seoyoon.backend.store.Store;
import org.seoyoon.backend.store.StoreRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/adminUser")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserRepository adminUserRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminUserService adminUserService;

    @PostMapping
    public ResponseEntity<ApiResponse<AdminUser>> createAdminUser(@RequestBody AdminUser adminUser){
        Long storeId = adminUser.getStoreId();
        Optional<Store> storeData = storeRepository.findById(storeId);
        if(!storeData.isEmpty()){
            var existingAdmins = adminUserRepository.findByStoreId(storeId);
            AdminUser newAdminUser = new AdminUser();
            newAdminUser.setName(adminUser.getName());
            String lodInId = adminUser.getLogInId();
            if (adminUserRepository.findByLogInId(lodInId).isPresent()){
                return ResponseEntity.status(409).body(new ApiResponse<>(false, null, "이미 사용하는 아이디 입니다.", 409));
            }
            if (adminUser.getName() == ""){
                return ResponseEntity.status(404).body(new ApiResponse<>(false, null, "이름은 필수입니다.", 404));
            }
            if (adminUser.getLogInId() == ""){
                return ResponseEntity.status(404).body(new ApiResponse<>(false, null, "아이디는 필수입니다.", 404));
            }
            if (adminUser.getPassword() == ""){
                return ResponseEntity.status(404).body(new ApiResponse<>(false, null, "비밀번호는 필수입니다.", 404));
            }
            newAdminUser.setLogInId(adminUser.getLogInId());
            newAdminUser.setPassword(passwordEncoder.encode(adminUser.getPassword()));


            if (existingAdmins.isEmpty()){
                newAdminUser.setRole(RoleType.MASTER);
            } else {
                newAdminUser.setRole(RoleType.STAFF);
            }

            newAdminUser.setStoreId(storeId);
            AdminUser saved = adminUserRepository.save(newAdminUser);
            return ResponseEntity.ok(new ApiResponse<>(true, saved, "회원가입 완료되었습니다.", 201));
        } else {
            throw new IllegalArgumentException("Store with ID " + storeId + " does not exist.");
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminUser>>> getAdminUsers() {
        var authentication = (UsernamePasswordAuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication();

        Long storeId = (Long) authentication.getDetails();
        var result =  adminUserRepository.findByStoreId(storeId);
        return ResponseEntity.ok(new ApiResponse<>(true, result, "유저 조회를 완료했습니다.",200));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminUser>> getAdminUserById(@PathVariable Long id) {
        var data =  adminUserRepository.findById(id);
        if (data.isPresent()) {
            AdminUser result = data.get();
            return ResponseEntity.ok(new ApiResponse<>(true, result, "유저 조회를 완료했습니다.",200));
        }else  {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, null, "유저가 존재하지 않습니다.",404));
        }
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<ApiResponse<AdminUser>> updateRole(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        String roleValue = (String) updates.get("role");
        RoleType role = RoleType.valueOf(roleValue.toUpperCase());
        AdminUser updated = adminUserService.updateAdminUserRole(id, role);

        return ResponseEntity.ok(
                new ApiResponse<>(true, updated, "역할 변경 완료", 200)
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminUser>> updateAdminUser(@PathVariable Long id , @RequestBody Map<String, Object> updates) {
        Optional<AdminUser> optionalStore = adminUserRepository.findById(id);

        if (optionalStore.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        AdminUser adminUser = optionalStore.get();

        if (updates.containsKey("name")) {
            adminUser.setName((String) updates.get("name"));
        }
        if (updates.containsKey("password")) {
            String password = updates.get("password").toString();
            adminUser.setPassword(passwordEncoder.encode(password));
        }

        AdminUser saved = adminUserRepository.save(adminUser);
        return ResponseEntity.ok(new ApiResponse<>(true, saved, "성공적으로 업데이트 되었습니다.", 201));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdminUser(@PathVariable Long id) {
        if (!adminUserRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        adminUserRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}