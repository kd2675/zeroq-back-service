package com.zeroq.back.database.pub.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "favorite", indexes = {
        @Index(name = "idx_profile_id_space_id", columnList = "profile_id,space_id", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "space_id", nullable = false)
    private Long spaceId;

    @Column(name = "order_num", nullable = false)
    @Builder.Default
    private int order = 0; // 즐겨찾기 순서

    @Column(length = 500)
    private String note; // 메모 (예: 친구 만나는 곳)
}
