package com.ntou.auctionSite.service;

import com.ntou.auctionSite.dto.user.PublicUserInfoResponse;
import com.ntou.auctionSite.dto.user.UpdatePasswordRequest;
import com.ntou.auctionSite.dto.user.UpdateUserRequest;
import com.ntou.auctionSite.dto.user.UserInfoResponse;
import com.ntou.auctionSite.model.product.Product;
import com.ntou.auctionSite.model.user.User;
import com.ntou.auctionSite.repository.ProductRepository;
import com.ntou.auctionSite.repository.UserRepository;
import com.ntou.auctionSite.service.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;


import static org.junit.jupiter.api.Assertions.*;

/**
 * 使用者服務測試類別
 * 測試 UserService 的所有功能
 */
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    public void setUp() {
        // 清理測試資料
        userRepository.deleteAll();
        productRepository.deleteAll();

        // 建立測試使用者
        testUser = User.builder()
                .userName("testuser")
                .email("testuser@example.com")
                .password(passwordEncoder.encode("password123"))
                .userNickname("測試使用者")
                .address("台北市中正區")
                .phoneNumber("0912345678")
                .averageRating(4.5f)
                .ratingCount(10)
                .isBanned(false)
                .remainingDrawTimes(5)
                .build();
        testUser = userRepository.save(testUser);

        // 建立測試商品
        testProduct = new Product();
        testProduct.setProductID("TEST_PRODUCT_001");
        testProduct.setSellerID(testUser.getId());
        testProduct.setProductName("測試商品");
        testProduct.setProductDescription("這是一個測試商品");
        testProduct.setProductPrice(1000);
        testProduct.setProductStock(5);
        testProduct.setProductCategory("測試類別");
        testProduct.setProductStatus(Product.ProductStatuses.ACTIVE);
        testProduct = productRepository.save(testProduct);
    }

    @AfterEach
    public void tearDown() {
        // 清理測試資料
        userRepository.deleteAll();
        productRepository.deleteAll();
    }

    // ==================== 測試：取得使用者資訊 ====================

    @Test
    public void testGetUserInfo_Success() {
        // 執行
        UserInfoResponse response = userService.getUserInfo("testuser");

        // 驗證
        assertNotNull(response);
        assertEquals(testUser.getId(), response.id());
        assertEquals("testuser", response.username());
        assertEquals("testuser@example.com", response.email());
        assertEquals("測試使用者", response.nickname());
        assertEquals("台北市中正區", response.address());
        assertEquals("0912345678", response.phoneNumber());
        assertEquals(4.5f, response.averageRating(), 0.01);
        assertEquals(10, response.ratingCount());
        assertFalse(response.isBanned());
        assertEquals(5, response.remainingDrawTimes());

        // 驗證販售商品列表
        assertNotNull(response.sellingProducts());
        assertEquals(1, response.sellingProducts().size());
        assertEquals("測試商品", response.sellingProducts().get(0).getProductName());

        System.out.println("✓ 測試成功：取得使用者資訊");
        System.out.println("  使用者名稱: " + response.username());
        System.out.println("  販售商品數量: " + response.sellingProducts().size());
    }

    @Test
    public void testGetUserInfo_UserNotFound() {
        // 執行與驗證
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserInfo("nonexistentuser");
        });

        assertEquals("使用者不存在", exception.getMessage());
        System.out.println("✓ 測試成功：使用者不存在時拋出異常");
    }

    @Test
    public void testGetUserInfo_NoSellingProducts() {
        // 清除所有商品
        productRepository.deleteAll();

        // 執行
        UserInfoResponse response = userService.getUserInfo("testuser");

        // 驗證
        assertNotNull(response);
        assertNotNull(response.sellingProducts());
        assertEquals(0, response.sellingProducts().size());

        System.out.println("✓ 測試成功：沒有販售商品時返回空列表");
    }

    // ==================== 測試：更新使用者資訊 ====================

    @Test
    public void testUpdateUserInfo_UpdateAllFields() {
        // 準備更新請求
        UpdateUserRequest request = new UpdateUserRequest(
                "newusername",
                "newemail@example.com",
                "新暱稱",
                "新地址：高雄市",
                "0987654321",
                10
        );

        // 執行
        UserInfoResponse response = userService.updateUserInfo("testuser", request);

        // 驗證
        assertNotNull(response);
        assertEquals("newusername", response.username());
        assertEquals("newemail@example.com", response.email());
        assertEquals("新暱稱", response.nickname());
        assertEquals("新地址：高雄市", response.address());
        assertEquals("0987654321", response.phoneNumber());
        assertEquals(10, response.remainingDrawTimes());

        // 從資料庫驗證
        User updatedUser = userRepository.findByUserName("newusername").orElse(null);
        assertNotNull(updatedUser);
        assertEquals("newemail@example.com", updatedUser.getEmail());

        System.out.println("✓ 測試成功：更新所有使用者資訊");
        System.out.println("  新使用者名稱: " + response.username());
        System.out.println("  新電子郵件: " + response.email());
    }

    @Test
    public void testUpdateUserInfo_UpdatePartialFields() {
        // 只更新暱稱和地址
        UpdateUserRequest request = new UpdateUserRequest(
                null,
                null,
                "部分更新暱稱",
                "部分更新地址",
                null,
                null
        );

        // 執行
        UserInfoResponse response = userService.updateUserInfo("testuser", request);

        // 驗證
        assertEquals("testuser", response.username()); // 未改變
        assertEquals("testuser@example.com", response.email()); // 未改變
        assertEquals("部分更新暱稱", response.nickname()); // 已更新
        assertEquals("部分更新地址", response.address()); // 已更新
        assertEquals("0912345678", response.phoneNumber()); // 未改變

        System.out.println("✓ 測試成功：部分更新使用者資訊");
    }

    @Test
    public void testUpdateUserInfo_DuplicateUsername() {
        // 建立第二個使用者
        User anotherUser = User.builder()
                .userName("anotheruser")
                .email("another@example.com")
                .password(passwordEncoder.encode("password123"))
                .build();
        userRepository.save(anotherUser);

        // 嘗試更新為已存在的使用者名稱
        UpdateUserRequest request = new UpdateUserRequest(
                "anotheruser", // 已存在的使用者名稱
                null,
                null,
                null,
                null,
                null
        );

        // 執行與驗證
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUserInfo("testuser", request);
        });

        assertEquals("使用者名稱已被使用", exception.getMessage());
        System.out.println("✓ 測試成功：使用者名稱重複時拋出異常");
    }

    @Test
    public void testUpdateUserInfo_DuplicateEmail() {
        // 建立第二個使用者
        User anotherUser = User.builder()
                .userName("anotheruser")
                .email("another@example.com")
                .password(passwordEncoder.encode("password123"))
                .build();
        userRepository.save(anotherUser);

        // 嘗試更新為已存在的電子郵件
        UpdateUserRequest request = new UpdateUserRequest(
                null,
                "another@example.com", // 已存在的電子郵件
                null,
                null,
                null,
                null
        );

        // 執行與驗證
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUserInfo("testuser", request);
        });

        assertEquals("此電子郵件已被使用", exception.getMessage());
        System.out.println("✓ 測試成功：電子郵件重複時拋出異常");
    }

    @Test
    public void testUpdateUserInfo_SameUsername() {
        // 更新為相同的使用者名稱（應該允許）
        UpdateUserRequest request = new UpdateUserRequest(
                "testuser",
                null,
                "更新暱稱",
                null,
                null,
                null
        );

        // 執行
        UserInfoResponse response = userService.updateUserInfo("testuser", request);

        // 驗證
        assertEquals("testuser", response.username());
        assertEquals("更新暱稱", response.nickname());

        System.out.println("✓ 測試成功：允許更新為相同的使用者名稱");
    }

    @Test
    public void testUpdateUserInfo_UserNotFound() {
        UpdateUserRequest request = new UpdateUserRequest(
                "newname",
                null,
                null,
                null,
                null,
                null
        );

        // 執行與驗證
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUserInfo("nonexistentuser", request);
        });

        assertEquals("使用者不存在", exception.getMessage());
        System.out.println("✓ 測試成功：更新不存在的使用者時拋出異常");
    }

    // ==================== 測試：更新密碼 ====================

    @Test
    public void testUpdatePassword_Success() {
        // 準備更新密碼請求
        UpdatePasswordRequest request = new UpdatePasswordRequest(
                "password123",
                "newPassword456"
        );

        // 執行
        assertDoesNotThrow(() -> {
            userService.updatePassword("testuser", request);
        });

        // 驗證密碼已更新
        User updatedUser = userRepository.findByUserName("testuser").orElse(null);
        assertNotNull(updatedUser);
        assertTrue(passwordEncoder.matches("newPassword456", updatedUser.getPassword()));
        assertFalse(passwordEncoder.matches("password123", updatedUser.getPassword()));

        System.out.println("✓ 測試成功：更新密碼");
    }

    @Test
    public void testUpdatePassword_WrongCurrentPassword() {
        // 使用錯誤的目前密碼
        UpdatePasswordRequest request = new UpdatePasswordRequest(
                "wrongpassword",
                "newPassword456"
        );

        // 執行與驗證
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updatePassword("testuser", request);
        });

        assertEquals("目前密碼錯誤", exception.getMessage());
        System.out.println("✓ 測試成功：目前密碼錯誤時拋出異常");
    }

    @Test
    public void testUpdatePassword_SameAsOldPassword() {
        // 新密碼與舊密碼相同
        UpdatePasswordRequest request = new UpdatePasswordRequest(
                "password123",
                "password123"
        );

        // 執行與驗證
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updatePassword("testuser", request);
        });

        assertEquals("新密碼不能與舊密碼相同", exception.getMessage());
        System.out.println("✓ 測試成功：新密碼與舊密碼相同時拋出異常");
    }

    @Test
    public void testUpdatePassword_UserNotFound() {
        UpdatePasswordRequest request = new UpdatePasswordRequest(
                "password123",
                "newPassword456"
        );

        // 執行與驗證
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updatePassword("nonexistentuser", request);
        });

        assertEquals("使用者不存在", exception.getMessage());
        System.out.println("✓ 測試成功：更新不存在的使用者密碼時拋出異常");
    }

    // ==================== 測試：取得公開使用者資訊 ====================

    @Test
    public void testGetPublicUserInfo_Success() {
        // 執行
        PublicUserInfoResponse response = userService.getPublicUserInfo(testUser.getId());

        // 驗證
        assertNotNull(response);
        assertEquals(testUser.getId(), response.id());
        assertEquals("testuser", response.username());
        assertEquals("testuser@example.com", response.email());
        assertEquals("測試使用者", response.nickname());
        assertEquals("台北市中正區", response.address());
        assertEquals("0912345678", response.phoneNumber());
        assertEquals(4.5f, response.averageRating(), 0.01);
        assertEquals(10, response.ratingCount());
        assertFalse(response.isBanned());

        // 驗證販售商品列表
        assertNotNull(response.sellingProducts());
        assertEquals(1, response.sellingProducts().size());

        System.out.println("✓ 測試成功：取得公開使用者資訊");
        System.out.println("  使用者名稱: " + response.username());
        System.out.println("  販售商品數量: " + response.sellingProducts().size());
    }

    @Test
    public void testGetPublicUserInfo_UserNotFound() {
        // 執行與驗證
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getPublicUserInfo("nonexistentid");
        });

        assertEquals("使用者不存在", exception.getMessage());
        System.out.println("✓ 測試成功：取得不存在的使用者公開資訊時拋出異常");
    }

    @Test
    public void testGetPublicUserInfo_MultipleProducts() {
        // 新增更多測試商品
        Product product2 = new Product();
        product2.setProductID("TEST_PRODUCT_002");
        product2.setSellerID(testUser.getId());
        product2.setProductName("測試商品2");
        product2.setProductPrice(2000);
        product2.setProductStatus(Product.ProductStatuses.ACTIVE);
        productRepository.save(product2);

        Product product3 = new Product();
        product3.setProductID("TEST_PRODUCT_003");
        product3.setSellerID(testUser.getId());
        product3.setProductName("測試商品3");
        product3.setProductPrice(3000);
        product3.setProductStatus(Product.ProductStatuses.INACTIVE);
        productRepository.save(product3);

        // 執行
        PublicUserInfoResponse response = userService.getPublicUserInfo(testUser.getId());

        // 驗證
        assertNotNull(response.sellingProducts());
        assertEquals(3, response.sellingProducts().size());

        System.out.println("✓ 測試成功：取得多個販售商品");
        System.out.println("  販售商品數量: " + response.sellingProducts().size());
    }

    // ==================== 測試：更新剩餘抽獎次數 ====================

    @Test
    public void testUpdateRemainingDrawTimes_Success() {
        // 執行
        userService.updateRemainingDrawTimes("testuser", 20);

        // 驗證
        User updatedUser = userRepository.findByUserName("testuser").orElse(null);
        assertNotNull(updatedUser);
        assertEquals(20, updatedUser.getRemainingDrawTimes());

        System.out.println("✓ 測試成功：更新剩餘抽獎次數");
        System.out.println("  新抽獎次數: " + updatedUser.getRemainingDrawTimes());
    }

    @Test
    public void testUpdateRemainingDrawTimes_DecreaseToZero() {
        // 減少到 0
        userService.updateRemainingDrawTimes("testuser", 0);

        // 驗證
        User updatedUser = userRepository.findByUserName("testuser").orElse(null);
        assertNotNull(updatedUser);
        assertEquals(0, updatedUser.getRemainingDrawTimes());

        System.out.println("✓ 測試成功：將抽獎次數減少到 0");
    }

    @Test
    public void testUpdateRemainingDrawTimes_UserNotFound() {
        // 執行與驗證
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateRemainingDrawTimes("nonexistentuser", 10);
        });

        assertEquals("使用者不存在", exception.getMessage());
        System.out.println("✓ 測試成功：更新不存在的使用者抽獎次數時拋出異常");
    }

    // ==================== 測試：邊界情況 ====================

    @Test
    public void testUserInfo_WithNullValues() {
        // 建立一個欄位為 null 的使用者
        User minimalUser = User.builder()
                .userName("minimaluser")
                .email("minimal@example.com")
                .password(passwordEncoder.encode("password123"))
                .build();
        minimalUser = userRepository.save(minimalUser);

        // 執行
        UserInfoResponse response = userService.getUserInfo("minimaluser");

        // 驗證預設值
        assertNotNull(response);
        assertEquals(0.0f, response.averageRating(), 0.01);
        assertEquals(0, response.ratingCount());
        assertFalse(response.isBanned());
        assertNotNull(response.sellingProducts());
        assertEquals(0, response.sellingProducts().size());

        System.out.println("✓ 測試成功：處理 null 值並提供預設值");
    }

    @Test
    public void testUpdateUserInfo_BlankValues() {
        // 嘗試使用空白字串更新
        UpdateUserRequest request = new UpdateUserRequest(
                "   ",
                "   ",
                "   ",
                "   ",
                "   ",
                null
        );

        // 執行
        UserInfoResponse response = userService.updateUserInfo("testuser", request);

        // 驗證：空白字串不應該更新欄位
        assertEquals("testuser", response.username());
        assertEquals("testuser@example.com", response.email());

        System.out.println("✓ 測試成功：空白字串不會更新欄位");
    }

    // ==================== 整合測試 ====================

    @Test
    public void testCompleteUserLifecycle() {
        System.out.println("開始完整使用者生命週期測試");

        // 1. 取得初始使用者資訊
        UserInfoResponse initialInfo = userService.getUserInfo("testuser");
        assertEquals("testuser", initialInfo.username());
        System.out.println("  ✓ 步驟 1: 取得初始使用者資訊");

        // 2. 更新使用者資訊
        UpdateUserRequest updateRequest = new UpdateUserRequest(
                "updateduser",
                "updated@example.com",
                "更新後的暱稱",
                "更新後的地址",
                "0987654321",
                15
        );
        UserInfoResponse updatedInfo = userService.updateUserInfo("testuser", updateRequest);
        assertEquals("updateduser", updatedInfo.username());
        assertEquals("updated@example.com", updatedInfo.email());
        System.out.println("  ✓ 步驟 2: 更新使用者資訊");

        // 3. 更新密碼
        UpdatePasswordRequest passwordRequest = new UpdatePasswordRequest(
                "password123",
                "newSecurePassword"
        );
        userService.updatePassword("updateduser", passwordRequest);
        User user = userRepository.findByUserName("updateduser").orElse(null);
        assertNotNull(user);
        assertTrue(passwordEncoder.matches("newSecurePassword", user.getPassword()));
        System.out.println("  ✓ 步驟 3: 更新密碼");

        // 4. 取得公開使用者資訊
        PublicUserInfoResponse publicInfo = userService.getPublicUserInfo(testUser.getId());
        assertEquals("updateduser", publicInfo.username());
        System.out.println("  ✓ 步驟 4: 取得公開使用者資訊");

        // 5. 更新抽獎次數
        userService.updateRemainingDrawTimes("updateduser", 0);
        User finalUser = userRepository.findByUserName("updateduser").orElse(null);
        assertNotNull(finalUser);
        assertEquals(0, finalUser.getRemainingDrawTimes());
        System.out.println("  ✓ 步驟 5: 更新抽獎次數");

        System.out.println("✓ 完整使用者生命週期測試成功");
    }
}

