package com.zeroq.back.service.occupancy.act;

import web.common.core.response.base.dto.ResponseDataDTO;
import com.zeroq.back.database.pub.dto.OccupancyDTO;
import com.zeroq.back.database.pub.entity.OccupancyData;
import com.zeroq.back.database.pub.entity.OccupancyHistory;
import com.zeroq.back.service.occupancy.biz.OccupancyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/v1/occupancy")
@RequiredArgsConstructor
public class OccupancyController {
    private final OccupancyService occupancyService;

    /**
     * 현재 점유율 조회
     * GET /api/v1/occupancy/spaces/{spaceId}
     */
    @GetMapping("/spaces/{spaceId}")
    public ResponseDataDTO<OccupancyDTO> getCurrentOccupancy(@PathVariable Long spaceId) {
        log.info("Get current occupancy: spaceId={}", spaceId);

        OccupancyData data = occupancyService.getCurrentOccupancy(spaceId);
        OccupancyDTO dto = convertToDTO(data);

        return ResponseDataDTO.of(dto, "현재 점유율 조회 성공");
    }

    /**
     * 점유율 히스토리 조회
     * GET /api/v1/occupancy/spaces/{spaceId}/history?page=0&size=20
     */
    @GetMapping("/spaces/{spaceId}/history")
    public ResponseDataDTO<Page<OccupancyDTO>> getOccupancyHistory(
            @PathVariable Long spaceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get occupancy history: spaceId={}, page={}, size={}", spaceId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<OccupancyHistory> histories = occupancyService.getOccupancyHistory(spaceId, pageable);

        Page<OccupancyDTO> dtos = histories.map(h -> OccupancyDTO.builder()
                .spaceId(h.getSpace().getId())
                .spaceName(h.getSpace().getName())
                .currentOccupancy(h.getOccupancyCount())
                .maxCapacity(h.getMaxCapacity())
                .occupancyPercentage(h.getOccupancyPercentage())
                .crowdLevel(h.getCrowdLevel())
                .lastUpdated(h.getCreatedAt())
                .build());

        return ResponseDataDTO.of(dtos, "점유율 히스토리 조회 성공");
    }

    /**
     * 기간별 점유율 평균
     * GET /api/v1/occupancy/spaces/{spaceId}/average?days=7
     */
    @GetMapping("/spaces/{spaceId}/average")
    public ResponseDataDTO<Double> getAverageOccupancy(
            @PathVariable Long spaceId,
            @RequestParam(defaultValue = "7") int days) {
        log.info("Get average occupancy: spaceId={}, days={}", spaceId, days);

        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        Double average = occupancyService.getAverageOccupancy(spaceId, startTime);

        return ResponseDataDTO.of(average, "평균 점유율 조회 성공");
    }

    /**
     * DTO 변환
     */
    private OccupancyDTO convertToDTO(OccupancyData data) {
        return OccupancyDTO.builder()
                .spaceId(data.getSpace().getId())
                .spaceName(data.getSpace().getName())
                .currentOccupancy(data.getCurrentOccupancy())
                .maxCapacity(data.getMaxCapacity())
                .occupancyPercentage(data.getOccupancyPercentage())
                .crowdLevel(data.getCrowdLevel())
                .lastUpdated(data.getUpdatedAt())
                .build();
    }
}
