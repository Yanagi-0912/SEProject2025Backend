package com.ntou.auctionSite.service;

import com.ntou.auctionSite.dto.user.AuthResponse;
import com.ntou.auctionSite.dto.user.LoginRequest;
import com.ntou.auctionSite.dto.user.RegisterRequest;
import com.ntou.auctionSite.model.user.User;
import com.ntou.auctionSite.repository.UserRepository;
import com.ntou.auctionSite.service.user.AuthService;
import com.ntou.auctionSite.utils.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 認證服務測試類別
 * 對應測試案例：UT-001, UT-003, UT-035
 * 測試 AuthService 的註冊、登入、JWT Token 功能
 */
@SpringBootTest
@DisplayName("認證服務測試 (AuthService)")
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private static final String TEST_USERNAME = "testAuthUser";
    private static final String TEST_PASSWORD = "testPassword123";
    private static final String TEST_EMAIL = "testauth@example.com";

    @BeforeEach
    void setUp() {
        // 清理測試資料
        userRepository.findByUserName(TEST_USERNAME)
                .ifPresent(userRepository::delete);
    }

    @AfterEach
    void tearDown() {
        // 清理測試資料
        userRepository.findByUserName(TEST_USERNAME)
                .ifPresent(userRepository::delete);
    }

    // ==================== UT-001: 註冊使用者 ====================

    @Test
    @DisplayName("UT-001: 註冊使用者 - 成功註冊新使用者")
    void register_Success_ShouldReturnAuthResponseWithToken() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername(TEST_USERNAME);
        request.setPassword(TEST_PASSWORD);
        request.setEmail(TEST_EMAIL);

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertNotNull(response, "回應不應為 null");
        assertNotNull(response.getToken(), "Token 不應為 null");
        assertEquals(TEST_USERNAME, response.getUsername(), "使用者名稱應該匹配");

        // 驗證使用者已被儲存到資料庫
        assertTrue(userRepository.findByUserName(TEST_USERNAME).isPresent(),
                "使用者應該被儲存到資料庫");

        // 驗證 Token 是有效的
        String extractedUsername = jwtUtil.extractUsername(response.getToken());
        assertEquals(TEST_USERNAME, extractedUsername, "Token 中的使用者名稱應該正確");

        System.out.println("✅ UT-001 測試通過：成功註冊新使用者");
        System.out.println("   Token: " + response.getToken().substring(0, 50) + "...");
    }

    @Test
    @DisplayName("UT-001: 註冊使用者 - 使用者名稱已存在時應拋出異常")
    void register_DuplicateUsername_ShouldThrowException() {
        // Arrange - 先建立一個使用者
        User existingUser = User.builder()
                .userName(TEST_USERNAME)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .email("existing@example.com")
                .build();
        userRepository.save(existingUser);

        // 嘗試用相同使用者名稱註冊
        RegisterRequest request = new RegisterRequest();
        request.setUsername(TEST_USERNAME);
        request.setPassword("anotherPassword");
        request.setEmail("another@example.com");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(request),
                "使用重複的使用者名稱註冊應該拋出異常");

        assertEquals("使用者名稱已存在", exception.getMessage());
        System.out.println("✅ UT-001 測試通過：重複使用者名稱正確拋出異常");
    }

    // ==================== UT-003: 登入、登出 ====================

    @Test
    @DisplayName("UT-003: 登入 - 成功登入")
    void login_Success_ShouldReturnAuthResponseWithToken() {
        // Arrange - 先註冊一個使用者
        User user = User.builder()
                .userName(TEST_USERNAME)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .email(TEST_EMAIL)
                .build();
        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setUsername(TEST_USERNAME);
        request.setPassword(TEST_PASSWORD);

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response, "回應不應為 null");
        assertNotNull(response.getToken(), "Token 不應為 null");
        assertEquals(TEST_USERNAME, response.getUsername(), "使用者名稱應該匹配");

        // 驗證 Token 有效
        User savedUser = userRepository.findByUserName(TEST_USERNAME).orElseThrow();
        assertTrue(jwtUtil.isTokenValid(response.getToken(), savedUser),
                "Token 應該是有效的");

        System.out.println("✅ UT-003 測試通過：成功登入");
    }

    @Test
    @DisplayName("UT-003: 登入 - 密碼錯誤時應拋出異常")
    void login_WrongPassword_ShouldThrowException() {
        // Arrange - 先註冊一個使用者
        User user = User.builder()
                .userName(TEST_USERNAME)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .email(TEST_EMAIL)
                .build();
        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setUsername(TEST_USERNAME);
        request.setPassword("wrongPassword");

        // Act & Assert
        assertThrows(BadCredentialsException.class,
                () -> authService.login(request),
                "錯誤密碼應該拋出 BadCredentialsException");

        System.out.println("✅ UT-003 測試通過：錯誤密碼正確拋出異常");
    }

    @Test
    @DisplayName("UT-003: 登入 - 使用者不存在時應拋出異常")
    void login_UserNotFound_ShouldThrowException() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistentUser");
        request.setPassword(TEST_PASSWORD);

        // Act & Assert
        assertThrows(Exception.class,
                () -> authService.login(request),
                "不存在的使用者登入應該拋出異常");

        System.out.println("✅ UT-003 測試通過：不存在的使用者正確拋出異常");
    }

    // ==================== UT-035: JWT Token 認證 ====================

    @Test
    @DisplayName("UT-035: JWT Token 認證 - Token 應包含正確的使用者資訊")
    void jwtToken_ShouldContainCorrectUserInfo() {
        // Arrange - 註冊並取得 Token
        RegisterRequest request = new RegisterRequest();
        request.setUsername(TEST_USERNAME);
        request.setPassword(TEST_PASSWORD);
        request.setEmail(TEST_EMAIL);

        AuthResponse response = authService.register(request);
        String token = response.getToken();

        // Act
        String extractedUsername = jwtUtil.extractUsername(token);

        // Assert
        assertEquals(TEST_USERNAME, extractedUsername,
                "從 Token 解析出的使用者名稱應該正確");

        System.out.println("✅ UT-035 測試通過：JWT Token 包含正確的使用者資訊");
    }

    @Test
    @DisplayName("UT-035: JWT Token 認證 - 有效 Token 驗證應通過")
    void jwtToken_ValidToken_ShouldPassValidation() {
        // Arrange - 註冊使用者並取得 Token
        User user = User.builder()
                .userName(TEST_USERNAME)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .email(TEST_EMAIL)
                .build();
        user = userRepository.save(user);

        String token = jwtUtil.generateToken(user);

        // Act & Assert
        assertTrue(jwtUtil.isTokenValid(token, user),
                "有效的 Token 應該通過驗證");

        System.out.println("✅ UT-035 測試通過：有效 Token 驗證通過");
    }

    @Test
    @DisplayName("UT-035: JWT Token 認證 - 無效 Token 驗證應失敗")
    void jwtToken_InvalidToken_ShouldFailValidation() {
        // Arrange
        User user = User.builder()
                .userName(TEST_USERNAME)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .email(TEST_EMAIL)
                .build();
        userRepository.save(user);

        String invalidToken = "invalid.jwt.token";

        // Act & Assert
        assertThrows(Exception.class,
                () -> jwtUtil.extractUsername(invalidToken),
                "無效的 Token 解析應該拋出異常");

        System.out.println("✅ UT-035 測試通過：無效 Token 正確拋出異常");
    }
}
