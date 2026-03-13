package com.zeroq.back.service.profile.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileSummaryResponse {
    private Long profileId;
    private String userKey;
    private String displayName;
    private String tagline;
    private String profileColor;
}
