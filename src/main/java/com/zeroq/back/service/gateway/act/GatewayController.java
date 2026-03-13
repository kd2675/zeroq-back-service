package com.zeroq.back.service.gateway.act;

import auth.common.core.context.UserContext;
import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.service.admin.biz.AdminProfileService;
import com.zeroq.back.service.gateway.biz.GatewayService;
import com.zeroq.back.service.gateway.vo.CreateGatewayRequest;
import com.zeroq.back.service.gateway.vo.GatewayRegistryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import web.common.core.response.base.dto.ResponseDataDTO;

@RestController
@RequestMapping("/api/zeroq/v1/gateways")
@RequiredArgsConstructor
public class GatewayController {
    private final GatewayService gatewayService;
    private final AdminProfileService adminProfileService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDataDTO<GatewayRegistryResponse> createGateway(
            @Valid @RequestBody CreateGatewayRequest request,
            UserContext userContext
    ) {
        Long profileId = resolveAdminProfileId(userContext);
        return ResponseDataDTO.of(
                gatewayService.createGateway(request, profileId, userContext.isAdmin()),
                "게이트웨이가 등록되었습니다"
        );
    }

    private Long resolveAdminProfileId(UserContext userContext) {
        if (userContext == null || !userContext.isAuthenticated()) {
            throw new LiveSpaceException.UnauthorizedException("Login required");
        }
        if (!userContext.isManager() && !userContext.isAdmin()) {
            throw new LiveSpaceException.ForbiddenException("MANAGER or ADMIN role required");
        }
        if (userContext.getUserKey() == null || userContext.getUserKey().isBlank()) {
            throw new LiveSpaceException.ForbiddenException("인증 사용자 정보가 없습니다");
        }
        return adminProfileService.resolveProfileId(
                userContext.getUserKey(),
                userContext.getUserName(),
                userContext.getRole()
        );
    }
}
