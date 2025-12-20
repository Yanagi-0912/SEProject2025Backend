package com.ntou.auctionSite.service.user;

import com.ntou.auctionSite.dto.user.AuthResponse;
import com.ntou.auctionSite.dto.user.LoginRequest;
import com.ntou.auctionSite.dto.user.RegisterRequest;
import com.ntou.auctionSite.model.user.User;
import com.ntou.auctionSite.repository.UserRepository;
import com.ntou.auctionSite.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * 使用者註冊
     */
    public AuthResponse register(RegisterRequest request){
        if (userRepository.findByUserName(request.getUsername()).isPresent()){
            throw new RuntimeException("使用者名稱已存在");
        }

        // 建立新使用者 - 使用 Builder 模式
        User user = User.builder()
                .userName(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .remainingDrawTimes(10)  // 新使用者預設 10 次抽獎機會
                .build();
        userRepository.save(user);

        // 產生 JWT token
        String token = jwtUtil.generateToken(user);

        return new AuthResponse(token, user.getUsername());
    }

    /**
     * 使用者登入
     */
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUserName(request.getUsername())
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        // 產生 JWT token
        String token = jwtUtil.generateToken(user);

        return new AuthResponse(token, user.getUsername());
    }

    /**
     * 使用者登出
     * 客戶端需要刪除儲存的 Token
     * Token 會在過期時間後自動失效（30分鐘）
     */
    public void logout(String token) {
        // JWT 是無狀態的，登出只需要客戶端刪除 token
        // Token 會在到期時間後自動失效
        // 如果需要立即撤銷，可以考慮使用 Redis 黑名單機制
    }

}
