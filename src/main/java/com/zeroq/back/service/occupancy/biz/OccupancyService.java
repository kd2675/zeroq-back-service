package com.zeroq.back.service.occupancy.biz;

import com.zeroq.back.common.exception.LiveSpaceException;
import com.zeroq.back.database.pub.entity.OccupancyData;
import com.zeroq.back.database.pub.entity.OccupancyHistory;
import com.zeroq.back.database.pub.repository.OccupancyDataRepository;
import com.zeroq.back.database.pub.repository.OccupancyHistoryRepository;
import com.zeroq.back.database.pub.entity.Space;
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
    private final OccupancyDataRepository occupancyDataRepository;
    private final OccupancyHistoryRepository occupancyHistoryRepository;
    private final SpaceService spaceService;

    /**
     * 현재 점유율 조회
     */
    public OccupancyData getCurrentOccupancy(Long spaceId) {
        return occupancyDataRepository.findBySpaceId(spaceId)
                .orElseThrow(() -> new LiveSpaceException.ResourceNotFoundException("OccupancyData", "spaceId", spaceId));
    }

    /**
     * 점유율 히스토리 조회
     */
    public Page<OccupancyHistory> getOccupancyHistory(Long spaceId, Pageable pageable) {
        return occupancyHistoryRepository.findBySpaceIdOrderByCreateDateDesc(spaceId, pageable);
    }

    /**
     * 기간별 점유율 히스토리
     */
    public java.util.List<OccupancyHistory> getOccupancyHistoryByDateRange(Long spaceId, LocalDateTime startTime, LocalDateTime endTime) {
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
        Space space = spaceService.getSpaceById(spaceId);
        
        OccupancyData occupancyData = occupancyDataRepository.findBySpaceId(spaceId)
                .orElse(OccupancyData.builder()
                        .space(space)
                        .maxCapacity(maxCapacity)
                        .build());
        
        occupancyData.updateOccupancy(currentOccupancy, maxCapacity);
        occupancyDataRepository.save(occupancyData);

        // 히스토리 저장
        OccupancyHistory history = OccupancyHistory.builder()
                .space(space)
                .occupancyCount(currentOccupancy)
                .occupancyPercentage(occupancyData.getOccupancyPercentage())
                .crowdLevel(occupancyData.getCrowdLevel())
                .maxCapacity(maxCapacity)
                .build();
        occupancyHistoryRepository.save(history);
    }
}
