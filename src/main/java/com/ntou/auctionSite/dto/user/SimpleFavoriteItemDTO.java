package com.ntou.auctionSite.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 簡化版收藏項目 DTO（只包含基本資訊）
 * 只回傳商品 ID 和加入時間
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "收藏項目簡化資訊")
public class SimpleFavoriteItemDTO {

    @Schema(description = "商品 ID", example = "P001")
    private String productId;

    @Schema(description = "加入收藏的時間", example = "2025-11-29T10:30:00")
    private LocalDateTime addedAt;
}

