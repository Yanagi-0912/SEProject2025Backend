package com.ntou.auctionSite.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 收藏項目 DTO（包含完整商品資訊）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "收藏項目詳細資訊")
public class FavoriteItemDTO {

    @Schema(description = "商品 ID", example = "P001")
    private String productId;

    @Schema(description = "商品名稱", example = "iPhone 15 Pro")
    private String productName;

    @Schema(description = "商品價格", example = "39900")
    private Integer productPrice;

    @Schema(description = "商品圖片 URL", example = "https://example.com/image.jpg")
    private String productImage;

    @Schema(description = "商品類型", example = "DIRECT")
    private String productType;

    @Schema(description = "商品狀態", example = "ACTIVE")
    private String productStatus;

    @Schema(description = "賣家 ID", example = "U001")
    private String sellerId;

    @Schema(description = "賣家名稱", example = "AppleStore")
    private String sellerName;

    @Schema(description = "加入收藏的時間", example = "2025-11-29T10:30:00")
    private LocalDateTime addedAt;
}

