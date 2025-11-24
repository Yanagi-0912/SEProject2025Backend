package com.ntou.auctionSite.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 更新密碼請求
 */
@Schema(description = "更新密碼請求")
public record UpdatePasswordRequest(
        @Schema(description = "目前密碼", example = "oldPassword123", required = true)
        @NotBlank(message = "目前密碼不能為空")
        String currentPassword,

        @Schema(description = "新密碼", example = "newPassword123", required = true)
        @NotBlank(message = "新密碼不能為空")
        @Size(min = 6, message = "新密碼長度至少 6 字元")
        String newPassword
) {
}

