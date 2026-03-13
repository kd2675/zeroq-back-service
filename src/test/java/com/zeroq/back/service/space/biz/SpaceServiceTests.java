package com.zeroq.back.service.space.biz;

import com.zeroq.back.database.admin.entity.AdminSpace;
import com.zeroq.back.database.admin.repository.AdminSpaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpaceServiceTests {

    @Mock
    private AdminSpaceRepository spaceRepository;

    @InjectMocks
    private SpaceService spaceService;

    @Test
    void searchSpaces_withoutCategory_usesKeywordSearchOnActiveSpaces() {
        Pageable pageable = PageRequest.of(0, 20);
        AdminSpace space = AdminSpace.builder().id(1L).name("Cafe One").active(true).build();
        Page<AdminSpace> expected = new PageImpl<>(List.of(space), pageable, 1);

        when(spaceRepository.findByNameContainingIgnoreCaseAndActiveTrue("cafe", pageable)).thenReturn(expected);

        Page<AdminSpace> result = spaceService.searchSpaces("cafe", pageable);

        assertThat(result.getContent()).containsExactly(space);
        verify(spaceRepository).findByNameContainingIgnoreCaseAndActiveTrue("cafe", pageable);
        verifyNoMoreInteractions(spaceRepository);
    }

    @Test
    void getManagedSpaces_forManager_filtersByOwnerProfileId() {
        Pageable pageable = PageRequest.of(0, 24);
        AdminSpace space = AdminSpace.builder()
                .id(101L)
                .ownerProfileId(7L)
                .name("Manager Cafe")
                .active(true)
                .verified(true)
                .build();
        Page<AdminSpace> expected = new PageImpl<>(List.of(space), pageable, 1);

        when(spaceRepository.findByActiveTrueAndVerifiedTrueAndOwnerProfileId(7L, pageable)).thenReturn(expected);

        Page<AdminSpace> result = spaceService.getManagedSpaces(7L, false, pageable);

        assertThat(result.getContent()).containsExactly(space);
        verify(spaceRepository).findByActiveTrueAndVerifiedTrueAndOwnerProfileId(7L, pageable);
        verifyNoMoreInteractions(spaceRepository);
    }

    @Test
    void getManagedSpaces_forAdmin_usesGlobalActiveSpaces() {
        Pageable pageable = PageRequest.of(0, 24);
        Page<AdminSpace> expected = new PageImpl<>(List.of(), pageable, 0);

        when(spaceRepository.findByActiveAndVerifiedTrue(true, pageable)).thenReturn(expected);

        Page<AdminSpace> result = spaceService.getManagedSpaces(99L, true, pageable);

        assertThat(result).isSameAs(expected);
        verify(spaceRepository).findByActiveAndVerifiedTrue(true, pageable);
        verifyNoMoreInteractions(spaceRepository);
    }

    @Test
    void getWorkspaceManagedSpaces_forManager_includesDraftAndInactiveOwnedSpaces() {
        Pageable pageable = PageRequest.of(0, 24);
        AdminSpace draft = AdminSpace.builder()
                .id(403L)
                .ownerProfileId(4L)
                .name("Draft Store")
                .active(true)
                .verified(false)
                .build();
        AdminSpace inactive = AdminSpace.builder()
                .id(404L)
                .ownerProfileId(4L)
                .name("Inactive Lounge")
                .active(false)
                .verified(true)
                .build();
        Page<AdminSpace> expected = new PageImpl<>(List.of(draft, inactive), pageable, 2);

        when(spaceRepository.findByOwnerProfileId(4L, pageable)).thenReturn(expected);

        Page<AdminSpace> result = spaceService.getWorkspaceManagedSpaces(4L, false, pageable);

        assertThat(result.getContent()).containsExactly(draft, inactive);
        verify(spaceRepository).findByOwnerProfileId(4L, pageable);
        verifyNoMoreInteractions(spaceRepository);
    }
}
