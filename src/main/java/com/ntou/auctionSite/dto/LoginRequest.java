package com.ntou.auctionSite.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "使用者登入請求")
public class LoginRequest {

    @Schema(description = "使用者名稱", example = "john_doe", required = true)
    private String username;

    @Schema(description = "使用者密碼", example = "password123", required = true)
    private String password;
}
