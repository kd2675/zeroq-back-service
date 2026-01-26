package com.zeroq.back.database.pub.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "location", indexes = {
        @Index(name = "idx_latitude_longitude", columnList = "latitude,longitude")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double latitude; // 위도

    @Column(nullable = false)
    private Double longitude; // 경도

    @Column(nullable = false, length = 500)
    private String address; // 주소

    @Column(length = 100)
    private String roadAddress; // 도로명 주소

    @Column(length = 100)
    private String district; // 지역 (동, 구 등)

    @Column(length = 100)
    private String city; // 시/도

    @Column(nullable = false)
    private String placeId; // 외부 장소 ID (구글 플레이스 ID 등)

    @OneToOne
    @JoinColumn(name = "space_id", nullable = false)
    private Space space;
}
