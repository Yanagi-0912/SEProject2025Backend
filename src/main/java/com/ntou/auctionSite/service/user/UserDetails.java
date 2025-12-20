package com.ntou.auctionSite.service.user;

import com.ntou.auctionSite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetails implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 從資料庫中載入使用者資訊，以供 Spring Security 認證使用
     */
    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 因為 User 已經實作了 UserDetails，直接返回即可
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
