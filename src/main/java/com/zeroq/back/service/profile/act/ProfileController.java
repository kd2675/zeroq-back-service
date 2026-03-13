package com.zeroq.back.service.profile.act;

import auth.common.core.context.UserContext;
import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.service.profile.biz.ProfileUserService;
import com.zeroq.back.service.profile.vo.ProfileSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.StringUtils;
import web.common.core.response.base.dto.ResponseDataDTO;

@RestController
@RequestMapping("/api/zeroq/v1/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileUserService profileUserService;

    @GetMapping("/summary")
    public ResponseDataDTO<ProfileSummaryResponse> getProfileSummary(UserContext userContext) {
        requireUserRole(userContext);
        String userKey = requireUserKey(userContext);
        return ResponseDataDTO.of(
                profileUserService.getProfileSummary(userKey),
                "프로필 요약 조회 성공"
        );
    }

    @PostMapping("/initialize")
    public ResponseDataDTO<ProfileSummaryResponse> initializeProfile(UserContext userContext) {
        requireUserRole(userContext);
        String userKey = requireUserKey(userContext);
        return ResponseDataDTO.of(
                profileUserService.initializeProfile(userKey, userContext.getUserName()),
                "프로필 초기화 성공"
        );
    }

    private String requireUserKey(UserContext userContext) {
        if (userContext == null || !StringUtils.hasText(userContext.getUserKey())) {
            throw new LiveSpaceException.UnauthorizedException("Login required");
        }
        return userContext.getUserKey();
    }

    private void requireUserRole(UserContext userContext) {
        if (userContext == null || !userContext.isAuthenticated()) {
            throw new LiveSpaceException.UnauthorizedException("Login required");
        }
        if (!userContext.isUser()) {
            throw new LiveSpaceException.ForbiddenException("USER role required");
        }
    }
}
