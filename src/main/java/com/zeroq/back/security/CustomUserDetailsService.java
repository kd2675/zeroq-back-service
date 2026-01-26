package com.zeroq.back.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * UserDetailsService 구현
 * 사용자 인증 시 DB에서 사용자 정보를 조회합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // TODO: UserRepository를 주입받아 사용자 정보 조회
        // User user = userRepository.findByEmail(email)
        //         .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // 임시 구현 - 실제로는 DB에서 조회
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        return org.springframework.security.core.userdetails.User.builder()
                .username(email)
                .password("") // DB에서 조회한 암호화된 비밀번호 사용
                .authorities(authorities)
                .disabled(false)
                .build();
    }
}
