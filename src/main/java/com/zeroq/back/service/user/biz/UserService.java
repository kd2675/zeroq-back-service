package com.zeroq.back.service.user.biz;

import auth.common.core.client.UserServiceClient;
import auth.common.core.dto.UserDto;
import com.zeroq.back.common.exception.LiveSpaceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * User Service
 * - auth-back-server의 User API를 호출하여 사용자 정보 관리
 * - UserServiceClient (Feign)를 통해 인증 서버와 통신
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserServiceClient userServiceClient;

    /**
     * ID로 사용자 조회
     */
    public UserDto getUserById(Long userId) {
        try {
            return userServiceClient.getUserById(userId);
        } catch (Exception e) {
            log.error("Failed to get user by id: {}", userId, e);
            throw new LiveSpaceException.ResourceNotFoundException("User", "id", userId);
        }
    }

    /**
     * Username으로 사용자 조회
     */
    public UserDto getUserByUsername(String username) {
        try {
            return userServiceClient.getUserByUsername(username);
        } catch (Exception e) {
            log.error("Failed to get user by username: {}", username, e);
            throw new LiveSpaceException.ResourceNotFoundException("User", "username", username);
        }
    }

    /**
     * Email로 사용자 조회
     */
    public UserDto getUserByEmail(String email) {
        try {
            return userServiceClient.getUserByEmail(email);
        } catch (Exception e) {
            log.error("Failed to get user by email: {}", email, e);
            throw new LiveSpaceException.ResourceNotFoundException("User", "email", email);
        }
    }

    /**
     * 사용자 존재 여부 확인
     */
    public boolean existsById(Long userId) {
        try {
            return userServiceClient.existsById(userId);
        } catch (Exception e) {
            log.error("Failed to check user existence: {}", userId, e);
            return false;
        }
    }
}
