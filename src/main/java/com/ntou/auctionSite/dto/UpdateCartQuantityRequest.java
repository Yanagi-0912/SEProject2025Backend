package com.ntou.auctionSite.dto;

import jakarta.validation.constraints.Min;

public record UpdateCartQuantityRequest(
        @Min(value = 0, message = "數量不可為負數")
        Integer quantity
) {}

