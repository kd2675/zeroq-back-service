package com.zeroq.back.database.pub.dto;

import com.zeroq.back.common.jpa.CommonDateDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpaceDTO extends CommonDateDTO {
    private Long id;
    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private String address;
    private double averageRating;
    private int reviewCount;
    private String imageUrl;
    private List<String> amenities;
    private boolean verified;
}
