package com.zeroq.back.database.admin.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "space", indexes = {
        @Index(name = "idx_admin_space_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSpace extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_profile_id", nullable = false)
    private Long ownerProfileId;

    @Column(name = "space_code", nullable = false, length = 50)
    private String spaceCode;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "operational_status", nullable = false, length = 20)
    @Builder.Default
    private String operationalStatus = "ACTIVE";

    @Column(length = 100)
    private String phoneNumber;

    @Column(length = 1000)
    private String imageUrl;

    @Column(length = 500)
    private String operatingHours;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double averageRating = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean verified = false;

    @Column(length = 50)
    private String ownerName;

    @Column(length = 100)
    private String ownerContact;

    @OneToOne(mappedBy = "space", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private AdminLocation location;

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AdminAmenity> amenities = new ArrayList<>();
}
