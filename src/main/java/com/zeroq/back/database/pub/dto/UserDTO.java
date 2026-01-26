package com.zeroq.back.database.pub.dto;

import com.zeroq.back.database.pub.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private String phoneNumber;
    private String profileImageUrl;
    private String bio;
    private LocalDate dateOfBirth;
    private User.UserRole role;
    private boolean emailVerified;
    private boolean active;
}
