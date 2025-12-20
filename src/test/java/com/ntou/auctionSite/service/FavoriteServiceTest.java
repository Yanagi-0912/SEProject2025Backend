package com.ntou.auctionSite.service;

import com.ntou.auctionSite.dto.user.FavoriteResponseDTO;
import com.ntou.auctionSite.dto.user.SimpleFavoriteResponseDTO;
import com.ntou.auctionSite.model.product.Product;
import com.ntou.auctionSite.model.product.ProductTypes;
import com.ntou.auctionSite.model.user.Favorite;
import com.ntou.auctionSite.model.user.User;
import com.ntou.auctionSite.repository.FavoriteRepository;
import com.ntou.auctionSite.repository.ProductRepository;
import com.ntou.auctionSite.repository.UserRepository;
import com.ntou.auctionSite.service.user.FavoriteService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 收藏清單服務測試類別
 * 對應測試案例：UT-027, UT-028, UT-029, UT-030
 * 測試 FavoriteService 的 CRUD 操作
 */
@SpringBootTest
@DisplayName("收藏清單服務測試 (FavoriteService)")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FavoriteServiceTest {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User testSeller;
    private Product testProduct;
    private static final String TEST_PRODUCT_ID_PREFIX = "TEST_FAV_PRODUCT_";

    @BeforeEach
    void setUp() {
        // 清理測試資料
        cleanupTestData();

        // 建立測試賣家
        testSeller = User.builder()
                .userName("testFavSeller")
                .email("seller_fav@example.com")
                .password("password")
                .userNickname("測試賣家")
                .build();
        testSeller = userRepository.save(testSeller);

        // 建立測試使用者
        testUser = User.builder()
                .userName("testFavUser")
                .email("user_fav@example.com")
                .password("password")
                .userNickname("測試使用者")
                .build();
        testUser = userRepository.save(testUser);

        // 建立測試商品
        testProduct = new Product();
        testProduct.setProductID(TEST_PRODUCT_ID_PREFIX + "001");
        testProduct.setSellerID(testSeller.getId());
        testProduct.setProductName("測試收藏商品");
        testProduct.setProductDescription("測試用商品描述");
        testProduct.setProductPrice(1500);
        testProduct.setProductStock(10);
        testProduct.setProductCategory("測試類別");
        testProduct.setProductImage("https://example.com/image.jpg");
        testProduct.setProductType(ProductTypes.DIRECT);
        testProduct.setProductStatus(Product.ProductStatuses.ACTIVE);
        testProduct = productRepository.save(testProduct);
    }

    @AfterEach
    void tearDown() {
        cleanupTestData();
    }

    private void cleanupTestData() {
        // 清理收藏清單
        favoriteRepository.findAll().stream()
                .filter(f -> f.getUserId() != null && f.getUserId().contains("testFav"))
                .forEach(favoriteRepository::delete);

        // 清理商品
        productRepository.findAll().stream()
                .filter(p -> p.getProductID() != null && p.getProductID().startsWith(TEST_PRODUCT_ID_PREFIX))
                .forEach(productRepository::delete);

        // 清理使用者
        userRepository.findByUserName("testFavSeller").ifPresent(userRepository::delete);
        userRepository.findByUserName("testFavUser").ifPresent(userRepository::delete);
    }

    // ==================== UT-027: 新增商品至收藏清單 ====================

    @Test
    @Order(1)
    @DisplayName("UT-027: 新增商品至收藏清單 - 成功新增")
    void addFavorite_Success_ShouldAddProductToFavorites() {
        // Act
        favoriteService.addToFavorites(testUser.getId(), testProduct.getProductID());

        // Assert - 查詢收藏清單確認
        FavoriteResponseDTO response = favoriteService.getUserFavorites(testUser.getId());

        assertNotNull(response, "回應不應為 null");
        assertEquals(testUser.getId(), response.getUserId(), "使用者 ID 應該匹配");
        assertEquals(1, response.getTotalItems(), "應該有 1 個收藏項目");
        assertEquals(testProduct.getProductID(), response.getItems().get(0).getProductId(),
                "商品 ID 應該匹配");
        assertEquals(testProduct.getProductName(), response.getItems().get(0).getProductName(),
                "商品名稱應該匹配");

        System.out.println("✅ UT-027 測試通過：成功新增商品至收藏清單");
        System.out.println("   收藏商品: " + response.getItems().get(0).getProductName());
    }

    @Test
    @Order(2)
    @DisplayName("UT-027: 新增商品至收藏清單 - 重複新增應拋出異常")
    void addFavorite_Duplicate_ShouldThrowException() {
        // Act - 新增第一次
        favoriteService.addToFavorites(testUser.getId(), testProduct.getProductID());

        // Assert - 新增第二次應拋出異常
        assertThrows(RuntimeException.class,
                () -> favoriteService.addToFavorites(testUser.getId(), testProduct.getProductID()),
                "重複新增應該拋出異常");

        System.out.println("✅ UT-027 測試通過：重複新增正確處理");
    }

    // ==================== UT-028: 查詢使用者收藏清單 ====================

    @Test
    @Order(3)
    @DisplayName("UT-028: 查詢使用者收藏清單 - 返回完整資訊")
    void getUserFavorites_ShouldReturnCompleteInfo() {
        // Arrange - 新增商品到收藏
        favoriteService.addToFavorites(testUser.getId(), testProduct.getProductID());

        // Act
        FavoriteResponseDTO response = favoriteService.getUserFavorites(testUser.getId());

        // Assert
        assertNotNull(response, "回應不應為 null");
        assertEquals(testUser.getId(), response.getUserId());
        assertNotNull(response.getItems());
        assertFalse(response.getItems().isEmpty(), "收藏清單不應為空");

        // 驗證商品詳細資訊
        var item = response.getItems().get(0);
        assertEquals(testProduct.getProductID(), item.getProductId());
        assertEquals(testProduct.getProductName(), item.getProductName());
        assertEquals(testProduct.getProductPrice(), item.getProductPrice());
        assertEquals(testProduct.getProductImage(), item.getProductImage());
        assertEquals(testProduct.getProductType().toString(), item.getProductType());
        assertEquals(testProduct.getProductStatus().toString(), item.getProductStatus());

        // 驗證賣家資訊
        assertEquals(testSeller.getId(), item.getSellerId());
        assertEquals(testSeller.getUsername(), item.getSellerName());

        System.out.println("✅ UT-028 測試通過：查詢收藏清單返回完整資訊");
        System.out.println("   商品: " + item.getProductName());
        System.out.println("   賣家: " + item.getSellerName());
    }

    @Test
    @Order(4)
    @DisplayName("UT-028: 查詢使用者收藏清單 - 空清單時返回空列表")
    void getUserFavorites_EmptyList_ShouldReturnEmptyItems() {
        // Act - 直接查詢（沒有新增任何收藏）
        FavoriteResponseDTO response = favoriteService.getUserFavorites(testUser.getId());

        // Assert
        assertNotNull(response);
        assertEquals(testUser.getId(), response.getUserId());
        assertNotNull(response.getItems());
        assertEquals(0, response.getTotalItems(), "收藏清單應為空");

        System.out.println("✅ UT-028 測試通過：空收藏清單正確返回");
    }

    @Test
    @Order(5)
    @DisplayName("UT-028: 查詢使用者收藏清單 - 簡化版查詢")
    void getSimpleUserFavorites_ShouldReturnSimplifiedInfo() {
        // Arrange
        favoriteService.addToFavorites(testUser.getId(), testProduct.getProductID());

        // Act
        SimpleFavoriteResponseDTO response = favoriteService.getSimpleUserFavorites(testUser.getId());

        // Assert
        assertNotNull(response);
        assertEquals(testUser.getId(), response.getUserId());
        assertEquals(1, response.getTotalItems());
        assertEquals(testProduct.getProductID(), response.getItems().get(0).getProductId());
        assertNotNull(response.getItems().get(0).getAddedAt());

        System.out.println("✅ UT-028 測試通過：簡化版查詢正確返回");
    }

    // ==================== UT-029: 從收藏清單移除商品 ====================

    @Test
    @Order(6)
    @DisplayName("UT-029: 從收藏清單移除商品 - 成功移除")
    void removeFavorite_Success_ShouldRemoveProduct() {
        // Arrange - 先新增商品
        favoriteService.addToFavorites(testUser.getId(), testProduct.getProductID());

        // 確認已新增
        FavoriteResponseDTO beforeRemove = favoriteService.getUserFavorites(testUser.getId());
        assertEquals(1, beforeRemove.getTotalItems());

        // Act - 移除商品
        favoriteService.removeFromFavorites(testUser.getId(), testProduct.getProductID());

        // Assert
        FavoriteResponseDTO afterRemove = favoriteService.getUserFavorites(testUser.getId());
        assertEquals(0, afterRemove.getTotalItems(), "移除後收藏清單應為空");

        System.out.println("✅ UT-029 測試通過：成功從收藏清單移除商品");
    }

    @Test
    @Order(7)
    @DisplayName("UT-029: 從收藏清單移除商品 - 移除不存在的商品（收藏清單已存在時）")
    void removeFavorite_NonExistent_ShouldNotThrowError() {
        // Arrange - 先新增一個商品建立收藏清單
        favoriteService.addToFavorites(testUser.getId(), testProduct.getProductID());

        // Act & Assert - 移除不存在的商品不應該影響現有收藏
        assertDoesNotThrow(() -> {
            favoriteService.removeFromFavorites(testUser.getId(), "nonexistent_product");
        }, "移除不存在的商品不應該拋出異常");

        // 確認原有收藏仍然存在
        FavoriteResponseDTO response = favoriteService.getUserFavorites(testUser.getId());
        assertEquals(1, response.getTotalItems(), "原有收藏應該仍然存在");

        System.out.println("✅ UT-029 測試通過：移除不存在的商品正確處理");
    }

    // ==================== UT-030: 清空收藏清單 ====================

    @Test
    @Order(8)
    @DisplayName("UT-030: 清空收藏清單 - 成功清空")
    void clearFavorites_Success_ShouldClearAll() {
        // Arrange - 新增商品
        favoriteService.addToFavorites(testUser.getId(), testProduct.getProductID());

        // 建立第二個測試商品
        Product product2 = new Product();
        product2.setProductID(TEST_PRODUCT_ID_PREFIX + "002");
        product2.setSellerID(testSeller.getId());
        product2.setProductName("測試收藏商品 2");
        product2.setProductDescription("測試用商品描述 2");
        product2.setProductPrice(2000);
        product2.setProductStock(5);
        product2.setProductCategory("測試類別");
        product2.setProductType(ProductTypes.DIRECT);
        product2.setProductStatus(Product.ProductStatuses.ACTIVE);
        product2 = productRepository.save(product2);

        favoriteService.addToFavorites(testUser.getId(), product2.getProductID());

        // 確認已新增
        FavoriteResponseDTO beforeClear = favoriteService.getUserFavorites(testUser.getId());
        assertEquals(2, beforeClear.getTotalItems(), "清空前應該有 2 個項目");

        // Act - 清空收藏清單
        favoriteService.clearFavorites(testUser.getId());

        // Assert
        FavoriteResponseDTO afterClear = favoriteService.getUserFavorites(testUser.getId());
        assertEquals(0, afterClear.getTotalItems(), "清空後 totalItems 應為 0");
        assertTrue(afterClear.getItems().isEmpty(), "清空後 items 應為空");

        System.out.println("✅ UT-030 測試通過：成功清空收藏清單");
    }

    @Test
    @Order(9)
    @DisplayName("UT-030: 清空收藏清單 - 清空已清空的清單不應報錯")
    void clearFavorites_EmptyList_ShouldNotThrowError() {
        // Arrange - 先建立收藏清單（新增後移除，使其變成空清單）
        favoriteService.addToFavorites(testUser.getId(), testProduct.getProductID());
        favoriteService.removeFromFavorites(testUser.getId(), testProduct.getProductID());

        // 確認清單已為空
        FavoriteResponseDTO beforeClear = favoriteService.getUserFavorites(testUser.getId());
        assertEquals(0, beforeClear.getTotalItems(), "清空前應該是空的");

        // Act & Assert - 再次清空空清單不應該拋出異常
        assertDoesNotThrow(() -> {
            favoriteService.clearFavorites(testUser.getId());
        }, "清空空清單不應該拋出異常");

        FavoriteResponseDTO response = favoriteService.getUserFavorites(testUser.getId());
        assertEquals(0, response.getTotalItems());

        System.out.println("✅ UT-030 測試通過：清空空清單正確處理");
    }

    // ==================== 額外測試：使用者驗證 ====================

    @Test
    @Order(10)
    @DisplayName("收藏清單 - 使用不存在的使用者 ID 應拋出異常")
    void getUserFavorites_InvalidUserId_ShouldThrowException() {
        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> favoriteService.getUserFavorites("invalid_user_id"),
                "使用不存在的使用者 ID 應該拋出異常");

        System.out.println("✅ 額外測試通過：無效使用者 ID 正確拋出異常");
    }

    @Test
    @Order(11)
    @DisplayName("收藏清單 - 檢查商品是否在收藏清單中")
    void isFavorited_ShouldReturnCorrectResult() {
        // 先確認商品不在收藏清單中
        assertFalse(favoriteService.isFavorited(testUser.getId(), testProduct.getProductID()),
                "新增前商品應該不在收藏清單中");

        // 新增到收藏
        favoriteService.addToFavorites(testUser.getId(), testProduct.getProductID());

        // 確認商品在收藏清單中
        assertTrue(favoriteService.isFavorited(testUser.getId(), testProduct.getProductID()),
                "新增後商品應該在收藏清單中");

        System.out.println("✅ 額外測試通過：isFavorited 功能正常");
    }

    @Test
    @Order(12)
    @DisplayName("收藏清單 - 取得收藏數量")
    void getFavoritesCount_ShouldReturnCorrectCount() {
        // 初始數量
        assertEquals(0, favoriteService.getFavoritesCount(testUser.getId()),
                "初始收藏數量應為 0");

        // 新增到收藏
        favoriteService.addToFavorites(testUser.getId(), testProduct.getProductID());

        // 確認數量
        assertEquals(1, favoriteService.getFavoritesCount(testUser.getId()),
                "新增後收藏數量應為 1");

        System.out.println("✅ 額外測試通過：getFavoritesCount 功能正常");
    }
}
