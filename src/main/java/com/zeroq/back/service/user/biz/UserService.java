package com.zeroq.back.service.user.biz;

import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.pub.entity.User;
import com.zeroq.back.database.pub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    /**
     * 사용자 조회
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("User", "id", userId));
    }

    /**
     * 이메일로 사용자 조회
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("User", "email", email));
    }

    /**
     * 활성 사용자 목록 조회
     */
    public Page<User> getActiveUsers(Pageable pageable) {
        return userRepository.findActiveUsers(pageable);
    }

    /**
     * 사용자 생성 (회원가입)
     */
    @Transactional
    public User createUser(User user) {
        // 이메일 중복 확인
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new LiveSpaceException.ConflictException("이미 존재하는 이메일입니다: " + user.getEmail());
        }
        return userRepository.save(user);
    }

    /**
     * 사용자 정보 수정
     */
    @Transactional
    public User updateUser(Long userId, User updatedUser) {
        User user = getUserById(userId);
        user.setName(updatedUser.getName());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setProfileImageUrl(updatedUser.getProfileImageUrl());
        user.setBio(updatedUser.getBio());
        return userRepository.save(user);
    }

    /**
     * 사용자 삭제 (탈퇴)
     */
    @Transactional
    public void deleteUser(Long userId, String deletionReason) {
        User user = getUserById(userId);
        user.setDeleted(true);
        user.setActive(false);
        user.setDeletionReason(deletionReason);
        userRepository.save(user);
        log.info("User deleted: userId={}", userId);
    }

    /**
     * 활성 사용자 수
     */
    public long countActiveUsers() {
        return userRepository.countActiveUsers();
    }
}
