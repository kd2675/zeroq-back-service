package com.zeroq.back.database.pub.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "amenity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Amenity extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    private Space space;

    @Column(nullable = false, length = 50)
    private String name; // 편의시설명 (와이파이, 휴대폰 충전 등)

    @Column(length = 500)
    private String description; // 설명

    @Column(nullable = false)
    private String icon; // 아이콘 URL

    @Column(nullable = false)
    private boolean available = true; // 이용 가능 여부
}
