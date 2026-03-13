package com.zeroq.back.service.occupancy.biz;

import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.admin.entity.AdminOccupancyData;
import com.zeroq.back.database.admin.entity.AdminOccupancyHistory;
import com.zeroq.back.database.admin.entity.AdminSpace;
import com.zeroq.back.database.admin.repository.AdminOccupancyDataRepository;
import com.zeroq.back.database.admin.repository.AdminOccupancyHistoryRepository;
import com.zeroq.back.service.space.biz.SpaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OccupancyService {
    private final AdminOccupancyDataRepository occupancyDataRepository;
    private final AdminOccupancyHistoryRepository occupancyHistoryRepository;
    private final SpaceService spaceService;

    /**
     * 현재 점유율 조회
     */
    public AdminOccupancyData getCurrentOccupancy(Long spaceId) {
        return occupancyDataRepository.findBySpaceId(spaceId)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("OccupancyData", "spaceId", spaceId));
    }

    /**
     * 점유율 히스토리 조회
     */
    public Page<AdminOccupancyHistory> getOccupancyHistory(Long spaceId, Pageable pageable) {
        return occupancyHistoryRepository.findBySpaceIdOrderByCreateDateDesc(spaceId, pageable);
    }

    /**
     * 기간별 점유율 히스토리
     */
    public java.util.List<AdminOccupancyHistory> getOccupancyHistoryByDateRange(Long spaceId, LocalDateTime startTime, LocalDateTime endTime) {
        return occupancyHistoryRepository.findBySpaceIdAndDateRange(spaceId, startTime, endTime);
    }

    /**
     * 평균 점유율 조회
     */
    public Double getAverageOccupancy(Long spaceId, LocalDateTime startTime) {
        return occupancyHistoryRepository.getAverageOccupancy(spaceId, startTime);
    }

    /**
     * 점유율 업데이트 (센서로부터 수신)
     */
    @Transactional
    public void updateOccupancy(Long spaceId, int currentOccupancy, int maxCapacity) {
        AdminSpace space = spaceService.getSpaceById(spaceId);

        AdminOccupancyData occupancyData = occupancyDataRepository.findBySpaceId(spaceId)
                .orElse(AdminOccupancyData.builder()
                        .space(space)
                        .maxCapacity(maxCapacity)
                        .build());
        
        occupancyData.updateOccupancy(currentOccupancy, maxCapacity);
        occupancyDataRepository.save(occupancyData);

        // 히스토리 저장
        AdminOccupancyHistory history = AdminOccupancyHistory.builder()
                .space(space)
                .occupancyCount(currentOccupancy)
                .occupancyPercentage(occupancyData.getOccupancyPercentage())
                .crowdLevel(occupancyData.getCrowdLevel())
                .maxCapacity(maxCapacity)
                .build();
        occupancyHistoryRepository.save(history);
    }
}
