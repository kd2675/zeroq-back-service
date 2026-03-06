package com.zeroq.back.service.space.biz;

import com.zeroq.back.database.pub.entity.Space;
import com.zeroq.back.database.pub.repository.SpaceRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpaceServiceTests {

    @Mock
    private SpaceRepository spaceRepository;

    @InjectMocks
    private SpaceService spaceService;

    @Test
    void searchSpaces_withoutCategory_usesKeywordSearchOnActiveSpaces() {
        Pageable pageable = PageRequest.of(0, 20);
        Space space = Space.builder().id(1L).name("Cafe One").active(true).build();
        Page<Space> expected = new PageImpl<>(List.of(space), pageable, 1);

        when(spaceRepository.findByNameContainingIgnoreCaseAndActiveTrue("cafe", pageable)).thenReturn(expected);

        Page<Space> result = spaceService.searchSpaces("cafe", pageable);

        assertThat(result.getContent()).containsExactly(space);
        verify(spaceRepository).findByNameContainingIgnoreCaseAndActiveTrue("cafe", pageable);
        verifyNoMoreInteractions(spaceRepository);
    }

    @Test
    void searchSpaces_withCategory_usesCategoryAwareSearch() {
        Pageable pageable = PageRequest.of(0, 20);
        Space space = Space.builder().id(2L).name("Cafe Two").active(true).build();
        Page<Space> expected = new PageImpl<>(List.of(space), pageable, 1);

        when(spaceRepository.searchByKeywordAndCategory("cafe", 3L, pageable)).thenReturn(expected);

        Page<Space> result = spaceService.searchSpaces("cafe", 3L, pageable);

        assertThat(result.getContent()).containsExactly(space);
        verify(spaceRepository).searchByKeywordAndCategory("cafe", 3L, pageable);
        verifyNoMoreInteractions(spaceRepository);
    }
}
