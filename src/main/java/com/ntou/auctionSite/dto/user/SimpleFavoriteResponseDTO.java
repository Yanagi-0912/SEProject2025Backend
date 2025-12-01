package com.ntou.auctionSite.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 收藏清單簡化回應（只包含商品 ID）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "收藏清單簡化回應")
public class SimpleFavoriteResponseDTO {

    @Schema(description = "使用者 ID", example = "U001")
    private String userId;

    @Schema(description = "收藏商品 ID 列表")
    private List<SimpleFavoriteItemDTO> items;

    @Schema(description = "總收藏數", example = "5")
    private Integer totalItems;
}

