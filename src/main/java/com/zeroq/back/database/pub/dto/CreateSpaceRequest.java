package com.zeroq.back.database.pub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "주소는 필수입니다")
    private String address;

    @NotNull(message = "위도는 필수입니다")
    private Double latitude;

    @NotNull(message = "경도는 필수입니다")
    private Double longitude;

    private String phoneNumber;

    private String operatingHours;

    private String imageUrl;

    private String operationalStatus;
}
