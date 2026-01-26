package com.zeroq.back.database.pub.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String name; // 카테고리명 (카페, 공원, 쇼핑몰 등)

    @Column(length = 500)
    private String description; // 설명

    @Column(nullable = false)
    private String icon; // 아이콘 URL

    @Column(nullable = false)
    private boolean active = true; // 활성화 여부
}
