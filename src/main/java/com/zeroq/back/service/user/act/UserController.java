package com.zeroq.back.service.user.act;

import auth.common.core.dto.UserDto;
import web.common.core.response.base.dto.ResponseDataDTO;
import com.zeroq.back.service.user.biz.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * User Controller
 * - auth-back-server의 User API를 프록시하는 컨트롤러
 * - 사용자 정보 조회 기능 제공
 */
@Slf4j
@RestController
@RequestMapping("/api/zeroq/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * ID로 사용자 조회
     * GET /api/v1/users/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseDataDTO<UserDto> getUserById(@PathVariable Long userId) {
        log.info("Get user by id: userId={}", userId);
        UserDto user = userService.getUserById(userId);
        return ResponseDataDTO.of(user, "사용자 조회 성공");
    }

    /**
     * Username으로 사용자 조회
     * GET /api/v1/users/username/{username}
     */
    @GetMapping("/username/{username}")
    public ResponseDataDTO<UserDto> getUserByUsername(@PathVariable String username) {
        log.info("Get user by username: username={}", username);
        UserDto user = userService.getUserByUsername(username);
        return ResponseDataDTO.of(user, "사용자 조회 성공");
    }

    /**
     * Email로 사용자 조회
     * GET /api/v1/users/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseDataDTO<UserDto> getUserByEmail(@PathVariable String email) {
        log.info("Get user by email: email={}", email);
        UserDto user = userService.getUserByEmail(email);
        return ResponseDataDTO.of(user, "사용자 조회 성공");
    }

    /**
     * 사용자 존재 여부 확인
     * GET /api/v1/users/{userId}/exists
     */
    @GetMapping("/{userId}/exists")
    public ResponseDataDTO<Boolean> existsById(@PathVariable Long userId) {
        log.info("Check user existence: userId={}", userId);
        boolean exists = userService.existsById(userId);
        return ResponseDataDTO.of(exists, "사용자 존재 여부 확인 성공");
    }
}
