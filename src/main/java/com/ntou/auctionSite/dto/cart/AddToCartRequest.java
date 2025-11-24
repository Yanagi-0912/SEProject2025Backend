package com.ntou.auctionSite.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AddToCartRequest(
        @NotBlank(message = "商品ID不可為空")
        String productId,

        @Min(value = 1, message = "數量必須大於0")
        Integer quantity
) {}

