package com.zeroq.back.service.user.act;

import com.zeroq.core.response.base.dto.ResponseDataDTO;
import com.zeroq.back.database.pub.dto.UserDTO;
import com.zeroq.back.database.pub.entity.User;
import com.zeroq.back.service.user.biz.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 현재 사용자 프로필 조회
     * GET /api/v1/users/profile
     */
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDataDTO<UserDTO>> getProfile() {
        log.info("Get user profile");
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getDetails();
        
        User user = userService.getUserById(userId);
        UserDTO dto = convertToDTO(user);
        
        return ResponseEntity.ok(ResponseDataDTO.of(dto, "사용자 프로필 조회 성공"));
    }

    /**
     * 사용자 정보 수정
     * PUT /api/v1/users/profile
     */
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDataDTO<UserDTO>> updateProfile(
            @RequestBody User updatedUser) {
        log.info("Update user profile");
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getDetails();
        
        User user = userService.updateUser(userId, updatedUser);
        UserDTO dto = convertToDTO(user);
        
        return ResponseEntity.ok(ResponseDataDTO.of(dto, "사용자 정보가 수정되었습니다"));
    }

    /**
     * 사용자 계정 삭제 (탈퇴)
     * DELETE /api/v1/users/profile
     */
    @DeleteMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDataDTO<Void>> deleteProfile(
            @RequestParam(required = false) String reason) {
        log.info("Delete user profile");
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getDetails();
        
        userService.deleteUser(userId, reason != null ? reason : "사용자 요청");
        
        return ResponseEntity.ok(ResponseDataDTO.of(null, "계정이 삭제되었습니다"));
    }

    /**
     * 특정 사용자 조회 (Admin만 가능)
     * GET /api/v1/users/{userId}
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDataDTO<UserDTO>> getUserById(@PathVariable Long userId) {
        log.info("Get user by id: userId={}", userId);
        
        User user = userService.getUserById(userId);
        UserDTO dto = convertToDTO(user);
        
        return ResponseEntity.ok(ResponseDataDTO.of(dto, "사용자 조회 성공"));
    }

    /**
     * DTO 변환
     */
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .profileImageUrl(user.getProfileImageUrl())
                .bio(user.getBio())
                .dateOfBirth(user.getDateOfBirth())
                .role(user.getRole())
                .emailVerified(user.isEmailVerified())
                .active(user.isActive())
                .build();
    }
}
