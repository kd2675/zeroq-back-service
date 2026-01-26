package com.zeroq.back.database.pub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long id;
    private Long spaceId;
    private String spaceName;
    private Long userId;
    private String userName;
    private int rating;
    private String title;
    private String content;
    private int likeCount;
    private boolean verified;
    private LocalDateTime createdAt;
}
