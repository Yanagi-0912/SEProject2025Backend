package com.ntou.auctionSite.service;

import com.ntou.auctionSite.dto.AuthResponse;
import com.ntou.auctionSite.dto.LoginRequest;
import com.ntou.auctionSite.dto.RegisterRequest;
import com.ntou.auctionSite.model.User;
import com.ntou.auctionSite.repository.UserRepository;
import com.ntou.auctionSite.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

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

}
