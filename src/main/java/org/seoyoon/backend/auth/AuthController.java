package org.seoyoon.backend.auth;

import lombok.RequiredArgsConstructor;
import org.seoyoon.backend.admin_user.AdminUser;
import org.seoyoon.backend.admin_user.AdminUserRepository;
import org.seoyoon.backend.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody Map<String, String> loginData) {
        String logInId = loginData.get("logInId");
        String password = loginData.get("password");

        Optional<AdminUser> userOptional = adminUserRepository.findByLogInId(logInId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse<>(false, null,"존재하지 않는 유저입니다.", 401)
            );
        }

        AdminUser user = userOptional.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse<>(false, null, "비밀번호가 일치하지 않습니다.", 401)
            );
        }

        // 토큰 발급
        String token = jwtUtil.generateToken(user.getLogInId(), user.getStoreId(), user.getName(), user.getRole());

        // Header에 Authorization 추가
        return ResponseEntity.ok()
                .header("Authorization", token)
                .body(new ApiResponse<>(true, null, "로그인 성공",200));
    }
}