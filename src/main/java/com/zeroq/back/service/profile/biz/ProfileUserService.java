package com.zeroq.back.service.profile.biz;

import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.pub.entity.ProfileUser;
import com.zeroq.back.database.pub.repository.ProfileUserRepository;
import com.zeroq.back.service.profile.vo.ProfileSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileUserService {
    private static final String DEFAULT_TAGLINE = "실시간 공간 점유율을 관리하는 운영자";
    private static final String[] DEFAULT_COLORS = {
            "#2B8CEE",
            "#00A878",
            "#E67E22",
            "#D64550",
            "#6C5CE7",
            "#00897B"
    };

    private final ProfileUserRepository profileUserRepository;

    public ProfileSummaryResponse getProfileSummary(String userKey) {
        ProfileUser profile = profileUserRepository.findByUserKey(userKey)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("ProfileUser", "userKey", userKey));
        return toSummary(profile);
    }

    @Transactional(transactionManager = "pubTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public ProfileSummaryResponse initializeProfile(String userKey, String userName) {
        ProfileUser profile = getOrCreateProfile(userKey, userName);
        return toSummary(profile);
    }

    @Transactional(transactionManager = "pubTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public Long resolveProfileId(String userKey, String userName) {
        return getOrCreateProfile(userKey, userName).getProfileId();
    }

    public Long getProfileIdByUserKey(String userKey) {
        return profileUserRepository.findByUserKey(userKey)
                .map(ProfileUser::getProfileId)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("ProfileUser", "userKey", userKey));
    }

    @Transactional(transactionManager = "pubTransactionManager", propagation = Propagation.MANDATORY)
    protected ProfileUser getOrCreateProfile(String userKey, String userName) {
        return profileUserRepository.findByUserKey(userKey)
                .orElseGet(() -> profileUserRepository.save(ProfileUser.builder()
                        .userKey(userKey)
                        .displayName(resolveDisplayName(userName, userKey))
                        .tagline(DEFAULT_TAGLINE)
                        .profileColor(resolveProfileColor(userKey))
                        .build()));
    }

    private ProfileSummaryResponse toSummary(ProfileUser profile) {
        return ProfileSummaryResponse.builder()
                .profileId(profile.getProfileId())
                .userKey(profile.getUserKey())
                .displayName(profile.getDisplayName())
                .tagline(profile.getTagline())
                .profileColor(profile.getProfileColor())
                .build();
    }

    private String resolveDisplayName(String userName, String userKey) {
        if (StringUtils.hasText(userName)) {
            return userName;
        }
        if (!StringUtils.hasText(userKey)) {
            return "ZeroQ User";
        }
        return "User-" + userKey.substring(Math.max(0, userKey.length() - 6));
    }

    private String resolveProfileColor(String userKey) {
        if (!StringUtils.hasText(userKey)) {
            return DEFAULT_COLORS[0];
        }
        int hash = userKey.hashCode() & Integer.MAX_VALUE;
        return DEFAULT_COLORS[hash % DEFAULT_COLORS.length];
    }
}
