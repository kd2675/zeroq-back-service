package com.zeroq.back.service.auth.act;

import com.zeroq.core.response.base.dto.ResponseDataDTO;
import com.zeroq.back.database.pub.dto.LoginRequest;
import com.zeroq.back.database.pub.dto.SignUpRequest;
import com.zeroq.back.database.pub.dto.TokenResponse;
import com.zeroq.back.service.auth.biz.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * 회원가입
     * POST /api/v1/auth/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<ResponseDataDTO<TokenResponse>> signUp(@Valid @RequestBody SignUpRequest request) {
        log.info("Sign up request: email={}", request.getEmail());
        TokenResponse response = authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDataDTO.of(response, "회원가입이 완료되었습니다"));
    }

    /**
     * 로그인
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseDataDTO<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request: email={}", request.getEmail());
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(ResponseDataDTO.of(response, "로그인이 완료되었습니다"));
    }

    /**
     * 토큰 갱신
     * POST /api/v1/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<ResponseDataDTO<TokenResponse>> refreshToken(
            @RequestHeader("Authorization") String refreshToken) {
        String token = refreshToken.replace("Bearer ", "");
        TokenResponse response = authService.refreshAccessToken(token);
        return ResponseEntity.ok(ResponseDataDTO.of(response, "토큰이 갱신되었습니다"));
    }

    /**
     * 헬스 체크
     * GET /api/v1/auth/health
     */
    @GetMapping("/health")
    public ResponseEntity<ResponseDataDTO<String>> health() {
        return ResponseEntity.ok(ResponseDataDTO.of("OK", "서버가 정상입니다"));
    }
}
