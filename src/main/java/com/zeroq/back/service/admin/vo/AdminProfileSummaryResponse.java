package com.zeroq.back.service.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProfileSummaryResponse {
    private Long profileId;
    private String userKey;
    private String displayName;
    private String tagline;
    private String profileColor;
    private String role;
}
