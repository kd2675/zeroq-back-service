package com.zeroq.back.database.pub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteDTO {
    private Long id;
    private Long spaceId;
    private String spaceName;
    private String categoryName;
    private int order;
    private String note;
}
