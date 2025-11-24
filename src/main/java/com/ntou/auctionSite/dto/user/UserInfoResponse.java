package com.ntou.auctionSite.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 使用者資訊回應
 */
@Schema(description = "使用者資訊回應")
public record UserInfoResponse(
        @Schema(description = "使用者 ID", example = "507f1f77bcf86cd799439011")
        String id,

        @Schema(description = "使用者名稱", example = "john_doe")
        String username,

        @Schema(description = "電子郵件", example = "john@example.com")
        String email,

        @Schema(description = "使用者暱稱", example = "小明")
        String nickname,

        @Schema(description = "地址", example = "台北市中正區")
        String address,

        @Schema(description = "電話號碼", example = "0912345678")
        String phoneNumber,

        @Schema(description = "平均評分", example = "4.5")
        float averageRating,

        @Schema(description = "評分次數", example = "10")
        int ratingCount
) {
}

