package org.seoyoon.backend.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.seoyoon.backend.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody User user){
        User newUser = new User();
        newUser.setStoreId(user.getStoreId());
        newUser.setName(user.getName());
        newUser.setLogInId(user.getLogInId());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setEmail(user.getEmail());
        if (user.getAge() != null) {
            newUser.setAge(user.getAge());
        }
        if (user.getBirthday() != null) {
            newUser.setBirthday(user.getBirthday());
        }
        newUser.setGrade(GradeType.valueOf("BASIC"));
        User saved = userRepository.save(newUser);
        return ResponseEntity.ok(new ApiResponse<>(true, saved, "유저 가입이 완료되었습니다.", 201));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getUsers() {
        var authentication = (UsernamePasswordAuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication();

        Long storeId = (Long) authentication.getDetails();
        List<User> users = userRepository.findByStoreId(storeId);

        return ResponseEntity.ok(new ApiResponse<>(true, users, "유저 조회를 완료헀습니다.", 200));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable Long id) {
        var result = userRepository.findById(id);
        if (result.isPresent()) {
            var user = result.get();
            return ResponseEntity.ok(new ApiResponse<>(true, user, "유저 조회를 완료헀습니다.", 200));
        }else  {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, null, "대상을 찾지 못했습니다.", 404));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id , @RequestBody Map<String, Object> updates) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();

        if (updates.containsKey("name")) {
            user.setName((String) updates.get("name"));
        }
        if (updates.containsKey("password")) {
            String password = updates.get("password").toString();
            user.setPassword(passwordEncoder.encode(password));
        }
        if (updates.containsKey("email")) {
            user.setEmail((String) updates.get("email"));
        }
        if (updates.containsKey("age")) {
            user.setAge((Integer) updates.get("age"));
        }
        if(updates.containsKey("birthday")) {
            user.setBirthday((String) updates.get("birthday"));
        }
        if (updates.containsKey("grade")) {
            user.setGrade(GradeType.valueOf((String) updates.get("grade")));
        }
        System.out.println("user" + user);
        User saved = userRepository.save(user);
        return ResponseEntity.ok(new ApiResponse<>(true, saved,"성공적으로 업데이트 되었습니다.", 201));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if(!userRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
