package com.zeroq.back.database.pub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSpaceRequest {
    @NotBlank(message = "공간명은 필수입니다")
    private String name;

    @NotBlank(message = "설명은 필수입니다")
    private String description;

    @NotNull(message = "카테고리 ID는 필수입니다")
    private Long categoryId;

    @Positive(message = "수용인원은 0보다 커야 합니다")
    private int capacity;

    @NotBlank(message = "주소는 필수입니다")
    private String address;

    @NotNull(message = "위도는 필수입니다")
    private Double latitude;

    @NotNull(message = "경도는 필수입니다")
    private Double longitude;

    private String phoneNumber;

    private String operatingHours;

    private String imageUrl;
}
