package com.zeroq.back.service.admin.biz;

import com.zeroq.back.database.admin.entity.AdminProfile;
import com.zeroq.back.database.admin.repository.AdminProfileRepository;
import com.zeroq.back.service.admin.vo.AdminProfileSummaryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminProfileServiceTests {

    @Mock
    private AdminProfileRepository adminProfileRepository;

    @InjectMocks
    private AdminProfileService adminProfileService;

    @Test
    void initializeProfile_createsAdminProfileInAdminStore() {
        when(adminProfileRepository.findByUserKey("admin-user-key")).thenReturn(Optional.empty());
        when(adminProfileRepository.save(any(AdminProfile.class)))
                .thenAnswer(invocation -> {
                    AdminProfile profile = invocation.getArgument(0);
                    profile.setProfileId(21L);
                    return profile;
                });

        AdminProfileSummaryResponse response = adminProfileService.initializeProfile(
                "admin-user-key",
                "admin01",
                "ADMIN"
        );

        assertThat(response.getProfileId()).isEqualTo(21L);
        assertThat(response.getUserKey()).isEqualTo("admin-user-key");
        assertThat(response.getDisplayName()).isEqualTo("admin01");
        assertThat(response.getRole()).isEqualTo("ADMIN");
        verify(adminProfileRepository).save(any(AdminProfile.class));
    }

    @Test
    void resolveProfileId_reusesExistingAdminProfile() {
        AdminProfile profile = AdminProfile.builder()
                .profileId(7L)
                .userKey("manager-user-key")
                .displayName("manager")
                .tagline("기존 관리자")
                .profileColor("#2B8CEE")
                .role("MANAGER")
                .build();
        when(adminProfileRepository.findByUserKey("manager-user-key")).thenReturn(Optional.of(profile));

        Long profileId = adminProfileService.resolveProfileId("manager-user-key", "manager", "MANAGER");

        assertThat(profileId).isEqualTo(7L);
    }
}
