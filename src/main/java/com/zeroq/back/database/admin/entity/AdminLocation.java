package com.zeroq.back.database.admin.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "location", indexes = {
        @Index(name = "idx_admin_location_lat_lng", columnList = "latitude,longitude")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminLocation extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    private AdminSpace space;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(length = 100)
    private String roadAddress;

    @Column(length = 100)
    private String district;

    @Column(length = 100)
    private String city;

    @Column(nullable = false, length = 255)
    private String placeId;
}
