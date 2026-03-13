package com.zeroq.back.service.admin.biz;

import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.admin.entity.AdminProfile;
import com.zeroq.back.database.admin.repository.AdminProfileRepository;
import com.zeroq.back.service.admin.vo.AdminProfileSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminProfileService {
    private static final String DEFAULT_TAGLINE = "실시간 공간 점유율을 관리하는 운영자";
    private static final String[] DEFAULT_COLORS = {
            "#2B8CEE",
            "#00A878",
            "#E67E22",
            "#D64550",
            "#6C5CE7",
            "#00897B"
    };

    private final AdminProfileRepository adminProfileRepository;

    public AdminProfileSummaryResponse getProfileSummary(String userKey) {
        AdminProfile profile = adminProfileRepository.findByUserKey(userKey)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("AdminProfile", "userKey", userKey));
        return toSummary(profile);
    }

    @Transactional(transactionManager = "adminTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public AdminProfileSummaryResponse initializeProfile(String userKey, String userName, String role) {
        return toSummary(getOrCreateProfile(userKey, userName, role));
    }

    @Transactional(transactionManager = "adminTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public Long resolveProfileId(String userKey, String userName, String role) {
        return getOrCreateProfile(userKey, userName, role).getProfileId();
    }

    @Transactional(transactionManager = "adminTransactionManager", propagation = Propagation.MANDATORY)
    protected AdminProfile getOrCreateProfile(String userKey, String userName, String role) {
        return adminProfileRepository.findByUserKey(userKey)
                .map(profile -> updateProfile(profile, userName, role))
                .orElseGet(() -> adminProfileRepository.save(AdminProfile.builder()
                        .userKey(userKey)
                        .displayName(resolveDisplayName(userName, userKey))
                        .tagline(DEFAULT_TAGLINE)
                        .profileColor(resolveProfileColor(userKey))
                        .role(normalizeRole(role))
                        .build()));
    }

    private AdminProfile updateProfile(AdminProfile profile, String userName, String role) {
        String normalizedRole = normalizeRole(role);
        boolean changed = false;

        String displayName = resolveDisplayName(userName, profile.getUserKey());
        if (!displayName.equals(profile.getDisplayName())) {
            profile.setDisplayName(displayName);
            changed = true;
        }
        if (!normalizedRole.equals(profile.getRole())) {
            profile.setRole(normalizedRole);
            changed = true;
        }

        return changed ? adminProfileRepository.save(profile) : profile;
    }

    private AdminProfileSummaryResponse toSummary(AdminProfile profile) {
        return AdminProfileSummaryResponse.builder()
                .profileId(profile.getProfileId())
                .userKey(profile.getUserKey())
                .displayName(profile.getDisplayName())
                .tagline(profile.getTagline())
                .profileColor(profile.getProfileColor())
                .role(profile.getRole())
                .build();
    }

    private String resolveDisplayName(String userName, String userKey) {
        if (StringUtils.hasText(userName)) {
            return userName;
        }
        if (!StringUtils.hasText(userKey)) {
            return "ZeroQ Admin";
        }
        return "Admin-" + userKey.substring(Math.max(0, userKey.length() - 6));
    }

    private String normalizeRole(String role) {
        if (!StringUtils.hasText(role)) {
            return "MANAGER";
        }
        return role.trim().toUpperCase(Locale.ROOT);
    }

    private String resolveProfileColor(String userKey) {
        if (!StringUtils.hasText(userKey)) {
            return DEFAULT_COLORS[0];
        }
        int hash = userKey.hashCode() & Integer.MAX_VALUE;
        return DEFAULT_COLORS[hash % DEFAULT_COLORS.length];
    }
}
