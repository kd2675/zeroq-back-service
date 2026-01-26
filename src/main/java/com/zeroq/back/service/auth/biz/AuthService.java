package com.zeroq.back.service.auth.biz;

import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.pub.dto.LoginRequest;
import com.zeroq.back.database.pub.dto.SignUpRequest;
import com.zeroq.back.database.pub.dto.TokenResponse;
import com.zeroq.back.database.pub.entity.User;
import com.zeroq.back.database.pub.repository.UserRepository;
import com.zeroq.back.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입
     */
    @Transactional
    public TokenResponse signUp(SignUpRequest request) {
        // 이메일 중복 확인
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new LiveSpaceException.ConflictException("이미 존재하는 이메일입니다: " + request.getEmail());
        }

        // 사용자 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .role(User.UserRole.USER)
                .active(true)
                .emailVerified(false)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User signed up successfully: userId={}, email={}", savedUser.getId(), savedUser.getEmail());

        // 토큰 생성
        return generateToken(savedUser);
    }

    /**
     * 로그인
     */
    public TokenResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("User", "email", request.getEmail()));

            log.info("User logged in successfully: userId={}, email={}", user.getId(), user.getEmail());
            return generateToken(user);
        } catch (Exception e) {
            log.warn("Login failed for email: {}", request.getEmail());
            throw new LiveSpaceException.UnauthorizedException("이메일 또는 비밀번호가 일치하지 않습니다");
        }
    }

    /**
     * 토큰 생성
     */
    private TokenResponse generateToken(User user) {
        String roles = "ROLE_" + user.getRole().name();
        String accessToken = jwtTokenProvider.generateTokenFromUserId(user.getId(), user.getEmail(), roles);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(86400) // 24 hours
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    /**
     * 리프레시 토큰으로 새 액세스 토큰 발급
     */
    public TokenResponse refreshAccessToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new LiveSpaceException.UnauthorizedException("유효하지 않은 리프레시 토큰입니다");
        }

        String email = jwtTokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("User", "email", email));

        String roles = "ROLE_" + user.getRole().name();
        String newAccessToken = jwtTokenProvider.generateTokenFromUserId(user.getId(), user.getEmail(), roles);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .expiresIn(86400)
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
