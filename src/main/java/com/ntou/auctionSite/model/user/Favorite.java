package com.ntou.auctionSite.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 收藏清單 - 獨立 Collection
 * 設計理念：獨立 Collection + 一對一關係 + 每個使用者一個文檔
 * - 獨立儲存：favorites 是獨立的 collection，不是寫在 User 內
 * - 一對一關係：一個 User 對應一個 Favorite 文檔（透過 userId）
 * - 集中管理：每個使用者的所有收藏都在同一個文檔的 items 陣列內
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "favorites")
public class Favorite {

    @Id
    private String id;              // favoriteId（等於 userId，建立一對一關係）
    private String userId;          // 使用者 ID
    private List<FavoriteItem> items = new ArrayList<>();  // 收藏項目列表

    /**
     * 收藏項目
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FavoriteItem {
        private String productId;           // 商品 ID
        private LocalDateTime addedAt;      // 加入收藏的時間

        // 建構子：只傳入 productId，自動設定加入時間
        public FavoriteItem(String productId) {
            this.productId = productId;
            this.addedAt = LocalDateTime.now();
        }
    }
}

