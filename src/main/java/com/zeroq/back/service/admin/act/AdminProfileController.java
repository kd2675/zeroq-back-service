package com.zeroq.back.service.admin.act;

import auth.common.core.context.UserContext;
import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.service.admin.biz.AdminProfileService;
import com.zeroq.back.service.admin.vo.AdminProfileSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import web.common.core.response.base.dto.ResponseDataDTO;

@RestController
@RequestMapping("/api/zeroq/v1/admin/profile")
@RequiredArgsConstructor
public class AdminProfileController {
    private final AdminProfileService adminProfileService;

    @GetMapping("/summary")
    public ResponseDataDTO<AdminProfileSummaryResponse> getProfileSummary(UserContext userContext) {
        requireManagerOrAdmin(userContext);
        return ResponseDataDTO.of(
                adminProfileService.getProfileSummary(requireUserKey(userContext)),
                "관리자 프로필 요약 조회 성공"
        );
    }

    @PostMapping("/initialize")
    public ResponseDataDTO<AdminProfileSummaryResponse> initializeProfile(UserContext userContext) {
        requireManagerOrAdmin(userContext);
        return ResponseDataDTO.of(
                adminProfileService.initializeProfile(
                        requireUserKey(userContext),
                        userContext.getUserName(),
                        userContext.getRole()
                ),
                "관리자 프로필 초기화 성공"
        );
    }

    private String requireUserKey(UserContext userContext) {
        if (userContext == null || !StringUtils.hasText(userContext.getUserKey())) {
            throw new LiveSpaceException.UnauthorizedException("Login required");
        }
        return userContext.getUserKey();
    }

    private void requireManagerOrAdmin(UserContext userContext) {
        if (userContext == null || !userContext.isAuthenticated()) {
            throw new LiveSpaceException.UnauthorizedException("Login required");
        }
        if (!userContext.isManager() && !userContext.isAdmin()) {
            throw new LiveSpaceException.ForbiddenException("MANAGER or ADMIN role required");
        }
    }
}
