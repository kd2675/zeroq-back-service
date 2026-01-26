package com.zeroq.back.database.pub.entity;

import com.zeroq.back.common.jpa.CommonDateEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user", indexes = {
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_phone", columnList = "phone_number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email; // 이메일

    @Column(nullable = false, length = 255)
    private String password; // 비밀번호 (암호화됨)

    @Column(nullable = false, length = 50)
    private String name; // 이름

    @Column(length = 20)
    private String phoneNumber; // 전화번호

    @Column(length = 500)
    private String profileImageUrl; // 프로필 이미지

    @Column(length = 500)
    private String bio; // 자기소개

    @Column(nullable = false)
    private LocalDate dateOfBirth; // 생년월일

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserRole role = UserRole.USER; // 사용자 역할

    @Column(nullable = false)
    @Builder.Default
    private boolean emailVerified = false; // 이메일 인증 여부

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true; // 활성화 여부

    @Column(nullable = false)
    @Builder.Default
    private boolean deleted = false; // 탈퇴 여부

    @Column(length = 500)
    private String deletionReason; // 탈퇴 사유

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UserPreference> preferences = new HashSet<>();

    /**
     * 사용자 역할 Enum
     */
    public enum UserRole {
        USER("사용자"),
        OWNER("사업자"),
        ADMIN("관리자");

        private final String description;

        UserRole(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
