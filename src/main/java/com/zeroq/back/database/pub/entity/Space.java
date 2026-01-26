package com.zeroq.back.database.pub.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "space", indexes = {
        @Index(name = "idx_category_id", columnList = "category_id"),
        @Index(name = "idx_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Space extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name; // 공간명

    @Column(length = 1000)
    private String description; // 설명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; // 카테고리

    @OneToOne(mappedBy = "space", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Location location; // 위치 정보

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Amenity> amenities = new ArrayList<>(); // 편의시설

    @Column(nullable = false)
    private int capacity = 0; // 최대 수용인원

    @Column(length = 100)
    private String phoneNumber; // 전화번호

    @Column(length = 1000)
    private String imageUrl; // 이미지 URL

    @Column(length = 500)
    private String operatingHours; // 운영시간

    @Column(columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double averageRating = 0.0; // 평균 별점

    @Column(nullable = false)
    private boolean active = true; // 활성화 여부

    @Column(nullable = false)
    private boolean verified = false; // 인증 여부

    @Column(length = 50)
    private String ownerName; // 소유자명

    @Column(length = 100)
    private String ownerContact; // 소유자 연락처
}
