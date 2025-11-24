package com.ntou.auctionSite.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "使用者註冊請求")
public class RegisterRequest {

    @Schema(description = "使用者名稱", example = "john_doe", required = true)
    private String username;

    @Schema(description = "使用者密碼", example = "password123", required = true)
    private String password;

    @Schema(description = "電子郵件", example = "john@example.com", required = true)
    private String email;
}
