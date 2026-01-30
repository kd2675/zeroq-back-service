package com.zeroq.back.database.pub.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "review", indexes = {
        @Index(name = "idx_space_id_created_at", columnList = "space_id,created_at"),
        @Index(name = "idx_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    private Space space;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private int rating; // 별점 (1-5)

    @Column(nullable = false, length = 1000)
    private String title; // 제목

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 내용

    @Column(nullable = false)
    private int likeCount = 0; // 좋아요 수

    @Column(nullable = false)
    private boolean verified = false; // 인증 리뷰 여부

    @Column(nullable = false)
    private boolean deleted = false; // 삭제 여부

    @Column(length = 500)
    private String adminReply; // 관리자 답변
}
