package com.zeroq.back.database.pub.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nfc_tag", indexes = {
        @Index(name = "idx_nfc_id", columnList = "nfc_id"),
        @Index(name = "idx_space_id", columnList = "space_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NfcTag extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nfcId; // NFC 태그 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id")
    private Space space; // 위치한 공간

    @Column(length = 100)
    private String location; // 상세 위치 (예: 입구, 카운터 등)

    @Column(nullable = false)
    private boolean active = true; // 활성화 여부

    @Column(nullable = false)
    private int readCount = 0; // 읽힘 횟수

    @Column(nullable = false)
    private long lastReadTimestamp; // 마지막 읽음 시간

    @Column(length = 500)
    private String note;
}
