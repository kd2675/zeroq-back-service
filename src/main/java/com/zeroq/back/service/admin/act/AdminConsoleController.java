package com.zeroq.back.service.admin.act;

import auth.common.core.context.UserContext;
import com.zeroq.back.service.admin.biz.AdminConsoleService;
import com.zeroq.back.service.admin.vo.AdminConsoleSettingsResponse;
import com.zeroq.back.service.admin.vo.AdminWorkspaceResponse;
import com.zeroq.back.service.admin.vo.UpdateAdminConsoleSettingsRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import web.common.core.response.base.dto.ResponseDataDTO;

@RestController
@RequestMapping("/api/zeroq/v1/admin")
@RequiredArgsConstructor
public class AdminConsoleController {
    private final AdminConsoleService adminConsoleService;

    @GetMapping("/workspace")
    public ResponseDataDTO<AdminWorkspaceResponse> getWorkspace(UserContext userContext) {
        return ResponseDataDTO.of(
                adminConsoleService.getWorkspace(userContext),
                "관리자 콘솔 워크스페이스 조회 완료"
        );
    }

    @GetMapping("/settings")
    public ResponseDataDTO<AdminConsoleSettingsResponse> getSettings(UserContext userContext) {
        return ResponseDataDTO.of(
                adminConsoleService.getSettings(userContext),
                "관리자 콘솔 설정 조회 완료"
        );
    }

    @PutMapping("/settings")
    public ResponseDataDTO<AdminConsoleSettingsResponse> updateSettings(
            @Valid @RequestBody UpdateAdminConsoleSettingsRequest request,
            UserContext userContext
    ) {
        return ResponseDataDTO.of(
                adminConsoleService.updateSettings(request, userContext),
                "관리자 콘솔 설정 저장 완료"
        );
    }
}
