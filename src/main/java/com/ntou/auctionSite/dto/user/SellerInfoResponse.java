package com.ntou.auctionSite.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 賣家資訊回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerInfoResponse {
    private String sellerId;           // 賣家 ID
    private String username;           // 使用者名稱
    private String nickname;           // 暱稱
    private String address;            // 地址
    private String phoneNumber;        // 電話號碼
    private String email;              // 電子郵件
    private Float averageRating;       // 平均評分（改為 Float）
    private Integer ratingCount;       // 評價數量
    private List<SellerProduct> products;  // 販售中的商品列表
    private Integer totalProducts;     // 總商品數

    /**
     * 賣家商品簡要資訊
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SellerProduct {
        private String productId;      // 商品 ID
        private String productName;    // 商品名稱
        private Integer price;         // 價格
        private String imageUrl;       // 商品圖片
        private String status;         // 商品狀態 (AVAILABLE, SOLD, etc.)
    }
}
