package com.ntou.auctionSite.controller;

import com.ntou.auctionSite.controller.dto.LoginRequest;
import com.ntou.auctionSite.controller.dto.RegisterRequest;
import com.ntou.auctionSite.model.User;
import com.ntou.auctionSite.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    //測試伺服器是否運行
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Service is running");
    }

    // 登入功能
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        User u = userService.loginService(req.getId(), req.getPassword());
        if (u == null) return ResponseEntity.status(401).body("invalid credentials");
        return ResponseEntity.ok(u);
    }

    //註冊功能
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest req) {
        User user = new User();
        user.setUserId(req.getId());
        user.setPassword(req.getPassword());
        user.setEmail(req.getEmail());

        userService.registerService(user);
        return ResponseEntity.ok("註冊成功");
    }
}
